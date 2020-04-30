package uct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import recomendation.SegmentProcess;
//import uct.UCTNodes.UCTActionNode;
//import uct.UCTNodes.UCTStateNode;



public class UCTNodes1 {	
	/**
	 * Associates UCT state nodes with a state/depth pair so we can share information between
	 * states at the same depth. 
	 * 
	 * @author Jeshua Bratman
	 */
	public static class UCTNodeStore1{
		//private int numActions;
		private HashMap<StateAtDepth1, UCTStateNode1> activeNodes;
		public UCTNodeStore1() {
			//this.numActions = numActions;
			this.activeNodes = new HashMap<StateAtDepth1,UCTStateNode1>();
		}

		/**
		 * Call this to get a state node object either from the hash table or
		 * by constructing a new instance.
		 * 
		 * @return
		 */
		public UCTStateNode1 checkout(MDPState1 mdpstate,int depth,int numActions) {
			 
			 StateAtDepth1 sad = new StateAtDepth1(mdpstate,depth);
			 if (activeNodes.containsKey(sad)) {			
					return activeNodes.get(sad);
				} else {
					UCTStateNode1 n = new UCTStateNode1(this, numActions, mdpstate);
					n.state = mdpstate;
					n.depth = depth;
					activeNodes.put(sad, n);
					return n;
				}
			}
		
		
		
			
		public void clearHash() {
			this.activeNodes.clear();
		}
		
		}
	
	

	/**
	 * Represents an action in the UCT tree. For each state reached while taking this
	 * action from the parent, an action node has one state node child.
	 * @author Jeshua Bratman
	 */
	public static class UCTActionNode1 {
		private static final int INIT_SIZE = 5;
		//expand factor is used to expand the depth of the tree if it crosses 5
		private static final int EXPAND_FACTOR = 2;
		int currBranches;
		//TODO child ki states...why it is set to be 5 
		State1[] childStates = new State1[INIT_SIZE];
		//TODO child ki nodes...why it is set to be 5
		UCTStateNode1[] childNodes = new UCTStateNode1[INIT_SIZE];

		public UCTActionNode1() {
			this.currBranches = 0;
		}
// receives children state not parent one and returns children node if present in childStates
		UCTStateNode1 get(State1 state) {
			for (int i = 0; i < currBranches; ++i)
			{
				//System.out.println(childStates[i]+" "+ state);
				if (childStates[i].equals(state))
					return childNodes[i];}
			return null;
		}

		void add(State1 state, UCTStateNode1 node) {
			if (currBranches >= childNodes.length) {
				int newlen = EXPAND_FACTOR * childNodes.length;
				childNodes = Arrays.copyOf(childNodes, newlen);
				childStates = Arrays.copyOf(childStates, newlen);
			}
			childNodes[currBranches] = node;
			childStates[currBranches] = state;
			currBranches++;
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
		
		

		/**
		 * A state node represents a single state in the UCT tree. Each state node
		 * has a Q value associating current estimated value for each action and a
		 * action node child. Additionally, state nodes store statistics about
		 * the number of visits and actions attempted.
		 * 
		 * @author Jeshua Bratman
		 *
		 */
		public static class UCTStateNode1 
		{ 
			private static final double INITIAL_VALUE = 0d;
			
		    private final UCTNodeStore1 nodeCache;
		    public double Q[]; 
		    public State1 state;
		    public int depth;
		    public int sCount;//number of times visiting this state..parent count
		    public int saCounts[];//number of times visiting this state on each action...child count

		    UCTActionNode1[] children;

		    public UCTStateNode1(UCTNodeStore1 nodeCache, int numActions, MDPState1 mdpstate)
		    {
		        this.nodeCache = nodeCache;
		        this.Q = new double[numActions];
		        this.saCounts = new int[numActions];
		        this.children = new UCTActionNode1[numActions];
		        for(int a = 0; a < numActions; ++a)
		            children[a] = new UCTActionNode1();
		        
		        this.sCount = 1;
		        Arrays.fill(saCounts, 0);
		        Arrays.fill(Q, 0);
		        
		        ArrayList<SegmentProcess> neighbourList = DemoUCT1.roadNetwork.neighborSegment(mdpstate.segment);
				//ArrayList<MDPState1> neighbourMdpState = new ArrayList<MDPState1>();
				int i=0;
				//System.out.println("size"+neighbourList.size());
				//Earlier we have kept Q equal to potential cost ,now we will keep it as 0
				/*for(SegmentProcess segment : neighbourList)
				{
					//neighbourMdpState.add(new MDPState1(segment));
					Q[i] = segment.potentialCost;
					i++;
				}*/
    
		    }

		    
		    public UCTStateNode1 getChildNode(int action, MDPState1 mdpstate, int depth,int numActions) {
		    	//get action node for this action
		        UCTActionNode1 actNode = children[action];
		        //get state node associated with that action node
		        UCTStateNode1 child = actNode.get(mdpstate);
		        //if it is null, then we haven't visited state/action at this depth        
		        if(child == null){
		            child = nodeCache.checkout(mdpstate,depth,numActions);
		            actNode.add(mdpstate, child);
		        }        
		        return child;
		    }
		}
		
		
		
		
		
		
		
	}
