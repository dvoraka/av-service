## v0.7
The new version mostly brings improved file service and a replication prototype.
It is not completely done and will need a tuning. Next version should stabilize new functionality.
For the pure AV service it could be a good idea to use the still maintained version 0.5.

The server module was redesigned and there are two modules now. Server is mostly for a server
infrastructure. Sending and receiving messages is possible with the client module only.
Next thing is that the client offers a new layer for an easier usage.

## v0.7-RC1 (May 6, 2017)
 * Improved configuration for AV program
 * Updated message mapper for file service messages
 * Separated client module from server module
 * Implemented remote file service
 * Prepared file service replication prototype
 * Redesigned server configuration
 * Redesigned client configuration
 * New max speed settings for performance testing
 * Improved socket pool and set as default
 * Improved file service
 * New file server with replication runner
 * Redesigned AMQP queues
 * New user service
 * Improved technical debt
 
#### modules
 * New user module
 * New client module

## v0.5.1 (Apr 18, 2017)
 * Library versions updated

## v0.6 (Mar 1, 2017)
 * Improved tests

## v0.6-RC1 (Feb 26, 2017)
 * File service prototype
 * Redesigned REST
 * Redesigned message processing
 * Redesigned DB module
 * Removed service ID field from AvMessage
 * New socket pooling utility prototype
 * Removed old XML performance test configuration
 * New file server runner
 * Redesigned performance testing
 * Environment configurator has its own configuration
 * New script for finding all Spring profiles
 * New script for testing Gradle tasks
 * New script for load testing
 * Improved tests
 
#### modules
 * New storage module
 
#### Docker
 * Update Jessie based images to Stretch

## v0.5 (Jan 20, 2017)

## v0.5-rc1 (Jan 16, 2017)
 * New REST to AMQP infrastructure
 * New REST to JMS infrastructure
 * New REST design and remote strategy
 * Main message processor redesign
 * Interfaces redesign
 * Improved error messages
 * New timed storage for saving temporary data
 * New SocketPool class for reusing open sockets
 * Performance tester improvement
 * Used Spring BOM for dependencies
 * Spring 5 migration
 * New Haskell example client for sending messages
 * Improved ClamAV wrapper
 * Improved tests and coverage
 * Added package info for packages
 * Fixed Javadoc generation
 * Migrated to Spring Boot
 * Added Spring Actuator to REST
 * Basic implementation of statistics (stats) module
 * New REST endpoint for stats module
 * Build configuration redesign
 
#### modules
 * New stats module

#### Docker
 * Updated SonarQube to 6.2
 * Disabled file logging for ClamAV

## v0.4 (Dec 6, 2016)

## v0.4-rc1 (Dec 5, 2016)
 * New Spring profile for disabling AV message logging
 * Full implementation of AV message logging
 * Added Solr as a AV message logging backend
 * New Docker image for Solr
 * Code style updated
 * Added performance.md file for performance notes
 * Spring configuration redesign for better extensibility
 * Used Spring converter for message converting
 * Runners redesign
 * New custom runner for custom app configuration
 * New Docker image for SonarQube
 * Added AMQP to JMS bridge
 * Added JMS to AMQP bridge
 * Added checker module replacement
 * New checker for AMQP
 * New checker for JMS
 * New performance test for AMQP
 * New performance test for JMS
 * Removed JMS client (new checker is replacement)
 * Removed AMQP client (new checker is replacement)
 * Exceptions cleaning
 * Updated 3rd party libraries versions
 * Improved build configuration

#### modules
 * Removed checker module

## v0.3 (Oct 8, 2016)

## v0.3-rc1 (Oct 2, 2016)
 * Better performance
 * AV program caching for speedup same checks
 * Better multi-threading synchronization
 * XML REST configuration migrated to Java config
 * DB logging service prototype
 * Use development Docker containers on Travis CI
 * Changed logging implementation to Logback for better Logstash connection
 * Updated external libraries versions
 * One configuration property file for the whole application
 * Performance tests
 * Reference implementation of AMQP client
 * Implementation of a message converter with Spring
 * Virus info string in a message is now much more standardized
 
#### modules
 * New rest module
 * New database module
 * New avprogram module
 * service module renamed to core

## v0.2 (Jul 29, 2016)
 * Added this document

## v0.2-rc1 (Jul 26, 2016)
 * JMS support
 * Improved tests and coverage
 * New docker images for better development
 * Better code organization
 * Added runners for AMQP, JMS and REST servers for easy starting

## v0.1 (May 29, 2016)
 * First prototypes
 * AMQP support
 * REST support
 * AV-checker project integration
 * Docker support for development
 * Configuration in one properties file
