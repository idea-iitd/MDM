package recomendation;
import java.util.Random;

public class CrossValidation {
	
	/**
     * The number of rounds of cross validation.
     */
    public final int k;
    /**
     * The index of training instances.
     */
    public final int[][] train;
    /**
     * The index of testing instances.
     */
    public final int[][] test;

    /**
     * Constructor.
     * @param n the number of samples.
     * @param k the number of rounds of cross validation.
     */
    public CrossValidation(int n, int k) {
        if (n < 0) {
            throw new IllegalArgumentException("Invalid sample size: " + n);
        }

        if (k < 0 || k > n) {
            throw new IllegalArgumentException("Invalid number of CV rounds: " + k);
        }

        this.k = k;
        
      
        int[] index = new int[n];

        // insert integers 0..n-1
        for (int i = 0; i < n; i++)
            index[i] = i;

        // shuffle  ,to create permutation of array
        for (int i = 0; i < n; i++) {
            int r = (int) (Math.random() * (i+1));     // int between 0 and i
            int swap = index[r];
            index[r] = index[i];
            index[i] = swap;
        }

        

        train = new int[k][];
        test = new int[k][];

        int chunk = n / k;
        for (int i = 0; i < k; i++) {
            int start = chunk * i;
            int end = chunk * (i + 1);
            if (i == k-1) end = n;

            train[i] = new int[n - end + start];
            test[i] = new int[end - start];
            for (int j = 0, p = 0, q = 0; j < n; j++) {
                if (j >= start && j < end) {
                    test[i][p++] = index[j];
                } else {
                    train[i][q++] = index[j];
                }
            }
        }
    }
}	
	
	


