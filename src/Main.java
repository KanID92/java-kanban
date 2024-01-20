import model.Epic;
import model.Progress;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        taskManager.createTask(new Task(taskManager.setNewID(),"Лампочка",
                "Съездить на станцию ТО, и заменить лампочку в задней фаре"));
        taskManager.createTask(new Task(taskManager.setNewID(),"Елка",
                "Убрать елку") );

        taskManager.createTask(new Epic(taskManager.setNewID(),"Спринт №4",
                "Закончить спринт №4 от ЯП"));

        taskManager.createTask(new SubTask(taskManager.setNewID(), "Теория №4",
                "Закончить теорию спринта"), taskManager.getEpicByID(3));

        taskManager.createTask(new SubTask(taskManager.setNewID(),"Финальное задание №4",
                "Выполнить задание 4-го спринта"), taskManager.getEpicByID(3));


        /* ПРОВЕРКИ
        taskManager.getAllTasks(); // Вывод всех задач на экран для проверки

        // проверка получения подзадач эпика
        System.out.println(taskManager.getEpicSubtasks(taskManager.getEpicByName("Спринт №4")));

        taskManager.updateTask(1, new Task(1,"Задняя фара", "Купить заднюю фару"),
                Progress.IN_PROGRESS); // Обновление обычной задачи
        taskManager.updateSubtask(4, new SubTask(4,"Теория №4", "Закончить теорию спринта"),
                Progress.DONE, taskManager.getEpicByID(3));
        taskManager.updateSubtask(5, new SubTask(5,"Финальное задание №4",
                        "Выполнить задание 4-го спринта"), Progress.IN_PROGRESS, taskManager.getEpicByID(3));

        taskManager.getAllTasks();

        //проверка обновления Эпика
        taskManager.updateEpic(3, new Epic(3, "Ревью 4-го спринта", "Отправить материалы через GitHub" ));

        taskManager.getAllTasks();

        //проверка удаления обычной задачи
        taskManager.deleteTaskByID(2);

        taskManager.getAllTasks();

        //проверка удаления подзадачи
        taskManager.deleteSubtaskByID(4);

        taskManager.getAllTasks();

        //проверка удаления всех задач
        taskManager.deleteAllTasks();

        taskManager.getAllTasks();

         */
    }
}
