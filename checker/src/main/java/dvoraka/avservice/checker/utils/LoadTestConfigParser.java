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
    private StringBuffer buffer = new StringBuffer();

    public LoadTestConfigParser() {

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
        if (qName.equalsIgnoreCase("host")) {
            props.put("host", buffer.toString());
        } else if (qName.equalsIgnoreCase("virtualHost")) {
            props.put("virtualHost", buffer.toString());
        } else if (qName.equalsIgnoreCase("appId")) {
            props.put("appId", buffer.toString());
        } else if (qName.equalsIgnoreCase("destinationQueue")) {
            props.put("destinationQueue", buffer.toString());
        } else if (qName.equalsIgnoreCase("messageCount")) {
            props.put("messageCount", buffer.toString());
        } else if (qName.equalsIgnoreCase("synchronous")) {
            props.put("synchronous", buffer.toString());
        } else if (qName.equalsIgnoreCase("sendOnly")) {
            props.put("sendOnly", buffer.toString());
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        buffer.append(ch, start, length);
    }
}
