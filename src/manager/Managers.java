package manager;

import manager.history.HistoryManager;
import manager.history.InMemoryHistoryManager;
import manager.http.HTTPTaskManager;
import manager.task.FileBackedTasksManager;
import manager.task.TaskManager;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    public static TaskManager getDefault() {
        return new HTTPTaskManager("http://localhost:8078");
    }
    public static TaskManager getDefaultFileManager() {
        return new FileBackedTasksManager();
    }
}
