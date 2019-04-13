import java.util.function.Consumer;
import java.util.Arrays;
/**
 * Timer
 */
public class Timer {

    public static double timeOnce(int n, Consumer<Integer> c) {
        long start, end; 
        start = System.nanoTime();
        c.accept(n);
        end =  System.nanoTime();
        return (end-start) / 1000000.0;
    }

    public static double jitOptimalAverageTime(int n, Consumer<Integer> c){
        double[] timeArray = new double[7];

        long start, end; 
        for (int i = 0; i < 7; i++) {
            start = System.nanoTime();
            c.accept(n);
            end =  System.nanoTime();
            timeArray[i] = (end-start) / 1000000.0;
        }
        
        Arrays.sort(timeArray);
        return timeArray[3];
    }
    
    public static void main(String[] args) {
        int n = 1000;
        double execTime = timeOnce(n, numb -> test(numb));
        double jitOptimalExecTime = jitOptimalAverageTime(n, numb -> test(numb));
        System.out.printf("Once: %.2f ms\n", execTime);
        System.out.printf("JIT optimal average: %.2f ms\n", jitOptimalExecTime);
    }

    private static void test(int n) {
        for (int i = 0; i < n; i++) {
            System.out.println(n);
        }
    }
}