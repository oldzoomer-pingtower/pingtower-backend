package ru.oldzoomer.pingtower.notificator.service;

public interface NotificationChannel {
    void send(String message);
    String getChannelType();
}