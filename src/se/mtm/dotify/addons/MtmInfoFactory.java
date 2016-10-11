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
		String locale = "sv-SE";
		TaskGroupSpecification.Builder builder = new TaskGroupSpecification.Builder("html", "html", locale);
		for (TaskOption option : MtmInfo.REQUIRED_OPTIONS) {
			builder.addRequired(option);
		}
		supportedSpecifications.add(builder.build()
		);
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
		return new MtmInfo();
	}

	@Override
	public void setCreatedWithSPI() {
		//
	}

}
