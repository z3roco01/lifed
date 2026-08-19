package z3roco01.lifed.features;

import net.minecraft.world.level.Level;
import z3roco01.lifed.Lifed;
import z3roco01.lifed.config.ConfigFiles;

public class WorldBorder {
    public static void setSize() {
        int wantedSize = ConfigFiles.gameplay.borderSize;

        // dont set when lower than one
        if(wantedSize < 1)
            return;

        for(Level level : Lifed.server.getAllLevels()) {
            setLevelBorder(level, wantedSize);
        }
    }

    private static void setLevelBorder(Level level, int size) {
        double currentSize = level.getWorldBorder().getSize();

        if((int)currentSize != size) {
            level.getWorldBorder().setCenter(0, 0);
            level.getWorldBorder().setSize(size);
        }
    }
}
