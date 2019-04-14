import java.io.PrintWriter;
import java.util.Random;

/**
 * 
 * @author Magnus Espeland <magnuesp@ifi.uio.no>
 * @changed 2019.03.15
 * 
 *          Class for ensuring unified output from Oblig 4, IN3030 - Spring 2019
 * 
 *          Usage:
 * 
 *          Your code should take n (int) and a seed (int) as command line
 *          parameters.
 * 
 *          To get the array you are going to sort: -- int[] arr =
 *          Oblig4Precode.generateArray(n, seed); --
 * 
 *          When you are done sorting, call this method to save some of your
 *          results: -- Oblig4Precode.saveResults(Oblig4Precode.Algorithm.SEQ,
 *          seed, arr); --
 * 
 *          Please ask questions in Piazza or by mail.
 * 
 *          Good luck! :)
 * 
 */

public class Oblig4Precode {

    enum Algorithm {
        SEQ, PARA
    }

    public static int[] generateArray(int n, int seed) {
        int[] ret = new int[n];

        Random rnd = new Random(seed);

        int max = (n > Integer.MAX_VALUE / 4) ? Integer.MAX_VALUE : n * 4;

        for (int i = 0; i < ret.length; i++)
            ret[i] = rnd.nextInt(max);

        return ret;

    }

    public static void saveResults(Algorithm algo, int seed, int[] arr) {
        String filename = String.format("O4Result_%s_%d_%d.txt", algo, seed, arr.length);

        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.printf("Results for n=%d with seed=%d\n", arr.length, seed);

            if (arr.length <= 100) {
                for (int i = 0; i < arr.length; i++)
                    writer.println(i + " : " + arr[i]);

            } else {
                for (int i = 0; i < 20; i++)
                    writer.println(i + " : " + arr[i]);

                int half = arr.length / 2;

                for (int i = half; i < half + 20; i++)
                    writer.println(i + " : " + arr[i]);

                for (int i = arr.length - 20; i < arr.length; i++)
                    writer.println(i + " : " + arr[i]);

            }

            writer.flush();
            writer.close();

        } catch (Exception e) {
            System.out.printf("Got exception when trying to write file %s : ", filename, e.getMessage());
        }

    }

}
