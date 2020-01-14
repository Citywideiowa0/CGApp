package c.connectinggrinnellians.connectinggrinnellians;

public class LogInfo {

    // +--------+-----------------------------------------------------------------------------
    // | Fields |
    // +--------+
    private String date;
    private String time;
    private int count;
    private String siteLocation;

    // +--------------+--------------------------------------------------------------------------
    // | Constructors |
    // +--------------+

    public LogInfo() {
        this.date = null;
        this.time = null;
        this.count = 0;
        this.siteLocation = null;
    }

    public LogInfo(String date, String time, String siteLocation) {
        this.date = date;
        this.time= time;
        this.count = 0;
        this.siteLocation = siteLocation;
    }

    public LogInfo(String date, String time, int count, String siteLocation) {
        this.date = date;
        this.time = time;
        this.count = count;
        this.siteLocation = siteLocation;
    }

    // +---------+-----------------------------------------------------------------------------
    // | Getters |
    // +---------+

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getCount() {
        return count;
    }

    public String getSiteLocation() {
        return siteLocation;
    }

    // +---------+-----------------------------------------------------------------------------
    // | Setters |
    // +---------+

    public void setCount(int newCount) {
        this.count = newCount;
    }


} // end Log class
