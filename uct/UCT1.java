package uct;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import driver.Centralized;

import recomendation.RoadNetwork;
import recomendation.SegmentProcess;

//import uct.UCTNodes.UCTNodeStore;
//import uct.UCTNodes.UCTStateNode;

import uct.UCTNodes1.*;
//import uct.Utils.Maximizer;
import uct.Utils1.Maximizer1;


/**
 * UCT Planning algorithm. Takes a simulator and search parameters, then call
 * plan() to do UCT planning to estimate Q values at root node.
 * 
 * This implementation of UCT builds a search tree explicitly. An alternative 
 * implementation uses a hash function to retain state/depth pairs. The latter method
 * has the advantage of saving memory, but this tree-based implementation requires 
 * fewer evaluations of the hash function and has the added benefit of providing easy
 * visualization of the UCT search tree. If the memory requirements are too high
 * then the flat implementation would be better. As it stands, the bulk of UCT's 
 * computation usually lies in the simulation steps and state reward evaluation.
 * 
 * @author Jeshua Bratman
 */



public class UCT1 {
	// this is the C value for UCB:
		
		//public double costPerMetre=0.25;
		
		// ================================================================================
		// PUBLIC INTERFACE

		/**
		 * Create a new UCT planner
		 * 
		 * @param sim
		 *            Simulator object (note: this will be modified so pass in a copy!)
		 * @param trajectories
		 *            Number of trajectories per planning step.
		 * @param depth
		 *            Maximum search depth per trajectory.
		 * @param gamma
		 *            Discount factor.
		 * @param random
		 *            Random number generator for all tie breakers and action
		 *            decisions.
		 */
		public UCT1(SegmentSimulator1 sim, int trajectories, int distanceThreshold,
				Random random,RoadNetwork road) {
			this.random = random;
			this.maximizer = new Maximizer1(random);
			this.maxDistance = distanceThreshold;
			this.numTrajectories = trajectories;
			this.simulator = sim;
			//this.numActions = sim.getNumActions();
			this.cache = new UCTNodes1.UCTNodeStore1();
			this.root = null;
			this.roadNetwork=road;
			routeRecommended=new ArrayList<SegmentProcess>();
			this.count=0;
			this.initialSegmentLength=0.0;
			this.initialSegment=null;
			this.flagActionZero=0;
			this.alphaTest = Centralized.alpha;
			
			
		}
		
		
		public int planAndAct(MDPState1 mdpstate,int noOfActions) {
			mdpstate.printState();
			cache.clearHash();
			this.rootState=mdpstate;
			this.root=cache.checkout(rootState,0,noOfActions);
			for (int i = 0; i < this.numTrajectories; ++i) {
				//System.out.println("\ntrajectory " + (i + 1) + ":");
				simulator.setState(mdpstate.copy());
				if(i>0){
				simulator.populateNeighbor();}
				plan(mdpstate.copy(), root, 0,  mdpstate.segmentLength);
				
				
			}
			return getGreedyAction(noOfActions);
		}
		
		
		public int planAndActV(MDPState1 mdpstate,int noOfActions, List<Long> process) { //planAndAct for Verma method
			mdpstate.printState();
			this.rootState=mdpstate;
			this.root=cache.checkout(rootState,0,noOfActions);
			simulator.setState(mdpstate.copy());
			planV(mdpstate.copy(), root, 0,  mdpstate.segmentLength, process,2); //start after index 1 in process 
			return getGreedyAction(noOfActions);
		}
		
		
		
		
		protected int getGreedyAction(int n) {
			maximizer.clear();
			double[] Q = root.Q;
			for (int a = 0; a < n; a++) {
				maximizer.add(Q[a], a);
			}
			return maximizer.getMaxIndex();
		}
		
		
		
		
		public UCTNodeStore1 getCache()
		{
			
			return this.cache;
		}
		
		
		

		/**
		 * Get the current Q value for a given action.
		 * Note: you must call plan(State) first.
		 */
		public double getQ(int action) {
			return root.Q[action];
		}
		
