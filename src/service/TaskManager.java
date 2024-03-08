package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getTaskList();

    List<Epic> getEpicList();

    List<SubTask> getSubtaskList();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTaskByID(int id);

    Epic getEpicByID(int id);

    SubTask getSubtaskByID(int id);

    int createTask(Task task);

    int createEpic(Epic epic);

    int createSubtask(SubTask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(SubTask subTask);

    void deleteTaskByID(int id);

    void deleteEpicByID(int epicId);

    void deleteSubtaskByID(int subtaskId);

    ArrayList<SubTask> getEpicSubtasks(int epicId);

    List<Task> getHistory();


}
