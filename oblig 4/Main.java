import java.util.Arrays;

class Main {
    static int n;
    static int seed;
    static int mode;
    static int threads;
    
    static int trials = 9;
    static int useBits = 2;
    

    // store results from sequential version to  correct results are given in
    // parallel version when benchmarking.
    static int[][] seqResults;
	
	public static void main(String[] args) {
        int arg0, arg1, arg2, arg3;

        try {
            arg0 = Integer.parseInt(args[0]);
            arg1 = Integer.parseInt(args[1]);
            arg2 = Integer.parseInt(args[2]);
        } catch(Exception e) {
            instructions();
            return;
        }

        mode = arg0;
        seed = arg1;

        // check if args are positive
        if (seed <= 0 || arg2 <= 0) 
            instructions();
        

        
        if (mode == 0) {
            n = arg2;
            sequential();

        } else if (mode == 1) {
            n = arg2;

            try { 
                arg3 = Integer.parseInt(args[3]);
                if (arg3 <= 0)
                    throw new Exception();
            } catch (Exception e) {
                instructions();
                return;
            }

            threads = arg3;

            parallel();

        } else if (mode == 2) {
            threads = arg2;
            benchmark();

        } else {
            instructions();
        }
		
    }

    private static void sequential() {
        int[] arr = Oblig4Precode.generateArray(n, seed);
        SequentialRadix.sort(arr, useBits);
        Oblig4Precode.saveResults(Oblig4Precode.Algorithm.SEQ, seed, arr);

        
    }

    private static void parallel() {
        int[] arr = Oblig4Precode.generateArray(n, seed);
        new ParallelRadix(arr, threads, useBits).sort();
        Oblig4Precode.saveResults(Oblig4Precode.Algorithm.PARA, seed, arr);

    }


    // instructions for the user.
    private static void instructions() {
        System.out.println("Please provide proper arguments!\n");
        System.out.println("java Main {mode} {seed}..{ }..{ }..\n");
        System.out.println("mode\n-[0]\tsequential\n-[1]\tparallel\n-[2]\tbenchmark\n");

        // mode 0 (sequential)
        System.out.println("Sequential Radix Sort:");
        System.out.println("java Main 0 {seed} {n}\n");

        // mode 1 (parallel)
        System.out.println("Parallel Radix Sort:");
        System.out.println("java Main 1 {seed} {n} {threads}\n");

        // mode 2 (benchmarking)
        System.out.println("Benchmarking (Sequential vs Parallel):");
        System.out.println("java Main 2 {seed} {threads}\n\n");


        // additinal information
        System.out.println("{seed}, {n} and {threads} must all be positive integers!");


        System.exit(1);
    }


    static void benchmark() {
        int[] nValues = {1000, 10000, 100000, 1000000, 10000000, 100000000};

        seqResults = new int[nValues.length][];
        
        double[] seqTiming = new double[nValues.length];

        // assert sequential results;

        double[] paraTiming = new double[nValues.length];
        
        for (int i = 0; i < nValues.length; i++) {
            seqTiming[i] = seqTimer(nValues[i], i);
            paraTiming[i] = paraTimer(nValues[i], i);
        }

        System.out.println("All n values are executed " + trials + "times. The median times is the one displayed.");
        System.out.println("All timings are in ms");

        for (int i = 0; i < nValues.length; i++) {
            System.out.println("n: " + nValues[i]);
            System.out.println("sequential:\t" + seqTiming[i]);
            System.out.println("parallel:\t" + paraTiming[i]);
            System.out.println("speedup:\t" + (seqTiming[i]/paraTiming[i]) + "\n");
        }

    }


    static double seqTimer(int n, int round) {
        double[] times = new double[trials];


        for (int i = 0; i < trials; i++) {
            int[] unsortedArray = Oblig4Precode.generateArray(n, seed);
            long startTime = System.nanoTime();
            SequentialRadix.sort(unsortedArray, useBits);
            double time = (System.nanoTime() - startTime) / 1000000.0;

            times[i] = time;

            // store result
            seqResults[round] = unsortedArray;

        }

        // return median
        Arrays.sort(times);


        return times[trials / 2];

    }

    static double paraTimer(int n, int round) {
        double[] times = new double[trials];

        for (int i = 0; i < trials; i++) {
            int[] unsortedArray = Oblig4Precode.generateArray(n, seed);
            long startTime = System.nanoTime();
            new ParallelRadix(unsortedArray, threads, useBits).sort();
            double time = (System.nanoTime() - startTime) / 1000000.0;

            times[i] = time;

            // assert result
            if (!Arrays.equals(unsortedArray, seqResults[round])) {
                System.out.println("error: Sequential and Parallel algorithms are not outputting same results!");
                System.exit(1);
            }
        }

        // return median
        Arrays.sort(times);
        return times[trials / 2];

    }

} 