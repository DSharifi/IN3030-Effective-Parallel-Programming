import java.util.Arrays;

/*
    This is a working solution of sequential radix sort. Feel free to change it as you please
    I may edit it later with better comments
*/

class SequentialRadix {
	static int DIGIT_BITS; // Used for calculating number of digits later

	public static void sort(int[] a, int useBits) {
		DIGIT_BITS=useBits;
		
		int max = a[0]; // Varaible for max value
		int n = a.length; // Length of array
		int maxNumberBits = 2; // Number of bits needed for largest number (calculated later)
		int numDigits; // How many different digits we use
		int[] bit; // bit.length = Amount of different digits. bit[i] = digitlength of digit i

		// Part A: Find max value in a
		for (int i = 1; i < n; i++) {
			if (a[i] > max)
				max = a[i];
		}

		// Count how many bits needed for the largest number
		while (max >= 1L << maxNumberBits)
			maxNumberBits++;

		// How many digits we work on
		numDigits = Math.max(1, maxNumberBits / DIGIT_BITS);
		bit = new int[numDigits];
		int rest = maxNumberBits % DIGIT_BITS;

		// Divide the parts that we sort on equally.
		for (int i = 0; i < bit.length; i++) {
			bit[i] = maxNumberBits / numDigits;
			if (rest-- > 0)
				bit[i]++;
		}

		int[] temp = a;
		int[] b = new int[n];
		int sum = 0; // Used for shifting to the digit we are working on in radixSort

		for (int i = 0; i < bit.length; i++) {
			// Sorting on digit i.
			radixSort(a, b, bit[i], sum);
			sum += bit[i];
			// Swap the arrays.
			temp = a;
			a = b;
			b = temp;
		}

		// If the end result ends up in the b array, copy it to the a array
		if ((bit.length & 1) != 0) {
			System.arraycopy(a, 0, b, 0, a.length);
		}
	}

	public static void radixSort(int[] a, int[] b, int maskLen, int shift) {
		int acumVal = 0; // Used later for making "pointers"
		int temp;
		int n = a.length;
		int mask = (1 << maskLen) - 1; // a bitstring of 1's with the length we need.
		int[] count = new int[mask + 1]; // For counting occurences.

		// Part B: Counting occurences of digits
		for (int i = 0; i < n; i++) {
			count[a[i] >>> shift & mask]++;
		}

		// Part C: Making "pointers" so we know where to put the numbers from a to b
		for (int i = 0; i <= mask; i++) {
			temp = count[i];
			count[i] = acumVal;
			acumVal += temp;
		}

		// Part D: Move numbers from a to b.
		for (int i = 0; i < n; i++) {
			b[count[(a[i] >>> shift) & mask]++] = a[i];
		}
	}

}
