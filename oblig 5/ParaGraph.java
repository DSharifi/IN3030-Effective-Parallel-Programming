import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

class ParaGraph {
    final int[] x, y;
    final Oblig5 graph;
    
    final private int threads;

    final private CyclicBarrier threadBarrier;
    final private CyclicBarrier mainBarrier;

    final private CyclicBarrier finalBarrier = new CyclicBarrier(2);

    // indices. Used to make a rectangle
    int MIN_X;
    int MAX_X;
    int topLeft;
    int topRight;

    final int[] local_MIN_X;
    final int[] local_MAX_X;

    final int[] localLeft;
    final int[] leftSideDistances;
    
    final int[] localRight;
    final int[] rightSideDistances;



    // all values;
    final IntList all;


    // ---
    final IntList leftSide = new IntList();
    final IntList rightSide = new IntList();
    final IntList onLine = new IntList();

    final AtomicInteger activeThreads;

    final IntList[] leftSides;
    final IntList[] rightSides;
    final IntList[] onLines;
    
    // path of envelope
    final IntList envelope;
    
    final IntList leftEnvelope = new IntList();
    final IntList rightEnvelope = new IntList();
    
    private ParaGraph(int[] x, int[] y, IntList all, int threads) {
        this.x = x;
        this.y = y;

        if (threads < 4) 
            this.threads = 4;
        else
            this.threads = threads;

        this.envelope = new IntList();
        this.graph = new Oblig5(x, y, envelope);
        this.all = all;


        this.threadBarrier = new CyclicBarrier(this.threads);
        this.mainBarrier = new CyclicBarrier(this.threads + 1);      

        this.leftSides = new IntList[this.threads];
        this.rightSides = new IntList[this.threads];
        this.onLines = new IntList[this.threads];


        this.localLeft = new int[this.threads];
        this.leftSideDistances = new int[this.threads];
        this.localRight = new int[this.threads];
        this.rightSideDistances = new int[this.threads];
        
        this.local_MIN_X = new int[this.threads];
        this.local_MAX_X = new int[this.threads];

        activeThreads = new AtomicInteger(4);
    }

    public static Oblig5 findConvexEnvelope(int[] x, int[] y, IntList all, int threads) {
        ParaGraph paraSolver = new ParaGraph(x, y, all, threads);
        paraSolver.start();

        paraSolver.graph.MAX_X = paraSolver.MAX_X;

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

        // System.out.println("starting intier");

        for (int i = 0; i < threads; i++) {
            new Thread(new InitWorker(i)).start();
        }

        try {
            mainBarrier.await();
        } catch (Exception e) {
            // TODO: handle exception
        }

        Tree tree1 = new Tree(topLeft);
        Tree tree2 = new Tree(topRight);

        Thread thread1 = new Thread(new Worker(topLeft, MAX_X, leftSide, tree1, "r"));
        Thread thread2 = new Thread(new Worker(MIN_X, topLeft, leftSide, tree1, "l"));
        Thread thread3 = new Thread(new Worker(topRight, MIN_X, rightSide, tree2, "r"));
        Thread thread4 = new Thread(new Worker(MAX_X, topRight, rightSide, tree2, "l"));

        // System.out.println("starting runners");

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();

        try {
            finalBarrier.await();
        } catch (Exception e) {
            // TODO: handle exception
        }

        // System.out.println("printing");

        // System.out.println("top tree");
        // System.out.println(MAX_X);
        // tree1.printContent();
        // System.out.println("\nbot tree");
        // System.out.println(MIN_X);
        // tree2.printContent();

        IntList topEnvelope = tree1.toIntList();
        IntList botEnvelope = tree2.toIntList();

        envelope.add(MAX_X);
        envelope.append(topEnvelope);
        envelope.add(MIN_X);
        envelope.append(botEnvelope);

        // System.out.println("Top:\n" + topEnvelope);
        // System.out.println("Bot:\n" + botEnvelope);
        // System.out.println(envelope);

    }


    private Integer[] copy(IntList src) {
        Integer[] val = new Integer[src.size()];

        for (int i = 0; i < src.size(); i++) {
            val[i] = src.data[i];
        }

        return val;
    }

