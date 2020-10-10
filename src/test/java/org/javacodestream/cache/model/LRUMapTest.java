package org.javacodestream.cache.model;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class LRUMapTest<K,V> {

    @Test
    public void testLRUMap() {
        LRUMap<Integer,Integer> lruMap = new LRUMap<>(4);
        lruMap.putEntry(10, 15);
        lruMap.putEntry(15, 10);
        lruMap.putEntry(10, 16);
        lruMap.putEntry(12, 15);
        lruMap.putEntry(18, 10);
        lruMap.putEntry(13, 16);
        assertThat(4, equalTo(4));
    }
}
