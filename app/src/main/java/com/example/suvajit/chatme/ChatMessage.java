package com.example.suvajit.chatme;

public class ChatMessage {
    String message;
    String name;

    public ChatMessage() {
    }

    public ChatMessage(String name, String message) {
        this.message = message;
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }


}
