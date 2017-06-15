package com.example.lisahabermehl.calendar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.*;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Array;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * I used the following sample code to make this work:
 * https://developers.google.com/google-apps/calendar/quickstart/android
 */

public class GoogleCalendarTest extends Activity implements EasyPermissions.PermissionCallbacks {
    GoogleAccountCredential mCredential;
    TextView mOutputText;
    ProgressDialog mProgress;
    MyCalendarDbHelper myCalendarDbHelper;
    private MyCalendarAdapter myCalendarAdapter;
    Context context;
    private ListView listView;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_calendar_test);

        mOutputText = (TextView) findViewById(R.id.mOutputText);
        listView = (ListView) findViewById(R.id.list_calendar);
        myCalendarDbHelper = new MyCalendarDbHelper(this);

        // let user know that app is fetching data
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Un momento ...");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        // get the date that user selected
        String date = getIntent().getExtras().getString("date");
        getResultsFromApi(date);
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi(String date) {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount(date);
        } else if (! isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            // execute the AsyncTask and give date
            new MakeRequestTask(mCredential).execute(date);
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount(String date) {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi(date);
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                GoogleCalendarTest.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<String, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(String... params) {
            try {
//                List<MyCalendarObject> result = getDataFromApi(params[0]);
                return getDataFromApi(params[0]);
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        // where all the data comes from, try to put these in an SQLite database?
        private List<String> getDataFromApi(String date) throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());

//            List<MyCalendarObject> myCalendarObject = new ArrayList<MyCalendarObject>();
            List<String> eventStrings = new ArrayList<>();

            Events events = mService.events().list("primary")
                    .setMaxResults(30)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            // list with all the events
            List<Event> items = events.getItems();


            // gonna try to get a specific date here
            for (Event event : items) {

                // get the date
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date original = new Date(event.getStart().getDateTime().getValue());
                String dateStart = dateFormat.format(original);

                // get the time
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                String timeStart = timeFormat.format(original);

                // get the end date
                Date originalEnd = new Date(event.getEnd().getDateTime().getValue());
                String dateEnd = dateFormat.format(originalEnd);
                String timeEnd = timeFormat.format(originalEnd);

                // what is the activity about
                String activity = event.getSummary();

                // date to compare with
                String dateCompare = date;

                Log.d(String.valueOf(dateStart), "datum2");
                Log.d(String.valueOf(dateCompare), "datum5");

                if (dateStart.equals(dateCompare)) {
                    eventStrings.add(String.format("%s_%s_%s_%s", activity, dateStart, timeStart, timeEnd));

//                     add the above to DB
                    SQLiteDatabase db = myCalendarDbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(MyCalendarTable.CalendarEntry.COL_CAL_TITLE, activity);
                    values.put(MyCalendarTable.CalendarEntry.COL_CAL_DATE, dateStart);
                    values.put(MyCalendarTable.CalendarEntry.COL_CAL_START, timeStart);
                    values.put(MyCalendarTable.CalendarEntry.COL_CAL_END, timeEnd);
                    Log.d("ACTIVITY", activity);

                    // add new values to table
                    db.insertWithOnConflict(MyCalendarTable.CalendarEntry.TABLE,
                            null,
                            values,
                            SQLiteDatabase.CONFLICT_REPLACE);
                    db.close();
                }
            }
            return eventStrings;
        }

        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();

            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
//                // so this is where the data from above will be printed on the screen
//                // have to find a way to send this information to an xml file
//                // to update the information on the screen / to update the information of a specific day

//                String one = TextUtils.join("\n", output);


                mOutputText.setText(TextUtils.join("\n", output));

//                    // add the above to DB
//                    SQLiteDatabase db = myCalendarDbHelper.getWritableDatabase();
//
//                    ContentValues values = new ContentValues();
//                    values.put(MyCalendarTable.CalendarEntry.COL_CAL_TITLE, activity);
//                    values.put(MyCalendarTable.CalendarEntry.COL_CAL_DATE, dateStart);
//                    values.put(MyCalendarTable.CalendarEntry.COL_CAL_START, timeStart);
//                    values.put(MyCalendarTable.CalendarEntry.COL_CAL_END, timeEnd);
//                    Log.d("ACTIVITY", activity);
//
//                    // add new values to table
//                    db.insertWithOnConflict(MyCalendarTable.CalendarEntry.TABLE,
//                            null,
//                            values,
//                            SQLiteDatabase.CONFLICT_REPLACE);
//                    db.close();

//                if (myCalendarAdapter == null) {
//                    myCalendarAdapter = new MyCalendarAdapter(context, 3, output);
//                    listView.setAdapter(myCalendarAdapter);
//                } else {
//                    myCalendarAdapter.clear();
//                    myCalendarAdapter.addAll(output);
//                    myCalendarAdapter.notifyDataSetChanged();
//                }

            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GoogleCalendarTest.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }
}