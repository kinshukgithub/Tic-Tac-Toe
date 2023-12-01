package androidsamples.java.tictactoe;

import android.app.AlertDialog;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class GameFragment extends Fragment {
    private static final String TAG = "GameFragment";
    public static final String KEY_GAME = "KEY_GAME";
    public static final String KEY_GAME_STATUS = "KEY_GAME_STATUS";
    private static final int GRID_SIZE = 9;
    private final Button[] mButtons = new Button[GRID_SIZE];
    private TextView tv_your_symbol, tv_opponents_symbol;
    private NavController mNavController;
    private TextView display;
    private boolean isSinglePlayer;
    private String myChar;
    private String otherChar;
    private GameModel game;
    private boolean isHost;
    private DatabaseReference gameReference, userReference;

    // on screen buttons
    private Button quitGame;
    private GameOutcomes gameStatus = GameOutcomes.IN_PROGRESS;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Needed to display the action menu for this fragment
        // Handle the back press by adding a confirmation dialog
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
        initialize_views(view);
        if( savedInstanceState != null){
            game = (GameModel) savedInstanceState.getSerializable(KEY_GAME);
            gameStatus = (GameOutcomes) savedInstanceState.getSerializable(KEY_GAME_STATUS);
            updateUI();
            tv_your_symbol.setText( "");
            tv_opponents_symbol.setText( "");
            if( gameStatus != GameOutcomes.IN_PROGRESS){
                for (int i = 0; i < 9; i++) {
                    mButtons[i].setClickable(false);
                }
                quitGame.setText(R.string.go_back);
                if( gameStatus == GameOutcomes.WON){
                    display.setText(R.string.you_win);
                }
                else if( gameStatus == GameOutcomes.LOST){
                    display.setText(R.string.you_lose);
                }
                else{
                    display.setText(R.string.draw);
                }
            }
        }
        quitGame.setOnClickListener(v -> getActivity().onBackPressed());

        // Extract the argument passed with the action in a type-safe way
        GameFragmentArgs args = GameFragmentArgs.fromBundle(getArguments());
        Log.d(TAG, "New game type = " + args.getGameType());

        isSinglePlayer = (args.getGameType().equals("One-Player"));
        userReference = FirebaseDatabase.getInstance(MainActivity.rtdb_url)
                .getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid());
        mNavController = Navigation.findNavController(view);

        if( gameStatus == GameOutcomes.IN_PROGRESS ){
            if (!isSinglePlayer) {
                gameReference = FirebaseDatabase.getInstance(MainActivity.rtdb_url).getReference("games").child(args.getGameId());
                gameReference.get().addOnCompleteListener(
                        new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if( task.isSuccessful()){
                                    game = task.getResult().getValue(GameModel.class);
                                    assert game != null;
                                    Log.d( TAG, "Game initialized, Id:\t" + game.getGameId() );
                                    isHost = game.getHost().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    if (isHost) {
                                        myChar = "X";
                                        otherChar = "O";
                                    } else {
                                        myChar = "O";
                                        otherChar = "X";
                                    }
                                    //get the initial state, regardless whether I am the host or the guest.
                                    updateUI();
                                    tv_your_symbol.setText( "Your symbol is:\n" + myChar);
                                    tv_opponents_symbol.setText( "Opponent's symbol is:\n" + otherChar);

                                    if( !isSinglePlayer && !getItsMyTurn( game.getTurn(), isHost) ) {
                                        Log.d( TAG, "Not single player, and it's not my turn anywaz");
                                        //second player has joined, now the game is closed for new players
                                        game.setIsOpen(false);
                                        updateGame();
                                        waitForOtherPlayer();
                                    }
                                }
                                else{
                                    Log.d( TAG, "Failed to fetch initial game state");
                                }
                            }
                        }
                );
            }
            else{//single player
                isHost = true;
                myChar = "X";
                otherChar = "O";
                //if the game was not loaded from savedInstanceBundle
                if( game == null){
                    game = new GameModel( FirebaseAuth.getInstance().getUid(), Integer.toString(0));
                }
                tv_your_symbol.setText( "Your symbol is:\n" + myChar);
                tv_opponents_symbol.setText( "Opponent's symbol is:\n" + otherChar);
            }
            //set up on OnClickListeners
            for (int i = 0; i < mButtons.length; i++) {
                mButtons[i].setOnClickListener( get_btn_onclick_listener(mButtons[i], i) );
            }
            display.setText(R.string.your_turn);
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable( KEY_GAME, game);
        outState.putSerializable( KEY_GAME_STATUS, gameStatus);
    }

    private void initialize_views(@NonNull View view) {
        display = view.findViewById(R.id.display_tv);
        quitGame = view.findViewById(R.id.back_btn);

        tv_your_symbol = view.findViewById( R.id.tv_your_symbol );
        tv_opponents_symbol = view.findViewById( R.id.tv_opponents_symbol);

        mButtons[0] = view.findViewById(R.id.button0);
        mButtons[1] = view.findViewById(R.id.button1);
        mButtons[2] = view.findViewById(R.id.button2);

        mButtons[3] = view.findViewById(R.id.button3);
        mButtons[4] = view.findViewById(R.id.button4);
        mButtons[5] = view.findViewById(R.id.button5);

        mButtons[6] = view.findViewById(R.id.button6);
        mButtons[7] = view.findViewById(R.id.button7);
        mButtons[8] = view.findViewById(R.id.button8);
    }

    private void computerMove() {
        Random rand = new Random();
        int x = rand.nextInt(9);
        if (checkDraw()) {
            endGame(0);
            return;
        }
        while ( ! game.gameArray.get(x).isEmpty() ) x = rand.nextInt(9);
        Log.i("CHECKING CONDITIONS", "Complete");
        game.gameArray.set(x, otherChar);
        mButtons[x].setText(otherChar);
        mButtons[x].setClickable(false);
        game.setTurn(  game.getTurn()==1 ? 2 : 1 );
        display.setText(R.string.your_turn);
        int win = checkWin();
        if (win == 1 || win == -1) endGame(win);
        else if (checkDraw()) endGame(0);
    }

    private void waitForOtherPlayer() {
        display.setText(R.string.waiting);
        gameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                game = snapshot.getValue(GameModel.class);
                updateUI();
                if ( getItsMyTurn( game.getTurn(), isHost) ) {
                    display.setText(R.string.your_turn);
                } else {
                    display.setText(R.string.waiting);
                }
                if(game.isForfeited()){
                    endGame(1);
                }
                Log.i("onDataChange","data change detected");
                int win = checkWin();
                if (win == 1 || win == -1) endGame(win);
                else if (checkDraw()) endGame(0);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Sets the turn variable, and updates the games array, isOpen
    private void updateGame() {
        gameReference.setValue(game);
    }

    private boolean checkDraw() {
        if (checkWin() != 0){
            return false;
        }

        for (int i = 0; i < 9; i++) {
            if (game.gameArray.get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    int checkWin() {
        String winChar = "";
        if (game.gameArray.get(0).equals(game.gameArray.get(1)) && game.gameArray.get(1).equals(game.gameArray.get(2)) && !game.gameArray.get(0).isEmpty())
            winChar = game.gameArray.get(0);
        else if (game.gameArray.get(3).equals(game.gameArray.get(4)) && game.gameArray.get(4).equals(game.gameArray.get(5)) && !game.gameArray.get(3).isEmpty())
            winChar = game.gameArray.get(3);
        else if (game.gameArray.get(6).equals(game.gameArray.get(7)) && game.gameArray.get(7).equals(game.gameArray.get(8)) && !game.gameArray.get(6).isEmpty())
            winChar = game.gameArray.get(6);
        else if (game.gameArray.get(0).equals(game.gameArray.get(3)) && game.gameArray.get(3).equals(game.gameArray.get(6)) && !game.gameArray.get(0).isEmpty())
            winChar = game.gameArray.get(0);
        else if (game.gameArray.get(4).equals(game.gameArray.get(1)) && game.gameArray.get(1).equals(game.gameArray.get(7)) && !game.gameArray.get(1).isEmpty())
            winChar = game.gameArray.get(1);
        else if (game.gameArray.get(2).equals(game.gameArray.get(5)) && game.gameArray.get(5).equals(game.gameArray.get(8)) && !game.gameArray.get(2).isEmpty())
            winChar = game.gameArray.get(2);
        else if (game.gameArray.get(0).equals(game.gameArray.get(4)) && game.gameArray.get(4).equals(game.gameArray.get(8)) && !game.gameArray.get(0).isEmpty())
            winChar = game.gameArray.get(0);
        else if (game.gameArray.get(6).equals(game.gameArray.get(4)) && game.gameArray.get(4).equals(game.gameArray.get(2)) && !game.gameArray.get(2).isEmpty())
            winChar = game.gameArray.get(2);
        else return 0;
        Log.i("check win","calculating check win");
        return (winChar.equals(myChar)) ? 1 : -1;
    }

    private boolean getItsMyTurn( int turn, boolean isHost){
        return (turn == 1) == isHost;
    }

    // sets the text on the buttons according to the gameArray, and sets filled buttons as unclickable
    private void updateUI() {
        for (int i = 0; i < 9; i++) {
            String v = game.gameArray.get(i);
            if (!v.isEmpty()) {
                mButtons[i].setText(v);
                mButtons[i].setClickable(false);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_logout, menu);
        // this action menu is handled in MainActivity
    }

    // updates user's win/loss count, sets game_over to true, updates game reference, disables all buttons
    private void endGame(int win) {
        //update the user's win/loss count
        switch (win) {
            case 1:
                display.setText(R.string.you_win);
                userReference.child("won").get().addOnSuccessListener(
                        dataSnapshot -> {
                            int value = Integer.parseInt(dataSnapshot.getValue().toString());
                            value = value + 1;
                            dataSnapshot.getRef().getParent().child("won").setValue(value);
                        }
                );
                gameStatus = GameOutcomes.WON;
                break;
            case -1:
                display.setText(R.string.you_lose);
                userReference.child("lost").get().addOnSuccessListener(
                        dataSnapshot -> {
                            int value = Integer.parseInt(dataSnapshot.getValue().toString());
                            value = value + 1;
                            dataSnapshot.getRef().getParent().child("lost").setValue(value);
                        }
                );
                gameStatus = GameOutcomes.LOST;
                break;
            case 0:
                display.setText(R.string.draw);
                gameStatus = GameOutcomes.DRAW;
                break;
            default:
                display.setText(R.string.error);
                Log.i("CHECKING DRAW", "Error: " + win);
                break;
        }
        for (int i = 0; i < 9; i++) {
            mButtons[i].setClickable(false);
        }
        quitGame.setText(R.string.go_back);
        game.setIsOpen(false);
        if (!isSinglePlayer) {
            // Update the game's file
            updateGame();
        }

    }

    //    If a user clicks the back button, a dialog to confirm she wants to forfeit the game is shown.
//    If the user forfeits, it increments her lost count and the opponent’s win count and brings both
//    to their respective dashboards.
    private final OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {

            if( gameStatus != GameOutcomes.IN_PROGRESS ){
                //game is over, endGame function has already updated my win/loss count
                mNavController.popBackStack();
            }
            else{
                //game is not over, but the gentleman is legging it, we will surely penalize him
                AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                        .setTitle(R.string.confirm)
                        .setMessage(R.string.forfeit_game_dialog_message)
                        .setPositiveButton(R.string.yes, (d, which) -> {
                            endGame(-1);
                            if( !isSinglePlayer) {
                                gameStatus = GameOutcomes.LOST;
                                // let the other guy know that ze has won
                                game.setForfeited(true);
                                updateGame();
                            }
                            mNavController.popBackStack();
                        })
                        .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                        .create();
                dialog.show();
            }
        }
    };

    private View.OnClickListener get_btn_onclick_listener(View v, int i) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Button " + i + " clicked");
                // TODO implement listeners
                if ( getItsMyTurn( game.getTurn(), isHost) ){
                    ((Button) v).setText(myChar);
                    game.gameArray.set( i, myChar);
                    v.setClickable(false);
                    game.setTurn( game.getTurn()==1?2:1 );
                    display.setText(R.string.waiting);
                    if (!isSinglePlayer) {
                        updateGame();
                        waitForOtherPlayer();
                    } else {
                        int win = checkWin();
                        if (win == 1 || win == -1) {
                            endGame(win);
                            return;
                        } else if (checkDraw()) {
                            endGame(0);
                            return;
                        }
                        computerMove();
                    }
                    int win = checkWin();
                    if (win == 1 || win == -1) {
                        endGame(win);
                        return;
                    } else if (checkDraw()) {
                        endGame(0);
                        return;
                    }
                } else {
                    Toast.makeText(getContext(), "Please wait for your turn!", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }
}