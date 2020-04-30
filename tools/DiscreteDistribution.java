/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 *
 * @author sairam
 */
public class DiscreteDistribution<Set> implements Distribution<Set>{

	private Set[] xs;
	private double[] ps;
	public DiscreteDistribution(Set[] points, double[] parameters){
		ps = parameters;
		xs = points;
		
		double s = 0;
		for(int i=0;i<ps.length;i++){
			s+= ps[i];
		}
		for(int i=0;i<ps.length;i++){
			ps[i] = ps[i]/s;
		}
	}
	
	public Set sampleFromDistribution() {
	  double u = Math.random();
	  int i=0;
	  //System.out.println("Random:" + u);
	  //System.out.println("Probas:" + ps);
	  double cumsum = ps[i];
      while(i < ps.length){
    	  if (u < cumsum){
    		  return xs[i];
    	  }
    	  i++;
    	  if (i<ps.length) {    	  cumsum += ps[i]; }
    	  else {return xs[ps.length-1];}
      }
	  return xs[ps.length-1];

	}
	
	private double todouble(Set s){
		if (s instanceof Double){
		return ((Double) s).doubleValue();
		} else if(s instanceof Integer){
			return ((Integer) s).doubleValue();
			} 
		return 0.;
	}

	public double getMean() {
		double m = 0;
		for(int i=0;i<ps.length;i++){
			m+= ps[i]* todouble(xs[i]);
		}		
		return m;
	}

	@Override
	public double getRiskAverse(double lambda) {
		// TODO Auto-generated method stub
		return 0;
	}
        
        
}