		/**
		 * Get the current saCount value for a given action.
		 * Note: you must call plan(State) first.
		 */
		
		public int getSaCount(int action)
		{
			return root.saCounts[action];
		}
		
		/**
		 * Get the current sCount value for a given root.
		 * Note: you must call plan(State) first.
		 */
		
		public int getSCount()
		{
			return root.sCount;
		}
			
		/**
		This method will change the sign to opposite of a double value
		@param double x-value to be modified
		@return double value-value after modification
		**/
		
		public double changeSign(double x)
		{
			return(-x);
		}
				
				
		
		// ======================================================================
		// IMPLEMENTATION

		protected Random random;
		protected SegmentSimulator1 simulator;
		//protected int numActions;
		protected MDPState1 rootState;
		protected int maxDistance;
		protected int numTrajectories;
		protected UCTStateNode1 root;
		protected UCTNodeStore1 cache;
		protected Maximizer1 maximizer;
		protected RoadNetwork roadNetwork;
		public static ArrayList<SegmentProcess> routeRecommended=new ArrayList<SegmentProcess>();
		public int count;
		public double initialSegmentLength;
		public int flagActionZero;
		public SegmentProcess initialSegment;
		public double alphaTest;
		
		

		/**
		 * UCT planning procedure for training
		 * 
		 * @param state
		 *            Current state.
		 * @param node
		 *            Current node in the uct tree.
		 * @param depth
		 *            Current depth.
		 * @return
		 */
		
		
		protected double plan(MDPState1 mdpstate, UCTStateNode1 node,int depth, double length) {
			
			if(count==0)
			{
				initialSegmentLength=length;
			}
			
			//System.out.println("length "+(length));
			if(length-initialSegmentLength>maxDistance)
			{
				//System.out.println("No recommendation,length exceeds 1km");
				return ((length-initialSegmentLength)*DemoUCT1.costPerMetre);
				//return 0;
			}
			else{
				int action = getPlanningAction(node,simulator.numActions);
				simulator.takeAction(action);
				if(simulator.numActions==0 && simulator.getState().isAbsorbing()==false)
				{
					//System.out.println("case2");
					flagActionZero=1;
					MDPState1 mdpstate2 = simulator.getState().copy();
					UCTStateNode1 child = node.getChildNode(action, mdpstate2,depth+1,simulator.numActions);
					node.sCount++;
					int sa_count = ++node.saCounts[action];
					
					// compute rolling average for Q
					double alpha = 1d/sa_count;
					node.Q[action] += (changeSign(this.maxDistance*DemoUCT1.costPerMetre)- node.Q[action]) * alpha;
					//System.out.println("No recommendation,no further actions");
					return (this.maxDistance*DemoUCT1.costPerMetre);
				}
				
				else
					if(simulator.getState().isAbsorbing()==true)
				{
					//System.out.println("case3");
					MDPState1 mdpstate2 = simulator.getState().copy();
					UCTStateNode1 child = node.getChildNode(action, mdpstate2,depth+1,simulator.numActions);
					node.sCount++;
					int sa_count = ++node.saCounts[action];
					
					// compute rolling average for Q
					double alpha = 1d/sa_count;
					//System.out.println("Q B4"+node.Q[action]);
					//System.out.println("finalLength"+(simulator.getState().segmentLength+length-initialSegmentLength));
					node.Q[action] += (changeSign((simulator.getState().segmentLength+length-initialSegmentLength))*DemoUCT1.costPerMetre - node.Q[action]) * alpha;
					//node.Q[action] += (changeSign((simulator.getState().segmentLength))*DemoUCT1.costPerMetre - node.Q[action]) * alpha;
					//System.out.println("Q After"+node.Q[action]);
					return (simulator.getState().segmentLength+length-initialSegmentLength)*DemoUCT1.costPerMetre;
					//return (simulator.getState().segmentLength)*DemoUCT1.costPerMetre;
				}
				
				else
				{
					count++;
					MDPState1 mdpstate2 = simulator.getState().copy();
					UCTStateNode1 child = node.getChildNode(action, mdpstate2,depth+1,simulator.numActions);
					double q = plan(mdpstate2, child, depth + 1, mdpstate2.segmentLength+length);
					//System.out.println(q);
					//update counts
					node.sCount++;
					int sa_count = ++node.saCounts[action];
					
					// compute rolling average for Q
					double alpha = 1d/sa_count;
					//System.out.println("Before"+node.Q[action]);
					//modification done in the code..uncomment for original code			//node.Q[action] += (changeSign(q)- node.Q[action]) * alpha;
					if(flagActionZero==0) ///ITS UTILITY IS EXPLAINED IN FURTHER PLAN FUNCTIONS
					{
						node.Q[action] += (changeSign(q)- node.Q[action]) * alpha; //for different reward from each node,see planV(VERMA CODE)
						return q;
					}
					else
					{
						node.Q[action] += (changeSign(q)- node.Q[action]) * alpha;
						return q;
					}
				}
				
				
				
			}
			
		}
		
		
		
