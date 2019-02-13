import java.util.Arrays;
import java.util.Random;

class Main {
    static int seed = 972;

    public static void main(String[] args) {
        int n = 0;
        int k = 0;
        
        try {
            n = Integer.parseInt(args[0]);
            k = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.out.println("Provide proper sys args");
            System.exit(0);
        }
        
        int trials = 7;
 
        double a2Median = timeA2(trials, n, k);
        double arrSortMedian = timeArrSort(trials, n);
        double a2ParaMedian = timeA2Para(trials, n , k);

        System.out.println("a2 median:\t\t\t" + a2Median + 
        "\nArrays.sort median:\t\t" + arrSortMedian +"\na2 parallel:\t\t\t" + a2ParaMedian);
        
    }


    static double timeA2Para(int trials, int n, int k) {
        double[] times = new double[trials];

        for (int i = 0; i < trials; i++) {
            int[] randomArray = createArray(seed, n);

            long startTime = System.nanoTime();
            new A2Parallel(randomArray, k).sort();
            double time = (System.nanoTime() - startTime) / 1000000.0;

            times[i] = time;


        }
        // return median
        Arrays.sort(times);
        return times[trials / 2];
    }

    static double timeA2(int trials, int n, int k) {
        double[] times = new double[trials];
        for (int i = 0; i < trials; i++) {
            int[] randomArray = createArray(seed, n);

            long startTime = System.nanoTime();
            A2.a2(randomArray, k);
            double time = (System.nanoTime()-startTime) / 1000000.0;

            times[i] = time;


        }
        // return median
        Arrays.sort(times);
        return times[trials/2];
    }

    static double timeArrSort(int trials, int n) {

        double[] times = new double[trials];

        for (int i = 0; i < trials; i++) {
            int[] randomArray = createArray(seed, n);
            long startTime = System.nanoTime();
            Arrays.sort(randomArray);
            double time = (System.nanoTime() - startTime) / 1000000.0;
            times[i] = time;
        }

        // return median
        Arrays.sort(times);
        return times[trials/2];
    }

    static int[] createArray(int seed, int n) {
        Random r = new Random(7361);
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = r.nextInt(i + 1);
        }
        return array;
    }
}