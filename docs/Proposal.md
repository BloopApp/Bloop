# Final project proposal


## The names of everyone in your group
Danny, Philip, Sam


## A general description of your app idea
Multiplayer deathmatch capture the flag with a persistent world. Each individual user who logs in drops a flag wherever they’d like to start playing. From there, they get visual and audio cues (as we call, bloops) as to the location of other players’ flags. Once you are on a flag, you can capture it.


## A list of features that will be included. Include both:
- Required features for an MVP
    - place a flag (location marker)
    - take other people’s flag (with indication of how close)
    - bloops
    - high scores
    - usernames of some variety (so also included login)
- Medium Goals
    - being able to move your flag (pickup and drop-off)
    - defending flags with some type of time boxing
    - weekly and current leaderboards (and resets)
    - persistent notifications and push notifications 
- Stretch goals
    - user designed flags
    - fancier bloops


## What technologies/APIs/libraries/hardware-components your app will depend on
- location
- maps
- dropwizard postgres (backend component for sending nearest bloop count)
- firebase for user auth


## For each team member: three project learning goals. Explain what you hope to get out of the project and how your project will help accomplish that goal
### Danny
- I want to publish this app on the Google Play Store and add tracking of some sort
- Push notifications, I know this requires some sort of service on the backend, and I’m curious how to do it
- I want to make the GPS data tracking screen


### Philip
- Want to learn about push notifications. Both the implementation on the Android side and the needed backend support. If we don’t get to the point of implementing in the app, I want to make my own demo of a push notification and service.
- I want to learn more about GPS location stuff. I’ve used it before, but some of the methods don’t make sense and I want to be able to implement coarse location stuff without too much battery drain.
- Being able to work effectively with Danny and Sam. Three person teams are hard to manage/organize, so being able to stay on task and accomplish more than one stretch goal would be optimal.


### Sam
- Also push notifications.
- Also learn how to use location APIs. Haven’t worked with this yet. 
- Having focused mostly on functionality and not much on UI in the last couple of labs, I’d like to actually get some experience making nice-looking Android layouts.
- Learn how to keep Pip alive for the longest period of time
