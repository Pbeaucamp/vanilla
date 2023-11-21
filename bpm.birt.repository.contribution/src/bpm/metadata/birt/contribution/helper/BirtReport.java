package bpm.metadata.birt.contribution.helper;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.eclipse.core.runtime.CoreException;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * the class that holds the report from the workspace and it s dependencies
 * (mostly _fr_FR.properties language files).
 * 
 * @author ere
 * 
 */
public class BirtReport {

	private File birtFile;
	private String rawxml;

	class PropertyFilter implements FilenameFilter {
		private String reportName;

		public PropertyFilter(String reportName) {
			this.reportName = reportName;
		}

		public boolean accept(File dir, String name) {
			return (name.startsWith(reportName) && name.endsWith(".properties"));
		}
	}

	// private HashMap<String, IFile> dependencies = new HashMap<String,
	// IFile>();
	// private HashMap<String, String> errors = new HashMap<String, String>();

	private List<BirtDependency> dependencies = new ArrayList<BirtDependency>();

	public BirtReport(File file, String xml) throws Exception {
		this.birtFile = file;
		this.rawxml = xml;

		try {
			parseAndFinDependencies();
		} catch (Exception e) {
			throw new Exception("Failed to find dependencies : " + e.getMessage());
		}
	}

	public File getFile() {
		return birtFile;
	}

	public String getRawXml() throws IOException, CoreException {
		return rawxml;
		// return IOUtils.toString(birtFile.getContents());
	}

	public List<BirtDependency> getDependencies() {
		return dependencies;
	}

	@SuppressWarnings("unchecked")
	protected void parseAndFinDependencies() throws DocumentException, IOException, CoreException, JaxenException {
		Document document = DocumentHelper.parseText(rawxml);
		File dir = birtFile.getParentFile();

		SimpleNamespaceContext nmsCtx = null;
		// NameSpace shit, needed for BIrt, amongst others
		HashMap<String, String> nameSpaceMap = new HashMap<String, String>();
		if (document.getRootElement().getNamespaceURI() != null) {
			nameSpaceMap.put("birt", document.getRootElement().getNamespaceURI());
			nmsCtx = new SimpleNamespaceContext(nameSpaceMap);
		}

		Dom4jXPath xpath;
		if (nmsCtx != null) {
			xpath = new Dom4jXPath("//birt:simple-property-list[@name='includeResource']/birt:value");
			xpath.setNamespaceContext(nmsCtx);
		}
		else {
			xpath = new Dom4jXPath("//simple-property-list[@name='includeResource']/value");
		}

		List<Node> nodes = xpath.selectNodes(document);

		for (Node n : nodes) {
			String depName = n.getText();

			List<String> deps = new ArrayList<String>();

			// multiple, ressource might contain "report_en; report_fr"
			if (depName.contains(";")) {
				String[] tokens = depName.split(";");
				for (String t : tokens) {

					deps.add(t);
				}
			}
			else {
				deps.add(depName);
			}

			// we will have something like [report_name] .properties
			// and we need to find all like [report_name]_fr_FR.properties,
			// [report_name]_en_US.properties, etc
			for (String dep : deps) {
				try {
					File[] childs = dir.listFiles(new PropertyFilter(depName));

					for (File child : childs) {
						if (!child.getName().equals(depName + ".properties")) {
							dependencies.add(new BirtDependency(false, child.getName(), child, "", false));
						}
					}
				} catch (Exception e) {
					dependencies.add(new BirtDependency(true, dep, null, "Warning: could not locate this dependency: " + e.getMessage(), false));
				}
			}
		}

		dependencies.addAll(getSharedResources(document, nmsCtx, dir, "//birt:list-property[@name='libraries']"));
		dependencies.addAll(getSharedResources(document, nmsCtx, dir, "//birt:list-property[@name='cssStyleSheets']"));
	}

	@SuppressWarnings("unchecked")
	private List<BirtDependency> getSharedResources(Document document, SimpleNamespaceContext namespace, File workspaceDir, String xpathValue) throws JaxenException {
		List<BirtDependency> deps = new ArrayList<BirtDependency>();

		// Check shared resources
		Dom4jXPath xpath = null;
		if (namespace != null) {
			xpath = new Dom4jXPath(xpathValue);
			xpath.setNamespaceContext(namespace);
		}
		else {
			xpath = new Dom4jXPath(xpathValue);
		}

		List<Node> resourcesNode = xpath.selectNodes(document);

		File[] childs = workspaceDir.listFiles();
		for (Node n : resourcesNode) {
			Element structureEl = ((Element) n).element("structure");
			for (Element propEl : (List<Element>) structureEl.elements("property")) {
				if (propEl.attribute("name") != null && propEl.attribute("name").getText().equals("fileName")) {
					String fileName = propEl.getText();
					try {
						for (File child : childs) {
							if (child.getName().equals(fileName)) {
								deps.add(new BirtDependency(false, child.getName(), child, "", true));
								break;
							}
						}
					} catch (Exception e) {
						deps.add(new BirtDependency(true, fileName, null, "Warning: could not locate this dependency: " + e.getMessage(), false));
					}
				}
			}
		}

		return deps;
	}
}
