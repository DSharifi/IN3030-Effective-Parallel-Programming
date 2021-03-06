import java.util.Arrays;

class Main {
    static int mode;
    static int n;
    static int threads;
    static final int trials = 9;


    public static void main(String[] args) {
        int arg1;
        int arg2;
        int arg3;

        try {
            arg1 = Integer.parseInt(args[0]);
            arg2 = Integer.parseInt(args[1]);

        } catch(Exception e) {
            instructions();
            return;
        }

        mode = arg1;

        if (mode == 0) {
            // sequential
            n = arg2;

            if (n <= 0) {
                System.out.println("N must be positive!");
                instructions();
            }
            sequential();

        } else if (mode == 1) {
            // parallel
            n = arg2;
            if (n <= 0) {
                System.out.println("N must be positive!");
                instructions();
            }

            try {
                arg3 = Integer.parseInt(args[2]);

            } catch (Exception e) {
                System.out.println("Specify a core count!!");
                instructions();
                return;
            }

            if (arg3 < 0) {
                System.out.println("Secify a positive core count!!!");
                instructions();
            }

            
            threads = (arg3 == 0) ? Runtime.getRuntime().availableProcessors() : arg3;
            System.out.println(threads);
            System.out.println(n);
            sequential();



        } else if (mode == 2) {
            // benchmarking
            threads = (arg2 == 0) ? Runtime.getRuntime().availableProcessors() : arg2;
            benchMarks();

        } else {
            System.out.println("Choose a correct mode, either 0, 1 or 2.");
            instructions();
        }
    }

    private static void sequential() {
        SequentialSieve sieve = new SequentialSieve(n);
        int[] primes = sieve.findPrimes();
        long[][] factorization = Factorizer.factorize(primes, n);

        writeFactorization(factorization, n);
    }

    private static void prallel() {
        ParaSieve sieve = new ParaSieve(n);
        int[] primes = sieve.findPrimes(threads);
        System.out.println("Sieve done");
        long[][] factorization = new ParaFactorizer(primes, n, threads).factorize();
        System.out.println("factorizer done");

        writeFactorization(factorization, n);
    }

    private static void benchMarks() {
        int[] numbers = {2000000, 20000000, 200000000, 2000000000};
        int[][] primesSeq = new int[4][];
        int[][] primesPara = new int[4][];

        double[] seqSieveTimings = new double[4];
        double[] paraSieveTimings = new double[4];        

        int round = 0;
        for (int n : numbers) {
            sieveTimer(n, round++, primesSeq, primesPara, seqSieveTimings, paraSieveTimings);
        }

        // check primes are equal;
        if (!assertSieve(primesSeq, primesPara)) {
            System.out.println("Sequential and parallel sieve did not give same results!\nProgram will now exit");
            System.exit(1);
        }

        round = 0;
        double[] seqFactorTiming = new double[4];
        double[] paraFactorTiming = new double[4];

        long[][][] factorizationsSeq = new long[4][][];
        long[][][] factorizationsPara = new long[4][][];

        round = 0;
        for (int n : numbers) {
            factorizationTimer(n, round++, primesSeq, factorizationsSeq, factorizationsPara, seqFactorTiming, paraFactorTiming);
        }

        // check factorization is correct and equal
        if (!assertFactorization(factorizationsSeq, factorizationsPara)) {
            System.out.println("Sequential and parallel factorizers did not give same results!\nProgram will now exit");
            System.exit(1);
        }

        // store results
        for (int i = 0; i < 4; i++) {
            writeFactorization(factorizationsSeq[i], numbers[i]);
        }

        System.out.println("Timings: (all times are in ms)");
        System.out.println("Sieve:");
        for (int i = 0; i < 4; i++) {
            System.out.println("n:" +numbers[i]);
            System.out.println("sequential:\t" + seqSieveTimings[i]);
            System.out.println("parallell:\t" + paraSieveTimings[i] +"\n");
            System.out.println("Speedup:\t" + (seqSieveTimings[i] / paraSieveTimings[i]));

        }

        System.out.println("\nFactorization");
        for (int i = 0; i < 4; i++) {
            System.out.println("n:\t" + numbers[i]);
            System.out.println("sequential:\t" + seqFactorTiming[i]);
            System.out.println("parallell:\t" + paraFactorTiming[i]);
            System.out.println("Speedup:\t" + (seqFactorTiming[i] / paraFactorTiming[i]));
        }

    }


