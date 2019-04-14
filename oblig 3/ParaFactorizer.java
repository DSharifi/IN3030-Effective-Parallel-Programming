import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

class ParaFactorizer {
    final long base;
    final int[] primes;
    final int threads;


    // n --->  (threads ---> factors)
    final ArrayList<ArrayList<ArrayList<Integer>>> nestedFactors;

    // n ---> factors    
    final long[][] finalFactors;

    final CyclicBarrier barrierThreads;
    final CyclicBarrier barrierMain;
    


    ParaFactorizer (int[] primes, int n, int threads) {
        base = (long) n * n;
        this.primes = primes;
        this.threads = threads;

        this.nestedFactors = new ArrayList<ArrayList<ArrayList<Integer>>>(100);
        this.finalFactors = new long[100][];

        for (int i = 0; i < 100; i++) {
            ArrayList<ArrayList<Integer>> threadFactors = new ArrayList<ArrayList<Integer>>(threads);

            for (int j = 0; j < threads; j++) {
                threadFactors.add(new ArrayList<Integer>());
            }

            nestedFactors.add(threadFactors);
        }

        barrierMain = new CyclicBarrier(threads + 1);
        barrierThreads = new CyclicBarrier(threads);
    }

    long[][] factorize() {
        for (int id = 0; id < threads; id++) {
            new Thread(new Worker(id)).start();
        }

        try {
            barrierMain.await();
        } catch(Exception e) {}


        return finalFactors;
    }


    class Worker implements Runnable {
        final int id;

        Worker(int id) {
            this.id = id;
        }


        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                factorize_partly(base - i);
            }

            try {
                barrierThreads.await();
            } catch (Exception e) {
            }
            // do cleanup
            cleanup();
            try {
                barrierMain.await();
            } catch (Exception e) {
            }
        }


        void factorize_partly(long n) {
            int index = id;
            int nIndex = (int) (base - n);

            // factors sotred locally
            ArrayList<Integer> factors = nestedFactors.get(nIndex).get(id);
            
            while (index < primes.length && Math.pow(primes[index], 2) <= n) {
                if (n % primes[index] == 0) {;
                    factors.add(primes[index]);
                    n /= primes[index];

                } else {
                    index += threads;
                }
            }

        }

        // n ---> threads ---> factors
        void cleanup() {
            for (int index = id; index < nestedFactors.size(); index+=threads) {
                
                long[] factorSet;                
                int size = 0;
                long product = 1;
                long n = base - index;


                ArrayList<ArrayList<Integer>> factors = nestedFactors.get(index);
                
                for (ArrayList<Integer> factorList : factors) {
                    size += factorList.size();
                    for (int factor : factorList) {
                        product *= factor;
                    }
                }
                
                if (product != n) {
                    factorSet = new long[size + 1];
                    factorSet[factorSet.length - 1] =  (n/product);

                } else {
                    factorSet = new long[size];
                }

                int i = 0;
                for (ArrayList<Integer> factorList : factors) {
                    for (int factor : factorList) {
                        factorSet[i++] = factor;
                    }
                } 

                finalFactors[index] = factorSet;

                Arrays.sort(factorSet);

                // System.out.println("ID\t" + id + "\nn\t" + n + "\nindex\t" + index + "\nproduct\t" + j + "\n\n");

            }

        }

    }

}