package bpm.aklabox.workflow.core.model.resources;

import java.io.Serializable;

public class FormCell implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private int formId;
	private String name;
	private int xAxis;
	private int yAxis;
	private int width;
	private int height;

	public FormCell() {}
	
	public FormCell(String name, int xAxis, int yAxis, int width, int height) {
		super();
		this.name = name;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.width = width;
		this.height = height;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getxAxis() {
		return xAxis;
	}

	public void setxAxis(int xAxis) {
		this.xAxis = xAxis;
	}

	public int getyAxis() {
		return yAxis;
	}

	public void setyAxis(int yAxis) {
		this.yAxis = yAxis;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
