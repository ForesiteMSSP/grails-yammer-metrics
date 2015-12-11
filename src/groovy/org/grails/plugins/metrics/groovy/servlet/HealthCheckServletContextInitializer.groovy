package org.grails.plugins.metrics.groovy.servlet

import com.codahale.metrics.health.HealthCheckRegistry
import com.codahale.metrics.servlets.HealthCheckServlet
import org.grails.plugins.metrics.groovy.HealthChecks

class HealthCheckServletContextInitializer extends HealthCheckServlet.ContextListener {

    @Override
    protected HealthCheckRegistry getHealthCheckRegistry() {
        return HealthChecks.registry
    }
}