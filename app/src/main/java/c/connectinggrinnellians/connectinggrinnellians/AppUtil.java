package c.connectinggrinnellians.connectinggrinnellians;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.Month;
import java.util.Calendar;

public class AppUtil {

    // +---------+-----------------------------------------------------------------------------
    // | Methods |
    // +---------+

    public static void checkUserSession(final Activity activity, final DatabaseReference databaseReference) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
           // Log.e("Current User", user.getUid().toString());
            databaseReference.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Log.e("User Check", "User: " + dataSnapshot.getValue());
                    if(dataSnapshot.getValue() == null) {
                        activity.finish();
                        FirebaseAuth.getInstance().signOut();
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                        //Log.e("User Found Guilty", "Exited User Session");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
    } // checkUserSession()

    public static String getFullTimeStamp() {
        Calendar cal = Calendar.getInstance();
        return getDayAndMonthAndYear(cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.YEAR));
    } // getFullTimeStamp())

    public static String getCurrentHourAndMinute() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.HOUR + 1;
        int min = cal.MINUTE;
        String sMin;
        if (min < 10) {
            sMin = "0" + min;
        } else {
            sMin = "" + min;
        }
        return hour + ":" + min;
    } // getCurrentHourAndMinute()

    public static String getCurrentHourAndMinute(int hour, int min) {
        if (min < 10) {
            return hour + ":0" + min;
        } else {
            return hour + ":" + min;
        }
    } // getCurrentHourAndMinute()

    public static String addHourAndMinute(int hour, int min, int deltaHour, int deltaMinute) {
        int newHour = hour + deltaHour % 12;
        int newMin = min + deltaMinute % 60;
        if (newMin < 10) {
            return newHour + ":0" + newMin;
        } else {
            return newHour + ":" + newMin;
        }
    } // addHourAndMinute( hour, min, deltaHour, deltaMinute)

    public static String getDayAndMonthAndYear(int dayOfMonth, int monthOfYear, int year) {
        return determineMonth(monthOfYear) + " " + dayOfMonth + ", " + year;
    }

    // +---------+---------------------
    // | Helpers |
    // +---------+

    private static String determineMonth(int month) {
        String sMonth = "";

        switch (month) {
            case Calendar.JANUARY:
                sMonth = "January";
                break;
            case Calendar.FEBRUARY:
                sMonth = "February";
                break;
            case Calendar.MARCH:
                sMonth = "March";
                break;
            case Calendar.APRIL:
                sMonth = "April";
                break;
            case Calendar.MAY:
                sMonth = "May";
                break;
            case Calendar.JUNE:
                sMonth = "June";
                break;
            case Calendar.JULY:
                sMonth = "July";
                break;
            case Calendar.AUGUST:
                sMonth = "August";
                break;
            case Calendar.SEPTEMBER:
                sMonth = "September";
                break;
            case Calendar.OCTOBER:
                sMonth = "October";
                break;
            case Calendar.DECEMBER:
                sMonth = "December";
                break;
            default:
                sMonth = "UHOH, Error in AppUtil.determineMonth()!";
        } // Switch( month)

        return sMonth;
    } // determineMonth()

} // AppUtil

