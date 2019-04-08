import java.util.ArrayList;
import java.util.Arrays;

class Factorizer {
    static Oblig3Precode factorize(int[] primes, int n) {
        long base = n*n;
        Oblig3Precode output = new Oblig3Precode(n);

        for (long i = 0; i < 100; i++) {
            factorize_bases(primes, base-i, output);
        }

        return output;
    }

    public static void factorize_bases(int[] primes, long base, Oblig3Precode output) {
        int index = 0;
        long factor = base;

        while (primes.length > index && primes[index] <= (int) (Math.sqrt(base)) + 1) {
            if (factor % primes[index] == 0) {
                output.addFactor(base, primes[index]);
                factor = factor/primes[index];
            } else {
                index++;
            }
        }

        // las divided can also be a prime!
        if (factor != 1) {
            output.addFactor(base, factor);
        }
    }
}