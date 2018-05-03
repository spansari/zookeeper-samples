package com.sanjiv.samples.serviceprovider;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;

import com.sanjiv.samples.serviceprovider.api.Worker;
import com.sanjiv.samples.zkutils.ServiceRegistryUtil;

public class App extends ResourceConfig {
    public static void main(String[] args) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        String port = "8000";
        if (args.length > 0 && args.length ==2 ) {
        	String workerName = args[0];
        	System.setProperty("workerName", workerName);
        	port = args[1];
       	
        	
        	System.out.println("Worker:"+workerName);
        	System.out.println("port:"+port);
        }

        Server jettyServer = new Server(Integer.valueOf(port));
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
        
        ServiceRegistryUtil.instance.register("worker", "localhost", Integer.valueOf(port));
             
        System.out.println("registerd worker service");
        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
        
        System.out.println("Done");
        
    }
    
   
}