import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

class ParaFactorizer {
    final long base;
    final int[] primes;
    final int threads;
    final Oblig3Precode output;

    final CyclicBarrier barrier;

    ParaFactorizer (int[] primes, int n, int threads) {
        base = (long) n * n;
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

    public void factorize_bases(int[] primes, long base, int threads) {
        ArrayList<ArrayList<Integer>> factors = new ArrayList<ArrayList<Integer>>();

        for (int id = 0; id < threads; id++) {
            factors.add(new ArrayList<Integer>());
            new Thread(new Worker(id, factors.get(id), base)).start();
        }

        try {
            barrier.await();
        } catch (Exception e) {
            System.out.println(e);
        }

        // add factors
        long product = 1;
        for (ArrayList<Integer> factorList : factors) {
            for (Integer factor : factorList) {
                // System.out.println("Base:\t" + base + "\nFactor:\t" + factor);
                output.addFactor(base, factor);
                product *= factor;
            }
        }

        // last divided can also be a prime!
        if (product != 1 && product != base) {
            // System.out.println("Base:\t" + base);
            // System.out.println("product:\t" + product);
            
            output.addFactor(base, product);
        }
    }




    class Worker implements Runnable {
        int id;
        long base;
        ArrayList<Integer> factors;

        Worker(int id, ArrayList<Integer> list, long base) {
            factors = list;
            this.base = base;
        }

        void factorize_partly() {
            int index = id;

            // TODO: De faktoriserer kun til nest siste faktor. Så legges produktet til som faktor?
            //          - SE PAA java Main 0 100 1
            // TODO: 

            while (index < primes.length && Math.pow(primes[index], 2) <= base) {
                if (base % primes[index] == 0) {
                    System.out.println("factor:\t" + primes[index]);
                    System.out.println("base:\t" + base);
                    factors.add(primes[index]);
                    base /= primes[index];

                } else {
                    index += threads;
                }

            }
            System.out.println("last base:\t" + base);
            System.out.println("\n\n\n\n");
        }

        @Override
        public void run() {
            factorize_partly();
            try {
                barrier.await();
            } catch (Exception e) {
            }
        }
    }

}