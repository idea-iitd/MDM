//This file can be used to spli the sorted and unsorted can data based on trips into test and train.
//If isCrossValidation = true,we can create multiple validation sets of the same data


package recomendation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;

public class splitDriverData {
	
	public static int[] dataSize={10}; //{1,5,10,15,20,25,30,50,75,90};
	//public static int[] dataSize={1,5,15,20};
	public static String dataType="Top";
	
	public static boolean isCrossValidation = true;
	
	public static int noOfCrossValidation = 10;
	
	public static String dataPath = "/home/nandani/Documents/sem-3/taxi-project2/Datasets/SFCrossValidationData";
	
	//public static String OUTPUT_FOLDER = "/home/nandani/Documents/sem-3/taxi-project2/Datasets/SFExperimentData/";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ArrayList<String> driverFrequencies=new ArrayList<>();
		ArrayList<String> sortedDriverFrequencies=new ArrayList<>();
		try {
			BufferedReader bufferedReader1 = new BufferedReader(new FileReader(dataPath+"/_cabs.txt"));
			String str1;
			while((str1 = bufferedReader1.readLine()) != null) {
				
				driverFrequencies.add(str1);
				
				
			}
			
		
			BufferedReader bufferedReader2 = new BufferedReader(new FileReader(dataPath+"/topall.txt"));
			String str2;
			while((str2 = bufferedReader2.readLine()) != null) {
				
				sortedDriverFrequencies.add(str2);
				
				
			}
			
			for(int j=0;j<dataSize.length;j++)
			{
			//LowerPart Data- "LowerPartData/1yrData/FOIL2010Results/1/driverData/"+dataType+"/input/Train"+dataSize[j]+".csv"
			PrintStream out1=new PrintStream(new File(dataPath+"/"+dataType+"/Train"+dataSize[j]+".csv"));
			PrintStream out2=new PrintStream(new File(dataPath+"/"+dataType+"/Test"+dataSize[j]+".csv"));
			
			//Case1: Normal data without cross validation
			if(dataType=="Normal" && isCrossValidation==false)
			{
			System.out.println(driverFrequencies.size());
			float datalength=((driverFrequencies.size())*((dataSize[j]*1.0f/100.0f)));
			int driverSize=Math.round(datalength);
			System.out.println(driverSize);
			for(int i=0;i<driverFrequencies.size();i++)
			{
				if(i<driverSize)
				{
					
					out1.println(driverFrequencies.get(i));
					
				}
				else
					out2.println(driverFrequencies.get(i));	
				
			}
			}
		
			//Case2: Top% data without cross validation
			else if(dataType =="Top" && isCrossValidation==false)
			{
				
				System.out.println(sortedDriverFrequencies.size());
				float datalength=((sortedDriverFrequencies.size())*((dataSize[j]*1.0f/100.0f)));
				int driverSize=Math.round(datalength);
				System.out.println(driverSize);
				for(int i=0;i<sortedDriverFrequencies.size();i++)
				{
					if(i<driverSize)
					{
						
						out1.println(sortedDriverFrequencies.get(i));
						
					}
					else
						out2.println(sortedDriverFrequencies.get(i));
					
					
					
				}
			}
			
			
			
			//Case3:Normal data with cross validation
			else if(dataType=="Normal" && isCrossValidation==true)
			{
			CrossValidation obj=new CrossValidation(driverFrequencies.size(),noOfCrossValidation);
			int t;
		    for(int i=0;i<noOfCrossValidation;i++)
		    {
		    t=i+1;
		    PrintStream outTrain=new PrintStream(new File(dataPath+"/"+dataType+"/Train"+dataSize[j]+"_"+t+".csv"));
			PrintStream outTest=new PrintStream(new File(dataPath+"/"+dataType+"/Test"+dataSize[j]+"_"+t+".csv"));
		    	for(int k=0;k<obj.train[i].length;k++)
		    	{
		    		
		    		//System.out.println(obj.train[i][k]);
		    	
		    		outTrain.println(driverFrequencies.get(obj.train[i][k]));
		    		
		    		
		    	}
		    	
		    	
		    	for(int s=0;s<obj.test[i].length;s++)
		    	{
		    		
		    		outTest.println(driverFrequencies.get(obj.test[i][s]));
		    		
		    	}
		    	
		    }
			
			
			}
			
			
			//Case4: Top% data without cross validation
			else if(dataType=="Top" && isCrossValidation==true)
			{
				
				float datalength=((sortedDriverFrequencies.size())*((dataSize[j]*1.0f/100.0f)));
				int driverSize=Math.round(datalength);
				
				CrossValidation obj=new CrossValidation(driverSize,noOfCrossValidation);
				HashSet<Integer> testabs = new HashSet<>();
				int t;
			    for(int i=0;i<noOfCrossValidation;i++)
			    {
			    	 t=i+1;
			    	 PrintStream outTrain=new PrintStream(new File(dataPath+"/"+dataType+"/Train"+dataSize[j]+"_"+t+".csv"));
					 PrintStream outTest=new PrintStream(new File(dataPath+"/"+dataType+"/Test"+dataSize[j]+"_"+t+".csv"));
			    	
			    	
			    	for(int k=0;k<obj.train[i].length;k++)
			    	{
			    		
			    		outTrain.println(sortedDriverFrequencies.get(obj.train[i][k]));
			    		
			    		
			    	}
			    	
			    	for(int s=0;s<obj.test[i].length;s++)
			    	{
			    		
			    		outTest.println(sortedDriverFrequencies.get(obj.test[i][s]));
			    		
			    	}
			    	
			    }
			    
				
				
				
			}
				
				
				
				
			}
			
		
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		

	}

}
