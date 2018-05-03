package com.sanjiv.samples.serviceprovider.api;

import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/api")
public class Worker implements Serializable {
	
	@GET
    @Path("/work")
    public String work() {
        String response = "Service Provided by "+System.getProperty("workerName");
        System.out.println(response);
        return response;
    }
	
}
