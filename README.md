# CONFERENCE SCHEDULE

Conference schedule provide service which is using for scheduling for any conference.
Base on list of talks need to present in a conference, we group talks by two group.
One is fix time group and another one is flexible time group. The service will provide the
way to set time for talks in flexible group.
After all talks already are scheduled, we combine fix time group and flexible group, then put to 
a Hashmap which having key is a day and value is all talks need to present in this day.

## SOLUTION
Base on fix time group, we calculate slots for flexible talks.
Example: we have four fix time group KEY_NOTE,LUNCH,TEA,CLOSING for one day.
we will create 3 group talks:
##### finish KEY_NOTE time to start LUNCH time
##### finish LUNCH    time to TEA start   time
##### finish TEA      time to CLOSING     time
The number of day in a conference is the number of Closing Talk. 
If closing talk is 2, so group talk is 2 * 3 = 6.
Each group talk will have start time, total time, available time and list of Talks.
We Iterate all dynamic talks, then add it to 6 dynamic group talks if available time
minus talk duration is greater than zero (available time - duration > 0).
When a talk is added to a group, available time we be decrease (available time = available time - duration)
When a group talk is full, we will move to the next group talk. We loop all dynamic talk until all talks are scheduled.

## Getting Started 
This project is running on Java 8 and Spring Boot version 2.0.1.RELEASE.
Download ConferenceSchedule_John.zip and extract zip file to your local machine.
###1. Execute jar file
go to jars folder on command line: 
#### java -jar Conference-Scheduler-1.0.jar
open web browser
#### localhost:8080
we can see all talks already scheduled. 
###2. Setup project
Download ConferenceSchedule_John.zip and extract zip file to your local machine.
go to ConferenceSchedule folder to build project.
#### build project: mvn clean install 
After after build successfully, we already download all dependency to your local.
Open Eclipse/Intelij to import the project. Navigate to com.conference.Application to run
application.