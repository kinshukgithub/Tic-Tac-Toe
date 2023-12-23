package androidsamples.java.tictactoe;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class GameFragmentDirections {
  private GameFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionNeedAuth() {
    return new ActionOnlyNavDirections(R.id.action_need_auth);
  }
}
