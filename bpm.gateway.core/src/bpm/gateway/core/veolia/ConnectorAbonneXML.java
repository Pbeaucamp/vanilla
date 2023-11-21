package bpm.gateway.core.veolia;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.Transformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.veolia.RunConnectorAbonne;
import bpm.vanilla.platform.core.IRepositoryContext;

public class ConnectorAbonneXML extends ConnectorXML {

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("connectorAbonneXML");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);

		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);

		if (getServer() != null) {
			e.addElement("serverRef").setText(getServer().getName());
		}

		if (getDefinition() != null) {
			e.addElement("definition").setText(getDefinition());
		}

		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		e.addElement("encoding").setText(getEncoding());
		
		e.addElement("isInput").setText(isInput() + "");
		e.addElement("isOds").setText(isODS() + "");
		e.addElement("beginDate").setText(getBeginDate() != null ? getBeginDate() : "");
		e.addElement("endDate").setText(getEndDate() != null ? getEndDate() : "");
		e.addElement("query").setText(getQuery() != null ? getQuery() : "");
		e.addElement("query2").setText(getQuery2() != null ? getQuery2() : "");
		
		e.addElement("useMdm").setText(useMdm() + "");
		
		if (getSelectedContract() != null) {
			e.addElement("supplierId").setText(getSelectedContract().getParent().getId() + "");
		}
		if (getSelectedContract() != null) {
			e.addElement("contractId").setText(getSelectedContract().getId() + "");
		}
		
		return e;
	}

	@Override
	public Transformation copy() {
		ConnectorAbonneXML copy = new ConnectorAbonneXML();
		copy.setServer(getServer());
		copy.setDefinition(getDefinition());
		copy.setDescription(description);
		copy.setName("copy of " + name);
		copy.setTemporaryFilename(getTemporaryFilename());
		copy.setTemporarySpliterChar(getTemporarySpliterChar());
		copy.setInput(isInput());
		copy.setODS(isODS());
		copy.setBeginDate(getBeginDate());
		copy.setEndDate(getEndDate());
		copy.setQuery(getQuery());
		copy.setQuery(getQuery2());
		
		return copy;
	}

	@Override
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("FilePath : " + getDefinition() + "\n");
		buf.append("Encoding: " + getEncoding() + "\n");
		buf.append("SqlServer : \n" + getServer().getName() + "\n");
		buf.append("IsInput : \n" + isInput() + "\n");
		buf.append("IsOds : \n" + isODS() + "\n");
		buf.append("BeginDate : \n" + getBeginDate() + "\n");
		buf.append("EndDate : \n" + getEndDate() + "\n");
		buf.append("Query : \n" + getQuery() + "\n");
		buf.append("Query2 : \n" + getQuery2() + "\n");

		return buf.toString();
	}

	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		try {
			return new RunConnectorAbonne(repositoryCtx, this, bufferSize);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
