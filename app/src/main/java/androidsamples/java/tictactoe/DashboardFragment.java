package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class DashboardFragment extends Fragment {

  private static final String TAG = "DashboardFragment";
  private NavController mNavController;
  private FirebaseAuth auth;
  private DatabaseReference gamesRef, userRef;
  private RecyclerView rv;
  private TextView won, lost, info;

  private int moves=0;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public DashboardFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");
    setHasOptionsMenu(true); // Needed to display the action menu for this fragment
    gamesRef = FirebaseDatabase.getInstance(MainActivity.rtdb_url).getReference("games");
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_dashboard, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mNavController = Navigation.findNavController(view);

    // Check if the user is not logged in, navigate to LoginFragment
    rv = view.findViewById(R.id.list);
    won = view.findViewById(R.id.won_score);
    lost = view.findViewById(R.id.lost_score);
    info = view.findViewById(R.id.open_display);

    // Check if the user is logged in; if not, navigate to LoginFragment
    auth = FirebaseAuth.getInstance();
    if(auth.getCurrentUser() == null) {
      mNavController.navigate(R.id.action_need_auth);
      return;
    }

    // Reference to the user data in the Firebase Realtime Database
    userRef = FirebaseDatabase.getInstance(MainActivity.rtdb_url).getReference("users").child(auth.getCurrentUser().getUid());
    ArrayList<GameModel> gameList = new ArrayList<>();

    // ValueEventListener to listen for changes in the "games" node in the database
    gamesRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        // Clear the list of games
        gameList.clear();
        for (DataSnapshot shot : snapshot.getChildren()) {
          // Convert the DataSnapshot to a GameModel object
          GameModel game = shot.getValue(GameModel.class);
          // Check if the game is open and not the single game ID and not hosted by the current user
          if (game.getIsOpen() && !Objects.equals(shot.getKey(), "Single Game ID") && !game.getHost().equals(auth.getCurrentUser().getUid()))
            gameList.add(game);
        }
        // Set up the RecyclerView with the list of open games
        rv.setAdapter(new OpenGamesAdapter(gameList, mNavController));
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        info.setText(gameList.isEmpty() ? "No Open Games Available :(" : "Open Games");
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {}
    });

    // ValueEventListener to listen for changes in the user data
    userRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        // Update the displayed won and lost scores
        won.setText(snapshot.child("won").getValue().toString());
        lost.setText(snapshot.child("lost").getValue().toString());
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error){}
    });

    // Show a dialog when the user clicks the "new game" button
    view.findViewById(R.id.fab_new_game).setOnClickListener(v -> {
      // A listener for the positive and negative buttons of the dialog
      DialogInterface.OnClickListener listener = (dialog, which) -> {
        String gameType = "No type";
        String gameId;
        if (which == DialogInterface.BUTTON_POSITIVE) {
          // Two-player game
          gameType = getString(R.string.two_player);
          gameId = UUID.randomUUID().toString();
          // Create a new game in the database
          gamesRef.child(gameId).setValue( new GameModel( auth.getCurrentUser().getUid().toString(), gameId  ));
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
          // One-player game
          gameType = getString(R.string.one_player);
          gameId = "Single Game ID";
        } else {
          // Cancel button
          return;
        }
        Log.d(TAG, "New Game: " + gameType);

        // Passing the game type and ID as parameters to the action
        // extract it in GameFragment in a type-safe way
        NavDirections action = DashboardFragmentDirections.actionGame(gameType, gameId);
        mNavController.navigate(action);
      };

      // create the dialog
      AlertDialog dialog = new AlertDialog.Builder(requireActivity())
              .setTitle(R.string.new_game)
              .setMessage(R.string.new_game_dialog_message)
              .setPositiveButton(R.string.two_player, listener)
              .setNegativeButton(R.string.one_player, listener)
              .setNeutralButton(R.string.cancel, (d, which) -> d.dismiss())
              .create();
      dialog.show();
    });
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    // Inflate the menu_logout.xml to add items to the action bar
    inflater.inflate(R.menu.menu_logout, menu);
    // This action menu is handled in MainActivity
  }
}
