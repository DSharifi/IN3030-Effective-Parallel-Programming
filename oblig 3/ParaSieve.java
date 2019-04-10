import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;


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

    private ReentrantLock l = new ReentrantLock();
    private ReentrantLock lo = new ReentrantLock();

    


    public ParaSieve(int n) {
        this.n = n;
        this.m = (int) Math.sqrt(n);
        this.mSqrt = (int) Math.sqrt(m);

        cells = n / 16 + 1;
        byteArray = new byte[cells];
    }


    void printPrimes() {
        for (int i = 0; i <= m; i++) {
            if (isPrime(i))
                System.out.println(i);
        }
    }

    public int[] findPrimes(int threads) {      
        findFirstPrimes();
        para(threads);
        primes[0] = 2;
        return primes;
    }

    boolean isPrime(int i) {
        if ((i & 1) == 0) {
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
        this.threads = threads;
        this.primesNested = new int[threads][];


        int startByte = m / 16;        
        int partitionSize = (cells - startByte) / threads;
        int remainder = (cells - startByte)  % threads;


        System.out.println("msqrt " + mSqrt);
        System.out.println("startByte " + startByte);
        
        
        
        int indice = startByte;

        barrierMain = new CyclicBarrier(threads + 1);
        barrierThreads = new CyclicBarrier(threads);
        
        


        // start threads
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
            this.id = id;
            this.start = start*16 + 1;

            if (id == threads - 1)
                // last thread
                this.end = ((n & 1) == 0) ? n+1 : n+2;
            else
                // all others        
                this.end = end*16 + 1;

            System.out.println(id + ":\t" + this.start + "\t" + this.end);
        }


        void traverse(int p, int startFactor, int endFactor) {
            for (int i = startFactor * p; i < this.end; i += p * 2) {
                flip(i);
            }

        }

        void traversePartly() {
            int startFactor, endFactor;
            int currentPrime = 3;

            int end = this.end;
            startFactor = (this.start + currentPrime - 1) / currentPrime;
            
            // (this.start + this.start % currentPrime) / currentPrime;
            startFactor = (currentPrime < startFactor) ? startFactor : currentPrime;

            endFactor = (end- 2) / currentPrime;

            System.out.println(end);

            if ((startFactor & 1) == 0)
                startFactor++;

            if ((endFactor & 1) == 0)
                endFactor--;



            while (currentPrime != 0 && currentPrime <= m && currentPrime*currentPrime <= this.end - 2) {
                traverse(currentPrime, startFactor, endFactor);
                
                currentPrime = findNextPrime(currentPrime + 2);

                if (currentPrime == 0)
                    break;

                // ceil division
                startFactor = (this.start + currentPrime - 1) / currentPrime;
                // might have to start lower
                startFactor = (currentPrime < startFactor) ? startFactor : currentPrime;

                endFactor = (end-2) / currentPrime;

                if ((startFactor & 1) == 0)
                    startFactor++;

                if ((endFactor & 1) == 0)
                    endFactor--;

            }

        }

        private void gather() {    
            int count = 0;
            l.lock();
            
                 
            // count primes
            for (int i = (id == 0) ? 1 : start; i < end; i++) {
                if(isPrime(i)) {
                    count++;
                }
            }

            // gather them
            localPrimes = new int[count];

            int j = 0;
            for (int i = (id == 0) ? 1 : start; i < end && j < count; i++) {
                if(isPrime(i)) 
                    localPrimes[j++] = i;
            }

            primesNested[id] = localPrimes;
            l.unlock();

        }



        private void initialize() {
            primeCount = 0;
            for (int[] list : primesNested) {
                primeCount += list.length;
            }

            primes = new int[primeCount];
        }

        private void fillUp() {
            l.lock();
            int startIndex = 0;

            if (id != 0) {
                for (int i = 0; i < id; i++) {
                    startIndex += primesNested[i].length;
                }                    
            }

            System.out.println("ID:\t" + id);
            System.out.println("startIndex:\t" + startIndex);
            System.out.println("endINdex:\t" + (localPrimes.length + startIndex));

            int j = startIndex;

            for (int i = 0; i < localPrimes.length; i++) {
                primes[j++] = localPrimes[i];
            }

            l.unlock();

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
            traversePartly();
            gather();
            System.out.println("Local primes:\t" + Arrays.toString(localPrimes));
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
