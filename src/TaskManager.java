import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    public static final String newStatus = "NEW";
    public static final String inProgressStatus = "IN_PROGRESS";
    public static final String doneStatus = "DONE";

    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicMap = new HashMap<>();
    private int nextId = 1;

    public List<Task> getTaskList() {
        List<Task> taskList = new ArrayList<>();
        for (Integer taskKey : taskMap.keySet()) {
            taskList.add(taskMap.get(taskKey));
        }
        return taskList;
    }

    public List<Epic> getEpicList() {
        List<Epic> epicList = new ArrayList<>();
        for (Integer epicKey : epicMap.keySet()) {
            epicList.add(epicMap.get(epicKey));
        }
        return epicList;
    }

    public List<SubTask> getSubTaskList() {
        List<SubTask> subTaskList = new ArrayList<>();
        for (Integer epicKey : subTaskMap.keySet()) {
            subTaskList.add(subTaskMap.get(epicKey));
        }
        return subTaskList;
    }

    public void removeAllTask() {
        taskMap.clear();
    }

    public void removeAllEpic() {
        epicMap.clear();
        subTaskMap.clear();
    }

    public void removeAllSubTask() {
        subTaskMap.clear();
        for (Integer epicKey : epicMap.keySet()) {
            epicMap.get(epicKey).getSubTaskIds().clear();
            epicMap.get(epicKey).setStatus(newStatus);
        }
    }

    public Task getTask(int id) {
        return taskMap.get(id);
    }

    public Epic getEpic(int id) {
        return epicMap.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTaskMap.get(id);
    }

    public int createTask(Task task) {
        int taskId = nextId++;
        if (task.getStatus().equals(newStatus)) {
            task.setId(taskId);
            taskMap.put(taskId, task);
        }
        return task.getId();
    }

    public int createEpic(Epic epic) {
        int epicId = nextId++;
        epic.setId(epicId);
        epicMap.put(epicId, epic);

        return epic.getId();
    }

    public int createSubTask(Epic epic, SubTask subTask) {
        int subTaskId = nextId++;
        if (subTask.getStatus().equals(newStatus)) {
            subTask.setId(subTaskId);
            subTaskMap.put(subTaskId, subTask);
            epic.getSubTaskIds().add(subTaskId);
            subTask.setEpicId(epic.getId());
        }
        return subTask.getId();
    }

    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epicMap.put(epic.getId(), epic);
    }

    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getId(), subTask);
        int epicId = subTask.getEpicId();

        updateSubTaskStatus(epicId);
    }

    public void removeTask(int id) {
        taskMap.remove(id);
    }

    public void removeEpic(int id) {
        List<Integer> subTaskIds = epicMap.get(id).getSubTaskIds();
        for (Integer subTaskId : subTaskIds) {
            subTaskMap.remove(subTaskId);
        }
        epicMap.remove(id);
    }

    public void removeSubTask(int id) {
        int epicId = subTaskMap.get(id).getEpicId();
        subTaskMap.remove(id);

        List<Integer> subTaskIds = epicMap.get(epicId).getSubTaskIds();
        subTaskIds.removeIf(subTaskId -> subTaskId == id);

        updateSubTaskStatus(epicId);
    }

    public List<SubTask> getAllSubTaskOfEpic(Epic epic) {
        List<SubTask> subTaskList = new ArrayList<>();
        for (Integer subTaskKey : subTaskMap.keySet()) {
            if (subTaskMap.get(subTaskKey).getEpicId() == epic.getId()) {
                subTaskList.add(subTaskMap.get(subTaskKey));
            }
        }
        return subTaskList;
    }

    private void updateSubTaskStatus(int id) {
        int numberOfSubTask = epicMap.get(id).getSubTaskIds().size();
        int counterStatusNew = 0;
        int counterStatusDone = 0;

        List<Integer> subTaskKeys = epicMap.get(id).getSubTaskIds();
        for (Integer subTaskKey : subTaskKeys) {
            if (subTaskMap.get(subTaskKey).getStatus().equals(newStatus)) {
                counterStatusNew++;
            } else if (subTaskMap.get(subTaskKey).getStatus().equals(doneStatus)) {
                counterStatusDone++;
            }
        }
        if (counterStatusNew == numberOfSubTask) {
            epicMap.get(id).setStatus(newStatus);
        } else if (counterStatusDone == numberOfSubTask) {
            epicMap.get(id).setStatus(doneStatus);
        } else {
            epicMap.get(id).setStatus(inProgressStatus);
        }
    }
}
