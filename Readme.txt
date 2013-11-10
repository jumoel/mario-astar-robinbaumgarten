(Clone of the Mario A* agent made by Robin Baumgarten because his website doesn't allow for downloading at the moment - I take no credit for anything in here)

A* Mario AI v1.1 as used by me (Robin Baumgarten) for the CIG 2009 Mario AI competition.

Homepage: http://www.doc.ic.ac.uk/~rb1006/projects:marioai

Overview:
- AStarAgent.java: The entry point of the source-code, called by the API. It calls the optimize() function in AStarSimulator.java
- AStarSimulator.java: Contains the meat of the A* search. The SearchNode class contains a node, including its action, and a copy of the world state. The optimize() function sets up the A* planner, extracts the latest plan and returns an optimal action. search() contains the planning loop itself, which explores the search space.
- LevelScene.java: This class contains most of the world-state, and holds the Mario-object and enemy objects.

How to get it to run:
- Download the competition mario code from Julian's site.
- Put my Mario AI into the src/competition/cig/robinbaumgarten/ folder
- Launch \src\ch\idsia\scenarios\Play.java, changed so that it loads AStarAgent.java.

License
The source-code is published under the WTFPL (http://sam.zoy.org/wtfpl/, Do What The Fuck You Want To Public License). Basically, do whatever you like with the source code.

Of course, I'd be happy if you mention that it has been developed by me, and give me a heads up if you use it. 

hf,
Robin
