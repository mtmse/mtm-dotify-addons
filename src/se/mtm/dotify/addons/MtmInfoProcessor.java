package se.mtm.dotify.addons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.dotify.api.tasks.AnnotatedFile;
import org.daisy.dotify.api.tasks.DefaultAnnotatedFile;
import org.daisy.dotify.api.tasks.ExpandingTask;
import org.daisy.dotify.api.tasks.InternalTask;
import org.daisy.dotify.api.tasks.InternalTaskException;
import org.daisy.dotify.api.tasks.TaskOption;
import org.daisy.dotify.common.xml.XMLInfo;
import org.daisy.dotify.common.xml.XMLTools;
import org.daisy.dotify.common.xml.XMLToolsException;
import org.daisy.dotify.tasks.tools.XsltTask;

class MtmInfoProcessor extends ExpandingTask {
	private final Map<String, Object> xsltParams;
	private final String inputFormat;

	MtmInfoProcessor(String inputFormat, Map<String, Object> xsltParams) {
		super("MTM addons");
		this.inputFormat = inputFormat;
		this.xsltParams = xsltParams;
	}

	@Override
	public List<InternalTask> resolve(File input) throws InternalTaskException {
		return resolve(new DefaultAnnotatedFile.Builder(input).build());
	}

	@Override
	public List<InternalTask> resolve(AnnotatedFile input) throws InternalTaskException {
		ArrayList<InternalTask> ret = new ArrayList<>();
		try {
			XMLInfo peekResult = XMLTools.parseXML(input.getFile(), true);
			String rootNS = peekResult.getUri();
			String rootElement = peekResult.getLocalName();
			if ("dtbook".equals(rootElement) && "http://www.daisy.org/z3986/2005/dtbook/".equals(rootNS)) {
				ret.addAll(getDtbookTasks(xsltParams));
			} else if ("html".equals(rootElement) && (rootNS==null || "http://www.w3.org/1999/xhtml".equals(rootNS))) {
				ret.addAll(getHtmlTasks(xsltParams));
			}
		} catch (XMLToolsException e) {
			throw new InternalTaskException("XMLToolsException while reading input", e);
		}
		return ret;	
	}
	
	static List<InternalTask> getDtbookTasks(Map<String, Object> parameters) {
		ArrayList<InternalTask> ret = new ArrayList<>();
		Object mergeLineGroups = parameters.get("merge-line-groups");
		if (mergeLineGroups!=null && "true".equalsIgnoreCase(mergeLineGroups.toString())) {
			ret.add(new XsltTask("Merge linegroups", MtmInfoProcessor.class.getResource("resource-files/linegroup.xsl"), parameters));
		}
		ret.add(new XsltTask("Move cover text", MtmInfoProcessor.class.getResource("resource-files/move-cover-text.xsl"), parameters));
		ret.add(new XsltTask("MTM info (dtbook)", MtmInfoProcessor.class.getResource("resource-files/punktinfo.xsl"), parameters));
		return ret;
	}
	
	static List<InternalTask> getHtmlTasks(Map<String, Object> parameters) {
		ArrayList<InternalTask> ret = new ArrayList<>();
		ret.add(new XsltTask("MTM info (html)", MtmInfoProcessor.class.getResource("resource-files/info-html.xsl"), parameters));
		return ret;
	}

	@Override
	public List<TaskOption> getOptions() {
		//Currently, if this is the initial step all xml-based formats will come in as
		//"xml" and consequently we need to list this option when the format is xml,
		//although it only applies to dtbook
		//See also https://github.com/brailleapps/dotify.task-api/issues/5
		if ("dtbook".equalsIgnoreCase(inputFormat) || "xml".equalsIgnoreCase(inputFormat)) {
			List<TaskOption> ret = new ArrayList<>();
			ret.add(new TaskOption.Builder("merge-line-groups").description("Merges line groups (DTBook only).").build());
			return ret;
		} else {
			return super.getOptions();
		}
	}

}