Feature: U4 Default Global Application Admin (DGAA)

#  The application must always have a DGAA when it is running. This allows the team to still get into the system via the interface if something goes wrong.  The DGAA can make any individual a Global Application Admin (GAA).
#  AC1: When the application is (re-) started, it checks that the DGAA exists.
#  AC2: The application periodically checks that at least one DGAA exists. You may choose the period but this should be easily adjusted (e.g. via a config file) for demoing purposes.
#  AC3: If a DGAA does not exist (e.g. it was incorrectly deleted directly from the database), the DGAA should be created automatically and an entry should be made into the error log.  The DGAA will have a predefined username and password. Changing this username and password would require access to the server itself (e.g. changing it in the database).
#  The DGAA has the ability to do anything in the system (e.g. delete anything, change any passwords, …). However, the main facility we are concerned with in this user story is the ability to add an application administrator role to any individual account.  CR
#  AC4: The DGAA can search for a particular individual.
#  AC5: It is obvious to the DGAA if an individual already has system/application admin rights.
#  AC6: The DGAA can make the individual a GAA. It is then obvious to see that the user has admin rights.
#  AC7: The DGAA can remove admin rights from any individual.
#  AC8: Only individuals can be given application admin rights. Business accounts (... coming in future stories) cannot be given admin rights.
#  AC9: When that individual logs into their account, they should be able to see that they have application admin rights. Note that the individual may also be an administrator for one or more businesses, so these roles should be clear.
