package dvoraka.avservice.common.service

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

    def "size synchronization test"() {
        given:
            String info = 'INFO'
            int maxSize = 1_000

            Runnable putting = {
                String tName = Thread.currentThread().getName()
                10.times {
                    cachingService.put(tName + it, info)
                }
            }

            Thread[] threads = new Thread[120]
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(putting)
            }

            cachingService.setMaxCacheSize(maxSize)

        when:
            threads.each {
                it.start()
            }
            threads.each {
                it.join()
            }

        then:
            cachingService.cacheSize() == maxSize
    }
}
