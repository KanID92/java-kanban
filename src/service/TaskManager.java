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

    private int setId() {
        counter++;
        return counter;
    }

    public List<Task> getTaskList () { // геттер списка обычных задач
        List<Task> tList = new ArrayList<>();
        tList.addAll(taskList.values());
        return tList;
    }

    public ArrayList<Epic> getEpicList () { // геттер списка Эпиков
        ArrayList<Epic> eList = new ArrayList<>();
        eList.addAll(epicList.values());
        return eList;
    }

    public ArrayList<SubTask> getSubtaskList () { // геттер списка подзадач
        ArrayList<SubTask> sList = new ArrayList<>();
        sList.addAll(subTaskList.values());
        return sList;
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

    public void createTask(Task task) { // Создание Task'а.
        int newId = setId();
        task.setId(newId);
        taskList.put(newId, task); // Добавление в общую мап'у задач менеджера
    }

    public void createEpic(Epic epic) { // Создание Epic'а.
        int newId = setId();
        epic.setId(newId);
        epicList.put(newId, epic); // Добавление в общую мап'у Эпиков менеджера
    }

    public void createSubtask(SubTask subtask) { // Создание subtask'а.
        int epicId = subtask.getEpicId();
        if (epicList.containsKey(epicId)) { // Проверка на наличие id эпика в общем списке
            int newId = setId();
            subtask.setId(newId); // назначение id подзадаче
            subTaskList.put(newId, subtask); // Добавление в общую мап'у подзадач менеджера
            epicList.get(epicId).addSubtaskIdToEpic(newId); // Добавление подзадачи в Эпик
            changeEpicProgress(epicId); // Изменение статуса Эпика
        }
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
        Epic epic = epicList.get(epicId);
        for (int subtaskId : epic.getAllEpicSubtasksIds()) { // сначала удаление всех подзадач.
            subTaskList.remove(subtaskId); // удаление подзадачи в общем списке
        }
        epicList.remove(epicId);
        }

    public void deleteSubtaskByID(int subtaskId) {
        int epicId = subTaskList.get(subtaskId).getEpicId();
        Epic epic = epicList.get(epicId);

        ArrayList<Integer> epicSubtasksIds = epic.getAllEpicSubtasksIds();
        Integer integer = subtaskId;

        epicSubtasksIds.remove(integer);

        subTaskList.remove(subtaskId);
        changeEpicProgress(epicId);
        }


    public ArrayList<Integer> getEpicSubtaskIDs(int epicId) {
        if (epicList.containsKey(epicId)) {
            return epicList.get(epicId).getAllEpicSubtasksIds();
        }
        return null;
    }

    private void changeEpicProgress(int epicId) {

        boolean isAllNewSubtasks = true;
        boolean isAllDoneSubtasks = true;


        Epic epic = epicList.get(epicId);

        if (getEpicSubtaskIDs(epicId).isEmpty()) {
            epic.setProgress(Progress.NEW);
            return;
        }

        for (int subtasksId : getEpicSubtaskIDs(epicId)) {
            if (subTaskList.get(subtasksId).getProgress() != Progress.NEW) {
                isAllNewSubtasks = false;
            }
        }

        for (int subtasksId : getEpicSubtaskIDs(epicId)) {
            if (subTaskList.get(subtasksId).getProgress() != Progress.DONE) {
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
