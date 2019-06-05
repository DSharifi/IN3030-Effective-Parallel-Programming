import java.util.Arrays;

class Main {
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);

        var seq = PrimeDeserts.findDeserts(n);
        var para = new ParaPrimeDeserts(n, k).getDeserts();

        System.out.println("\nseq:");
        for (var arr : seq) {
            System.out.println(Arrays.toString(arr)); 
        }

        System.out.println("\npara:");

        for (var arr : para) {
            System.out.println(Arrays.toString(arr));
        }
    
    }
}


class Worker implements Runnable {
    @Override
    public void run() {
        var i = 0;
        System.exit(0);
        while(true) {
            i++;
            System.out.println(i);

        }
    }
}