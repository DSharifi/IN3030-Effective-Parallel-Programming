import java.util.concurrent.CyclicBarrier;

class ParallelRadix {
    int useBits;
    int[] a, b;

    int[][] allcount;
    int[] count;

    int[] bit;

    int[] localMaxValues;

    int[][] allDigitPointers;

    // int[] digitPointers;


    int max;
    int threads;

    CyclicBarrier mainBarrier;
    CyclicBarrier threadBarrier;

    ParallelRadix(int[] unsortedArray, int threads, int useBits) {
        this.a = unsortedArray;
        this.b = new int[a.length];

        this.allcount = new int[threads][];
        this.allDigitPointers = new int[threads][];

        this.threads = threads;
        this.useBits = useBits;

        localMaxValues = new int[threads];
        
        mainBarrier = new CyclicBarrier(threads + 1);
        threadBarrier = new CyclicBarrier(threads);
    }


    // taken from live code session
    public void sort() {
        for (int i = 0; i < threads; i++) {
            new Thread(new Worker(i, a, b)).start();
        }

        try {
            mainBarrier.await();
        } catch (Exception e) {
        }

    }

    class Worker implements Runnable {
        int id;

        // part of a[] being owned
        int startIndex;
        int endIndex;

        int[] a;
        int[] b;

        Worker(int id, int[] a, int[] b) {
            this.id = id;

            this.a = a;
            this.b = b;

            partition();

        }


        @Override
        public void run() {
            setLocalMax();
            try {
                threadBarrier.await();
            } catch (Exception e) {
            }
            setGlobalMax();

            int n = a.length; // Length of array
            int maxNumberBits = 2; // Number of bits needed for largest number (calculated later)
            int numDigits; // How many different digits we use
            int[] bit; // bit.length = Amount of different digits. bit[i] = digitlength of digit i


		    // Count how many bits needed for the largest number
            while (max >= 1L << maxNumberBits)
                maxNumberBits++;

            // How many digits we work on
            numDigits = Math.max(1, maxNumberBits / useBits);
            bit = new int[numDigits];
            int rest = maxNumberBits % useBits;

            // Divide the parts that we sort on equally.
            for (int i = 0; i < bit.length; i++) {
                bit[i] = maxNumberBits / numDigits;
                if (rest-- > 0)
                    bit[i]++;
            }

            int[] temp = a;
            int sum = 0; // Used for shifting to the digit we are working on in radixSort


            for (int i = 0; i < bit.length; i++) {
                // Sorting on digit i.
                paraRadixSort(a, b, bit[i], sum);
                sum += bit[i];
                // Swap the arrays.
                temp = a;
                a = b;
                b = temp;
            }

            if (id == 0 && (bit.length & 1) != 0) {
                System.arraycopy(a, 0, b, 0, a.length);
            }

            try {
                mainBarrier.await();
            } catch (Exception e) {
                //TODO: handle exception
            }
        }





        
        void paraRadixSort(int[] a, int[] b, int maskLen, int shift) {
        
            // The size / mask of the digit we are interested in this turn
            int mask = (1 << maskLen) - 1;

            // The count of each digit
            int[] count = new int[mask + 1];

            // STEP B - Count frequency of each digit
            for (int i = startIndex; i < endIndex; i++) {
                count[a[i] >>> shift & mask]++;
            }

            // store local count
            allcount[id] = count;

            // digit pointers
            int[] localDigitPointer = new int[count.length];
            allDigitPointers[id] = localDigitPointer; 




            try {
                threadBarrier.await();
            } catch (Exception e) {
            }

            if (id == 0) {
                // STEP C - Calculate pointers for digits
                int accumulated = 0;
                for (int i = 0; i < count.length; i++) {
                    for (int j = 0; j < threads; j++) {
                        allDigitPointers[j][i] = accumulated;
                        accumulated += allcount[j][i];
                    }
                }
            }


            try {
                threadBarrier.await();
            } catch (Exception e) {
            }

            // STEP D - Move numbers into correct places
            for (int i = startIndex; i < endIndex; i++) {
                b[localDigitPointer[(a[i] >>> shift) & mask]++] = a[i];
            }

            try {
                threadBarrier.await();
            } catch(Exception e) {}
        }

        void setGlobalMax() {
            int localMax = localMaxValues[0];

            for (int val : localMaxValues) {
                if (val > localMax)
                    localMax = val;
            }
            
            max = localMax;
        }

        void partition() {
            // indice of a[]
            int partitionSize = a.length / threads;

            startIndex = partitionSize * id;
            endIndex = (id != threads - 1) ? partitionSize * (1 + id) : a.length;
        }

        void setLocalMax() {
            // find local max
            int localMax = a[startIndex];
            for (int i = startIndex; i < endIndex; i++) {
                if (a[i] > localMax)
                    localMax = a[i];
            }

            localMaxValues[id] = localMax;
        }
    }
}