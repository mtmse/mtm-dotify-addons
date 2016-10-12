package se.mtm.dotify.addons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.daisy.dotify.api.tasks.InternalTask;
import org.daisy.dotify.api.tasks.TaskGroup;
import org.daisy.dotify.api.tasks.TaskOption;
import org.daisy.dotify.api.tasks.TaskOptionValue;
import org.daisy.dotify.api.tasks.TaskSystemException;
import org.daisy.dotify.tasks.tools.XsltTask;

public class PefTweaker implements TaskGroup {
	private static final String REQUIRED_KEY = "apply-pef-tweaks";
	static final List<TaskOption> REQUIRED_OPTIONS;
	static {
		List<TaskOption> ret = new ArrayList<>();
		ret.add(new TaskOption.Builder(REQUIRED_KEY).description("This parameter must be set for the group to be included.")
				.defaultValue("false")
				.addValue(new TaskOptionValue.Builder("true").description(
						"The pef-file will be enhanced with a few meta data entries. Also, empty pages are inserted so that "
						+ "each duplex sequence ends on a verso page.").build())
				.addValue(new TaskOptionValue.Builder("false").description("The group is not included.").build())
				.build());
		REQUIRED_OPTIONS = Collections.unmodifiableList(ret);
	}
	private final String inputFormat;
	
	PefTweaker(String inputFormat) {
		this.inputFormat = inputFormat;
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

	@Override
	public List<TaskOption> getOptions() {
		return Collections.emptyList();
	}

}