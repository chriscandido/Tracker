package com.upd.contraplus2020.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import com.upd.contraplus2020.utils.LatLng;

import java.util.ArrayList;
import java.util.List;

public class UserDBHandler extends SQLiteOpenHelper {

    private final Context context;

    //----------------------------------------------------------------------------------------------Database name and version
    private static int DB_VERSION = 2;
    private static String DB_NAME = "ConTra";

    //----------------------------------------------------------------------------------------------Table Names
    public static final String TABLE_USERS = "userProfile";
    public static final String TABLE_LOCATIONS = "locations";
    public static final String TABLE_SYMPTOMS = "symptoms";
    public static final String TABLE_BLUETOOTH = "bluetooth";
    public static final String TABLE_CONTACT = "contact";

    //----------------------------------------------------------------------------------------------Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_UNIQUEID = "uniqueId";

    //----------------------------------------------------------------------------------------------Userprofile column names
    private static final String KEY_DATEOFBIRTH = "dateOfBirth";

    private static final String KEY_FIRSTNAME = "firstName";
    private static final String KEY_LASTNAME = "lastName";
    private static final String KEY_AGE = "age";
    private static final String KEY_CONTACT = "contactNumber";
    private static final String KEY_PERMANENT_ADDRESS = "permanentAddress";
    private static final String KEY_MUN_CITY = "municipalityOrCity";
    private static final String KEY_PRESENT_ADDRESS = "presentAddress";
    private static final String KEY_PRESENT_MUN_CITY = "presentMunicipalityOrCity";
    private static final String KEY_ISSUSPECTED_OF_CONTACT = "isSuspectedOfContact";
    private static final String KEY_ISPUI = "isPUI";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_TEST_RESULT = "testResult";

    //----------------------------------------------------------------------------------------------Location column names
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_CREATEDAT = "createdAt";

    //----------------------------------------------------------------------------------------------Symptoms columns names
    private static final String KEY_SYMPTOMS = "symptoms";

    //----------------------------------------------------------------------------------------------Bluetooth Column names
    private static final String KEY_MCADDRESS = "mcaddress";
    private static final String KEY_DISTANCE = "distance";

    //----------------------------------------------------------------------------------------------Manual Contact
    private static final String KEY_NUMBER = "number";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DATE = "date";


