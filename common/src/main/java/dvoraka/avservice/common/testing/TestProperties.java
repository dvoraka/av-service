package dvoraka.avservice.common.testing;

import java.io.IOException;

/**
 * Basic interface for testing properties.
 */
@FunctionalInterface
public interface TestProperties {

    void loadPropertiesFromXML() throws IOException;
}
