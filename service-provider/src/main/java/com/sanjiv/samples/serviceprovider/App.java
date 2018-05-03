package com.sanjiv.samples.serviceprovider;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;

import com.sanjiv.samples.serviceprovider.api.Worker;

public class App extends ResourceConfig {
    public static void main(String[] args) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        int port = 8000;
        if (args.length > 0 && args.length ==2 ) {
        	String workerName = args[0];
        	System.setProperty("workerName", workerName);
        	port = Integer.valueOf(args[1]);
        	
        	System.out.println("Worker:"+workerName);
        	System.out.println("port:"+port);
        }

        Server jettyServer = new Server(port);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
             org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        String serviceNames = Test.class.getCanonicalName()+","+ HelloWorld.class.getCanonicalName();
        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter(
           "jersey.config.server.provider.classnames",
           serviceNames);
        String servicePackages = Worker.class.getPackage().getName();
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages",servicePackages);
        
        registerInZookeeper(port);
        System.out.println("Registered worker");
                
        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
        
        System.out.println("Done");
        
    }
    
    private static void registerInZookeeper(int port) {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("localhost:2181", new RetryNTimes(5, 1000));
        curatorFramework.start();
        
        ServiceInstance<Worker> serviceInstance;
		try {
			serviceInstance = ServiceInstance.<Worker>builder()
			        .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
			        .address("localhost")
			        .port(port)
			        .name("worker")
			        .build();
		

	        ServiceDiscoveryBuilder.builder(Worker.class)
	                .basePath("service-registry")
	                .client(curatorFramework)
	                .thisInstance(serviceInstance)
	                .build()
	                .start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
    }
    
}
