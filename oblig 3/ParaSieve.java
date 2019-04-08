import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


class ParaSieve {
    private int[] allPrimes;
    private byte[] byteArray;
    private int n;
    
    private int m;
    private int mSqrt;

    private int primeCount;


    private int threads;

    private int cells;

    private CyclicBarrier barrierMain;
    private CyclicBarrier barrierThreads;

    private int[][] primesNested;


    


    public ParaSieve(int n) {
        this.n = n;
        this.m = (int) Math.sqrt(n);
        this.mSqrt = (int) Math.sqrt(m);

        cells = n / 16 + 1;
        byteArray = new byte[cells];
    }

    public int[] findPrimes(int threads) {
        findFirstPrimes();
        para(threads);

        gatherPrimes();

        return primes;
    }

    boolean isPrime(int i) {
        if ((i % 2) == 0) {
            return false;
        }

        int byteCell = i / 16;
        int bit = (i / 2) % 8;

        return (byteArray[byteCell] & (1 << bit)) == 0;
    }


    /**
     * Thread safe method that return next primes in bulk size specified
     */
    // private int[] findNextPrimes(int size) {
    //     int[] primes = new int[size];
    //     if (lastFoundPrime = 0)
    //         return primes;


    //     lock.lock();
        
    //     lastFoundPrime = findNextPrime(lastFoundPrime + 2);
    //     primes[0] = prime;
        
    //     if (lastFoundPrime!=0)
    //         primeCount++;

    //     for (int i = 1; i < size && lastFoundPrime != 0; i++) {
    //         lastFoundPrime = findNextPrime(lastFoundPrime+2);
    //         primes[i] = findNextPrime(lastFoundPrime);

    //         if (lastFoundPrime!=0)
    //             primeCount++;
    //     }

    //     lock.unlock();
    //     return primes;
    // }

    /**
     * Finds all primes from 3 - m
     */

    void findFirstPrimes() {
        primeCount = 1;
        int currentPrime = 3;

        while (currentPrime != 0 && currentPrime <= mSqrt) {
            traverse(currentPrime);
            currentPrime = findNextPrime(currentPrime + 2);
            primeCount++;
        }
        
        lastFoundPrime = currentPrime;
    }

    void traverse(int p) {
        for (int i = p * p; i < mSqrt; i += p * 2) {
            flip(i); 
        }
    }

    void flip(int i) {
        if (i % 2 == 0) {
            return;
        }

        int byteCell = i / 16;
        int bit = (i / 2) % 8;

        byteArray[byteCell] |= (1 << bit);
    }

    int findNextPrime(int startAt) {
        for (int i = startAt; i < n; i += 2) {
            if (isPrime(i)) {
                return i;
            }
        }
        return 0;
    }



    void para(int threads) {
        int startByte = mSqrt / 16;
        int endByte = cells - 1;
        
        int partitionSize = (endByte - startByte) / threads;
        int remainder = (endByte - startByte)  % threads;

        this.threads = threads;

        int indice = 0;

        barrierMain = new CyclicBarrier(threads + 1);
        barrierThreads = new CyclicBarrier(threads);

        // start threads
        // two loops ensure even partitioning
        for (int i = 0; i < remainder; i++, indice += partitionSize + 1) {
            new Thread(new Worker(indice, indice + partitionSize + 1, i)).start();
        }

        for (int i = remainder; i < threads; i++, indice += partitionSize) {
            new Thread(new Worker(indice, indice + partitionSize, i)).start();
        }


        try {
            barrier.await();
        } catch (Exception e) {
            return;
        }
    }
    

    void gatherPrimes() {
        primes = new int[primeCount];

        primes[0] = 2;
        int currentPrime = 3;

        for (int i = 1; i < primeCount; i++) {
            primes[i] = currentPrime;
            // currentPrime = findNextPrime(currentPrime + 2, n);
        }
    }



    class Worker implements Runnable {
        // numbers in sieve to take care of
        final int start, end, id;
        private int foundPrimes = 0;
        int[] primes;

        Worker(int start, int end, int id) {
            this.start = start*16;
            this.end = end*16-1;
            this.id = id;
        }


        void traverse(int p, int start, int end) {
            for (int i = p * start; i < end; i += p * 2) {
                flip(i); 
            }
        }


        private void tick() {
            int p = 3;
            while (p < m) {
                p = findNextPrime(p);
                int start = this.start / p;
                int end = this.end / p;

                traverse(p, start, end);
            }
        }

        private void gather() {
            int count = 0;
            
            // count primes
            for (int i = start; i < end; i++) {
                if(isPrime(i)) 
                    count++;
                
            }

            // gather them
            primes = new int[prime];
            int j = 0;
            for (int i = start; i < end && j < count; i++) {
                if(isPrime(i)) 
                    primes[j++] = i;
                
            }


        }

        private void initialize() {
            primeCount = 0;

            for (int[] list : primesNested) {
                primeCount += list.length;
            }
        }

        private void fillUp() {
            int startIndex, endIndex;
            if (id == 0) {
                startIndex = 0;
                endIndex = primes.length - 1;
            } else {
                startIndex =
            }


            for (int i = startIndex; i <= endIndex; i++) {
                
            }
        }




        @Override
        public void run() {
            // traverse
            tick();
            primesNested[id] = primes;

            try {
                barrierThreads.await();
            } catch(Exception e) {
            }

            if (id == 0)
                initialize();
            
            try {
                barrierThreads.await();
                } catch(Exception e) {
                }
    
            
            fillUp();

            try {
                barrierMain.await();
            } catch(Exception e) {
            }

        }        
    }
}
