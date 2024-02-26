import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {


        System.out.println("Поехали!");

        Managers manager = new Managers();
        TaskManager taskManager = Managers.getDefault();


        int task1Id = taskManager.createTask(new Task("Лампочка",
                "Съездить на станцию ТО, и заменить лампочку в задней фаре"));
        int task2Id = taskManager.createTask(new Task("Елка",
                "Убрать елку"));

        int epic1Id = taskManager.createEpic(new Epic("Спринт №6",
                "Закончить спринт №6 от ЯП"));

        int subtask1Id = taskManager.createSubtask(new SubTask("Теория №6", "Закончить теорию спринта",
                epic1Id));
        int subtask2Id = taskManager.createSubtask(new SubTask("Финальное задание №6", "Выполнить задание 4-го спринта",
                epic1Id));
        int subtask3Id = taskManager.createSubtask(new SubTask("Финальное задание №6", "Отправить Pull Request в GitHab",
                epic1Id));


        System.out.println("ПРОВЕРКА ОБНОВЛЕНИЯ ИСТОРИИ");

        System.out.println("|Вызов задачи с id 5|");
        taskManager.getSubtaskByID(5);
        System.out.println(taskManager.getHistory());

        System.out.println("|Вызов задачи с id 4|");
        taskManager.getSubtaskByID(4);
        System.out.println(taskManager.getHistory());

        System.out.println("|Вызов задачи с id 3|");
        taskManager.getEpicByID(3);
        System.out.println(taskManager.getHistory());

        System.out.println("|Вызов задачи с id 1|");
        taskManager.getTaskByID(1);
        System.out.println(taskManager.getHistory());

        System.out.println("|Вызов задачи с id 6|");
        taskManager.getSubtaskByID(6);
        System.out.println(taskManager.getHistory());

        System.out.println("|Вызов задачи с id 3|");
        taskManager.getEpicByID(3);
        System.out.println(taskManager.getHistory());

        System.out.println("|Вызов задачи с id 4|");
        taskManager.getSubtaskByID(4);
        System.out.println(taskManager.getHistory());

        System.out.println("|Вызов задачи с id 2|");
        taskManager.getTaskByID(2);
        System.out.println(taskManager.getHistory());

        System.out.println("|Удаление задачи №1|");
        taskManager.deleteTaskByID(1);
        System.out.println("Проверяем историю после удаления задачи 1");
        System.out.println(taskManager.getHistory());

        System.out.println("|Удаление эпика №3|");
        taskManager.deleteEpicByID(3);
        System.out.println("Проверяем историю после удаления эпика №3");
        System.out.println(taskManager.getHistory());


    }
}
