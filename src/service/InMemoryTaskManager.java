package service;

import model.Epic;
import model.Progress;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HistoryManager historyManager;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    private int counter = 0;

    private int getId() {
        return ++counter;
    }

    @Override
    public List<Task> getTaskList() { // геттер списка обычных задач
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicList() { // геттер списка Эпиков
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubtaskList() { // геттер списка подзадач
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public Task getTaskByID(int id) { // получение Task задачи по id
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicByID(int id) { // получение Epic задачи по id
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public ArrayList<SubTask> getEpicSubtasks(int epicId) {
        if (epics.containsKey(epicId)) {

            Epic epic = epics.get(epicId);
            ArrayList<Integer> epicSubtasksIds = epic.getAllEpicSubtasksIds();
            ArrayList<SubTask> epicSubtasks = new ArrayList<>();
            for (int subtasksIDs : epicSubtasksIds) {
                epicSubtasks.add(subTasks.get(subtasksIDs));
            }

            return epicSubtasks;
        }
        return null;
    }

    @Override
    public SubTask getSubtaskByID(int id) { //получение подзадачи по id
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }


    @Override
    public int createTask(Task task) { // Создание Task'а.
        int newId = getId();
        task.setId(newId);
        tasks.put(newId, task); // Добавление в общую мап'у задач менеджера
        return task.getId();
    }

    @Override
    public int createEpic(Epic epic) { // Создание Epic'а.
        int newId = getId();
        epic.setId(newId);
        epics.put(newId, epic); // Добавление в общую мап'у Эпиков менеджера
        return epic.getId();
    }

    @Override
    public int createSubtask(SubTask subtask) { // Создание subtask'а.
        int epicId = subtask.getEpicId();
        if (epics.containsKey(epicId)) { // Проверка на наличие id эпика в общем списке
            int newId = getId();
            subtask.setId(newId); // назначение id подзадаче
            subTasks.put(newId, subtask); // Добавление в общую мап'у подзадач менеджера
            epics.get(epicId).addSubtaskIdToEpic(newId); // Добавление подзадачи в Эпик
            changeEpicProgress(epicId); // Изменение статуса Эпика
        }
        return subtask.getId();
    }

    @Override
    public void updateTask(Task task) { // Обновление обычной задачи
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) { // обновление Эпика
        if (epics.containsKey(epic.getId())) {
            epics.get(epic.getId()).setName(epic.getName()); // изменение имени Эпика
            epics.get(epic.getId()).setDescription(epic.getDescription()); // изменение описания Эпика
        }
    }

    @Override
    public void updateSubtask(SubTask subTask) { // Обновление подзадачи
        if (subTasks.containsKey(subTask.getId())) {
            SubTask subTaskFromMap = subTasks.get(subTask.getId());
            if (subTaskFromMap.getEpicId() == subTask.getEpicId()) {
                subTasks.put(subTask.getId(), subTask);
                changeEpicProgress(subTask.getEpicId());
            }
        }
    }


    @Override
    public void deleteAllTasks() {  //удаление всех обычных задач
        deleteTasksFromHistory(tasks);
        tasks.clear(); // удаление самих задач;
    }

    @Override
    public void deleteAllEpics() {  //удаление всех Эпиков
        deleteTasksFromHistory(subTasks);
        deleteTasksFromHistory(epics);
        subTasks.clear(); //удаление всех подзадач
        epics.clear(); //удаление всех эпиков;
    }

    @Override
    public void deleteAllSubtasks() {  //удаление всех Подзадач
        deleteTasksFromHistory(subTasks);
        subTasks.clear();
        for (Epic epic : epics.values()) { // удаление
            epic.removeAllSubtaskFromEpic();
        }
    }

    @Override
    public void deleteTaskByID(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void deleteEpicByID(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            for (int subtaskId : epic.getAllEpicSubtasksIds()) { // сначала удаление всех подзадач.
                historyManager.remove(subtaskId);
                subTasks.remove(subtaskId); // удаление подзадачи в общем списке
            }
            historyManager.remove(epicId);
            epics.remove(epicId);
        }
    }

    @Override
    public void deleteSubtaskByID(int subtaskId) {
        if (subTasks.containsKey(subtaskId)) {
            int epicId = subTasks.get(subtaskId).getEpicId();
            Epic epic = epics.get(epicId);

            epic.removeSubtaskFromEpic(subtaskId);

            historyManager.remove(subtaskId);
            subTasks.get(subtaskId).deleteEpicId();
            subTasks.remove(subtaskId);
            changeEpicProgress(epicId);
        }
    }

    private void changeEpicProgress(int epicId) {

        boolean isAllNewSubtasks = true;
        boolean isAllDoneSubtasks = true;

        Epic epic = epics.get(epicId);

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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    <T> void deleteTasksFromHistory(HashMap<Integer, T> map) {
        ArrayList<Integer> historyRemoveList = new ArrayList<>(map.keySet());
        for (int id : historyRemoveList) {
            historyManager.remove(id);
        }
    }


}
