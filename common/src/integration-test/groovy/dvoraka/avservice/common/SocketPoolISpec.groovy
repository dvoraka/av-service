package dvoraka.avservice.common

import dvoraka.avservice.common.socket.SocketPool
import spock.lang.Specification
import spock.lang.Subject

/**
 * Socket pool spec.
 */
class SocketPoolISpec extends Specification {

    @Subject
    SocketPool socketPool

    int socketCount = 5
    String hostname = 'localhost'
    int port = 3310


    def setup() {
        socketPool = new SocketPool(socketCount, hostname, port, null)
    }

    def cleanup() {
        socketPool.close()
    }

    def "get socket"() {
        expect:
            socketPool.getSocket()
    }

    def "get and return socket without usage"() {
        expect:
            socketPool.availableSockets() == socketCount

        when:
            SocketPool.SocketWrapper wrapper = socketPool.getSocket()

        then:
            socketPool.availableSockets() == (socketCount - 1)

        when:
            socketPool.returnSocket(wrapper)

        then:
            socketPool.availableSockets() == socketCount
    }

    def "get and return socket with usage"() {
        expect:
            socketPool.availableSockets() == socketCount

        when:
            SocketPool.SocketWrapper wrapper = socketPool.getSocket()
            wrapper.getOutputStream()
            wrapper.getBufferedReader()

        then:
            socketPool.availableSockets() == (socketCount - 1)

        when:
            socketPool.returnSocket(wrapper)

        then:
            socketPool.availableSockets() == socketCount
    }

    def "get hostname"() {
        expect:
            socketPool.getHost() == hostname
    }

    def "get port"() {
        expect:
            socketPool.getPort() == port
    }

    def "close pool"() {
        expect:
            socketPool.close()
    }
}
