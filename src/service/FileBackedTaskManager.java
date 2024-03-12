package service;

import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File autoSaveFile;

    public FileBackedTaskManager(HistoryManager historyManager, File autoSaveFile) {
        super(historyManager);
        this.autoSaveFile = autoSaveFile;
    }

    @Override
    public Task getTaskByID(int id) { // получение Task задачи по id c сохранением в файл
        Task task = super.getTaskByID(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicByID(int id) { // получение Epic задачи по id c сохранением в файл
        Epic epic = super.getEpicByID(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubtaskByID(int id) { //получение подзадачи по id
        SubTask subTask = super.getSubtaskByID(id);
        save();
        return subTask;
    }

    @Override
    public int createTask(Task task) { // Создание Task'а.
        int taskId = super.createTask(task);
        save();
        return taskId;
    }

    @Override
    public int createEpic(Epic epic) { // Создание Epic'а.
        int epicId = super.createEpic(epic);
        save();
        return epicId;
    }

    @Override
    public int createSubtask(SubTask subtask) { // Создание subtask'а.
        int subtaskId = super.createSubtask(subtask);
        save();
        return subtaskId;
    }

    @Override
    public void updateTask(Task task) { // Обновление обычной задачи
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) { // обновление Эпика
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask subTask) { // Обновление подзадачи
        super.updateSubtask(subTask);
        save();
    }

    @Override
    public void deleteAllTasks() {  //удаление всех обычных задач
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {  //удаление всех Эпиков
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {  //удаление всех Подзадач
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteTaskByID(int id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteEpicByID(int epicId) {
        super.deleteEpicByID(epicId);
        save();
    }

    @Override
    public void deleteSubtaskByID(int subtaskId) {
        super.deleteSubtaskByID(subtaskId);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    public String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getProgress() + ","
                + task.getDescription() + "," + task.getEpicId();
    }

    static FileBackedTaskManager loadFromFile(File autoSaveFile) { // восстановление данных менеджера
        FileBackedTaskManager fileBTManager =
                new FileBackedTaskManager(Managers.getDefaultHistory(), autoSaveFile);

        int maxId = 0;
        String historyString = "";
        try (BufferedReader br = new BufferedReader(new FileReader(autoSaveFile, StandardCharsets.UTF_8))) {
            if (autoSaveFile.length() == 0 && autoSaveFile.exists()) {
                return fileBTManager;
            } else {
                while (br.ready()) {
                    String stringTask = br.readLine();
                    if (!stringTask.equals("История:")) { //проверка на начало хранения истории
                        Task task = fromString(stringTask);
                        switch (task.getType()) {
                            case TASK:
                                fileBTManager.tasks.put(task.getId(), task);
                                break;
                            case EPIC:
                                fileBTManager.epics.put(task.getId(), (Epic) task);
                                break;
                            case SUBTASK:
                                fileBTManager.subTasks.put(task.getId(), (SubTask) task);
                                break;
                        }
                        if (task.getId() > maxId) {
                            maxId = task.getId();
                        }
                    } else {
                        historyString = br.readLine();
                    }
                }
                fileBTManager.setCounter(maxId); // восстановление счетчика id

                for (SubTask subTask : fileBTManager.getSubtaskList()) { // восстановление данных об подзадачах в эпиках
                    int epicId = subTask.getEpicId();
                    fileBTManager.epics.get(epicId).addSubtaskIdToEpic(subTask.getId());
                }

                for (int historyTask : historyFromString(historyString)) { // Восстановление наполнения менеджера истории
                    if (fileBTManager.getTaskFromMap(historyTask) != null) {
                        fileBTManager.historyManager.add(fileBTManager.getTaskFromMap(historyTask));
                    } else if (fileBTManager.getEpicFromMap(historyTask) != null) {
                        fileBTManager.historyManager.add(fileBTManager.getEpicFromMap(historyTask));
                    } else {
                        fileBTManager.historyManager.add(fileBTManager.getSubtaskFromMap(historyTask));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при восстановлении Таскменеджера из файла", e);
        }
        return fileBTManager;
    }

    private Task getTaskFromMap(int id) { // получение Task задачи по id, без сохранения в историю
        return tasks.get(id);
    }

    private Epic getEpicFromMap(int id) { // получение Task задачи по id, без сохранения в историю
        return epics.get(id);
    }

    private SubTask getSubtaskFromMap(int id) { // получение Task задачи по id, без сохранения в историю
        return subTasks.get(id);
    }


    public static Task fromString(String value) {
        String[] splitTaskString = value.split(",");
        int id = Integer.parseInt(splitTaskString[0]);
        TaskType tasktype = TaskType.valueOf(splitTaskString[1]); //Нужно сделать в верхнем регистре???
        String name = splitTaskString[2];
        Progress progress = Progress.valueOf(splitTaskString[3]); //Нужно сделать в верхнем регистре???
        String description = splitTaskString[4];

        Task task = null;

        switch (tasktype) {
            case TASK:
                task = new Task(name, description);
                task.setId(id);
                task.setProgress(progress);
                break;
            case EPIC:
                task = new Epic(name, description);
                task.setId(id);
                task.setProgress(progress);
                break;
            case SUBTASK:
                task = new SubTask(name, description, Integer.parseInt(splitTaskString[5]));
                task.setId(id);
                task.setProgress(progress);
                break;
        }

        return task;
    }

    static String historyToString(HistoryManager manager) {
        List<Task> historyList = manager.getHistory();
        if (historyList != null) {
            String[] historyStrArray = new String[historyList.size()];
            int i = 0;
            for (Task task : historyList) {
                historyStrArray[i] = String.valueOf(task.getId());
                i++;
            }
            String historyStr = String.join(",", historyStrArray);

            return historyStr;
        } else {
            return null;
        }
    }

    static List<Integer> historyFromString(String value) {
        List<Integer> historyTaskIds = new ArrayList<>();
        String[] historyArray = value.split(",");
        for (int i = 0; i < historyArray.length; i++) {
            historyTaskIds.add(Integer.parseInt(historyArray[i]));
        }
        return historyTaskIds;
    }

    private void save() {
        try {
            BufferedWriter bWriter = new BufferedWriter(new FileWriter(autoSaveFile));
            if (!autoSaveFile.exists()) {
                Files.createFile(autoSaveFile.toPath());
                bWriter.write("id,type,name,status,description,epic");
                bWriter.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при создании файла сохранения данных", e);
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(autoSaveFile))) {
            for (Task task : getTaskList()) {
                bufferedWriter.write(toString(task));
                bufferedWriter.newLine();
            }

            for (Epic epic : getEpicList()) {
                bufferedWriter.write(toString(epic));
                bufferedWriter.newLine();
            }

            for (SubTask subtask : getSubtaskList()) {
                bufferedWriter.write(toString(subtask));
                bufferedWriter.newLine();
            }

            bufferedWriter.write("История:");
            bufferedWriter.newLine();
            String historyString = historyToString(historyManager);
            if (historyString != null) {
                bufferedWriter.write(historyString);
            }

        } catch (IOException ioe) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + autoSaveFile.getName(), ioe);
        }
    }

    private void setCounter(int maxID) {
        counter = maxID;
    }
}
