package manager.task;

import common.Epic;
import common.SubTask;
import common.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<SubTask> getSubTaskList();

    void removeAllTask();

    void removeAllEpic();

    void removeAllSubTask();

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubTask(Epic epic, SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubTask(int id);

    List<SubTask> getAllSubTaskOfEpic(Epic epic);

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

}


