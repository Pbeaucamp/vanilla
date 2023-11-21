package org.fasd.olap;

import java.util.List;

public interface ICubeView {
	public String getXml();
	public List<String> getRows();
	public List<String> getCols();
}
