Feature: U3 Searching for Users using Name

  Background:
    Given these users exist
    |emailAddress |password |firstName |lastName |nickName
    |bob@fri.com  |Test@1234 |Bob       |Vance    |Fridgeman
    |beet@dm.com  |Test@1234 |Dwight    |Shrute   |Bear
    |bo@ring.com  |Test@1234 |Andrew    |Tuna     |Randyandy
    |michael@dm.com |Test@1234 |Michael |Scott    |Bossman

  Scenario: AC1 successfully search for user based on name or nickname
    Given A user "bob@fri.com" with password "Test@1234" is logged in.

      When A search is performed for another user named "Dwight"
      Then A user record for user "Dwight" is returned.

      When A search is performed for another user with nickname "boss"
      Then A user record for user "Michael" is returned.

      When A search is performed for a non existent user "Donald"
      Then No user records are returned


#  See the notes on “Search” in the section at the beginning of this document.
#  Future stories will allow searches based on other criteria.
#  AC1: As a logged-in individual user, I can access a search facility containing one textbox to search for users based on their name or nickname.
#  AC2: I can enter a user’s full name or one or more of their names/nickname into the text box. Clicking on a search button/icon displays results in a tabular form (or something similar that can fulfil the other ACs while being user friendly).
#  AC3: The search results should be ordered in a way that makes sense (e.g. full matches first).
#  AC4: There should be enough information for me to work out which user I am searching for, i.e. there could be multiple people with the same name. A user’s date of birth and phone number should not be made public. Only the city, state/region, and country parts of their home address may be made public. Displaying the email address is OK for this user story. Once we add profile photos in a future story, we will make the email addresses private as well.
#  AC5: I should be able to order or reverse order the results using any of the attributes displayed. Clicking on the same header should display the results in reverse order. The ordering should be obvious (e.g. an arrow in that header pointing up or down). This should apply to all the results.
#  AC6: The results may potentially have a very large number of results. The application should be able to deal with this gracefully without forcing the loading a very large number of results (i.e. it should not crash my small-screen mobile device, which also has a slow internet connection).
#  AC7: The total number of results should be displayed. It should be obvious as to what section  of the results I am currently looking at, especially if the results are not all displayed at once. An example of this might be “Displaying 30-45 of 100 results” (or something similar).
#  AC8: Clicking on any result should take me to the profile page of that user.
