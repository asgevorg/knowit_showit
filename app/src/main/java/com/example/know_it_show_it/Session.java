package com.example.know_it_show_it;

public class Session {
    private String name;
    private String gamePin;
    private boolean isActive;

    public Session() {
        //for firestroe
    }

    public Session(String name, String gamePin) {
        this.name = name;
        this.gamePin = gamePin;
        this.isActive = true;
    }

    public String getName() {
        return name;
    }

    public String getGamePin() {
        return gamePin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGamePin(String gamePin) {
        this.gamePin = gamePin;
    }
}
