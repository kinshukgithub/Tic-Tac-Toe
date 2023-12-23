package androidsamples.java.tictactoe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import androidsamples.java.tictactoe.models.GameModel;

public class OpenGamesAdapter extends RecyclerView.Adapter<OpenGamesAdapter.ViewHolder> {

  private ArrayList<GameModel> list;
  private NavController navController;

  public OpenGamesAdapter(ArrayList<GameModel> list, NavController navController) {
    this.list = list;
    this.navController = navController;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    holder.populate(list.get(position).getGameId(), position + 1, navController);
  }

  @Override
  public int getItemCount() {
    return list.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mIdView;
    public final TextView mContentView;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      mIdView = view.findViewById(R.id.item_number);
      mContentView = view.findViewById(R.id.content);
    }

    @NonNull
    @Override
    public String toString() {
      return super.toString() + " '" + mContentView.getText() + "'";
    }

    public void populate (String game, int i, NavController nav) {
      mContentView.setText(game);
      mIdView.setText("#" + i);
      mView.setOnClickListener(v -> {
        NavDirections action = DashboardFragmentDirections.actionGame("Two-Player", game);
        Navigation.findNavController(mView).navigate(action);
      });
    }
  }
}