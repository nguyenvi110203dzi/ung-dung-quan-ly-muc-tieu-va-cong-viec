package vn.edu.stu.doan_qlmuctieu.model;

import java.io.Serializable;

public class user implements Serializable {
    private int UserID;
    private String Name;
    private String Email;
    private String Password;
    private int Role;

    public user() {
    }

    public user(String name, String email, String password, int role) {
        Name = name;
        Email = email;
        Password = password;
        Role = role;
    }

    public user(int userID, String name, String email, String password, int role) {
        UserID = userID;
        Name = name;
        Email = email;
        Password = password;
        Role = role;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }
}
