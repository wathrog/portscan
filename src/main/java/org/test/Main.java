package org.test;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println("Hello world!");
		String testSubnet = "192.168.1.0/24";
		
		HostScanner scanner = new HostScanner(testSubnet, 1000);
		
		Collection<String> hosts = scanner.getActiveHosts();
		
		for (String host : hosts) {
			System.out.println(host);
		}

	}

}