		/**
		 * UCT planning procedure for training for Verma work
		 * 
		 * @param state
		 *            Current state.
		 * @param node
		 *            Current node in the uct tree.
		 * @param depth
		 *            Current depth.
		 * @return
		 */
		
		
		protected double planV(MDPState1 mdpstate, UCTStateNode1 node,int depth, double length, List<Long> process,int index) {
			
			if(count==0)
			{
				initialSegmentLength=length;
			}
			
			//System.out.println("length "+(length));
			if(length-initialSegmentLength>maxDistance)
			{
				//System.out.println("No recommendation,length exceeds 1km");
				return ((length-initialSegmentLength)*DemoUCT1.costPerMetre);
				//modification done in the code..uncomment for modified code//
				//return 0;
			}
			else{
				SegmentUCB[] ob;
				if(DemoUCT1.VermaValuesList.containsKey(mdpstate.segment.toString())==false)
				{
					int noofActions = simulator.numActions;
					ob = new SegmentUCB[noofActions];
					
					
				}
				else
				{
					ob = DemoUCT1.VermaValuesList.get(mdpstate.segment.toString());
					
					
				}
				
				
				
				
				
				int action = getPlanningActionV(node,simulator.numActions,process,index);
				simulator.takeAction(action);
				if(simulator.numActions==0 && index<process.size()-1)
				{
					//System.out.println("case2");
					flagActionZero=1;
					MDPState1 mdpstate2 = simulator.getState().copy();
					UCTStateNode1 child = node.getChildNode(action, mdpstate2,depth+1,simulator.numActions);
					node.sCount++;
					int sa_count = ++node.saCounts[action];
					
					// compute rolling average for Q
					double alpha = 1d/sa_count;
					node.Q[action] += (changeSign(this.maxDistance*DemoUCT1.costPerMetre)- node.Q[action]) * alpha;
					SegmentUCB store = new SegmentUCB(node.Q[action],node.saCounts[action]);
					ob[action]= store;
					DemoUCT1.VermaValuesList.put(mdpstate.segment.toString(), ob);
					//System.out.println("No recommendation,no further actions");
					return (this.maxDistance*DemoUCT1.costPerMetre);
				}
				
				else
					if(index==process.size()-1)
				{
					//System.out.println("case3");
					MDPState1 mdpstate2 = simulator.getState().copy();
					UCTStateNode1 child = node.getChildNode(action, mdpstate2,depth+1,simulator.numActions);
					node.sCount++;
					int sa_count = ++node.saCounts[action];
					
					// compute rolling average for Q
					double alpha = 1d/sa_count;
					//System.out.println("Q B4"+node.Q[action]);
					//System.out.println("finalLength"+(simulator.getState().segmentLength+length-initialSegmentLength));
					//modification done in the code..uncomment for modified code		
					//node.Q[action] += (changeSign((simulator.getState().segmentLength))*DemoUCT1.costPerMetre - node.Q[action]) * alpha;
					
					node.Q[action] += (changeSign((simulator.getState().segmentLength+length-initialSegmentLength))*DemoUCT1.costPerMetre - node.Q[action]) * alpha;
					SegmentUCB store = new SegmentUCB(node.Q[action],node.saCounts[action]);
					ob[action]= store;
					DemoUCT1.VermaValuesList.put(mdpstate.segment.toString(), ob);
					return (length-initialSegmentLength)*DemoUCT1.costPerMetre;
					
					//System.out.println("Q After"+node.Q[action]);
					//modification done in the code..uncomment for modified code	//
					//return (simulator.getState().segmentLength)*DemoUCT1.costPerMetre;
				}
				
				else
				{
					count++;
					MDPState1 mdpstate2 = simulator.getState().copy();
					UCTStateNode1 child = node.getChildNode(action, mdpstate2,depth+1,simulator.numActions);
					double q = planV(mdpstate2, child, depth + 1, mdpstate2.segmentLength+length,process,index+1);
					//System.out.println(q);
					//update counts
					node.sCount++;
					int sa_count = ++node.saCounts[action];
					
					
					// compute rolling average for Q
					double alpha = 1d/sa_count;
					//System.out.println("Before"+node.Q[action]);
					//modification done in the code..uncomment for original code			//node.Q[action] += (changeSign(q)- node.Q[action]) * alpha;
					if(flagActionZero==0) //this flag makes sure that the same reward is passed to each node when there are no further actions and different rewards to each node according to length if actions are non-zero
					{
					node.Q[action] += (changeSign(q)- node.Q[action]) * alpha;
					SegmentUCB store = new SegmentUCB(node.Q[action],node.saCounts[action]);
					ob[action]= store;
					DemoUCT1.VermaValuesList.put(mdpstate.segment.toString(), ob);
					//System.out.println("After"+node.Q[action]);
					//modification done in the code..uncomment for original code	//return q;
					return q-(mdpstate2.segmentLength*DemoUCT1.costPerMetre);
					}
					else
					{
						node.Q[action] += (changeSign(q)- node.Q[action]) * alpha;
						SegmentUCB store = new SegmentUCB(node.Q[action],node.saCounts[action]);
						ob[action]= store;
						DemoUCT1.VermaValuesList.put(mdpstate.segment.toString(), ob);
						return q;
					}
				}
				
				
				
			}
			
		}
		
		
		
		
		
		
		/**
		 * UCT planning procedure for testing
		 * 
		 * @param mdpstate
		 *            Current state.
		 * @param length
		 *            Current segmentlength + length of path traversed in further iterations.
		 * @param segment
		 *            Current segment.
		 * @return
		 */
		
		
	
		
		protected double planForTesting(MDPState1 mdpstate,double length,SegmentProcess segment)
		{
			double costy= DemoUCT1.testingCost;
			//System.out.println("length "+(length));
			if(count==0)
			{
				initialSegmentLength=length;
				initialSegment=segment;
			}
			
			if(count!=0)
			{
			routeRecommended.add(segment);
			}
			
			
			ArrayList<SegmentUCB> segmentUCBObj=new ArrayList<SegmentUCB>();
			segmentUCBObj=DemoUCT1.testSegmentUCBValuesList.get(segment.toString());
			int sCount=DemoUCT1.segmentSCountList.get(mdpstate.segment);
			if(length-initialSegmentLength>maxDistance)
			{
				
				//System.out.println("No recommendation,length exceeds 1km");
				//DemoUCT1.out1.println("No recommendation,length exceeds 1km");
				/**
				 * uncomment this line later
				 * 
				 */
				DemoUCT1.out1.println(maxDistance+1000);
				//System.out.print("Net Profit on route of length "+(length-initialSegmentLength)+" is:");
				//System.out.println(roadNetwork.NetProfitOnRoute(routeRecommended,DemoUCT1.timeSlot));
				return (((length-initialSegmentLength)*costy))/*DemoUCT1.costPerMetre*/;
				//return 0;
			}
			else
			{
				int action = getPlanningActionForTesting(simulator.numActions,mdpstate,segment.toString());
				simulator.takeAction(action);
				
				if(simulator.numActions==0 && simulator.getState().isAbsorbing()==false)
				{
					//routeRecommended.add(simulator.getState().segment);
					//System.out.println("case2");
					//System.out.println("No recommendation,no further actions");
					//DemoUCT1.out1.println("No recommendation,no further actions");
					/**
					 * uncomment this line later
					 * 
					 */
					DemoUCT1.out1.println(maxDistance+1000);
					flagActionZero=1;
			
					sCount++;
					DemoUCT1.segmentSCountList.put(mdpstate.segment,sCount);
					int sa_count = ++segmentUCBObj.get(action).saCount;
					
					// compute rolling average for Q
					//uncomment it for stationary distribution
					double alpha = 1d/sa_count;
					segmentUCBObj.get(action).Q += (changeSign(this.maxDistance*costy/*DemoUCT1.costPerMetre*/)- segmentUCBObj.get(action).Q) * alphaTest; 
					DemoUCT1.testSegmentUCBValuesList.remove(segment.toString());
					DemoUCT1.testSegmentUCBValuesList.put(segment.toString(),segmentUCBObj);
					return (this.maxDistance*costy/*DemoUCT1.costPerMetre*/);
					
					
					
					
				}
				
				else
					if(simulator.getState().isAbsorbing()==true)
				{
					//System.out.println("case3");
					routeRecommended.add(simulator.getState().segment);
				
					sCount++;
					DemoUCT1.segmentSCountList.put(mdpstate.segment,sCount);
					int sa_count = ++segmentUCBObj.get(action).saCount;
					
					// compute rolling average for Q
					//uncomment it for stationary distribution
					//double alpha = 1d/sa_count;
					//System.out.println("Q before"+segmentUCBObj.get(action).Q);
					//System.out.println((simulator.getState().segmentLength+length-initialSegmentLength));
					segmentUCBObj.get(action).Q += (changeSign(simulator.getState().segmentLength+length-initialSegmentLength)*costy - segmentUCBObj.get(action).Q) * alphaTest;
					//segmentUCBObj.get(action).Q += (changeSign(simulator.getState().segmentLength)*costy- segmentUCBObj.get(action).Q) * alphaTest; 
					//System.out.println("Q after"+segmentUCBObj.get(action).Q);
					DemoUCT1.testSegmentUCBValuesList.remove(segment.toString());
					DemoUCT1.testSegmentUCBValuesList.put(segment.toString(),segmentUCBObj);
					//System.out.print(initialSegment+"	:	"+(simulator.getState().segmentLength+length-initialSegmentLength)+" ");
					/**
					 * uncomment this line later
					 * 
					 */
					DemoUCT1.out1.println(simulator.getState().segmentLength+length-initialSegmentLength);
					//System.out.println(roadNetwork.NetProfitOnRoute(routeRecommended,DemoUCT1.timeSlot));
					//System.out.println(routeRecommended);
					//return ((simulator.getState().segmentLength)*costy);
					return ((simulator.getState().segmentLength+length-initialSegmentLength)*costy/*DemoUCT1.costPerMetre*/);
				}
				
				else
				{
					count++;
					MDPState1 mdpstate2 = simulator.getState().copy();
					//UCTStateNode1 child = node.getChildNode(action, mdpstate2,depth+1,simulator.numActions);*/
					double q = planForTesting(mdpstate2,mdpstate2.segmentLength+length,mdpstate2.segment);
					
					//update counts
			
					sCount++;
					DemoUCT1.segmentSCountList.put(mdpstate.segment,sCount);
					int sa_count = ++segmentUCBObj.get(action).saCount;
	
					
					// compute rolling average for Q
					//uncomment it for stationary distribution
					//double alpha = 1d/sa_count;
					//System.out.println("Q before"+segmentUCBObj.get(action).Q);
					if(flagActionZero==0) // in case wants to return rewards according to length of the intermediate nodes,see verma planV function
					{
						
						segmentUCBObj.get(action).Q += (changeSign(q) - segmentUCBObj.get(action).Q) * alphaTest;
						//System.out.println("Q after"+segmentUCBObj.get(action).Q);
						DemoUCT1.testSegmentUCBValuesList.remove(segment.toString());
						DemoUCT1.testSegmentUCBValuesList.put(segment.toString(),segmentUCBObj);
						//System.out.println(roadNetwork.NetProfitOnRoute(routeRecommended,DemoUCT1.timeSlot));
						return q;	
						
						
						
					}
					
					
					
					else
					{
				
					segmentUCBObj.get(action).Q += (changeSign(q) - segmentUCBObj.get(action).Q) * alphaTest;
					//System.out.println("Q after"+segmentUCBObj.get(action).Q);
					DemoUCT1.testSegmentUCBValuesList.remove(segment.toString());
					DemoUCT1.testSegmentUCBValuesList.put(segment.toString(),segmentUCBObj);
					//System.out.println(roadNetwork.NetProfitOnRoute(routeRecommended,DemoUCT1.timeSlot));
					return q;
					}
				}
				
				
				
			}
			
		}
		
				
		/**
		 * UCT planning procedure for testing for verma work
		 * 
		 * @param mdpstate
		 *            Current state.
		 * @param length
		 *            Current segmentlength + length of path traversed in further iterations.
		 * @param segment
		 *            Current segment.
		 * @return
		 */
		
		
	
		
		protected double planForTestingV(MDPState1 mdpstate,double length,SegmentProcess segment)
		{
			double costy= DemoUCT1.testingCost;
			if(count==0)
			{
				initialSegmentLength=length;
				initialSegment=segment;
			}
			
			if(count!=0)
			{
			routeRecommended.add(segment);
			}
			
			
			
			int sCount=DemoUCT1.segmentSCountList.get(mdpstate.segment);
			if(length-initialSegmentLength>maxDistance)
			{
				
				
				DemoUCT1.out1.println(maxDistance+1000);
				return 0;
			}
			else
			{
				int action = getPlanningActionForTestingV(simulator.numActions,mdpstate,segment.toString());
				simulator.takeAction(action);
				
				if(simulator.numActions==0 && simulator.getState().isAbsorbing()==false)
				{
				
					DemoUCT1.out1.println(maxDistance+1000);
					flagActionZero=1;
				
					return (this.maxDistance*costy);
					
					
					
					
				}
				
				else
					if(simulator.getState().isAbsorbing()==true)
				{
					routeRecommended.add(simulator.getState().segment);
				
					DemoUCT1.out1.println(simulator.getState().segmentLength+length-initialSegmentLength);
					return ((simulator.getState().segmentLength)*costy);
					//modification done //return ((simulator.getState().segmentLength+length-initialSegmentLength)*costy/*DemoUCT1.costPerMetre*/);
				}
				
				else
				{
					count++;
					MDPState1 mdpstate2 = simulator.getState().copy();
					
					double q = planForTestingV(mdpstate2,mdpstate2.segmentLength+length,mdpstate2.segment);
					
					if(flagActionZero==0)
					{
					
						return q+mdpstate2.segmentLength*costy;	
							
					}
					
					
					
					else
					{

					return q;
					}
				}
				
				
				
			}
			
		}
		
				
				
