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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginFragment extends Fragment {


    private FirebaseAuth auth;
    private EditText email, password;
    private DatabaseReference userReference;
    private ProgressDialog pd;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO if a user is logged in, go to Dashboard
        auth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance(MainActivity.rtdb_url).getReference("users");

        if (auth.getCurrentUser() != null) {
            NavHostFragment.findNavController(this).navigate(R.id.action_login_successful);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        //
        email = view.findViewById(R.id.edit_email);
        pd = new ProgressDialog(getContext());
        password = view.findViewById(R.id.edit_password);
        pd.setMessage("Loading...");
        pd.setTitle("Authentication");


        //
        view.findViewById(R.id.btn_log_in)
                .setOnClickListener(v -> {
                    // TODO implement sign in logic
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

                                    } else {
                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        task.getException().printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "User Registered", Toast.LENGTH_SHORT).show();
                                    NavHostFragment.findNavController(this).navigate(R.id.action_login_successful);
                                    userReference.child(task.getResult().getUser().getUid()).child("lost").setValue(0);
                                    userReference.child(task.getResult().getUser().getUid()).child("won").setValue(0);
                                }
                                pd.dismiss();
                            });

                    NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
                    Navigation.findNavController(v).navigate(action);
                });

        return view;
    }

    // No options menu in login fragment.
    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i("LOGIN", "SUCCESS");
                        Log.i("User logged in", Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid());

                        Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(this).navigate(R.id.action_login_successful);
                    } else {
                        Log.i("LOGIN", "FAIL");
                        Toast.makeText(getContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                });
    }
}