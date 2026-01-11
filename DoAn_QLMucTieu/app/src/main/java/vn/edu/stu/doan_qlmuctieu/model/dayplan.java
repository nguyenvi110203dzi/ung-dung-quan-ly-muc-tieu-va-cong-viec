package vn.edu.stu.doan_qlmuctieu.model;

import java.io.Serializable;
import java.util.Date;

public class dayplan implements Serializable {
    private int DayPlanID;
    private int GoalID;
    private Date Date;
    private String Notes;
    private int Status;
    private int Progress;

    public dayplan() {
    }

    public dayplan(int dayPlanID, int goalID, Date date, String notes, int status, int progress) {
        DayPlanID = dayPlanID;
        Date = date;
        GoalID = goalID;
        Notes = notes;
        Progress = progress;
        Status = status;
    }

    public int getDayPlanID() {
        return DayPlanID;
    }

    public void setDayPlanID(int dayPlanID) {
        DayPlanID = dayPlanID;
    }

    public int getGoalID() {
        return GoalID;
    }

    public void setGoalID(int goalID) {
        GoalID = goalID;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getProgress() {
        return Progress;
    }

    public void setProgress(int progress) {
        Progress = progress;
    }
}
