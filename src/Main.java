import model.Epic;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        int task1Id = taskManager.createTask(new Task("Лампочка",
                "Съездить на станцию ТО, и заменить лампочку в задней фаре"));
        int task2Id = taskManager.createTask(new Task("Елка",
                "Убрать елку") );

        int epic1Id = taskManager.createEpic(new Epic("Спринт №4",
                "Закончить спринт №4 от ЯП"));

        int subtask1Id = taskManager.createSubtask(new SubTask("Теория №4","Закончить теорию спринта",
                epic1Id));
        int subtask2Id = taskManager.createSubtask(new SubTask("Финальное задание №4","Выполнить задание 4-го спринта",
                epic1Id));



        // проверка получения подзадач эпика

        System.out.println(taskManager.getEpicSubtasks(epic1Id));

        taskManager.updateTask(new Task("Задняя фара", "Купить заднюю фару")); // Обновление обычной задачи
        taskManager.updateSubtask(new SubTask("Теория №4", "Закончить теорию спринта", epic1Id));
        taskManager.updateSubtask(new SubTask("Финальное задание №4","Выполнить задание 4-го спринта",epic1Id));

        //проверка обновления Эпика
        taskManager.updateEpic(new Epic("Ревью 4-го спринта", "Отправить материалы через GitHub" ));

        //проверка удаления обычной задачи
        taskManager.deleteTaskByID(task2Id);

        //проверка удаления подзадачи
        taskManager.deleteSubtaskByID(subtask1Id);

        //проверка удаления всех задач


        System.out.println(taskManager.getSubtaskByID(subtask2Id).toString());

        System.out.println(taskManager.getEpicByID(epic1Id).toString());

        taskManager.deleteAllTasks();
    }
}
