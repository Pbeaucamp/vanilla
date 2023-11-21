package bpm.aklabox.workflow.core.model.activities;

import java.util.List;

public interface IAklaBoxServer {

//	public int getAklaboxServer();
//
//	public void setAklaboxServer(int aklaboxServer);

	public List<AklaBoxFiles> getAklaBoxFiles();

	public void setAklaBoxFiles(List<AklaBoxFiles> aklaBoxFiles);

}
