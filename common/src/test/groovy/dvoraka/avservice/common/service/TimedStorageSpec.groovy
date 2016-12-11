package dvoraka.avservice.common.service

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject

/**
 * Timed storage spec.
 */
@Ignore('WIP')
class TimedStorageSpec extends Specification {

    @Subject
    TimedStorage<String> storage


    def setup() {
        storage = new TimedStorage<>(5_000)
    }
}
