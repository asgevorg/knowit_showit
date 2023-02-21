package com.example.know_it_show_it;

public class User {
    private String nickname;
    private String gamePin;
    private String ID;

    public User(String nickname, String gamePin, String ID) {
        this.nickname = nickname;
        this.gamePin = gamePin;
        this.ID = ID;
    }

    public String getNickname() {
        return nickname;
    }

    public String getGamePin() {
        return gamePin;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setGamePin(String gamePin) {
        this.gamePin = gamePin;
    }
}
