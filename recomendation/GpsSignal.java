package recomendation;
public class GpsSignal
	{
	public double latitude;
	public double longitude;
	public int isOccupied;
	public long unixTime;
	
	GpsSignal(double latitude, double longitude, int isOccupied, long unixTime)
		{
		this.latitude = latitude;
		this.longitude = longitude;
		this.isOccupied = isOccupied;
		this.unixTime = unixTime;
		}
	}