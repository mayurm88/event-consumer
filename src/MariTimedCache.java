import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

class MariTimedCache<T> {

    private long timeToKeep;
    private ConcurrentHashMap<T, MaroTimedCacheQueueNode<T>> timedCacheHashMap;
    private ConcurrentLinkedQueue<MaroTimedCacheQueueNode<T>> timedCacheQueue;

    private MariTimedCache(TimedCacheBuilder<T> tTimedCacheBuilder) {
        this.timeToKeep = tTimedCacheBuilder.timeToKeep;
        this.timedCacheQueue = new ConcurrentLinkedQueue<>();
        this.timedCacheHashMap = new ConcurrentHashMap<>();
    }


    T get(T event) {
        MaroTimedCacheQueueNode<T> queueNode = timedCacheHashMap.get(event);
        T eventToReturn;
        if(queueNode != null && System.currentTimeMillis() - queueNode.getTimeStored() < timeToKeep) {
            eventToReturn = queueNode.getValue();
        } else {
            eventToReturn = null;
        }

        MaroTimedCacheQueueNode<T> currentNode = timedCacheQueue.peek();
        while(currentNode != null) {
            if (System.currentTimeMillis() - currentNode.getTimeStored() < timeToKeep) {
                break;
            }
            timedCacheQueue.poll();
            currentNode = timedCacheQueue.peek();
        }

        return eventToReturn;
    }

    void put(T event) {
        MaroTimedCacheQueueNode<T> eventToStore = new MaroTimedCacheQueueNode<>();
         eventToStore.setValue(event)
                 .setTimeStored(System.currentTimeMillis());
         timedCacheHashMap.put(event, eventToStore);
    }

    static class TimedCacheBuilder<T> {

        long timeToKeep;

        static TimedCacheBuilder newInstance() {
            return new TimedCacheBuilder();
        }

        private TimedCacheBuilder() {}

        TimedCacheBuilder withTimeToKeepInSeconds(long timeToKeep) {
            this.timeToKeep = timeToKeep*1000;
            return this;
        }

        MariTimedCache<T> build() {
            return new MariTimedCache<>(this);
        }

    }
}
