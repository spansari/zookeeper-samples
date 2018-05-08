package com.sanjiv.samples.zkutils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;

public enum ServiceRegistryUtil {
    instance;

    private static final String ZK_CONNECT_STRING = "localhost:2181";
    private static final String BASE_PATH = "service-registry";
    
    private static CuratorFramework curatorFramework = null;
    
    private ServiceRegistryUtil() {
	init();
    }
    private void init() {
	curatorFramework = CuratorFrameworkFactory.newClient(ZK_CONNECT_STRING, new RetryNTimes(5, 1000));
	curatorFramework.start();
    }

    public void register(String serviceName, String serviceHost, int servicePort) {
	ServiceInstance<Object> serviceInstance;
	try {
	    serviceInstance = ServiceInstance.<Object>builder()
		.uriSpec(new UriSpec("{scheme}://{address}:{port}"))
		.address(serviceHost)
		.port(servicePort)
		.name(serviceName)
		.build();

	    ServiceDiscoveryBuilder.builder(Object.class)
	    	.basePath(BASE_PATH)
	    	.client(curatorFramework)
		.thisInstance(serviceInstance)
		.build()
		.start();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
  
}
