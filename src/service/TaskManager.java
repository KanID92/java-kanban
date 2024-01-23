package service;

import model.Epic;
import model.Progress;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TaskManager {

    private HashMap<Integer, Task> taskList = new HashMap<>();
    private HashMap<Integer, Epic> epicList  = new HashMap<>();
    private HashMap<Integer, SubTask> subTaskList  = new HashMap<>();

    private int counter;

    private int getId() {
        counter++;
        return counter;
    }

    public List<Task> getTaskList () { // геттер списка обычных задач
        return new ArrayList<>(taskList.values());
    }

    public ArrayList<Epic> getEpicList () { // геттер списка Эпиков
        return new ArrayList<>(epicList.values());
    }

    public ArrayList<SubTask> getSubtaskList () { // геттер списка подзадач
        return new ArrayList<>(subTaskList.values());
    }


    public void deleteAllTasks() {  //удаление всех обычных задач
        taskList.clear();
    }

    public void deleteAllEpics() {  //удаление всех Эпиков
        subTaskList.clear(); //удаление всех подзадач
        epicList.clear();
    }

    public void deleteAllSubtasks() {  //удаление всех Подзадач
        subTaskList.clear();
        for (Epic epic : epicList.values()) { // удаление
            epic.removeAllSubtaskFromEpic();
        }
    }

    public Task getTaskByID(int id) { // получение Task задачи по id
        return taskList.get(id);
    }

    public Epic getEpicByID(int id) { // получение Epic задачи по id
        return epicList.get(id);
    }

    public SubTask getSubtaskByID(int id) { //получение Epic задачи по id
        return subTaskList.get(id);
    }

    public int createTask(Task task) { // Создание Task'а.
        int newId = getId();
        task.setId(newId);
        taskList.put(newId, task); // Добавление в общую мап'у задач менеджера
        return task.getId();
    }

    public int createEpic(Epic epic) { // Создание Epic'а.
        int newId = getId();
        epic.setId(newId);
        epicList.put(newId, epic); // Добавление в общую мап'у Эпиков менеджера
        return epic.getId();
    }

    public int createSubtask(SubTask subtask) { // Создание subtask'а.
        int epicId = subtask.getEpicId();
        if (epicList.containsKey(epicId)) { // Проверка на наличие id эпика в общем списке
            int newId = getId();
            subtask.setId(newId); // назначение id подзадаче
            subTaskList.put(newId, subtask); // Добавление в общую мап'у подзадач менеджера
            epicList.get(epicId).addSubtaskIdToEpic(newId); // Добавление подзадачи в Эпик
            changeEpicProgress(epicId); // Изменение статуса Эпика
        }
        return subtask.getId();
    }

    public void updateTask(Task task) { // Обновление обычной задачи
        if (taskList.containsKey(task.getId())) {
            taskList.put(task.getId(), task);
            System.out.println("Задача обновлена!");
        }
    }

    public void updateEpic(Epic epic) { // обновление Эпика
        if (epicList.containsKey(epic.getId())) {
            epicList.get(epic.getId()).setName(epic.getName()); // изменение имени Эпика
            epicList.get(epic.getId()).setDescription(epic.getDescription()); // изменение описания Эпика
            System.out.println("Эпик обновлен!");
        }
    }

    public void updateSubtask(SubTask subTask) { // Обновление подзадачи
        if (subTaskList.containsKey(subTask.getId())) {
            SubTask subTaskFromMap = subTaskList.get(subTask.getId());
            if (subTaskFromMap.getEpicId() == subTask.getEpicId()) {
                subTaskList.put(subTask.getId(), subTask);
                changeEpicProgress(subTask.getEpicId());
                System.out.println("Подзадача обновлена!");
            }
        }
    }

    public void deleteTaskByID(int id) {
        taskList.remove(id);
    }

    public void deleteEpicByID(int epicId) {
        if (epicList.containsKey(epicId)) {
            Epic epic = epicList.get(epicId);
            for (int subtaskId : epic.getAllEpicSubtasksIds()) { // сначала удаление всех подзадач.
                subTaskList.remove(subtaskId); // удаление подзадачи в общем списке
            }
            epicList.remove(epicId);
        }
    }

    public void deleteSubtaskByID(int subtaskId) {
        if (subTaskList.containsKey(subtaskId)) {
            int epicId = subTaskList.get(subtaskId).getEpicId();
            Epic epic = epicList.get(epicId);

            epic.removeSubtaskFromEpic(subtaskId);

            subTaskList.remove(subtaskId);
            changeEpicProgress(epicId);
        }
    }


    public ArrayList<SubTask> getEpicSubtasks(int epicId) {
        Epic epic = epicList.get(epicId);
        ArrayList<Integer> epicSubtasksIds = epic.getAllEpicSubtasksIds();
        ArrayList<SubTask> epicSubtasks = new ArrayList<>();
        for (int subtasksIDs : epicSubtasksIds) {
            epicSubtasks.add(subTaskList.get(subtasksIDs));
        }

        return epicSubtasks;
    }

    private void changeEpicProgress(int epicId) {

        boolean isAllNewSubtasks = true;
        boolean isAllDoneSubtasks = true;


        Epic epic = epicList.get(epicId);

        if (getEpicSubtasks(epicId).isEmpty()) {
            epic.setProgress(Progress.NEW);
            return;
        }

        for (SubTask subTask : getEpicSubtasks(epicId)) {

            if (subTask.getProgress() != Progress.NEW) {
                isAllNewSubtasks = false;
            }

            if (subTask.getProgress() != Progress.DONE) {
                isAllDoneSubtasks = false;
            }
        }

        if (isAllNewSubtasks) {
            epic.setProgress(Progress.NEW);
        } else if (isAllDoneSubtasks) {
            epic.setProgress(Progress.DONE);
        } else {
            epic.setProgress(Progress.IN_PROGRESS);
        }
    }

}
