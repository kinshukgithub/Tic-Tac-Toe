package androidsamples.java.tictactoe;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import androidsamples.java.tictactoe.models.UserModel;

public class LoginFragment extends Fragment {

    private FirebaseAuth auth;
    private EditText email, password;
    private DatabaseReference userReference;
    private ProgressDialog pd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userReference = FirebaseDatabase.getInstance("https://tic-tac-toe-fc4a3-default-rtdb.firebaseio.com/").getReference("users");
        auth = FirebaseAuth.getInstance();
        // If a user is logged in, go to Dashboard
        if (auth.getCurrentUser() != null) {
            NavHostFragment.findNavController(this).navigate(R.id.action_login_successful);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        email = view.findViewById(R.id.edit_email);
        pd = new ProgressDialog(getContext());
        password = view.findViewById(R.id.edit_password);
        pd.setMessage("Loading...");
        pd.setTitle("Authentication");
        view.findViewById(R.id.btn_log_in)
                .setOnClickListener(v -> {
                    // Logic to Sign in with email and password using Firebase
                    pd.show();
                    if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                        Toast.makeText(getContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        login(email.getText().toString(), password.getText().toString());
//                                    Log.i("User created", task.getResult().getUser().getUid());
                                    } else {
                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        task.getException().printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "User Registered", Toast.LENGTH_SHORT).show();
                                    NavHostFragment.findNavController(this).navigate(R.id.action_login_successful);
                                }
                                pd.dismiss();
                            });

                    NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
                    Navigation.findNavController(v).navigate(action);
                });

        return view;
    }

    /**
     * Logs in the user with the provided email and password.
     *
     * @param email    The email of the user.
     * @param password The password of the user.
     */
    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("LOGIN", "SUCCESS");
                        Log.d("User logged in", task.getResult().getUser().getUid());
                        UserModel user = new UserModel(task.getResult().getUser().getUid(), 0, 0);
                        userReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                            NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_login_successful);
                                        } else {
                                            Toast.makeText(getContext(), "Database operation failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Log.i("LOGIN", "FAIL");
                        Toast.makeText(getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                });
    }
}