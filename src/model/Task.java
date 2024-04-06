package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    protected String name;
    protected String description;
    protected int id;
    protected Progress progress;
    protected LocalDateTime startTime;
    protected Duration duration;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    //конструктор без начала старта и продолжительности задачи
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.progress = Progress.NEW;
        this.startTime = null;
        this.duration = null;
    }

    //конструктор с началом старта задачи и продолжительностью задачи
    public Task(String name, String description, String startTime, int duration) {
        this.name = name;
        this.description = description;
        this.progress = Progress.NEW;
        this.startTime = LocalDateTime.parse(startTime, dtf);
        this.duration = Duration.ofMinutes(duration);
    }

    public int getId() {
        return id;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Progress getProgress() {
        return progress;
    }

    public Integer getEpicId() {
        return null;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public DateTimeFormatter getDateTimeFormat() {
        return dtf;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        } else {
            return startTime.plus(duration);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && progress == task.progress && Objects.equals(startTime, task.startTime)
                && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, progress);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", progress=" + progress +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + getEndTime() +
                '}';
    }
}


