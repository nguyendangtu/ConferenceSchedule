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
Download ConferenceSchedule.zip and extract zip file to your local machine.
These instructions will get you a copy of the project up and running on 
your local machine for development and testing purposes. See deployment for 
notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them

```
Give examples
```

### Installing

A step by step series of examples that tell you how to get a development env running

Say what the step will be

```
Give the example
```

And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc