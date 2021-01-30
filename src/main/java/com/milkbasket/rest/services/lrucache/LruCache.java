package com.milkbasket.rest.services.lrucache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sonamrathore
 *
 */
public class LruCache<K, V> implements Cache<K, V> {

	private int capacity;
	private ConcurrentHashMap<K, Node<K, V>> map;
	private Node<K, V> head, last;
	private ExpireKeys<K, V> expireKeysAlgo;

	public LruCache(int capacity) {
		this.capacity = capacity;
		map = new ConcurrentHashMap<K, Node<K,V>>();
		head = new Node<K, V>();
		last = head;
		expireKeysAlgo = new ExpireKeysLazyAlgo<K, V>();
	}

	public void put(K key, V value, long ttlMillis) {
		if (map.size() == capacity) {
			map.remove(head.key);
			head = head.nextNode;
		}
		final long expiryTime = System.currentTimeMillis() + ttlMillis;
		final Node<K, V> node = new Node<K, V>(key, value, expiryTime);
		map.put(key, node);
		last.nextNode = node;
		node.prevNode = last;
		last = last.nextNode;

		expireKeysAlgo.add(node);
		callExpire();

	}

	public V get(K key) {
		if (map.isEmpty()) {
			return null;
		}
		final Node<K, V> node = map.get(key);
		callExpire();

		if (node == null || node.ttl <= System.currentTimeMillis()) {
			return null;
		}
		if (node.prevNode != null) {
			node.prevNode.nextNode = node.nextNode;
		}
		if (node.nextNode != null) {
			node.nextNode.prevNode = node.prevNode;
		}
		node.nextNode = null;
		node.prevNode = last.prevNode;
		last = node;
		return node != null ? node.value : null;
	}

	private void callExpire() {
		new Thread(new Runnable() {
			public void run() {
				expireKeysAlgo.expire(map);
			}
		}).start();
	}

	public void remove(K key) {
		if (map.containsKey(key)) {
			final Node<K, V> node = map.get(key);
			map.remove(key);
			if (node.prevNode != null) {
				node.prevNode.nextNode = node.nextNode;
			}

			if (node.nextNode != null) {
				node.nextNode.prevNode = node.prevNode;
			}
		}
		new Thread(new Runnable() {
			public void run() {
				expireKeysAlgo.expire(map);
			}
		}).start();
	}

}

class Node<K, V> {
	K key;
	V value;
	long ttl;
	Node<K, V> prevNode;
	Node<K, V> nextNode;

	/**
	 *
	 */
	public Node(K key, V value, long ttl) {
		this.value = value;
		this.prevNode = null;
		this.nextNode = null;
		this.ttl = ttl;
		this.key = key;
	}

	public Node() {
	}
}
