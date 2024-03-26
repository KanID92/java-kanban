package model;

import java.util.Objects;

public class SubTask extends Task {


    private int epicId;

    //конструктор без начала старта и продолжительности задачи
    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    //конструктор с началом старта задачи и продолжительностью задачи
    public SubTask(String name, String description, int epicId, String startTime, int duration) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
    }

    public void deleteEpicId() {
        epicId = -1;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", progress=" + progress +
                ", epicId=" + epicId +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, progress, epicId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return id == subTask.id && Objects.equals(name, subTask.name) &&
                Objects.equals(description, subTask.description) &&
                progress == subTask.progress &&
                epicId == subTask.epicId;
    }

}
