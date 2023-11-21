package bpm.vanilla.workplace.api.datasource.disco;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.vanilla.workplace.core.disco.DisconnectedBackupConnection;
import bpm.vanilla.workplace.core.disco.DisconnectedDataset;
import bpm.vanilla.workplace.core.disco.IDisconnectedReplacement;

public class BIRTDatasourceDisconnected implements IDisconnectedReplacement {

	private static final String NODE_DATASOURCES = "data-sources";
	private static final String NODE_DATASOURCE = "oda-data-source";
	
	private static final String NODE_DATASETS = "data-sets";
	private static final String NODE_DATASET = "oda-data-set";
	private static final String NODE_XML_PROPERTY = "xml-property";
	private static final String NODE_QUERY_TEXT = "queryText";
	
	private static final String ATTRIBUTE_DATASOURCE_EXTENSION = "extensionID";

	@Override
	public String replaceElement(String xml, String dsName, String sqlLiteURL, DisconnectedBackupConnection backupConnection) throws Exception {
		Document document = DocumentHelper.parseText(xml);
		Element ds = document.getRootElement().element(NODE_DATASOURCES);

		if (ds != null) {
			for (Object o : ds.elements(NODE_DATASOURCE)) {

				String datasourceName = ((Element) o).attributeValue("name");
				if (dsName.equals(datasourceName)) {
					((Element) o).attribute(ATTRIBUTE_DATASOURCE_EXTENSION).setText("org.eclipse.birt.report.data.oda.jdbc");
					((Element) o).clearContent();
					
					for(Element el : prepareSQLLiteDatasource(sqlLiteURL)) {
						el.detach();
						((Element) o).add(el);
					}
					break;
				}
			}
		}
		
		Element datasetsEl = document.getRootElement().element(NODE_DATASETS);

		if (datasetsEl != null) {
			for (Object o : datasetsEl.elements(NODE_DATASET)) {
				
				String datasetName = ((Element) o).attributeValue("name");
				for(DisconnectedDataset dataset : backupConnection.getDatasets()) {
					if (datasetName.equals(dataset.getName())) {
						((Element) o).attribute(ATTRIBUTE_DATASOURCE_EXTENSION).setText("org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet");
						
						for (Object p : ((Element) o).elements(NODE_XML_PROPERTY)) {
							if (((Element) p).attribute("name").getText().equals(NODE_QUERY_TEXT)) {
								((Element) p).setText(dataset.getQuerySqlite());
								break;
							}
						}
						break;
					}
				}
			}
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		OutputFormat form = OutputFormat.createPrettyPrint();
		form.setTrimText(false);
		XMLWriter writer = new XMLWriter(bos, form);
		writer.write(document.getRootElement());
		writer.close();

		String result = bos.toString("UTF-8");

		return result;
	}

	@SuppressWarnings("unchecked")
	private List<Element> prepareSQLLiteDatasource(String sqlLiteURL) throws DocumentException {
		StringBuffer buf = new StringBuffer();
		buf.append("<root>");
		buf.append("<list-property name=\"privateDriverProperties\">");
		buf.append("    <ex-property>");
		buf.append("        <name>metadataBidiFormatStr</name>");
		buf.append("        <value>ILYNN</value>");
		buf.append("    </ex-property>");
		buf.append("    <ex-property>");
		buf.append("        <name>disabledMetadataBidiFormatStr</name>");
		buf.append("    </ex-property>");
		buf.append("    <ex-property>");
		buf.append("        <name>contentBidiFormatStr</name>");
		buf.append("        <value>ILYNN</value>");
		buf.append("    </ex-property>");
		buf.append("    <ex-property>");
		buf.append("        <name>disabledContentBidiFormatStr</name>");
		buf.append("    </ex-property>");
		buf.append("</list-property>");
		buf.append("<property name=\"odaDriverClass\">org.sqlite.JDBC</property>");
		buf.append("<property name=\"odaURL\">" + sqlLiteURL + "</property>");
		buf.append("</root>");
		
		Document doc = DocumentHelper.parseText(buf.toString());
		return doc.getRootElement().elements();
	}
}
