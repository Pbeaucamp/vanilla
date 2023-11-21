package bpm.gateway.core.tsbn.rpu;

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
import bpm.gateway.runtime2.transformation.tsbn.RunRpuConnector;
import bpm.vanilla.platform.core.IRepositoryContext;

public class RpuConnector extends AbstractTransformation {

	private DataBaseServer server;

	private String finessFilter = "";
	private String orderFilter = "";

	private String dateDebut = "";
	private String dateFin = "";

	private String sqlPatient;
	private String sqlDiag;
	private String sqlActes;

	private String outputFile = "";

	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return new DefaultStreamDescriptor();
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("rpuConnector");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		e.addElement("finessFilter").setText(finessFilter);
		e.addElement("orderFilter").setText(orderFilter);

		e.addElement("dateDebut").setText(dateDebut);
		e.addElement("dateFin").setText(dateFin);

		if (server != null) {
			e.addElement("server").setText(server.getName());
			e.addElement("sqlPatient").addCDATA(sqlPatient);
			e.addElement("sqlDiag").addCDATA(sqlDiag);
			e.addElement("sqlActes").addCDATA(sqlActes);
		}
		
		e.addElement("outputFile").setText(outputFile);

		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunRpuConnector(repositoryCtx, this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
	}

	@Override
	public Transformation copy() {
		RpuConnector copy = new RpuConnector();

		copy.setName("copy of " + name);

		copy.setDateDebut(dateDebut);
		copy.setDateFin(dateFin);
		copy.setFinessFilter(finessFilter);
		copy.setOrderFilter(orderFilter);
		copy.setServer(server.getName());
		copy.setSqlActes(sqlActes);
		copy.setSqlDiag(sqlDiag);
		copy.setSqlPatient(sqlPatient);
		copy.setOutputFile(outputFile);

		return copy;
	}

	@Override
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();

		buf.append("No documentation\n");

		return buf.toString();
	}
	
	public void setDateDebut(String dateDebut) {
		this.dateDebut = dateDebut;
	}
	
	public String getDateDebut() {
		return dateDebut;
	}
	
	public void setDateFin(String dateFin) {
		this.dateFin = dateFin;
	}
	
	public String getDateFin() {
		return dateFin;
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

	public final void setServer(String serverName) {
		if (owner != null && owner.getResourceManager() != null && owner.getResourceManager().getServer(serverName) != null) {
			server = (DataBaseServer) owner.getResourceManager().getServer(serverName);
		}
		else {
			server = (DataBaseServer) ResourceManager.getInstance().getServer(serverName);
		}
	}
}
