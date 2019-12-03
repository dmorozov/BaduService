package com.badu.server.core.utils;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ImmutableMultivaluedMap <K, V> implements MultivaluedMap<K, V> {

    /**
     * Returns an empty immutable map.
     *
     * @return an empty immutable map.
     */
    public static <K, V> ImmutableMultivaluedMap<K, V> empty() {
        return new ImmutableMultivaluedMap<K, V>(new MultivaluedHashMap<K, V>());
    }

    private final MultivaluedMap<K, V> delegate;

    /**
     * Creates a new ImmutableMultivaluedMap.
     *
     * @param delegate the underlying MultivaluedMap
     */
    public ImmutableMultivaluedMap(final MultivaluedMap<K, V> delegate) {
        if (delegate == null) {
            throw new NullPointerException("ImmutableMultivaluedMap delegate must not be 'null'.");
        }
        this.delegate = delegate;
    }

    @Override
    public boolean equalsIgnoreValueOrder(final MultivaluedMap<K, V> otherMap) {
        return delegate.equalsIgnoreValueOrder(otherMap);
    }

    @Override
    public void putSingle(final K key, final V value) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }

    @Override
    public void add(final K key, final V value) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }

    @Override
    public V getFirst(final K key) {
        return delegate.getFirst(key);
    }

    @Override
    public void addAll(final K key, final V... newValues) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }

    @Override
    public void addAll(final K key, final List<V> valueList) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }

    @Override
    public void addFirst(final K key, final V value) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public List<V> get(final Object key) {
        return delegate.get(key);
    }

    @Override
    public List<V> put(final K key, final List<V> value) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }

    @Override
    public List<V> remove(final Object key) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }

    @Override
    public void putAll(final Map<? extends K, ? extends List<V>> m) {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("This MultivaluedMap implementation is immutable.");
    }

    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(delegate.keySet());
    }

    @Override
    public Collection<List<V>> values() {
        return Collections.unmodifiableCollection(delegate.values());
    }

    @Override
    public Set<Entry<K, List<V>>> entrySet() {
        return Collections.unmodifiableSet(delegate.entrySet());
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImmutableMultivaluedMap)) {
            return false;
        }

        final ImmutableMultivaluedMap that = (ImmutableMultivaluedMap) o;

        if (!delegate.equals(that.delegate)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}