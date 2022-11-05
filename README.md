# Wikipedia First Link Follower

## This project answers the question: 
### What would happen if you kept following the first link in wikipedia's articles?

## Description
This app finds a wikipedia article using wikipedia's random url. The app then follows the first link found in the article. The app continues following first links until a loop in the link chain is detected. The link chain is stored in a local links.txt file.

## Technologies
- [Java Programming Language](https://docs.oracle.com/javase/8/docs/technotes/guides/language/index.html) version 17.0.4.1
- [Gradle build tool](https://docs.gradle.org/current/userguide/userguide.html) version 7.5.1

## Installation and Execution
- Install Java and Gradle with correct versions.
- Clone the repository locally.
	- To build, use gradle command `gradle build` from within cloned repository.
	- To run, use gradle command `gradle run` from within cloned repository.
	
## Use
Run `gradle run` from the cloned repository. The app will ask for a wikipedia article URL. The entered URL will then follow the first link found in the article's main content. If no URL is entered, then a random one is found via the url `https://en.wikipedia.org/wiki/Special:Random`. The app then keeps following and recording first links until a loop is detected. The recorded links can be found in the file `/app/links.txt`. This file is overridden everytime the app is run.

## Findings
With current link exceptions, most articles end in a loop of 11 articles:
- Religion
- Social_system
- Sociology
- Social_science
- Branches_of_science
- Science
- Scientific_method
- Empirical_evidence
- Proposition
- Logic
- Religious_philosophy

