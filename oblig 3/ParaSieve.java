import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;


class ParaSieve {
    private int[] primes;
    private byte[] byteArray;
    private int n;
    
    private int m;
    private int mSqrt;

    private int primeCount;

    private int lastFoundPrime;
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


    void printPrimes() {
        for (int i = 0; i < m; i++) {
            if (isPrime(i))
                System.out.println(i);
        }
    }

    public int[] findPrimes(int threads) {      
        findFirstPrimes();

        printPrimes();


        para(threads);


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
     * Finds all primes from 3 - m
     */

    void findFirstPrimes() {
        primeCount = 1;
        int currentPrime = 3;

        while (currentPrime != 0 && currentPrime <= mSqrt) {
            traverse(currentPrime);
            currentPrime = findNextPrime(currentPrime + 2);
            
        }

        lastFoundPrime = currentPrime;
        
    }

    void traverse(int p) {
        for (int i = p * p; i < m; i += p * 2) {
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
        for (int i = startAt; i < m; i += 2) {
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

        this.primesNested = new int[threads][];

        // start threads
        // two loops ensure even partitioning
        for (int i = 0; i < remainder; i++, indice += partitionSize + 1) {
            new Thread(new Worker(indice, indice + partitionSize + 1, i)).start();
        }

        for (int i = remainder; i < threads; i++, indice += partitionSize) {
            new Thread(new Worker(indice, indice + partitionSize, i)).start();
        }


        try {
            barrierMain.await();
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
        int[] localPrimes;

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
            int p = findNextPrime(lastFoundPrime + 2);

            while (p <= m && p!= 0) {
                if (id == 0) {
                    System.out.println("mellom\t" + p);
                }
                int start = this.start / p;
                int end = this.end / p;
                traverse(p, start, end);

                p = findNextPrime(p + 2);

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
            localPrimes = new int[count];

            int j = 0;
            for (int i = start; i < end && j < count; i++) {
                if(isPrime(i)) 
                    localPrimes[j++] = i;
            }

            primesNested[id] = localPrimes;


        }

        private void initialize() {
            primeCount = 0;
            for (int[] list : primesNested) {
                primeCount += list.length;
            }

            primes = new int[primeCount];
        }

        private void fillUp() {
            int startIndex;
            if (id == 0) {
                startIndex = 0;
            } else {
                startIndex = primesNested[id-1].length;
            }

            int k = 0, l = startIndex;
            try {
                for (int i = 0, j = startIndex; i < localPrimes.length; i++, j++) {
                    System.out.println(primes);
                    System.out.println(localPrimes);

                    primes[j] = localPrimes[i];
                    i++;j++;
                    System.out.println("coolio");
                }
            } catch(NullPointerException e) {
                System.out.println(k + "\t"+ l);
            }

        }


        int findNextPrime(int startAt) {
            for (int i = startAt; i < m; i += 2) {
                if (isPrime(i)) {
                    return i;
                }
            }
            return 0;
        }




        @Override
        public void run() {
            // traverse
            tick();
            System.out.println("gather");
            gather();

            try {
                barrierThreads.await();
            } catch(Exception e) {
            }

            if (id == 0)
                initialize();
            System.out.println("init");
            
            try {
                barrierThreads.await();
                } catch(Exception e) {
                }
            
            System.out.println("filling");        
            fillUp();

            try {
                barrierMain.await();
            } catch(Exception e) {
            }

        }        
    }
}
