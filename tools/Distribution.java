/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;
import java.util.*;

/**
 *
 * @author sairam
 */
public interface Distribution<Set>{
    
	public Set sampleFromDistribution();
	
	public double getMean();
	

	public double getRiskAverse(double lambda);
	
    
}
