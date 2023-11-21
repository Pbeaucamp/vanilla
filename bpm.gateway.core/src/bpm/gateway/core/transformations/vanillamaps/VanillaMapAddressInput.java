package bpm.gateway.core.transformations.vanillamaps;

import java.sql.Types;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformation.norparena.NorparenaExtractAdresses;
import bpm.vanilla.platform.core.IRepositoryContext;

public class VanillaMapAddressInput extends AbstractTransformation {

	
	private static final String DESC_ARRONDISSEMENT = "Arrondissement";
	private static final String DESC_BLOC = "Bloc";
	private static final String DESC_CITY = "City";
	private static final String DESC_COUNTRY = "Country";
	private static final String DESC_ID = "Id";
	private static final String DESC_INSEECODE = "Insee Code";
	private static final String DESC_LABEL = "Label";
	private static final String DESC_STREET1 = "Street1";
	private static final String DESC_STREET2 = "Street2";
	private static final String DESC_ZIPCODE = "ZipCode";
	
	private DefaultStreamDescriptor descriptor;
	
	
	public VanillaMapAddressInput(){
		descriptor = new DefaultStreamDescriptor();
		
		StreamElement col = new StreamElement();
		col.className = String.class.getName();
		col.name = DESC_ARRONDISSEMENT;
		col.typeName = "String";
		col.type = Types.VARCHAR;
		
		descriptor.addColumn(col);
		
		col = new StreamElement();
		col.className = String.class.getName();
		col.name = DESC_BLOC;
		col.typeName = "String";
		col.type = Types.VARCHAR;
		
		descriptor.addColumn(col);
		
		col = new StreamElement();
		col.className = String.class.getName();
		col.name = DESC_CITY;
		col.typeName = "String";
		col.type = Types.VARCHAR;
		
		descriptor.addColumn(col);
		
		col = new StreamElement();
		col.className = String.class.getName();
		col.name = DESC_COUNTRY;
		col.typeName = "String";
		col.type = Types.VARCHAR;
		
		descriptor.addColumn(col);
		
		col = new StreamElement();
		col.className = Integer.class.getName();
		col.name = DESC_ID;
		col.typeName = "INTEGER";
		col.type = Types.INTEGER;
		
		descriptor.addColumn(col);
		
		col = new StreamElement();
		col.className = Integer.class.getName();
		col.name = DESC_INSEECODE;
		col.typeName = "INTEGER";
		col.type = Types.INTEGER;
		
		descriptor.addColumn(col);
		
		col = new StreamElement();
		col.className = Integer.class.getName();
		col.name = DESC_LABEL;
		col.typeName = "String";
		col.type = Types.VARCHAR;
		
		descriptor.addColumn(col);
		
		col = new StreamElement();
		col.className = Integer.class.getName();
		col.name = DESC_STREET1;
		col.typeName = "String";
		col.type = Types.VARCHAR;
		descriptor.addColumn(col);
		
		col = new StreamElement();
		col.className = Integer.class.getName();
		col.name = DESC_STREET2;
		col.typeName = "String";
		col.type = Types.VARCHAR;
		
		
		descriptor.addColumn(col);
		
		col = new StreamElement();
		col.className = String.class.getName();
		col.name = DESC_ZIPCODE;
		col.typeName = "String";
		col.type = Types.VARCHAR;
		
		descriptor.addColumn(col);
	}
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("vanillaMapAddressInput");
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		try {
			return new NorparenaExtractAdresses(repositoryCtx, this, bufferSize);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		
		for(Transformation t : getOutputs()){
			t.refreshDescriptor();
		}

	}

	public Transformation copy() {
		return new VanillaMapAddressInput();
	}

	public String getAutoDocumentationDetails() {
		return "";
	}
	
	
}
