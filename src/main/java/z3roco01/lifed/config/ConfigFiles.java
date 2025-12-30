package z3roco01.lifed.config;

import z3roco01.lifed.Lifed;
import z3roco01.lifed.config.annotation.ConfigProperty;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Holds methods which handle reading and writing of ConfigProperties
 */
public class ConfigFiles {
    /**
     * Saves an object's marked config properties to a passed path
     * @param path the path of the file to save to, creates file if needed
     * @param object the object to save
     * @param <T> the type of the object
     */
    public static <T> void store(String path, T object) throws IOException, IllegalAccessException {
        File file = new File(path);
        store(file, object);
    }

    /**
     * Saves an object's marked config properties to a passed path
     * @param file the file to save to, creates file if needed
     * @param object the object to save
     * @param <T> the type of the object
     */
    public static <T> void store(File file, T object) throws IOException, IllegalAccessException {
        createIfNotPresent(file);

        // TODO: MAKE BETTER CONFIG SYSTEM THAT DOESNT RELY ON PROPERTIES
        // underlying properties objects, mostly for easily text handling
        Properties properties = new Properties();

        // first loop over all properties and filter for ones annotated as config
        for(Field field : object.getClass().getDeclaredFields()) {
            if(!field.isAnnotationPresent(ConfigProperty.class))
                continue;

            String key = getKey(field);

            boolean accessible = field.canAccess(object);
            // make it accessible temporarily
            field.setAccessible(true);
            // get the value of this field from the passed object, then turn it into a string
            String value = field.get(object).toString();
            // return accessibility
            field.setAccessible(accessible);

            properties.setProperty(key, value);
        }

        // use the properties object to write
        properties.store(new FileWriter(file), null);
    }

    /**
     * loads an object's marked config properties from a file
     * @param path the path of the file to save to, creates file and stores defaults if needed
     * @param object the object to save
     * @param <T> the type of the object
     */
    public static <T> void load(String path, T object) throws IOException, IllegalAccessException {
        File file = new File(path);
        load(file, object);
    }

    /**
     * loads an object's marked config properties from a file
     * @param file the file to save to, creates file and stores defaults if needed
     * @param object the object to save
     * @param <T> the type of the object
     */
    public static <T> void load(File file, T object) throws IOException, IllegalAccessException {
        // if the file did not need creation
        if(!createIfNotPresent(file)) {
            Properties properties = new Properties();
            properties.load(new FileReader(file));
            // if the file is missing at least one property, this will be true meaning a save is needed
            boolean propertiesUpdated = false;

            for(Field field : object.getClass().getDeclaredFields()) {
                if(!field.isAnnotationPresent(ConfigProperty.class))
                    continue;

                String key = getKey(field);

                if(properties.containsKey(key)) {
                    // get the string value, handle conversion
                    String strValue = properties.getProperty(key);

                    // TODO: MAKE BETTER
                    // quick hacky way of doing it
                    Class<?> fieldClass = field.get(object).getClass();

                    if(fieldClass == Integer.class)
                        field.set(object, Integer.valueOf(strValue));
                    else if(fieldClass == Float.class)
                        field.set(object, Float.valueOf(strValue));
                    else if(fieldClass == String.class)
                        field.set(object, strValue);
                    else if(fieldClass == Boolean.class)
                        field.set(object, Boolean.valueOf(strValue));

                    Lifed.LOGGER.info(field.get(object).toString());
                }else {
                    // the property needs to be added to the list
                    propertiesUpdated = true;
                    properties.setProperty(key, field.get(object).toString());
                }

                // save properties directly
                if(propertiesUpdated)
                    properties.store(new FileWriter(file), null);
            }
        }else {
            // if it did, save defaults and thats it
            store(file, object);
        }
    }

    /**
     * Creates a file if it does not exist
     * @param file the file to maybe create
     */
    private static boolean createIfNotPresent(File file) throws IOException {
        // create any parent directories if needed
        file.toPath().getParent().toFile().mkdirs();

        if(!file.exists())
            return file.createNewFile();
        return false;
    }

    /**
     * Gets the key from a field annotated with the ConfigProperty annotation
     * @param field the field, must be annotated
     * @return the key if specified, or the field's name
     */
    private static String getKey(Field field) {
        String key = field.getAnnotation(ConfigProperty.class).key();
        // if no key was specified ( or it is intentionally blank ) then set it to the fields name
        if(key.isBlank())
            key = field.getName();

        return key;
    }
}
