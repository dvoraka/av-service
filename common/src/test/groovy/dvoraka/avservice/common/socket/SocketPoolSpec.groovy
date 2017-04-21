package dvoraka.avservice.common.socket

import spock.lang.Specification
import spock.lang.Subject

/**
 * Socket pool spec.
 */
class SocketPoolSpec extends Specification {

    @Subject
    SocketPool socketPool

    SocketFactory socketFactory
    Socket socket

    int socketCount = 5
    String host = 'localhost'
    int port = 3310


    def setup() {
        socketFactory = Mock()

        socket = Mock()
        socket.getOutputStream() >> Mock(OutputStream)
        socket.getInputStream() >> Mock(InputStream)

        socketPool = new SocketPool(socketCount, host, port, socketFactory)
    }

    def "constructor"() {
        expect:
            socketPool.getHost() == host
            socketPool.getPort() == port
            socketPool.getSocketFactory() == socketFactory
    }

    def "get output stream"() {
        given:
            SocketPool.SocketWrapper wrapper = socketPool.getSocket()

        when:
            wrapper.getOutputStream()

        then:
            1 * socketFactory.createSocket(host, port) >> socket
    }

    def "get buffered reader"() {
        given:
            SocketPool.SocketWrapper wrapper = socketPool.getSocket()

        when:
            wrapper.getBufferedReader()

        then:
            1 * socketFactory.createSocket(host, port) >> socket
    }

    def "get socket factory"() {
        expect:
            socketFactory == socketPool.getSocketFactory()
    }
}
