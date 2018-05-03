package com.sanjiv.samples.serviceconsumer.api;

import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import com.sanjiv.samples.zkutils.ServiceProviderUtil;

@Path("/api")
public class Manager implements Serializable {

    @GET
    @Path("/delegate")
    @Produces(MediaType.TEXT_PLAIN)
    public String delegate() {

	String response = "";
	try {

	    String address = ServiceProviderUtil.instance.getServiceAddress("worker");
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
