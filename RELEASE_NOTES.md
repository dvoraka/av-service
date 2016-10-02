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
