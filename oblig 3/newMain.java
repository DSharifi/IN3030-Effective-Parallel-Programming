import java.util.Arrays;
import java.util.ArrayList;

class newMain {
    public static void main(String[] args) {
        // int threads = Integer.parseInt(args[0]);
        // int primeMax = Integer.parseInt(args[1]);

        // ParaSieve sieve = new ParaSieve(primeMax);
        // int[] primes = sieve.findPrimes(threads);
        // System.out.println(Arrays.toString(primes));
        // System.out.println(Arrays.toString(primes));


        int mode = Integer.parseInt(args[0]);
        int n = Integer.parseInt(args[1]);

        Oblig3Precode precode = null;

        
        if (mode == 0) {
            int threads = Integer.parseInt(args[2]); 
            // har ikke fatt til parallel sil 
            ParaSieve sieve = new ParaSieve(n);
            int[] primes = sieve.findPrimes(threads);

            SequentialSieve siv = new SequentialSieve(n);
            int[] prim = siv.findPrimes();

            System.out.println(assert_sieve(prim, primes));

            // System.out.println(Arrays.toString(primes));

        } else if (mode == 1) {
            SequentialSieve sieve = new SequentialSieve(n);
            int[] primes = sieve.findPrimes();
            precode = Factorizer.factorize(primes, n);
            
        } else {
            System.out.println("Use mode 0 or 1\n(0): Parallel\n(1): Sequential");
            System.exit(1);
        }

    }

    private static boolean assert_sieve(int[] seq, int[] para) {
        System.out.println(Arrays.toString(seq));
        System.out.println(Arrays.toString(para));
        System.out.println(seq.length == para.length);
        if (seq.length != para.length)
            return false;
        
        for (int i = 0; i < seq.length; i++) {
            if (seq[i] != para[i])
                return false;
        }


        return true;
        
    }



    // used to assert factorization was correct
    private static void testResult(int n, ArrayList<Integer> factors) {
        long i = 1;
        for (Integer j : factors) {
            i *= j;
        }
        System.out.println(i == n);
    }
}