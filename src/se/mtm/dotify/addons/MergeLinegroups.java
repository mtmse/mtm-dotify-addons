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

public class MergeLinegroups implements TaskGroup {
	private static final String REQUIRED_KEY = "merge-line-groups";
	static final List<TaskOption> REQUIRED_OPTIONS;
	static {
		List<TaskOption> ret = new ArrayList<>();
		ret.add(new TaskOption.Builder(REQUIRED_KEY).description("This parameter must be set for the group to be included.")
				.defaultValue("false")
				.addValue(new TaskOptionValue.Builder("true").description("Merges line groups.").build())
				.addValue(new TaskOptionValue.Builder("false").description("The group is not included.").build())
				.build());
		REQUIRED_OPTIONS = Collections.unmodifiableList(ret);
	}
	private final String inputFormat;
	
	MergeLinegroups(String inputFormat) {
		this.inputFormat = inputFormat;
	}

	@Override
	public String getName() {
		return "Merge linegroups";
	}

	@Override
	public List<InternalTask> compile(Map<String, Object> parameters) throws TaskSystemException {
		if (validateRequirements(parameters)) {
			ArrayList<InternalTask> ret = new ArrayList<>();
			if ("dtbook".equalsIgnoreCase(inputFormat)) {
				ret.addAll(MergeLinegroupsProcessor.getDtbookTasks(parameters));
			} else if ("xml".equalsIgnoreCase(inputFormat)) {
				//Currently, if this is the initial step all xml-based formats will come in as
				//"xml" and need to be parsed again to get the actual format. This should be fixed
				//in the surrounding code. Once it has been, this can be updated.
				//See also https://github.com/brailleapps/dotify.task-api/issues/5
				ret.add(new MergeLinegroupsProcessor(parameters));
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