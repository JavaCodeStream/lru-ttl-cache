package org.javacodestream.cache.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LRUMap<K,V> {
    private int MAX_SIZE;
    private HashMap<K, Entry<K,V>> hashMap;

    private Entry<K,V> head;
    private Entry<K,V> tail;

    public LRUMap(int size) {
        this.MAX_SIZE = size;
        hashMap = new HashMap<>(size);
    }

    public void addAtTop(Entry<K, V> node) {
        node.setRight(head);
        node.setLeft(null);

        if (head != null)
            head.setLeft(node);
        head = node;
        if (tail == null)
            tail = head;
    }

    public void removeNode(Entry<K, V> node) {
        if (node.getLeft() != null) {
            node.getLeft().setRight(node.getRight());
        } else {
            // the first node in doubly linked list
            head = node.getRight();
        }

        if (node.getRight() != null) {
            node.getRight().setLeft(node.getLeft());
        } else {
            // the last node in doubly linked list
            tail = node.getLeft();
        }
    }

    public V removeEntryByKey(K key) {
        if (hashMap.containsKey(key)) {
            Entry<K,V> value = hashMap.get(key);
            removeNode(value);
            Entry<K,V> valueToBeRemoved = hashMap.remove(key);
            return valueToBeRemoved.getValue();
        }
        return null;
    }

    public Iterator<Map.Entry<K, Entry<K,V>>> mapIterator() {
        return this.hashMap.entrySet().iterator();
    }

    public V getEntry(K key) {
        if (hashMap.containsKey(key)) // Key Already Exist, just update the
        {
            Entry<K, V> entry = hashMap.get(key);
            return entry.getValue();
        }
        return null;
    }

    public void putEntry(K key, V value) {
        if (hashMap.containsKey(key)) { // Key Already Exist, just update the value and move it to top
            //update the value in the hashmap
            Entry<K, V> entry = hashMap.get(key);
            entry.setValue(value);

            // move the value(node) to the top of the doubly linked list
            removeNode(entry);
            addAtTop(entry);
        } else {
            Entry<K, V> newnode = new Entry<>(key, value);
            newnode.setLeft(null);
            newnode.setRight(null);
            newnode.setValue(value);
            newnode.setKey(key);

            if (hashMap.size() >= MAX_SIZE) { // We have reached maximum size so need to make room for new element.
                hashMap.remove(tail.getKey());
                removeNode(tail);
            }
            addAtTop(newnode);

            hashMap.put(key, newnode);
        }
    }

    public int size() {
        return this.hashMap.size();
    }
}
