import org.apache.commons.lang.StringUtils

/*
 * Copyright 2012 Jeff Ellis
 */
class YammerMetricsGrailsPlugin {

	// the plugin version
    def version = "2.2.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.0.3 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views",
            "web-app/**"
    ]

    def author = "Jeff Ellis"
    def authorEmail = "codemonkey@ellises.us"
    def title = "Grails plugin to package Coda Hale's yammer metrics jars"
    def description = '''\\
Provides the following features:
   * metrics-core
   * metrics-servlet (wired to the /metrics end point for the app).

See the source code documentation on Github for more details.
'''

    // URL to the plugin's documentation
    def documentation = "http://github.com/jeffellis/grails-yammer-metrics"

    def doWithWebDescriptor = { xml ->

        if(application.config.metrics.servletEnabled!=false){
            def count = xml.'servlet'.size()
            if(count > 0) {

                def servletElement = xml.'servlet'[count - 1]

                servletElement + {
                    'servlet' {
                        'servlet-name'("YammerMetrics")
                        'servlet-class'("org.grails.plugins.yammermetrics.reporting.GrailsAdminServlet")
                    }
                }
                println "***\nYammerMetrics servlet injected into web.xml"
            }

            count = xml.'servlet-mapping'.size()
            if(count > 0) {
                def servletUrlPattern = application.config.metrics.servletUrlPattern
                if (servletUrlPattern.isEmpty()) {
                    servletUrlPattern = '/metrics/*'
                }
                def servletMappingElement = xml.'servlet-mapping'[count - 1]
                servletMappingElement + {

                    'servlet-mapping' {
                        'servlet-name'("YammerMetrics")
                        'url-pattern'(servletUrlPattern)
                    }
                }
                println "YammerMetrics Admin servlet-mapping (for $servletUrlPattern) injected into web.xml\n***"
            }
        } else{
            println "Skipping YammerMetrics Admin servlet mapping\n***"
        }
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
