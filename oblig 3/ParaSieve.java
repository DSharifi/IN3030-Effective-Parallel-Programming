import java.util.concurrent.CyclicBarrier;

class ParaSieve {
    private int[] primes;
    private byte[] byteArray;
    private int n;
    private int primesCounter;
    private int cells;
    private CyclicBarrier barrier;


    public ParaSieve(int n) {
        this.n = n;
        cells = n / 16 + 1;
        byteArray = new byte[cells];
    }

    public int[] findPrimes(int threads) {
        findFirstPrimes();


        // parallize rest count 
        paraCountRestPrimes(threads);
        gatherPrimes();

        return primes;
    }

    void findFirstPrimes() {
        primesCounter = 1;
        int currentPrime = 3;
        int squareRootN = (int) Math.sqrt(n);


        while (currentPrime != 0 && currentPrime <= squareRootN) {
            traverse(currentPrime);
            currentPrime = findNextPrime(currentPrime + 2, n);
            primesCounter++;
        }

    }

    void traverse(int p) {
        for (int i = p * p; i < n; i += p * 2) {
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


    void paraCountRestPrimes(int threads) {
        int partitionSize = cells / threads;
        System.out.println(partitionSize);
        int indice = 0;

        
        // start threads
        for (int i = 0; i < threads; i++, indice+= partitionSize) {
            new Thread(new Worker(indice, indice + partitionSize)).start();
        }

        try {
            barrier.await();
        } catch (Exception e) {
            return;
        }
    }

    
    int findNextPrime(int startAt, int end) {
        for (int i = startAt; i < end; i += 2) {
            if (isPrime(i)) {
                return i;
            }
        }
        return -1;
    }

    boolean isPrime(int i) {
        if ((i % 2) == 0) {
            return false;
        }

        int byteCell = i / 16;
        int bit = (i / 2) % 8;

        return (byteArray[byteCell] & (1 << bit)) == 0;
    }


    void gatherPrimes() {
        primes = new int[primesCounter];
        primes[0] = 2;

        int currentPrime = 3;
        for (int i = 1; i < primesCounter; i++) {
            primes[i] = currentPrime;
            currentPrime = findNextPrime(currentPrime + 2, n);
        }
    }

    class Worker implements Runnable {
        int start, end;

        Worker(int start, int end) {
            this.start = start*16;
            this.end = end*16;
        }

        @Override
        public void run() {
            System.out.println("\tstart: " + start +"\tEnd: " + end);
            start = findNextPrime(start, end);
            System.out.println("New start " + start);

            while (start != -1) {
                primesCounter++;
                start = findNextPrime(start + 2, end);
            }
            try {
                barrier.await();
            } catch (Exception e) {
            }
        }        
    }
}
