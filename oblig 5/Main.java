import java.util.Arrays;

class Main {
    static int n;
    static int mode;
    static int threads;

    static int trials = 9;
    static int useBits = 7;

    // store results from sequential version to correct results are given in
    // parallel version when benchmarking.
    static int[][] seqResults;


    static int[] x;
    static int[] y;

    static IntList allpoints;

    public static void main(String[] args) {
        int arg0, arg1, arg2;

        try {
            arg0 = Integer.parseInt(args[0]);
            arg1 = Integer.parseInt(args[1]);
        } catch (Exception e) {
            instructions();
            return;
        }

        mode = arg0;

        if (mode == 2) {
            // benchmarks
            threads = arg1;
            benchmark();

        } else if (mode == 0) {
            // sequential
            n = arg1;
            sequential();

        } else if (mode == 1) {
            // parallel
            n = arg1;
            try {
                arg2 = Integer.parseInt(args[2]);
            } catch (Exception e) {
                instructions();
                return;
            }

            threads = arg2;
            parallel();

        } else {
            // invalid mode;
            System.out.println("Invalid mode!");
            instructions();
        }
    }

    private static void sequential() {
        int[] x = new int[n];
        int[] y = new int[n];

        // data til grafen
        NPunkter17 nPunkter = new NPunkter17(n);
        IntList allPoints = nPunkter.lagIntList();
        nPunkter.fyllArrayer(x, y);

        Oblig5 graph = SeqGraph.findConvexEnvelope(x, y, allPoints);

        System.out.println(graph);
        
        // sett max data for plot
        setMAX(graph);

        // plot
        new TegnUt(graph, graph.envelope);
    }

    private static void parallel() {
        int[] x = new int[n];
        int[] y = new int[n];

        NPunkter17 nPunkter = new NPunkter17(n);
        IntList allPoints = nPunkter.lagIntList();
        nPunkter.fyllArrayer(x, y);

        Oblig5 graph = ParaGraph.findConvexEnvelope(x, y, allPoints, threads);
        // sett max data for plot
        setMAX(graph);

        // plot
        new TegnUt(graph, graph.envelope);
        // new TegnUt(graph, graph.envelope);
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


        int[] nValues = {100, 1000, 10000, 100000, 1000000, 10000000};


        double[] seqTiming = new double[nValues.length];
        double[] paraTiming = new double[nValues.length];

        for (int i = 0; i < nValues.length; i++) {
            int n = nValues[i];

            x = new int[n];
            y = new int[n];

            NPunkter17 nPunkter = new NPunkter17(n);
            allpoints = nPunkter.lagIntList();
            nPunkter.fyllArrayer(x, y);

            seqTiming[i] = seqTimer(nValues[i], i);
            paraTiming[i] = paraTimer(nValues[i], i);
        }


        System.out.println("All n values are executed " + trials + "times. The median times is the one displayed.");
        System.out.println("All timings are in ms");

        for (int i = 0; i < nValues.length; i++) {
            System.out.println("n: " + nValues[i]);
            System.out.println("sequential:\t" + seqTiming[i]);
            System.out.println("parallel:\t" + paraTiming[i]);
            System.out.println("speedup:\t" + (seqTiming[i] / paraTiming[i]) + "\n");
        }

    }

    private static void setMAX(Oblig5 graph) {
        int[] y = graph.y;
        int MAX_Y = y[0];
 
        for (int i : graph.y) {
            if (i > MAX_Y)
                MAX_Y = i;            
        }

        graph.MAX_Y = MAX_Y;
    }

    static double seqTimer(int n, int round) {
        double[] times = new double[trials];

        for (int i = 0; i < trials; i++) {
            long startTime = System.nanoTime();
            SeqGraph.findConvexEnvelope(x, y, allpoints);
            double time = (System.nanoTime() - startTime) / 1000000.0;

            times[i] = time;

        }

        // return median
        Arrays.sort(times);

        return times[trials / 2];

    }

    static double paraTimer(int n, int round) {
        double[] times = new double[trials];

        for (int i = 0; i < trials; i++) {
            long startTime = System.nanoTime();
            ParaGraph.findConvexEnvelope(x, y, allpoints, threads);
            double time = (System.nanoTime() - startTime) / 1000000.0;
            times[i] = time;

        }

        // return median
        Arrays.sort(times);
        return times[trials / 2];

    }

}