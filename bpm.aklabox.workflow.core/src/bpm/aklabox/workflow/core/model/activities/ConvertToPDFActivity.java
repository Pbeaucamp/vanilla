package bpm.aklabox.workflow.core.model.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConvertToPDFActivity extends Activity implements IDestinationServer, IFileServer, IAklaBoxServer, IFileType {

	private static final long serialVersionUID = 1L;

	private String address;
	private int destination;
	private String fileName;
	private int fileServerId;
//	private int aklaboxServer;
	private String fileType = "";
	private List<AklaBoxFiles> aklaBoxFiles = new ArrayList<AklaBoxFiles>();

	public ConvertToPDFActivity(String activityId, String activityName, int workflowId, int posX, int posY, int listIndex, String address, int destination, String fileName, int fileServerId/*, int aklaboxServer*/) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.workflowId = workflowId;
		this.posX = posX;
		this.posY = posY;
		this.listIndex = listIndex;
		this.address = address;
		this.destination = destination;
		this.fileName = fileName;
		this.fileServerId = fileServerId;
//		this.aklaboxServer = aklaboxServer;
	}

	public ConvertToPDFActivity() {
		this.activityId = "convertToPDF";
		this.activityName = "Convert to PDF";
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	public int getDestination() {
		return destination;
	}

//	@Override
//	public int getAklaboxServer() {
//		return aklaboxServer;
//	}
//
//	@Override
//	public void setAklaboxServer(int aklaboxServer) {
//		this.aklaboxServer = aklaboxServer;
//	}

	@Override
	public int getFileServerId() {
		return fileServerId;
	}

	@Override
	public void setFileServerId(int fileServerId) {
		this.fileServerId = fileServerId;
	}

	@Override
	public String getFileType() {
		return fileType;
	}

	@Override
	public void setFileType(String type) {
		this.fileType = type;
	}

	@Override
	public List<String> getListType() {
		if (!this.fileType.isEmpty()) {
			return Arrays.asList(this.fileType.split(";"));
		}
		else {
			return new ArrayList<String>();
		}
	}

	@Override
	public List<AklaBoxFiles> getAklaBoxFiles() {
		return aklaBoxFiles;
	}

	@Override
	public void setAklaBoxFiles(List<AklaBoxFiles> aklaBoxFiles) {
		this.aklaBoxFiles = aklaBoxFiles;
	}

}
