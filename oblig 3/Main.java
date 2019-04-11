import java.util.ArrayList;
import java.util.Arrays;

class Main {
    public static void main(String[] args) {
        int mode = Integer.parseInt(args[0]);
        int n = Integer.parseInt(args[1]);

        Oblig3Precode precode = null;
        
        if (mode == 0) {


            int threads = Integer.parseInt(args[2]);
            // har ikke fatt til parallel sil 
            ParaSieve sieve = new ParaSieve(n);

            int[] primes = sieve.findPrimes(threads);

            ParaFactorizer f = new ParaFactorizer(primes, n, threads);

            precode = f.factorize();










        } else if (mode == 1) {
            SequentialSieve sieve = new SequentialSieve(n);
            int[] primes = sieve.findPrimes();

            precode = Factorizer.factorize(primes, n);
            
        } else {
            System.out.println("Use mode 0 or 1\n(0): Parallel\n(1): Sequential");
            System.exit(1);
        }

        precode.writeFactors();

    }



    // used to assert factorization was correct
    private static void testResult(int n, ArrayList<Integer> factors) {
        long i = 1;
        for (Integer j : factors) {
            i *= j;
        }
        System.out.println(i == n);
    }

    private static boolean assert_sieve(int[] seq, int[] para) {
        if (seq.length != para.length) {
            return false;
        }

        for (int i = 0; i < seq.length; i++) {
            if (seq[i] != para[i]) {
                System.out.println("Para:\t" + para[i]);
                System.out.println("Seq:\t" + seq[i]);
                return false;
            }
        }

        return true;

    }
}