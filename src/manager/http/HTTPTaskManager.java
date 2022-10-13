package manager.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import common.Epic;
import common.SubTask;
import common.Task;
import exception.ManagerSaveException;
import manager.task.FileBackedTasksManager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;


public class HTTPTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;
    private final Gson gson = new Gson();

    public HTTPTaskManager(String url) {
        this.kvTaskClient = new KVTaskClient(url);
    }



    @Override
    protected void save() {
        try {
            kvTaskClient.put("tasks/task", gson.toJson(taskMap));
            kvTaskClient.put("tasks/epic", gson.toJson(epicMap));
            kvTaskClient.put("tasks/subtask", gson.toJson(subTaskMap));
            kvTaskClient.put("tasks/history", gson.toJson(getHistory()));
        } catch (Exception e) {
            throw new ManagerSaveException("Ошибка в сохранении");
        }
    }

    public void loadFromServer() {
        String tasksFromJson = kvTaskClient.load("tasks/task");
        if (tasksFromJson != null) {
            Type typeToken = new TypeToken<HashMap<Integer, Task>>() {
            }.getType();

            taskMap = gson.fromJson(tasksFromJson, typeToken);
            taskTreeSet.addAll(taskMap.values());
        }

        String epicsFromJson = kvTaskClient.load("tasks/epic");
        if (epicsFromJson != null) {
            Type typeToken = new TypeToken<HashMap<Integer, Epic>>() {
            }.getType();

            epicMap = gson.fromJson(epicsFromJson, typeToken);
            taskTreeSet.addAll(epicMap.values());
        }

        String subTasksFromJson = kvTaskClient.load("tasks/subtask");
        if (subTasksFromJson != null) {
            Type typeToken = new TypeToken<HashMap<Integer, SubTask>>() {
            }.getType();

            subTaskMap = gson.fromJson(subTasksFromJson, typeToken);
            taskTreeSet.addAll(subTaskMap.values());
        }

        String historyFromJson = kvTaskClient.load("tasks/history");
        if (historyFromJson != null) {
            Type typeToken = new TypeToken<List<Task>>() {
            }.getType();

            List<Task> historyList = gson.fromJson(historyFromJson, typeToken);
            for (Task task : historyList) {
                historyManager.add(task);
            }
        }
    }
}
