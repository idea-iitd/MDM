package uct;

/**
 * State used for planning. Must have equality and hashcode functions as well as a copy function
 * so we can manipulate and compare states while planning.
 * @author Jeshua Bratman
 */
public interface State1 {
	
	public abstract boolean equals(Object other);
	
	//public void populateFreqOfSuccess();

	public abstract int hashCode();

	public abstract MDPState1 copy();

	public abstract boolean isAbsorbing();
	
	public abstract void printState();
}