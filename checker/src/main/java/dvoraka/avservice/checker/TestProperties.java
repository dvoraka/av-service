package dvoraka.avservice.checker;

import java.io.IOException;

/**
 * Basic interface for testing properties.
 */
@FunctionalInterface
public interface TestProperties {

    void loadPropertiesFromXML() throws IOException;
}
