package vn.edu.stu.doan_qlmuctieu.model;

import java.io.Serializable;
import java.util.Date;

public class task implements Serializable {
    private int TaskID;
    private int DayPlanID;
    private String Title;
    private String Description;
    private int Priority;
    private int Status;
    private int Progress;
    private Date StartTime;
    private Date EndTime;

    public task() {
    }

    public task(int taskID, int dayPlanID, String title, int priority, String description, int status, int progress, Date startTime, Date endTime) {
        TaskID = taskID;
        Title = title;
        DayPlanID = dayPlanID;
        Priority = priority;
        Description = description;
        Status = status;
        Progress = progress;
        StartTime = startTime;
        EndTime = endTime;
    }

    public int getTaskID() {
        return TaskID;
    }

    public void setTaskID(int taskID) {
        TaskID = taskID;
    }

    public int getDayPlanID() {
        return DayPlanID;
    }

    public void setDayPlanID(int dayPlanID) {
        DayPlanID = dayPlanID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getPriority() {
        return Priority;
    }

    public void setPriority(int priority) {
        Priority = priority;
    }

    public int getProgress() {
        return Progress;
    }

    public void setProgress(int progress) {
        Progress = progress;
    }

    public Date getEndTime() {
        return EndTime;
    }

    public void setEndTime(Date endTime) {
        EndTime = endTime;
    }

    public Date getStartTime() {
        return StartTime;
    }

    public void setStartTime(Date startTime) {
        StartTime = startTime;
    }
}
