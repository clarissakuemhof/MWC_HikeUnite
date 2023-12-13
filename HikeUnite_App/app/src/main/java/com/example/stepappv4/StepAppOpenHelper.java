package com.example.stepappv4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;


public class StepAppOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DB_HikeUnite";
    public static final String TABLE_NAME = "hiking data";
    public static final String KEY_ID = "id";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_DAY = "day";
    public static final String KEY_HOUR = "hour";

    // New columns for location, distance, and steps
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_ALTITUDE = "altitude";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_STEPS = "steps";

    public static final String CREATE_TABLE_SQL = "CREATE TABLE  " + TABLE_NAME + " (" +
            KEY_ID + " INTEGER PRIMARY KEY, " +
            KEY_DAY + " TEXT, " +
            KEY_HOUR + " TEXT, " +
            KEY_TIMESTAMP + " TEXT, " +
            KEY_LONGITUDE + " REAL, " +
            KEY_ALTITUDE + " REAL, " +
            KEY_DISTANCE + " REAL, " +
            KEY_STEPS + " INTEGER);";

    public StepAppOpenHelper (Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    // Call this method every 10 minutes to save the relevant data during the hike
    public void insertHikeData(double longitude, double altitude, double distance, int steps) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Get current timestamp
        String timestamp = getCurrentTimestamp();

        values.put(KEY_DAY, getCurrentDate());
        values.put(KEY_HOUR, getCurrentHour());
        values.put(KEY_TIMESTAMP, timestamp);
        values.put(KEY_LONGITUDE, longitude);
        values.put(KEY_ALTITUDE, altitude);
        values.put(KEY_DISTANCE, distance);
        values.put(KEY_STEPS, steps);

        // Insert the values into the database
        database.insert(TABLE_NAME, null, values);

        // Close the database
        database.close();
    }

    // Utility methods for date and time
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private String getCurrentHour() {
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return hourFormat.format(Calendar.getInstance().getTime());
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return timestampFormat.format(Calendar.getInstance().getTime());
    }

        // Load all records in the database
    public static void loadRecords(Context context){
        List<String> dates = new LinkedList<String>();
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String [] columns = new String [] {StepAppOpenHelper.KEY_TIMESTAMP};
        Cursor cursor = database.query(StepAppOpenHelper.TABLE_NAME, columns, null, null, StepAppOpenHelper.KEY_TIMESTAMP,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            dates.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Log.d("STORED TIMESTAMPS: ", String.valueOf(dates));
    }

    // load records from a single day
    public static Integer loadSingleRecord(Context context, String date){
        List<String> steps = new LinkedList<String>();
        // Get the readable database
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        String where = StepAppOpenHelper.KEY_DAY + " = ?";
        String [] whereArgs = { date };

        Cursor cursor = database.query(StepAppOpenHelper.TABLE_NAME, null, where, whereArgs, null,
                null, null );

        // iterate over returned elements
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            steps.add(cursor.getString(0));
            cursor.moveToNext();
        }
        database.close();

        Integer numSteps = steps.size();
        Log.d("STORED STEPS TODAY: ", String.valueOf(numSteps));
        return numSteps;
    }

    public static void deleteRecords (Context context) {
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        int numberDeletedRecords = 0;

        numberDeletedRecords = database.delete(StepAppOpenHelper.TABLE_NAME, null, null);
        database.close();

        Toast.makeText(context, "Deleted + "+ String.valueOf(numberDeletedRecords) + " steps", Toast.LENGTH_LONG).show();

    }



    public static Map<Integer, Integer> loadStepsByHour(Context context, String date){
        // 1. Define a map to store the hour and number of steps as key-value pairs
        Map<Integer, Integer>  map = new HashMap<>();

        // 2. Get the readable database
        StepAppOpenHelper databaseHelper = new StepAppOpenHelper(context);
        SQLiteDatabase database = databaseHelper.getReadableDatabase();

        // 3. Define the query to get the data
        Cursor cursor = database.rawQuery("SELECT hour, COUNT(*)  FROM num_steps " +
                "WHERE day = ? GROUP BY hour ORDER BY  hour ASC ", new String [] {date});

        // 4. Iterate over returned elements on the cursor
        cursor.moveToFirst();
        for (int index=0; index < cursor.getCount(); index++){
            Integer tmpKey = Integer.parseInt(cursor.getString(0));
            Integer tmpValue = Integer.parseInt(cursor.getString(1));

            //2. Put the data from the database into the map
            map.put(tmpKey, tmpValue);


            cursor.moveToNext();
        }

        // 5. Close the cursor and database
        cursor.close();
        database.close();

        // 6. Return the map with hours and number of steps
        return map;
    }

    public static Map<String, Integer> loadStepsByDateForLastWeek(Context context) {
        Map<String, Integer> stepsByDate = new TreeMap<>();

        // Get the readable database
        SQLiteDatabase database = new StepAppOpenHelper(context).getReadableDatabase();

        // Calculate the date seven days ago from the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            String date = dateFormat.format(calendar.getTime());

            // Query to retrieve steps for a specific date
            Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM num_steps WHERE day = ?", new String[]{date});

            // Check for data in the cursor and retrieve the step count for each date
            if (cursor != null && cursor.moveToFirst()) {
                int stepsCount = cursor.getInt(0);
                stepsByDate.put(date, stepsCount);
            }

            // Move to the previous day for the next iteration
            calendar.add(Calendar.DAY_OF_YEAR, -1);

            if (cursor != null) {
                cursor.close();
            }
        }

        database.close();
        return stepsByDate;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
