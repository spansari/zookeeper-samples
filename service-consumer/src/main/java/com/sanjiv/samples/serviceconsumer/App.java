package com.sanjiv.samples.serviceconsumer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;

import com.sanjiv.samples.serviceconsumer.api.Manager;

public class App extends ResourceConfig {
    public static void main(String[] args) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        int port = 8001;
        if (args.length != 1 ) {
        	throw new IllegalArgumentException();
        } else {
        	port = Integer.valueOf(args[0]);
        	System.out.println("port:"+port);
        }

        Server jettyServer = new Server(port);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
             org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        String serviceNames = Test.class.getCanonicalName();
        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter(
           "jersey.config.server.provider.classnames",
           serviceNames);
        String servicePackages = Manager.class.getPackage().getName();
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages",servicePackages);
        
        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
        
        System.out.println("Done");
        
    }

}
