package manager.task;

import common.*;
import constant.Status;
import exception.ManagerSaveException;
import manager.Managers;
import manager.history.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> taskMap = new HashMap<>();
    protected HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicMap = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final TreeSet<Task> taskTreeSet = new TreeSet<>((o1, o2) -> {
        if (o1.getStartTime() == null && o2.getStartTime() == null) return o1.getId() - o2.getId();
        if (o1.getStartTime() == null) return 1;
        if (o2.getStartTime() == null) return -1;
        if (o1.getStartTime().isAfter(o2.getStartTime())) return 1;
        if (o1.getStartTime().isBefore(o2.getStartTime())) return -1;
        if (o1.getStartTime().isEqual(o2.getStartTime())) return o1.getId() - o2.getId();
        return 0;
    });

    protected int nextId = 1;

    private void setTaskEndTime(Task task) {
        if (task.getDuration() != null && task.getStartTime() != null) {
            LocalDateTime endTime = task.getStartTime().plus(task.getDuration());
            task.setEndTime(endTime);
        }
    }

    private void setEpicEndTimeAndDuration(Epic epic) {

        if (!epic.getSubTaskIds().isEmpty()) {
            LocalDateTime start = LocalDateTime.MAX;
            LocalDateTime end = LocalDateTime.MIN;
            for (Integer id : epic.getSubTaskIds()) {
                if (subTaskMap.get(id).getStartTime() != null && subTaskMap.get(id).getStartTime().isBefore(start)) {
                    start = subTaskMap.get(id).getStartTime();
                }
                if (subTaskMap.get(id).getStartTime() != null && subTaskMap.get(id).getEndTime().isAfter(end)) {
                    end = subTaskMap.get(id).getEndTime();
                }
            }

            epic.setStartTime(start);
            epic.setEndTime(end);
            epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()));

        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
        }
    }

    private boolean checkIntersection(Task task) {
        boolean isValid = true;

        if (!taskTreeSet.isEmpty()) {
            for (Task taskForCheck : taskTreeSet) {

                if ((task.getStartTime() != null && task.getEndTime() != null) &&
                        (taskForCheck.getStartTime() != null && taskForCheck.getEndTime() != null)) {

                    if ((task.getStartTime().isEqual(taskForCheck.getStartTime())) ||
                            (task.getEndTime().isEqual(taskForCheck.getEndTime())) ||
                            ((task.getStartTime().isBefore(taskForCheck.getEndTime())) &&
                                    (task.getStartTime().isAfter(taskForCheck.getStartTime()))) ||
                            ((task.getEndTime().isBefore(taskForCheck.getEndTime())) &&
                                    (task.getEndTime().isAfter(taskForCheck.getStartTime()))) ||
                            ((task.getStartTime().isBefore(taskForCheck.getStartTime())) &&
                                    (task.getEndTime().isAfter(taskForCheck.getEndTime()))) ||
                            ((task.getStartTime().isAfter(taskForCheck.getStartTime())) &&
                                    (task.getEndTime().isBefore(taskForCheck.getEndTime())))) {
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
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
        for (Integer taskId : taskMap.keySet()) {
            historyManager.remove(taskId);
            taskTreeSet.remove(taskMap.get(taskId));
        }
        taskMap.clear();
    }

    @Override
    public void removeAllEpic() {
        epicMap.keySet().forEach(historyManager::remove);
        subTaskMap.keySet().stream().map(subTaskMap::get).forEach(taskTreeSet::remove);
        subTaskMap.keySet().forEach(historyManager::remove);

        epicMap.clear();
        subTaskMap.clear();
    }

    @Override
    public void removeAllSubTask() {
        subTaskMap.keySet().stream().map(subTaskMap::get).forEach(taskTreeSet::remove);
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

        if (task.getDuration() == null) {
            task.setDuration(Duration.ZERO);
        }

        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }

        setTaskEndTime(task);

        if (checkIntersection(task)) {
            taskTreeSet.add(task);
            taskMap.put(task.getId(), task);
        } else {
            throw new ManagerSaveException("Задача не сохранена, задачи не должны пересекаться по времени исполнения");
        }
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(nextId++);

        if (epic.getSubTaskIds() == null) {
            epic.setSubTaskIds(new ArrayList<>());
        }

        if (epic.getDuration() == null) {
            epic.setDuration(Duration.ZERO);
        }

        if (epic.getStatus() == null) {
            epic.setStatus(Status.NEW);
        }

        epicMap.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int createSubTask(Epic epic, SubTask subTask) {
        subTask.setId(nextId++);

        if (subTask.getDuration() == null) {
            subTask.setDuration(Duration.ZERO);
        }

        if (subTask.getStatus() == null) {
            subTask.setStatus(Status.NEW);
        }

        setTaskEndTime(subTask);
        epic.getSubTaskIds().add(subTask.getId());
        subTask.setEpicId(epic.getId());

        if (checkIntersection(subTask)) {
            taskTreeSet.add(subTask);
            subTaskMap.put(subTask.getId(), subTask);

            if (subTask.getStartTime() == null) {
                return subTask.getId();
            }

            setEpicEndTimeAndDuration(epic);

        } else {
            throw new ManagerSaveException("Подзадача не сохранена, задачи не должны пересекаться по времени исполнения");
        }
        return subTask.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            setTaskEndTime(task);
            taskTreeSet.remove(taskMap.get(task.getId()));
            taskTreeSet.add(task);
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
            setTaskEndTime(subTask);
            taskTreeSet.remove(subTaskMap.get(subTask.getId()));
            subTaskMap.put(subTask.getId(), subTask);
            int epicId = subTask.getEpicId();

            updateSubTaskStatus(epicId);
            setEpicEndTimeAndDuration(epicMap.get(epicId));
            taskTreeSet.add(subTask);
        }
    }

    @Override
    public void removeTask(int id) {
        taskTreeSet.remove(taskMap.get(id));
        taskMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        List<Integer> subTaskIds = epicMap.get(id).getSubTaskIds();
        for (Integer subTaskId : subTaskIds) {
            taskTreeSet.remove(subTaskMap.get(subTaskId));
            subTaskMap.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
        epicMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        int epicId = subTaskMap.get(id).getEpicId();
        taskTreeSet.remove(subTaskMap.get(id));
        subTaskMap.remove(id);
        historyManager.remove(id);

        List<Integer> subTaskIds = epicMap.get(epicId).getSubTaskIds();
        subTaskIds.removeIf(subTaskId -> subTaskId == id);

        updateSubTaskStatus(epicId);
        setEpicEndTimeAndDuration(epicMap.get(epicId));
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

    @Override
    public Set<Task> getPrioritizedTasks() {
        return taskTreeSet;
    }
}
