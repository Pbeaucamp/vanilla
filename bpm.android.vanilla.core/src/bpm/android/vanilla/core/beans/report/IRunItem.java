package bpm.android.vanilla.core.beans.report;

import java.io.Serializable;

public interface IRunItem extends Serializable {

	public int getItemId();
	
	public String getHtml();
	
	public String getOutputFormat();
}
