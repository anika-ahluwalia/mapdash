# cs0320 Term Project 2021

**Team Members:** Anika Ahluwalia, Ming-May Hu, Trevor Ing, Adwith Mukherjee

**Team Strengths and Weaknesses:** 

**Trevor**:
- Technical Strengths: Front-end (React, HTML, CSS, JS), design experience (Photoshop, Illustrator, Figma), Java, Python
- Technical Weaknesses: Back-end (SQL and other databases), web servers
- General Strengths: Organizational skills, creative problem-solving, communication with team members
- General Weaknesses: Can get too focused on small aspects of a project and lose sight of broader goal, tendency to rush through tasks to try and finish them early which sometimes hurts thoroughness, can be too stubborn to reach out for help when blocked   

**Ming-May**:
- Technical Strengths: Design experience (Photoshop, Illustrator, Figma, AdobeXD), Java, Python
- Technical Weaknesses: Not experienced with front-end
- General Strengths: Collaboration, open-minded, likes to try new solutions, good at planning ahead
- General Weaknesses: Can get stuck on trying to find the best way of doing something rather than just getting it done, I tend to work in waves of being super focused and productive and then not rather than being consistent

**Anika**:
- Technical Strengths: Java, Python, UI UX Experience (Figma, AdobeXD, Balsamiq), Front End (React, HTML/CSS, JS, TypeScript)
- Technical Weaknesses: Not experienced with back-end and servers
- General Strengths: likes to plan ahead, organized, likes to communicate and collaborate
- General Weaknesses: not very good at finding creative solutions to difficult problems, can rush through tasks in order to get them done on time
 
**Adwith**: 
- Technical Strengths: Java, Python, Javascript & Typescript (React, React Native, Node, Express), Algos
- Technical Weaknesses: Front end, HTML/CSS
- General Strengths: Creative
- General Weaknesses: Can get entrenched in a poor solution. 


**Project Idea(s):** _Fill this in with three unique ideas! (Due by March 1)_
### Idea 1

**Head-to-Head GeoGuessr**

**Overview:** We discovered that our group shares a common love for the Google Street View-based game GeoGuessr, which involves placing a player at a random location around the globe and having them guess where they are. We envision creating a faster-paced head-to-head variation of GeoGuessr in which players are placed at the same start location in a Google Street View and are given some nearby target destination (landmark/building/street) that they must race to. 

**Problems to be solved:** We feel that our variation of GeoGuessr addresses two main problems. Firstly, GeoGuessr’s primary mode is based on a single player format. We think that creating a faster-paced multiplayer environment within Google Street View would be an immersive and fun experience that hasn’t been as well-addressed. Secondly, in 2019, GeoGuessr added a paywall to their game that only allows players to play one free daily game unless they purchase a pro subscription. We feel that, since Google’s Street View API is so widely available, there shouldn’t be a cost to play a game utilizing this concept. 

**Features:** 
- Google Street View API access (they give a $200 free credit per month, which is enough to cover 14,000+ requests, which is more than enough for our project’s needs). This is the most critical feature because our project relies on interactive panorama imaging. The most challenging thing for this feature will be familiarizing ourselves with the extensive documentation.  
- Google Places API (also a part of the broader Google Maps Platform that Google Street View is in). This API contains data for “establishments, geographic locations, or prominent points of interest contained within a defined area”, which we could use to find target locations for players. This could require algorithmic complexity if we create some sort of system to sift through different places and choose the “best” one based on some criteria.
- A well-designed UI that hosts the Street View and contains extra elements such as timers, player names, and a scoreboard. Ensuring that this UI doesn’t overwhelm the Street View will be the most challenging part.
- The ability to create user accounts and add friends. Adding a friendship graph is another option for algorithmic complexity. The most challenging part for this feature would be the friendship graph, if we chose to add one.

**HTA Approval (crusch):** Idea approved, but a few things: the Google Street View API is very expensive, so it may be very difficult to develop and test your application while staying under the request limit. Your idea needs more of a concrete algorithm, but there's definitely potential in this idea to come up with something good. No resubmission required.



