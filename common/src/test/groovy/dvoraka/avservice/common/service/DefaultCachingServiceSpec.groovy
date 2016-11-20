package dvoraka.avservice.common.service

import spock.lang.Specification
import spock.lang.Subject

/**
 * DefaultCachingService spec.
 */
class DefaultCachingServiceSpec extends Specification {

    @Subject
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

    def "retrieving with null key"() {
        when:
            String result = cachingService.get(null)

        then:
            !result
    }

    def "create digest for OK size array"() {
        given:
            byte[] bytes = 'Some text'.getBytes('UTF-8')
            cachingService.setMaxCachedFileSize(bytes.length)

        expect: "we get something"
            cachingService.arrayDigest(bytes)
    }

    def "create digest for too big array"() {
        given:
            byte[] bytes = 'Some text'.getBytes('UTF-8')
            cachingService.setMaxCachedFileSize(bytes.length - 1)

        expect: "we should get null"
            !cachingService.arrayDigest(bytes)
    }

    def "you can insert null digest, info or both without NPE"() {
        when:
            cachingService.put(null, "info")
            cachingService.put("digest", null)
            cachingService.put(null, null)

        then:
            notThrown(NullPointerException)
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

    def "set max cache size"() {
        given:
            long maxSize = 10

        when:
            cachingService.setMaxCacheSize(maxSize)

        then:
            cachingService.getMaxCacheSize() == maxSize
    }

    def "set max cached file size"() {
        given:
            long maxFileSize = 11

        when:
            cachingService.setMaxCachedFileSize(maxFileSize)

        then:
            cachingService.getMaxCachedFileSize() == maxFileSize
    }
}
