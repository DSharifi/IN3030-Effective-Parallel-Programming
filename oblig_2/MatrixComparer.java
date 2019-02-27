public abstract class MatrixComparer {
    public static boolean compare(double[][] a, double[][]b) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                if (a[i][j] != b[i][j])
                    return false;
            }
        }
        return true;
    }
}