		/**
		 * Get the action at a given node without simulation - Verma's method
		 * @param node - state node at which to choose action
		 * @return - chosen action
		 */
		protected int getPlanningActionV(UCTStateNode1 node,int numActions,List<Long> process,int index) {
			//System.out.println("Recommendation : "+DemoUCT1.ucb_scaler);
			if (node == null) return random.nextInt(numActions);
			else {
				
					Long currentNode = process.get(index);
					ArrayList<MDPState1> listOfNeighbours=simulator.getNeighbourState();
					
					for(int a =0;a<listOfNeighbours.size();++a)
					{
						
						if(listOfNeighbours.get(a).segment.endLocation.id.equals(currentNode))
							return a;
						
					
					}
					return random.nextInt(numActions);	
				
			}
			
		}
				
		
		
		/**
		 * Get the action at a given node using the UCB rule
		 * @param node - state node at which to choose action
		 * @return - chosen action
		 */
		protected int getPlanningAction(UCTStateNode1 node,int numActions) {
			//System.out.println("Recommendation : "+DemoUCT1.ucb_scaler);
			if (node == null) return random.nextInt(numActions);
			else {
				maximizer.clear();
				double numerator = Math.log(node.sCount);
				for (int a = 0; a < numActions; ++a) {
					//val means cost here
					double val = node.Q[a];
					if (node.saCounts[a] == 0) val = Double.MAX_VALUE;
					//otherwise use UCB1 rule
					
					else {
						//System.out.println("exploit"+","+"explore of action"+a);
						//System.out.println(val+","+DemoUCT1.ucb_scaler * Math.sqrt(numerator / node.saCounts[a]));
						val += DemoUCT1.ucb_scaler * Math.sqrt(numerator / node.saCounts[a]); 
						//System.out.println("UCB after updation of action"+a+1);
						//System.out.println("val"+val);
					}
					
					//System.out.println("UCB "+val);
					maximizer.add(val, a);
				}
				return maximizer.getMaxIndex();			
			}
		}
		
		
		/**
		 * Get the action at a given node using the learnt parameters present in testSegmentUCBValuesList
		 * @param numActions -No of actions of current segment
		 * @param mdpstate-Current state
		 * @param startSegment-current segment in string format
		 * @return - chosen action
		 */
		
