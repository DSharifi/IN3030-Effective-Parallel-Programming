import java.util.concurrent.CyclicBarrier;

class ParaGraph {
    int[] x, y;
    Oblig5 graph;
    
    final private int threads;

    private CyclicBarrier threadBarrier;
    private CyclicBarrier mainBarrier;

    // index of min and max X
    int MIN_X;
    int MAX_X;

    int[] local_MIN_X;
    int[] local_MAX_X;

    int[] localLeft;
    int[] localRight;

    int topLeft;
    int topRight;

    // all values;
    IntList all;


    // ---
    IntList leftSide = new IntList();
    IntList rightSide = new IntList();
    IntList onLine = new IntList();

    IntList[] leftSides;
    IntList[] rightSides;
    IntList[] onLines;
    
    // path of envelope
    IntList envelope;
    
    IntList leftEnvelope = new IntList();
    IntList rightEnvelope = new IntList();
    
    private ParaGraph(int[] x, int[] y, IntList all, int threads) {
        this.x = x;
        this.y = y;

        this.envelope = new IntList();
        this.graph = new Oblig5(x, y, envelope);
        this.all = all;

        this.threads = threads;

        this.threadBarrier = new CyclicBarrier(threads);
        this.mainBarrier = new CyclicBarrier(threads + 1);        
    }

    public static Oblig5 findConvexEnvelope(int[] x, int[] y, IntList all, int threads) {
        ParaGraph paraSolver = new ParaGraph(x, y, all, threads);
        paraSolver.start();

        paraSolver.graph.MAX_X = paraSolver.MAX_X;

        try {
            paraSolver.mainBarrier.await();
        } catch(Exception e) {}

        return paraSolver.graph;
    }

    private int[] lineEquation(int p1, int p2) {
        int x1, x2, y1, y2;
        int a, b, c;

        x1 = x[p1];
        x2 = x[p2];

        y1 = y[p1];
        y2 = y[p2];

        a = y1 - y2;
        b = x2 - x1;
        c = y2 * x1 - y1 * x2;

        return new int[] {a, b, c};
    }

    private void start() {
        // initialize with values left and right from p1 to p2;
        envelope.add(MAX_X);
        findValues(all, MIN_X, MAX_X);
        envelope.add(MIN_X);
        findValues(all, MAX_X, MIN_X);
    }

    private void findValues(IntList possibleValues, int p1, int p2) {
        int[] abc = lineEquation(p1, p2);

        int a = abc[0];
        int b = abc[1];
        int c = abc[2];

        // if still negative, val was never set;
        int topLeft = -1;
        int distance = 0;

        // values for iteration
        int pos;
        int d, x, y;

        IntList onLine = new IntList();
        IntList leftSide = new IntList();

        // System.out.println("Left of " + p1 + " --> " + p2);

        for (int i = 0; i < possibleValues.len; i++) {
            // find values on the
            pos = possibleValues.get(i);

            x = this.x[pos];
            y = this.y[pos];

            d = a * x + b * y + c;

            if (d == 0 && i != p1 && i != p2)
                onLine.add(i);

            else if (d > 0) {
                // on left side
                leftSide.add(possibleValues.get(i));
                if (d > distance) {
                    distance = d;
                    topLeft = i;
                }
            }
        }


        if (topLeft != -1) {
            findValues(possibleValues, topLeft, p2);
            envelope.add(topLeft);
            findValues(possibleValues, p1, topLeft);

        } else {
            envelope.append(onLine);
        }

    }

    
    class InitWorker implements Runnable {
        final int id;
        final int start;
        final int end;
    
        InitWorker(int id) {
            this.id = id;

            int partitionSize = x.length / threads;

            start = partitionSize * id;
            end = (id != threads - 1) ? partitionSize * (1 + id) : x.length;
        }
        

        @Override
        public void run() {
            findLocalMinMax();

            try {
                threadBarrier.await();
            } catch (Exception e) {
            }

            if (id == 0) {
                setGlobalMinMax();
            }

            try {
                threadBarrier.await();
            } catch (Exception e) {
            }

            findLocalPoints();
            
            try {
                threadBarrier.await();
            } catch (Exception e) {
            }

            int p1 = MIN_X;
            int p2 = MAX_X;

            setPoints(p1, p2);

            try {
                mainBarrier.await();
            } catch (Exception e) {
            }
        }



        private void setPoints(int p1, int p2) {
            int[] abc = lineEquation(p1, p2);

            int a = abc[0];
            int b = abc[1];
            int c = abc[2];

            // if still negative, val was never set;
            int topLeft = -1;
            int topDistance = 0;

            int botRight = -1;
            int botDistance = 0;


            // values for iteration
            int pos;
            int d, x, y;

            IntList onLine = new IntList();
            IntList leftSide = new IntList();
            IntList rightSide = new IntList();


            // System.out.println("Left of " + p1 + " --> " + p2);
            for (int i = 0; i < all.len; i++) {
                // find values on the
                pos = all.get(i);

                x = ParaGraph.this.x[pos];
                y = ParaGraph.this.y[pos];

                d = a * x + b * y + c;

                if (d == 0 && i != p1 && i != p2)
                    onLine.add(i);

                else if (d > 0) {
                    // on left side
                    leftSide.add(all.get(i));
                    if (d > topDistance) {
                        topDistance = d;
                        topLeft = i;
                    }
                } else {
                    rightSide.add(all.get(i));
                    if (d > botDistance) {
                        botDistance = d;
                        botRight = i;
                    }
                }

                onLines[id] = onLine;
                leftSides[id] = leftSide;
                rightSides[id] = rightSide;

                if (topLeft != -1)
                    localLeft[id] = topLeft;
                
                if (botRight != -1)
                    localRight[id] = botRight;
            }



        }

        private void findLocalPoints() {
        }



        private void findLocalMinMax() {
            int min = x[start];
            int max = x[start];

            for (int i = start; i < end; i++) {
                if (x[min] > x[i])
                    min = i;
                else if (x[max] < x[i])
                    max = i;
            }

            local_MIN_X[id] = min;
            local_MAX_X[id] = max;
        }

        private void setGlobalMinMax() {
            int min = local_MIN_X[0];
            int max = local_MAX_X[0];

            for (int i = 0; i < threads; i++) {
                if (x[local_MIN_X[i]] < x[min])
                    min = local_MIN_X[i];
                
                else if(x[max] < x[local_MAX_X[i]])
                    max = local_MAX_X[i];
            }

            MIN_X = min;
            MAX_X = max;
        
        }
    }

}
