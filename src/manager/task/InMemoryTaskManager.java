package manager.task;

import common.*;
import constant.Status;
import manager.Managers;
import manager.history.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> taskMap = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    protected final HashMap<Integer, Epic> epicMap = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int nextId = 1;

    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public List<SubTask> getSubTaskList() {
        return new ArrayList<>(subTaskMap.values());
    }

    @Override
    public void removeAllTask() {
        taskMap.keySet().forEach(historyManager::remove);
        taskMap.clear();
    }

    @Override
    public void removeAllEpic() {
        epicMap.keySet().forEach(historyManager::remove);
        subTaskMap.keySet().forEach(historyManager::remove);

        epicMap.clear();
        subTaskMap.clear();
    }

    @Override
    public void removeAllSubTask() {
        subTaskMap.keySet().forEach(historyManager::remove);
        subTaskMap.clear();

        for (Integer epicKey : epicMap.keySet()) {
            epicMap.get(epicKey).getSubTaskIds().clear();
            epicMap.get(epicKey).setStatus(Status.NEW);
        }
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epicMap.get(id));
        return epicMap.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        historyManager.add(subTaskMap.get(id));
        return subTaskMap.get(id);
    }

    @Override
    public int createTask(Task task) {
        task.setId(nextId++);
        taskMap.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(nextId++);
        epicMap.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int createSubTask(Epic epic, SubTask subTask) {
        subTask.setId(nextId++);
        subTaskMap.put(subTask.getId(), subTask);
        epic.getSubTaskIds().add(subTask.getId());
        subTask.setEpicId(epic.getId());
        return subTask.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            epicMap.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTaskMap.containsKey(subTask.getId())) {
            subTaskMap.put(subTask.getId(), subTask);
            int epicId = subTask.getEpicId();

            updateSubTaskStatus(epicId);
        }
    }

    @Override
    public void removeTask(int id) {
        taskMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        List<Integer> subTaskIds = epicMap.get(id).getSubTaskIds();
        for (Integer subTaskId : subTaskIds) {
            subTaskMap.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
        epicMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        int epicId = subTaskMap.get(id).getEpicId();
        subTaskMap.remove(id);
        historyManager.remove(id);

        List<Integer> subTaskIds = epicMap.get(epicId).getSubTaskIds();
        subTaskIds.removeIf(subTaskId -> subTaskId == id);

        updateSubTaskStatus(epicId);
    }

    @Override
    public List<SubTask> getAllSubTaskOfEpic(Epic epic) {
        List<SubTask> subTaskList = new ArrayList<>();
        for (Integer subTaskKey : subTaskMap.keySet()) {
            if (subTaskMap.get(subTaskKey).getEpicId() == epic.getId()) {
                subTaskList.add(subTaskMap.get(subTaskKey));
            }
        }
        return subTaskList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateSubTaskStatus(int id) {
        int numberOfSubTask = epicMap.get(id).getSubTaskIds().size();
        int counterStatusNew = 0;
        int counterStatusDone = 0;

        List<Integer> subTaskKeys = epicMap.get(id).getSubTaskIds();
        for (Integer subTaskKey : subTaskKeys) {
            if (subTaskMap.get(subTaskKey).getStatus().equals(Status.NEW)) {
                counterStatusNew++;
            } else if (subTaskMap.get(subTaskKey).getStatus().equals(Status.DONE)) {
                counterStatusDone++;
            }
        }
        if (counterStatusNew == numberOfSubTask) {
            epicMap.get(id).setStatus(Status.NEW);
        } else if (counterStatusDone == numberOfSubTask) {
            epicMap.get(id).setStatus(Status.DONE);
        } else {
            epicMap.get(id).setStatus(Status.IN_PROGRESS);
        }
    }
}
