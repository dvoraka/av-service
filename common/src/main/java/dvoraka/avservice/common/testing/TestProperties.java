package dvoraka.avservice.common.testing;

/**
 * Interface for testing properties.
 */
@FunctionalInterface
public interface TestProperties {

    void loadPropertiesFromXml(String filename);
}
