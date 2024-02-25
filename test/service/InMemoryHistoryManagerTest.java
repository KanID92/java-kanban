package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();
    int taskTest1Id;
    int taskTest2Id;
    int taskTest3Id;
    Task taskTest1;
    Task taskTest2;
    Task taskTest3;

    int epicTest1Id;
    Epic epicTest1;
    int subTaskTest1Id;
    SubTask subTaskTest1;


    @BeforeEach
    void beforeEach() {
        taskTest1Id = taskManager.createTask(new Task("Задача №1", "Для тестов"));
        taskTest1 = taskManager.getTaskByID(taskTest1Id); // вызов 1 задачи

        taskTest2Id = taskManager.createTask(new Task("Задача №2", "Для тестов"));
        taskTest2 = taskManager.getTaskByID(taskTest2Id); // вызов 2 задачи

        taskTest3Id = taskManager.createTask(new Task("Задача №3", "Для тестов"));
        taskTest3 = taskManager.getTaskByID(taskTest2Id); // вызов 3 задачи

        epicTest1Id = taskManager.createEpic(new Epic("Задача №4", "Для тестов"));
        epicTest1 = taskManager.getEpicByID(epicTest1Id); // вызов 4 задачи

        subTaskTest1Id = taskManager.createSubtask(new SubTask("Задача №5", "Для тестов", epicTest1Id));
        subTaskTest1 = taskManager.getSubtaskByID((subTaskTest1Id)); // вызов 5 задачи

        taskManager.getTaskByID(1); // вызов 1 задачи
        taskManager.getTaskByID(3); // вызов 3 задачи
        taskManager.getTaskByID(2); // вызов 2 задачи
        taskManager.getTaskByID(1); // вызов 1 задачи

    }
    @Test
    void shouldSavePreviousAndNewTasksVersions() {

        taskTest2.setId(taskTest1Id);
        taskManager.updateTask(taskTest2);

        assertTrue(taskManager.getHistory().contains(taskTest1),
                "История содержит предыдущую версию задачи");
        assertTrue(taskManager.getHistory().contains(taskTest2),
                "История содержит текущую версию задачи");

    }

    @Test
    void shouldGetHistory() {
        ArrayList<Task> taskArrayList = new ArrayList<>();
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        assertEquals(taskArrayList.getClass(), taskManager.getHistory().getClass(),
                "Проверка правильности возвращаемого класса");

        assertEquals(5, taskManager.getHistory().size(),
                "Проверка правильности размера истории");
        assertEquals(taskTest1, taskManager.getHistory().getLast(),
                "Проверка верности последнего значения");


        assertTrue(taskManager.getHistory().contains(taskTest1) &&
                        taskManager.getHistory().contains(taskTest2) &&
                        taskManager.getHistory().contains(taskTest3) &&
                        taskManager.getHistory().contains(epicTest1) &&
                        taskManager.getHistory().contains(subTaskTest1),
                "Содержит все вызванные задачи");
    }

    @Test
    void shouldAddToHistory() {
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history,
                "История не пустая.");
        assertEquals(5, history.size(),
                "История не пустая.");
        assertTrue(taskManager.getHistory().contains(taskTest1), "История содержит добавленную задачу");
        assertTrue(taskManager.getHistory().contains(taskTest2), "История содержит добавленную задачу");
        assertTrue(taskManager.getHistory().contains(taskTest3), "История содержит добавленную задачу");
    }

    @Test
    void shouldLinkLast() {
        assertTrue(taskManager.getHistory().contains(subTaskTest1),
                "Проверка на наличие последней задачи");
        assertEquals(taskManager.getHistoryManager().getLast(), subTaskTest1,
                "Проверка записи как последнего значения");
        assertEquals(taskManager.getHistoryManager().getLast().getId(), subTaskTest1Id);
        // !! проверка на отсутствие в истории такого же значения
    }

    @Test
    void shouldRemoveTaskFromHistory() {
        taskManager.getHistoryManager().remove(subTaskTest1Id);
        List<Task> tasksHistory = taskManager.getHistory();
        assertFalse(tasksHistory.contains(subTaskTest1));
        assertEquals(tasksHistory.get(3), taskTest1, "Соседний справа элемент истории занял " +
                "откорректированное положение");
        assertEquals(tasksHistory.get(0), epicTest1, "Соседний слева элемент истории остался в том же положении");
    }

}

