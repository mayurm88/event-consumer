class MaroTimedCacheQueueNode<K> {

    K getValue() {
        return value;
    }

    long getTimeStored() {
        return timeStored;
    }

    MaroTimedCacheQueueNode<K> setValue(K value) {
        this.value = value;
        return this;
    }

    MaroTimedCacheQueueNode<K> setTimeStored(long timeStored) {
        this.timeStored = timeStored;
        return this;
    }

    private K value;
    private long timeStored;
}
