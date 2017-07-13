package dvoraka.avservice.common.data;

/**
 * Info sources.
 */
public enum InfoSource {
    CUSTOM,

    AMQP_COMPONENT_IN,
    AMQP_COMPONENT_OUT,

    JMS_COMPONENT_IN,
    JMS_COMPONENT_OUT,

    KAFKA_COMPONENT_IN,
    KAFKA_COMPONENT_OUT,

    REST_ENDPOINT,

    SERVER,
    PROCESSOR,
    RESPONSE_CACHE,

    TEST
}
