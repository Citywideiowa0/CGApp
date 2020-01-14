package c.connectinggrinnellians.connectinggrinnellians;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    // +--------+-----------------------------------------------------------------------------
    // | Fields |
    // +--------+

    // Declare Widgets
    private Button editProfileButton;
    private Button goUserHomeButton;
    private Button logoutButton;
    private TextView backgroundCheckTextView, requiredTrainingsTextView;
    private TextView fullNameTextView, schoolYearTextView, siteLocationTextView,
            accountCreatedDateTextView;

    // Declare Firebase Objects
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference userDataReference;
    private FirebaseUser user;

    // +----------+-----------------------------------------------------------------------------
    // | onCreate |
    // +----------+

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Widgets
        editProfileButton = (Button) findViewById(R.id.editProfileButton);
        goUserHomeButton = (Button) findViewById(R.id.goUserHomeButton);
        logoutButton = (Button) findViewById(R.id.logoutButton);
        backgroundCheckTextView = (TextView) findViewById(R.id.backgroundCheckTextView);
        requiredTrainingsTextView = (TextView) findViewById(R.id.requiredTrainingsTextView);
        fullNameTextView = (TextView) findViewById(R.id.fullNameTextView);
        schoolYearTextView = (TextView) findViewById(R.id.schoolYearTextView);
        siteLocationTextView = (TextView) findViewById(R.id.siteLocationTextView);
        accountCreatedDateTextView = (TextView) findViewById(R.id.accountCreatedDateTextView);

        // Initialize Firebase Objects
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userDataReference = databaseReference.child("users").child(user.getUid());

        // Check User
       AppUtil.checkUserSession(this, databaseReference);

        // set OnClickListeners
        editProfileButton.setOnClickListener(this);
        goUserHomeButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

    } // onCreate( savedInstanceState)

    // +----------+-----------------------------------------------------------------------------
    // | onStart  |
    // +----------+

    @Override
    protected void onStart() {
        super.onStart(); // Modify Required Background Check StatusColors

        // Display user profile info every time the activity starts
        userDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User me = dataSnapshot.getValue(User.class);
                if (me != null) {
                    fullNameTextView.setText(me.getFullName());
                    schoolYearTextView.setText(me.getClassYear());
                    siteLocationTextView.setText(me.getSiteLocation());
                    accountCreatedDateTextView.setText(me.getTimeStamp());

                    if (me.isBackgroundCheckStatus() == true) {
                        backgroundCheckTextView.setBackgroundColor(getResources().getColor(R.color.complete));
                    } else {
                        backgroundCheckTextView.setBackgroundColor(getResources().getColor(R.color.incomplete));
                    }

                    if (me.isRequiredTrainingsStatus() == true) {
                        requiredTrainingsTextView.setBackgroundColor(getResources().getColor(R.color.complete));
                    } else {
                        requiredTrainingsTextView.setBackgroundColor(getResources().getColor(R.color.incomplete));
                    }
                } else {
                    Log.e("No User Information", "User Retrieved is null");
                    Toast.makeText(ProfileActivity.this, "No User Information Saved", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // +---------+-----------------------------------------------------------------------------
    // | onClick |
    // +---------+

    @Override
    public void onClick(View v) {
        if(v == editProfileButton) {
            startActivity(new Intent(this, EditProfileActivity.class));
        } else if (v == goUserHomeButton) {
            startActivity(new Intent(this, UserHomeActivity.class));
        } else if (v == logoutButton) {
            finish();
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
