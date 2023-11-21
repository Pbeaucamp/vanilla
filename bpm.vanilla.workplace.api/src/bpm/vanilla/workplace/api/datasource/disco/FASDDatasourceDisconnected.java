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

public class FASDDatasourceDisconnected implements IDisconnectedReplacement {

	private static final String NODE_DATASOURCES = "datasources";
	private static final String NODE_DATASOURCE_ODA = "datasource-oda";
	private static final String NODE_DATASOURCE_EXTENSION_ID = "odadatasourceextensionid";
	private static final String NODE_EXTENSION_ID = "odaextensionid";

	private static final String NODE_PUBLIC_PROPS = "public-properties";

	private static final String NODE_DATASET = "dataobject-oda";
	private static final String NODE_DATASET_EXTENSION_ID = "datasetextensionid";
	private static final String NODE_QUERY_TEXT = "querytext";

	@Override
	public String replaceElement(String xml, String dsName, String sqlLiteURL, DisconnectedBackupConnection backupConnection) throws Exception {
		Document document = DocumentHelper.parseText(xml);
		Element ds = document.getRootElement().element(NODE_DATASOURCES);

		if (ds != null) {
			for (Object o : ds.elements(NODE_DATASOURCE_ODA)) {

				String datasourceName = ((Element) o).element("name").getText();
				if (dsName.equals(datasourceName)) {
					((Element) o).element(NODE_DATASOURCE_EXTENSION_ID).setText("org.eclipse.birt.report.data.oda.jdbc");
					((Element) o).element(NODE_EXTENSION_ID).setText("org.eclipse.birt.report.data.oda.jdbc");
					Element elementPublic = ((Element) o).element(NODE_PUBLIC_PROPS);
					
					if(elementPublic != null) {
						elementPublic.clearContent();
						
						for(Element el : prepareSQLLiteDatasourcePublic(sqlLiteURL)) {
							el.detach();
							elementPublic.add(el);
						}
					}
				}

				if (((Element) o).elements(NODE_DATASET) != null) {
					for (Object dsEl : ((Element) o).elements(NODE_DATASET)) {

						String datasetName = ((Element) dsEl).element("name").getText();
						for(DisconnectedDataset dataset : backupConnection.getDatasets()) {
							if (datasetName.equals(dataset.getName())) {
								((Element) dsEl).element(NODE_DATASET_EXTENSION_ID).setText("org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet");
								((Element) dsEl).element(NODE_QUERY_TEXT).setText(dataset.getQuerySqlite());
								break;
							}
						}
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
	private List<Element> prepareSQLLiteDatasourcePublic(String sqlLiteURL) throws DocumentException {
		StringBuffer buf = new StringBuffer();
		buf.append("<public-properties>");
		buf.append("<property><name>odaJndiName</name><value></value></property>");
		buf.append("<property><name>odaDriverClass</name><value>org.sqlite.JDBC</value></property>");
		buf.append("<property><name>odaURL</name><value>" + sqlLiteURL + "</value></property>");
		buf.append("<property><name>odaPassword</name><value></value></property>");
		buf.append("<property><name>odaUser</name><value></value></property>");
		buf.append("<property><name>odaAutoCommit</name><value>false</value></property>");
		buf.append("</public-properties>");
		
		Document doc = DocumentHelper.parseText(buf.toString());
		return doc.getRootElement().elements();
	}

}
