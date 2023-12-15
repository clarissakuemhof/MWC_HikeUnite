package com.example.stepappv4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public static final String TABLE_NAME = "hiking_data";

    public static final String TABLE_NAME2 = "gps_points";
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_DAY = "day";
    public static final String KEY_HOUR = "hour";

    // New columns for location, distance, and steps
    public  static final String KEY_GPS_NUM = "gpsnum";
    public static final String KEY_LONGITUDE = "longitude";

    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_ALTITUDE = "altitude";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_STEPS = "steps";



    public StepAppOpenHelper (Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.onCreate(getWritableDatabase());

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TABLE_SQL);
        sqLiteDatabase.execSQL(CREATE_TABLE_GPS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(sqLiteDatabase);
    }



    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +"  (" +
            KEY_ID + " INTEGER PRIMARY KEY, " +
            KEY_DAY + " TEXT, " +
            KEY_NAME + " TEXT, " +
            KEY_DISTANCE + " REAL, " +
            KEY_STEPS + " INTEGER);";

    public static final String CREATE_TABLE_GPS = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME2 + " (" +
            KEY_GPS_NUM + " INTEGER PRIMARY KEY, " +
            KEY_DAY + " TEXT, " +
            KEY_HOUR + " TEXT, " +
            KEY_TIMESTAMP + " TEXT, " +
            KEY_ID + " INTEGER, " +
            KEY_LONGITUDE + " REAL, " +
            KEY_LATITUDE + " REAL, " +
            KEY_ALTITUDE + " REAL);";

    // Call this method every 10 minutes to save the relevant data during the hike
    public void insertHikeData(double distance, int steps, String name) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Get the last id from the database
        int lastId = getLastId(database);

        // Increment the last id to assign a new one
        int newId = lastId + 1;

        // Get current timestamp
        String timestamp = getCurrentTimestamp();

        values.put(KEY_ID, newId);
        values.put(KEY_DAY, getCurrentDate());
        values.put(KEY_DISTANCE, distance);
        values.put(KEY_STEPS, steps);
        values.put(KEY_NAME, name);

        // Insert the values into the database
        database.insert(TABLE_NAME, null, values);
        Log.d("DatabaseInsert", "Inserted GPS data: "
                + ", ID: " + newId
                + ", Distance: " + distance
                + ", steps: " + steps
                + ", Timestamp: " + timestamp);

        // Close the database
        database.close();
    }

    public void insertGPSData(double longitude, double altitude, double latitude, int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Get the last gpsnum from the database
        int lastGpsNum = getLastGpsNum(database);

        // Increment the last gpsnum to assign a new one
        int newGpsNum = lastGpsNum + 1;

        // Get current timestamp
        String timestamp = getCurrentTimestamp();

        values.put(KEY_GPS_NUM, newGpsNum);
        values.put(KEY_DAY, getCurrentDate());
        values.put(KEY_HOUR, getCurrentHour());
        values.put(KEY_TIMESTAMP, timestamp);
        values.put(KEY_ID, id);
        values.put(KEY_LONGITUDE, longitude);
        values.put(KEY_LATITUDE, longitude);  // Note: This should be latitude, not longitude
        values.put(KEY_ALTITUDE, altitude);

        // Insert the values into the database
        database.insert(TABLE_NAME2, null, values);
        // Log the inserted entry
        Log.d("DatabaseInsert", "Inserted GPS data - GPS_NUM: " + newGpsNum
                + ", ID: " + id
                + ", Longitude: " + longitude
                + ", Latitude: " + latitude
                + ", Altitude: " + altitude
                + ", Timestamp: " + timestamp);
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



    /**
     * Function to get the last gpsnum entry in the gps_points table
     * @param database database you want to access
     * @return last gps entry in the table
     */
    private int getLastGpsNum(SQLiteDatabase database) {
        int lastGpsNum = 0;
        Cursor cursor = database.rawQuery("SELECT MAX(" + KEY_GPS_NUM + ") FROM " + TABLE_NAME2, null);

        if (cursor != null && cursor.moveToFirst()) {
            lastGpsNum = cursor.getInt(0);
            cursor.close();
        }

        return lastGpsNum;
    }

    /**
     * Function to get the last id entry in the hiking_data table
     * @param database database you want to access
     * @return last id entry in the table
     */
    // Helper method to get the last id from the database
    public int getLastId(SQLiteDatabase database) {
        int lastId = 0;
        Cursor cursor = database.rawQuery("SELECT MAX(" + KEY_ID + ") FROM " + TABLE_NAME, null);

        if (cursor != null && cursor.moveToFirst()) {
            lastId = cursor.getInt(0);
            cursor.close();
        }

        return lastId;
    }


    /**
     * This function provides a list of GeoPoints linked to a certain hike by id. We extract the longitude
     * and latitude from each entry identified by the same gpsnum to get matching pairs.
     *
     * @param id id of the hike
     * @return list of GeoPoints with gps data
     */
    public List<GeoPoint> getGeoPointsById(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        List<GeoPoint> geoPoints = new ArrayList<>();

        String[] columns = {KEY_GPS_NUM, KEY_LONGITUDE, KEY_LATITUDE};
        String selection = KEY_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = database.query(TABLE_NAME2, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Map<Integer, GeoPoint> geoPointMap = new HashMap<>();

            do {
                int gpsNumIndex = cursor.getColumnIndex(KEY_GPS_NUM);
                int longitudeIndex = cursor.getColumnIndex(KEY_LONGITUDE);
                int latitudeIndex = cursor.getColumnIndex(KEY_LATITUDE);

                if (gpsNumIndex == -1 || longitudeIndex == -1 || latitudeIndex == -1) {
                    Log.w("GeoPoints", "One or more columns not found in cursor.");
                    continue;
                }

                int gpsNum = cursor.getInt(gpsNumIndex);
                double longitude = cursor.getDouble(longitudeIndex);
                double latitude = cursor.getDouble(latitudeIndex);

                if (!geoPointMap.containsKey(gpsNum)) {
                    GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                    geoPointMap.put(gpsNum, geoPoint);
                }
            } while (cursor.moveToNext());

            geoPoints.addAll(geoPointMap.values());
            cursor.close();
        }

        database.close();
        return geoPoints;
    }

    /**
     * This function returns a list of altitude values linked to a certain hike
     * @param id is the id of the hike
     * @return list with altitude values
     */
    public List<Double> getAltitudesById(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        List<Double> altitudeList = new ArrayList<>();

        String[] columns = {KEY_ALTITUDE};
        String selection = KEY_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = database.query(TABLE_NAME2, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int altitudeIndex = cursor.getColumnIndex(KEY_ALTITUDE);

            if (altitudeIndex == -1) {
                Log.w("Altitudes", "Column not found in cursor.");
            } else {
                do {
                    double altitude = cursor.getDouble(altitudeIndex);
                    altitudeList.add(altitude);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        database.close();
        return altitudeList;
    }


    // Method to get hikes from the "hiking_data" table for a specific month
    public Cursor getHikesForMonth(int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Calculate the start and end dates for the specified month
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

        // Define the columns you want to retrieve
        String[] projection = {
                KEY_ID + " AS _id",
                KEY_NAME,
                KEY_DAY,
                KEY_DISTANCE
                // Add other columns as needed
        };

        // Define the WHERE clause to filter by month
        String selection = KEY_DAY + " BETWEEN ? AND ?";
        String[] selectionArgs = {startDate, endDate};

        // Query the "hiking_data" table
        Cursor cursor = db.query(
                TABLE_NAME,       // The table name
                projection,       // The columns to retrieve
                selection,        // Selection (filter by month)
                selectionArgs,    // SelectionArgs
                null,             // GroupBy
                null,             // Having
                KEY_DAY + " DESC" // OrderBy (assuming you want to order by date)
        );

        // Note: If you have additional WHERE clauses, you can append them using "AND" in the selection.

        return cursor;
    }

    public int getIdFromName(String name) {
        SQLiteDatabase database = this.getReadableDatabase();
        int id = 0;

        // Define the columns you want to retrieve
        String[] projection = {
                KEY_ID
                // Add other columns as needed
        };

        // Define the WHERE clause to filter by name
        String selection = KEY_NAME + "=?";
        String[] selectionArgs = {name};

        // Query the "hiking_data" table
        Cursor cursor = database.query(
                TABLE_NAME,       // The table name
                projection,       // The columns to retrieve
                selection,        // Selection (filter by name)
                selectionArgs,    // SelectionArgs
                null,             // GroupBy
                null,             // Having
                null              // OrderBy
        );

        // Check if the cursor has data
        if (cursor != null && cursor.moveToFirst()) {
            // Retrieve the ID from the cursor
            int idIndex = cursor.getColumnIndex(KEY_ID);
            if (idIndex != -1) {
                id = cursor.getInt(idIndex);
            }

            // Close the cursor
            cursor.close();
        }

        // Close the database
        database.close();

        return id;
    }

    public int getStepsDataById(int hikeId) {
        int steps = -1; // Default value if no data is found

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Define the columns to retrieve
            String[] columns = {KEY_STEPS};

            // Define the WHERE clause to filter by hike ID
            String selection = KEY_ID + "=?";
            String[] selectionArgs = {String.valueOf(hikeId)};

            // Query the "hiking_data" table
            cursor = database.query(
                    TABLE_NAME,        // The table name
                    columns,            // The columns to retrieve
                    selection,          // Selection (filter by hike ID)
                    selectionArgs,      // SelectionArgs
                    null,               // GroupBy
                    null,               // Having
                    null                // No specific ordering needed for a single value
            );

            // Check if the cursor is not null and contains data
            if (cursor != null && cursor.moveToFirst()) {
                // Get the steps data from the cursor
                int stepsColumnIndex = cursor.getColumnIndex(KEY_STEPS);
                if (stepsColumnIndex != -1) {
                    steps = cursor.getInt(stepsColumnIndex);
                } else {
                    Log.e("getStepsDataById", "Column KEY_STEPS not found in cursor");
                }
            } else {
                Log.e("getStepsDataById", "Cursor is null or empty");
            }
        } finally {
            // Close the cursor in a finally block to ensure it gets closed
            if (cursor != null) {
                cursor.close();
            }
        }

        // Close the database
        database.close();

        return steps;
    }


    public float getDistanceDataById(int hikeId) {
        float distance = -1; // Default value if no data is found

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Define the columns to retrieve
            String[] columns = {KEY_DISTANCE};

            // Define the WHERE clause to filter by hike ID
            String selection = KEY_ID + "=?";
            String[] selectionArgs = {String.valueOf(hikeId)};

            // Query the "hiking_data" table
            cursor = database.query(
                    TABLE_NAME,        // The table name
                    columns,            // The columns to retrieve
                    selection,          // Selection (filter by hike ID)
                    selectionArgs,      // SelectionArgs
                    null,               // GroupBy
                    null,               // Having
                    null                // No specific ordering needed for a single value
            );

            // Check if the cursor is not null and contains data
            if (cursor != null && cursor.moveToFirst()) {
                // Get the steps data from the cursor
                int stepsColumnIndex = cursor.getColumnIndex(KEY_DISTANCE);
                if (stepsColumnIndex != -1) {
                    distance = cursor.getInt(stepsColumnIndex);
                } else {
                    Log.e("getStepsDataById", "Column KEY_DISTANCE not found in cursor");
                }
            } else {
                Log.e("getStepsDataById", "Cursor is null or empty");
            }
        } finally {
            // Close the cursor in a finally block to ensure it gets closed
            if (cursor != null) {
                cursor.close();
            }
        }

        // Close the database
        database.close();

        return distance;
    }


    public String getNameDataById(int hikeId) {
        String name = "your hike"; // Default value if no data is found

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Define the columns to retrieve
            String[] columns = {KEY_NAME};

            // Define the WHERE clause to filter by hike ID
            String selection = KEY_ID + "=?";
            String[] selectionArgs = {String.valueOf(hikeId)};

            // Query the "hiking_data" table
            cursor = database.query(
                    TABLE_NAME,        // The table name
                    columns,            // The columns to retrieve
                    selection,          // Selection (filter by hike ID)
                    selectionArgs,      // SelectionArgs
                    null,               // GroupBy
                    null,               // Having
                    null                // No specific ordering needed for a single value
            );

            // Check if the cursor is not null and contains data
            if (cursor != null && cursor.moveToFirst()) {
                // Get the steps data from the cursor
                int stepsColumnIndex = cursor.getColumnIndex(KEY_NAME);
                if (stepsColumnIndex != -1) {
                    name = cursor.getString(stepsColumnIndex);
                } else {
                    Log.e("getStepsDataById", "Column KEY_NAME not found in cursor");
                }
            } else {
                Log.e("getStepsDataById", "Cursor is null or empty");
            }
        } finally {
            // Close the cursor in a finally block to ensure it gets closed
            if (cursor != null) {
                cursor.close();
            }
        }

        // Close the database
        database.close();

        return name;
    }

    public List<Double> getAltitudesForHikeById(int hikeId) {
        List<Double> altitudeList = new ArrayList<>();

        // Get the readable database
        SQLiteDatabase database = this.getReadableDatabase();

        try {
            // Define the columns to retrieve
            String[] columns = {KEY_ALTITUDE};

            // Define the WHERE clause to filter by hike ID
            String selection = KEY_ID + "=?";
            String[] selectionArgs = {String.valueOf(hikeId)};

            // Query the "gps_points" table
            Cursor cursor = database.query(
                    TABLE_NAME2,       // The table name
                    columns,           // The columns to retrieve
                    selection,         // Selection (filter by hike ID)
                    selectionArgs,     // SelectionArgs
                    null,              // GroupBy
                    null,              // Having
                    KEY_TIMESTAMP + " ASC" // OrderBy (ascending order of timestamp)
            );

            // Check if the cursor is not null and contains data
            if (cursor != null && cursor.moveToFirst()) {
                // Get the altitude data from the cursor and add it to the list
                int altitudeIndex = cursor.getColumnIndex(KEY_ALTITUDE);
                if (altitudeIndex != -1) {
                    do {
                        double altitude = cursor.getDouble(altitudeIndex);
                        altitudeList.add(altitude);
                    } while (cursor.moveToNext());
                } else {
                    Log.e("getAltitudesForHikeById", "Column KEY_ALTITUDE not found in cursor");
                }
            } else {
                Log.e("getAltitudesForHikeById", "Cursor is null or empty");
            }

            // Close the cursor
            if (cursor != null) {
                cursor.close();
            }
        } finally {
            // Close the database in a finally block to ensure it gets closed
            database.close();
        }

        return altitudeList;
    }





}
