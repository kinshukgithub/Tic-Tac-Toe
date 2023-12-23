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

import androidsamples.java.tictactoe.models.GameModel;

public class DashboardFragment extends Fragment {

  private static final String TAG = "DashboardFragment";
  private NavController mNavController;
  private FirebaseAuth auth;
  private DatabaseReference gamesRef, usersRef;
  private RecyclerView rv;
  private TextView won, lost, info;

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
    if(FirebaseAuth.getInstance().getCurrentUser() != null) {
      usersRef = FirebaseDatabase.getInstance("https://tic-tac-toe-fc4a3-default-rtdb.firebaseio.com/").getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }
    gamesRef = FirebaseDatabase.getInstance("https://tic-tac-toe-fc4a3-default-rtdb.firebaseio.com/").getReference("games");
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_dashboard, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mNavController = Navigation.findNavController(view);
    won = view.findViewById(R.id.won_score);
    lost = view.findViewById(R.id.lost_score);
    rv = view.findViewById(R.id.list);
    info = view.findViewById(R.id.open_display);
    //if a user is not logged in, go to LoginFragment

    auth = FirebaseAuth.getInstance();
    if(auth.getCurrentUser() == null) {
      mNavController.navigate(R.id.action_need_auth);
      return;
    }

    usersRef = FirebaseDatabase.getInstance("https://tic-tac-toe-fc4a3-default-rtdb.firebaseio.com/").getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    ArrayList<GameModel> gameList = new ArrayList<>();
    gamesRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        gameList.clear();
        for (DataSnapshot shot : snapshot.getChildren()) {
          GameModel game = shot.getValue(GameModel.class);
          if (game.getIsOpen() && !game.getHost().equals(auth.getCurrentUser().getUid())) gameList.add(game);
        }
        rv.setAdapter(new OpenGamesAdapter(gameList, mNavController));
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        info.setText(gameList.isEmpty() ? "No Open Games Available :(" : "Open Games");
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });

    usersRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
if(snapshot.child("wins").getValue() != null) {
    won.setText(snapshot.child("wins").getValue().toString());
}
if(snapshot.child("loses").getValue() != null) {
    lost.setText(snapshot.child("loses").getValue().toString());
}
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });

    // Show a dialog when the user clicks the "new game" button
    view.findViewById(R.id.fab_new_game).setOnClickListener(v -> {

      // A listener for the positive and negative buttons of the dialog
      DialogInterface.OnClickListener listener = (dialog, which) -> {
        String gameType = "No type";
        String gameId = "Single Game ID";
        if (which == DialogInterface.BUTTON_POSITIVE) {
          gameType = getString(R.string.two_player);
          gameId = gamesRef.push().getKey();
          assert gameId != null;
          gamesRef.child(gameId).setValue(new GameModel(FirebaseAuth.getInstance().getCurrentUser().getUid(), gameId));
          Log.i("FIREBASE", "Value set");
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
          gameType = getString(R.string.one_player);
        }
        Log.d(TAG, "New Game: " + gameType);

        // Passing the game type as a parameter to the action
        // extract it in GameFragment in a type safe way
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
    inflater.inflate(R.menu.menu_logout, menu);
    // this action menu is handled in MainActivity
  }
}