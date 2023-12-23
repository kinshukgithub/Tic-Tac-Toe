package androidsamples.java.tictactoe;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;
import androidx.navigation.NavArgs;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;

public class GameFragmentArgs implements NavArgs {
  private final HashMap arguments = new HashMap();

  private GameFragmentArgs() {
  }

  @SuppressWarnings("unchecked")
  private GameFragmentArgs(HashMap argumentsMap) {
    this.arguments.putAll(argumentsMap);
  }

  @NonNull
  @SuppressWarnings("unchecked")
  public static GameFragmentArgs fromBundle(@NonNull Bundle bundle) {
    GameFragmentArgs __result = new GameFragmentArgs();
    bundle.setClassLoader(GameFragmentArgs.class.getClassLoader());
    if (bundle.containsKey("gameType")) {
      String gameType;
      gameType = bundle.getString("gameType");
      if (gameType == null) {
        throw new IllegalArgumentException("Argument \"gameType\" is marked as non-null but was passed a null value.");
      }
      __result.arguments.put("gameType", gameType);
    } else {
      throw new IllegalArgumentException("Required argument \"gameType\" is missing and does not have an android:defaultValue");
    }
    if (bundle.containsKey("gameId")) {
      String gameId;
      gameId = bundle.getString("gameId");
      if (gameId == null) {
        throw new IllegalArgumentException("Argument \"gameId\" is marked as non-null but was passed a null value.");
      }
      __result.arguments.put("gameId", gameId);
    } else {
      throw new IllegalArgumentException("Required argument \"gameId\" is missing and does not have an android:defaultValue");
    }
    return __result;
  }

  @NonNull
  @SuppressWarnings("unchecked")
  public static GameFragmentArgs fromSavedStateHandle(@NonNull SavedStateHandle savedStateHandle) {
    GameFragmentArgs __result = new GameFragmentArgs();
    if (savedStateHandle.contains("gameType")) {
      String gameType;
      gameType = savedStateHandle.get("gameType");
      if (gameType == null) {
        throw new IllegalArgumentException("Argument \"gameType\" is marked as non-null but was passed a null value.");
      }
      __result.arguments.put("gameType", gameType);
    } else {
      throw new IllegalArgumentException("Required argument \"gameType\" is missing and does not have an android:defaultValue");
    }
    if (savedStateHandle.contains("gameId")) {
      String gameId;
      gameId = savedStateHandle.get("gameId");
      if (gameId == null) {
        throw new IllegalArgumentException("Argument \"gameId\" is marked as non-null but was passed a null value.");
      }
      __result.arguments.put("gameId", gameId);
    } else {
      throw new IllegalArgumentException("Required argument \"gameId\" is missing and does not have an android:defaultValue");
    }
    return __result;
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

  @SuppressWarnings("unchecked")
  @NonNull
  public Bundle toBundle() {
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

  @SuppressWarnings("unchecked")
  @NonNull
  public SavedStateHandle toSavedStateHandle() {
    SavedStateHandle __result = new SavedStateHandle();
    if (arguments.containsKey("gameType")) {
      String gameType = (String) arguments.get("gameType");
      __result.set("gameType", gameType);
    }
    if (arguments.containsKey("gameId")) {
      String gameId = (String) arguments.get("gameId");
      __result.set("gameId", gameId);
    }
    return __result;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
        return true;
    }
    if (object == null || getClass() != object.getClass()) {
        return false;
    }
    GameFragmentArgs that = (GameFragmentArgs) object;
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
    return true;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + (getGameType() != null ? getGameType().hashCode() : 0);
    result = 31 * result + (getGameId() != null ? getGameId().hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "GameFragmentArgs{"
        + "gameType=" + getGameType()
        + ", gameId=" + getGameId()
        + "}";
  }

  public static final class Builder {
    private final HashMap arguments = new HashMap();

    @SuppressWarnings("unchecked")
    public Builder(@NonNull GameFragmentArgs original) {
      this.arguments.putAll(original.arguments);
    }

    @SuppressWarnings("unchecked")
    public Builder(@NonNull String gameType, @NonNull String gameId) {
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
    public GameFragmentArgs build() {
      GameFragmentArgs result = new GameFragmentArgs(arguments);
      return result;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public Builder setGameType(@NonNull String gameType) {
      if (gameType == null) {
        throw new IllegalArgumentException("Argument \"gameType\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("gameType", gameType);
      return this;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public Builder setGameId(@NonNull String gameId) {
      if (gameId == null) {
        throw new IllegalArgumentException("Argument \"gameId\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("gameId", gameId);
      return this;
    }

    @SuppressWarnings({"unchecked","GetterOnBuilder"})
    @NonNull
    public String getGameType() {
      return (String) arguments.get("gameType");
    }

    @SuppressWarnings({"unchecked","GetterOnBuilder"})
    @NonNull
    public String getGameId() {
      return (String) arguments.get("gameId");
    }
  }
}
