package com.milkbasket.rest.services.lrucache;

import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sonamrathore
 *
 */
public class ExpireKeysLazyAlgo<K, V> implements ExpireKeys<K, V> {

	PriorityQueue<Node<K, V>> queue;

	public ExpireKeysLazyAlgo() {
		queue = new PriorityQueue<Node<K, V>>();
	}

	public void expire(ConcurrentHashMap<K, Node<K, V>> map) {
		while (!queue.isEmpty() && queue.peek().ttl <= System.currentTimeMillis()) {
			final Node<K,V> node = queue.poll();
			if (node != null && map != null && map.containsKey(node.key)) {
				System.out.println("Expired: " + node.key);
				map.remove(node.key);
				if (node.prevNode != null) {
					node.prevNode.nextNode = node.nextNode;
				}

				if (node.nextNode != null) {
					node.nextNode.prevNode = node.prevNode;
				}
			}
		}

	}

	public void add(Node<K, V> node) {
		queue.add(node);
	}

}
