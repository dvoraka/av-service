package dvoraka.avservice

import spock.lang.Specification

/**
 * DefaultCachingService spec.
 */
class DefaultCachingServiceSpec extends Specification {

    DefaultCachingService cachingService


    def setup() {
        cachingService = new DefaultCachingService()
    }

    def "test cache"() {
        expect:
            cachingService.cacheSize() == 0

        when:
            cachingService.put("AA", "INFO")

        then:
            cachingService.cacheSize() == 1
    }

    def "retrieving test"() {
        given:
            String digest = 'DIGEST'
            String info = 'INFO'

        expect:
            cachingService.cacheSize() == 0

        when:
            cachingService.put(digest, info)

        then:
            cachingService.cacheSize() == 1
            cachingService.get(digest) == info
    }
}
