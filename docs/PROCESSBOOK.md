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

# day 13
### TODO
* could use this: https://stackoverflow.com/questions/4777272/android-listview-with-different-layouts-for-each-row to fix the layout for the calendar view OR make an expandable listview something?

### Process
* tried to fix the calendar's layout by making two different xml files for an item in the calendar listview. But somehow the if-else statement doesn't work in the arrayadapter's getview. The one that's called first will show but with the other one the values seem to be passed as "null" and doesn't show anything in the listview. Except for when the value of a textview is initialized in the xml file itself.

### Think, think
### Important decisions

# day 14 
### TODO
### Process
### Think, think
* Het is makkelijker om de calendar te syncen door alle values te verwijderen en deze weer opnieuw op te halen van google calendar. Maar daarom ook de beslissing genomen om de kalender alleen voor 3 maanden te kunnen gebruiken. MAW op basis van drie maanden worden de todos ingepland, obv drie maanden kunnen er todos aangemaakt worden dan ook? Of kunnen users todos voor ver in de toekomst aanmaken en dan wordt het gewoon ingepland met het idee van “dit kan wel zo laat mogelijk”
* Nog steeds de mogelijkheid toevoegen om activiteiten aan Google Calendar toe te voegen? Voordeel hiervan is dat de user een melding krijgt dat die in de knoop komt met het afronden van een todo item door het toevoegen van deze nieuwe activiteit
* Is het mogelijk om de id steeds naar 0 te resetten als de calendar gesyncd wordt?
### Important decisions
* Ook een optie maken om in te stellen voor welke periode je suggesties wilt krijgen voor je todo lijst: drie maanden? Of misschien is het op dit moment wel handig omdat de tentamenweek er bijna is over drie weken, dan zou je het kunnen instellen op drie weken

# day 15
### TODO
* add events to GoogleCalendar with my app
### Process
*
### Important decisions

# day 16
### TODO
### Process
### Think, think
* Wat zijn de uitgangspunten voor het plannen van Todos in de Calendar? (1) Eerst in de time gaps van <120 minuten in de ochtend, (2) dan in tim gaps van <120 minuten whenever (want alle kleine dingen die gedaan moeten worden passen hier dan mooi in) (3) time gaps van <120 minuten in de avond liever niet? (4) Time gaps van >120 in de ochtend, (5) time gaps van > 120 whenever, (6) time gaps van >120 in de avond.
* Deadlines zo vroeg mogelijk? Of beter
* Ga je eerst alles wat in de korte lijst past hier in zetten, of ga je per korte Todo item waar deze het beste past: in <120 of >120. Denk dat per Todo item op dit moment het makkelijkst is, want die looped als laatste en dan zou je al die verschillende tijdsvakken af kunnen gaan
* En dan alle verschillende condities op de volgorde dat je dingen als eerste zou doen. Dus eerst in een kort vakje proberen te laten passen: eerst in de ochtend want dan heb je nog een fris hoofd, dan random op de dag en anders als het echt niet kan 's avonds. Dan in een lang vakje: in de ochtend, random en avond.
* Of heb je die big/small time gaps wel nodig? Eigenlijk kan je ook een onderscheid maken tussen ochtend, random en avond. En dan de eerste gap die je tegenkomt vullen met Todo items, als dit niet past ga je naar de volgende gap.
### Important decisions
* gonna give users the option to plan for a specific time span
* maybe it's also good to give users the option to set minimum time gap between the Todos in the Calendar

# day 17
### Todo
### Process
* Geen Calendarview meer, want dan krijg je een long, met een Datepicker kan je gewoon day, month en year ophalen. Maar month wordt wel als 6 gegeven in plaats van 06, dus daar nog even wat aan doen
* select by date works
* added an intro screen
* it's possible to search by specific date and title
* updated the algorithm for planning Todos in the Calendar
### Important decisions
* gonna 

# day 18
### Todo
*
### Process
* Als je nu een nieuwe todo toevoegt, dan wordt die nog niet op de goede volgorde gedaan lijkt het
* not possible to search by title and date anymore
### Important decisions
* Het ophalen van gegevens duurt soms wel een beetje lang, daarom misschien beter om een limiet aan te zetten. Zoals standaard alleen gegeven van 3 maanden ophalen en daarvooor kunnen plannen. 
* implement the option for users to set different times where they don't want to do anything, not just bedtime gaps (zoals het nu is), but also time for lunch etc.

# day 19
### Todos
* make sure that the user can set different time gaps, a time span
### Process
* so at first I had different Databasehelpers (one for the Todos and one for the Calendar). But I have a lot of files now, so I thought it might be better to just merge these Databasehelpers
* also deleted unnecessary files 

# day 20
### Todo
* make sure the user can set things in settings
### Process
* user is able to set a time gap between activities a time span to plan for and a bedtime
* afronden naar boven van tijden, want als todo wordt ingepland tussen current time en start next activity dan krijg je gekke tijden

# day 21
### Todo
* make sure the user can't only set a bedtime, but also set different times that he/she doesn't want to be bothered with having to do Todos
* try to make the UpdateUI in MyCalendar.java less long (maybe put some of the database stuff in the Database helper and call these functions from UpdateUI?)
* Bij toevoegen van een nieuw event automatisch refreshen
* Voorkomen dat user dubbel kan plannen in Calendar o.b.v. events die uit de Google Calendar worden gehaald
### Process
* Beetje aan layout gewerkt ook, wil eigenlijk dat de Timegap dingen van rechts komen scrollen, 
* Verschillende timegaps in een tabel plaatsen en de andere dingen in shared preferences. 
### Think, think
* Bij passeren deadline wordt het nog steeds ingedeeld
* Als er een kleine time gap is dan wordt deze ook niet ingedeeld met todos die weinig tijd innemen, en wordt er gewacht op een grote time gap om die ene “belangrijkere” todo in te delen

# day 22
### Process
* In plaats van checkedtextview, gewoon checkboxes gebruiken want ik krijg die checkboxes niet aan de praat
* Het is nu mogelijk om checkboxes te checken en dan deze dagen in de database op te slaan (+ begin en eind van donotdisturb tijd instellen + titel op slaan)
* Mogelijk om time span en time gaps tussen Todos in te stellen met een NumberPicker en deze opslaan in SharedPreferences
* Bij time gaps van user: eerst checken hoe groot de time gap is, net zoals bij de vorige versie, maar dan ook een check of de time gap van de user in deze time gap zit. En dan opsplitsen in twee: time gap part 1 en time gap part 2. 
### Think, think
* Bij time gaps van user: eerst checken hoe groot de time gap is, net zoals bij de vorige versie, maar dan ook een check of de time gap van de user in deze time gap zit. En dan opsplitsen in twee: time gap part 1 en time gap part 2. 

# day 23
### Todo
* Vandaag de do not disturb tijden die ingesteld zijn door de user implementeren in de calendar, zou het als een soort calendar event gemaakt worden? En het dan zo meenemen in de calendar
* Never mind bovenstaande. Nu focussen op het implementeren van todo’s dichter naar de deadline toe. 
Beetje opnieuw beginnen om zo ook meteen de code beter te maken. En eerst handmatig values doorgeven zoals de time gap tussen de verschillende todos en daarna gebruik maken van wat de user invoert.
### Important decisions
* Toch maar ervoor gekozen om de verschillende time gaps eruit te laten, want dat werkt nog niet. Past niet goed in het algoritme dat ik al heb
