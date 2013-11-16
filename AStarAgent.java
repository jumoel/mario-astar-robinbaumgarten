package competition.cig.robinbaumgarten;

/* This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. */ 

import ch.idsia.agents.Agent;
import ch.idsia.agents.AgentLoader;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.benchmark.tasks.MarioCustomSystemOfValues;
import ch.idsia.tools.MarioAIOptions;

import competition.cig.robinbaumgarten.astar.AStarSimulator;
import competition.cig.robinbaumgarten.astar.sprites.Mario;

public class AStarAgent implements Agent
{
    public AStarAgent() {
		reset();
	}

	protected boolean action[] = new boolean[Environment.numberOfKeys];
    protected String name = "AStarAgent";
    private AStarSimulator sim;
    private float lastX = 0;
    private float lastY = 0;
    
    private Environment environment;
    
    public void reset()
    {
        action = new boolean[Environment.numberOfKeys];
        sim = new AStarSimulator();
    }
    
    public void integrateObservation(Environment environment) {
    	this.environment = environment; 
    }
    
    public boolean[] getAction() {
    	return getAction(environment);
    }

    public boolean[] getAction(Environment observation)
    {
    	// This is the main function that is called by the mario environment.
    	// we're supposed to compute and return an action in here.
    	
    	long startTime = System.currentTimeMillis();
    	
    	// everything with "verbose" in it is debug output. 
    	// Set Levelscene.verbose to a value greater than 0 to enable some debug output.
    	String s = "Fire";
    	if (!sim.levelScene.mario.fire)
    		s = "Large";
    	if (!sim.levelScene.mario.large)
    		s = "Small";
    	if (sim.levelScene.verbose > 0) System.out.println("Next action! Simulated Mariosize: " + s);

    	boolean[] ac = new boolean[Environment.numberOfKeys];
    	ac[Mario.KEY_RIGHT] = true;
    	ac[Mario.KEY_SPEED] = true;
    	
    	// get the environment and enemies from the Mario API
     	byte[][] scene = observation.getLevelSceneObservationZ(0);
    	float[] enemies = observation.getEnemiesFloatPos();
		float[] realMarioPos = observation.getMarioFloatPos();
   	
    	if (sim.levelScene.verbose > 2) System.out.println("Simulating using action: " + sim.printAction(action));
        
    	// Advance the simulator to the state of the "real" Mario state
    	sim.advanceStep(action);   
       		
		// Handle desynchronisation of mario and the environment.
		if (sim.levelScene.mario.x != realMarioPos[0] || sim.levelScene.mario.y != realMarioPos[1])
		{
			// Stop planning when we reach the goal (just assume we're in the goal when we don't move)
			if (realMarioPos[0] == lastX && realMarioPos[1] == lastY)
				return ac;

			// Some debug output
			if (sim.levelScene.verbose > 0) System.out.println("INACURATEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE!");
			if (sim.levelScene.verbose > 0) System.out.println("Real: "+realMarioPos[0]+" "+realMarioPos[1]
			      + " Est: "+ sim.levelScene.mario.x + " " + sim.levelScene.mario.y +
			      " Diff: " + (realMarioPos[0]- sim.levelScene.mario.x) + " " + (realMarioPos[1]-sim.levelScene.mario.y));
			
			// Set the simulator mario to the real coordinates (x and y) and estimated speeds (xa and ya)
			sim.levelScene.mario.x = realMarioPos[0];
			sim.levelScene.mario.xa = (realMarioPos[0] - lastX) *0.89f;
			if (Math.abs(sim.levelScene.mario.y - realMarioPos[1]) > 0.1f)
				sim.levelScene.mario.ya = (realMarioPos[1] - lastY) * 0.85f;// + 3f;

			sim.levelScene.mario.y = realMarioPos[1];
		}
		
		// Update the internal world to the new information received
		sim.setLevelPart(scene, enemies, realMarioPos);
        
		lastX = realMarioPos[0];
		lastY = realMarioPos[1];


		// This is the call to the simulator (where all the planning work takes place)
        action = sim.optimise();
        
        // Some time budgeting, so that we do not go over 40 ms in average.
        sim.timeBudget += 39 - (int)(System.currentTimeMillis() - startTime);
        return action;
    }

	@Override
	public void giveIntermediateReward(float intermediateReward) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setObservationDetails(int rfWidth, int rfHeight, int egoRow,
			int egoCol) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public static void main(String[] args)
	{
		final MarioAIOptions marioAIOptions = new MarioAIOptions("-srf on -vis on"); // -ag " + competition.cig.robinbaumgarten.AStarAgent.class.getName());
		//marioAIOptions.setVisualization(true);
		marioAIOptions.setAgent(AgentLoader.getInstance().loadAgent(competition.cig.robinbaumgarten.AStarAgent.class.getName(), false));
		//marioAIOptions.setLevelDifficulty(0);		

		final BasicTask basicTask = new BasicTask(marioAIOptions);

		boolean verbose = false;
		basicTask.doEpisodes(1, verbose, 1);

		final MarioCustomSystemOfValues m = new MarioCustomSystemOfValues();

		System.out.println("\nEvaluationInfo: \n" + basicTask.getEnvironment().getEvaluationInfoAsString());
		System.out.println("\nCustom : \n" + basicTask.getEnvironment().getEvaluationInfo().computeWeightedFitness(m));

		System.exit(0);
	}
}
