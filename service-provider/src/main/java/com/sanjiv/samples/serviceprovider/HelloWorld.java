package com.sanjiv.samples.serviceprovider;

import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;


@Path("/rest")
public class HelloWorld {

    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Hello World";
    }
    

}