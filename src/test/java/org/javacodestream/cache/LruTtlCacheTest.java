package org.javacodestream.cache;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class LruTtlCacheTest {

    @Test
    public void testAddRemoveCacheElements() {

        // Test with default TimeToLive = 3 mins
        // evictTimerInterval = 3 seconds
        // maxItems = 6

        LruTtlCache<String, String> cache = new LruTtlCache(6);
        cache.put("eBay Key", "eBay Value");
        cache.put("Paypal Key", "Paypal Value");
        cache.put("Google Key", "Google Value");
        cache.put("Microsoft Key", "Microsoft Value");
        cache.put("IBM Key", "IBM Value");
        cache.put("Facebook Key", "Facebook Value");

        System.out.println("6 Cache Object Added.. cache.size(): " + cache.size());
        assertThat(6, equalTo(cache.size()));
        cache.remove("IBM Key");
        System.out.println("One object removed.. cache.size(): " + cache.size());
        assertThat(5, equalTo(cache.size()));

        cache.put("Twitter Key", "Twitter Value");
        cache.put("Goldman Key", "Goldman Value");
        cache.put("Facebook Key", "Facebook Value2");
        System.out.println("2 new object Added, 1 overridden, but reached maxItems.. cache.size(): " + cache.size());
        assertThat(6, equalTo(cache.size()));
        assertNull(cache.get("eBay Key"));
        assertThat("Facebook Value2", equalTo(cache.get("Facebook Key")));
    }

    @Test
    public void testExpiredCacheElements() throws InterruptedException {
        // Test with TimeToLive = 1 second
        // evictTimerInterval = 1 second
        // maxItems = 10

        LruTtlCache<String, String> cache = new LruTtlCache(1 * 1000, 1 * 1000, 6);
        cache.put("eBay Key", "eBay Value");
        cache.put("Paypal Key", "Paypal Value");
        // Adding 3 seconds sleep.. Both above objects will be removed from
        // Cache because of TimeToLive value
        assertThat(2, equalTo(cache.size()));
        System.out.println("Sleeping for next 5 secs......");
        Thread.sleep(5000);
        System.out.println("Woke Up...Now Lets see what's there is the cache.. ;)....");
        System.out.println("Two objects are added but reached timeToLive. cache.size(): " + cache.size());
        assertThat(0, equalTo(cache.size()));
    }

    @Test
    public void testCacheElementsCleanupTime() throws InterruptedException {
        // Test with timeToLive = 100 seconds
        // timerInterval = 100 seconds
        // maxItems = 500000
        int size = 500000;

        LruTtlCache<String, String> cache = new LruTtlCache(100 * 1000, 100 * 1000, 500000);

        for (int i = 0; i < size; i++) {
            String value = Integer.toString(i);
            cache.put(value, value);
        }

        System.out.println("Sleeping for next 2 secs......");
        Thread.sleep(2000);

        long start = System.currentTimeMillis();
        System.out.println("Staring cleanup....");
        cache.cleanup();
        System.out.println("Completed cleanup....");
        double finish = (double) (System.currentTimeMillis() - start) / 1000.0;

        System.out.println("Cleanup times for " + size + " objects are " + finish + " s");
    }
}
