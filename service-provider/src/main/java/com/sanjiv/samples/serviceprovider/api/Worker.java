package com.sanjiv.samples.serviceprovider.api;

import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api")
public class Worker implements Serializable {
	
	@GET
    @Path("/work")
	@Produces(MediaType.TEXT_PLAIN)
    public String work() {
        String response = "Service Provided by "+System.getProperty("workerName");
        System.out.println(response);
        return response;
    }
	
}