### Idea 2

**Currently - A Social Media Web App** 

**Overview:** Everyone has been engaging in more show/movie watching, music listening, and book reading in the past year or so. We want to create a social platform that allows users to create a profile of what they’re currently watching, listening to, and reading. Users can view their friends’ pages to keep up on the latest content that they’ve been interacting with. A recommendation algorithm would also use data about users’ current and past content and recommend them new friends who share similar tastes in shows/movies, music, and/or books. 

**Problems to be solved:** Remembering content that your friends love and recommend to you can be difficult, especially when some recommendations may be hiding in your texts while others were told to you in-person or on a call. This app would solve this issue by giving users a reliable way to find this information without having to admit to their friends that they forgot their recommendations... 

**Features:**
- Profile pages for every user where they can create “cards” (or some other UI element) containing some info about the show/movie, song/album/artist, or book that they’ve currently been engaging with. These cards would give users the option to add notes/descriptions containing their thoughts. The most challenging part of this feature will be creating a readable design for this card-like element.
- A friend searching section/feature that will query some sort of user database. The hardest part of this feature will be constructing efficient database queries.
- A recommendation algorithm that utilizes information on genres, similar content, etc. to match users with related tastes and recommend the top matches. The most difficult part will be determining what information should be considered for this algorithm.
- As somewhat of a stretch goal, accessing APIs for movies/shows, music, and books to make more robust card UI (adding things like thumbnails) and strengthening the algorithm. The toughest part will be integrating multiple APIs on one platform.

**HTA Approval (crusch):** Idea approved contingent on additional algorithm specification — please don't use ML in your recommendation algorithm, it usually goes poorly in 32 projects. If you really want to use this idea, please resubmit and specify more about what your recommendation algorithm would entail.

### Idea 3

**Color Scheme/Doodler -> Music Recommender**

**Overview:** A user can create an image on some sort of digital canvas/whiteboard and our algorithm will analyze the image’s sentiment on the basis of color themes. Using the sentiment, we will use the Spotify API to recommend songs, artists, albums or create playlists that match the sentiment. 

**Problems to be solved:** Finding the perfect song or artist that embodies a particular “vibe” is a difficult process, especially when one is asked to describe the sentiment in words. It can often be easier to visually represent a mood or feeling. This app would allow users to tap into their artistic creativity as a means of capturing a certain vibe and our algorithm would do the rest. 

**Features:**
- A basic canvas/whiteboard GUI with click-and-drag drawing, shapes, and colors where users can doodle (possible using some sort of canvas/graphics library). The most challenging part of this would be determining how many graphical features would be sufficient.
- An algorithm that takes in a user-created image and uses color schemes, shapes, and other visual elements to assign certain sentiment keywords with corresponding weights. Determining what elements to take into account, how to weight things, and ensuring that our algorithm is backed by actual sentiment research will make the creation of this feature difficult.
- Spotify API access. The Spotify API contains many helpful endpoints such as generating recommendations based on “seed” tracks, artists, or albums and a set of parameters that are closely related to sentiment. Some examples are danceability, energy, and acousticness. The toughest part will be judging how our sentiment analysis can be best translated to API calls.

**HTA Approval (crusch):** Idea rejected. This seems pretty complex (use of CV, recommendation algo, etc.) and will likely be too complicated for the scope of a 32 project. Really cool idea though!



**Mentor TA:** Grace Bramley-Simmons grace_bramley-simmons@brown.edu

## Meetings
_On your first meeting with your mentor TA, you should plan dates for at least the following meetings:_

**Specs, Mockup, and Design Meeting:** _(Schedule for on or before March 15)_

**4-Way Checkpoint:** _(Schedule for on or before April 5)_

**Adversary Checkpoint:** _(Schedule for on or before April 12 once you are assigned an adversary TA)_

## How to Build and Run
_A necessary part of any README!_
# mapdash
