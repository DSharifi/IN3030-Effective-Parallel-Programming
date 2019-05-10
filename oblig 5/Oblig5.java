class Oblig5 {
    IntList envelope;

    int MAX_X, MAX_Y;

    int[] x, y;
    int n;


    public Oblig5(int[] x, int[] y, IntList envelope) {
        this.x = x;
        this.y = y;

        n = x.length;

        this.envelope = envelope;
    }
    
    
    @Override
    public String toString() {
        String output = "";

        for (int i = 0; i < envelope.size(); i++) {
            output += envelope.get(i);
            output += " --> ";
        }

        output += envelope.get(0);

        return output;
    }
}