package androidsamples.java.tictactoe;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import androidsamples.java.tictactoe.models.GameModel;

public class GameFragmentTest {
    private GameFragment gameFragment;

    @Before
    public void setUp() {
        gameFragment = new GameFragment();
        GameModel game = new GameModel();
    }

    @Test
    public void testCheckWin() {
        // Set up a winning game state
        gameFragment.gameArray = new String[]{"X", "X", "X", "", "", "", "", "", ""};
        // Check that the winning condition is detected correctly
        assertEquals(1, gameFragment.checkWin());
    }

    @Test
    public void testCheckLost() {
        // Set up a winning game state
        gameFragment.gameArray = new String[]{"O", "O", "X", "X", "O", "X", "O", "X", "O"};
        // Check that the winning condition is detected correctly
        assertEquals(-1, gameFragment.checkWin());
    }

    @Test
    public void testCheckDraw() {
        // Set up a draw game state
        gameFragment.gameArray = new String[]{"X", "O", "X", "X", "O", "O", "O", "X", "X"};
        // Check that the draw condition is detected correctly
        assertEquals(0, gameFragment.checkWin());
    }
    @Test
    public void testUpdateTurn() {
        boolean turn = gameFragment.updateTurn(1);
        assertEquals(turn, gameFragment.myTurn);
    }

}