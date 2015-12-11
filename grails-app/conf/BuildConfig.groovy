/*
 * Copyright 2013 Jeff Ellis
 */

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

metrics.core.version = "3.1.2"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
	
	
    repositories {

        grailsRepo "http://grails.org/plugins"

        grailsPlugins()
        grailsHome()
        grailsCentral()


        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenLocal()
        mavenCentral()

    }
	
	plugins {
		build ':release:3.1.2', ':rest-client-builder:2.1.1', {
            export = false
        }

        build ":codenarc:0.19", {
            export = false
        }


    }
	
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		runtime "io.dropwizard.metrics:metrics-core:${metrics.core.version}"
		runtime "io.dropwizard.metrics:metrics-jvm:${metrics.core.version}"
		runtime "io.dropwizard.metrics:metrics-servlets:${metrics.core.version}"
    }
}
