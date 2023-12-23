package androidsamples.java.tictactoe;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class LoginFragmentDirections {
  private LoginFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionLoginSuccessful() {
    return new ActionOnlyNavDirections(R.id.action_login_successful);
  }
}
