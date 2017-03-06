package com.armut.armuthv.services;

/**
 * Created by oguzemreozcan on 19/10/16.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.armut.armuthv.utils.MyUtility;

import java.io.IOException;
import java.util.Calendar;

public class DbExportTask extends AsyncTask<Void, Void, Boolean> {

    private static final String EXPORT_FILENAME = "trackingmap";
    private static final String LOCTABLE_PATH = "/data/com.armut.armuthv/databases/loctable.db";
    private static final String DEBUG_TAG = "DbExportTask";
    private Context ctx;
    private MyUtility mu = new MyUtility();

    public DbExportTask(Context c) {
        ctx = c;
    }


    // automatically done on worker thread (separate from UI thread)
    protected Boolean doInBackground(final Void... args) {
        LocDatabaseHelper database = new LocDatabaseHelper(ctx);
        DatabaseExporter dbe = new DatabaseExporter(database.getReadableDatabase());
        try {
            Calendar calendar = Calendar.getInstance();
            String time = MyUtility.parseTime(calendar.getTimeInMillis());
            dbe.export(Environment.getDataDirectory()+ LOCTABLE_PATH, EXPORT_FILENAME + time);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            mu.appendLog(DEBUG_TAG, e.getMessage());
            return false;
        }
    }

    // can use UI thread here
    protected void onPostExecute(final Boolean success) {
        if (success) {
            Toast.makeText(ctx, "Export successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ctx, "Export failed", Toast.LENGTH_SHORT).show();
        }
    }

}
