package se.mtm.dotify.addons;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.daisy.streamline.api.tasks.TaskGroup;
import org.daisy.streamline.api.tasks.TaskGroupFactory;
import org.daisy.streamline.api.tasks.TaskGroupInformation;
import org.daisy.streamline.api.tasks.TaskGroupSpecification;

import aQute.bnd.annotation.component.Component;

@Component
public class MergeLinegroupsFactory implements TaskGroupFactory {
	private final Set<TaskGroupInformation> information;

	public MergeLinegroupsFactory() {
		Set<TaskGroupInformation> tmp = new HashSet<>();
		tmp.add(TaskGroupInformation.newEnhanceBuilder("dtbook").locale("sv-SE").setRequiredOptions(MergeLinegroups.REQUIRED_OPTIONS).build());
		tmp.add(TaskGroupInformation.newEnhanceBuilder("xml").locale("sv-SE").setRequiredOptions(MergeLinegroups.REQUIRED_OPTIONS).build());
		information = Collections.unmodifiableSet(tmp);
	}
	
	@Override
	public boolean supportsSpecification(TaskGroupInformation spec) {
		return listAll().contains(spec);
	}

	@Override
	public TaskGroup newTaskGroup(TaskGroupSpecification spec) {
		return new MergeLinegroups(spec.getInputFormat());
	}

	@Override
	public Set<TaskGroupInformation> listAll() {
		return information;
	}

}