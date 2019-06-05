import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

class PrimeDeserts {
	ArrayList<int[]> deserts;

    static ArrayList<int[]> findDeserts(int n) {
        int[] primes = new Sieve(n).findPrimes();

        ArrayList<int[]> deserts = new ArrayList<int[]>();
        int lastDesertSize = -1;

        for (int i = 0; i < primes.length - 1; i++) {
            int a = primes[i];
            int b = primes[i + 1];

            int currentDesertSize = b - a - 1;

            if (currentDesertSize > lastDesertSize) {
                lastDesertSize = currentDesertSize;
                int[] pair = {a, b};
                deserts.add(pair);
            }
        }

        return deserts;
    }
}

class ParaPrimeDeserts {
    CyclicBarrier mainBarrier, threadBarrier;

    ArrayList<ArrayList<int[]>> desertList;

    int[] primes;
    int threads;

    ParaPrimeDeserts(int n, int threads) {
        primes = new Sieve(n).findPrimes();

        mainBarrier = new CyclicBarrier(threads + 1);


        this.desertList = new ArrayList<ArrayList<int[]>>();
        
        for (int i = 0; i < threads; i++) {
            desertList.add(new ArrayList<int[]>());
        }

        this.threads = threads;
    }

    ArrayList<int[]> getDeserts() {
        for (int i = 0; i < threads; i++) {
            new Thread(new Worker(i)).start();
        }


        try {
            mainBarrier.await();
        } catch (Exception e) {
            //TODO: handle exception
        }
        

        return cleanup();

    }




    private ArrayList<int[]> cleanup() {
        ArrayList<int[]> deserts = new ArrayList<int[]>();
        
        for (int[] desert : desertList.get(0)) {
            deserts.add(desert);
        }


        int[] lastDesert = deserts.get(deserts.size() - 1);
        int lastDesertLength = lastDesert[1] - lastDesert[0] - 1;

        for (int i = 1; i < desertList.size(); i++) {
            for (int[] desert : desertList.get(i)) {
                int currentLength = desert[1] - desert[0] - 1;

                if (currentLength > lastDesertLength) {
                    deserts.add(desert);
                    lastDesertLength = currentLength;
                }
            } 

        }


        return deserts;
	}




	class Worker implements Runnable {
        int start, end;
        int id;

        ArrayList<int[]> deserts;

        public Worker(int id) {
            this.id = id;
        }

        

        void partition() {
            // indice of a[]
            int partitionSize = primes.length / threads;

            start = partitionSize * id;
            end = (id != threads - 1) ? partitionSize * (1 + id) : primes.length;
        }



		@Override
		public void run() {
            partition();
            deserts = desertList.get(id);
            

            int lastDesertSize = -1;

            for (int i = start; i < end - 1; i++) {
                int a = primes[i];
                int b = primes[i + 1];

                int currentDesertSize = b - a - 1;

                if (currentDesertSize > lastDesertSize) {
                    lastDesertSize = currentDesertSize;
                    int[] desert = {a, b};
                    deserts.add(desert);
                }
            }

            try {
                mainBarrier.await();            
            } catch (Exception e) {
            }
		}
    }
}