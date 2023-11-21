package bpm.aklabox.workflow.core.model.resources;

import java.io.Serializable;

public class FormCellResult implements Serializable {


	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer cellId;
	private Integer docId;
	private String ocrResult;
	private String imageCell;
	
	public FormCellResult() {}

	public FormCellResult(String imageCell, Integer cellId, Integer docId, String ocrResult) {
		super();
		this.cellId = cellId;
		this.docId = docId;
		this.imageCell = imageCell;
		this.ocrResult = ocrResult;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCellId() {
		return cellId;
	}

	public void setCellId(Integer cellId) {
		this.cellId = cellId;
	}

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	public String getOcrResult() {
		return ocrResult;
	}

	public void setOcrResult(String ocrResult) {
		this.ocrResult = ocrResult;
	}

	public String getImageCell() {
		return imageCell;
	}

	public void setImageCell(String imageCell) {
		this.imageCell = imageCell;
	}
	
	
	
}

