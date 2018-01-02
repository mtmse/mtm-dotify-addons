package se.mtm.dotify.addons;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;

import org.daisy.streamline.api.config.ConfigurationDetails;
import org.daisy.streamline.api.config.ConfigurationsProvider;
import org.daisy.streamline.api.config.ConfigurationsProviderException;

import aQute.bnd.annotation.component.Component;

@Component
public class MtmConfigurations implements ConfigurationsProvider {
	private static final Logger logger = Logger.getLogger(MtmConfigurations.class.getCanonicalName());
	private final XMLInputFactory inFactory;
	private final Map<String, Configuration> templates;
	private final Set<ConfigurationDetails> details;

	public MtmConfigurations() {
		//FIXME: calls to newInstance below are OSGi violations that should be fixed.
		inFactory = XMLInputFactory.newInstance();
		inFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);        
        inFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
        inFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);
        templates = new HashMap<>();
        details = new HashSet<>();
        try {
        	URL url = this.getClass().getResource("resource-files/configurations.xml");
        	List<Configuration> tt = Configuration.loadFromXML(inFactory, url.openStream()); 
			for (Configuration t : tt) {
				addTemplate(t);
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load configuration.", e);
		}
	}
	
	private void addTemplate(Configuration t) {
		Configuration t0 = templates.put(t.getDetails().getKey(), t);
		if (t0!=null) {
			logger.warning("More than one template with name: " + t.getDetails().getKey());
			// if there is already a template with the same identifier, then the add below would fail unless we remove the old value first
			details.remove(t.getDetails());
		}
		details.add(t.getDetails());
	}

	@Override
	public Set<ConfigurationDetails> getConfigurationDetails() {
		return details;
	}

	@Override
	public Map<String, Object> getConfiguration(String identifier) throws ConfigurationsProviderException {
		Configuration t = templates.get(identifier);
		return t.getProperties();
	}

}
