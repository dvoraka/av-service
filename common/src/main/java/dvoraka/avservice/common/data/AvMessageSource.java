package dvoraka.avservice.common.data;

/**
 * AV Message sources.
 */
public enum AvMessageSource {
    CUSTOM,
    AMQP_COMPONENT,
    AMQP_COMPONENT_IN,
    AMQP_COMPONENT_OUT,
    JMS_COMPONENT,
    JMS_COMPONENT_IN,
    JMS_COMPONENT_OUT,
    SERVER,
    PROCESSOR,
    TEST
}
