package bpm.android.vanilla.wrapper.tools;

import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPResult;

public class CubeInfos {

	private OLAPCube faCube;
	private OLAPResult result;
	private String selectedCubeName;
	private boolean isIphone;
	
	public CubeInfos(OLAPCube faCube, String selectedCubeName, boolean isIphone) {
		this.faCube = faCube;
		this.selectedCubeName = selectedCubeName;
		this.isIphone = isIphone;
	}
	
	public OLAPCube getFaCube() {
		return faCube;
	}
	
	public OLAPResult getResult() {
		return result;
	}
	
	public void setResult(OLAPResult result) {
		this.result = result;
	}

	public String getSelectedCubeName() {
		return selectedCubeName;
	}

	public boolean isIphone() {
		return isIphone;
	}
}
