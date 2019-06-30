import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MaroConsumer implements Consumer<Event> {

    private static final int N = 1000;
    private static OrderedExecutor<Integer> es;
    private static ArrayList<Event> duplicateList;
    private static MariTimedCache<Event> mariTimedCache;

    private MaroConsumer() {
        es = new OrderedExecutor<>(10);
        duplicateList = new ArrayList<>();
        mariTimedCache = (MariTimedCache<Event>) MariTimedCache.TimedCacheBuilder.newInstance()
                .withTimeToKeepInSeconds(10)
                .build();
    }


    @Override
    public void accept(Event event) {
        if(mariTimedCache.get(event) != null) {
            System.out.println("Not processing duplicate event: " + event);
            return;
        }
        es.execute(event.user, () -> Util.process(event));
        mariTimedCache.put(event);
    }


    public static void main(String[] args) throws InterruptedException {
        long now = System.currentTimeMillis();
        Random r = new Random();
        MaroConsumer mc = new MaroConsumer();
        int id = 0;
        for (int i = 0; i < N; i++) {
            Event e = new Event(id++, r.nextInt(10));
            if(r.nextInt(100) < 10) {
                duplicateList.add(e);
                System.out.println("Adding event with id: " + e.id + " to duplicate list at time: " + (System.currentTimeMillis()/1000));
            }
            mc.accept(e);
        }

        for (Event event : duplicateList) {
            mc.accept(event);
        }

        es.shutdown();
        while (!es.awaitTermination(100, TimeUnit.SECONDS))  {}
        System.out.println("Time taken : " + (System.currentTimeMillis() - now));
    }
}
