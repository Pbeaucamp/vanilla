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

public class FDDicoDatasourceDisconnected implements IDisconnectedReplacement {

	private static final String NODE_DATASOURCE = "dataSource";
	
	private static final String ATTRIBUTE_EXTENSION = "odaExtensionId";
	private static final String ATTRIBUTE_DATASOURCE_EXTENSION = "odaExtensionDataSourceId";
	
	private static final String NODE_DATASET = "dataSet";
	private static final String ATTRIBUTE_DATASET_EXTENSION = "odaExtensionDataSetId";
	private static final String NODE_QUERY_TEXT = "queryText";

	@Override
	public String replaceElement(String xml, String dsName, String sqlLiteURL, DisconnectedBackupConnection backupConnection) throws Exception {
		Document document = DocumentHelper.parseText(xml);

		if (document.getRootElement().elements(NODE_DATASOURCE) != null) {
			for (Object o : document.getRootElement().elements(NODE_DATASOURCE)) {
				String datasourceName = ((Element) o).element("name").getText();

				if (dsName.equals(datasourceName)) {
					((Element) o).clearContent();
					
					for(Element el : prepareSQLLiteDatasource(dsName, sqlLiteURL)) {
						el.detach();
						((Element) o).add(el);
					}
					break;
				}
			}
		}

		if (document.getRootElement().elements(NODE_DATASET) != null) {
			for (Object o : document.getRootElement().elements(NODE_DATASET)) {

				String datasetName = ((Element) o).element("name").getText();
				for(DisconnectedDataset dataset : backupConnection.getDatasets()) {
					if (datasetName.equals(dataset.getName())) {
						((Element) o).element(ATTRIBUTE_DATASOURCE_EXTENSION).setText("org.eclipse.birt.report.data.oda.jdbc");
						((Element) o).element(ATTRIBUTE_DATASET_EXTENSION).setText("org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet");
						((Element) o).element(NODE_QUERY_TEXT).setText(dataset.getQuerySqlite());
						
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
	private List<Element> prepareSQLLiteDatasource(String datasourceName, String sqlLiteURL) throws DocumentException {
		StringBuffer buf = new StringBuffer();
		buf.append("<root>");
		buf.append("<name>" + datasourceName + "</name>");
		buf.append("<odaExtensionDataSourceId>org.eclipse.birt.report.data.oda.jdbc</odaExtensionDataSourceId>");
		buf.append("<odaExtensionId>org.eclipse.birt.report.data.oda.jdbc</odaExtensionId>");
		buf.append("<odaDriverClassName>org.eclipse.birt.report.data.oda.jdbc.OdaJdbcDriver</odaDriverClassName>");
		buf.append("<publicProperty name=\"odaJndiName\"></publicProperty>");
		buf.append("<publicProperty name=\"odaDriverClass\">org.sqlite.JDBC</publicProperty>");
		buf.append("<publicProperty name=\"odaURL\">" + sqlLiteURL + "</publicProperty>");
		buf.append("<publicProperty name=\"odaPassword\"></publicProperty>");
		buf.append("<publicProperty name=\"odaUser\"></publicProperty>");
		buf.append("<privateProperty name=\"contentBidiFormatStr\">ILYNN</privateProperty>");
		buf.append("<privateProperty name=\"metadataBidiFormatStr\">ILYNN</privateProperty>");
		buf.append("</root>");
		
		Document doc = DocumentHelper.parseText(buf.toString());
		return doc.getRootElement().elements();
	}

}
