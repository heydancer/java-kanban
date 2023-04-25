package manager.formatter;

import common.Epic;
import common.SubTask;
import common.Task;
import constant.Status;
import constant.TaskType;
import manager.history.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
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
        String duration = task.getDuration().toString();
        String startTime;
        String endTime;

        if (task instanceof Epic) {
            type = TaskType.EPIC.name();
            epic = "";

            if (task.getStartTime() != null && task.getEndTime() != null) {
                startTime = task.getStartTime().toString();
                endTime = task.getEndTime().toString();
            } else {
                startTime = "null";
                endTime = "null";
            }

        } else if (task instanceof SubTask) {
            type = TaskType.SUBTASK.name();
            epic = String.valueOf(((SubTask) task).getEpicId());

            if (task.getStartTime() != null && task.getEndTime() != null) {
                startTime = task.getStartTime().toString();
                endTime = task.getEndTime().toString();
            } else {
                startTime = "null";
                endTime = "null";
            }

        } else {
            type = TaskType.TASK.name();
            epic = "";

            if (task.getStartTime() != null && task.getEndTime() != null) {
                startTime = task.getStartTime().toString();
                endTime = task.getEndTime().toString();
            } else {
                startTime = "null";
                endTime = "null";
            }
        }

        return String.join(",", id, type, name, status, description, epic, duration, startTime, endTime)
                + System.lineSeparator();
    }

    public static Task fromString(String value) {
        String[] taskArray = value.split(",");

        int taskId = Integer.parseInt(taskArray[0]);
        TaskType taskType = TaskType.valueOf(taskArray[1]);
        String taskName = taskArray[2];
        Status taskStatus = Status.valueOf(taskArray[3]);
        String taskDescription = taskArray[4];
        Duration taskDuration = Duration.parse(taskArray[6]);
        LocalDateTime taskStartTime;
        LocalDateTime taskEndTime;

        if (taskArray[7].equals("null") && taskArray[8].equals("null")) {
            taskStartTime = null;
            taskEndTime = null;
        } else {
            taskStartTime = LocalDateTime.parse(taskArray[7]);
            taskEndTime = LocalDateTime.parse(taskArray[8]);
        }

        switch (taskType) {
            case TASK:
                Task task = new Task(taskName, taskDescription, taskDuration, taskStartTime);
                task.setId(taskId);
                task.setStatus(taskStatus);
                task.setEndTime(taskEndTime);
                return task;
            case EPIC:
                Epic epic = new Epic(taskName, taskDescription);
                epic.setId(taskId);
                epic.setStatus(taskStatus);
                epic.setEndTime(taskEndTime);
                return epic;
            case SUBTASK:
                int taskEpicId = Integer.parseInt(taskArray[5]);
                SubTask subTask = new SubTask(taskName, taskDescription, taskDuration, taskStartTime);
                subTask.setEpicId(taskEpicId);
                subTask.setId(taskId);
                subTask.setStatus(taskStatus);
                subTask.setEndTime(taskEndTime);
                return subTask;
        }

        return null;
    }
}
