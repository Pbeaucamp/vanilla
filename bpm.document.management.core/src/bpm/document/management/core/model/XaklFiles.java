package bpm.document.management.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class XaklFiles implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int xaklFilesId=0;
	private int xaklId=0;
	private int parentId=0;
	private String fileType="";
	private String fileName="";
	private String filePath="";
	private String thumbImage="";
	private String originalPath="";
	private int docId=0;
	private int docVersion=1;
	private List<XaklFiles> child = new ArrayList<XaklFiles>();
	private String xaklName;
	
	public int getXaklFilesId() {
		return xaklFilesId;
	}
	public void setXaklFilesId(int xaklFilesId) {
		this.xaklFilesId = xaklFilesId;
	}
	public int getXaklId() {
		return xaklId;
	}
	public void setXaklId(int xaklId) {
		this.xaklId = xaklId;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getThumbImage() {
		return thumbImage;
	}
	public void setThumbImage(String thumbImage) {
		this.thumbImage = thumbImage;
	}
	public int getDocVersion() {
		return docVersion;
	}
	public void setDocVersion(int docVersion) {
		this.docVersion = docVersion;
	}
	public String getOriginalPath() {
		return originalPath;
	}
	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public List<XaklFiles> getChild() {
		return child;
	}
	public void setChild(List<XaklFiles> child) {
		this.child = child;
	}
	
	public void addChild(XaklFiles item) {
		child.add(item);
	}
	public String getXaklName() {
		return xaklName;
	}
	public void setXaklName(String xaklName) {
		this.xaklName = xaklName;
	}
}