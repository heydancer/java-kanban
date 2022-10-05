package manager.task;

import common.Epic;
import common.SubTask;
import common.Task;
import exception.ManagerSaveException;
import manager.formatter.Formatter;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final File PATH_FILE = new File("resources/data.csv");

    private void save() {

        final String header = "id,type,name,status,description,epic,duration,startTime,endTime";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_FILE, StandardCharsets.UTF_8))) {

            bw.write(header);
            bw.write(System.lineSeparator());

            for (Task task : getTaskList()) {
                bw.write(Formatter.toString(task));
            }
            for (Epic epic : getEpicList()) {
                bw.write(Formatter.toString(epic));
            }
            for (SubTask subTask : getSubTaskList()) {
                bw.write(Formatter.toString(subTask));
            }

            bw.write(System.lineSeparator());
            bw.write(Formatter.historyToString(historyManager));

        } catch (IOException exception) {
            throw new ManagerSaveException("Не удалось записать файл");
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {

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
                    List<Integer> history = Formatter.historyFromString(line);

                    for (Integer id : history) {
                        if (id == 0) {
                            break;
                        }
                        fileBackedTasksManager.historyManager.add(allTasks.get(id));
                    }
                    break;
                }

                Task task = Formatter.fromString(lines.get(i));
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
}


