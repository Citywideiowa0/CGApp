package c.connectinggrinnellians.connectinggrinnellians;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Stack;
import java.util.TimeZone;

// TODO: If it is currently hour zero (midnight) it will subtract one and give a time of -1
public class UserHomeActivity extends AppCompatActivity implements View.OnClickListener {


    // +--------+-----------------------------------------------------------------------------
    // | Fields |
    // +--------+

    // Declare Widgets
    private TextView profileTextView;
    private Button addLogButton;
    private TextView logStartTimeTextView, logEndTimeTextView;
    private TextView logDateTextView;
    private ListView logsListView;
    private List<LogInfo> logList;

    // Declare Firebase Objects
    FirebaseAuth firebaseAuth;
    DatabaseReference userChildDataReference;
    DatabaseReference logsDataReference;
    FirebaseUser user;

    // Declare Calendar Objects
    private TimeZone timeZone;
    private Calendar cal;
    int minute, hour, day, month, year;

    // +----------+-----------------------------------------------------------------------------
    // | onCreate |
    // +----------+

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        // Initialize Fields
        logList = new ArrayList<LogInfo>();

        // Initialize Widgets
        profileTextView = (TextView) findViewById(R.id.profileTextView);
        addLogButton = (Button) findViewById(R.id.addLogButton);
        logStartTimeTextView = (TextView) findViewById(R.id.logStartTimeTextView);
        logEndTimeTextView = (TextView) findViewById(R.id.logEndTimeTextView);
        logDateTextView = (TextView) findViewById(R.id.logDateTextView);
        logsListView = (ListView) findViewById(R.id.logsListView);

        // Initialize Firebase Objects
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        userChildDataReference = FirebaseDatabase.getInstance().getReference()
                .child(getString(R.string.users_child_index)).child(user.getUid());
        logsDataReference = userChildDataReference.child(getString(R.string.logs_child_index));

        // Initialize OnClickListeners
        profileTextView.setOnClickListener(this);
        addLogButton.setOnClickListener(this);
        logStartTimeTextView.setOnClickListener(this);
        logEndTimeTextView.setOnClickListener(this);
        logDateTextView.setOnClickListener(this);

        // initialize Calendar Objects
        timeZone = TimeZone.getTimeZone(getString(R.string.time_zone));

        // Populate User Information Displays
        profileTextView.setText(user.getEmail());

