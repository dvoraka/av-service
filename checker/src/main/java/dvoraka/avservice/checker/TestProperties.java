package dvoraka.avservice.checker;

import java.io.IOException;

/**
 * Basic interface for testing properties.
 * <p>
 * Created by dvoraka on 4/20/14.
 */
@FunctionalInterface
public interface TestProperties {

    void loadPropertiesFromXML() throws IOException;
}
