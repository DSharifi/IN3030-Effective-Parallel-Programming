import java.util.concurrent.CyclicBarrier;

class ParallelBubbleSort {
    private final int[] arr;
    
    private final CyclicBarrier mainBarrier;
    private final CyclicBarrier threadBarrier;

    private final int threadCount;


    public static void sort(int[] arr, int threadCount) {
        new ParallelBubbleSort(arr, threadCount).run();
    }

    private ParallelBubbleSort(int[] arr, int threadCount) {
        this.arr = arr;
        this.threadCount = threadCount;

        mainBarrier = new CyclicBarrier(threadCount + 1);
        threadBarrier = new CyclicBarrier(threadCount);
    }




    class worker implements Runnable {
        int id;

        int stastartIndexrt;
        int endIndex;

        Worker(int id) {
            this.id = id;
            partition();
        }

        private void partition() {
            int partitionSize = a.length / threads;
            startIndex = partitionSize * id;
            endIndex = (id != threads - 1) ? partitionSize * (1 + id) : a.length;

            if (id == threads - 1)
                endIndex++;
        }

        @Override
        public void run() {

        }

        void bubbleSort() {
            int n = arr.length;
            int temp;
            for (int i = 0; i < n; i++) {
                for (int j = 1; j < (n - i); j++) {
                    if (arr[j - 1] > arr[j]) {
                        // swap elements
                        temp = arr[j - 1];
                        arr[j - 1] = arr[j];
                        arr[j] = temp;
                    }

                }
            }
        }
    }

}