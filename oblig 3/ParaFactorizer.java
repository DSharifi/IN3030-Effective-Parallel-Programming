import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

class ParaFactorizer {
    final long base;
    final int[] primes;
    final int threads;
    final Oblig3Precode output;

    final CyclicBarrier barrier;

    ParaFactorizer(int[] primes, int n, int threads) {
        long a = (long) n;
        base = a * a;
        this.primes = primes;
        this.threads = threads;
        output = new Oblig3Precode(n);
        barrier = new CyclicBarrier(threads + 1);
    }

    Oblig3Precode factorize() {
        for (int i = 0; i < 2; i++) {
            factorize_bases(primes, base - i, threads);
        }

        return output;
    }

    private void factorize_partly(int start, int end, ArrayList<Integer> factors, long base) {
        long factor = base;
        
        // Find all factors factors[start]...factors[end]

        // TODO: check if primes[start]**2 <= base; might be faster!!!
        while (end > start && primes[start] <= (int) (Math.sqrt(base)) + 1) {
            if (factor % primes[start] == 0) {
                // prime number found
                factors.add(primes[start]);
                factor = factor / primes[start];
            } else {
                // check next prime number
                start++;
            }
        }
    }

    public void factorize_bases(int[] primes, long base, int threads) {
        ArrayList<ArrayList<Integer>> factors = new ArrayList<ArrayList<Integer>>();

        int partitionSize = primes.length / threads;
        int remainder = primes.length % threads;

        int indice = 0;
        // start threads
        for (int i = 0; i < remainder; i++, indice += partitionSize + 1) {
            ArrayList<Integer> list = new ArrayList<Integer>();
            factors.add(list);
            new Thread(new Worker(indice, indice + partitionSize + 1, list, base)).start();
        }

        for (int i = remainder; i < threads; i++, indice += partitionSize) {
            ArrayList<Integer> list = new ArrayList<Integer>();
            factors.add(list);
            new Thread(new Worker(indice, indice + partitionSize, list, base)).start();
        }

        try {
            barrier.await();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(-1);
        }

        // add factors
        long product = 1;
        int i = 0;
        for (ArrayList<Integer> factorList : factors) {
            for (Integer factor : factorList) {
               // System.out.println("Base:\t" + base + "\nFactor:\t" + factor);
                output.addFactor(base, factor);
                product *= factor;
            }
        }

        // last divided can also be a prime!
        if (product != 1 && product != base) {
            output.addFactor(base, product);
        }

    }

    class Worker implements Runnable {
        int start, end;
        long base;
        ArrayList<Integer> factors;

        Worker(int start, int end, ArrayList<Integer> list, long base) {
            this.start = start;
            this.end = end;
            this.base = base;

            factors = list;

        }

        @Override
        public void run() {
            factorize_partly(start, end, factors, base);
            try {
                barrier.await();
            } catch (Exception e) {
            }
        }
    }

}