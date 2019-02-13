import java.util.concurrent.*;

class A2Parallel {
    CyclicBarrier b;
    int[] a;
    int cores;
    int k;

    public A2Parallel(int[] a, int k) {
        this.a = a;
        this.k = k;

        this.cores = Runtime.getRuntime().availableProcessors();
        b = new CyclicBarrier(cores + 1);
    }

    public void sort() {
        int partitionSize = a.length / cores;
        int remainder = a.length % cores;

        // keeps track of each threads starting position
        int[] startIndices = new int[cores];
        int indice = 0;

        // start threads
        for (int i = 0; i < remainder; i++, indice += partitionSize + 1) {
            startIndices[i] = indice;
            new Thread(new Parallel(indice, indice + partitionSize)).start();
        }

        for (int i = remainder; i < cores; i++, indice += partitionSize) {
            startIndices[i] = indice;
            new Thread(new Parallel(indice, indice + partitionSize - 1)).start();
        }

        try {
            b.await();
        } catch (Exception e) {
        }

        // keeps tab on indexes of the K biggest values, sorted from greatest to smallest.
        int[] kPositions = new int[k];

        // loop through all the sub arrays, finding max val each time, and store it in kPosition
        for (int i = 0; i < k; i++) {
            int max = a[startIndices[0]];
            int maxPosition = startIndices[0];

            int j = 0;
            int kPos = 0;
            while (j < cores) {
                if (max < a[startIndices[j]]) {
                    max = a[startIndices[j]];
                    maxPosition = startIndices[j];
                    kPos = j;
                }
                ++j;
            }

            kPositions[i] = maxPosition;
            ++startIndices[kPos];

        }

        // swap all the greatest values with [0...k-1]
        for (int i = 0; i < k; i++) {
            A2.swap(a, kPositions[i], i);
        }

    }

    class Parallel implements Runnable {
        // runs sequential algorithm on a[v..k]
        int v;
        int h;

        // range to sort
        public Parallel(int v, int h) {
            this.v = v;
            this.h = h;
        }

        public void run() {
            A2.a2(a, v, k, h);
            try {
                b.await();
            } catch (Exception e) {
                return;
            }
        }
    }
}
