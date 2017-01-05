package se.mtm.dotify.addons;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.daisy.dotify.api.tasks.TaskGroup;
import org.daisy.dotify.api.tasks.TaskGroupFactory;
import org.daisy.dotify.api.tasks.TaskGroupInformation;
import org.daisy.dotify.api.tasks.TaskGroupSpecification;
import org.daisy.dotify.api.tasks.TaskOption;

import aQute.bnd.annotation.component.Component;

@Component
public class PefTweakerFactory implements TaskGroupFactory {
	private final Set<TaskGroupInformation> information;

	public PefTweakerFactory() {
		Set<TaskGroupInformation> tmp = new HashSet<>();
		tmp.add(TaskGroupInformation.newEnhanceBuilder("pef").locale("sv-SE").setRequiredOptions(PefTweaker.REQUIRED_OPTIONS).build());
		information = Collections.unmodifiableSet(tmp);
	}
	
	@Override
	public boolean supportsSpecification(TaskGroupInformation spec) {
		return listAll().contains(spec);
	}

	@Override
	public TaskGroup newTaskGroup(TaskGroupSpecification spec) {
		return new PefTweaker(spec.getInputFormat());
	}

	@Override
	public Set<TaskGroupInformation> listAll() {
		return information;
	}

}