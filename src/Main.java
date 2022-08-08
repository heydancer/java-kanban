import common.*;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Java", "Сделать домашнее задание", Status.NEW);
        Task task2 = new Task("Английский", "Поехать на занятие", Status.NEW);
        Task task3 = new Task("Немецкий", "Поехать на занятие", Status.NEW);
        Task task4 = new Task("Китайский", "Поехать на занятие", Status.NEW);
        Task task5 = new Task("Французский", "Поехать на занятие", Status.NEW);
        Task task6 = new Task("Японский", "Поехать на занятие", Status.NEW);

        Epic epic1 = new Epic("Переезд", "Покупка новой квартиры");
        SubTask subTask1 = new SubTask("Собрать вещи", "Сложить все вещи по коробкам", Status.NEW);
        SubTask subTask2 = new SubTask("Собрать игрушки собаки", "Найти все игрушки", Status.NEW);

        Epic epic2 = new Epic("Магазин", "Поход в магазин");
        SubTask subTask3 = new SubTask("Купить крупу", "Найти рис", Status.NEW);

        //Создаем задачи
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.createEpic(epic1);
        taskManager.createSubTask(epic1, subTask1);
        taskManager.createSubTask(epic1, subTask2);

        taskManager.createEpic(epic2);
        taskManager.createSubTask(epic2, subTask3);

        taskManager.createTask(task3);
        taskManager.createTask(task4);
        taskManager.createTask(task5);
        taskManager.createTask(task6);

        //Смотрим список всех созданных задач
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubTaskList() + "\n");

        //Поменяем статус СабТаски и посомтрим изменения в истории
        subTask1.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);

        //Обращаемся ко всем задачам
        taskManager.getTask(1);
        taskManager.getTask(2);

        taskManager.getEpic(3);

        taskManager.getSubTask(4);
        taskManager.getSubTask(5);

        taskManager.getEpic(6);
        taskManager.getSubTask(7);

        taskManager.getTask(8);
        taskManager.getTask(9);
        taskManager.getTask(10);
        taskManager.getTask(11);

        //Выводим историю просмотров задач
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
