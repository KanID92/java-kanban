package service;

import exceptions.ValidationException;
import model.Epic;
import model.Progress;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HistoryManager historyManager;
    protected TreeSet<Task> prioritySet = new TreeSet<>(new PriorityComparator());

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    protected int counter = 0;

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
            epicSubtasksIds.forEach((id) -> epicSubtasks.add(subTasks.get(id)));
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
        if (isTasksIntersect(task)) {
            System.out.println(getPrioritizedTasks());
            throw new ValidationException("Пересечение времени выполнения при добавлении задачи " + task.getName() + " " + task.getStartTime() + " " + task.getEndTime() + " "
                    + "с задачей из списка приоритета");
        } else {
            int newId = getId();
            task.setId(newId);
            tasks.put(newId, task); // Добавление в общую мап'у задач менеджера
            prioritySet.add(task); //Добавление в множество задач по приоритетам
            return task.getId();
        }
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
        if (isTasksIntersect(subtask)) {
            throw new ValidationException("Пересечение времени выполнения задачи");
        } else {
            int epicId = subtask.getEpicId();
            if (epics.containsKey(epicId)) { // Проверка на наличие id эпика в общем списке
                int newId = getId();
                subtask.setId(newId); // назначение id подзадаче
                subTasks.put(newId, subtask); // Добавление в общую мап'у подзадач менеджера
                epics.get(epicId).addSubtaskIdToEpic(newId); // Добавление подзадачи в Эпик
                changeEpicProgress(epicId); // Изменение статуса Эпика
                changeEpicTimeAndDuration(epicId);
                prioritySet.add(subtask); //Добавление в множество задач по приоритетам
            }
            return subtask.getId();
        }
    }

    @Override
    public void updateTask(Task task) { // Обновление обычной задачи

        if (tasks.containsKey(task.getId())) {
            if (!isTasksIntersect(task)) {
                tasks.put(task.getId(), task);
            } else {
                throw new ValidationException("Ошибка пересечения времени выполнения при обновлении задачи");
            }
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
            if (!isTasksIntersect(subTask)) {
                SubTask subTaskFromMap = subTasks.get(subTask.getId());
                if (subTaskFromMap.getEpicId() == subTask.getEpicId()) {
                    subTasks.put(subTask.getId(), subTask);
                    changeEpicProgress(subTask.getEpicId());
                    changeEpicTimeAndDuration(subTask.getEpicId());
                }
            }
        }
    }


    @Override
    public void deleteAllTasks() {  //удаление всех обычных задач
        deleteTasksFromHistory(tasks);
        tasks.clear(); // удаление задач в мапе;
        prioritySet.clear(); // удаление задач в множестве приоритета;
    }

    @Override
    public void deleteAllEpics() {  //удаление всех Эпиков
        prioritySet.clear(); // удаление задач в множестве приоритета;
        deleteTasksFromHistory(subTasks);
        deleteTasksFromHistory(epics);
        subTasks.clear(); //удаление всех подзадач
        epics.clear(); //удаление всех эпиков;
    }

    @Override
    public void deleteAllSubtasks() {  //удаление всех Подзадач
        deleteTasksFromHistory(subTasks);
        prioritySet.clear(); // удаление подзадач в множестве приоритета;
        subTasks.clear();
        for (Epic epic : epics.values()) { // удаление
            epic.removeAllSubtaskFromEpic();
            changeEpicTimeAndDuration(epic.getId());
        }

    }

    @Override
    public void deleteTaskByID(int id) {
        historyManager.remove(id);
        prioritySet.remove(getTaskByID(id));
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
            SubTask subtask = subTasks.get(subtaskId);
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);

            epic.removeSubtaskFromEpic(subtaskId);

            historyManager.remove(subtaskId);
            prioritySet.remove(subtask);
            subTasks.get(subtaskId).deleteEpicId();
            subTasks.remove(subtaskId);
            changeEpicProgress(epicId);
            changeEpicTimeAndDuration(epicId);

        }
    }

    protected void changeEpicProgress(int epicId) {

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

    protected void changeEpicTimeAndDuration(int epicId) {
        Epic epic = epics.get(epicId);
        LocalDateTime epicStartTime = epic.getStartTime();
        Duration epicDuration = Duration.ZERO;
        ArrayList<SubTask> epicSubtasks = getEpicSubtasks(epicId);
        if (epicSubtasks != null) {
            for (SubTask subTask : epicSubtasks) {
                if (subTask.getStartTime() != null) {
                    if (subTask.getStartTime().isBefore(epicStartTime)) {
                        epicStartTime = subTask.getStartTime();
                    }
                }
                epicDuration = epicDuration.plus(subTask.getDuration());
            }
            epic.setStartTime(epicStartTime);
            epic.setDuration(epicDuration);
            epic.setEndTime(epicStartTime.plus(epicDuration));

        } else {
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    <T> void deleteTasksFromHistory(HashMap<Integer, T> map) {
        ArrayList<Integer> historyRemoveList = new ArrayList<>(map.keySet());
        historyRemoveList.forEach(historyManager::remove);
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritySet);
    }

    protected boolean isTasksIntersect(Task newTask) {
        return prioritySet.stream()
                .anyMatch(exTask -> ((newTask.getStartTime().isBefore(exTask.getEndTime())
                        && exTask.getEndTime().isBefore(newTask.getEndTime()))
                        || (newTask.getEndTime().isAfter(exTask.getStartTime())
                        && newTask.getEndTime().isBefore(exTask.getEndTime()))));
    }

    static class PriorityComparator implements Comparator<Task> {
        @Override
        public int compare(Task t1, Task t2) {
            if (t1.getStartTime().isBefore(t2.getStartTime())) {
                return -1;
            } else if (t1.getStartTime().isAfter(t2.getStartTime())) {
                return 1;
            } else {
                return 0;

            }
        }
    }

}

