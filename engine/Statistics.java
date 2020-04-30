/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package engine;

/**
 *
 * @author sairam
 */
import java.awt.font.NumericShaper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Statistics {

	
	
	
	
// public static void fprintMultiplePlaysData(Engine e,String name, int numberOfRounds, int numberOfPlays){
//		try{
//			RandomAccessFile raf = new RandomAccessFile(name, "rw");
//
//			raf.seek(0);
//			long cpt=0;
//			double[] cumrewards = new double[numberOfRounds];
//			for(int i=0;i<numberOfPlays;i++){
//			 e.newGame();
//			 cumrewards = e.play(numberOfRounds); 
//			 for(int j=0;j<numberOfRounds;j++){
//				 raf.seek(cpt*8);
//				 raf.writeDouble((Double) cumrewards[j]);
//				 cpt=cpt+1;
//			 }
//			}
//			
//			/*
//			for(int i=0;i<numberOfPlays;i++){
//			  for(int j=0;j<numberOfRounds;j++){
//				raf.seek((i*numberOfRounds + j)*8);
//				System.out.print(raf.readDouble()+ " ");
//				 }
//			  System.out.println();
//				}*/
//			
//			raf.close();
//			}catch (Exception ex){//Catch exception if any
//				  System.err.println("Error: " + ex.getMessage());
//			}		
//	}
	
	
	
	public static double computeMean(double[] cumr, int numberOfPlays){				
			double m = 0;
			for (int j=0;j<numberOfPlays;j++) {
				m += cumr[j];
			}
			m /= numberOfPlays;
		return m;	
	}
	public static double computeQuantile(double[] cumr, int numberOfPlays, double q){
		//System.out.println((int) Math.max(Math.round(q*numberOfPlays -1),0));
		double quantile = 0.;
		//R1
		int quantindex =  (int) Math.min(Math.max(Math.ceil(q*numberOfPlays),1)-1,numberOfPlays-1);
		//R2
		//int qim =  (int) Math.min(Math.max(Math.ceil(q*numberOfPlays),1) -1,numberOfPlays-1);
		//int qip =  (int) Math.min(Math.max(Math.floor(q*numberOfPlays+1),1)-1,numberOfPlays-1);		
		// R3
		//int quantindex =  (int) Math.max(Math.round(q*numberOfPlays -1),0);
		// R4
		//int qi = (int) Math.min(Math.max(Math.floor(q*numberOfPlays),1)-1,numberOfPlays-1);
		//double qr = q*numberOfPlays - qi -1;
					
			List<Double> cumi = new ArrayList<Double>();
			for (int j=0;j<numberOfPlays;j++) {
				cumi.add(cumr[j]);
			}
			Collections.sort(cumi);
			//R1
			quantile = cumi.get(quantindex);
			//R2 
			//quantiles[i] = (cumi.get(qim) + cumi.get(qip))/2.;
			//R3
			//quantiles[i] = cumi.get(quantindex);
			//R4
			/*if (qi+1<numberOfPlays){
			quantiles[i] = cumi.get(qi) + qr * (cumi.get(qi+1) - cumi.get(qi));
			}
			else{
			quantiles[i] = cumi.get(numberOfPlays-1);
			}*/
		return quantile;
	}
	
	public static void fprintStatisticsFromMultiplePlaysData(String inputName,String outputName, double maxMean, int numberOfRounds, int numberOfPlays, double[] qs){

		try{
			RandomAccessFile raf = new RandomAccessFile(inputName, "rw");
			
			FileWriter fstream = new FileWriter(outputName);
			BufferedWriter out = new BufferedWriter(fstream);
			
			double r;
			for(int j=0;j<numberOfRounds;j++){
				
		     double[] regretvalues = new double[numberOfPlays];
		     for (int i=0;i<numberOfPlays;i++){
		       raf.seek((i*numberOfRounds + j)*8);
		       r=raf.readDouble();		       
		       regretvalues[i] = maxMean*j - r;
		     }
		     
		     double mean = computeMean(regretvalues,numberOfPlays);
		     List<Double> quantileValues = new ArrayList<Double>();
				for (int q =0;q<qs.length;q++) {
					quantileValues.add(computeQuantile(regretvalues, numberOfPlays,qs[q]));
				}
				
			 out.write(j+ " " + mean+ " ");
			 for(int q =0;q<qs.length-1;q++) {
				out.write(quantileValues.get(q) + " ");
			 }
			 out.write(quantileValues.get(qs.length-1) + "\n");
				
		    }

		   	
		    out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}		
	}
	
	
	public double meanReward(List<Double> rewards){
		
		double m = 0;
		for(int i=0;i<rewards.size();i++){
			m += rewards.get(i);
		}
		m /= rewards.size();		
		return 		m;
	}
	
	public static void printData(double[][] cumr, int numberRounds, int numberOfPlays){
		for(int i=0;i<numberOfPlays;i++){
			System.out.print("[");
			for (int j=0;j<numberRounds-1;j++) {
				System.out.print(cumr[i][j]+", ");
			}
			System.out.print(cumr[i][numberRounds-1] + "]\n");
		}
	}
	
	public static void fprintData(String name, double[][] cumr, int numberRounds, int numberOfPlays){
		try{
		FileWriter fstream = new FileWriter(name);
		BufferedWriter out = new BufferedWriter(fstream);
		for(int i=0;i<numberOfPlays;i++){
			out.write("");
			for (int j=0;j<numberRounds-1;j++) {
				out.write(cumr[i][j]+" ");
			}
			out.write(cumr[i][numberRounds-1] + "\n");
		}
		out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	public static double[] computeMeans(double[][] cumr, int numberRounds, int numberOfPlays){
		double[] means = new double[numberRounds];
		for(int i=0;i<numberRounds;i++){
		
			double m = 0;
			for (int j=0;j<numberOfPlays;j++) {
				m += cumr[j][i];
			}
			m /= numberOfPlays;
			means[i] = m;
		}
		return means;	
	}
	
	public static void printMeans(double[][] cumr, int numberRounds, int numberOfPlays){
		double[] values = computeMeans(cumr, numberRounds, numberOfPlays);
		System.out.print("[");
		for(int i=0;i<numberRounds;i++){
			System.out.print(values[i]+", ");
		}
		System.out.print(values[numberRounds-1] + "]\n");
	}
	
	public static void fprintMeans(String name, double[][] cumr, int numberRounds, int numberOfPlays , double[][] accumilatedMeans, int pairIndex ){

		double[] values = computeMeans(cumr, numberRounds, numberOfPlays);
		try{
			FileWriter fstream = new FileWriter(name);
			BufferedWriter out = new BufferedWriter(fstream);
		out.write("");
		for(int i=0;i<numberRounds;i++){
			out.write(values[i]+"\n");
			accumilatedMeans[pairIndex][i] = values[i];
		}
		//out.write(values[numberRounds-1] + "\n");
		out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	
	
	public static double[] computeQuantiles(double[][] cumr, int numberRounds, int numberOfPlays, double q){
		//System.out.println((int) Math.max(Math.round(q*numberOfPlays -1),0));
		double[] quantiles = new double[numberRounds];
		//R1
		int quantindex =  (int) Math.min(Math.max(Math.ceil(q*numberOfPlays),1)-1,numberOfPlays-1);
		//R2
		//int qim =  (int) Math.min(Math.max(Math.ceil(q*numberOfPlays),1) -1,numberOfPlays-1);
		//int qip =  (int) Math.min(Math.max(Math.floor(q*numberOfPlays+1),1)-1,numberOfPlays-1);		
		// R3
		//int quantindex =  (int) Math.max(Math.round(q*numberOfPlays -1),0);
		// R4
		//int qi = (int) Math.min(Math.max(Math.floor(q*numberOfPlays),1)-1,numberOfPlays-1);
		//double qr = q*numberOfPlays - qi -1;
				
		for(int i=0;i<numberRounds;i++){
		
			List<Double> cumi = new ArrayList<Double>();
			for (int j=0;j<numberOfPlays;j++) {
				cumi.add(cumr[j][i]);
			}
			Collections.sort(cumi);
			//R1
			quantiles[i] = cumi.get(quantindex);
			//R2 
			//quantiles[i] = (cumi.get(qim) + cumi.get(qip))/2.;
			//R3
			//quantiles[i] = cumi.get(quantindex);
			//R4
			/*if (qi+1<numberOfPlays){
			quantiles[i] = cumi.get(qi) + qr * (cumi.get(qi+1) - cumi.get(qi));
			}
			else{
			quantiles[i] = cumi.get(numberOfPlays-1);
			}*/
				
			
			
		}
		 return quantiles;
	}
	
	public static void printQuantiles(double[][] cumr, int numberRounds, int numberOfPlays,double q){
		double[] values = computeQuantiles(cumr, numberRounds, numberOfPlays,q);
		System.out.print("[");
		for(int i=0;i<numberRounds;i++){
			System.out.print(values[i]+", ");
		}
		System.out.print(values[numberRounds-1] + "]\n");
	}
	
	public static void fprintQuantiles(String name, double[][] cumr, double maxMean, int numberRounds, int numberOfPlays,double q){
		double[] values = computeQuantiles(cumr, numberRounds, numberOfPlays,q);
		try{
			FileWriter fstream = new FileWriter(name);
			BufferedWriter out = new BufferedWriter(fstream);
		out.write("");
		for(int i=0;i<numberRounds;i++){
			out.write(values[i]+"\n");
		}
		out.write(values[numberRounds-1] + "\n");
		out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
	
	
	
	public static void fprintCumRegMeans(String name, Double maxMean, double[][] cumr, int numberRounds, int numberOfPlays){


		double[][] cumregret = new double[numberOfPlays][numberRounds];

		//if ( playername == "ucb")
		//{	
		for(int i=0;i<numberRounds;i++){

			for (int j=0;j<numberOfPlays;j++) {
				 //for the reward
				  cumregret[j][i] = maxMean*i - cumr[j][i];
				// for the loss
				//cumregret[j][i] = cumr[j][i]  - maxMean*i ;
			}
		}
		//		}
		//		else 
		//		{
		//			for(int i=0;i<numberRounds;i++){
		//				for (int j=0;j<numberOfPlays;j++) {
		//					// for the reward
		//					cumregret[j][i] = maxMean*i - cumr[j][i];
		//					// for the loss
		//					//cumregret[j][i] = cumr[j][i]  - maxMean*i ;
		//				}
		//			}
		//		}
		double[] values = computeMeans(cumr, numberRounds, numberOfPlays);
		try{
			FileWriter fstream = new FileWriter(name);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("");
			for(int i=0;i<numberRounds;i++){
				out.write(values[i]+"\n");
			}
			out.write(values[numberRounds-1] + "\n");
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	
	public static void fprintStatistics(String name, double[][] cumr,double maxMean, int numberRounds, int numberOfPlays, double[] qs){

		double[][] cumregret = new double[numberOfPlays][numberRounds];
		
		//if ( playername == "ucb")
		//{	
			for(int i=0;i<numberRounds;i++){
		
						for (int j=0;j<numberOfPlays;j++) {
							// for the reward
							cumregret[j][i] = maxMean*i - cumr[j][i];
							// for the loss
							//cumregret[j][i] = cumr[j][i]  - maxMean*i ;
						}
		        }
//		}
//		else 
//		{
//			for(int i=0;i<numberRounds;i++){
//				for (int j=0;j<numberOfPlays;j++) {
//					// for the reward
//					cumregret[j][i] = maxMean*i - cumr[j][i];
//					// for the loss
//					//cumregret[j][i] = cumr[j][i]  - maxMean*i ;
//				}
//			}
//		}
		double[] means = computeMeans(cumregret, numberRounds, numberOfPlays);
		List<double []> quantileValues = new ArrayList<double[]>();
		for (int i =0;i<qs.length;i++) {
			quantileValues.add(computeQuantiles(cumregret, numberRounds, numberOfPlays,qs[i]));
		}
		try{
			FileWriter fstream = new FileWriter(name);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("");
			for(int i=0;i<numberRounds;i++){
				out.write(i+ " " + means[i]+ " ");
				for(int j =0;j<qs.length-1;j++) {
				out.write(quantileValues.get(j)[i] + " ");
				}
				out.write(quantileValues.get(qs.length-1)[i] + "\n");
			}
			out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error: " + e.getMessage());
		}
	}
}