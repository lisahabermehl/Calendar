#### My Calendar
### Planning suggestions for poor planners
Have you ever been in a situation where you wish you started earlier with studying for a test or started earlier with writing an essay? The goal of this app is to give you an overview of when you can do something in a short period of time to make sure you’ll be done in time. There are two main functions. Firstly, a Todo List where you can make a list of what needs to be done (read chapter 1, write your essay), how much time you think to spend on this specific Todo Item and when the deadline is. Secondly, a Calendar that imports the events that you’ve already planned in your Google Calendar and that gives suggestions of when you can do a Todo Item. The events that are shown in your Calendar are determined by either the Time Span that you’ve chosen in in the Settings or the default Time Span of 14 days. How much time you’ll have between Events and Todo Items is determined by the Time Gap that you’ve set in the Settings or by the default Time Gap of 15 minutes. Besides this it’s also possible to set your Bedtime in the Settings to make sure that Todo Items won’t be planned while you’re in dreamland. 

### How is this app different from other Planner/Todo/Calendar apps?
I think that the visible part of my app will be the same as other Planner/Todo list apps. But the thing that distinguishes my app is the fact that my app will calculate and give suggestions for when you can do a Todo item from your list to make sure that you’ll be done in time with reading articles or writing an essay. 

### Behind the app
Let’s get technical. As you can deduce from the description above, the app consists of three activities: Calendar, Todo and Settings. 

## Calendar
This class uses three other classes. (1) A class that fetches events from Google Calendar with an AsyncTask, that saves all the events in the SQLite Database. But that also adds an event to Google Calendar. (2) A MyCalendarObject that consists of the title, date, starttime and endtime of the event. (3) A MyCalendarAdapter that makes sure that all these details of the MyCalendarObject will end up on the right spot in the list. 

## Todo
This class uses other classes as well. (2) A TodoObject that consists of the title, duration and deadline of the Todo Item. (3) A TodoAdapter that makes sure that all the details of the TodoObject will end up on the right spot in the list. 

Besides this, both Calendar and Todo use the DatabaseHelper (that creates and drops tables when called) and TableNames (where all the names of tables and columns are defined for consistency). 

## Settings
All the values that are set in this activity are stored in SharedPreferences. Values include: start of bedtime, end of bedtime, time span and time gap. 

### Process
Whilst developing this app I’ve ran into some minor problems. Nothing too big, but some time consuming functionalities that I’ve left out for stress’s sake. One of the “things to keep in mind” that I’ve set before starting developing this app was: if something seems to take more time than expected, replace it by something simpler. A lot of functionalities seemed to be more time consuming than expected. 

*The first week* I took time to figure syncing with Google Calendar out. I researched logging in with your FireBase account and to link this with giving permission to access your Google Calendar from the email address that you used to register with FireBase. In the end I decided on not giving the user the option to log in since this isn’t really necessary. It might be when the user is going to use another phone in the time he/she is planning Todos but since the goal of this app is short term planning, the chance that a user will get another phone in this short period is small. Other focus points of this week were displaying the Calendar and Todo list properly. At first I wanted to show a week overview like Google Calendar has. But this seemed to be too difficult and time consuming. Thought it might be possible with a GridView? But didn’t spend much time looking into this. After this I thought about just using a CalendarView and giving an overview of what has to be done on a specific day when the user clicks on a date. 

*Think-think-thingies* this week: What if you have suggestions for your Todo items, but you plan a spontaneous dinner, which leads to not being able to finish a Todo item before the deadline. Do you give the user the option to just ignore this and set a new deadline for the Todo Item and the option to cancel this spontaneous dinner? Also if you add a Todo with a deadline, but there is no way that this Todo can be done in time. Do you ask the user for a new deadline, give suggestions on what activities to cancel in order to finish in time?

*The second week* I was still working on fetching events from Google Calendar. The focus for this week was: making a Todo list, making sure that the items of this list will be visible in the Calendar and making sure that these won’t be shown between certain times (e.g. only between 08:00 and 22:00). Whilst figuring Google Calendar out I came across a lot of solutions to fetch data from Google Calendar, like a library wrapper or a website that gives you data based on a URL that you give ‘m (JSON). 

For the Todo list I thought it would be awesome to be able to drag the Todo items in the list according to importance and to which Todos the user wants to finish first. But in the end: time consuming and I let it go. 

For the Calendar I thought about showing all activities of a day 

This was also the week that I ran in problems that turned out to be the result of not initializing a new DatabaseHelper and such. And this was the week that I was trying to fix everything at the same time. So suggested by one of the Assistant Students I started focusing on fixing the Calendar first (using an ArrayAdapter here) and thought about how I’m going to manage the data. After this I could focus on making the algorithm work to make suggestions for Todo Items in the Calendar. 

*Think-think-thingies* this week: I need to check if values in different columns in one row are exactly the same as the values in different columns in another row, in order to be able to add new activities and “sync” the Calendar like this. But it’s way easier to sync the Calendar by deleting all the values and fetching all the data from Google Calendar. So if I’m going to do this, I’ll have to set a Time Span, because otherwise it will take a long time before every single event from Google Calendar is fetched. 

*The third week* I started implementing the different functionalities of the Calendar: adding an event to Google Calendar. Besides this I thought about the different restrictions that are necessary to plan Todos in the Calendar. 

*Think-think-thingies*. Is it better to fill the big time gaps and make a distinction between time gaps of more than and less than 120 minutes? Also thought about planning more Todos in the morning, because getting things done in the morning is always better. But it’s easier to make a distinction between time gaps between activities and fill these gaps with Todos until it’s filled. Based on this I decided to go with organising the Todo list based on the duration and the deadline: first the biggest Todos with the first deadline. 

Also decided on not using a CalendarView anymore when adding an event to Google Calendar. Other things I implemented this week: search by date or by title, an intro screen. Was planning on implementing a function where users can set different “do-not-disturb” times. Made a new table for this, and this worked: I could set different “do-not-disturb” time and edit these, could use these values in the Calendar class. 

This brings us to *the fourth week*. This is where I kinda realised that implementing the different “do-not-disturb” times could lead to some difficulties. Because while trying to make this work I was adjusting the whole algorithm that I had to plan the Todos. After two days of trying to make it work, I decided to let it go. And just work with what I had. Since this was one day before the deadline. 

Of course I’d rather keep these functionalities, but because the development of this app had to be done in four weeks it seemed better to just move on and… let it go. 
