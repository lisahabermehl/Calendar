package com.example.lisahabermehl.calendar;

/**
 * Created by lisahabermehl on 12/06/2017.
 */

public class GoogleCalendarTest2 {

    private static final String url = "https://www.googleapis.com/calendar/v3/calendars/test7083170%40gmail.com/";

    static String response;

    protected static synchronized String getEvents (String... params) {
        String apiKey = "{AIzaSyAGVKreGgevmD3mH_Pw21oG-OdOr1rOKqM}";
        String completeUrl = url + "events?maxResults=5&key=" + apiKey;

        return "an event";
    }
}
