import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

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
            findValues(leftSide, topLeft, p2);
            envelope.add(topLeft);
            findValues(leftSide, p1, topLeft);

        } else {
            // TODO: Sort the points on outer line
            // sortLine(onLine, p2);
            envelope.append(onLine);
        }

    }



    // private void sortLine(IntList line, int p2) {
    //     int[] values = line.data;
    //     Arrays.sort(values, 0, line.size(), ((int i, int j) -> (distanceToPoint(i, p2) - distanceToPoint(j, p2))));
    // }

    // private void test() {

    //     Integer[] months = {new Integer(1)};



    //     Arrays.sort(months, 0, 1, (int a, int b) -> a - b);

    // }

    private int distanceToPoint(int p1, int p2) {
        return (int) (Math.pow(x[p1] - x[p2], 2) + Math.pow(y[p1] - y[p2], 2));
    }


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
