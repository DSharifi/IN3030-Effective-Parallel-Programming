import java.io.PrintWriter;
import java.util.Random;

/**
 * Pre-code for Oblig 2 (Matrix multiplication) IN3030 - Spring 2019
 * 
 * @author Magnus Espeland <magnuesp@ifi.uio.no>
 * @date 2019.02.08
 * 
 * HOW TO USE THIS:
 * 
 * To get the matrices you are going to multiply:
 * (Remember to read 'seed' and 'n' from the command line arguments)
 * 
 * double[][] A = Oblig2Precode.generateMatrixA(seed,n);
 * double[][] B = Oblig2Precode.generateMatrixB(seed,n);
 * 
 * (Notice it's two different methods)
 * 
 * Then do your multiplications in the various ways:
 * - Sequential with no transpose
 * - Sequential with A transposed
 * - Sequential with B transposed
 * - Parallel with no transpose
 * - Parallel with A transposed
 * - Parallel with B transposed
 * 
 * When done, save all the results.
 *
 * Example:
 * 
 * Oblig2Precode.saveResult(seed, Oblig2Precode.Mode.SEQ_NOT_TRANSPOSED, seqNTResult);
 * 
 * Beware that it'll only actually write files with max n=100.
 * 
 * 
 * Example file output:
 *
 *	seed=42 mode=SEQ_NOT_TRANSPOSED n=5
 *
 *	1.35 2.27 1.73 1.07 1.69 
 *	1.42 2.35 1.95 0.98 1.72 
 *	1.19 2.14 1.87 1.09 1.47 
 *	0.92 1.90 1.87 1.21 1.53 
 *	1.22 2.17 1.61 0.97 1.65 
 *
 * 
 * 
 *
 */

class Oblig2Precode {

	/**
	 * The modes of operation your program should execute and measure.
	 * 
	 */
	public enum Mode {
		SEQ_NOT_TRANSPOSED, 
		SEQ_A_TRANSPOSED, 
		SEQ_B_TRANSPOSED, 
		PARA_NOT_TRANSPOSED,
		PARA_A_TRANSPOSED,
		PARA_B_TRANSPOSED
	}


	/**
	 * This method will generate the A matrix you are supposed to multiply with B.
	 * 
	 * @param seed The RNG seed
	 * @param n Size of matrix (NxN)
	 * @return Matrix A
	 */
	
	public static double[][] generateMatrixA(int seed, int n) {
		return generateMatrix(seed,n);
	}
	

	/**
	 * This method will generate the B matrix you are supposed to multiply with A.
	 * 
	 * @param seed The RNG seed
	 * @param n Size of matrix (NxN)
	 * @return Matrix B
	 */
	public static double[][] generateMatrixB(int seed, int n) {
		return generateMatrix(seed + 1,n);
	}
	
	
	
	/**
	 * For internal use, actually generating the matrix 
	 */

	private static double[][] generateMatrix(int seed, int n) {
		double[][] m = new double[n][n];

		Random rnd = new Random(seed);

		for(int i=0; i < n; i++)
			for(int j=0; j < n; j++)
				m[i][j] = rnd.nextDouble();

		return m;
	}

	/**
	 * Method for saving your result to a file.
	 * 
	 * Modes are:
	 * 's' : Sequential 
	 * 
	 * 
	 * @param seed The seed used in generateMatrix
	 * @param mode Which mode is this result from?
	 * @param m The result of your matrix multiplication using this mode
	 */
	
	
	public static void saveResult(int seed, Mode mode, double[][] m) {

		if(m.length > 100)
			return;


		String filename = String.format("O2Result_%d_%s_%d.txt", seed, mode, m.length);

		try (PrintWriter writer = new PrintWriter(filename)) {
			writer.printf("seed=%d mode=%s n=%d\n\n", seed, mode, m.length);

			for(int i=0; i< m.length; i++) {
				for(int j=0; j<m[0].length; j++) {

					writer.printf("%.2f ", m[i][j]);
				}

				writer.println();
			}

			writer.flush();
			writer.close();

		} catch(Exception e) {
			System.out.printf("Got exception when trying to write file %s : %s",filename, e.getMessage());
		}
	}
	
	




}