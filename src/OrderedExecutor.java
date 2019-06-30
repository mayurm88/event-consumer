import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class OrderedExecutor<K> {

    private final ExecutorService[] es;
    private final int n;

    OrderedExecutor(int n) {
        this.es = new ExecutorService[n];
        this.n = n;
        for (int i = 0; i < n; i++) {
            es[i] = Executors.newSingleThreadExecutor();
        }
    }

    void execute (K key, Runnable r){
        es[Math.abs(key.hashCode()%n)].submit(r);
    }

    void shutdown() {
        for (ExecutorService e : es) {
            e.shutdown();
        }
    }

    boolean awaitTermination(long i, TimeUnit seconds) {
        return Arrays.stream(es)
            .map(e -> {
                try {
                    return e.awaitTermination(i, seconds);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            })
            .reduce(true, (b1,b2) -> b1 && b2);
    }
}
