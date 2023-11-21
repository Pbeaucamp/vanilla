package bpm.vanilla.platform.core.repository;

import java.util.List;
import java.util.Properties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table (name = "rpy_datasource")
public class DataSource {

	public static final String DATASOURCE_JDBC = "org.eclipse.birt.report.data.oda.jdbc";
	
	public static final String DATASOURCE_JDBC_URL = "odaURL";
	public static final String DATASOURCE_JDBC_DRIVER = "odaDriverClass";
	public static final String DATASOURCE_JDBC_USER = "odaUser";
	public static final String DATASOURCE_JDBC_PASS = "odaPassword";
	
	
	public static final String DATASOURCE_FMDT = "bpm.metadata.birt.oda.runtime";
	public static final String DATASOURCE_EXCEL = "bpm.excel.oda.runtime";
	public static final String DATASOURCE_EXCEL_LOCAL = "bpm.excel.oda.runtime.localFile";
	public static final String DATASOURCE_CSV = "bpm.csv.oda.runtime";
	public static final String DATASOURCE_GSPEADSHEET = "bpm.google.spreadsheet.oda.driver.runtime";
	public static final String DATASOURCE_INLINE = "bpm.inlinedatas.oda.driver.runtime";
	public static final String DATASOURCE_LISTDATA = "bpm.vanilla.listdata.oda.driver";
	public static final String DATASOURCE_FM = "bpm.fm.oda.driver";
	public static final String DATASOURCE_FWR = "bpm.fwr.oda.runtime";
	public static final String DATASOURCE_GTABLE = "bpm.google.table.oda.driver.runtime";
	public static final String DATASOURCE_MAPS = "bpm.vanilla.map.oda.runtime";
	public static final String DATASOURCE_NOSQL_MONGO = "bpm.nosql.oda.runtime";
	public static final String DATASOURCE_NOSQL_HBASE = "bpm.nosql.oda.runtime.hbase";
	public static final String DATASOURCE_NOSQL_CASSANDRA = "bpm.nosql.oda.runtime.cassandra";
	
	public static final String ODA_EXTENSION = "bpm.datasource.vanilla.oda.runtime";
	public static final String CURRENT_ODA_EXTENSION = "odaExtensionId";
	public static final String CURRENT_ODA_EXTENSION_DATASOURCE = "odaExtensionDataSourceId";
	
	private int id;
	
	private String name;
	private String description;
	private String odaExtensionId;
	private String odaExtensionDataSourceId;
	private Properties datasourcePublicProperties = new Properties();
	private Properties datasourcePrivateProperties = new Properties();

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name = "id")
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setId(String id) {
		this.id = new Integer(id);
	}

	@Transient
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Transient
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public String getOdaExtensionId() {
		return odaExtensionId;
	}

	public void setOdaExtensionId(String odaExtensionId) {
		this.odaExtensionId = odaExtensionId;
	}
	
	@Transient
	public String getOdaExtensionDataSourceId() {
		return odaExtensionDataSourceId;
	}

	public void setOdaExtensionDataSourceId(String odaExtensionDataSourceId) {
		this.odaExtensionDataSourceId = odaExtensionDataSourceId;
	}

	@Transient
	public Properties getDatasourcePublicProperties() {
		return datasourcePublicProperties;
	}

	public void setDatasourcePublicProperties(Properties datasourcePublicProperties) {
		this.datasourcePublicProperties = datasourcePublicProperties;
	}

	@Transient
	public Properties getDatasourcePrivateProperties() {
		return datasourcePrivateProperties;
	}

	public void setDatasourcePrivateProperties(Properties datasourcePrivateProperties) {
		this.datasourcePrivateProperties = datasourcePrivateProperties;
	}

	// TODO: GROS WTF mais j'ai pas d'id�e
	@SuppressWarnings("unchecked")
	public void setModel(String model) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(model);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}

		Element rootElement = document.getRootElement();
		if (rootElement.element("datasourceName") != null) {
			Element element = rootElement.element("datasourceName");
			this.name = element.getStringValue();
		}

		if (rootElement.element("datasourceDescription") != null) {
			Element element = rootElement.element("datasourceDescription");
			this.description = element.getStringValue();
		}

		if (rootElement.element("odaExtensionId") != null) {
			Element element = rootElement.element("odaExtensionId");
			this.odaExtensionId = element.getStringValue();
		}

		if (rootElement.element("odaExtensionDataSourceId") != null) {
			Element element = rootElement.element("odaExtensionDataSourceId");
			this.odaExtensionDataSourceId = element.getStringValue();
		}

		this.datasourcePublicProperties = new Properties();
		
		Element publicElement = rootElement.element("publicProperties");
		if (publicElement != null && publicElement.elements("publicProperty") != null) {
			for (Element g : (List<Element>) publicElement.elements("publicProperty")) {
				
				String name = "";
				String value = "";
				if (g.element("name") != null) {
					Element d = g.element("name");
					if (d != null) {
						name = d.getStringValue();
					}
				}
				if (g.element("value") != null) {
					Element d = g.element("value");
					if (d != null) {
						value = d.getStringValue();
					}
				}
				
				datasourcePublicProperties.put(name, value);
			}
		}

		this.datasourcePrivateProperties = new Properties();
		
		Element privateElement = rootElement.element("privateProperties");
		if (privateElement != null && privateElement.elements("privateProperty") != null) {
			for (Element g : (List<Element>) privateElement.elements("privateProperty")) {
				
				String name = "";
				String value = "";
				if (g.element("name") != null) {
					Element d = g.element("name");
					if (d != null) {
						name = d.getStringValue();
					}
				}
				if (g.element("value") != null) {
					Element d = g.element("value");
					if (d != null) {
						value = d.getStringValue();
					}
				}
				
				datasourcePrivateProperties.put(name, value);
			}
		}
	}

	//completly useless but that hibernate pile of crap needs it so we let it her. 
	@Column(name = "datasource_model", length = 10000000)
	public String getModel() {
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		buf.append("<datasource>\n");
		buf.append("    <datasourceName>" + name + "</datasourceName>\n");
		buf.append("    <datasourceDescription>" + description + "</datasourceDescription>\n");
		buf.append("    <odaExtensionId>" + odaExtensionId + "</odaExtensionId>\n");
		buf.append("    <odaExtensionDataSourceId>" + odaExtensionDataSourceId + "</odaExtensionDataSourceId>\n");

		buf.append("    <publicProperties>\n");
		for (Object prop : datasourcePublicProperties.keySet()) {
			if (prop instanceof String) {
				buf.append("        <publicProperty>\n");
				buf.append("            <name>" + prop + "</name>\n");
				buf.append("            <value>" + datasourcePublicProperties.getProperty((String) prop) + "</value>\n");
				buf.append("        </publicProperty>\n");
			}
		}
		buf.append("    </publicProperties>\n");

		buf.append("    <privateProperties>\n");
		for (Object prop : datasourcePublicProperties.keySet()) {
			if (prop instanceof String) {
				buf.append("        <privateProperty>\n");
				buf.append("            <name>" + prop + "</name>\n");
				buf.append("            <value>" + datasourcePublicProperties.getProperty((String) prop) + "</value>\n");
				buf.append("        </privateProperty>\n");
			}
		}
		buf.append("    </privateProperties>\n");

		buf.append("</datasource>");
		return buf.toString();
	}
	 
	@Override
	public String toString() {
		return name;
	}
}
