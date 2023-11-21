package bpm.aklabox.workflow.core.model.activities;

import java.beans.Transient;
import java.util.List;

public interface IFileType {

	public String getFileType();

	public void setFileType(String type);

	@Transient
	public List<String> getListType();

}
