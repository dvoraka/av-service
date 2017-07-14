package dvoraka.avservice.common.data;

/**
 * Info sources.
 */
public enum InfoSource {
    AMQP_ADAPTER_IN,
    AMQP_ADAPTER_OUT,

    JMS_ADAPTER_IN,
    JMS_ADAPTER_OUT,

    KAFKA_ADAPTER_IN,
    KAFKA_ADAPTER_OUT,

    REST_ENDPOINT,

    SERVER,
    PROCESSOR,
    RESPONSE_CACHE,

    TEST
}
