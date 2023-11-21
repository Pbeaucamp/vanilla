package bpm.fd.design.ui.editor.style;

public enum CssConstants {
	
	
	borderColor("border-color", null),
	borderStyle("border-style", new String[]{"none", "dotted", "dashed", "solid"}),
	borderWidth("border-width", null),
	
	marginBottom("margin-bottom", null),
	marginTop("margin-top", null),
	marginLeft("margin-left", null),
	marginRight("margin-right", null),
	paddingBottom("padding-bottom", null),
	paddingTop("padding-top", null),
	paddingLeft("padding-left", null),
	paddingRight("padding-right", null),
	
	backgroundColor("background-color", null),
	backgroundImage("background-image", null),
	backgroundRepeat("background-repeat", new String[]{"no-repeat", "repeat", "repeat-x", "repeat-y"}),
	
	cursor("cursor", new String[]{"auto", "crosshair","default", "pointer", "move", "wait"}),
	textAlign("text-align", new String[]{"left", "right", "center", "justify"}),
	
	fontFamily("font-family", new String[]{"serif", "sans-serif", "cursive"}), 
	fontSize("font-size", null), 
	fontColor("color", null), 
	textDecoration("text-decoration", null), 
	fontWeight("font-weight", new String[]{"normal", "bold", "bolder"}), 
	fontType("font-style", new String[]{"normal", "italic"});
	//font-family(null, null)
	
	private String[] possibleValues;
	private String name;
	private CssConstants(String name, String[] predefined){
		this.possibleValues = predefined;
		this.name = name;
	}
	public String[] getValues(){
		return possibleValues;
	}
	public boolean isPredefinedvalues(){
		return possibleValues != null;
	}
	public String getName() {
		return name;
	}
}
