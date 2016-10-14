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

public class MtmInfo implements TaskGroup {
	private static final String REQUIRED_KEY = "apply-mtm-addons";
	static final List<TaskOption> REQUIRED_OPTIONS;
	static {
		List<TaskOption> ret = new ArrayList<>();
		ret.add(new TaskOption.Builder(REQUIRED_KEY).description("This parameter must be set for the group to be included.")
				.defaultValue("false")
				.addValue(new TaskOptionValue.Builder("true").build())
				.addValue(new TaskOptionValue.Builder("false").build())
				.build());
		REQUIRED_OPTIONS = Collections.unmodifiableList(ret);
	}

	@Override
	public String getName() {
		return "MTM Info";
	}

	@Override
	public List<InternalTask> compile(Map<String, Object> parameters) throws TaskSystemException {
		if (validateRequirements(parameters)) {
			ArrayList<InternalTask> ret = new ArrayList<>();
			ret.add(new XsltTask(getName(), this.getClass().getResource("resource-files/info-html.xsl"), parameters));
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