package manager;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import manager.task.FileBackedTasksManager;
import manager.task.InMemoryTaskManager;
import manager.task.TaskManager;

public class Managers {

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefaultFileManager() {
        return new FileBackedTasksManager();
    }
}
