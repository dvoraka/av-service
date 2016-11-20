package dvoraka.avservice.common.testing;

/**
 * Basic interface for testing properties.
 */
@FunctionalInterface
public interface TestProperties {

    void loadPropertiesFromXML(String filename);
}
