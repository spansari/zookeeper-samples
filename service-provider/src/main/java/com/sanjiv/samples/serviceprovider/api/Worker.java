package com.sanjiv.samples.serviceprovider.api;

import com.codahale.metrics.annotation.Counted;
import com.codahale.metrics.annotation.Metered;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api")
public class Worker {
	   
    @GET
    @Path("/work")
    @Produces(MediaType.TEXT_PLAIN)
    @Timed(name = "work()-Timed")
    @Metered (name = "work()-Metered")
    @Counted (name = "work()-Counted")
    public String work() {
        String response = "Service Provided by "+System.getProperty("workerName");
        System.out.println(response);
        return response;
    }
	
}
