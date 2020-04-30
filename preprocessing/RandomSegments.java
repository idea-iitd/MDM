package preprocessing;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Random;


public class RandomSegments {

	
	public static int TotalnoOfSegments=7963;
	
	public static int noOfRandomSegmnets=5000;
	
	public static void main(String[] args) throws IOException {
	
		// TODO Auto-generated method stub

		Random r = new Random();
		int Low = 0;
		int High = TotalnoOfSegments;
		int[] RandomNumbers=new int[5000];
		
		for(int i=0;i<5000;i++)
		{
		
		int Result = r.nextInt(High-Low) + Low;
		RandomNumbers[i]=Result;
		
		System.out.println(RandomNumbers[i]);
		
		}
		
		
		
		
		PrintStream out1=new PrintStream(new File("TestingData/Top50/Random5000SegmentsTest.csv"));
		//Reading test data from file CompleteTestDataSortedTimeslot2.csv
		/*for(int i=0;i<5;i++)
		{*/

		FileInputStream f1=new FileInputStream("TestingData/Top50/CompleteTest.csv");
		BufferedReader br=new BufferedReader(new InputStreamReader(f1));
		int count=0;
		String x[]=new String[TotalnoOfSegments];
		String str;
		
		
		while((str=br.readLine())!=null)
		{
				
				x[count]=str;
				
				count++;
		
		}
		
		String randomSegments[]=new String[noOfRandomSegmnets];
		
		for(int k=0;k<RandomNumbers.length;k++)
		{
			
			randomSegments[k]=x[RandomNumbers[k]];
			
			out1.println(randomSegments[k]);
		}
		
		
		
		
	}

}
