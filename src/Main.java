import model.Epic;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        taskManager.createTask(new Task("Лампочка",
                "Съездить на станцию ТО, и заменить лампочку в задней фаре"));
        taskManager.createTask(new Task("Елка",
                "Убрать елку") );

        taskManager.createEpic(new Epic("Спринт №4",
                "Закончить спринт №4 от ЯП"));

        taskManager.createSubtask(new SubTask("Теория №4","Закончить теорию спринта",
                3));
        taskManager.createSubtask(new SubTask("Финальное задание №4","Выполнить задание 4-го спринта",
                3));



        // проверка получения подзадач эпика

        System.out.println(taskManager.getEpicSubtaskIDs(3));

        taskManager.updateTask(new Task("Задняя фара", "Купить заднюю фару")); // Обновление обычной задачи
        taskManager.updateSubtask(new SubTask("Теория №4", "Закончить теорию спринта", 3));
        taskManager.updateSubtask(new SubTask("Финальное задание №4","Выполнить задание 4-го спринта",3));

        //проверка обновления Эпика
        taskManager.updateEpic(new Epic("Ревью 4-го спринта", "Отправить материалы через GitHub" ));

        //проверка удаления обычной задачи
        taskManager.deleteTaskByID(2);

        //проверка удаления подзадачи
        taskManager.deleteSubtaskByID(4);

        //проверка удаления всех задач
        taskManager.deleteAllTasks();

        System.out.println(taskManager.getSubtaskByID(5).toString());

    }
}
