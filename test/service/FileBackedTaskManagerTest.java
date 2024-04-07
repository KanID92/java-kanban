package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File autosaveTestFile = null;

    @Override
    @BeforeEach
    void setBeforeEach() {
        try {
            autosaveTestFile = File.createTempFile("testFile", null);
        } catch (IOException e) {
            System.out.println("Ошибка при создании временного файла");
        }
        taskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), autosaveTestFile);
        super.setBeforeEach();
    }

    @Test
    void shouldConvertToString() {
        assertEquals("1,TASK,Тестовый таск №1,NEW,Описание тестового таска №1,null," +
                        "2024-04-01T20:00:00,60,2024-04-01T21:00:00",
                taskManager.toString(taskManager.getTaskByID(1)), "Проверка приведения Таска к строке");
    }

    @Test
    void shouldConvertFromString() {
        assertEquals(taskManager.getTaskByID(1), FileBackedTaskManager.fromString
                ("1,TASK,Тестовый таск №1,NEW,Описание тестового таска №1,null," +
                        "2024-04-01T20:00:00,60,2024-04-01T20:30:00"));
    }

    @Test
    void shouldCreateAndLoadEmptyFile() {
        File emptyFile;
        try {
            emptyFile = File.createTempFile("TestFile2", null);
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
        FileBackedTaskManager taskManager4 = FileBackedTaskManager.loadFromFile(autosaveTestFile);
        assertEquals(taskManager.getTaskList(), taskManager4.getTaskList());
        assertEquals(taskManager.getEpicList(), taskManager4.getEpicList());
        assertEquals(taskManager.getSubtaskList(), taskManager4.getSubtaskList());
        assertEquals(taskManager.getHistory(), taskManager4.getHistory());
    }

}
