package com.milkbasket.rest.services.lrucache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sonamrathore
 *
 */
public interface ExpireKeys<K, V> {
	void expire(ConcurrentHashMap<K, Node<K, V>> map);

	void add(Node<K, V> node);
}
