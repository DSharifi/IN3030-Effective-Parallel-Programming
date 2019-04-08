import java.util.concurrent.CyclicBarrier;


class ParaMultiplier {
    double[][] a;
    double[][] b;
    double[][] c;

    int cores;
    CyclicBarrier barrier;
    Oblig2Precode.Mode mode;

    int partitionSize;
    int remainder;
    

    ParaMultiplier(double[][] a, double[][] b, double[][] c, Oblig2Precode.Mode mode) {
        this.c = c;
        this.mode = mode;

        switch(mode) {
            case PARA_NOT_TRANSPOSED:
                this.a = a;
                this.b = b;
                break;
            case PARA_A_TRANSPOSED:
                this.a = Util.transpose(a);
                this.b = b;
                break;
            case PARA_B_TRANSPOSED:
                this.a = a;
                this.b = Util.transpose(b);    
        }

        cores = Runtime.getRuntime().availableProcessors();
        barrier = new CyclicBarrier(cores + 1);
    }


    public void multiply() {
        partitionSize = a.length / cores;
        remainder = a.length % cores;

        int indice = 0;

        // start threads
        for (int i = 0; i < remainder; i++, indice += partitionSize + 1) {
            new Thread(new Worker(indice, indice + partitionSize + 1)).start();
        }

        for (int i = remainder; i < cores; i++, indice += partitionSize) {
            new Thread(new Worker(indice, indice + partitionSize)).start();
        }

        try {
            barrier.await();
        } catch(Exception e) { 
            return;
        }

    }


    class Worker implements Runnable {
        int id;

        int aStart;
        int aEnd;

        Worker(int aStart, int aEnd) {
            this.aStart = aStart;
            this.aEnd = aEnd;
        }

        @Override
        public void run() {
            switch(mode) {
                case PARA_NOT_TRANSPOSED:
                    Multiplier.normal(a, b, c, aStart, aEnd);
                    break;
                case PARA_A_TRANSPOSED:
                    Multiplier.aTrans(a, b, c, aStart, aEnd);
                    break;
                case PARA_B_TRANSPOSED:
                    Multiplier.bTrans(a, b, c, aStart, aEnd);
                    break;
            }

            try {
                barrier.await();
            } catch (Exception e) {
            }
        }
    }
}