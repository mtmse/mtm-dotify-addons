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

class MtmInfoProcessor extends ExpandingTask {
	private final Map<String, Object> xsltParams;

	MtmInfoProcessor(Map<String, Object> xsltParams) {
		super("MTM addons");
		this.xsltParams = xsltParams;
	}

	@Override
	@Deprecated
	public List<InternalTask> resolve(File input) throws InternalTaskException {
		return resolve(new DefaultAnnotatedFile.Builder(input).build());
	}

	@Override
	public List<InternalTask> resolve(AnnotatedFile input) throws InternalTaskException {
		ArrayList<InternalTask> ret = new ArrayList<>();
		try {
			XMLInfo peekResult = XMLTools.parseXML(input.getPath().toFile(), true);
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
		ret.add(new XsltTask("Move cover text", MtmInfoProcessor.class.getResource("resource-files/move-cover-text.xsl"), parameters));
		ret.add(new XsltTask("MTM info (dtbook)", MtmInfoProcessor.class.getResource("resource-files/punktinfo.xsl"), parameters));
		return ret;
	}
	
	static List<InternalTask> getHtmlTasks(Map<String, Object> parameters) {
		ArrayList<InternalTask> ret = new ArrayList<>();
		ret.add(new XsltTask("MTM info (html)", MtmInfoProcessor.class.getResource("resource-files/info-html.xsl"), parameters));
		return ret;
	}

}