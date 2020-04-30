package recomendation;

import java.util.ArrayList;

/**
 * 
 * @author hasit
 *
 */
public class Driver {
	ArrayList<GpsSignal> tour;
	String name;
	int numberOfTrips;

	/**
	 * @param tour
	 *            complete tour of the driver
	 * @param name
	 *            Name of the driver
	 */
	Driver(ArrayList<GpsSignal> tour, String name) {
		this.tour = tour;
		this.name = name;
		numberOfTrips = calculateNumberOfTrips();
	}

	public int getNumberOfCustomers() {
		return numberOfTrips;
	}

	private int calculateNumberOfTrips() {
		int noOfCustomers = 0;

		// If tour is empty
		if (tour.size() == 0) {
			return 0;
		}

		GpsSignal currentSignal = tour.get(0);

		int tourSize = tour.size();
		
		for (int i = 1; i < tourSize; ++i) {
			
			// get previous signal
			GpsSignal previousSignal = currentSignal;
			// get current signal
			currentSignal = tour.get(i);
			
			if(currentSignal.isOccupied != previousSignal.isOccupied) {
				if(currentSignal.isOccupied == 1) {
					noOfCustomers++;
				}
			}

		}

		return noOfCustomers;
	}
}
