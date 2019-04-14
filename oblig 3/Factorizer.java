import java.util.ArrayList;

class Factorizer {

    static long[][] factorize(int[] primes, int n) {
        long base = (long) n*n;

        long[][] factors = new long[100][];

        for (int i = 0; i < 100; i++) {
            factorize_bases(primes, base-i, factors, i);
        }

        return factors;
    }

    private static void factorize_bases(int[] primes, long base, long[][] factors, int round) {
        int index = 0;
        long factor = base;

        ArrayList<Long> factorSet = new ArrayList<Long>();

        while (primes.length > index && Math.pow(primes[index], 2) <= base) {
            if (factor % primes[index] == 0) {
                factor = factor/primes[index];
                factorSet.add((long) primes[index]);
            } else {
                index++;
            }
        }

        // las divided can also be a prime!
        if (factor != 1) {
            factorSet.add(factor);
        }

        long[] factorA = new long[factorSet.size()];
        int j = 0;

        for (long i : factorSet) {
            factorA[j++] = i;
        }

        factors[round] = factorA;
    }
}