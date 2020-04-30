package TaxiRecommendation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class UCTCostComparison {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
		
		
		FileInputStream f1=new FileInputStream("UCT_Baseline_Comparison_Decrease_1KM.csv");
		BufferedReader br=new BufferedReader(new InputStreamReader(f1));
		String str;
		int bestCostPerMetre[]=new int[2];
		int countOfNoRecoomendation[]= new int[2];
		while((str=br.readLine())!=null)
		{
			
			String s[] =str.split(",");
			float min=Float.MAX_VALUE;
			int index=0;
			int count=0;
			
			for(int i=0;i<s.length;i++)
			{
				
				if(Float.parseFloat(s[i])==10000)
				{
					count++;
					countOfNoRecoomendation[i]++;
				}
				else
				{
					float value=Float.parseFloat(s[i]);
					if(value<min)
					{
						
						min=value;
						
					}			
					
				}
					
				
			}
			
			
			if(count<bestCostPerMetre.length)
			{

				for(int i=0;i<s.length;i++)
				{
					if(Float.parseFloat(s[i])==min)
						
						bestCostPerMetre[i]++;	
					
				}
			}
			
			
			
			
			

			
			
			}     //end of while loop
			
			
			
		
		
		for(int i=0;i<bestCostPerMetre.length;i++)
		{
			
			//System.out.println("costpermeter performed best for "+bestCostPerMetre[i]+" locations"+","+" count of No Recommendation "+countOfNoRecoomendation[i]);
			System.out.println("Baseline Performance on length is :"+bestCostPerMetre[i]+" locations"+","+" count of No Recommendation "+countOfNoRecoomendation[i]);
			
		}
			
			
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		

	}