    private void sortLine(IntList line, int p) {
        Integer[] val = copy(line);
        Arrays.sort(val, 0, line.size(), ((Integer i,
                Integer j) -> (relativeDistanceBetweenPoints(i, p) - relativeDistanceBetweenPoints(j, p))));

        for (int i = 0; i < val.length; i++) {
            line.data[i] = val[i];
        }
    }

    private int relativeDistanceBetweenPoints(int p1, int p2) {
        return (int) (Math.pow(x[p1] - x[p2], 2) + Math.pow(y[p1] - y[p2], 2));
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

            findLocalPoints(MIN_X, MAX_X);

            try {
                threadBarrier.await();
            } catch (Exception e) {
            }

            if (id < 4) {
                /**
                 * 5 tasks depending on id;
                 * 
                 * 1 merge left side intlists 
                 * 2 merge right side intlists
                 * 3 merge online intlits
                 * 4 set topLeft and botRight
                 */

                setPoints();
            }

            try {
                mainBarrier.await();
            } catch (Exception e) {
            }
        }





        private void findLocalPoints(int p1, int p2) {
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
            for (int i = start; i < end; i++) {
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
                } else if (d < 0) {
                    rightSide.add(all.get(i));
                    if (d < botDistance) {
                        botDistance = d;
                        botRight = i;
                    }
                }

            }

            rightSideDistances[id] = botDistance;
            leftSideDistances[id] = topDistance;

            onLines[id] = onLine;
            leftSides[id] = leftSide;
            rightSides[id] = rightSide;

            if (topLeft != -1)
                localLeft[id] = topLeft;
            
            if (botRight != -1)
                localRight[id] = botRight;
        }




        private void setPoints() {
            if (id == 0) {
                for (IntList left : leftSides) {
                    leftSide.append(left);      
                }
            } else if (id == 1) {
                for (IntList right : rightSides) {
                    rightSide.append(right);
                }
            } else if (id == 2) {
                for (IntList middle : onLines) {
                    onLine.append(middle);
                }
            } else {
                setGlobalRightLeft();
            }

        }

        private void setGlobalRightLeft() {
            int right = localRight[0];
            int left = localLeft[0];

            int rightMax = rightSideDistances[0];
            int leftMax = leftSideDistances[0];

            int rightDistance, leftDistance;

            for (int i = 0; i < localRight.length; i++) {
                rightDistance = rightSideDistances[i];
                leftDistance = leftSideDistances[i];

                if (leftDistance > leftMax) {
                    leftMax = leftDistance;
                    left = localLeft[i];
                }

                if (rightDistance < rightMax) {
                    rightMax = rightDistance;
                    right = localRight[i];
                }

            }

            topLeft = left;
            topRight = right;
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

    class Worker implements Runnable {
        int id;
        int p1, p2;

        IntList possibleValues;
        IntList newValues;
        String path;

        Tree localEnvelope;

        public Worker(int p1, int p2, IntList possibleValues, Tree localEnvelope, String path) {
            this.p1 = p1;
            this.p2 = p2;

            this.path = path;

            this.possibleValues = possibleValues;
            this.localEnvelope = localEnvelope;
        }

        @Override
        public void run() {
            recurse(p1, p2, possibleValues, path);
            if (activeThreads.decrementAndGet() == 0)
                try {
                    finalBarrier.await();
                } catch(Exception e){}
        }

        private void recurse(int p1, int p2, IntList possibleValues, String path) {
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

                x = ParaGraph.this.x[pos];
                y = ParaGraph.this.y[pos];

                d = a * x + b * y + c;

                if (d == 0 && pos != p1 && pos != p2)
                    onLine.add(pos);

                else if (d > 0) {
                    // on left side
                    leftSide.add(pos);
                    if (d > distance) {
                        distance = d;
                        topLeft = pos;
                    }
                }
            }

            if (topLeft != -1) {
                localEnvelope.add(topLeft, path);

                // let new thread do leftSide;
                if(threads > activeThreads.getAndIncrement()) {
                    new Thread(new Worker(p1, topLeft, leftSide, localEnvelope, path + 'l')).start();
                } else {
                    activeThreads.decrementAndGet();
                    recurse(p1, topLeft, leftSide, path + 'l');
                }
                
                // rightside
                recurse(topLeft, p2, leftSide, path+'r');

            } else {
                // sortLine(onLine, p2);
                // localEnvelope.addLine(onLine, path);
            }
        }
    }

}
