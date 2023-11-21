package bpm.faweb.shared.drill;

import java.util.List;

public class DrillthroughStock {

	private List<List<String>> originalDrill;
	private List<List<String>> workingDrill;
	
	public DrillthroughStock(List<List<String>> originalDrill) {
		this.originalDrill = originalDrill;
	}

	public List<List<String>> getOriginalDrill() {
		return originalDrill;
	}

	public void setOriginalDrill(List<List<String>> originalDrill) {
		this.originalDrill = originalDrill;
	}

	public List<List<String>> getWorkingDrill() {
		return workingDrill;
	}

	public void setWorkingDrill(List<List<String>> workingDrill) {
		this.workingDrill = workingDrill;
	}
}
