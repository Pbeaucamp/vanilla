package bpm.fwr.api.beans.dataset;

import java.util.List;


public class GroupPrompt implements IResource {
	
	private static final long serialVersionUID = 1L;
	
	private String groupName;
	private List<FwrPrompt> cascadingPrompts;

	@Override
	public String getName() {
		return groupName;
	}

	@Override
	public void setName(String name) {
		this.groupName = name;
	}

	public void setCascadinPrompts(List<FwrPrompt> cascadingPrompts) {
		this.cascadingPrompts = cascadingPrompts;
		this.setName(buildName());
	}

	private String buildName() {
		boolean first = true;
		String grpName = "";
		for(IResource prt : cascadingPrompts) {
			if(first) {
				grpName += prt.getName();
				first = false;
			}
			else {
				grpName += " & " + prt.getName();
			}
		}
		return grpName;
	}

	public List<FwrPrompt> getCascadingPrompts() {
		return cascadingPrompts;
	}

	public void setDatasetInfo(int itemId, String businessModel, String businessPackage) {
		if(cascadingPrompts != null) {
			for(FwrPrompt prt : cascadingPrompts) {
				prt.setMetadataId(itemId);
				prt.setModelParent(businessModel);
				prt.setPackageParent(businessPackage);
			}
		}
	}
}
