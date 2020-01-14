package c.connectinggrinnellians.connectinggrinnellians;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * @author Michael Spicer
 *
 * @appCredits TVAC Studio - youtube firebase/android app studio tutorial playlist - https://www.youtube.com/watch?v=KEp5RAZNMng
 *       Simplified Coding - youtube firebase/android app studio tutorial playlist - https://www.youtube.com/watch?v=0NFwF7L-YA8&t=376s
 */
// TODO: implement Toolbar for quick and easy log out and profile access - https://www.youtube.com/watch?v=DMkzIOLppf4
// TODO: Figure out how to add and customize a picture for the profile picture
// TODO: Guest user can have acess to all of the same stuff, except I can disable all clickers or something if and just set up the stuff
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // +--------+-----------------------------------------------------------------------------
    // | Fields |
    // +--------+

    // Declare widgets
    private Button signInButton;
    private EditText userNameEditText;
    private EditText passwordEditText;
    private TextView signUpTextView;

    // Declare Firebase objects
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;
    // +----------+-----------------------------------------------------------------------------
    // | onCreate |
    // +----------+


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize firebase object
        firebaseAuth = firebaseAuth.getInstance();

        // If user is already logged in, send them to User Homescreen
        // TODO fix this bug.  goes into loop when no user, and doesn't when there is a user
        if(firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
        }


        // Initialize widgets
        signInButton = (Button) findViewById(R.id.loginButton);
        userNameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        signUpTextView = (TextView) findViewById(R.id.signInTextView);
        progressDialog = new ProgressDialog(this);

        // Initialize OnClickListeners
        signInButton.setOnClickListener(this);
        signUpTextView.setOnClickListener(this);
    } // onCreate ( savedInstanceState)

    // +---------+-----------------------------------------------------------------------------
    // | onClick |
    // +---------+


    @Override
    public void onClick(View v) {
        if (v == signInButton) {
            this.userLogin();
        } else if (v == signUpTextView) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
    } // onClick( v)

    // +---------+-----------------------------------------------------------------------------
    // | Methods |
    // +---------+

    /**
     * Logs in the user
     *
     * @author Michael Spicer 5/23/2019
     */
    private void userLogin() {

        //get username-email and password
        String email = userNameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        //if username or password is empty
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            CharSequence text = "Please Enter an Email and Password";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
            return;
        }

        progressDialog.setMessage("Signing In...");
        progressDialog.show();

        //attempt user signin and notify user if signin was successful or not
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            finish();
                            //start profile activity
                            startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                        } else {
                            passwordEditText.setText("");
                            Toast.makeText(LoginActivity.this, "Login error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    } // userLogin();
}
