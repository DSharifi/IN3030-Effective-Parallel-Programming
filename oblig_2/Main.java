import java.util.Arrays;

class Main {
    static int trials = 7;
    static int seed = 123;
    static Oblig2Precode.Mode execMode;


    public static void main(String[] args) {
        
        int mode = Integer.parseInt(args[0]);
        int size = Integer.parseInt(args[1]);


        switch (mode) {
            case 0:
                execMode = Oblig2Precode.Mode.SEQ_NOT_TRANSPOSED;
                break;
            case 1:
                execMode = Oblig2Precode.Mode.SEQ_A_TRANSPOSED;
                break;
            case 2:
                execMode = Oblig2Precode.Mode.SEQ_B_TRANSPOSED;
                break;
            case 3:
                execMode = Oblig2Precode.Mode.PARA_NOT_TRANSPOSED;
                break;
            case 4:
                execMode = Oblig2Precode.Mode.PARA_A_TRANSPOSED;
                break;
            case 6:
                execMode = Oblig2Precode.Mode.PARA_B_TRANSPOSED;
                break;
            default:
                System.out.println("Please provide a proper computation mode");
                return;
        }

        double[][] a = Oblig2Precode.generateMatrixA(seed, size);
        double[][] b = Oblig2Precode.generateMatrixB(seed, size);

        System.out.println(timeMultiplier(a, b, execMode));

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