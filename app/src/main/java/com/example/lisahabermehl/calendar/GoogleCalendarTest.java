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

import com.google.api.services.calendar.model.*;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    MyCalendar myCalendar;

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
        setContentView(R.layout.calendar_list);

        mOutputText = (TextView) findViewById(R.id.mOutputText);

        myCalendarDbHelper = new MyCalendarDbHelper(this);
        myCalendar = new MyCalendar();

        // let user know that app is fetching data
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Un momento ...");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        // get the date that user selected
        getResultsFromApi();
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    public void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            String[] passing = new String[4];
            passing[0] = "get";

            // execute the AsyncTask and give date
            new MakeRequestTask(mCredential).execute(passing);
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
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
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
    private class MakeRequestTask extends AsyncTask<String, Void, String> {
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
        protected String doInBackground(String... params) {
            if (params[0].equals("add")) {

                String des = params[1];
                DateTime startDateTime;
                DateTime endDateTime;
                Date startDate;
                Date endDate;

                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

                    startDate = simpleDateFormat.parse(params[2]);
                    endDate = simpleDateFormat.parse(params[3]);

                    startDateTime = new DateTime(startDate);
                    endDateTime = new DateTime(endDate);

                    insertEvent(des, startDateTime, endDateTime);

                    return "added";
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else if (params[0].equals("get"))
            try {
                List<String> output = getDataFromApi();
                if (output == null || output.size() == 0) {
                    return "No results returned.";
                }
                else {
                    return "Got it!";
                }

            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return null;
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        // where all the data comes from, try to put these in an SQLite database?
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());

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
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date original = new Date(event.getStart().getDateTime().getValue());
                String dateStart = dateFormat.format(original);

                // get the time
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                String timeStart = timeFormat.format(original);

                // get the end time (and date?)
                Date originalEnd = new Date(event.getEnd().getDateTime().getValue());
                String dateEnd = dateFormat.format(originalEnd);
                String timeEnd = timeFormat.format(originalEnd);

                // what is the activity about
                String activity = event.getSummary();

                eventStrings.add(String.format("%s - %s \n%s", timeStart, timeEnd, activity));

                // add the above to DB
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
            return eventStrings;
        }

        private void insertEvent(String des, DateTime startDate, DateTime endDate) {
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDate);
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDate);

            Event event = new Event()
                    .setDescription(des)
                    .setStart(start)
                    .setEnd(end);

            String calendarId = "primary";

            if(mService!=null)
                try {
                    mService.events().insert(calendarId, event).setSendNotifications(true).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }


        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(String output) {
            mProgress.hide();

            if (output == "No results returned.") {
                mOutputText.setText("No results returned.");
            }
            else {
                startActivity(new Intent(GoogleCalendarTest.this, MyCalendar.class));
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