package se.mtm.dotify.addons;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.daisy.dotify.api.config.ConfigurationsProviderException;
import org.junit.Test;
@SuppressWarnings("javadoc")
public class MtmConfigurationsTest {

	@Test
	public void testConfiguration() throws ConfigurationsProviderException {
		MtmConfigurations confs = new MtmConfigurations();
		assertFalse(confs.getConfigurationDetails().isEmpty());
		assertNotNull(confs.getConfiguration("mtm-a"));
		assertNotNull(confs.getConfiguration("mtm-b"));
	}
	
/*
	for (ConfigurationDetails details : confs.getConfigurationDetails()) {
		System.out.println(details.getKey());
		System.out.println(details.getNiceName());
		System.out.println(details.getDescription());
		System.out.println(confs.getConfiguration(details.getKey()));
	}
 */

}
