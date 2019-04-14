import java.util.concurrent.CyclicBarrier;

class RadixSort {
    int[] sortedArray;
    int[] unsortedArray;

    int[][] allcount;


    int[] localMaxValues;
    int max;


    int threads;
    CyclicBarrier mainBarrier;
    CyclicBarrier threadBarrier;

    RadixSort(int[] unsortedArray, int threads) {
        this.unsortedArray = unsortedArray;
        this.threads = threads;

        localMaxValues = new int[threads];

        sortedArray = new int[unsortedArray.length];

        mainBarrier = new CyclicBarrier(threads + 1);
        threadBarrier = new CyclicBarrier(threads);
        
    }

    // taken from live code session
    public int[] sort() {
        return sortedArray;
    }





    class Worker implements Runnable{
        int id;

        Worker(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            setLocalMax();
            try {
                threadBarrier.await();
            } catch(Exception e) {};

            if (id == 0) {
                // set global max
                max = localMaxValues[0];
                for (int val : localMaxValues) {
                    if (val > max)
                        max = val;
                }
            }

            try {
                threadBarrier.await();
            } catch (Exception e) {
            }

        }


        void setLocalMax() {
            // indice of unsortedArray
            int partitionSize = unsortedArray.length / threads;
            int remainder = unsortedArray.length % threads;

            int startIndex = (id <= remainder) ? partitionSize*id : (partitionSize + 1 )*id;
            int endIndex = (id < remainder) ? (partitionSize)*(id + 1) : (partitionSize + 1)*(id + 1);

            // find local max
            int localMax = unsortedArray[startIndex];
            for (int i = endIndex + 1; i < startIndex; i++) {
                if (unsortedArray[i] > localMax) 
                    localMax = unsortedArray[i];
            }

            localMaxValues[id] = localMax;
        }
    }
}