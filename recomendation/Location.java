/**
 * This class contains information about a location in the map.
 * The data members are a unique id, a latitude, and a longitude
 * @author stanley
 *
 */
package recomendation;
public class Location
	{
	public Long id;
	public double latitude;
	public double longitude;

	/**
	 * Creates a new instance given an id, latitude, and longitude.
	 * @param id A unique identifier of the location
	 * @param latitude The latitude of the location
	 * @param longitude The longitude of the location
	 */
	public Location(Long id, double latitude, double longitude)
		{
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		}
	
	public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        // object must be Pair at this point
        Location location = (Location)obj;
        return this.hashCode() == location.hashCode();
    }

	@Override
	public int hashCode()
	{
		return Long.toString(this.id).hashCode();
	}
	}