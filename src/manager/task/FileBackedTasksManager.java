package manager.task;

import common.Epic;
import common.SubTask;
import common.Task;
import constant.Status;
import constant.TaskType;
import exception.ManagerSaveException;
import manager.Managers;
import manager.history.HistoryManager;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final File PATH_FILE = new File("resources/data.csv");

    @Override
    public int createTask(Task task) {
        super.createTask(task);
        save();
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public int createSubTask(Epic epic, SubTask subTask) {
        super.createSubTask(epic, subTask);
        save();
        return subTask.getId();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();

    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();

    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();

    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeAllSubTask() {
        super.removeAllSubTask();
        save();
    }

    public void save() {

        final String header = "id,type,name,status,description,epic";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_FILE, StandardCharsets.UTF_8))) {

            bw.write(header);
            bw.write(System.lineSeparator());

            for (Task task : getTaskList()) {
                bw.write(toString(task));
            }
            for (Epic epic : getEpicList()) {
                bw.write(toString(epic));
            }
            for (SubTask subTask : getSubTaskList()) {
                bw.write(toString(subTask));
            }

            bw.write(System.lineSeparator());
            bw.write(HistoryManager.historyToString(historyManager));

        } catch (IOException exception) {
            throw new ManagerSaveException("Не удалось записать файл");
        }
    }

    private static FileBackedTasksManager loadFromFile(File file) {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        Map<Integer, Task> allTasks = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            List<String> lines = new ArrayList<>();

            while (br.ready()) {
                lines.add(br.readLine());
            }

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);

                if (line.isEmpty()) {
                    line = lines.get(i + 1);
                    List<Integer> history = HistoryManager.historyFromString(line);

                    for (Integer id : history) {
                        fileBackedTasksManager.historyManager.add(allTasks.get(id));
                    }
                    break;
                }

                Task task = fromString(lines.get(i));
                assert task != null;

                if (task instanceof Epic) {
                    fileBackedTasksManager.epicMap.put(task.getId(), (Epic) task);

                    if (task.getId() > fileBackedTasksManager.nextId) {
                        fileBackedTasksManager.nextId = task.getId();
                    }
                    allTasks.put(task.getId(), task);

                } else if (task instanceof SubTask) {
                    fileBackedTasksManager.subTaskMap.put(task.getId(), (SubTask) task);

                    if (task.getId() > fileBackedTasksManager.nextId) {
                        fileBackedTasksManager.nextId = task.getId();
                    }
                    allTasks.put(task.getId(), task);

                    Epic epicToConnectSubTasks = fileBackedTasksManager.epicMap.get(((SubTask) task).getEpicId());
                    epicToConnectSubTasks.getSubTaskIds().add(task.getId());

                } else {
                    fileBackedTasksManager.taskMap.put(task.getId(), task);

                    if (task.getId() > fileBackedTasksManager.nextId) {
                        fileBackedTasksManager.nextId = task.getId();
                    }
                    allTasks.put(task.getId(), task);
                }
            }

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка чтения файла");
        }
        return fileBackedTasksManager;
    }

    private String toString(Task task) {
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

    private static Task fromString(String value) {
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

    public static void main(String[] args) {

        //---------------------------Создаем менеджер---------------------------------
        TaskManager manager = Managers.getDefaultFileManager();

        Task task1 = new Task("Task1", "Description task1");
        Task task2 = new Task("Task2", "Description task2");
        Task task3 = new Task("Task3", "Description task3");

        Epic epic1 = new Epic("Epic1", "Description Epic1");
        SubTask subTask1 = new SubTask("SubTask1", "Description SubTask1");
        SubTask subTask2 = new SubTask("SubTask2", "Description SubTask2");
        SubTask subTask3 = new SubTask("SubTask3", "Description SubTask3");

        Epic epic2 = new Epic("Epic2", "Description Epic2");

        //---------------------------Создаем задачи------------------------------------
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        manager.createEpic(epic1);
        manager.createSubTask(epic1, subTask1);
        manager.createSubTask(epic1, subTask2);
        manager.createSubTask(epic1, subTask3);

        manager.createEpic(epic2);

        //---------------------------Просматриваем задачи------------------------------
        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubTask(subTask3.getId());
        manager.getSubTask(subTask2.getId());

        //---------------------------Создаем новый менеджер----------------------------
        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(PATH_FILE);

        //------------------Выводим на экран задачи, созданные первым менеджером-------
        System.out.println("Загружаем задачи...\n");

        for (Task task : newManager.getTaskList()) {
            System.out.println(task);
        }

        for (Epic epic : newManager.getEpicList()) {
            System.out.println(epic);
        }

        for (SubTask subTask : newManager.getSubTaskList()) {
            System.out.println(subTask);
        }

        System.out.println();
        System.out.println("Задачи загружены.\n");

        //------------------Выводим на экран ID просмотренных задач---------------------

        System.out.println();
        System.out.println("Загружаем историю просмотров...\n");

        for (Task task : newManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println();
        System.out.println("История просмотров загружена.");
    }
}
