package vn.edu.stu.doan_qlmuctieu.model;

import java.io.Serializable;
import java.util.Date;

public class follower implements Serializable {
    private int FollowerID;
    private int GoalID;
    private int UserID;
    private Date FollowedAt;

    public follower() {
    }

    public follower(int followerID, int userID, int goalID, Date followedAt) {
        FollowerID = followerID;
        UserID = userID;
        GoalID = goalID;
        FollowedAt = followedAt;
    }

    public int getFollowerID() {
        return FollowerID;
    }

    public void setFollowerID(int followerID) {
        FollowerID = followerID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getGoalID() {
        return GoalID;
    }

    public void setGoalID(int goalID) {
        GoalID = goalID;
    }

    public Date getFollowedAt() {
        return FollowedAt;
    }

    public void setFollowedAt(Date followedAt) {
        FollowedAt = followedAt;
    }
}
