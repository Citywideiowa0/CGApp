package c.connectinggrinnellians.connectinggrinnellians;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class User {

    // +---------+-----------------------------------------------------------------------------
    // | Fields |
    // +---------+

    private String uniqueEmail;
    private String fullName;
    private String classYear;
    private String siteLocation;
    private boolean backgroundCheckStatus, requiredTrainingsStatus;
    private String timeStamp;

    // +--------------+-----------------------------------------------------------------------------
    // | Constructors |
    // +--------------+

    public User() {
        uniqueEmail = "fake@email :)";
        fullName = "Name: Connor T. Grimmel";
        classYear = "Year: Never Going To Graduate";
        siteLocation = "N/A";
        backgroundCheckStatus = false;
        requiredTrainingsStatus = false;
        timeStamp = AppUtil.getFullTimeStamp();

    }

    public User(String email) {
        uniqueEmail = email;
        fullName = "Name: Connor T. Grimmel";
        classYear = "Year: Never Going To Graduate";
        siteLocation = "N/A";
        backgroundCheckStatus = false;
        requiredTrainingsStatus = false;
        timeStamp = AppUtil.getFullTimeStamp();
    }

    // +---------+-----------------------------------------------------------------------------
    // | Getters |
    // +---------+

    public String getUniqueEmail() {
        return uniqueEmail;
    }

    public String getFullName() {
        return fullName;
    }

    public String getClassYear() {
        return classYear;
    }

    public String getSiteLocation() {
        return siteLocation;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public boolean isBackgroundCheckStatus() {
        return backgroundCheckStatus;
    }

    public boolean isRequiredTrainingsStatus() {
        return requiredTrainingsStatus;
    }



}
