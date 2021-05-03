# User Manual

Basic manual for team-300's webapp.

## Logging in to the application

Regular user:  
Username: `fdi19@uclive.ac.nz`  
Password: `fun123`

Admin (DGAA):  
Username: `admin@wasteless.co.nz`  
Password: `admin`

User with business:  
Username: `dnb36@uclive.ac.nz`  
Password: `fun123`


## Connecting to a MariaDB database hosted at UC

For anyone wondering how to get this to work locally here are some steps:

1. Create a database table in https://dbadmin.csse.canterbury.ac.nz/ on your own account (password
   is student number)

2. In application.properties, uncomment the MARIADB section and comment the H2 section

3. Add the three environment variables to to your grade configuration with the url being:  
   `<platform>:<driver://<host>:<port>/<database>[?<options>`  
e.g. `jdbc:mariadb://db2.csse.canterbury.ac.nz:3306/{database_name}`

Note: if at home you will need to SSH into the uni servers using:
`ssh abc123@linux.cosc.canterbury.ac.nz -L 3306:db2.csse.canterbury.ac.nz:3306 -N`
and you will need to change the URL to: `jdbc:mariadb://localhost:3306/{database_name}`


## Resetting local storage between server restarts

After resetting our local backend server some weird things can happen in the browser.  
Sometimes the browser might not be sure if you are logged in or not and you might see this message `Error fetching user details`

**The fix:**  
In chrome we can delete our local storage by inspecting (CTRL+SHIFT+I on Windows), navigating to the Application tab and deleting everything in the local storage section


## Viewing a businesses' product catalogue

Not just anyone can view a businesses' product catalogue, the user trying to view the catalogue must be an admin of the business  
For a business admin to view their catalogue they must first act as the business, this can be done by selecting the business in the dropdown on the far right side of the navbar  
Once the user is acting as the business there will be a new link available in the navbar "catalogue", clicking this will take the user to the businesses' catalogue page!


## Notes

- If you come across this error `Caused by: java.sql.SQLNonTransientConnectionException: Socket fail to connect to host:localhost, port:3306. Connection refused: no further information`,
make sure to comment out the MairaDB section of application.properties and uncomment the H2 section (or just connect to the database)
- We (team 300) are the best team