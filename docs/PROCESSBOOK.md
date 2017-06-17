# day 1
Thought about what project to do. Had 3 different ideas at the beginning of the day:
An app that uses the NS API: sends a notification when train is delayed or calculates if you have to walk or run if you want to catch a certain train based on minutes remaining and how fast you run / walk
An app that gives an overview of all the different models that people use for instruction based on feedback.
An app that uses the Google Calendar API: user is able to make a TODO list (what to do, how much time do you need to do this, when does this need to be done), using an algoritm the app integrates this TODO list in the existing Google Calendar of the user.

### Important decisions
* Chose to do the last one in the end. Because I think it’ll be the most handy. Wrote a [project proposal](https://github.com/lisahabermehl/Project/blob/master/README.md) for this.

# day 2
Started the day with a standup. Everyone explained what they were planning on doing. Gave each other some tips.

At first I was planning on letting the user log in with a FireBase account, and link this to their Google account to get access to their Google Calendar. But it might be possible to just use this Google account for both logging in and getting access to the calendar. So I’m going to look into this today, noting the possibilities down here.

After that I started working out my design document which is due tomorrow.

Came across this while researching Firebase and Google Calendar:
 The Firebase SDKs will automatically refresh Firebase ID tokens, but not Google access tokens. There is no way to currently do this with the current SDKs that I am aware of, unless you roll your own Google OAuth flow using custom authentication.
https://stackoverflow.com/questions/28932427/firebase-auth-and-google-calendar
At present Firebase only supports certain scopes. You can’t use the *calendar scopes to authenticate with Firebase auth.
Scopes are strings that enable access to particular resources, such as user data. You include a scope in certain authorization requests, which then displays appropriate permissions text in a consent dialog that is presented to a user. Once the user consents to the permissions, Google sends your app tokens, which identify the specific authorization grant. In other words, the scopes and tokens determine what user data the user gives your app permission to access.
The only real workaround here, to avoid authenticating separately against Google and Firebase, would be to manually authenticate against Google with the requested scopes, then pass that OAuth token into Firebase's authWithOAuthToken method. In other words, reverse the auth process to log in with Google and then re-use the token in Firebase.
Works when used like this
Auth.$authWithOAuthPopup('google', { remember: "default", scope: 'https://www.googleapis.com/auth/calendar', scope: 'email' }) .then(function (authData) {
Maar aan de andere kant bestaat dit ook:
https://zapier.com/zapbook/firebase/google-calendar/

En lijken mensen er een oplossing voor te hebben gevonden:
https://stackoverflow.com/questions/39914899/using-firebase-auth-to-access-the-google-calendar-api

Alleen inloggen met Google lijkt het handigst te zijn:
http://www.androidhive.info/2014/02/android-login-with-google-plus-account-1/

Maaaar voor Firebase gebruik je ook gewoon je google-account. Dus misschien kan het wel gewooon.

### Important decisions
* Deze week richten op het ophalen van informatie van de Google Calendar.
* Richten op het weergeven van de calendar en het maken van de ToDo list, de rest komt later. 

# day 3
Niet zoveel kunnen doen vandaag omdat ik mijn eigen laptop nog niet terug had en de leenlaptop supervaak bleef vastzitten met AndroidStudio. 
Nog niet helemaal uitgekomen met GoogleCalendar mede omdat ik dit nog niet kon uittesten. 
Maar wel een simpel layout idee gevonden: CalendarView waarbij de user een ListView te zien krijgt als er op een datum wordt geklikt. 

Vanmiddag kon ik mijn eigen laptop weer ophalen (yay), maar het linken van GitHub, de files van de app op mijn computer enzo ging niet zo goed. Dus heb toen geprobeerd om alles te verwijderen en helemaal opnieuw te beginnen onder dezelfde naam, maar toen zei die dat de naam al bestond op GitHub. Dus toen uiteindelijk maar een hele nieuwe repository aangemaakt met een nieuwe naam: [Calendar](https://github.com/lisahabermehl/Calendar)

### Important decisions
* Morgen voornamelijk aandacht besteden aan de link van het klikken op een dag naar het zien van een popup scherm waarin staat wat er op die dag allemaal moet gebeuren (vind die andere weeklayout nog steeds mooier maar dit lijkt lastiger te zijn, kan dit later misschien nog even aanpassen met een GridView o.i.d. maar nu even niet zoveel aandacht aan besteden). 

# day 4
### TODO
* alle nodige activities maken ✓
* GoogleCalendar API werkende krijgen (as in: resultaten terug krijgen) ✓
* een popup scherm krijgen bij het klikken op een dag in de calendar
### Process
* alle nodige activities gemaakt om door de app heen te gaan (komen er vast nog meer bij, zoals Helpers e.d.)
* bij het linken van de GoogleCalendar API kreeg ik een error: "execution failed for task':app:prepareDebugAndroidTestDependencies'"
**opgelost** door - androidTestCompile 'com.android.support:support-annotations:25.3.1' - toe te voegen in de gradle (app module) om zo te forcen dat de laatste versie van libraries worden gebruikt
* in de voorbeeldcode van GoogleCalendar worden de resultaten in een List<> teruggestuurd, even kijken hoe dit verwerkt kan worden in de Calendar die ik nu heb
### Think, think
* If your Todo's are nicely implemented into your Calendar, but then you plan a spontaneous dinner on Saturday which will adjust the whole what Todo and when, but will also result in not finishing something of your Todo list before the deadline you've set: problem or..? Also, if you implement a Todo with a deadline, but there is no way that this Todo will be done in time, do you: ask the user for a new deadline, give suggestions on what activities should be cancelled in order to finish in time, or..?
### Important decisions
* Buttons and such won't be made in .java but in .xml

# day 5
### TODO
* make sure that you can see a specific GoogleCalendar day by clicking on a day in the CalendarView

### Process
* just read something about using a library wrapper instead of making my own http requests to Google
* also something about CalendarProvider, since I don't want to spend a lot of time on making this calendar work but more time on having an algoritm that makes sense, I am going to look into these things, to find out if this will make it any easier
* so Google Calendar's data looks like this: **2015-12-02T14:15:00.000+05:00** 

### Important decisions
* 

# Focus for this week
* make Todo list
* make sure that items of Todo list will be visible in MyCalendar
* make some restrictions to plan this (e.g. only between 08:00 and 22:00)

# day 7
### TODO
* show Google Calendar's data in a nice overview

### Process
* just found out that it's also possible to get an URL based on what information you need from Google Calendar. [This website](https://developers.google.com/apis-explorer/#s/calendar/v3/) looks super handy, but I'm not sure if I should spend more time on exploring this other option. Maybe later
* Adjusted the way Google Calendar API's output looks in the app, still basic but will fix that later

### Important decisions
* gonna try to link the Todo list with the Calendar this week, layout will be something for next week

# day 8
### TODO
* make Todo list ready to link with MyCalendar

### Process
* "An OAuth2 client already exists for this package name and SHA-1 in another project. You can omit the SHA-1 for now and read more about this situation and how to resolve it." >>> "Authentication: To use Google as an auth provider, you must manually whitelist the client ID from your existing project in the Sign-In configuration." **Problem? Or just don't add SHA-1 to Firebase project?**
* made an AlertDialog + SQLiteDatabase for user to add a new task

### Important decisions
* 

# day 9
### TODO
* make Todo list ready to link with MyCalendar: a column for user to give time needed for a task, 
* user should be able to drag items in todo list according to importance / according to what tasks he/she wants to finish first: https://github.com/bauerca/drag-sort-listview
### Process
* the Todo list that I want to make consists of a description of the task and the time needed, but this means that two TextViews need to be updated when a new task is added. It's not possible to do this within an xml file, since the ArrayAdapter doesn't have this option. So I should make my own adapter in another .java file. Gonna use this explanation: https://stackoverflow.com/questions/11106418/how-to-set-adapter-in-case-of-multiple-textviews-per-listview
* so I made a TaskObject with an adapter, works fine, but when I tried to adjust MyCalendar to how I implemented my Todo list/Tasks (object that defines the task/calendar, adapter that helps updating the UI, a TaskTable.java that defines all the names/variables in a table, and a DbHelper that creates or updates the table). It seems like it doesn't work because I'm trying to make the table while I'm still in an AsyncTask? Because when I add the piece of code that makes the table, the GoogleCalendar API returns null, if I remove just this little piece of code I get an output.

# day 10
### TODO
* 
### Process
* (so I forgot to initialize an new MyCalendarDbHelper....... that's why it didn't work -.-)
### Important decisions
* instead of trying to fix everything at the same time, I'm gonna fix the calendar first (ArrayAdapter) and how I'm going to manage the data. After that I can start thinking about the algoritm to plan/make suggestions for Todo items in the calendar
* also, start thinking about when to sync data from GoogleCalendar

# day 11
### TODO
* 
### Process
* MyCalendarAdapter works!
### Important decisions
* 
### Think, think
* what happens if someone wants to make a new activity, but making this new activity will lead to not finishing a Todo before the deadline. Should there be a check to see if it's possible to move this to another moment, and a warning to let the user know that he/she won't be able to finish in time (based on the time needed and deadline), and give user the option to: (1) say yes, this new activity is more important than finishing in time or (2) say no, maybe I shouldn't plan this activity because this Todo really has to be done in time

# day 12
### TODO
* MyCalendar works, Todo works, so now I should fix seeing activities from one day when clicking on a specific day OR make sure that all activities from GoogleCalendar are visible in an organised overview, one long list (and give the user the option to scroll to down to another date by clicking on a date in the calendar)
### Process
* results are shown in descending order by date and time
### Think, think
* how are you supposed to check for activities with the same name, date and time? With other words: how are you supposed to check if the values in different columns in one row are exactly the same as the values in different columns in another row?
