package com.sanjiv.samples.serviceconsumer.api;

import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;

@Path("/api")
public class Manager implements Serializable {
	static ServiceProvider serviceProvider = null;
	static {
		try {

			CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("localhost:2181",
					new RetryNTimes(5, 1000));
			curatorFramework.start();
			ServiceDiscovery<Object> serviceDiscovery = ServiceDiscoveryBuilder.builder(Object.class)
					.client(curatorFramework).basePath("service-registry").build();
			serviceDiscovery.start();
			serviceProvider = serviceDiscovery.serviceProviderBuilder().serviceName("worker").build();
			serviceProvider.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Manager started ");
	}

	@GET
	@Path("/delegate")
	@Produces(MediaType.TEXT_PLAIN)
	public String delegate() {

		String response = "";
		try {

			ServiceInstance instance = serviceProvider.getInstance();
			String address = instance.buildUriSpec();
			System.out.println(address);
			Client client = ClientBuilder.newClient();

			response = client.target(address + "/api/work").request(MediaType.TEXT_PLAIN).get(String.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(response);

		return response;
	}

}
