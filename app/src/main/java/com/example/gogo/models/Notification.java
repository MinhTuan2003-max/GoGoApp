package com.example.gogo.models;

public class Notification {
    private int notificationID;
    private User user;
    private String message;
    private String notifyTime;
    private int isRead;
    private int type;

    public Notification(int notificationID, User user, String message, String notifyTime, int isRead, int type) {
        this.notificationID = notificationID;
        this.user = user;
        this.message = message;
        this.notifyTime = notifyTime;
        this.isRead = isRead;
        this.type = type;
    }

    public Notification(User user, String message, String notifyTime, int isRead, int type) {
        this.user = user;
        this.message = message;
        this.notifyTime = notifyTime;
        this.isRead = isRead;
        this.type = type;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public String getNotifyTime() {
        return notifyTime;
    }

    public int getIsRead() {
        return isRead;
    }

    public int getType() {
        return type;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setNotifyTime(String notifyTime) {
        this.notifyTime = notifyTime;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public void setType(int type) {
        this.type = type;
    }

}
