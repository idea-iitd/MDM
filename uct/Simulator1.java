package uct;

import java.util.ArrayList;
import java.util.Map;

import recomendation.SegmentProcess;

/**
 * Represents a generative model. All information should be kept in a state variable
 * so a planning algorithm can reset the simulator to specific states.
 * @author Jeshua Bratman
 */
public interface Simulator1 {
    public State1 getState();
    public void populateNeighbor();
    //public void populatingNeighbour(Map<SegmentProcess,ArrayList<Double>> segmentFreqMap);
    public void setState(State1 state);
    public void takeAction(int a);
    public void implementAction(int a);
    public int getNumActions();    
    public double getDiscountFactor();
    public double getReward(); 			//reward at current state 
    public void initEpisode();
    //newly added
    //public SegmentSimulator1 copy();
}