package manager;

import common.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int nextId = 1;

    @Override
    public List<Task> getTaskList() {
        List<Task> taskList = new ArrayList<>();
        for (Integer taskKey : taskMap.keySet()) {
            taskList.add(taskMap.get(taskKey));
        }
        return taskList;
    }

    @Override
    public List<Epic> getEpicList() {
        List<Epic> epicList = new ArrayList<>();
        for (Integer epicKey : epicMap.keySet()) {
            epicList.add(epicMap.get(epicKey));
        }
        return epicList;
    }

    @Override
    public List<SubTask> getSubTaskList() {
        List<SubTask> subTaskList = new ArrayList<>();
        for (Integer epicKey : subTaskMap.keySet()) {
            subTaskList.add(subTaskMap.get(epicKey));
        }
        return subTaskList;
    }

    @Override
    public void removeAllTask() {
        taskMap.clear();
    }

    @Override
    public void removeAllEpic() {
        epicMap.clear();
        subTaskMap.clear();
    }

    @Override
    public void removeAllSubTask() {
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
        int taskId = nextId++;
        if (task.getStatus().equals(Status.NEW)) {
            task.setId(taskId);
            taskMap.put(taskId, task);
        }
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        int epicId = nextId++;
        epic.setId(epicId);
        epicMap.put(epicId, epic);

        return epic.getId();
    }

    @Override
    public int createSubTask(Epic epic, SubTask subTask) {
        int subTaskId = nextId++;
        if (subTask.getStatus().equals(Status.NEW)) {
            subTask.setId(subTaskId);
            subTaskMap.put(subTaskId, subTask);
            epic.getSubTaskIds().add(subTaskId);
            subTask.setEpicId(epic.getId());
        }
        return subTask.getId();
    }

    @Override
    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getId(), subTask);
        int epicId = subTask.getEpicId();

        updateSubTaskStatus(epicId);
    }

    @Override
    public void removeTask(int id) {
        taskMap.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        List<Integer> subTaskIds = epicMap.get(id).getSubTaskIds();
        for (Integer subTaskId : subTaskIds) {
            subTaskMap.remove(subTaskId);
        }
        epicMap.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        int epicId = subTaskMap.get(id).getEpicId();
        subTaskMap.remove(id);

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
