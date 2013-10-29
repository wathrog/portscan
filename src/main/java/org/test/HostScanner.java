package org.test;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.net.util.SubnetUtils;

public class HostScanner {
	
	private final String CIDRString;
	private final int timeout;
	
	public HostScanner(String CIDRString, int timeout) {
		this.CIDRString = CIDRString;
		this.timeout = timeout;
	}
	
	public Collection<String> getActiveHosts() throws Exception {
		return new Callable<Collection<String>>() {

			@Override
			public Collection<String> call() throws Exception {
				String [] addrList = new SubnetUtils(CIDRString).getInfo().getAllAddresses();
				
				ExecutorService ex = Executors.newFixedThreadPool(10);
				Vector<Future<String>> tasks = new Vector<>();
				for (String addr : addrList) {
					Callable<String> task = new Callable<String>() {
						
						private String address;

						@Override
						public String call() throws Exception {
							InetAddress iaddr = InetAddress.getByName(address);
							if (iaddr.isReachable(timeout)) {
								return address;
							} else {
								return null;
							}
						}
						
						public Callable<String> init(String addr) {
							address = addr;
							return this;
						}
					}.init(addr);
					tasks.add(ex.submit(task));
				}
				ex.shutdown();
				
				Collection<String> retVal = new ArrayList<>();
				
				for (Future<String> ft : tasks) {
					String a = ft.get();
					if (a != null) {
						retVal.add(a);
					}
				}
				return retVal;
				
			}
			
		}.call();
	}

}
