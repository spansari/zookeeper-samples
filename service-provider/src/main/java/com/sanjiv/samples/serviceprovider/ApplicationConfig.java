package com.sanjiv.samples.serviceprovider;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jersey2.InstrumentedResourceMethodApplicationListener;
import com.sanjiv.samples.serviceprovider.api.Worker;
import org.glassfish.jersey.server.ResourceConfig;
import org.hawkular.metrics.dropwizard.HawkularReporter;

import java.util.concurrent.TimeUnit;

public class ApplicationConfig  extends ResourceConfig {
    public ApplicationConfig() {
        registerListeners();
        this.register(Worker.class);
    }
    private void registerListeners() {
        final MetricRegistry metricRegistry = new MetricRegistry();
        register(new InstrumentedResourceMethodApplicationListener(metricRegistry));
        ConsoleReporter.forRegistry(metricRegistry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build()
                .start(30, TimeUnit.SECONDS);
        System.out.println("Console reporter is enabled successfully!");

//
//        HawkularReporter hawkularReporter = HawkularReporter.builder(metricRegistry, "sample-tenant")
//                .build();
//        hawkularReporter.start(1, TimeUnit.SECONDS);
    }
}

