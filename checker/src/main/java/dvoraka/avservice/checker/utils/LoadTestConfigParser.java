package dvoraka.avservice.checker.utils;

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
 * Simple XML parser for reading load test properties.
 * <p>
 * Created by dvoraka on 5/3/14.
 */
public class LoadTestConfigParser extends DefaultHandler {

    private static Logger logger = LogManager.getLogger();

    private Map<String, String> props = new HashMap<>();
    private StringBuilder buffer = new StringBuilder();

    public LoadTestConfigParser() {
        super();
    }

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
            logger.warn("Parser problem!", e);
        } catch (IOException e) {
            logger.warn("File problem!", e);
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        // clear buffer
        buffer.setLength(0);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if ("host".equalsIgnoreCase(qName)) {
            props.put("host", buffer.toString());
        } else if ("virtualHost".equalsIgnoreCase(qName)) {
            props.put("virtualHost", buffer.toString());
        } else if ("appId".equalsIgnoreCase(qName)) {
            props.put("appId", buffer.toString());
        } else if ("destinationQueue".equalsIgnoreCase(qName)) {
            props.put("destinationQueue", buffer.toString());
        } else if ("messageCount".equalsIgnoreCase(qName)) {
            props.put("messageCount", buffer.toString());
        } else if ("synchronous".equalsIgnoreCase(qName)) {
            props.put("synchronous", buffer.toString());
        } else if ("sendOnly".equalsIgnoreCase(qName)) {
            props.put("sendOnly", buffer.toString());
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        buffer.append(ch, start, length);
    }
}
