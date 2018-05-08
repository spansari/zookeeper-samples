package com.sanjiv.samples.zkutils;

import java.util.HashMap;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceProvider;

public enum ServiceProviderUtil {
    instance;

    private static final String ZK_CONNECT_STRING = "localhost:2181";
    private static final String BASE_PATH = "service-registry";
    
    
    private static CuratorFramework curatorFramework = null;

    private static ServiceDiscovery<Object> serviceDiscovery = null;

    private static Map<String, ServiceProvider> providers = new HashMap();

    private void ServiceProviderUtil() {
	init();
    }
    
    private void init() {
	curatorFramework = CuratorFrameworkFactory.newClient(ZK_CONNECT_STRING, new RetryNTimes(5, 1000));
	curatorFramework.start();
	serviceDiscovery = ServiceDiscoveryBuilder.builder(Object.class).client(curatorFramework).basePath(BASE_PATH)
		.build();
	try {
	    serviceDiscovery.start();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public String getServiceAddress(String serviceName) {
	String address = "";
	try {

	    ServiceProvider serviceProvider = providers.get(serviceName);

	    if (null == serviceProvider) {
		serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName(serviceName).build();
		serviceProvider.start();
		providers.put(serviceName, serviceProvider);
	    }

	    address = serviceProvider.getInstance().buildUriSpec();

	} catch (Exception e) {
	    e.printStackTrace();
	}

	return address;
    }

}
