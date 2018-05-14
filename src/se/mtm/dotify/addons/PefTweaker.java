package se.mtm.dotify.addons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.daisy.dotify.tasks.tools.XsltTask;
import org.daisy.streamline.api.media.FormatIdentifier;
import org.daisy.streamline.api.option.UserOption;
import org.daisy.streamline.api.option.UserOptionValue;
import org.daisy.streamline.api.tasks.InternalTask;
import org.daisy.streamline.api.tasks.TaskGroup;
import org.daisy.streamline.api.tasks.TaskSystemException;

public class PefTweaker implements TaskGroup {
	private static final String REQUIRED_KEY = "apply-pef-tweaks";
	static final List<UserOption> REQUIRED_OPTIONS;
	static {
		List<UserOption> ret = new ArrayList<>();
		ret.add(new UserOption.Builder(REQUIRED_KEY).description("This parameter must be set for the group to be included.")
				.defaultValue("false")
				.addValue(new UserOptionValue.Builder("true").description(
						"The pef-file will be enhanced with a few meta data entries. Also, empty pages are inserted so that "
						+ "each duplex sequence ends on a verso page.").build())
				.addValue(new UserOptionValue.Builder("false").description("The group is not included.").build())
				.build());
		REQUIRED_OPTIONS = Collections.unmodifiableList(ret);
	}
	private final String inputFormat;
	
	PefTweaker(FormatIdentifier inputFormat) {
		this.inputFormat = inputFormat.getIdentifier();
	}

	@Override
	public String getName() {
		return "PEF tweaker";
	}

	@Override
	public List<InternalTask> compile(Map<String, Object> parameters) throws TaskSystemException {
		if (validateRequirements(parameters)) {
			ArrayList<InternalTask> ret = new ArrayList<>();
			if ("pef".equalsIgnoreCase(inputFormat)) {
				ret.add(new XsltTask("PEF metadata finalizer", this.getClass().getResource("resource-files/pef-meta-finalizer.xsl"), parameters));
				ret.add(new XsltTask("PEF section patch", this.getClass().getResource("resource-files/pef-section-patch.xsl"), parameters));
			}
			return ret;
		} else {
			return Collections.emptyList();
		}
	}

	private static boolean validateRequirements(Map<String, Object> parameters) {
		return parameters.containsKey(REQUIRED_KEY) && "true".equalsIgnoreCase(""+parameters.get(REQUIRED_KEY));
	}

}