Feature: U1
  Background:
    Given this user exist
      |emailAddress |password  |firstName |lastName |nickname
      |tst@usr.com  |Test12345 |Test      |User     |testt

  Scenario: I log in with a valid username and password
    When I log in with username "tst@usr.com" and password "Test12345"
    Then I am logged in
    And I am taken to my profile page

#  Scenario: I log in with an invalid credentials
#    When I submit my credentials
#    Then I am shown an error that my username or password is not valid
#
#  Scenario: I log in with no username
#    When I submit my credentials
#    Then I am prompted to enter and username
#
#  Scenario: I log in with no password
#    When I submit my credentials
#    Then I am prompted to enter a password
#
#  Scenario: The network is unavailable
#    When I submit my credentials
#    Then A message displays instructing me to try again later

#  U1 Registering and logging into an individual account
#  CRud: Individual (i.e. this story is for create and read)
#  As a user, I want to have an individual account with at least my full name*, nickname, bio, email address*, date of birth*, phone number, and home address* (a textarea will do for this story). The attributes with the asterisk are mandatory (have to always contain values); an individual account cannot exist without the mandatory attributes filled in. You may add other attributes you deem are necessary.
#  AC1: Assuming I am not already logged in, the application gives me the ability to either log in or register (create) a new account. When registering, the mandatory attributes are clearly marked.
#  AC2: The username I use to log in should be my email address that I have previously registered in the system. If I try to register an account with an email address that is already registered, the system should not create the account but let me know. Similarly, if I try to log in with an email address that has not been registered, the system should let me know.
#  AC3: If when logging in, my details are incorrect (username/password), the system should generate an error message letting me know.
#  AC4: Appropriate validation is carried out and errors are clearly conveyed.
#  AC5: When I tab, I cycle through the form fields in the correct order (i.e. pressing tab should take me to the next form item that I need to fill in). This needs to always be true even in future stories.
#  AC6: Appropriate error messages should be shown (e.g. mandatory field not filled in). The error message should help me understand the problem and the location of the problem so that I can easily fix it.
#  AC7: Passwords are not stored in plain text.
#  AC8: On successful log-in or registration, I am taken to my own profile page within the system. Currently, the profile page simply displays my profile info formatted to be easily readable.
#  AC9: On the profile page, it shows the date of registration and in brackets the years and months (rounded down to the nearest month) since registration, e.g. “Member since: 2 April 2020 (10 months)”.
#  AC10 My data (user profile) is stored persistently so that I can log in at another time. For now, go for the easiest/simplest solution to do this.
#  AC11: I can log out. Pressing the browser’s back button or going directly to my profile’s URL does not show my data. Doing so will redirect to the log-in/sign-in page.
#  AC12: I can log back into my profile with my username (email address) and password. Appropriate error messages are shown for unsuccessful log-ins.
#  AC13: For demo purposes, all attributes can be shown on the profile page. We will change this in future stories so that only some attributes can be seen by others.
#  Note: even though a number of the attributes are mandatory, during development, the dev team can leave these as “not mandatory” as it would become annoying to fill these in every time by the developer. However, it would be advisable to have these as a “switch” that could be turned on easily when demoing or at submission.
