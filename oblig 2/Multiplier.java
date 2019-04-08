public abstract class Multiplier {
    public static double[][] multiply(double[][] a, double[][] b, Oblig2Precode.Mode mode) {
        // a and b are quadratic matrices
        double[][] c = new double[a.length][a.length];

        switch(mode) {
            case SEQ_NOT_TRANSPOSED:
                normal(a, b, c, 0, a.length);
                break;
            case SEQ_A_TRANSPOSED:
                aTrans(Util.transpose(a), b, c, 0, a.length);
                break;
            case SEQ_B_TRANSPOSED:
                bTrans(a, Util.transpose(b), c, 0, a.length);
                break;
            
            // para versions
            default:
                new ParaMultiplier(a, b, c, mode).multiply();
        }
        return c;
    }

    static void normal(double[][] a, double[][] b, double[][] c, int aStart, int aEnd) {
        for (int i = aStart; i < aEnd; i++) {
            for (int j = 0; j < a.length; j++) {
                for (int k = 0; k < a.length; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }            
        }
    }

    static void bTrans(double[][] a, double[][] b, double[][] c, int aStart, int aEnd) {
        for (int i = aStart; i < aEnd; i++) {
            for (int j = 0; j < a.length; j++) {
                for (int k = 0; k < a.length; k++) {
                    c[i][j] += a[i][k] * b[j][k];
                }
            }
        }
    }

    static void aTrans(double[][] a, double[][] b, double[][] c, int aStart, int aEnd) {
        for (int i = aStart; i < aEnd; i++) {
            for (int j = 0; j < a.length; j++) {
                for (int k = 0; k < a.length; k++) {
                    c[i][j] += a[k][i] * b[k][j];
                }
            }
        }
    }
}