		protected int getPlanningActionForTesting(int numActions,MDPState1 mdpstate,String startSegment) {
			if (startSegment == null) return random.nextInt(numActions);
			else {
				maximizer.clear();
				ArrayList<SegmentUCB> segmentUCBObj=new ArrayList<SegmentUCB>();
				
				segmentUCBObj=DemoUCT1.testSegmentUCBValuesList.get(startSegment);
				
				int sCount=DemoUCT1.segmentSCountList.get(mdpstate.segment);
				double numerator=Math.log(sCount);
				for(int a=0;a<segmentUCBObj.size();a++)
				{
					double val = segmentUCBObj.get(a).Q;
					if (segmentUCBObj.get(a).saCount == 0) val = Double.MAX_VALUE;
					//otherwise use UCB1 rule
					else val += DemoUCT1.testingUcb_scaler *Math.sqrt(numerator /segmentUCBObj.get(a).saCount );
					//else val += 0.1* Math.sqrt(numerator /segmentUCBObj.get(a).saCount );
					//System.out.println("UCB "+val);
					maximizer.add(val, a);
				}
				return maximizer.getMaxIndex();			
					
		}
		
		
		
		}
		
		
		protected int getPlanningActionForTestingV(int numActions,MDPState1 mdpstate,String startSegment) {
			if (startSegment == null) return random.nextInt(numActions);
			else {
				maximizer.clear();
				ArrayList<SegmentUCB> segmentUCBObj=new ArrayList<SegmentUCB>();
				if(DemoUCT1.testSegmentUCBValuesList.containsKey(startSegment)==false)
					return random.nextInt(numActions);
				else
				{
				segmentUCBObj=DemoUCT1.testSegmentUCBValuesList.get(startSegment);
				for(int a=0;a<segmentUCBObj.size();a++)
				{
					double val = segmentUCBObj.get(a).Q;
					if (segmentUCBObj.get(a).saCount < 10 || segmentUCBObj.get(a).saCount==0)  return random.nextInt(numActions);
					//otherwise use UCB1 rule
					else val += 0;
					
					maximizer.add(val, a);
				}
				return maximizer.getMaxIndex();			
				}	
			}
		
		
		
		}
				
		
		
		
		
		
		
		
}
		
		
		
		

