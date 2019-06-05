import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.ArrayList;

class Monitor {
    private ArrayList<Melding> meldinger = new ArrayList<Melding>();
    private Lock lock = new ReentrantLock();
    private Condition ikkeTomt = lock.newCondition();
    private boolean done = false;

    // trenger ikke catch, waiter ikke
    public void leggPaa(Melding melding) {
        lock.lock();
        meldinger.add(melding);
        ikkeTomt.signalAll();
        lock.unlock();
    }

    public Melding taAv() {
        lock.lock();
        try {
            while (!done && meldinger.isEmpty()) {
                ikkeTomt.await();
            }

            if (done)
                return null;

            return meldinger.remove(0);

        } catch (Exception e) {
            return null;
        }

        finally {
            lock.unlock();
        }

    }

    public void settFerdigInn() {
        lock.lock();
        done = true;
        ikkeTomt.signalAll();
        lock.unlock();
    }
}
