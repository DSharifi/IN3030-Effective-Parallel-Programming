import java.util.Arrays;

class Main {
    static int trials = 7;
    static int seed = 123;

    public static void main(String[] args) {
        int size = Integer.parseInt(args[0]);

        double[][] a = Oblig2Precode.generateMatrixA(seed, size);
        double[][] b = Oblig2Precode.generateMatrixB(seed, size);


        double time = timeMultiplier(a, b, Oblig2Precode.Mode.PARA_B_TRANSPOSED);
        System.out.println(time);

    }

    static double timeMultiplier(double[][] a, double[][] b, Oblig2Precode.Mode mode) {
        double[] times = new double[trials];

        for (int i = 0; i < trials; i++) {
            long startTime = System.nanoTime();
            Multiplier.multiply(a, b, mode);
            double time = (System.nanoTime() - startTime) / 1000000.0;

            times[i] = time;
        }

        // return median
        Arrays.sort(times);
        return times[trials / 2];
    }
}