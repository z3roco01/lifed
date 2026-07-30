package z3roco01.lifed.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;

/**
 * Allows to schedule tasks that will run in a certain amount of ticks
 */
public class TaskScheduling {
    private static final ArrayList<Task> newTasks = new ArrayList<>();
    private static final ArrayList<Task> tasks = new ArrayList<>();
    private static boolean shouldCancel = false;

    /**
     * Schedules a non blocking task in `tick` ticks
     * @param ticks how many ticks until itll run
     * @param runnable the thing that will run
     */
    public static void scheduleTask(int ticks, Runnable runnable) {
        Task task = new Task(ticks, runnable);
        newTasks.add(task);
    }

    // cancels all tasks
    public static void cancelTasks() {
        shouldCancel = true;
    }

    public static void registerTickEnd() {
        // runs once each serve tick
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // if canceling, clear all tasks
            if(shouldCancel) {
                tasks.clear();
                shouldCancel = false;
                return;
            }

            // for all started tasks, decrement timer, if the timer is done, do whatever it does
            for(Task task : tasks) {
                task.ticksRemaining--;

                if(task.ticksRemaining == 0)
                    task.task.run();
            }

            // for new tasks, add them all to the main tasks list, needed because if tasks are created in other tasks it causes errors
            tasks.addAll(newTasks);
            newTasks.clear();
        });
    }

    /**
     * A class that represents one scheduled task
     */
    public static class Task {
        public int ticksRemaining;
        public final Runnable task;

        /**
         * @param ticks how many ticks to wait
         * @param task what to actually run
         */
        public Task(int ticks, Runnable task) {
            this.ticksRemaining = ticks;
            this.task = task;
        }
    }
}
