package preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;

import uct.UCT;

public class Distribution {

	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//PrintStream out1=new PrintStream(new File("Increase_Distribution.csv"));
		
		FileInputStream f1=new FileInputStream("Complete_Combined_Results.csv");
		BufferedReader br=new BufferedReader(new InputStreamReader(f1));
		int x[] =new int[17];
		int y[]=new int[17];
		String str;
		
		
		/*while((str=br.readLine())!=null)
		{
			
			String s[]=str.split(",");
			float baseline_value=Float.parseFloat(s[1]);
			float UCT_value=Float.parseFloat(s[2]);
			
			
			
			int baseline_mod=(int)baseline_value/100;
			int UCT_mod=(int)UCT_value/100;
			if(baseline_mod<=15)
				x[baseline_mod]=x[baseline_mod]+1;
			
			else
				x[x.length-1]=x[x.length-1]+1;
			
			
			
			if(UCT_mod<=15)
				y[UCT_mod]=y[UCT_mod]+1;
			
			else
				y[y.length-1]=y[y.length-1]+1;
			
			
			
			
		
			
			
			
		}
		
		
		
		for(int i=0;i<x.length;i++)
		{
			out1.print(x[i]+",");
			out1.println(y[i]);
		}
		
		
		
		*/
		
		
		//calculate average distance in both cases-
		
		float sum1=0.0f;
		float sum2=0.0f;
		int count1=0;
		int count2=0;
		
		while((str=br.readLine())!=null)
		{
			String s[]=str.split(",");
			float baseline_value=Float.parseFloat(s[1]);
			float UCT_value=Float.parseFloat(s[2]);
			
			
			if(baseline_value!=1500)
			{
				sum1=sum1+baseline_value;
				count1++;
			}
			
			
			if(UCT_value!=1500)
			{
				sum2=sum2+UCT_value;
				count2++;
			}
			
			
		}
		
		System.out.println(sum1/count1);
		System.out.println(sum2/count2);
		
		
		
		
		
		
		
		
		
		

	}

}
