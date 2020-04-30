package preprocessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;

public class RemoveSegmentsWithNoNeighboursTesting {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
		PrintStream out1=new PrintStream("TestingData/SegmentWithNeighbours.csv");
		FileInputStream f1=new FileInputStream("UCT/Normal50/1000m/C1.41421356237/cost1.0/AllSegments/5/Output.csv");
		BufferedReader br1=new BufferedReader(new InputStreamReader(f1));
		FileInputStream f2=new FileInputStream("Baseline/Normal50/1000m/AllSegments/5/Output.csv");
		BufferedReader br2=new BufferedReader(new InputStreamReader(f2));
		HashMap<String,Double> storeAverageValue=new HashMap<>();
		String s1=" ";
		String s2=" ";
		
	
			while((s1=br1.readLine())!=null)
			{
		
				String average[]=s1.split(",");
				double value=Double.parseDouble(average[1]);
				if(value!=1500.0 && storeAverageValue.containsKey(average[0])==false)
				{
				storeAverageValue.put(average[0],Double.parseDouble(average[1]));
				out1.println(average[0]);
				}
		
			}
			
			while((s2=br2.readLine())!=null)
			{
		
				String average[]=s2.split(",");
				double value=Double.parseDouble(average[1]);
				if(value!=1500.0 && storeAverageValue.containsKey(average[0])==false)
				{
				storeAverageValue.put(average[0],Double.parseDouble(average[1]));
				out1.println(average[0]);
				}
		
		
			}
			
			
			
			
			
			
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
