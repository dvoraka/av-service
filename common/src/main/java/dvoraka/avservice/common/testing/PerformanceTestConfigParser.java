package dvoraka.avservice.common.testing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple XML parser for reading performance test configuration.
 */
public class PerformanceTestConfigParser extends DefaultHandler {

    private static final Logger log = LogManager.getLogger(PerformanceTestConfigParser.class);

    private Map<String, String> props = new HashMap<>();
    private StringBuilder buffer = new StringBuilder();


    public Map<String, String> getProperties() {
        return props;
    }

    public void parseFileSax(String filename) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser;
        try {
            saxParser = factory.newSAXParser();
            saxParser.parse(filename, this);
        } catch (ParserConfigurationException | SAXException e) {
            log.warn("Parser problem!", e);
        } catch (IOException e) {
            log.warn("File problem!", e);
        }
    }

    @Override
    public void startElement(
            String namespaceURI, String localName, String qName, Attributes attrs) {
        // clear buffer
        buffer.setLength(0);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        String appIdKey = "appId";
        String destQueueKey = "destinationQueue";
        String hostKey = "host";
        String msgCountKey = "messageCount";
        String sendOnlyKey = "sendOnly";
        String syncKey = "synchronous";
        String virtHostKey = "virtualHost";

        if (appIdKey.equalsIgnoreCase(qName)) {
            props.put(appIdKey, buffer.toString());
        } else if (destQueueKey.equalsIgnoreCase(qName)) {
            props.put(destQueueKey, buffer.toString());
        } else if (hostKey.equalsIgnoreCase(qName)) {
            props.put(hostKey, buffer.toString());
        } else if (msgCountKey.equalsIgnoreCase(qName)) {
            props.put(msgCountKey, buffer.toString());
        } else if (sendOnlyKey.equalsIgnoreCase(qName)) {
            props.put(sendOnlyKey, buffer.toString());
        } else if (syncKey.equalsIgnoreCase(qName)) {
            props.put(syncKey, buffer.toString());
        } else if (virtHostKey.equalsIgnoreCase(qName)) {
            props.put(virtHostKey, buffer.toString());
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        buffer.append(ch, start, length);
    }
}
