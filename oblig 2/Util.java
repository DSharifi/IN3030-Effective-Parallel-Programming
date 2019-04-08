public abstract class Util {
    
    public static double[][] transpose(double[][] a) {
        double[][] aT = new double[a.length][a.length];
        for (int i = 0; i < a.length; i++)
            for (int j = 0; j < a[0].length; j++)
                aT[j][i] = a[i][j];
                
        return aT;
    }


}