import java.util.Arrays;

public class Oblig4 {
	int n;
	int seed;
	
	int useBits = 4;
	
	public static void main(String[] args) {
		if(args.length != 2) {
			System.out.printf("This program takes two arguments: <n> <seed>\n");
			return;//System.exit(1);
		}

		int n = Integer.parseInt(args[0]);
		int seed = Integer.parseInt(args[1]);
		
		Oblig4 o4 = new Oblig4(n, seed);
		
		int unsortedArray[] = Oblig4Precode.generateArray(n, seed);
		
		int[] sortedArray = o4.sort(unsortedArray);
				
		Oblig4Precode.saveResults(Oblig4Precode.Algorithm.SEQ, seed, sortedArray);
		
	}
	
	public Oblig4(int n, int seed) {
		this.n = n;
		this.seed = seed;
	}
	
	private int[] sort(int[] unsortedArray) {
		int[] a = unsortedArray;
		
		// STEP A - Find max value
		int max = 0;
		for(int i=0; i < a.length; i++)
			if(a[i] > max) max = a[i];
		
		// Discover how many bits the max value needs
		int numBits = 1;
		while(max >= (1L << numBits)) numBits++;
				
		// Calculate how many digits we need
		int numDigits = Math.max(1, numBits/useBits);
		
		System.out.printf("Max %d giving %d bits and %d digits when useBits %d\n", max, numBits, numDigits, useBits);

		
		int[] bit = new int[numDigits];
		
		int rest = numBits % numDigits;
		
		// Distribute the bits over the digits
		for(int i=0; i < bit.length; i++) {
			bit[i] = numBits/numDigits;
			
			if(rest-- > 0) bit[i]++;	
		}
		
		int[] b = new int[a.length];
		
		int shift = 0;
		
		for(int i=0; i < bit.length; i++) {
			System.out.printf("Round %d with maskLength %d and shift %d\n",i, bit[i], shift);
			radixSort(a, b, bit[i], shift);
			shift += bit[i];
			
			int[] tmp = a;
			a = b;
			b = tmp;
		}
		
		return a;
	}
	
	
	private void radixSort(int[] a, int[] b, int maskLen, int shift) {
		
		// The size / mask of the digit we are interested in this turn
		int mask = (1<< maskLen) - 1;
		
		// The count of each digit
		int[] digitFrequency = new int[mask+1];
		
		// STEP B - Count frequency of each digit
		for(int i=0; i < a.length; i++) {
			digitFrequency[(a[i] >>> shift) & mask]++;
		}
		
		// STEP C - Calculate pointers for digits
		int accumulated = 0;
		int[] digitPointers = new int[digitFrequency.length];
		
		for(int i=0; i < digitFrequency.length; i++) {
			digitPointers[i] = accumulated;
			accumulated += digitFrequency[i];
		}
		
		
		// STEP D - Move numbers into correct places
		for(int i = 0; i < a.length; i++) {
			b[digitPointers[(a[i] >>> shift) & mask]++] = a[i];
		}
	}

}
















