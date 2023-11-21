package bpm.metadata;

public interface ISecurizable {
	public boolean isGrantedFor(String groupName);
	public void setGranted(String groupName, boolean grant);
}
