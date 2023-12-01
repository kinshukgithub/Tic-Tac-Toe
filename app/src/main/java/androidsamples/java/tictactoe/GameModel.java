package androidsamples.java.tictactoe;

import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class GameModel implements Serializable {

    public List<String> gameArray = null;
    private String host;
    private Boolean isOpen;
    private String gameId;
    // turn == 1 -> host's turn ; 2 -> other guy's
    private int turn;
    private boolean forfeited;

    public GameModel(String host, String id) {
        this.host = host;
        isOpen = true;
        gameArray = Arrays.asList("", "", "", "", "", "", "", "", "");
        this.gameId = id;
        turn = 1;
        forfeited = false;
    }

    public GameModel(){}

    public List<String> getGameArray() {
        return gameArray;
    }

    public void setGameArray(List<String> gameArray) {
        this.gameArray = (gameArray);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean open) {
        isOpen = open;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void updateGameArray(GameModel o) {
        gameArray = o.gameArray;
        turn = o.turn;
    }
    // turn == 1 -> host's turn ; 2 -> other guy's
    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public boolean isForfeited() {
        return forfeited;
    }

    public void setForfeited(boolean forfeited) {
        this.forfeited = forfeited;
    }



    protected boolean checkDraw() {
        if (checkWin() != 0){
            return false;
        }

        for (int i = 0; i < 9; i++) {
            if (this.gameArray.get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }
    int checkWin() {
        String winChar = "";
        if (this.gameArray.get(0).equals(this.gameArray.get(1)) && this.gameArray.get(1).equals(this.gameArray.get(2)) && !this.gameArray.get(0).isEmpty())
            winChar = this.gameArray.get(0);
        else if (this.gameArray.get(3).equals(this.gameArray.get(4)) && this.gameArray.get(4).equals(this.gameArray.get(5)) && !this.gameArray.get(3).isEmpty())
            winChar = this.gameArray.get(3);
        else if (this.gameArray.get(6).equals(this.gameArray.get(7)) && this.gameArray.get(7).equals(this.gameArray.get(8)) && !this.gameArray.get(6).isEmpty())
            winChar = this.gameArray.get(6);
        else if (this.gameArray.get(0).equals(this.gameArray.get(3)) && this.gameArray.get(3).equals(this.gameArray.get(6)) && !this.gameArray.get(0).isEmpty())
            winChar = this.gameArray.get(0);
        else if (this.gameArray.get(4).equals(this.gameArray.get(1)) && this.gameArray.get(1).equals(this.gameArray.get(7)) && !this.gameArray.get(1).isEmpty())
            winChar = this.gameArray.get(1);
        else if (this.gameArray.get(2).equals(this.gameArray.get(5)) && this.gameArray.get(5).equals(this.gameArray.get(8)) && !this.gameArray.get(2).isEmpty())
            winChar = this.gameArray.get(2);
        else if (this.gameArray.get(0).equals(this.gameArray.get(4)) && this.gameArray.get(4).equals(this.gameArray.get(8)) && !this.gameArray.get(0).isEmpty())
            winChar = this.gameArray.get(0);
        else if (this.gameArray.get(6).equals(this.gameArray.get(4)) && this.gameArray.get(4).equals(this.gameArray.get(2)) && !this.gameArray.get(2).isEmpty())
            winChar = this.gameArray.get(2);
        else return 0;

        return (winChar.equals("X")) ? 1 : -1;
    }

}