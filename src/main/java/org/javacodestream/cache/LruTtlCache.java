package org.javacodestream.cache;

import org.javacodestream.cache.model.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * reference: https://crunchify.com/how-to-create-a-simple-in-memory-cache-in-java-lightweight-cache/
 *
 */
public class LruTtlCache<K,V> {

    private static final Logger logger = LoggerFactory.getLogger(LruTtlCache.class);

    private static long timeToLiveInMillis = 3 * 60 * 1000; // TTL default is 3 min
    private static long evictTimerIntervalInMillis = 3 * 1000; // evict timer default is 3 sec

    private LRUMap lruMap;

    protected class CacheObject {
        public long lastAccessed = System.currentTimeMillis();
        public V value;

        public CacheObject(V value) {
            this.value = value;
        }
    }

    public LruTtlCache(int cacheSize) {
        this(timeToLiveInMillis, evictTimerIntervalInMillis, cacheSize);
    }

    public LruTtlCache(long timeToLiveInMillis, final long evictTimerIntervalInMillis, int cacheSize) {
        logger.info("Cache Initialized :: cacheSize: {} - timeToLiveInMillis: {} - evictTimerIntervalInMillis: {}",
                cacheSize, timeToLiveInMillis, evictTimerIntervalInMillis);
        this.timeToLiveInMillis = timeToLiveInMillis;
        this.lruMap = new LRUMap<>(cacheSize);
        if (timeToLiveInMillis > 0 && evictTimerIntervalInMillis > 0) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(evictTimerIntervalInMillis);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        cleanup();
                    }
                }
            });
            t.setDaemon(true); // run the eviction thread in background
            t.start();
        }
    }

    public void put(K key, V value) {
        synchronized (this.lruMap) {
            lruMap.putEntry(key, new CacheObject(value));
        }
    }

    public V get(K key) {
        synchronized (this.lruMap) {
            CacheObject c = (CacheObject) this.lruMap.getEntry(key);
            if (Objects.isNull(c)) {
                return null;
            }
            else {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            }
        }
    }

    public int size() {
        return this.lruMap.size();
    }

    public void remove(K key) {
        this.lruMap.removeEntryByKey(key);
    }

    protected void cleanup() {
        logger.info("Checking if there are any element(s) to be cleaned-up...");
        long now = System.currentTimeMillis();
        ArrayList<K> toBeDeletedKeys = null;

        synchronized (this.lruMap) {
            toBeDeletedKeys = new ArrayList<>((this.lruMap.size() / 2) + 1);
            Iterator<Map.Entry<K, org.javacodestream.cache.model.Entry<K,V>>> itr = this.lruMap.mapIterator();

            while (itr.hasNext()) {
                Map.Entry<K, org.javacodestream.cache.model.Entry<K,V>> element = itr.next();
                K toBeDeletedKey = element.getKey();
                CacheObject c = (CacheObject) element.getValue().getValue();
                if (! Objects.isNull(c) && now > timeToLiveInMillis + c.lastAccessed) {
                    toBeDeletedKeys.add(toBeDeletedKey);
                }
            }
        }

        for (K key : toBeDeletedKeys) {
            synchronized (this.lruMap) {
                logger.info("Evicting Element due to TTL expiry: {}", key);
                this.lruMap.removeEntryByKey(key);
            }
            Thread.yield();
        }
    }
}