    public UserDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public void initializeDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
    }

    //----------------------------------------------------------------------------------------------Create Tables
    @Override
    public void onCreate(SQLiteDatabase db){
        String CREATE_USERPROFILES_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"     //0
                + KEY_UNIQUEID + " TEXT,"                            //1
                + KEY_FIRSTNAME + " TEXT,"                           //2
                + KEY_LASTNAME + " TEXT,"                            //3
                + KEY_AGE + " INTEGER,"                              //4
                + KEY_CONTACT + " TEXT,"                             //5
                + KEY_PERMANENT_ADDRESS + " TEXT,"                   //
                + KEY_MUN_CITY + " TEXT,"                            //7
                + KEY_PRESENT_ADDRESS + " TEXT,"                     //8
                + KEY_PRESENT_MUN_CITY + " TEXT,"                    //9
                + KEY_ISSUSPECTED_OF_CONTACT + " INTEGER,"           //10
                + KEY_ISPUI + " INTEGER,"                            //11
                + KEY_GENDER + " TEXT,"                              //12
                + KEY_TEST_RESULT + " TEXT " + ")";                  //13
        db.execSQL(CREATE_USERPROFILES_TABLE);

        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"     //0
                + KEY_UNIQUEID + " TEXT,"                            //1
                + KEY_LATITUDE + " REAL,"                            //2
                + KEY_LONGITUDE + " REAL,"                           //3
                + KEY_CREATEDAT + " REAL " + ")";                    //4
        db.execSQL(CREATE_LOCATIONS_TABLE);

        String CREATE_SYMPTOMS_TABLE = "CREATE TABLE " + TABLE_SYMPTOMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"     //0
                + KEY_UNIQUEID + " TEXT,"                            //1
                + KEY_SYMPTOMS + " TEXT,"                            //2
                + KEY_CREATEDAT + " REAL " + ")";                    //3
        db.execSQL(CREATE_SYMPTOMS_TABLE);

        String CREATE_BLUETOOTH_TABLE = "CREATE TABLE " + TABLE_BLUETOOTH + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"     //0
                + KEY_UNIQUEID + " TEXT,"                            //1
                + KEY_MCADDRESS + " TEXT,"                           //2
                + KEY_DISTANCE + " REAL,"                            //3
                + KEY_CREATEDAT + " REAL " + ")";                    //4
        db.execSQL(CREATE_BLUETOOTH_TABLE);

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACT + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"     //0
                + KEY_UNIQUEID + " TEXT,"                            //1
                + KEY_NUMBER + " TEXT,"                              //2
                + KEY_ADDRESS + " TEXT,"                             //3
                + KEY_DATE + " TEXT " + ")";                         //4
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    private static final int NOT_AVAILABLE = -10000;

    private static final String DB_ALTER_TABLE_USERS_TO_V2 = "ALTER TABLE " + TABLE_USERS
            + " ADD COLUMN " + KEY_TEST_RESULT + " TEXT DEFAULT " + NOT_AVAILABLE +";";
    private static final String DB_ALTER_TABLE_LOCATIONS_TO_V2 = "ALTER TABLE " + TABLE_LOCATIONS
            + " ADD COLUMN " + KEY_CREATEDAT + " REAL DEFAULT " + NOT_AVAILABLE +";";
    private static final String DB_ALTER_TABLE_SYMPTOMS_TO_V2 = "ALTER TABLE " + TABLE_SYMPTOMS
            + " ADD COLUMN " + KEY_CREATEDAT + " REAL DEFAULT " + NOT_AVAILABLE +";";
    private static final String DB_ALTER_TABLE_BLUETOOTH_TO_V2 = "ALTER TABLE " + TABLE_BLUETOOTH
            + " ADD COLUMN " + KEY_CREATEDAT + " REAL DEFAULT " + NOT_AVAILABLE +";";
    private static final String DB_ALTER_TABLE_CONTACTS_TO_V2 = "ALTER TABLE " + TABLE_CONTACT
            + " ADD COLUMN " + KEY_DATE + " TEXT DEFAULT " + NOT_AVAILABLE +";";

    //----------------------------------------------------------------------------------------------Upgrading Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        switch (oldVersion){
            case 1:
                db.execSQL(DB_ALTER_TABLE_USERS_TO_V2);
                db.execSQL(DB_ALTER_TABLE_LOCATIONS_TO_V2);
                db.execSQL(DB_ALTER_TABLE_SYMPTOMS_TO_V2);
                db.execSQL(DB_ALTER_TABLE_BLUETOOTH_TO_V2);
                db.execSQL(DB_ALTER_TABLE_CONTACTS_TO_V2);
        }
        Log.w("ConTra+", "[#] DatabaseHandler.java - onUpgrade: DB upgraded to version " + newVersion);
    }

    //----------------------------------------------------------------------------------------------add location of user
    public void addLocation(UserDBLocationHelper userDBLocationHelper, String uniqueId){
        SQLiteDatabase db = this.getWritableDatabase();
        Location loc = userDBLocationHelper.getLocation();

        ContentValues locValues = new ContentValues();
        locValues.put(KEY_UNIQUEID, uniqueId);
        locValues.put(KEY_LATITUDE, loc.getLatitude());
        locValues.put(KEY_LONGITUDE, loc.getLongitude());
        locValues.put(KEY_CREATEDAT, loc.getTime());

        db.beginTransaction();
        db.insert(TABLE_LOCATIONS, null, locValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //----------------------------------------------------------------------------------------------add user profile
    public void addUser (UserDBHelper userDBHelper){
        SQLiteDatabase db = this.getWritableDatabase();

        String uid = userDBHelper.getUnique_Id();
        String firstName = userDBHelper.getFirstName();
        String lastName = userDBHelper.getLastName();
        Integer age = userDBHelper.getAge();
        String contactNumber = userDBHelper.getContactNumber();
        String permanentAddress = userDBHelper.getPermanentAddress();
        String municipalityOrCity = userDBHelper.getMunicipalityOrCity();
        String presentAddress = userDBHelper.getPresentAddress();
        String present_municipalityOrCity = userDBHelper.getPresent_municipalityOrCity();
        Integer isSuspectedOfContact = userDBHelper.getIsSuspectedOfContact();
        Integer isPUI = userDBHelper.getIsPUI();
        String gender = userDBHelper.getGender();
        String testResult = userDBHelper.getTestResult();

        ContentValues userValues = new ContentValues();
        userValues.put(KEY_UNIQUEID, uid);
        userValues.put(KEY_FIRSTNAME, firstName);
        userValues.put(KEY_LASTNAME, lastName);
        userValues.put(KEY_AGE, age);
        userValues.put(KEY_CONTACT, contactNumber);
        userValues.put(KEY_PERMANENT_ADDRESS, permanentAddress);
        userValues.put(KEY_MUN_CITY, municipalityOrCity);
        userValues.put(KEY_PRESENT_ADDRESS, presentAddress);
        userValues.put(KEY_PRESENT_MUN_CITY, present_municipalityOrCity);
        userValues.put(KEY_ISSUSPECTED_OF_CONTACT, isSuspectedOfContact);
        userValues.put(KEY_ISPUI, isPUI);
        userValues.put(KEY_GENDER, gender);
        userValues.put(KEY_TEST_RESULT, testResult);

        db.beginTransaction();
        db.insert(TABLE_USERS, null, userValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //----------------------------------------------------------------------------------------------add symptoms of the user
    public void addSymptoms (String symptoms, Integer time){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues symptomValues = new ContentValues();
        symptomValues.put(KEY_SYMPTOMS, symptoms);
        symptomValues.put(KEY_CREATEDAT, time);

        db.beginTransaction();
        db.insert(TABLE_SYMPTOMS, null, symptomValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //----------------------------------------------------------------------------------------------add bluetooth connections
    public void addScannedBluetooth (UserDBBluetoothHelper userDBBluetoothHelper, String uniqueId){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues bluetoothValues = new ContentValues();
        bluetoothValues.put(KEY_UNIQUEID, uniqueId);
        bluetoothValues.put(KEY_MCADDRESS, userDBBluetoothHelper.getMcaddress());
        bluetoothValues.put(KEY_DISTANCE, userDBBluetoothHelper.getDistance());
        bluetoothValues.put(KEY_CREATEDAT, userDBBluetoothHelper.getCreatedAd());

        db.beginTransaction();
        db.insert(TABLE_BLUETOOTH, null, bluetoothValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //----------------------------------------------------------------------------------------------add contacts of the user
    public void addContact(String number, String address, String date, String uniqueId){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contactValues = new ContentValues();
        contactValues.put(KEY_UNIQUEID, uniqueId);
        contactValues.put(KEY_NUMBER, number);
        contactValues.put(KEY_ADDRESS, address);
        contactValues.put(KEY_DATE, date);

        db.beginTransaction();
        db.insert(TABLE_CONTACT, null, contactValues);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    //----------------------------------------------------------------------------------------------get single location using id
    public UserDBLocationHelper getLocation(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        UserDBLocationHelper userDBLocationHelper = null;

        Cursor cursor = db.query(TABLE_LOCATIONS, new String[] {
                KEY_ID,
                KEY_UNIQUEID,
                KEY_LATITUDE,
                KEY_LONGITUDE,
                KEY_CREATEDAT}, KEY_ID + " = ?",
                new String[] {String.valueOf(id)}, null,null,null,null);

        if (cursor != null){
            cursor.moveToFirst();
            Location loc = new Location("DB");
            loc.setLatitude(cursor.getDouble(2));
            loc.setLongitude(cursor.getDouble(3));
            loc.setTime(cursor.getLong(4));

            userDBLocationHelper = new UserDBLocationHelper(loc);
            cursor.close();

        }
        return userDBLocationHelper;
    }

    //----------------------------------------------------------------------------------------------get list of location using id
    public List<LatLng> getLatLngList(long Id){
        List<LatLng> latLngList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + KEY_LATITUDE + "," + KEY_LONGITUDE + " FROM " + TABLE_LOCATIONS
                + " WHERE " + KEY_ID + " = " + Id + " ORDER BY " + KEY_ID;
        Log.v("SELECT: ", query);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    LatLng latLng = new LatLng();
                    latLng.latitude = cursor.getDouble(0);
                    latLng.longitude = cursor.getDouble(1);
                    latLngList.add(latLng);
                } while (cursor.moveToNext());
            } cursor.close();
        }
        return latLngList;
    }

    //----------------------------------------------------------------------------------------------get last id from user profile
    public int getLastUserId(){
        SQLiteDatabase db = this.getReadableDatabase();
        String QUERY ="SELECT " + KEY_ID + " FROM " + TABLE_USERS + " ORDER BY " + KEY_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(QUERY, null);

        int id = 0;
        if (cursor != null){
            if(cursor.moveToFirst()){
                id =cursor.getInt(0);
            } else
                Log.v("DATABASE", "[#] DatabaseHandler.java - User Table is Empty");
            cursor.close();
        } return id;
    }

    //----------------------------------------------------------------------------------------------get last id
    public int getLastId(){
        SQLiteDatabase db = this.getReadableDatabase();
        String QUERY ="SELECT " + KEY_ID + " FROM " + TABLE_LOCATIONS + " ORDER BY " + KEY_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(QUERY, null);

        int id = 0;
        if (cursor != null){
            if(cursor.moveToFirst()){
                id =cursor.getInt(0);
            } else
                Log.v("DATABASE", "[#] DatabaseHandler.java - Location Table is Empty");
            cursor.close();
        } return id;
    }

    //----------------------------------------------------------------------------------------------get last location
    public UserDBLocationHelper getLastLocation(){
        SQLiteDatabase db = this.getWritableDatabase();
        UserDBLocationHelper userDBLocationHelper = null;

        String QUERY ="SELECT "
                + KEY_ID + ","
                + KEY_LATITUDE + ","
                + KEY_LONGITUDE + ","
                + KEY_CREATEDAT + " FROM "
                + TABLE_LOCATIONS + " ORDER BY "
                + KEY_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(QUERY, null);

        if (cursor != null){
            cursor.moveToFirst();
            Location loc = new Location("DB");
            loc.setLatitude(cursor.getDouble(1));
            loc.setLongitude(cursor.getDouble(2));
            loc.setTime(cursor.getLong(3));
            Log.d("DATABASE", "[#] DatabaseHandler.java - LOCATION: " + cursor.getDouble(1)
                    +  " && " + cursor.getDouble(2));
            userDBLocationHelper = new UserDBLocationHelper(loc);
            cursor.close();
        }
        return userDBLocationHelper;
    }

    //----------------------------------------------------------------------------------------------get last user profile
    public UserDBHelper getLastUserProfile() {
        SQLiteDatabase db = this.getWritableDatabase();
        UserDBHelper userDBHelper = null;

        String QUERY = "SELECT "
                + KEY_ID + ","
                + KEY_UNIQUEID + ","
                + KEY_FIRSTNAME + ","
                + KEY_LASTNAME + ","
                + KEY_AGE + ","
                + KEY_CONTACT + ","
                + KEY_PERMANENT_ADDRESS + ","
                + KEY_MUN_CITY + ","
                + KEY_PRESENT_ADDRESS + ","
                + KEY_PRESENT_MUN_CITY + ","
                + KEY_ISSUSPECTED_OF_CONTACT + ","
                + KEY_ISPUI + ","
                + KEY_GENDER + ","
                + KEY_TEST_RESULT + " FROM "
                + TABLE_USERS + " ORDER BY "
                + KEY_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(QUERY, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String uniqueId = cursor.getString(1);
                String firstName = cursor.getString(2);
                String lastName = cursor.getString(3);
                Integer age = cursor.getInt(4);
                String contactNumber = cursor.getString(5);
                String permanentAddress = cursor.getString(6);
                String municipalityOrCity = cursor.getString(7);
                String presentAddress = cursor.getString(8);
                String presentMunicipalityOrCity = cursor.getString(9);
                Integer isSuspectedofContact = cursor.getInt(10);
                Integer isPUI = cursor.getInt(11);
                String gender = cursor.getString(12);
                String testResult = cursor.getString(13);
                userDBHelper = new UserDBHelper(uniqueId,
                        firstName,
                        lastName,
                        age,
                        contactNumber,
                        permanentAddress,
                        municipalityOrCity,
                        presentAddress,
                        presentMunicipalityOrCity,
                        isSuspectedofContact,
                        isPUI,
                        gender,
                        testResult);
                Log.v("DATABASE", "[#] DatabaseHandler.java - USER PROFILE: " + "\nName: " + firstName + " " + lastName +
                        "\nAge: " + age + "\nContact Number: " + contactNumber + "\nAddress: " + presentAddress + "," + presentMunicipalityOrCity +
                        "\nTest Result: " + testResult);
            }
            else
                Log.v("DATABASE", "[#] DatabaseHandler.java - UserProfile Table is Empty");
            cursor.close();
        }
        return userDBHelper;
    }

    //----------------------------------------------------------------------------------------------get last scanned Bluetooth
    public UserDBBluetoothHelper getLastScannedBluetooth() {
        SQLiteDatabase db = this.getWritableDatabase();
        UserDBBluetoothHelper userDBBluetoothHelper = null;

        String QUERY = "SELECT "
                + KEY_ID + ","
                + KEY_UNIQUEID + ","
                + KEY_MCADDRESS + ","
                + KEY_DISTANCE + ","
                + KEY_CREATEDAT + " FROM "
                + TABLE_BLUETOOTH + " ORDER BY "
                + KEY_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(QUERY, null);

        if (cursor != null) {
            cursor.moveToFirst();
            String mcaddress = cursor.getString(2);
            Float distance = cursor.getFloat(3);
            Integer createdAt = cursor.getInt(4);
            Log.d("DATABASE", "[#] DatabaseHandler.java - BLUETOOTH CONNECTION: " + cursor.getString(2)
                    + " && " + cursor.getFloat(3));
            userDBBluetoothHelper = new UserDBBluetoothHelper(mcaddress, distance, createdAt);
        } else {
            Log.v("DATABASE", "[#] DatabaseHandler.java - Bluetooth Connection Table is Empty");
            cursor.close();
        } return userDBBluetoothHelper;

    }

    //----------------------------------------------------------------------------------------------get last symptoms
    public UserDBSymptoms getLastSymptoms(){
        SQLiteDatabase db = this.getWritableDatabase();
        UserDBSymptoms userDBSymptoms = null;

        String QUERY = "SELECT "
                + KEY_SYMPTOMS + ","
                + KEY_CREATEDAT + " FROM "
                + TABLE_SYMPTOMS + " ORDER BY "
                + KEY_ID + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(QUERY, null);

        if (cursor != null){
            if (cursor.moveToFirst()){
                String symptoms = cursor.getString(0);
                Integer date = cursor.getInt(1);
                userDBSymptoms = new UserDBSymptoms(symptoms, date);
                Log.d("DATABASE", "[#] DatabaseHandler.java - SYMPTOMS: " + userDBSymptoms.getSymptoms()
                + " && " + "TIME: " + userDBSymptoms.getDate());
            } else
                Log.v("DATABASE", "[#] DatabaseHandler.java - Symptoms Table is Empty");
            cursor.close();
        }
        return userDBSymptoms;
    }

    //----------------------------------------------------------------------------------------------get last contact of the user
    public UserDBContactHelper getLastContact(){
        SQLiteDatabase db = this.getWritableDatabase();
        UserDBContactHelper userDBContactHelper = null;

        String QUERY = "SELECT "
                + KEY_NUMBER + ","
                + KEY_ADDRESS + ","
                + KEY_DATE + " FROM "
                + TABLE_CONTACT + " ORDER BY "
                + KEY_ID + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(QUERY, null);

        if (cursor != null){
            if (cursor.moveToFirst()){
                String number = cursor.getString(0);
                String address = cursor.getString(1);
                String date = cursor.getString(2);
                userDBContactHelper = new UserDBContactHelper(number, address, date);
                Log.d("DATABASE", "[#] DatabaseHandler.java - CONTACT NUMBER: " + userDBContactHelper.getNumber()
                        + " && " + "TIME: " + userDBContactHelper.getDate());
            } else
                Log.v("DATABASE", "[#] DatabaseHandler.java - Contacts Table is Empty");
            cursor.close();
        }
        return userDBContactHelper;
    }

    //----------------------------------------------------------------------------------------------get unique id of the user
    public String getKeyUniqueid(){
        SQLiteDatabase db = this.getWritableDatabase();
        String QUERY = "SELECT " + KEY_UNIQUEID + " FROM " + TABLE_USERS + " ORDER BY " + KEY_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(QUERY, null);

        String result = "";

        if (cursor != null){
            if(cursor.moveToFirst()){
                result = cursor.getString(0);
                Log.d("DATABASE", "[#] DatabaseHandler.java - UNIQUE ID: " + result);
            } else
                Log.v("DATABASE", "[#] DatabaseHandler.java - Location Table is Empty");
            cursor.close();
        } return result;
    }

    //----------------------------------------------------------------------------------------------check unique id of the user
    public boolean checkUniqueId (String uniqueId){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[] {
                        KEY_ID }, KEY_UNIQUEID + " = ?",
                new String[] { uniqueId }, null,null,null,null);

        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0){
            return true;
        }
        return false;
    }

    //----------------------------------------------------------------------------------------------update user profile
    public boolean updateUser (UserDBHelper userDBHelper, String lastID) {
        SQLiteDatabase db = this.getWritableDatabase();

        String firstName = userDBHelper.getFirstName();
        String lastName = userDBHelper.getLastName();
        Integer age = userDBHelper.getAge();
        String contactNumber = userDBHelper.getContactNumber();
        String permanentAddress = userDBHelper.getPermanentAddress();
        String municipalityOrCity = userDBHelper.getMunicipalityOrCity();
        String presentAddress = userDBHelper.getPresentAddress();
        String present_municipalityOrCity = userDBHelper.getPresent_municipalityOrCity();
        Integer isSuspectedOfContact = userDBHelper.getIsSuspectedOfContact();
        Integer isPUI = userDBHelper.getIsPUI();
        String gender = userDBHelper.getGender();
        String testResult = userDBHelper.getTestResult();

        ContentValues userValues = new ContentValues();
        userValues.put(KEY_FIRSTNAME, firstName);
        userValues.put(KEY_LASTNAME, lastName);
        userValues.put(KEY_AGE, age);
        userValues.put(KEY_CONTACT, contactNumber);
        userValues.put(KEY_PERMANENT_ADDRESS, permanentAddress);
        userValues.put(KEY_MUN_CITY, municipalityOrCity);
        userValues.put(KEY_PRESENT_ADDRESS, presentAddress);
        userValues.put(KEY_PRESENT_MUN_CITY, present_municipalityOrCity);
        userValues.put(KEY_ISSUSPECTED_OF_CONTACT, isSuspectedOfContact);
        userValues.put(KEY_ISPUI, isPUI);
        userValues.put(KEY_GENDER, gender);
        userValues.put(KEY_TEST_RESULT, testResult);

        try {
            db.beginTransaction();
            db.update(TABLE_USERS, userValues, "id = ?", new String[] { lastID });
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            return true;

        } catch (SQLException exception) {
            db.close();
            return false;

        }
    }

    //----------------------------------------------------------------------------------------------delete table
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, null, null);
        db.delete(TABLE_LOCATIONS, null, null);
        db.close();
    }
}
