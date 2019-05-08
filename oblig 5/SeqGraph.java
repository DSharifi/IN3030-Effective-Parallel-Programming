class SeqGraph {
    int[] x, y;

    Oblig5 graph;

    int j = 0;


    // index of min and max X
    int MIN_X;
    int MAX_X;
    

    // all values;
    IntList all;

    // path of envelope
    IntList envelope;
    
    private SeqGraph(int[] x, int[] y, IntList all) {
        this.x = x;
        this.y = y;

        this.envelope = new IntList();
        this.graph = new Oblig5(x, y, envelope);
        this.all = all;
    }

    public static Oblig5 findConvexEnvelope(int[] x, int[] y, IntList all) {
        SeqGraph seqSolver = new SeqGraph(x, y, all);
        seqSolver.start();

        seqSolver.graph.MAX_X = seqSolver.MAX_X;

        return seqSolver.graph;
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
        setMaxAndMinX();

        // initialize with values left and right from p1 to p2;
        envelope.add(MAX_X);
        findValues(all, MIN_X, MAX_X);
        // findValues(all, MAX_X, MIN_X);

        
    }

    private void findValues(IntList possibleValues, int point, int startPoint) {
        int[] abc = lineEquation(point, startPoint);

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

        for (int i = 0; i < possibleValues.len; i++) {
        // find values on the     
            pos = possibleValues.get(i);
            x = this.x[pos];
            y = this.y[pos];

            d = a * x + b * y + c;
            

            if (j == 0 && d < 0) {
                System.out.println(i);
            } 

            if (d == 0)
                onLine.add(i);

            else if (d > 0) {
                // on left side
                leftSide.add(i);
                if (d > distance) {
                    distance = d;
                    topLeft = i;
                }
            }
        }

        j++;
        if (topLeft != -1) {
            findValues(leftSide, topLeft, startPoint);
            envelope.add(topLeft);

        } else {
            envelope.append(onLine);
        }

    }



    // private void findValues(int a, int b, int c, IntList possibleValues, IntList stack) {
    //     // if still negative, val was never set;
    //     topLeft = -1;
    //     botRight = -1;

    //     int d, x, y;

    //     // left and right distances;
    //     int rD = 0;
    //     int lD = 0;

    //     for (int i = 0; i < this.x.length; i++) {
    //         x = this.x[i];
    //         y = this.y[i];

    //         // relative distance
    //         d = a * x + b * y + c;

    //         if (d == 0)
    //             onLine.add(i);

    //         else if (d < 0) {
    //             // on left side
    //             leftSide.add(i);
    //             if (d < lD) {
    //                 lD = d;
    //                 topLeft = i;

    //             }

    //         } else {
    //             // on right side
    //             rightSide.add(i);
    //             if (d > rD) {
    //                 rD = d;
    //                 botRight = i;
    //             }
    //         }

    //     }


    //     if (topLeft != -1) {
    //         envelope.add(topLeft);
    //         findValues(a, b, c, leftSide);
    //     } else {
    //         envelope.append(onLine);
    //     }

    //     envelope.add(MIN_X);

    //     if (botRight != -1) {
    //         envelope.add(MIN_X);
    //         // reverse the line
    //         findValues(-a, -b, -c, rightSide);
    //     } else {
    //         envelope.append(onLine);
    //     }



    // }


    // private void initialize(int a, int b, int c) {
    //     // if still negative, val was never set;
    //     topLeft = -1;
    //     botRight = -1;

    //     int d, x ,y;

    //     // left and right distances;
    //     int rD = 0;
    //     int lD = 0;

    //     for (int i = 0; i < this.x.length; i++) {
    //         x = this.x[i];
    //         y = this.y[i];

    //         d = a*x+b*y+c;

    //         if (d == 0)
    //             onLine.add(i);

    //         else if (d < 0) { 
    //             // on left side
    //             leftSide.add(i);
    //             if (d < lD) {
    //                 lD = d;
    //                 topLeft = i;

    //             }

    //         } else {
    //             // on right side
    //             rightSide.add(i);
    //             if (d > rD) {
    //                 rD = d;
    //                 botRight = i;
    //             }
    //         }

    //     }
    // }


    private void setMaxAndMinX() {
        MIN_X = x[0];
        MAX_X = x[0];

        for (int i = 0; i < x.length; i++) {
            if (x[MIN_X] > x[i])
                MIN_X = i;
            else if (x[MAX_X] < x[i])
                MAX_X = i;
        }
    }

}
