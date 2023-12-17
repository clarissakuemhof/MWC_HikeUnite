package com.example.stepappv4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


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


    /**
     * Constructor for StepAppOpenHelper
     * use onUpgrade to delete tables and then switch back to onCreate
     *
     * @param context context of current app
     */
    public StepAppOpenHelper (Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.onCreate(getWritableDatabase());
        //this.onUpgrade(getWritableDatabase(),0,0);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TABLE_SQL);
        sqLiteDatabase.execSQL(CREATE_TABLE_GPS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(sqLiteDatabase);
    }


    /**
     * Method to create hiking_data table if it not already exists
     * Consists of: KEY_ID = unique identifier of row
     *              KEY_DAY = day of the hike
     *              KEY_NAME = name of the hike
     *              KEY_DISTANCE = total walked distance during hike
     *              KEY_STEPS = total steps during hike
     */
    public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +"  (" +
            KEY_ID + " INTEGER PRIMARY KEY, " +
            KEY_DAY + " TEXT, " +
            KEY_NAME + " TEXT, " +
            KEY_DISTANCE + " REAL, " +
            KEY_STEPS + " INTEGER);";

    /**
     * Method to create gps_points table if it not already exists
     * Consists of: KEY_GPS_NUM = unique identifier of row
     *              KEY_TIMESTAMP = timestamp of location tracking
     *              KEY_LONGITUDE = longitude value
     *              KEY_LATITUDE = latitude value
     *              KEY_ALTITUDE = altitude value
     */
    public static final String CREATE_TABLE_GPS = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME2 + " (" +
            KEY_GPS_NUM + " INTEGER PRIMARY KEY, " +
            //KEY_DAY + " TEXT, " +
            //KEY_HOUR + " TEXT, " +
            KEY_TIMESTAMP + " TEXT, " +
            KEY_ID + " INTEGER, " +
            KEY_LONGITUDE + " REAL, " +
            KEY_LATITUDE + " REAL, " +
            KEY_ALTITUDE + " REAL);";


    /**
     * Method to create new hike in hiking_data table
     * automatically adds unique id and name to hike
     */
    public void insertHikeData() {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        int lastId = getLastId(database);

        int newId = lastId + 1;
        String name = "Your Hike"+ newId;

        String timestamp = getCurrentTimestamp();

        values.put(KEY_ID, newId);
        values.put(KEY_DAY, getCurrentDate());
        values.put(KEY_DISTANCE, 0);
        values.put(KEY_STEPS, 0);
        values.put(KEY_NAME, name);

        database.insert(TABLE_NAME, null, values);
        Log.d("DatabaseInsert", "Inserted GPS data: "
                + ", ID: " + newId
                + ", Distance: " + 0
                + ", steps: " + 0
                + ", Timestamp: " + timestamp);
        database.close();
    }

    /**
     * This method is used to update the steps value of a certain hike
     *
     * @param id hike you want to update
     * @param newSteps new value for steps
     */
    public void updateHikeData(int id, int newSteps) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_STEPS, newSteps);

        String whereClause = KEY_ID + "=?";
        String[] whereArgs = {String.valueOf(id)};

        database.update(TABLE_NAME, values, whereClause, whereArgs);

        Log.d("DatabaseUpdate", "Updated Hike Data - ID: " + id
                + ", New Steps: " + newSteps);

        database.close();
    }

    /**
     * This method is used to update the distance value of a certain hike
     *
     * @param id hike you want to update
     * @param distance new distance value
     */
    public void updateHikeDistance(int id, float distance) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_DISTANCE, distance);

        String whereClause = KEY_ID + "=?";
        String[] whereArgs = {String.valueOf(id)};

        database.update(TABLE_NAME, values, whereClause, whereArgs);

        Log.d("DatabaseUpdate", "Updated Hike Distance - ID: " + id
                + ", New Distance: " + distance);

        database.close();
    }

    /**
     * Method to save GeoData in the gps_point table
     *
     * @param longitude longitude value of location
     * @param latitude latitude value of location
     * @param altitude altitude value of location
     * @param id id of hike that is connected to location data
     */
    public void insertGPSData(double longitude, double latitude, double altitude, int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Get the last gpsnum from the database
        int lastGpsNum = getLastGpsNum(database);

        // Increment the last gpsnum to assign a new one
        int newGpsNum = lastGpsNum + 1;

        // Get current timestamp
        String timestamp = getCurrentTimestamp();

        values.put(KEY_GPS_NUM, newGpsNum);
        //values.put(KEY_DAY, getCurrentDate());
        //values.put(KEY_HOUR, getCurrentHour());
        values.put(KEY_TIMESTAMP, timestamp);
        values.put(KEY_ID, id);
        values.put(KEY_LONGITUDE, longitude);
        values.put(KEY_LATITUDE, latitude);
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

    /**
     * Function to get the last gpsnum entry in the gps_points table
     *
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
     *
     * @param database database you want to access
     * @return last id entry in the table
     */
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
     * and latitude from each entry identified by the same gpssum to get matching pairs.
     *
     * @param id id of the hike
     * @return list of GeoPoints with gps data
     */
    public List<GeoPoint> getGeoPointsById(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        List<GeoPoint> geoPoints = new ArrayList<>();

        String[] columns = {KEY_GPS_NUM, KEY_LONGITUDE, KEY_LATITUDE, KEY_ALTITUDE};
        String selection = KEY_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = database.query(TABLE_NAME2, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Map<Integer, GeoPoint> geoPointMap = new HashMap<>();
            int maxGpsNum = -1;

            do {
                int gpsNumIndex = cursor.getColumnIndex(KEY_GPS_NUM);
                int longitudeIndex = cursor.getColumnIndex(KEY_LONGITUDE);
                int latitudeIndex = cursor.getColumnIndex(KEY_LATITUDE);
                int altitudeIndex = cursor.getColumnIndex(KEY_ALTITUDE);

                if (gpsNumIndex == -1 || longitudeIndex == -1 || latitudeIndex == -1 || altitudeIndex == -1) {
                    Log.w("GeoPoints", "One or more columns not found in cursor.");
                    continue;
                }

                int gpsNum = cursor.getInt(gpsNumIndex);
                double longitude = cursor.getDouble(longitudeIndex);
                double latitude = cursor.getDouble(latitudeIndex);
                double altitude = cursor.getDouble(altitudeIndex);

                GeoPoint geoPoint = new GeoPoint(latitude, longitude, altitude);
                geoPointMap.put(gpsNum, geoPoint);

                maxGpsNum = Math.max(maxGpsNum, gpsNum);
            } while (cursor.moveToNext());

            for (int i = 0; i <= maxGpsNum; i++) {
                if (geoPointMap.containsKey(i)) {
                    geoPoints.add(geoPointMap.get(i));
                }
            }

            cursor.close();
        }
        database.close();

        return geoPoints;
    }

    /**
     * This function returns a list of altitude values linked to a certain hike
     *
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

    /**
     * This method returns a cursor with all the hikes of a certain month. Used to display the hikes in the history fragment
     *
     * @param month month for hike display
     * @param year year for hike display
     * @return cursor with all hike data of this month
     */
    public Cursor getHikesForMonth(int month, int year) {
        SQLiteDatabase db = this.getReadableDatabase();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());


        String[] projection = {
                KEY_ID + " AS _id",
                KEY_NAME,
                KEY_DAY,
                KEY_DISTANCE

        };

        String selection = KEY_DAY + " BETWEEN ? AND ?";
        String[] selectionArgs = {startDate, endDate};

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                KEY_DAY + " DESC"
        );

        return cursor;
    }


    /**
     * This method returns the step count of a certain hike from the hiking_data table
     *
     * @param hikeId identifies the hike
     * @return stepcount of this hike
     */
    public int getStepsDataById(int hikeId) {
        int steps = -1;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] columns = {KEY_STEPS};

            String selection = KEY_ID + "=?";
            String[] selectionArgs = {String.valueOf(hikeId)};

            cursor = database.query(
                    TABLE_NAME,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
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
            if (cursor != null) {
                cursor.close();
            }
        }

        database.close();

        return steps;
    }

    /**
     * This method returns the name of a hike based on the id
     * Sets default value if no name is found
     *
     * @param hikeId identifies the hike
     * @return name of the hike
     */
    public String getNameDataById(int hikeId) {
        String name = "your hike";

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            String[] columns = {KEY_NAME};

            String selection = KEY_ID + "=?";
            String[] selectionArgs = {String.valueOf(hikeId)};

            cursor = database.query(
                    TABLE_NAME,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
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
            if (cursor != null) {
                cursor.close();
            }
        }
        database.close();

        return name;
    }

    /**
     * This method gets all timestamps of a certain hike in the gps_points table
     * identified by id
     *
     * @param id used to identify hike
     * @return list with all timestamps
     */
    public List<String> getTimestampsById(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        List<String> timestamps = new ArrayList<>();

        String[] columns = {KEY_TIMESTAMP};
        String selection = KEY_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = database.query(TABLE_NAME2, columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int timestampIndex = cursor.getColumnIndex(KEY_TIMESTAMP);

                if (timestampIndex != -1) {
                    String timestamp = cursor.getString(timestampIndex);
                    timestamps.add(timestamp);
                } else {
                    Log.w("Timestamps", "Column KEY_TIMESTAMP not found in cursor.");
                }
            } while (cursor.moveToNext());

            cursor.close();
        }

        database.close();
        return timestamps;
    }


    /**
     * This method calculates the duration of a hike based on the first and the last timestamp of this hike
     * the hike is identified by the id and uses getTimestampsById to create list of timestamps
     *
     * @param id identifies hike
     * @return duration of hike in hrs and minutes
     */
    public String calculateDuration(int id) {
        List<String> timestamps = getTimestampsById(id);

        if (timestamps.size() >= 2) {
            String firstTimestamp = timestamps.get(0);
            String lastTimestamp = timestamps.get(timestamps.size() - 1);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date firstDate = dateFormat.parse(firstTimestamp);
                Date lastDate = dateFormat.parse(lastTimestamp);

                assert lastDate != null;
                assert firstDate != null;
                long timeDifference = lastDate.getTime() - firstDate.getTime();

                long hours = TimeUnit.MILLISECONDS.toHours(timeDifference);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference) % 60;

                return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return "Null";
    }

    /**
     * Function to count all hikes in the hiking_data table
     *
     * @return count of hikes
     */
    public int getCountOfHikes() {

        try (SQLiteDatabase database = this.getReadableDatabase()) {

            long count = DatabaseUtils.queryNumEntries(database, TABLE_NAME);
            return (int) count;
        }
    }

    /**
     * Function to sum up all distance values in the hiking_data table
     * checks if cursor is not null and if not sums up steps and returns sum
     *
     * @return sum of total distance
     */
    public double getTotalDistance() {
        SQLiteDatabase database = this.getReadableDatabase();
        double totalDistance = 0;

        try {
            String[] columns = { "SUM(" + KEY_DISTANCE + ")" };
            Cursor cursor = database.query(
                    TABLE_NAME,
                    columns,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            if (cursor != null && cursor.moveToFirst()) {
                int distanceColumnIndex = cursor.getColumnIndex("SUM(" + KEY_DISTANCE + ")");
                if (distanceColumnIndex != -1) {
                    totalDistance = cursor.getDouble(distanceColumnIndex);
                } else {
                    Log.e("getTotalDistance", "Column SUM(KEY_DISTANCE) not found in cursor");
                }
            } else {
                Log.e("getTotalDistance", "Cursor is null or empty");
            }

            if (cursor != null) {
                cursor.close();
            }
        } finally {
            database.close();
        }

        return totalDistance;
    }

    /**
     * Function to sum up all steps in the hiking_data table
     * checks if cursor is not null and if not sums up steps and returns sum
     *
     * @return sum of all steps
     */
    public int getTotalSteps() {
        SQLiteDatabase database = this.getReadableDatabase();
        int totalSteps = 0;

        try {
            String[] columns = { "SUM(" + KEY_STEPS + ")" };
            Cursor cursor = database.query(
                    TABLE_NAME,
                    columns,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            // Check if the cursor is not null and contains data
            if (cursor != null && cursor.moveToFirst()) {
                // Get the sum of steps from the cursor
                int stepsColumnIndex = cursor.getColumnIndex("SUM(" + KEY_STEPS + ")");
                if (stepsColumnIndex != -1) {
                    totalSteps = cursor.getInt(stepsColumnIndex);
                } else {
                    Log.e("getTotalSteps", "Column SUM(KEY_STEPS) not found in cursor");
                }
            } else {
                Log.e("getTotalSteps", "Cursor is null or empty");
            }

            if (cursor != null) {
                cursor.close();
            }
        } finally {
            database.close();
        }

        return totalSteps;
    }

    /**
     * Gets the current date in the format "yyyy-MM-dd".
     *
     * @return The current date in the format "yyyy-MM-dd".
     */
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    /**
     * Gets the current hour in the 24-hour format "HH:mm".
     *
     * @return The current hour in the 24-hour format "HH:mm".
     */
    private String getCurrentHour() {
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return hourFormat.format(Calendar.getInstance().getTime());
    }

    /**
     * Gets the current timestamp in the format "yyyy-MM-dd HH:mm:ss".
     *
     * @return The current timestamp in the format "yyyy-MM-dd HH:mm:ss".
     */
    private String getCurrentTimestamp() {
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return timestampFormat.format(Calendar.getInstance().getTime());
    }



}
