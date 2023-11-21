package bpm.fwr.shared.models.report.wysiwyg;

import java.util.HashMap;

import bpm.fwr.api.beans.Constants.Orientation;
import bpm.fwr.client.utils.SizeComponentConstants;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ReportParameters implements IsSerializable {
	private static final String DEFAULT_OUTPUT_TYPE = "HTML";
	private static final Orientation DEFAULT_ORIENTATION = Orientation.PORTRAIT;
	private static final int DEFAULT_WIDTH = SizeComponentConstants.WIDTH_REPORT;
	private static final int DEFAULT_HEIGHT = SizeComponentConstants.HEIGHT_REPORT;
	private static final int DEFAULT_MARGIN = 10;
//	private static final HashMap<Integer, String> DEFAULT_METADATA_LANGUAGES = new HashMap<Integer, String>();
	
	public enum ReportParametersType {
		DEFAULT,
		NEW
	}
	
	private String outputType;
	private Orientation orientation;
	private int width;
	private int height;
	private String pageSize = "A4";
	private HashMap<String, String> margins = new HashMap<String, String>();
//	private HashMap<Integer, String> metadataLanguages = new HashMap<Integer, String>();
	private String selectedLanguage = "";
	
	private String topLeft;
	private String topRight;
	private String bottomLeft;
	private String bottomRight;
	
	private boolean nbPageTopLeft;
	private boolean nbPageTopRight;
	private boolean nbPageBottomLeft;
	private boolean nbPageBottomRight;
	
	public ReportParameters(ReportParametersType type) {
		if(type == ReportParametersType.DEFAULT){
			this.outputType = DEFAULT_OUTPUT_TYPE;
			this.orientation = DEFAULT_ORIENTATION;
			this.width = DEFAULT_WIDTH;
			this.height = DEFAULT_HEIGHT;
			this.setMargins(DEFAULT_MARGIN);
//			this.setMetadataLanguages(DEFAULT_METADATA_LANGUAGES);
		}
	}
	
	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
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

	public HashMap<String, String> getMargins() {
		return margins;
	}
	
	public void setMargins(HashMap<String, String> margins){
		this.margins = margins;
	}
	
	/**
	 * This method set the same margin for the top, bottom, left and right
	 * 
	 * @param margin
	 */
	public void setMargins(int margin){
		margins.remove("top");
		margins.put("top", String.valueOf(margin));
		
		margins.remove("left");
		margins.put("left", String.valueOf(margin));

		margins.remove("right");
		margins.put("right", String.valueOf(margin));

		margins.remove("bottom");
		margins.put("bottom", String.valueOf(margin));
	}
	
	public void setMargins(int marginTop, int marginLeft, int marginRight, int marginBottom){
		margins.remove("top");
		margins.put("top", String.valueOf(marginTop));
		
		margins.remove("left");
		margins.put("left", String.valueOf(marginLeft));

		margins.remove("right");
		margins.put("right", String.valueOf(marginRight));

		margins.remove("bottom");
		margins.put("bottom", String.valueOf(marginBottom));
	}
	
	public String getMarginTop(){
		return margins.get("top");
	}
	
	public String getMarginLeft(){
		return margins.get("left");
	}
	
	public String getMarginRight(){
		return margins.get("right");
	}
	
	public String getMarginBottom(){
		return margins.get("bottom");
	}

//	public void setMetadataLanguages(HashMap<Integer, String> metadataLanguages) {
//		this.metadataLanguages = metadataLanguages;
//	}
//
//	public HashMap<Integer, String> getMetadataLanguages() {
//		return metadataLanguages;
//	}
	//
	// /**
	// * This method remove the old language for the metadata if it exist
	// */
	// public void addMetadataLanguage(int metadataId, String value){
	// this.metadataLanguages.remove(metadataId);
	// this.metadataLanguages.put(metadataId, value);
	// }
	
	public void setSelectedLanguage(String selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}
	
	public String getSelectedLanguage() {
		return selectedLanguage != null ? selectedLanguage : "";
	}

	public String getTopLeft() {
		return topLeft;
	}

	public void setTopLeft(String topLeft) {
		this.topLeft = topLeft;
	}

	public String getTopRight() {
		return topRight;
	}

	public void setTopRight(String topRight) {
		this.topRight = topRight;
	}

	public String getBottomLeft() {
		return bottomLeft;
	}

	public void setBottomLeft(String bottomLeft) {
		this.bottomLeft = bottomLeft;
	}

	public String getBottomRight() {
		return bottomRight;
	}

	public void setBottomRight(String bottomRight) {
		this.bottomRight = bottomRight;
	}

	public boolean isNbPageTopLeft() {
		return nbPageTopLeft;
	}

	public void setNbPageTopLeft(boolean nbPageTopLeft) {
		this.nbPageTopLeft = nbPageTopLeft;
	}

	public boolean isNbPageTopRight() {
		return nbPageTopRight;
	}

	public void setNbPageTopRight(boolean nbPageTopRight) {
		this.nbPageTopRight = nbPageTopRight;
	}

	public boolean isNbPageBottomLeft() {
		return nbPageBottomLeft;
	}

	public void setNbPageBottomLeft(boolean nbPageBottomLeft) {
		this.nbPageBottomLeft = nbPageBottomLeft;
	}

	public boolean isNbPageBottomRight() {
		return nbPageBottomRight;
	}

	public void setNbPageBottomRight(boolean nbPageBottomRight) {
		this.nbPageBottomRight = nbPageBottomRight;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	public String getPageSize() {
		return pageSize;
	}
}
