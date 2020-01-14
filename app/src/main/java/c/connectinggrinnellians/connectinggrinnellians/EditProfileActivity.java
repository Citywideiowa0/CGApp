package c.connectinggrinnellians.connectinggrinnellians;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    // +--------+-----------------------------------------------------------------------------
    // | Fields |
    // +--------+

    // Declare Fields
    HashMap<String, Object> backup;

    // Declare Widgets
    private Button goProfileButton;
    private EditText fullNameEditText, ClassYearEditText, siteLocationEditText;
    private Button saveInfoButton;
    private Button deleteAccountButton;
    private ProgressDialog progressDialog;

    // Declare Firebase Objects
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userChildDataReference;
    private FirebaseUser user;


    // +----------+-----------------------------------------------------------------------------
    // | onCreate |
    // +----------+

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase Objects
        firebaseAuth = firebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userChildDataReference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.users_child_index)).child(user.getUid());

        // Check to see if User is not signed in, send to login screen if not
        AppUtil.checkUserSession(this, FirebaseDatabase.getInstance().getReference());

        // Initialize Widgets
        fullNameEditText = (EditText) findViewById(R.id.fullNameEditText);
        ClassYearEditText = (EditText) findViewById(R.id.classYearEditText);
        siteLocationEditText = (EditText) findViewById(R.id.siteLocationEditText);
        saveInfoButton = (Button) findViewById(R.id.saveInfoButton);
        goProfileButton = (Button) findViewById(R.id.goProfileButton);
        deleteAccountButton = (Button) findViewById(R.id.deleteAccountButton);
        progressDialog = new ProgressDialog(this);

        // Initialize Fields
        backup = new HashMap<String, Object>();

        // Set OnClickListeners
        goProfileButton.setOnClickListener(this);
        saveInfoButton.setOnClickListener(this);
        deleteAccountButton.setOnClickListener(this);
        siteLocationEditText.setOnClickListener(this);

        // Display User Information
        userChildDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User me = dataSnapshot.getValue(User.class);
                if(me != null) {
                    fullNameEditText.setHint(me.getFullName());
                    ClassYearEditText.setHint(me.getClassYear());
                    siteLocationEditText.setHint(me.getSiteLocation());
                } else {
                    Log.e("No User Information", "User Retrieved is null");
                    //Toast.makeText(EditProfileActivity.this, "No User Information Saved", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    } // onCreate( savedInstanceState)

    // +---------+-----------------------------------------------------------------------------
    // | onClick |
    // +---------+

    @Override
    public void onClick(View v) {

        if(v == goProfileButton) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (v == saveInfoButton) {
            saveUserInformation();
        } else if (v == deleteAccountButton) {
            deleteAccount();
        } else if (v == siteLocationEditText) {
            PopupMenu sitePopup = new PopupMenu(this, siteLocationEditText);
            Menu sitePopupMenu = sitePopup.getMenu();
            String[] itemsArray = getResources().getStringArray(R.array.sites);
            for(String title: itemsArray) {
                sitePopupMenu.add(title);
            }
            sitePopup.getMenuInflater().inflate(R.menu.edit_site_popup_menu, sitePopupMenu);
            sitePopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    siteLocationEditText.setText(item.getTitle());
                    return true;
                }
            });
            sitePopup.show();
        }
    } // OnClick( v)

    // +---------+-----------------------------------------------------------------------------
    // | Helpers |
    // +---------+

    private void saveUserInformation() {
        String name = fullNameEditText.getText().toString().trim();
        String year = ClassYearEditText.getText().toString().trim();
        String site = siteLocationEditText.getText().toString().trim();
        boolean nameExists = name.length() > 0;
        boolean yearExists = year.length() > 0;
        boolean siteExists = site.length() > 0;
        if(nameExists) {
            userChildDataReference.child("fullName").setValue(name);
        }
        if(yearExists) {
            userChildDataReference.child("classYear").setValue(year);
        }
        if(siteExists) {
            userChildDataReference.child("siteLocation").setValue(site);
        }
        if (nameExists || yearExists || siteExists) {
            Toast.makeText(this, "User Information Saved...", Toast.LENGTH_LONG).show();
        }
    } // saveUserInformation()


    private void deleteAccount() {
        AlertDialog.Builder dialog = new AlertDialog.Builder( EditProfileActivity.this);
        dialog.setTitle("Are you sure?");
        dialog.setMessage("Deleting this account will result in completely removing your " +
                "account from the system and you won't be able to access the app");
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.setMessage("Deleting Account...");
                progressDialog.show();
                backupThenDelete();
            }
        }); // dialog.setPositiveButton( message, dialogInterface);

        dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }); // DialogInterface.OnClickListener()

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    } // deleteAccount()

    private void backupThenDelete() {
        userChildDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snap: dataSnapshot.getChildren()) {
                    EditProfileActivity.this.backup.put(snap.getKey(), snap.getValue());
                }
                userChildDataReference.removeValue();
                deleteUserAuthOnCallback();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("backupThenDelete", databaseError.getMessage());
            }
        });
    } // saveBackup()

    private void deleteUserAuthOnCallback() {

        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Account Deleted",
                            Toast.LENGTH_LONG).show();
                    firebaseAuth.signOut();
                    EditProfileActivity.this.backup = null;
                    startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                } else {
                    userChildDataReference.setValue(EditProfileActivity.this.backup);
                    Toast.makeText(EditProfileActivity.this, task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                } // if/else
            } // onComplete( task)
        }); // user.delete().addOnCompleteLister()

    } // deleteUserAuthOnCallback()

} // end EditProfileActivity
