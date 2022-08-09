import common.*;
import manager.Managers;
import manager.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Java", "Сделать домашнее задание");
        Task task2 = new Task("Английский", "Поехать на занятие");
        Task task3 = new Task("Немецкий", "Поехать на занятие");
        Task task4 = new Task("Китайский", "Поехать на занятие");
        Task task5 = new Task("Французский", "Поехать на занятие");
        Task task6 = new Task("Японский", "Поехать на занятие");

        Epic epic1 = new Epic("Переезд", "Покупка новой квартиры");
        SubTask subTask1 = new SubTask("Собрать вещи", "Сложить все вещи по коробкам");
        SubTask subTask2 = new SubTask("Собрать игрушки собаки", "Найти все игрушки");

        Epic epic2 = new Epic("Магазин", "Поход в магазин");
        SubTask subTask3 = new SubTask("Купить крупу", "Найти рис");

        //Создаем задачи
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);
        taskManager.createTask(task5);
        taskManager.createTask(task6);

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        taskManager.createSubTask(epic1, subTask1);
        taskManager.createSubTask(epic1, subTask2);
        taskManager.createSubTask(epic2, subTask3);

        System.out.println("Создание Task: ");
        for (Task task : taskManager.getTaskList()) {
            System.out.println(task);
        }
        System.out.println();

        System.out.println("Создание Epic: ");
        for (Epic epic : taskManager.getEpicList()) {
            System.out.println(epic);
        }
        System.out.println();

        System.out.println("Создание SubTask: ");
        for (SubTask subTask : taskManager.getSubTaskList()) {
            System.out.println(subTask);
        }
        System.out.println();

        //Меняем статус одной из созданой SubTask
        subTask1.setStatus(Status.DONE);
        //Обновляем SubTask
        taskManager.updateSubTask(subTask1);

        //Обращаемся ко всем задачам
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(3);
        taskManager.getTask(4);
        taskManager.getTask(5);
        taskManager.getTask(6);

        taskManager.getEpic(7);
        taskManager.getEpic(8);

        taskManager.getSubTask(9);
        taskManager.getSubTask(10);
        taskManager.getSubTask(11);

        System.out.println("История просмотров: ");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