    private static void sieveTimer(int n, int round, int[][] primesArraySequential, int[][] primesArrayParallel, double[] seqTiming, double[] paraTiming) {
        double[] timesSeq = new double[trials];

        for (int i = 0; i < trials; i++) {
            long startTime = System.nanoTime();
            int[] primes = new SequentialSieve(n).findPrimes();
            double time = (System.nanoTime() - startTime) / 1000000.0;
            timesSeq[i] = time;
            primesArraySequential[round] = primes;            
        }

        double[] timesPara = new double[trials];

        for (int i = 0; i < trials; i++) {
            long startTime = System.nanoTime();
            int[] primes = new ParaSieve(n).findPrimes(threads);
            double time = (System.nanoTime() - startTime) / 1000000.0;
            timesPara[i] = time;
            primesArrayParallel[round] = primes;
        }

        Arrays.sort(timesSeq);
        Arrays.sort(timesPara);

        // store median
        seqTiming[round] = timesSeq[trials / 2];
        paraTiming[round] = timesPara[trials / 2];
    }

    private static void factorizationTimer(int n, int round, int[][] primes, long[][][] seqFactorization, long[][][] paraFactorization, double[] seqTiming, double[] paraTiming) {
        double[] timesSeq = new double[trials];

        for (int i = 0; i < trials; i++) {
            long startTime = System.nanoTime();
            long[][] factors = Factorizer.factorize(primes[round], n);
            double time = (System.nanoTime() - startTime) / 1000000.0;
            timesSeq[i] = time;
            seqFactorization[round] = factors;
        }


        double[] timesPara = new double[trials];
        for (int i = 0; i < trials; i++) {
            long startTime = System.nanoTime();
            long[][] factors = new ParaFactorizer(primes[round], n, threads).factorize();
            double time = (System.nanoTime() - startTime) / 1000000.0;
            timesPara[i] = time;
            paraFactorization[round] = factors;
        }

        Arrays.sort(timesSeq);
        Arrays.sort(timesPara);

        seqTiming[round] = timesSeq[trials / 2];
        paraTiming[round] = timesPara[trials / 2];
    }

    private static void writeFactorization(long[][] factorization, int n) {
        Oblig3Precode precode = new Oblig3Precode(n);
        long nn = (long)n*n;

        for (long[] factors : factorization) {
            for (long factor : factors) {
                precode.addFactor(nn, factor);    
            }
            nn--;
        }

        precode.writeFactors();
    }

    // instructions for the user.
    private static void instructions() {
        System.out.println("Please provide proper arguments!\n");
        System.out.println("java Main {mode} {n} {threads}\n");
        System.out.println("mode\n-[0]\tsequential\n-[1]\tparallel\n-[2]\benchmark\n");
        System.out.println("n\nPositive integer. The program will find all primes <= n, " +
        "and prime factorize [n*n-99, n*n]\n");
        System.out.println("threads\nPositive integer. Can not be greater than n/16; Should only be provided if mode 1 is selected. Specifies the number of working threads" + 
        " for parallel version. Choosing 0 will default to the number of cores on the machine");

        System.out.println("Examples: ");
        System.out.println("java Main 0 200\tSequential, n = 200");
        System.out.println("java Main 1 200 4\tParallel, n = 200, threads = 4");
        System.out.println("java Main 1 200 0\tParallel, n = 200, threads = {cores on machine}");
        System.out.println("java Main 2 10\tBenchmarking, n = {2000000, 20000000, 200000000, 2000000000}, threads = 10");

        System.exit(-1);
    }


    private static boolean assertFactorization(long[][][] seq, long[][][] para) {
        if (seq.length != para.length) {
            System.out.println("not same length");   
            return false;
        }

        // check content
        for (int i = 0; i < seq.length; i++) {
            if (seq[i].length != para[i].length)
                return false;
            for (int j = 0; j < seq[i].length; j++) {
                if (seq[i][j].length != para[i][j].length)
                    return false;
                for (int k = 0; k < seq[i][j].length; k++) {
                    if (seq[i][j][k] != para[i][j][k]) {
                        System.out.println(seq[i][j][k] != para[i][j][k]);
                        System.out.println(seq[i][j][k]);
                        System.out.println(para[i][j][k]);
                        return false;
                    }
                }
            }
        }

        // check calculation
        int round = 0;
        long[] numbers = {2000000, 20000000, 200000000, 2000000000};

        for (long[][] factorlist : seq) {
            if (!assertCalculation(numbers[round++], factorlist)) {
                System.out.println("not same calculation");
                return false;
            }
        }

        return true;
    }

    // used to assert factorization was correct
    private static boolean assertCalculation(long n, long[][] factorization) {
        long nn = (long)n * n;

        for (long[] factors : factorization) {
            long product = 1;
            for (long factor : factors) {
                product *= factor;
            }
            if (product != nn--) {
                System.out.println("failed on " + nn);
                System.out.println(product);
                return false;
            }
        }

        return true;
    }



    private static boolean assertSieve(int[][] seq, int[][] para) {
        for (int i = 0; i < 4; i++) {
            if (checkEqualPrimes(seq[i], para[i]) == false)
                return false;
        }

        return true;
    }

    private static boolean checkEqualPrimes(int[] seq, int[] para) {
        if (seq.length != para.length) {
            return false;
        }

        for (int i = 0; i < seq.length; i++) {
            if (seq[i] != para[i]) {
                return false;
            }
        }

        return true;

    }
}