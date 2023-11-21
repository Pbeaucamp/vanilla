package bpm.gateway.core.tsbn.syrius;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.tsbn.RunSyriusConnector;
import bpm.vanilla.platform.core.IRepositoryContext;

public class SyriusConnector extends AbstractTransformation {

	private String userId = "";
	private String password = "";
	
	private String year = "";
	private String period = "";
	
	private String serviceUrl = "https://www.epmsi.atih.sante.fr/syriusUpload.do";
	private String outputFile = "";
	
	private String finessFilter = "";
	private String orderFilter = "";
	
	private DataBaseServer server;
	
	private String sqlPatient;
	private String sqlDiag;
	private String sqlActes;
	private String sqlGeo;
	
	private String columnCp;
	private String columnCodeGeo;
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return new DefaultStreamDescriptor();
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("syriusConnector");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("userId").setText(userId);
		e.addElement("password").setText(password);
		e.addElement("serviceUrl").setText(serviceUrl);
		e.addElement("year").setText(year);
		e.addElement("period").setText(period);
		e.addElement("finessFilter").setText(finessFilter);
		e.addElement("orderFilter").setText(orderFilter);
		
		if(server != null) {
		
			e.addElement("server").setText(server.getName());
			e.addElement("sqlPatient").addCDATA(sqlPatient);
			e.addElement("sqlDiag").addCDATA(sqlDiag);
			e.addElement("sqlActes").addCDATA(sqlActes);
			e.addElement("sqlGeo").addCDATA(sqlGeo);
			e.addElement("columnCp").setText(columnCp == null ? "" : columnCp );
			e.addElement("columnCodeGeo").setText(columnCodeGeo == null ? "" : columnCodeGeo );
		}
		
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunSyriusConnector(repositoryCtx, this, bufferSize);
	}

	@Override
	public void refreshDescriptor() { }

	@Override
	public Transformation copy() {
		SyriusConnector copy = new SyriusConnector();
		
		copy.setName("copy of " + name);
		
		copy.setPassword(password);
		copy.setPeriod(period);
		copy.setYear(year);
		copy.setUserId(userId);
		copy.setServiceUrl(serviceUrl);
		copy.setFinessFilter(finessFilter);
		copy.setOrderFilter(orderFilter);
		copy.setServer(server.getName());
		copy.setColumnCp(columnCp);
		copy.setColumnCodeGeo(columnCodeGeo);
		copy.setSqlGeo(sqlGeo);
		copy.setSqlActes(sqlActes);
		copy.setSqlDiag(sqlDiag);
		copy.setSqlPatient(sqlPatient);
		
		return copy;
	}

	@Override
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("No documentation\n");

		return buf.toString();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setFinessFilter(String finessFilter) {
		this.finessFilter = finessFilter;
	}

	public String getFinessFilter() {
		return finessFilter;
	}

	public void setOrderFilter(String orderFilter) {
		this.orderFilter = orderFilter;
	}

	public String getOrderFilter() {
		return orderFilter;
	}

	public DataBaseServer getServer() {
		return server;
	}

	public void setServer(DataBaseServer server) {
		this.server = server;
	}

	public String getSqlGeo() {
		return sqlGeo == null ? "" : sqlGeo;
	}

	public void setSqlGeo(String sqlGeo) {
		this.sqlGeo = sqlGeo;
	}
	
	public String getSqlActes() {
		return sqlActes == null ? "" : sqlActes;
	}
	
	public void setSqlActes(String sqlActes) {
		this.sqlActes = sqlActes;
	}
	
	public String getSqlDiag() {
		return sqlDiag == null ? "" : sqlDiag;
	}
	
	public void setSqlDiag(String sqlDiag) {
		this.sqlDiag = sqlDiag;
	}
	
	public String getSqlPatient() {
		return sqlPatient == null ? "" : sqlPatient;
	}
	
	public void setSqlPatient(String sqlPatient) {
		this.sqlPatient = sqlPatient;
	}

	public String getColumnCp() {
		return columnCp;
	}

	public void setColumnCp(String columnCp) {
		this.columnCp = columnCp;
	}

	public String getColumnCodeGeo() {
		return columnCodeGeo;
	}

	public void setColumnCodeGeo(String columnCodeGeo) {
		this.columnCodeGeo = columnCodeGeo;
	}
	
	public final void setServer(String serverName) {
		if (owner != null && owner.getResourceManager() != null && owner.getResourceManager().getServer(serverName) != null) {
			server = (DataBaseServer) owner.getResourceManager().getServer(serverName);
		}
		else {
			server = (DataBaseServer) ResourceManager.getInstance().getServer(serverName);
		}
	}
}
