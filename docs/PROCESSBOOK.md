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

# day 7
### TODO
* show Google Calendar's data in a nice overview

### Process
* 

### Important decisions
* 

