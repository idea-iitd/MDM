package TaxiRecommendation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Results {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		FileInputStream f1=new FileInputStream("/home/nandani/Documents/sem-3/taxi project2/Bandit/Length.csv");
		BufferedReader br=new BufferedReader(new InputStreamReader(f1));
		String s;
		/*int count_uct=0;
		int count_baseline=0;
		int count_equal=0;
		while((s=br.readLine())!=null)
		{
			if(s.startsWith("#"))
			{}
			else
			{
				double a=Double.parseDouble(s);
				if(a>0.0)
					count_uct++;
				if(a<0.0)
					count_baseline++;
				if(a==0.0)
					count_equal++;
					
			}		
			
			
		}
		System.out.println("uct "+count_uct+"	"+"baseline "+count_baseline+"	"+"equal "+count_equal);
		*/
		
		int count=0;
		while((s=br.readLine())!=null)
		{
			
			//String s1[]=s.split(",");
			if(Float.parseFloat(s)!=10000 && Float.parseFloat(s)>1000)
			{
				System.out.println(s);
				count++;
				
			}
			
			
			
		}
		System.out.println(count);
		
		
		
		
		
		
		

	}

}
