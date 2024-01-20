package service;

import model.Epic;
import model.Progress;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {

    private HashMap<Integer, Task> taskList;
    private HashMap<Integer, Epic> epicList;
    private HashMap<Integer, SubTask> subTaskList;

    public static int counter;

    public TaskManager() {
        taskList = new HashMap<>();
        epicList = new HashMap<>();
        subTaskList = new HashMap<>();
    }

    public static int setNewID() {
        counter++;
        return counter;
    }

    public HashMap<Integer, Object> getAllTasks() { // получение перечня всех видов имеющихся задач
        HashMap<Integer, Object> allTasksList = new HashMap<>();

        System.out.println("Вывод всех задач.");
        System.out.println("Перечень обычных задач:");
        for (Integer key : taskList.keySet()) {
            System.out.println("id: " + key + ". Задача: " + taskList.get(key).getName());
            System.out.println("Описание: " + taskList.get(key).getDescription() + ". Прогресс: " + taskList.get(key).getProgress());
            allTasksList.put(key, taskList.get(key));
        }

        System.out.println("Перечень задач эпиков:");
        for (Integer key : epicList.keySet()) {
            System.out.println("id: " + key + ". " + epicList.get(key).getName());
            System.out.println("Описание: " + epicList.get(key).getDescription() + ". Прогресс: " + epicList.get(key).getProgress());
            allTasksList.put(key, epicList.get(key));
            System.out.println("Подзадачи эпика " + epicList.get(key).getName() + ":");
            for (SubTask subTask : epicList.get(key).getAllEpicSubtasks()) {
                System.out.println("id: " + subTask.getId() + ". Подзадача: " + subTask.getName());
                System.out.println("Описание: " + subTask.getDescription() + ". Прогресс: " + subTask.getProgress());
                allTasksList.put(key, subTask);
            }
        }

        return allTasksList;
    }

    public HashMap<Integer, Task> getTaskList () { // геттер списка обычных задач
        return taskList;
    }

    public HashMap<Integer, Epic> getEpicList () { // геттер списка Эпиков
        return epicList;
    }

    public HashMap<Integer, SubTask> getSubtaskList () { // геттер списка подзадач
        return subTaskList;
    }


    public void deleteAllTasks() {  //удаление всех задач
        taskList.clear();
        subTaskList.clear();
        epicList.clear();
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

    public void createTask(Task task) {
        this.taskList.put(task.getId(), task); // Создание Task'а.
    }

    public void createTask(Epic epic) { // Создание Epic'а.
        this.epicList.put(epic.getId(), epic); // Добавление в общий список менеджера
    }

    public void createTask(SubTask subtask, Epic epic) { // Создание subtask'а.
        subtask.setEpicAffilation(epic);
        this.subTaskList.put(subtask.getId(), subtask); // Добавление в общий список менеджера
        epic.addSubtask(subtask); // Добавление подзадачи в список эпик'а;

        checkOrChangeEpicProgress(epic);

    }

    public void updateTask(int id, Task task, Progress progress) { // Обновление обычной задачи
        task.setProgress(progress);
        taskList.put(id, task); // Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
        System.out.println("Задача обновлена!");
    }

    public void updateEpic(int id, Epic epic) { // обновление Эпика
        ArrayList<SubTask> subTasks = epicList.get(id).getAllEpicSubtasks(); // сохранение списка задач при обновлении
        epic.setSubtasksList(subTasks);
        epicList.put(id, epic);

        System.out.println("Epic обновлен!");
    }

    public void updateSubtask(int id, SubTask subTask, Progress progress, Epic epic) { // Обновление подзадачи
        subTask.setProgress(progress);
        subTaskList.put(id, subTask);
        epic.addSubtask(subTask);

        checkOrChangeEpicProgress(epic);
        System.out.println("Подзадача обновлена!");
    }

    public void deleteTaskByID(int id) {
        for (int key : taskList.keySet()) {
            if (key == id) {
                taskList.remove(id);
                System.out.println("Задача удалена");
                return;
            }
        }
        System.out.println("Задача не найдена");
    }
    public void deleteEpicByID(int id) {
            for (int key : epicList.keySet()) {
                if (key == id) {
                    for (SubTask subTask : epicList.get(id).getAllEpicSubtasks()) { // сначала удаление всех подзадач.
                        int idSubtask = subTask.getId();
                        subTaskList.remove(idSubtask); // удаление подзадачи в общем списке
                    }
                    epicList.remove(id);
                    System.out.println("Epic и его подзадачи удалены");
                    return;
                } else {
                    System.out.println("Эпик не найден");
                }
            }
        }
        public void deleteSubtaskByID(int id) {
              for (int key : subTaskList.keySet()) {
                if (key == id) {
                    Epic subTL = subTaskList.get(id).getEpicAffilation();
                    subTL.getAllEpicSubtasks().remove(subTaskList.get(id));
                    subTaskList.remove(id);

                    System.out.println("Подзадача удалена");
                    return;
                } else {
                    System.out.println("Подзадача не найдена");
                }
            }
        }


    public ArrayList<SubTask> getEpicSubtasks(Epic epic) {
        return epic.getAllEpicSubtasks();
    }

    private void checkOrChangeEpicProgress(Epic epic) {
        boolean checkNewSubtasks = true;
        boolean checkDoneSubtasks = true;
        for (SubTask subT : getEpicSubtasks(epic)) {
            if (subT.getProgress() != Progress.NEW) {
                checkDoneSubtasks = false;
            }
        }
        for (SubTask subT : getEpicSubtasks(epic)) {
            if (subT.getProgress() != Progress.DONE) {
                checkDoneSubtasks = false;
            }
        }
        if (checkNewSubtasks) {
            epic.setProgress(Progress.NEW);
        } else if (checkDoneSubtasks) {
            epic.setProgress(Progress.DONE);
        }
    }

    /*
    public Epic getEpicByName(String name) {
        for (Epic epic : epicList.values()) {
            if (epic.getName().equals(name)) {
                return epic;
            }
        }
        return null;
    }
    */

}
