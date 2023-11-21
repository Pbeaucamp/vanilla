package bpm.gwt.commons.shared.fmdt;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FmdtModel extends FmdtObject implements IsSerializable {

	private List<FmdtPackage> packages = new ArrayList<FmdtPackage>();

	public FmdtModel() {
		super();
	}
	
	public FmdtModel(String name) {
		super(name);
	}

	public List<FmdtPackage> getPackages() {
		return packages;
	}

	public void setPackages(List<FmdtPackage> packages) {
		this.packages = packages;
	}
	
	public void addPackage(FmdtPackage pack) {
		packages.add(pack);
	}
	
}
