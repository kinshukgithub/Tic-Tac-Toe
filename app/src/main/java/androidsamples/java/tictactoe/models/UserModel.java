package androidsamples.java.tictactoe.models;

import java.util.Arrays;

public class UserModel {
    private String uid;
    private int wins;
    private int loses;
    public UserModel(String uid, int wins, int loses) {
        this.uid = uid;
        this.wins = wins;
        this.loses = loses;
    }

    public UserModel(){}
    // getter and setter for uid
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // getter and setter for wins
    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    // getter and setter for loses
    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = loses;
    }

}
