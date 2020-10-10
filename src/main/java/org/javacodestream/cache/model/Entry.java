package org.javacodestream.cache.model;

public class Entry<K,V> {
    private K key;
    private V value;

    private Entry<K,V> left;
    private Entry<K,V> right;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Entry<K, V> getLeft() {
        return left;
    }

    public void setLeft(Entry<K, V> left) {
        this.left = left;
    }

    public Entry<K, V> getRight() {
        return right;
    }

    public void setRight(Entry<K, V> right) {
        this.right = right;
    }

}
