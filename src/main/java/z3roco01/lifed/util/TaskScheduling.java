package z3roco01.lifed.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;

/**
 * Allows to schedule tasks that will run in a certain amount of ticks
 */
public class TaskScheduling {
    private static final ArrayList<Task> tasks = new ArrayList<>();
    private static boolean shouldCancel = false;

    public static void scheduleTask(int ticks, Runnable runnable) {
        Task task = new Task(ticks, runnable);
        tasks.add(task);
    }

    public static void cancelTasks() {
        shouldCancel = true;
    }

    public static void registerTickEnd() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if(shouldCancel) {
                tasks.clear();
                shouldCancel = false;
                return;
            }

            for(Task task : tasks) {
                task.ticksRemaining--;

                if(task.ticksRemaining == 0)
                    task.task.run();
            }
        });
    }

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
