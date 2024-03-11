package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {

    File autosaveTestFile = null;
    FileBackedTaskManager taskManager = null;

    @BeforeEach
    void beforeEach() {
        try {
            autosaveTestFile = File.createTempFile("testFile", null);
        } catch (IOException e) {
            System.out.println("Ошибка при создании временного файла");
        }
        taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), autosaveTestFile);
        taskManager.createTask(new Task("Тестовый таск №1", "Описание тестового таска №1"));
        taskManager.createTask(new Task("Тестовый таск №2", "Описание тестового таска №2"));
        taskManager.createEpic(new Epic("Тестовый эпик №3", "Описание тестового эпика №3"));
        taskManager.createSubtask(new SubTask("Тестовая подзадача №4",
                "Описание тестовой подзадачи №4", 3));
        taskManager.createEpic(new Epic("Тестовый эпик №5", "Описание тестового эпика №5"));
        taskManager.createSubtask(new SubTask("Тестовая подзадача №6",
                "Описание тестовой подзадачи №6", 5));

        Task taskForUpdate = new Task("Тестовый таск №2 (обновленный)",
                "Описание тестового таска №2 (обновленное)");
        taskForUpdate.setId(2);
        taskManager.updateTask(taskForUpdate);

        taskManager.createSubtask(new SubTask("Тестовая подзадача №6",
                "Описание тестовой подзадачи №6", 5));
        taskManager.createEpic(new Epic("Тестовый эпик №7", "Описание тестового эпика №7"));
        taskManager.createSubtask(new SubTask("Тестовая подзадача №8",
                "Описание тестовой подзадачи №8", 7));


        taskManager.getEpicByID(3);
        taskManager.getTaskByID(2);
        taskManager.getSubtaskByID(6);

    }


    @Test
    void shouldConvertToString() {
        assertEquals("1,TASK,Тестовый таск №1,NEW,Описание тестового таска №1,null",
                taskManager.toString(taskManager.getTaskByID(1)), "Проверка приведения Таска к строке");
    }

    @Test
    void shouldConvertfromString() {
        assertEquals(taskManager.getTaskByID(1), FileBackedTaskManager.fromString
                ("1,TASK,Тестовый таск №1,NEW,Описание тестового таска №1,null)"));
    }


    @Test
    void shouldCreateAndLoadEmptyFile() {
        FileBackedTaskManager taskManager2;
        File emptyFile;
        try {
            emptyFile = File.createTempFile("TestFile2", null);
            taskManager2 = new FileBackedTaskManager(Managers.getDefaultHistory(), emptyFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileBackedTaskManager taskManager3 = FileBackedTaskManager.loadFromFile(emptyFile);

        assertTrue(taskManager3.getTaskList().isEmpty());
        assertTrue(taskManager3.getEpicList().isEmpty());
        assertTrue(taskManager3.getSubtaskList().isEmpty());
        assertTrue(taskManager3.getHistory().isEmpty());

    }

    @Test
    void shouldLoadNotEmptyFile() {
        FileBackedTaskManager taskManager4;
        File file;
        file = autosaveTestFile;
        taskManager4 = FileBackedTaskManager.loadFromFile(file);
        assertEquals(taskManager.getTaskList(), taskManager4.getTaskList());
        assertEquals(taskManager.getEpicList(), taskManager4.getEpicList());
        assertEquals(taskManager.getSubtaskList(), taskManager4.getSubtaskList());
        assertEquals(taskManager.getHistory(), taskManager4.getHistory());

    }

}
