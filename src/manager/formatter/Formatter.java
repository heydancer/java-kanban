package manager.formatter;

import common.Epic;
import common.SubTask;
import common.Task;
import constant.Status;
import constant.TaskType;
import manager.history.HistoryManager;

import java.util.ArrayList;
import java.util.List;

public class Formatter {

    public static String historyToString(HistoryManager manager) {
        StringBuilder historyIds = new StringBuilder();

        if (manager.getHistory().isEmpty()) {
            return historyIds.append("0").toString();

        } else

            for (Task task : manager.getHistory()) {
                historyIds.append(task.getId()).append(',');
            }

        return historyIds.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> historyIds = new ArrayList<>();
        String[] taskArray = value.split(",");

        for (String element : taskArray) {
            historyIds.add(Integer.valueOf(element));
        }

        return historyIds;
    }

    public static String toString(Task task) {
        String id = String.valueOf(task.getId());
        String type;
        String name = task.getName();
        String status = String.valueOf(task.getStatus());
        String description = task.getDescription();
        String epic;

        if (task instanceof Epic) {
            type = TaskType.EPIC.name();
            epic = "";
        } else if (task instanceof SubTask) {
            type = TaskType.SUBTASK.name();
            epic = String.valueOf(((SubTask) task).getEpicId());
        } else {
            type = TaskType.TASK.name();
            epic = "";
        }

        return String.join(",", id, type, name, status, description, epic) + System.lineSeparator();
    }

    public static Task fromString(String value) {
        String[] taskArray = value.split(",");

        int taskId = Integer.parseInt(taskArray[0]);
        TaskType taskType = TaskType.valueOf(taskArray[1]);
        String taskName = taskArray[2];
        Status taskStatus = Status.valueOf(taskArray[3]);
        String taskDescription = taskArray[4];

        switch (taskType) {
            case TASK:
                Task task = new Task(taskName, taskDescription);
                task.setId(taskId);
                task.setStatus(taskStatus);
                return task;
            case EPIC:
                Epic epic = new Epic(taskName, taskDescription);
                epic.setId(taskId);
                epic.setStatus(taskStatus);
                return epic;
            case SUBTASK:
                int taskEpicId = Integer.parseInt(taskArray[5]);
                SubTask subTask = new SubTask(taskName, taskDescription);
                subTask.setEpicId(taskEpicId);
                subTask.setId(taskId);
                subTask.setStatus(taskStatus);
                return subTask;
        }

        return null;
    }
}
