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
	private final Set<TaskGroupSpecification> supportedSpecifications;
	private final Set<TaskGroupInformation> information;

	public PefTweakerFactory() {
		supportedSpecifications = new HashSet<>();
		supportedSpecifications.add(makeSpec("pef"));
		Set<TaskGroupInformation> tmp = new HashSet<>();
		tmp.add(TaskGroupInformation.newEnhanceBuilder("pef").locale("sv-SE").setRequiredOptions(PefTweaker.REQUIRED_OPTIONS).build());
		information = Collections.unmodifiableSet(tmp);
	}
	
	private static TaskGroupSpecification makeSpec(String format) {
		TaskGroupSpecification.Builder builder = new TaskGroupSpecification.Builder(format, format, "sv-SE");
		for (TaskOption option : PefTweaker.REQUIRED_OPTIONS) {
			builder.addRequired(option);
		}
		return builder.build();
	}

	@Override
	public boolean supportsSpecification(TaskGroupSpecification spec) {
		//TODO: move this to default implementation after move to java 8
		for (TaskGroupInformation i : listAll()) {
			if (spec.matches(i)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean supportsSpecification(TaskGroupInformation spec) {
		return listAll().contains(spec);
	}
	
	@Override
	@Deprecated
	public Set<TaskGroupSpecification> listSupportedSpecifications() {
		return Collections.unmodifiableSet(supportedSpecifications);
	}

	@Override
	public TaskGroup newTaskGroup(TaskGroupSpecification spec) {
		return new PefTweaker(spec.getInputFormat());
	}

	@Override
	public void setCreatedWithSPI() {
		//
	}

	@Override
	public Set<TaskGroupInformation> listAll() {
		return information;
	}

	@Override
	public Set<TaskGroupInformation> list(String locale) {
		//TODO: move this to default implementation after move to java 8 (and use streams)
		Objects.requireNonNull(locale);
		Set<TaskGroupInformation> ret = new HashSet<>();
		for (TaskGroupInformation info : listAll()) {
			if (info.matchesLocale(locale)) {
				ret.add(info);
			}
		}
		return ret;
	}

}