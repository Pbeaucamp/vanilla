package bpm.gateway.core.transformations;

import org.dom4j.Element;

import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.database.RunDeleteRows;
import bpm.vanilla.platform.core.IRepositoryContext;

/**
 * This transfo perform a delete on full rows on its target DataStream
 * 
 * 
 * 
 * @author LCA
 *
 */
public class DeleteRows extends DataBaseOutputStream {

	
	private boolean keepDistinctRows;

	

	public boolean isKeepDistinctRows() {
		return keepDistinctRows;
	}

	public void setKeepDistinctRows(boolean keepDistinctRows) {
		this.keepDistinctRows = keepDistinctRows;
	}
	
	public void setKeepDistinctRows(String keepDistinctRows) {
		this.keepDistinctRows = Boolean.parseBoolean(keepDistinctRows);
	}

	@Override
	public Element getElement() {
		Element e =  super.getElement();
		e.setName("deleteRows");
		e.addElement("keepDistinct").setText(keepDistinctRows + "");
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		return e;
	}

//	@Override
//	public TransformationRuntime getExecutioner(RuntimeEngine runtimeEngine) {
//		return new DeleteRowsRuntime(this, runtimeEngine);
//	}
	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunDeleteRows(this, bufferSize);
	}
	
		
}
