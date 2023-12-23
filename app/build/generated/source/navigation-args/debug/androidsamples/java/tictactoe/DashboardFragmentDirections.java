package androidsamples.java.tictactoe;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;

public class DashboardFragmentDirections {
  private DashboardFragmentDirections() {
  }

  @NonNull
  public static ActionGame actionGame(@NonNull String gameType, @NonNull String gameId) {
    return new ActionGame(gameType, gameId);
  }

  @NonNull
  public static NavDirections actionNeedAuth() {
    return new ActionOnlyNavDirections(R.id.action_need_auth);
  }

  public static class ActionGame implements NavDirections {
    private final HashMap arguments = new HashMap();

    @SuppressWarnings("unchecked")
    private ActionGame(@NonNull String gameType, @NonNull String gameId) {
      if (gameType == null) {
        throw new IllegalArgumentException("Argument \"gameType\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("gameType", gameType);
      if (gameId == null) {
        throw new IllegalArgumentException("Argument \"gameId\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("gameId", gameId);
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public ActionGame setGameType(@NonNull String gameType) {
      if (gameType == null) {
        throw new IllegalArgumentException("Argument \"gameType\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("gameType", gameType);
      return this;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public ActionGame setGameId(@NonNull String gameId) {
      if (gameId == null) {
        throw new IllegalArgumentException("Argument \"gameId\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("gameId", gameId);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NonNull
    public Bundle getArguments() {
      Bundle __result = new Bundle();
      if (arguments.containsKey("gameType")) {
        String gameType = (String) arguments.get("gameType");
        __result.putString("gameType", gameType);
      }
      if (arguments.containsKey("gameId")) {
        String gameId = (String) arguments.get("gameId");
        __result.putString("gameId", gameId);
      }
      return __result;
    }

    @Override
    public int getActionId() {
      return R.id.action_game;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public String getGameType() {
      return (String) arguments.get("gameType");
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public String getGameId() {
      return (String) arguments.get("gameId");
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) {
          return true;
      }
      if (object == null || getClass() != object.getClass()) {
          return false;
      }
      ActionGame that = (ActionGame) object;
      if (arguments.containsKey("gameType") != that.arguments.containsKey("gameType")) {
        return false;
      }
      if (getGameType() != null ? !getGameType().equals(that.getGameType()) : that.getGameType() != null) {
        return false;
      }
      if (arguments.containsKey("gameId") != that.arguments.containsKey("gameId")) {
        return false;
      }
      if (getGameId() != null ? !getGameId().equals(that.getGameId()) : that.getGameId() != null) {
        return false;
      }
      if (getActionId() != that.getActionId()) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int result = 1;
      result = 31 * result + (getGameType() != null ? getGameType().hashCode() : 0);
      result = 31 * result + (getGameId() != null ? getGameId().hashCode() : 0);
      result = 31 * result + getActionId();
      return result;
    }

    @Override
    public String toString() {
      return "ActionGame(actionId=" + getActionId() + "){"
          + "gameType=" + getGameType()
          + ", gameId=" + getGameId()
          + "}";
    }
  }
}
