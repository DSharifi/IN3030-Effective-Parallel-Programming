class Sieve {
    private int[] primes;
    private byte[] byteArray;
    private int n;
    private int primesCounter;

    public Sieve(int n) {
        this.n = n;
        int cells = n / 16 + 1;
        byteArray = new byte[cells];
    }

    public int[] findPrimes() {
        findFirstPrimes();
        countRestPrimes();
        gatherPrimes();

        return primes;
    }

    void findFirstPrimes() {
        primesCounter = 1;
        int currentPrime = 3;
        int squareRootN = (int) Math.sqrt(n);

        while (currentPrime != 0 && currentPrime <= squareRootN) {
            traverse(currentPrime);
            currentPrime = findNextPrime(currentPrime + 2);
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

    int findNextPrime(int startAt) {
        for (int i = startAt; i < n; i += 2) {
            if (isPrime(i)) {
                return i;
            }
        }
        return 0;
    }

    boolean isPrime(int i) {
        if ((i % 2) == 0) {
            return false;
        }

        int byteCell = i / 16;
        int bit = (i / 2) % 8;

        return (byteArray[byteCell] & (1 << bit)) == 0;
    }

    void countRestPrimes() {
        int startAt = (int) Math.sqrt(n) + 1;

        if (startAt % 2 == 0) {
            startAt++;
        }

        startAt = findNextPrime(startAt);
        while (startAt != 0) {
            primesCounter++;
            startAt = findNextPrime(startAt + 2);
        }
    }

    void gatherPrimes() {
        primes = new int[primesCounter];
        primes[0] = 2;

        int currentPrime = 3;
        for (int i = 1; i < primesCounter; i++) {
            primes[i] = currentPrime;
            currentPrime = findNextPrime(currentPrime + 2);
        }
    }
}