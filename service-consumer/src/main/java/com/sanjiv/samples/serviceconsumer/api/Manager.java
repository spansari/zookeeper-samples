package com.sanjiv.samples.serviceconsumer.api;

import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.strategies.RandomStrategy;

@Path("/api")
public class Manager implements Serializable {
	
	@GET
    @Path("/delegate")
    public String delegate() {
		
		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("localhost:2181", new RetryNTimes(5, 1000));
        curatorFramework.start();
        
        String response = "";
        
		try {
			
			ServiceDiscovery<Object> serviceDiscovery = ServiceDiscoveryBuilder.builder(Object.class).client(curatorFramework).basePath("service-registry").build();
			serviceDiscovery.start();

			ServiceProvider serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName("worker").providerStrategy(new RandomStrategy<Object>()).build();
			serviceProvider.start();
			
			ServiceInstance instance = serviceProvider.getInstance();
			String address = instance.buildUriSpec();
			System.out.println(address);
			Client client = ClientBuilder.newClient();
			
			response = client.target(address+ "/api/work").request().get().toString();
			
			System.out.println(response);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return response;
    }
	
}
