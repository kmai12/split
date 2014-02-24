CS48 W14
G03
Authors: 
	Kevin Jih
	Lesley Khuu
	Kevin Mai
	Sohan Shah
	Jenny Vien

Application:
	Split It

Table of Contents
--------------------
I.Installation and Libraries

II.Setting up the Database

III.Creating and running Split

IV.Split It

V.Unfinished/Bugs





I.Installation and Libraries
------------------------------
INSTRUCTIONS ON HOW TO DEPLOY SPLIT
This program is designated for Windows.

You will need JRE7 if you do not  have it already.
Download from 
http://www.oracle.com/technetwork/java/javase/downloads/java-se-jre-7-download-432155.html
and install.

You will need Apache TomCat 7.0 to use a the local server.
Install Apache TomCat 7.0

1.Go to 
 http://tomcat.apache.org/download-70.cgi
     ->Click on: 32-bit/64-bit Windows Service Installer
     
2.Open and install apache-tomcat-7.0.52.exe

3.Use default install options, do not create a user/password if prompted.

Make note of the installation path.

Ex. C:/Program Files/Apache Software Foundation/Tomcat 7.0"/>

3.Go to: 
https://bitbucket.org/xerial/sqlite-jdbc/downloads
and Download sqlite-jdbc-3.7.2.jar

4.Place sqlite-jdbc-3.7.2.jar into the "lib" folder of your tomcat installation.
Ex.  C:/Program Files/Apache Software Foundation/Tomcat 7.0/lib

II.Setting up the database
---------------------------
An example SQLite database is provided for you. All you need to do is to put it into the correct place.

1.Create a folder called "work" inside your C:/ drive folder.
Ex. C:/work/

2.Place splitdb.db into the folder.


III.Creating and running Split
------------------------------
You may need to edit a property inside the build.xml in order for
 the app to work.
      *Property name="tomcat" value="The base of your Tomcat Installation"
      	Ex.value="C:/Program Files/Apache Software Foundation/apache-tomcat-7.0.50"

Now you are ready to run the application!

Start up the Tomcat Server

1.In the windows start menu, search for "Monitor Tomcat" and execute it. A window should pop up.

2.Under the general tab, select START.

Run the program

3.Run a CommandPrompt as an Adminstrator:
 *Search for it in the startup menu.
 *Right click it and select "Run as administrator"

NOTE:This step is important. YOU NEED TO RUN AS ADMINISTRATOR.

4.cd into the Project directory.

Ex. C:/Users/%USERNAME%/Desktop/Projects

5.Enter in the following ant commands:
	*ant warTarget
	*ant deployTarget

6.Go on your favorite web browser and type in:

http://localhost:8080/splitApp
NOTE:The default port for Tomcat is 8080, if you changed it in the installation, just replace 8080 by the port number you selected.


You are done!

7.Closing the app and server

Click "Stop" on the Apache Tomcat 7.0 Tomcat7 window to stop the server.
Use command "ant clean" to undeploy the app.


IV.Split it
-------------
Split It is a web app intended to keep track of bills between friends. You can register to the app. Then you are free to login, add, pay, and view bills. All of this is brought to you by simple html pages.

NOTE: To play around with Split It, you can log in and use our test-users already included in the database. Or you can just register a bunch of users for you to play around with yourself.
Users:	      Password:
test	      test
test1	      test1
test2	      test2
test3	      test3

Add a Bill:

    Bill Name: Enter name that the bill is for

    Total Cost: Enter the total cost of the bill that will be split evenly between the recipients.

    Recipients: Enter the usernames of the recipients of the bill you want to charge to. 
    These recipients will be charged the split cost.

View Bills:

     In the front-page there is a table of pending bills that you owe on the bottom left.

Pay:

     Type in the bill_id if the bill that you have paid off. Then click Pay. This will remove the bill from your bill list.



    
V.UNFINISHED/BUGS
------------------
There are a couple of unusable buttons in the program. These are features that will either be removed or implemented in the final version.
They include:
     *Archive
     *Payment History
     *Forgot PW?

Bugs:
     *statusMessage does not reset, often displaying wrong status messages.
     *input forms do not reset, displaying previous information that was input
     into the forms.
