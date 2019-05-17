package se.mtm.dotify.addons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.daisy.streamline.api.option.UserOption;
import org.daisy.streamline.api.option.UserOptionValue;
import org.daisy.streamline.api.tasks.InternalTask;
import org.daisy.streamline.api.tasks.TaskGroup;
import org.daisy.streamline.api.tasks.TaskGroupSpecification;
import org.daisy.streamline.api.tasks.TaskSystemException;

public class MtmInfo implements TaskGroup {
	private static final String REQUIRED_KEY = "apply-mtm-addons";
	private static final Logger LOGGER = Logger.getLogger(MtmInfo.class.getCanonicalName());
	private static final String LANG_KEY = "l10nLang";
	static final List<UserOption> REQUIRED_OPTIONS;
	static {
		List<UserOption> ret = new ArrayList<>();
		ret.add(new UserOption.Builder(REQUIRED_KEY).description("This parameter must be set for the group to be included.")
				.defaultValue("false")
				.addValue(new UserOptionValue.Builder("true").build())
				.addValue(new UserOptionValue.Builder("false").build())
				.build());
		REQUIRED_OPTIONS = Collections.unmodifiableList(ret);
	}
	private final String inputFormat;
	private final String lang;
	
	MtmInfo(TaskGroupSpecification spec) {
		this.inputFormat = spec.getInputType().getIdentifier();
		this.lang = spec.getLocale();
	}

	@Override
	public String getName() {
		return "MTM Info (" + inputFormat + ")";
	}

	@Override
	public List<InternalTask> compile(Map<String, Object> p) throws TaskSystemException {
		if (validateRequirements(p)) {
			Map<String, Object> parameters = new HashMap<>(p);
			if (!parameters.containsKey(LANG_KEY)) {
				parameters.put(LANG_KEY, lang);
			}
			ArrayList<InternalTask> ret = new ArrayList<>();
			if ("html".equalsIgnoreCase(inputFormat)) {
				LOGGER.warning("Format identifier \"html\" is deprecated, use format identifer \"xhtml\" instead.");
				ret.addAll(MtmInfoProcessor.getHtmlTasks(parameters));
			} else if ("xhtml".equalsIgnoreCase(inputFormat)) {
				ret.addAll(MtmInfoProcessor.getHtmlTasks(parameters));
			} else if ("dtbook".equalsIgnoreCase(inputFormat)) {
				ret.addAll(MtmInfoProcessor.getDtbookTasks(parameters));
			} else if ("xml".equalsIgnoreCase(inputFormat)) {
				//Currently, if this is the initial step all xml-based formats will come in as
				//"xml" and need to be parsed again to get the actual format. This should be fixed
				//in the surrounding code. Once it has been, this can be updated.
				//See also https://github.com/brailleapps/dotify.task-api/issues/5
				ret.add(new MtmInfoProcessor(parameters));
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