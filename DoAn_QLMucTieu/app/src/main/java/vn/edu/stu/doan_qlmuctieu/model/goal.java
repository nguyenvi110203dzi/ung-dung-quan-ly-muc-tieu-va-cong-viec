package vn.edu.stu.doan_qlmuctieu.model;

import java.io.Serializable;
import java.util.Date;

public class goal implements Serializable {
    private int GoalID;
    private int UserID;
    private String GoalTitle;
    private String Description;
    private Date TargetDate;
    private boolean Visibility;
    private int Status;
    private int Progress;

    public goal() {
    }

    public goal(int goalID, int userID, String goalTitle, String description, Date targetDate, boolean visibility, int status, int progress) {
        GoalID = goalID;
        UserID = userID;
        GoalTitle = goalTitle;
        Description = description;
        Visibility = visibility;
        TargetDate = targetDate;
        Status = status;
        Progress = progress;
    }

    public int getGoalID() {
        return GoalID;
    }

    public void setGoalID(int goalID) {
        GoalID = goalID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getGoalTitle() {
        return GoalTitle;
    }

    public void setGoalTitle(String goalTitle) {
        GoalTitle = goalTitle;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Date getTargetDate() {
        return TargetDate;
    }

    public void setTargetDate(Date targetDate) {
        TargetDate = targetDate;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public boolean isVisibility() {
        return Visibility;
    }

    public void setVisibility(boolean visibility) {
        Visibility = visibility;
    }

    public int getProgress() {
        return Progress;
    }

    public void setProgress(int progress) {
        Progress = progress;
    }
}
