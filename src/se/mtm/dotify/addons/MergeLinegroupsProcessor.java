package se.mtm.dotify.addons;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.dotify.common.xml.XMLInfo;
import org.daisy.dotify.common.xml.XMLTools;
import org.daisy.dotify.common.xml.XMLToolsException;
import org.daisy.dotify.tasks.tools.XsltTask;
import org.daisy.streamline.api.media.AnnotatedFile;
import org.daisy.streamline.api.media.DefaultAnnotatedFile;
import org.daisy.streamline.api.tasks.ExpandingTask;
import org.daisy.streamline.api.tasks.InternalTask;
import org.daisy.streamline.api.tasks.InternalTaskException;

class MergeLinegroupsProcessor extends ExpandingTask {
	private final Map<String, Object> xsltParams;

	MergeLinegroupsProcessor(Map<String, Object> xsltParams) {
		super("MTM linegroups");
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
			}
		} catch (XMLToolsException e) {
			throw new InternalTaskException("XMLToolsException while reading input", e);
		}
		return ret;	
	}
	
	static List<InternalTask> getDtbookTasks(Map<String, Object> parameters) {
		ArrayList<InternalTask> ret = new ArrayList<>();
		ret.add(new XsltTask("Merge linegroups", MergeLinegroupsProcessor.class.getResource("resource-files/linegroup.xsl"), parameters));
		return ret;
	}

}