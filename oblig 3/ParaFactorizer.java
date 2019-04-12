import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CyclicBarrier;

class ParaFactorizer {
    final long base;
    final int[] primes;
    final int threads;


    // n --->  (threads ---> factors)
    final HashMap<Long, ArrayList<ArrayList<Integer>>> nestedFactors;

    // n ---> factors    
    final HashMap<Long, ArrayList<Integer>> finalFactors;

    final CyclicBarrier barrier;


    ParaFactorizer (int[] primes, int n, int threads) {
        base = (long) n * n;
        this.primes = primes;
        this.threads = threads;

        this.nestedFactors = new HashMap<Long, ArrayList<ArrayList<Integer>>>(100);
        this.finalFactors = new HashMap<Long, ArrayList<Integer>>(100);

        for (int i = 0; i < 100; i++) {
            ArrayList<ArrayList<Integer>> threadFactors = new ArrayList<ArrayList<Integer>>(threads);

            for (int j = 0; j < threads; j++) {
                threadFactors.add(new ArrayList<Integer>());
            }

            nestedFactors.put(base - i, threadFactors);
            finalFactors.put(base - i, new ArrayList<Integer>());
        }

        barrier = new CyclicBarrier(threads + 1);
    }

    HashMap<Long, ArrayList<Integer>> factorize() {
        for (int id = 0; id < threads; id++) {
            new Thread(new Worker(id)).start();
        }

        try {
            barrier.await();
        } catch(Exception e) {}

        return finalFactors;
    }


    class Worker implements Runnable {
        int id;

        Worker(int id) {
            this.id = id;
        }


        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                factorize_partly(base - i);
            }

            try {
                barrier.await();
            } catch (Exception e) {
            }
            // do cleanup
            cleanup();
        }


        // n ---> threads ---> factors
        void factorize_partly(long currentBase) {
            int index = id;

            // factors sotred locally
            ArrayList<Integer> localFactorsForN = nestedFactors.get(currentBase).get(id);
            
            while (index < primes.length && Math.pow(primes[index], 2) <= currentBase) {
                if (currentBase % primes[index] == 0) {;
                    localFactorsForN.add(primes[index]);
                    currentBase /= primes[index];

                } else {
                    index += threads;
                }
            }

        }




        void cleanup() {
            long key = base - id;
            long limit = base - 100;

            while (limit < key) {
                ArrayList<ArrayList<Integer>> threadFactors = nestedFactors.get(key);
                ArrayList<Integer> outputFactors = finalFactors.get(key);

                // add factors
                long product = 1;

                for (ArrayList<Integer> factorList : threadFactors) {
                    for (Integer factor : factorList) {
                        // System.out.println("Base:\t" + base + "\nFactor:\t" + factor);
                        outputFactors.add(factor);
                        product *= factor;
                    }
                }

                // last divided can also be a prime!
                if (product != key) {
                    // System.out.println("Base:\t" + base);
                    // System.out.println("product:\t" + product);
                    outputFactors.add((int) product);
                }

                key -= threads;
            }

        }

    }

}