/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 *
 * @author sairam
 */;



public class BernoulliDistribution implements Distribution<Double>{

	private double p;
	public BernoulliDistribution(double parameter){
		p = parameter;
	}
	
	public Double sampleFromDistribution() {
	  int x = 0;
          if(Math.random() < p) {
			  x++;
	  }
	  return (double) x;
        }

	@Override
	public double getMean() {
		// TODO Auto-generated method stub
		return p;
	}

	@Override
	public double getRiskAverse(double lambda) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}