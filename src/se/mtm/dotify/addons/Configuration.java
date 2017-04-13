package se.mtm.dotify.addons;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.dotify.api.config.ConfigurationDetails;

class Configuration {
	private static final Logger logger = Logger.getLogger(Configuration.class.getCanonicalName());
	private static final QName TEMPLATE = new QName("template");
	private static final QName ATTR_IDENTIFIER = new QName("identifier");
	private static final QName ATTR_DESC = new QName("desc");
	private static final QName ATTR_NICENAME = new QName("nicename");
	private static final QName ENTRY = new QName("entry");
	private static final QName ATTR_KEY = new QName("key");
	private static final QName ATTR_VALUE = new QName("value");
	private final ConfigurationDetails details;
	private final Map<String, Object> properties;

	private Configuration(ConfigurationDetails details, Map<String, Object> properties) {
		this.details = details;
		this.properties = Collections.unmodifiableMap(new HashMap<>(properties));
	}

	public ConfigurationDetails getDetails() {
		return details;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	static List<Configuration> loadFromXML(XMLInputFactory inFactory, InputStream is) {
		List<Configuration> ret = new ArrayList<>();
		try {
			XMLEventReader reader = inFactory.createXMLEventReader(is);
			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();
				if (event.getEventType()==XMLStreamConstants.START_ELEMENT) {
					if (TEMPLATE.equals(event.asStartElement().getName())) {
						Configuration t = readTemplate(event, reader);
						if (t!=null) {
							ret.add(t);
						}
					}
				} else if (event.getEventType()==XMLStreamConstants.END_ELEMENT) {
					break;
				}
			}
			reader.close();
		} catch (XMLStreamException e) {
			if (logger.isLoggable(Level.FINE)) {
				logger.log(Level.FINE, "Failed to read from file.", e);
			}
		} finally {
			try {
				is.close();
			} catch (IOException e) {}
		}
		return ret;
	}
	
	private static String getAttr(StartElement el, QName name) {
		Attribute at = el.getAttributeByName(name);
		return at==null?null:at.getValue();
	}
	
	private static void skipTree(XMLEvent event, XMLEventReader reader) throws XMLStreamException {
		StartElement start = event.asStartElement();
		int i = 1;
		while (reader.hasNext()) {
			event = reader.nextEvent();
			if (event.getEventType()==XMLStreamConstants.START_ELEMENT) {
				i++;
			} else if (event.getEventType()==XMLStreamConstants.END_ELEMENT) {
				i--;
				if (i==0 && start.getName().equals(event.asEndElement().getName())) {
					break;
				}
			}
		}
	}
	
	private static String toLocation(XMLEvent event) {
		return " (at " + event.getLocation().getLineNumber() + ", " +event.getLocation().getColumnNumber() + ")";
	}
	
	private static Configuration readTemplate(XMLEvent event, XMLEventReader reader) throws XMLStreamException {
		StartElement start = event.asStartElement();
		String identifier = getAttr(start, ATTR_IDENTIFIER);
		if (identifier==null||"".equals(identifier)) {
			logger.warning("Skipping template due to invalid identifier" + toLocation(event));
			skipTree(event, reader);
			return null;
		}
		ConfigurationDetails.Builder confBuilder = new ConfigurationDetails.Builder(identifier);
		String desc = getAttr(start, ATTR_DESC);
		if (desc!=null) {
			confBuilder.description(desc);
		}
		String nicename = getAttr(start, ATTR_NICENAME);
		if (nicename!=null) {
			confBuilder.niceName(nicename);
		}
	
		int i = 1;
		Map<String, Object> properties = new HashMap<>();
		while (reader.hasNext()) {
			event = reader.nextEvent();
			if (event.getEventType()==XMLStreamConstants.START_ELEMENT) {
				i++;
				if (ENTRY.equals(event.asStartElement().getName())) {
					String key = getAttr(event.asStartElement(), ATTR_KEY);
					String value = getAttr(event.asStartElement(), ATTR_VALUE);
					if (key==null || "".equals(key)) {
						logger.warning("Skipping entry due to invalid key" + toLocation(event));
					} else if (value==null || "".equals(value)) {
						logger.warning("Skipping entry due to invalid value" + toLocation(event));
					} else {
						properties.put(key, value);
					}
				} else {
					logger.warning("Unexpected content" + toLocation(event));
				}
			} else if (event.getEventType()==XMLStreamConstants.END_ELEMENT) {
				i--;
				if (i==0 && start.getName().equals(event.asEndElement().getName())) {
					break;
				}
			}
		}
		return new Configuration(confBuilder.build(), properties);
	}
	
}
