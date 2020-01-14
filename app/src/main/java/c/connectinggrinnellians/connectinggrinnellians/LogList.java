package c.connectinggrinnellians.connectinggrinnellians;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class LogList extends ArrayAdapter<LogInfo> {
    // +--------+-----------------------------------------------------------------------------
    // | Fields |
    // +--------+
    private Activity context;
    private List<LogInfo> logList;

    // +---------+-----------------------------------------------------------------------------
    // | Methods |
    // +---------+

    public LogList(Activity context, List<LogInfo> logList) {
        super(context, R.layout.log_list_layout, logList);
        this.context = context;
        this.logList = logList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.log_list_layout, null, true);

        // Initialize ListViewItem views
        TextView logKeyTextView = (TextView) listViewItem.findViewById(R.id.logKeyTextView);
        TextView logDateListViewTextView = (TextView) listViewItem.findViewById(R.id.logDateListViewTextView);
        TextView logTimeListViewTextView = (TextView) listViewItem.findViewById(R.id.logTimeListViewTextView);
        TextView logSiteLocationTextView = (TextView) listViewItem.findViewById(R.id.logSiteLocationTextView);

        // Set ListViewItems view's texts
        LogInfo logInfo = logList.get(position);
        logKeyTextView.setText("Log " + logInfo.getCount());
        logDateListViewTextView.setText(logInfo.getDate());
        logTimeListViewTextView.setText(logInfo.getTime());
        logSiteLocationTextView.setText(logInfo.getSiteLocation());

        return listViewItem;
    } // getView( position, convertView, parent)

} // logList
