package preprocessing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class removeLowFreqSegmentsFromData {

	/**
	 * @param args
	 */
	
	public static String dataType="Normal";
	public static int[] dataSplit={10,25,50,75,90};
	public static String[] dataPhase={"Train","Test"};
	
	
	
	
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		for(int i=0;i<dataSplit.length;i++)
		{
			for(int j=0;j<dataPhase.length;j++)
			{
			FileInputStream in =new FileInputStream("/home/nandani/Documents/sem-3/taxi-project2/Datasets/NYLatestData/"+dataType+"/"+dataType+Integer.toString(dataSplit[i])+dataPhase[j]+".csv");
			BufferedReader br=new BufferedReader(new InputStreamReader(in));
			String str="";
			while((str=br.readLine())!=null)
			{
				String s1[]=str.split(";");
				String s11[]=s1[0].split(",");
				String s12[]=s1[1].split(",");
				String startId=s11[0];
				String endId=s12[0];
				
				
				
				
				
				
				
				
				
				
				
				
				
			}
			}
			
			
			
			
		}
		
		
		
		
		
		

	}

}