        // Check User
        AppUtil.checkUserSession(this, FirebaseDatabase.getInstance().getReference());

    } // onCreate()

    // +----------+-----------------------------------------------------------------------------
    // | onStart  |
    // +----------+

    @Override
    protected void onStart() {
        super.onStart();

        //Update Time
        cal = Calendar.getInstance();
        cal.setTimeZone(timeZone);
        hour = cal.get(Calendar.HOUR) - 1; /* -1 since tutor likely logs an hour after tutoring */
        minute = cal.get(Calendar.MINUTE);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        logStartTimeTextView.setHint(AppUtil.getCurrentHourAndMinute(hour, minute));
        logEndTimeTextView.setHint(AppUtil.addHourAndMinute(hour, minute,1, 0));
        logDateTextView.setHint(AppUtil.getDayAndMonthAndYear(day, month, year));

        // Display ListView with Logs
        logsDataReference.orderByChild("count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //TODO optimize this to not clear the list and recreate, but only add to the list (probably do logList.set(log.getCount(), log))
                logList.clear();
                Stack<LogInfo> reverse = new Stack<>();
                for(DataSnapshot logSnap: dataSnapshot.getChildren()) {
                    LogInfo log = logSnap.getValue(LogInfo.class);
                    reverse.push(log);
                }
                for(int i = reverse.size(); i > 0; i--) {
                    logList.add(reverse.pop());
                }
                LogList adapter = new LogList(UserHomeActivity.this, logList);
                logsListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); // logsDataReference.orderByChild("count").addValueEventListener()

        // Click Listener for individual Log item on List View
        logsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                PopupMenu popup = new PopupMenu(UserHomeActivity.this, view);
                popup.inflate(R.menu.delete_log_popup_menu);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        TextView v = (TextView) view.findViewById(R.id.logKeyTextView);
                        String childValueDeletePath = "/" + getString(R.string.users_child_index)
                                + "/" + user.getUid() + "/" + getString(R.string.logs_child_index)
                                + "/" + v.getText() + "/";
                        DatabaseReference dR = FirebaseDatabase.getInstance().getReference().child(childValueDeletePath);
                        dR.removeValue();
                        return true;
                    }
                });
            }
        }); // logsListView.setOnItemClickListener()
    } // onStart()

    // +---------+-----------------------------------------------------------------------------
    // | onClick |
    // +---------+

    @Override
    public void onClick(View v) {
        if(v == profileTextView) {
            startActivity(new Intent(this, ProfileActivity.class));
        }else if (v == addLogButton) {
            addLog();
        } else if (v == logStartTimeTextView) {
            setLogStartTime();
        } else if (v == logEndTimeTextView) {
            setLogEndtTime();
        } else if (v == logDateTextView) {
            setLogDate();
        }
    } // onClick( v)



    // +---------+-----------------------------------------------------------------------
    // | Helpers |
    // +---------+


    private void addLog(){

        userChildDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User me = dataSnapshot.getValue(User.class);
                if(me != null) {
                    updateTutorLogs(me.getSiteLocation());
                } else {
                    Log.e("No User Information", "User Retrieved is null");
                    //Toast.makeText(UserHomeActivity.this, "No User Information Saved", Toast.LENGTH_LONG).show();
                }
            } // onDataChange( dataSnapshot)

            @Override
            public void onCancelled(DatabaseError databaseError) {

            } // onCancelled( databseError)
        }); // userChildDataReference.addListenerForSingleValueEvent();

    } // addLog()

    private void setLogStartTime() {
        TimePickerDialog.OnTimeSetListener timeSetListener;
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                logStartTimeTextView.setText(hourOfDay + ":" + minute);
            }
        }; // DatePickerDialog.OnDateSetListener()

        TimePickerDialog dialog = new TimePickerDialog(
                UserHomeActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                timeSetListener,
                hour, minute,false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    } // setLogStartTime()

    private void setLogEndtTime() {
        TimePickerDialog.OnTimeSetListener timeSetListener;
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                logEndTimeTextView.setText(hourOfDay + ":" + minute);
            }
        }; // DatePickerDialog.OnDateSetListener()

        TimePickerDialog dialog = new TimePickerDialog(
                UserHomeActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                timeSetListener,
                hour + 1, minute,false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void setLogDate() {

        DatePickerDialog.OnDateSetListener dateSetListener;
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                logDateTextView.setText(AppUtil.getDayAndMonthAndYear(dayOfMonth, month, year));
            }
        }; // DatePickerDialog.OnDateSetListener()
        
        DatePickerDialog dialog = new DatePickerDialog(
                UserHomeActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    } // setLogDate()

    private void updateTutorLogs(final String siteLocation) {

        // Retrieve Date and Time Strings from TextViews
        String startTime = logStartTimeTextView.getText().toString().trim();
        String endTime = logEndTimeTextView.getText().toString().trim();
        String date = logDateTextView.getText().toString().trim();
        // if input is empty, get default information or previously stored information
        if (startTime.length() <= 0) {
            startTime = logStartTimeTextView.getHint().toString().trim();
        }
        if (endTime.length() <= 0) {
            endTime = logEndTimeTextView.getHint().toString().trim();
        }
        if (date.length() <= 0) {
            date = logDateTextView.getHint().toString().trim();
        }
        final String logTime = startTime + " - " + endTime;
        final String logDate = date;

        // Add log to user in firebase database reference
        Query query = logsDataReference.orderByChild("count").limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //TODO add indexOn() method to improve query searching
                LogInfo newLog = new LogInfo(logDate, logTime, siteLocation);
                LogInfo lastLog = new LogInfo();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.getValue() != null) {
                        lastLog = snap.getValue(LogInfo.class);
                    } // if
                } // for
                int count = lastLog.getCount() + 1;
                newLog.setCount(count);
                logsDataReference.child("Log " + count).setValue(newLog);
            } // onDataChange( dataSnapshot)

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); // query.addListenerForSingleValueEvent()
    } // setLogArrayList()

} // end UserHomeActivity
