package com.milkbasket.rest.services.lrucache;

/**
 * Hello world!
 *
 */
public class App
{
	public static void main(String[] args) throws InterruptedException
	{
		final Cache<String, String> cache = new LruCache<String, String>(5);
		cache.put("sonam", "rathore", 10000);
		System.out.println("Value:" + cache.get("sonam"));
		Thread.sleep(20000);
		System.out.println("Value:" + cache.get("sonam"));

	}
}
