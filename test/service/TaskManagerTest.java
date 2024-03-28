package service;


import exceptions.ValidationException;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    public T taskManager;

    @BeforeEach
    void setBeforeEach() {
        //создание задач
        taskManager.createTask(new Task( /* №1 */
                "Тестовый таск №1",
                "Описание тестового таска №1",
                "2024.04.01 20:00",
                60)); //1
        taskManager.createTask(new Task( /* №2 */
                "Тестовый таск №2",
                "Описание тестового таска №2",
                "2024.04.03 22:00",
                120)); //2
        taskManager.createEpic(new Epic( /* №3 */
                "Тестовый эпик №3",
                "Описание тестового эпика №3"));
        taskManager.createSubtask(new SubTask( /* №4 */
                "Тестовая подзадача №4",
                "Описание тестовой подзадачи №4",
                3,
                "2024.04.06 21:30",
                150)); //4
        taskManager.createEpic(new Epic( /* №5 */
                "Тестовый эпик №5",
                "Описание тестового эпика №5"));
        taskManager.createSubtask(new SubTask( /* №6 */
                "Тестовая подзадача №6",
                "Описание тестовой подзадачи №6",
                5,
                "2024.03.29 08:30",
                15));
        taskManager.createEpic(new Epic( /* №7 */
                "Тестовый эпик №7",
                "Описание тестового эпика №7"));
        taskManager.createSubtask(new SubTask( /* №8 */
                "Тестовая подзадача №8",
                "Описание тестовой подзадачи №8",
                7));
        taskManager.createSubtask(new SubTask( /* №9 */
                "Тестовая подзадача №9",
                "Описание тестовой подзадачи №9",
                7,
                "2024.07.03 19:00",
                200));
        taskManager.createSubtask(new SubTask( /* №10 */
                "Тестовая подзадача №10",
                "Описание тестовой подзадачи №10",
                7,
                "2024.09.03 20:00",
                60));

        //обновление задач
        Task taskForUpdate = new Task("Тестовый таск №2 (обновленный)",
                "Описание тестового таска №2 (обновленное)", "2024.04.02 21:00", 120);
        taskForUpdate.setId(2);
        taskManager.updateTask(taskForUpdate);

        //обращение к задачам для записи в историю
        taskManager.getEpicByID(3);
        taskManager.getTaskByID(2);
        taskManager.getSubtaskByID(6);

    }

    @Test
    void shouldGetLists() {
        Assertions.assertTrue(taskManager.getTaskList().contains(taskManager.getTaskByID(1)),
                "Задача получена");
        Assertions.assertTrue(taskManager.getEpicList().contains(taskManager.getEpicByID(3)),
                "Эпик получен");
        Assertions.assertTrue(taskManager.getSubtaskList().contains(taskManager.getSubtaskByID(4)),
                "Подзадача получена");
    }

    @Test
    void shouldDeleteAllTasks() {
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getTaskList().isEmpty());
        for (Task task : taskManager.getHistory()) {
            assertNotEquals(task.getType(), TaskType.TASK);
        }
        for (Task task : taskManager.getPrioritizedTasks()) {
            assertNotEquals(task.getType(), TaskType.TASK);
        }
    }

    @Test
    void shouldDeleteAllEpics() {
        taskManager.deleteAllEpics();
        assertTrue(taskManager.getSubtaskList().isEmpty());
        assertTrue(taskManager.getEpicList().isEmpty());
        for (Task task : taskManager.getHistory()) {
            assertNotEquals(task.getType(), TaskType.SUBTASK);
            assertNotEquals(task.getType(), TaskType.EPIC);
        }
    }

    @Test
    void shouldDeleteAllSubtasks() {
        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getSubtaskList().isEmpty());
        for (Task task : taskManager.getHistory()) {
            assertNotEquals(task.getType(), TaskType.SUBTASK);
        }
    }

    @Test
    void shouldGetTaskByID() {
        Task task = new Task(
                "Тестовый таск №1",
                "Описание тестового таска №1",
                "2024.04.01 20:00",
                60);
        task.setId(1);
        assertEquals(task, taskManager.getTaskByID(1));
        assertTrue(taskManager.getHistory().contains(taskManager.getTaskByID(1)));
    }

    @Test
    void shouldGetEpicByID() {

        Epic epic = new Epic(
                "Тестовый эпик №3",
                "Описание тестового эпика №3");
        epic.setId(3);
        epic.addSubtaskIdToEpic(4);
        assertEquals(epic.getName(), taskManager.getEpicByID(3).getName());
        assertEquals(epic.getDescription(), taskManager.getEpicByID(3).getDescription());
        assertTrue(taskManager.getHistory().contains(taskManager.getEpicByID(3)));
    }

    @Test
    void shouldGetSubtaskByID() {
        SubTask subtask = new SubTask(
                "Тестовая подзадача №4",
                "Описание тестовой подзадачи №4",
                3,
                "2024.04.06 21:30",
                150);
        subtask.setId(4);
        assertEquals(subtask, taskManager.getSubtaskByID(4));
        assertTrue(taskManager.getHistory().contains(taskManager.getSubtaskByID(4)));
    }

    @Test
    void shouldGetEpicStartTime() {
        assertEquals("2024.07.03 19:00", taskManager.getEpicByID(7).getStartTime()
                .format(taskManager.getEpicByID(7).getDateTimeFormat()));
    }

    @Test
    void shouldGetEpicDuration() {
        assertEquals(260, taskManager.getEpicByID(7).getDuration().toMinutes());
    }

    @Test
    void shouldGetEpicEndTime() {
        assertEquals("2024.07.03 23:20", taskManager.getEpicByID(7).getEndTime()
                .format(taskManager.getEpicByID(7).getDateTimeFormat()));
    }

    @Test
    void shouldGetPriorityList() {
        boolean isPriorityRight = true;
        List<Task> priporityList = taskManager.getPrioritizedTasks();
        for (int i = 1; i < priporityList.size(); i++) {
            if (priporityList.get(i - 1).getStartTime().isAfter(priporityList.get(i).getStartTime())) {
                isPriorityRight = false;
                break;
            }
        }
        assertTrue(isPriorityRight);
    }

    @Test
    void shouldChangeEpicStatus() {
        assertEquals(Progress.NEW, taskManager.getEpicByID(7).getProgress());

        SubTask subTask1 = taskManager.getSubtaskByID(8);
        subTask1.setProgress(Progress.IN_PROGRESS);
        taskManager.updateSubtask(subTask1);
        assertEquals(Progress.IN_PROGRESS, taskManager.getEpicByID(7).getProgress());

        SubTask subTask2 = taskManager.getSubtaskByID(9);
        subTask1.setProgress(Progress.NEW);
        taskManager.updateSubtask(subTask1);
        subTask2.setProgress(Progress.DONE);
        taskManager.updateSubtask(subTask2);
        assertEquals(Progress.IN_PROGRESS, taskManager.getEpicByID(7).getProgress());

        SubTask subTask3 = taskManager.getSubtaskByID(10);
        subTask1.setProgress(Progress.DONE);
        subTask2.setProgress(Progress.DONE);
        subTask3.setProgress(Progress.DONE);
        taskManager.updateSubtask(subTask1);
        taskManager.updateSubtask(subTask2);
        taskManager.updateSubtask(subTask3);
        assertEquals(Progress.DONE, taskManager.getEpicByID(7).getProgress());
    }

    @Test
    void shouldThrowValidationException() {
        Task task = new Task(
                "TaskIntersectionTask",
                "TaskIntersectionTask description",
                "2024.03.31 23:00",
                1440);
        Assertions.assertThrows(ValidationException.class, () -> taskManager.createTask(task));
    }


}
