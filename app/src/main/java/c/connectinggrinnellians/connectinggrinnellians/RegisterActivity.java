package c.connectinggrinnellians.connectinggrinnellians;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    // +--------+-----------------------------------------------------------------------------
    // | Fields |
    // +--------+

    // Declare Widgets
    private Button registerButton;
    private EditText userNameEditText, usernameConfirmEditText;
    private EditText passwordEditText, passwordConfirmEditText;
    private TextView signInTextView;
    private ProgressDialog progressDialog;

    // Declare Firebase Objects
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    // +-----------+-----------------------------------------------------------------------------
    // | onCreate  |
    // +-----------+

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Object
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Initialize Widgets
        registerButton = (Button) findViewById(R.id.registerButton);
        userNameEditText = (EditText) findViewById(R.id.usernameEditText);
        usernameConfirmEditText = (EditText) findViewById(R.id.usernameConfirmEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordConfirmEditText = (EditText) findViewById(R.id.passwordConfirmEditText);
        signInTextView = (TextView) findViewById(R.id.signInTextView);
        progressDialog = new ProgressDialog(this);

        //Set OnClickListeners
        registerButton.setOnClickListener(this);
        signInTextView.setOnClickListener(this);
    } // onCreate( savedInstanceState)

    // +----------+-----------------------------------------------------------------------------
    // | onClick  |
    // +----------+


    @Override
    public void onClick(View v) {
        if (v == registerButton) {
            //register user
            registerUser();
        } else if (v == signInTextView) {
            startActivity(new Intent(this, LoginActivity.class));
        }

    } // onClick( v);

    // +----------+-----------------------------------------------------------------------------
    // | Helpers  |
    // +----------+
    /**
     * Registers the user
     *
     * @author Michael Spicer 5/24/2019 1:28 a.m.
     */
    private void registerUser() {
        String email = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Confirm username and password
        if (confirmInput(email, password)) {
            progressDialog.setMessage("Registering...");
            progressDialog.show();

            // Create new User
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful()) {
                                        //update firebaseAuth and currentUser
                                        firebaseAuth = FirebaseAuth.getInstance();
                                        currentUser = firebaseAuth.getCurrentUser();
                                        User userInfo = new User(currentUser.getEmail());
                                        databaseReference = databaseReference.child(getString(R.string.users_child_index)).child(currentUser.getUid());
                                        databaseReference.setValue(userInfo);
                                        startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Registration error", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
        }
    } // registerUser()

    /**
     * Confirms username and password with username confirm and password confirm entries
     * @param username
     * @param password
     */
    private boolean confirmInput(String username, String password) {
        String userConfirm = usernameConfirmEditText.getText().toString().trim();
        String passConfirm = passwordConfirmEditText.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter an Username and Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(userConfirm) || TextUtils.isEmpty(passConfirm)) {
            Toast.makeText(this, "Please Enter an Username and Password Confirmation", Toast.LENGTH_SHORT).show();
            return false;
        } else if (! username.contentEquals(userConfirm)) {
            Toast.makeText(this, "Usernames Did Not Match", Toast.LENGTH_SHORT).show();
            return false;
        } else if (! password.contentEquals(passConfirm)) {
            Toast.makeText(this, "Passwords Did Not Match", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    } // confirmInput( username, password)

} // end RegisterActivity
