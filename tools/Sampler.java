/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jdk.internal.dynalink.beans.StaticClass;



/**
 *
 * @author sairam
 */

    
public class Sampler {
  private static Random rng = new Random(
      Calendar.getInstance().getTimeInMillis() +
      Thread.currentThread().getId());

  public static Random getRandom(){
	  return rng;
  }
  
  public static boolean sampleCoin(){
	  return rng.nextBoolean();
  }
  
    
  public static double sampleBeta(double alpha, double beta) {
   double x,y;
    x = sampleGamma(alpha, 1);
    y = sampleGamma(beta, 1);
    return x/(x+y);
  }
  
  // Sample from the 1 - X where X ~ beta( alpha , beta)
  public static double one_minus_sampleBeta(double alpha, double beta) {
	   double x,y;
	    x = sampleGamma(alpha, 1);
	    y = sampleGamma(beta, 1);
	    return y/(x+y);
  }
	  
  
//   public static long nextPoisson(double lambda) {
//	double elambda = Math.exp(-1*lambda);
//	double product = 1;
//	int count =  0;
//	 int result=0;
//	while (product >= elambda) {
//		product *= rng.nextDouble();
//		result = count;
//		count++; // keep result one behind
//		}
//	return result;
//  }
  public static  long nextPoisson(double lambda)
  {
      return  (long)(-1.0 * Math.log(1.0 - rng.nextDouble()*1.0) / lambda);
  }
    
  
   

  public static double nextExponential(double b) {
	double randx;
	double result;
	randx = rng.nextDouble();
	result = -1*b*Math.log(randx);
	return result;
  }	
  
  public static double sampleGamma(double k, double theta) {
    boolean accept = false;
    if (k < 1) {
 // Weibull algorithm
 double c = (1 / k);
 double d = ((1 - k) * Math.pow(k, (k / (1 - k))));
 double u, v, z, e, x;
 do {
  u = rng.nextDouble();
  v = rng.nextDouble();
  z = -Math.log(u);
  e = -Math.log(v);
  x = Math.pow(z, c);
  if ((z + e) >= (d + x)) {
   accept = true;
  }
 } while (!accept);
 return (x * theta);
    } else {
 // Cheng's algorithm
 double b = (k - Math.log(4));
 double c = (k + Math.sqrt(2 * k - 1));
 double lam = Math.sqrt(2 * k - 1);
 double cheng = (1 + Math.log(4.5));
 double u, v, x, y, z, r;
 do {
  u = rng.nextDouble();
  v = rng.nextDouble();
  y = ((1 / lam) * Math.log(v / (1 - v)));
  x = (k * Math.exp(y));
  z = (u * v * v);
  r = (b + (c * y) - x);
  if ((r >= ((4.5 * z) - cheng)) ||
                    (r >= Math.log(z))) {
   accept = true;
  }
 } while (!accept);
 return (x * theta);
    }
  }
  
  

  public static Double[] subSampleNaive(List<Double> samples, int m) {
	   int n = samples.size();
	   int nm = Math.min(m, n);	   
	   Double[] sub = new Double[nm];
	   if (nm==n){
		   sub = samples.toArray(sub);
		   return sub;
	   }
	   for (int k=0;k<nm;k++){
		   int i = rng.nextInt(n);
		   Double aux = samples.get(k);
		   samples.set(k, samples.get(i));
		   samples.set(i, aux);
	   }
	   sub = samples.subList(0, nm).toArray(sub);
	   return sub;
  }
    
  public static Double[] subSample(List<Double> samples, int m) {
	  //System.out.println(samples.size() +" " + m);
	   
	   
	   int n = samples.size();
	   int nm = Math.min(m, n);	   
	   Double[] sub = new Double[nm];
	   if (nm==n){
		   sub = samples.toArray(sub);
		   return sub;
	   }
	   if (m<n/2){
		   for (int k=0;k<nm;k++){
			   int i = rng.nextInt(n);
			   Double aux = samples.get(0);
			   samples.set(0, samples.get(i));
			   samples.set(i, aux);
		   }
		   sub = samples.subList(0, nm).toArray(sub);
	   } else {
		   for (int k=0;k<n-nm;k++){
			   int i = rng.nextInt(n);
			   Double aux = samples.get(0);
			   samples.set(0, samples.get(i));
			   samples.set(i, aux);
		   }
		   sub = samples.subList(n-nm, n).toArray(sub);
	   }
	   /****
	   if (nm==n){
		   sub = samples.toArray(sub);
	   } else {
	     if (m<samples.size()/2){
	    	 List<Integer> indexes = new ArrayList<Integer>();
		     for (int k=0;k<nm;k++){
		        	int i = rng.nextInt(samples.size());
		        	while (indexes.contains(i)){
		        		i = rng.nextInt(samples.size());
		        	}
		        	sub[k] = samples.get(i);   	
		        }
		     
	    	 List<Double> lsamples = new ArrayList<Double>(samples);
	    	 for (int k=0;k<nm;k++){
		        	int i = rng.nextInt(lsamples.size());
		        	sub[k]= lsamples.get(i);
		        	lsamples.remove(i);	        	
		        }
	     } else {
	    	 
	    	 List<Integer> indexes = new ArrayList<Integer>();
		     for (int k=0;k<n-nm;k++){
		        	int i = rng.nextInt(samples.size());
		        	while (indexes.contains(i)){
		        		i = rng.nextInt(samples.size());
		        	} 	
		        }
	        	  
		     
	    	 List<Double> lsamples = new ArrayList<Double>(samples);
	    	 for (int k=0;k<n-nm;k++){
		        	int i = rng.nextInt(lsamples.size());
		        	lsamples.remove(i);	        	
		        }
	    	 sub = lsamples.toArray(sub);
	     }	         
	     
	     
	   }
	   ****/
	    	return sub;
	    
	  }

    
    
}
