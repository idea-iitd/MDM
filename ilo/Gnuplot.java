/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ilo;

/**
 *
 * @author sairam
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public final class Gnuplot  {


	
	public static void makeGnuplotFile(String s, int T,int N, double[] qs){
		System.out.println("IN make GNU 0");
		try{
			FileWriter fstream = new FileWriter(s+".gnu");
			BufferedWriter out = new BufferedWriter(fstream);
			System.out.println("IN make GNU 1");
			out.write("plot = \"" + s + "_Statistics.dat\"\n");
			out.write("title = \"" + s + "\"\n");
			out.write("set size 1, 1\n");
			out.write("set term png size 600, 400\n");
			out.write("set output \"" + s +".png\"\n");
			System.out.println("Make GNU 2");
			//out.write("plot plot using 1:"+3 + " lt rgb \"#AAAAAA\" with filledcurves x1, ");
			out.write("plot plot using 1:"+3 + " lt rgb \"#AAAAAA\" with lines notitle, ");
			System.out.println("IN make GNU 3");
			for(int i=0;i<qs.length-1;i++){
			//out.write("plot using 1:"+(i+3)+":"+(i+4)+ " lt rgb \"#AAAAAA\" with filledcurves closed, ");
			out.write("plot using 1:"+(i+3)+ " lt rgb \"#AAAAAA\" with lines notitle, ");
			}
			//out.write("plot using 1:"+(qs.length+2)+ " lt rgb \"#AAAAAA\" with filledcurves x2, ");
			out.write("plot using 1:"+(qs.length+2)+ " lt rgb \"#AAAAAA\" with lines notitle, ");
			out.write("plot using 1:2 lt rgb \"#FF0000\" with lines notitle\n");
			out.write("pause -1");
			out.close();
		}catch (Exception e){//Catch exception if any
			  System.err.println("Error 1: " + e.getMessage());
		}
	}
	
	
	public static void executeGnuplot(String s){
		Runtime runtime = Runtime.getRuntime();
		String[] args = new String[2];
		args[0] = "gnuplot";
		args[1] = s + ".gnu";
		try {
		runtime.exec(args);
		} catch(IOException ioe) {
			System.err.println("Error 2: " + ioe.getMessage());
			//ioe.printStackTrace();
		}
	}
	
}