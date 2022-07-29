public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Java", "Сделать домашнее задание", TaskManager.newStatus);
        Task task2 = new Task("Английский", "Поехать на занятие", TaskManager.newStatus);

        Epic epic1 = new Epic("Переезд", "Покупка новой квартиры");
        SubTask subTask1 = new SubTask("Собрать вещи", "Сложить все вещи по коробкам", TaskManager.newStatus);
        SubTask subTask2 = new SubTask("Собрать игрушки собаки", "Найти все игрушки", TaskManager.newStatus);

        Epic epic2 = new Epic("Магазин", "Поход в магазин");
        SubTask subTask3 = new SubTask("Купить крупу", "Найти рис", TaskManager.newStatus);

        //Создайте 2 задачи, один эпик с 2 подзадачами, а другой эпик с 1 подзадачей.
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.createEpic(epic1);
        taskManager.createSubTask(epic1, subTask1);
        taskManager.createSubTask(epic1, subTask2);

        taskManager.createEpic(epic2);
        taskManager.createSubTask(epic2, subTask3);

        //Распечатайте списки эпиков, задач и подзадач, через System.out.println(..)
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubTaskList() + "\n");

        /*Измените статусы созданных объектов, распечатайте.
         Проверьте, что статус задачи и подзадачи сохранился,
         а статус эпика рассчитался по статусам подзадач.*/
        task1.setStatus(TaskManager.inProgressStatus);
        task2.setStatus(TaskManager.doneStatus);
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);

        subTask1.setStatus(TaskManager.inProgressStatus);
        taskManager.updateSubTask(subTask1);

        subTask3.setStatus(TaskManager.doneStatus);
        taskManager.updateSubTask(subTask3);

        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubTaskList() + "\n");

        //И, наконец, попробуйте удалить одну из задач и один из эпиков.
        taskManager.removeTask(task1.getId());
        taskManager.removeEpic(epic1.getId());

        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubTaskList());
    }
}
