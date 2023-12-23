package androidsamples.java.tictactoe;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import androidsamples.java.tictactoe.models.GameModel;
import androidsamples.java.tictactoe.models.UserModel;

public class GameFragment extends Fragment {
    private static final String TAG = "GameFragment";
    private static final int GRID_SIZE = 9;

    private final Button[] mButtons = new Button[GRID_SIZE];
    private Button quitGame;
    private NavController mNavController;
    private TextView display;

    private boolean isSinglePlayer = true;
    private String myChar = "X";
    private String otherChar = "O";
    boolean myTurn = true;
    String[] gameArray = new String[]{"", "", "", "", "", "", "", "", ""};
    private boolean gameEnded = false;
    private GameModel game;
    private boolean isHost = true;
    private DatabaseReference userReference;
    private DatabaseReference gameReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        GameFragmentArgs args = null;
        if (getArguments() != null) {
            args = GameFragmentArgs.fromBundle(getArguments());
        }
        Log.d(TAG, "New game type = " + args.getGameType());
        isSinglePlayer = (args.getGameType().equals("One-Player"));

        userReference = FirebaseDatabase.getInstance("https://tic-tac-toe-fc4a3-default-rtdb.firebaseio.com/").getReference("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        if (!isSinglePlayer) {
            gameReference = FirebaseDatabase.getInstance("https://tic-tac-toe-fc4a3-default-rtdb.firebaseio.com/").getReference("games").child(args.getGameId());
            gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    game = snapshot.getValue(GameModel.class);
                    assert game != null;
                    gameArray = (game.getGameArray()).toArray(new String[9]);
                    if (game.getTurn() == 1) {
                        if (game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            isHost = true;
                            myTurn = true;
                            myChar = "X";
                            otherChar = "O";
                        } else {
                            isHost = false;
                            myTurn = false;
                            myChar = "O";
                            otherChar = "X";
                        }
                    } else {
                        if (!game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            myTurn = true;
                            myChar = "X";
                            otherChar = "O";
                            isHost = false;
                        } else {
                            isHost = true;
                            myTurn = false;
                            myChar = "O";
                            otherChar = "X";
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Game setup error", error.getMessage());
                }
            });
        }

        /**
         * A callback for handling the back button press event.
         */
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d(TAG, "Back pressed");
                if (!gameEnded) {
                    AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                            .setTitle(R.string.confirm)
                            .setMessage(R.string.forfeit_game_dialog_message)
                            .setPositiveButton(R.string.yes, (d, which) -> {
                                if (!isSinglePlayer) {
                                    gameReference.child("isOpen").setValue(false);
                                }
                                mNavController.popBackStack();
                            })
                            .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                            .create();
                    dialog.show();
                } else {
                    assert getParentFragment() != null;
                    NavHostFragment.findNavController(getParentFragment()).navigateUp();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        display = view.findViewById(R.id.display_tv);
        quitGame = view.findViewById(R.id.back_btn);
        quitGame.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
        if (!isSinglePlayer) {
            gameReference.child("isOpen").setValue(true);
            boolean check = false;
            for (String s : gameArray) {
                if (!s.isEmpty()) {
                    check = true;
                    break;
                }
            }
            if (!check) {
                waitForOtherPlayer();
            }
        } else {
            Log.d(TAG, "My turn is" + myTurn);
            if(myTurn){
                display.setText(R.string.your_turn);
            } else {
                display.setText(R.string.waiting);
            }
        }

        mNavController = Navigation.findNavController(view);

        mButtons[0] = view.findViewById(R.id.button0);
        mButtons[1] = view.findViewById(R.id.button1);
        mButtons[2] = view.findViewById(R.id.button2);

        mButtons[3] = view.findViewById(R.id.button3);
        mButtons[4] = view.findViewById(R.id.button4);
        mButtons[5] = view.findViewById(R.id.button5);

        mButtons[6] = view.findViewById(R.id.button6);
        mButtons[7] = view.findViewById(R.id.button7);
        mButtons[8] = view.findViewById(R.id.button8);

        for (int i = 0; i < mButtons.length; i++) {
            int finalI = i;
            mButtons[i].setOnClickListener(v -> {
                if (myTurn) {
                    Log.d(TAG, "Button " + finalI + " clicked");
                    ((Button) v).setText(myChar);
                    gameArray[finalI] = myChar;
                    v.setClickable(false);
                    if (!isSinglePlayer) {
                        updateDB();
                        myTurn = updateTurn(game.getTurn());
                    }
                    int win = checkWin();
                    if (win == 1 || win == -1) {
                        endGame(win);
                        if(!isSinglePlayer){
                            gameReference.child("isOpen").setValue(false);
                        }
                        return;
                    } else if (checkDraw()) {
                        endGame(0);
                        if(!isSinglePlayer){
                            gameReference.child("isOpen").setValue(false);
                        }
                        return;
                    }
                    myTurn = !myTurn;

                    if (isSinglePlayer) {
                        doRoboThings();
                    } else {
                        waitForOtherPlayer();
                    }
                } else {
                    Toast.makeText(getContext(), "Please wait for your turn!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Checks if the game is a draw.
     * 
     * @return true if the game is a draw, false otherwise.
     */
    private boolean checkDraw() {
        if (checkWin() != 0) return false;
        Log.i("CHECKING WIN IN DRAW", "Complete: " + checkWin());
        for (int i = 0; i < 9; i++) {
            if (gameArray[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Ends the game and displays the result based on the win parameter.
     * If win is 1, displays a dialog box with the "You win" message and navigates back to the previous fragment.
     * If win is -1, displays a dialog box with the "You lose" message and navigates back to the previous fragment.
     * If win is 0, displays a dialog box with the "Draw" message and navigates back to the previous fragment.
     * If win is any other value, displays an error message.
     * Disables the clickable functionality of the game buttons.
     * Sets the gameEnded flag to true.
     * Sets the text of the quitGame button to "Go back".
     * If the game is not single player, updates the database.
     */
    private void endGame(int result) {
        if (gameEnded) return; // Prevent multiple dialogs
        DialogUtils _dialogUtils = new DialogUtils();
        for (int i = 0; i < 9; i++) {
            mButtons[i].setClickable(false);
            display.setText("Game ended");
        }
        switch (result) {
            case 1:
                display.setText(R.string.you_win);
                _dialogUtils.showDialog(getContext(), "Congratulation! You won", "OK");
                break;
            case -1:
                display.setText(R.string.you_lose);
                _dialogUtils.showDialog(getContext(), "Sorry! You lost", "OK");
                break;
            case 0:
                display.setText(R.string.draw);
                _dialogUtils.showDialog(getContext(), "It's a draw", "OK");
                break;
            default:
                display.setText(R.string.error);
                Log.i("CHECKING DRAW", "Error: " + result);
                break;
        }
        quitGame.setText(R.string.go_back);
        gameEnded = true; // Set this flag as soon as the game ends
    }

    /**
     * Waits for the other player's move and updates the game state accordingly.
     */
    private void waitForOtherPlayer() {

        display.setText(R.string.waiting);
        gameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GameModel l = snapshot.getValue(GameModel.class);
                game.updateGameArray(l);
                gameArray = (game.getGameArray()).toArray(new String[9]);
                updateUI();
                myTurn = updateTurn(game.getTurn());
                if(myTurn){
                    display.setText(R.string.your_turn);
                } else {
                    display.setText(R.string.waiting);
                }
                int win = checkWin();
                if (win == 1 || win == -1) endGame(win);
                else if (checkDraw()) endGame(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Updates the turn based on the current player's turn.
     * 
     * @param turn The current player's turn (1 for host, 2 for guest).
     * @return true if it is the current player's turn, false otherwise.
     */
    boolean updateTurn(int turn) {
        return (turn == 1) == isHost;
    }

    /**
     * Updates the UI by setting the text of the buttons based on the gameArray values.
     * If a value in the gameArray is not empty, the corresponding button text is set to that value.
     * Additionally, the clickable property of the buttons is set to false for non-empty values.
     */
    private void updateUI() {
        for (int i = 0; i < 9; i++) {
            String v = gameArray[i];
            if (!v.isEmpty()) {
                mButtons[i].setText(v);
                mButtons[i].setClickable(false);
            }
        }
    }

    /**
     * Updates the game state in the database.
     * - Sets the game array to the current gameArray.
     * - Sets the isOpen flag to indicate if the game has ended.
     * - Updates the turn to the next player's turn.
     */
    private void updateDB() {
        gameReference.child("gameArray").setValue(Arrays.asList(gameArray));
        gameReference.child("isOpen").setValue(!gameEnded);
        if (game.getTurn() == 1) {
            game.setTurn(2);
        } else {
            game.setTurn(1);
        }
        gameReference.child("turn").setValue(game.getTurn());
    }

    /**
     * Performs the actions for the AI player in the game.
     * Generates a random move for the AI player and updates the game state accordingly.
     * Checks for a draw or win condition after each move.
     */
    private void doRoboThings() {
        Random rand = new Random();
        int x = rand.nextInt(9);
        if (checkDraw()) {
            endGame(0);
            return;
        }
        while (!gameArray[x].isEmpty()) x = rand.nextInt(9);
        Log.i("CHECKING CONDITIONS", "Complete");
        gameArray[x] = otherChar;
        mButtons[x].setText(otherChar);
        mButtons[x].setClickable(false);
        myTurn = !myTurn;
        display.setText(R.string.your_turn);
        int win = checkWin();
        if (win == 1 || win == -1) endGame(win);
        else if (checkDraw()) endGame(0);
    }

    /**
     * Checks if there is a winning condition in the tic-tac-toe game.
     * 
     * @return 1 if the player wins, -1 if the opponent wins, 0 if there is no winner yet.
     */
    int checkWin() {
        String winChar = "";
        if (gameArray[0].equals(gameArray[1]) && gameArray[1].equals(gameArray[2]) && !gameArray[0].isEmpty())
            winChar = gameArray[0];
        else if (gameArray[3].equals(gameArray[4]) && gameArray[4].equals(gameArray[5]) && !gameArray[3].isEmpty())
            winChar = gameArray[3];
        else if (gameArray[6].equals(gameArray[7]) && gameArray[7].equals(gameArray[8]) && !gameArray[6].isEmpty())
            winChar = gameArray[6];
        else if (gameArray[0].equals(gameArray[3]) && gameArray[3].equals(gameArray[6]) && !gameArray[0].isEmpty())
            winChar = gameArray[0];
        else if (gameArray[4].equals(gameArray[1]) && gameArray[1].equals(gameArray[7]) && !gameArray[1].isEmpty())
            winChar = gameArray[1];
        else if (gameArray[2].equals(gameArray[5]) && gameArray[5].equals(gameArray[8]) && !gameArray[2].isEmpty())
            winChar = gameArray[2];
        else if (gameArray[0].equals(gameArray[4]) && gameArray[4].equals(gameArray[8]) && !gameArray[0].isEmpty())
            winChar = gameArray[0];
        else if (gameArray[6].equals(gameArray[4]) && gameArray[4].equals(gameArray[2]) && !gameArray[2].isEmpty())
            winChar = gameArray[2];
        else return 0;

        return (winChar.equals(myChar)) ? 1 : -1;
    }

    public class DialogUtils {
        public void showDialog(Context context, String message, String text) {
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(Objects.equals(message, "Congratulation! You won")){
                        int value = Integer.parseInt(dataSnapshot.child("wins").getValue().toString());
                        value = value + 1;
                        dataSnapshot.getRef().child("wins").setValue(value);
                    } else if(Objects.equals(message, "Sorry! You lost")){
                        int value = Integer.parseInt(dataSnapshot.child("loses").getValue().toString());
                        value = value + 1;
                        dataSnapshot.getRef().child("loses").setValue(value);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message)
                    .setPositiveButton(text, (dialog, which) -> {
                        dialog.dismiss();
                        mNavController.navigateUp();
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_logout, menu);
    }
}