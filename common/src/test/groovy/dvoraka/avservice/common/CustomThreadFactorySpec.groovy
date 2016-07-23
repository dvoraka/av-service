package dvoraka.avservice.common

import spock.lang.Specification

/**
 * Custom thread factory test.
 */
class CustomThreadFactorySpec extends Specification {

    def "test factory"() {
        setup:
            String name = 'TEST-POOl-'
            CustomThreadFactory factory = new CustomThreadFactory(name)

            Thread thread1 = factory.newThread(new Runnable() {
                @Override
                void run() {
                }
            })

            Thread thread2 = factory.newThread(new Runnable() {
                @Override
                void run() {
                }
            })

        expect:
            thread1.getName().startsWith(name)
            thread2.getName().startsWith(name)

            !thread1.getName().equals(thread2.getName())
    }
}
