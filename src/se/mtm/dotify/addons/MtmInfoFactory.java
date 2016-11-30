package se.mtm.dotify.addons;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.daisy.dotify.api.tasks.TaskGroup;
import org.daisy.dotify.api.tasks.TaskGroupFactory;
import org.daisy.dotify.api.tasks.TaskGroupSpecification;
import org.daisy.dotify.api.tasks.TaskOption;

import aQute.bnd.annotation.component.Component;

@Component
public class MtmInfoFactory implements TaskGroupFactory {
	private final Set<TaskGroupSpecification> supportedSpecifications;

	public MtmInfoFactory() {
		supportedSpecifications = new HashSet<>();
		supportedSpecifications.add(makeSpec("html"));
		supportedSpecifications.add(makeSpec("dtbook"));
		supportedSpecifications.add(makeSpec("xml"));
	}
	
	private static TaskGroupSpecification makeSpec(String format) {
		TaskGroupSpecification.Builder builder = new TaskGroupSpecification.Builder(format, format, "sv-SE");
		for (TaskOption option : MtmInfo.REQUIRED_OPTIONS) {
			builder.addRequired(option);
		}
		return builder.build();
	}

	@Override
	public boolean supportsSpecification(TaskGroupSpecification spec) {
		return supportedSpecifications.contains(spec);
	}
	
	@Override
	public Set<TaskGroupSpecification> listSupportedSpecifications() {
		return Collections.unmodifiableSet(supportedSpecifications);
	}

	@Override
	public TaskGroup newTaskGroup(TaskGroupSpecification spec) {
		return new MtmInfo(spec.getInputFormat());
	}

	@Override
	public void setCreatedWithSPI() {
		//
	}

}
