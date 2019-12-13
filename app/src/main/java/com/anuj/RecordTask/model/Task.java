package com.anuj.RecordTask.model;

public class Task {
    private int taskId;
    private String taskTitle;
    private String taskDescription;
    private String date;
    private int status;

    public Task() {

    }

    public Task(String taskTitle, String taskDescription, String date, int status) {
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.date = date;
        this.status = status;
        if(taskDescription != null)
            this.taskDescription = "No Description";
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String Date) {
        this.date = Date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskTitle='" + taskTitle + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", date='" + date + '\'' +
                ", status=" + status +
                '}';
    }
}
