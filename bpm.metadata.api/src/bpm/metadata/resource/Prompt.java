package bpm.metadata.resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bpm.metadata.ISecurizable;
import bpm.metadata.layer.logical.IDataStreamElement;

/**
 * A prompt is a question asked when you try to run a query from the metadata
 * The values come from the origin dataStreamElement
 * The selected value is applyed for the gotoDataStreamElement
 * By default the operator is =
 * @author LCA
 *
 */
public class Prompt implements IResource, ISecurizable {
	
	public static final String[] TYPES = {"java.lang.String", "java.math.BigDecimal", "java.lang.Boolean", "java.lang.Integer", "java.lang.Long", "java.lang.Double", "java.sql.Date"};
	public static final int[] TYPES_CODE = {12, 2, 7, 4, 5, 6, 91};
	private String question;
	
	public Prompt() {

	}

	//where the possible datas come from
	private String originDataStreamName, originDataStreamElementName;
	private IDataStreamElement origin;
	
	//where the datas go to
	private String gotoDataStreamName, gotoDataStreamElementName;
	private IDataStreamElement gotoDataStreamElement;
	private String gotoSql = "";
	private String promptType = "java.lang.String";
	
	private String name;
	private String operator = "=";
	
	private HashMap<String, Boolean> granted = new HashMap<String, Boolean>();
	private HashMap<Locale, String> outputName = new HashMap<Locale, String>();
	
	private Prompt parentPrompt;
	
	/**
	 * For digester
	 */
	private String parentPromptName;
	
	public String getOutputName(Locale l){
		return outputName.get(l) != null && !outputName.get(l).equals("") ? outputName.get(l) : getName() ;
	}
	
	public String getOutputName(){
		return outputName.get(Locale.getDefault()) != null && outputName.get(Locale.getDefault()).equals("") ? outputName.get(Locale.getDefault()) : getName();
	}
	
	public void setOutputName(Locale l, String value){
		outputName.put(l, value);
	}
	public void setOutputName(String country, String language, String value){
		outputName.put(new Locale(language, country), value);
	}
	
	public boolean isGrantedFor(String groupName){
		if(groupName.equals("none")) {
			return true;
		}
		for(String s : granted.keySet()){
			if (s.equals(groupName)){
				return granted.get(s);
			}
		}
		return false;
	}
	
	public void setGranted(String groupName, boolean value) {
		for(String s : granted.keySet()){
			if (s.equals(groupName)){
				this.granted.put(s, value);
				return;
			}
		}
		this.granted.put(groupName, value);
	}
	
	/**
	 * 
	 * This method is just left for old Metadata model. However it will not be used for new model
	 * 
	 * @param groupName
	 * @param value
	 */
	@Deprecated
	public void setGranted(String groupName, String value) {
		setGranted(groupName, Boolean.parseBoolean(value));
	}
	
	public void setGroupsGranted(String groups){
		if(!groups.isEmpty()){
			String[] arrayGroups = groups.split(",");
			for(String gr : arrayGroups){
				setGranted(gr, true);
			}
		}
	}
	
	public HashMap<String, Boolean> getGrants(){
		return granted;
	}

	public String getName() {
		return name;
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("        <prompt>\n");
		buf.append("            <name>" + name + "</name>\n");
		buf.append("            <question>" + question + "</question>\n");
		buf.append("            <operator>" + operator.replace(">", "&gt;").replace("<", "&lt;") + "</operator>\n");
		
		if(origin != null && origin.getDataStream() != null) {
			buf.append("            <originDataStream>" + origin.getDataStream().getName() + "</originDataStream>\n");
			buf.append("            <originDataStreamElement>" + origin.getName() + "</originDataStreamElement>\n");
		}
		
		if(gotoDataStreamElement != null) {
			buf.append("            <destinationDataStream>" + gotoDataStreamElement.getDataStream().getName() + "</destinationDataStream>\n");
			buf.append("            <destinationDataStreamElement>" + gotoDataStreamElement.getName() + "</destinationDataStreamElement>\n");
		}
		else {
			buf.append("			<destinationSql><![CDATA["+gotoSql+"]]></destinationSql>\n");
			buf.append("			<destinationType>"+promptType+"</destinationType>\n");
		}
		
		//grants
		buf.append("            <groupNames>");
		boolean first = true;
		for(String s : granted.keySet()){
			if(granted.get(s)){
				if(first){
					first = false;
				}
				else {
					buf.append(",");
				}
				buf.append(s);
			}
		}
		buf.append("</groupNames>\n");
		
		for(Locale l : outputName.keySet()){
			buf.append("            <outputname>\n");
			buf.append("                <country>" + l.getCountry() + "</country>\n");
			buf.append("                <language>" + l.getLanguage() + "</language>\n");
			buf.append("                <value>" + outputName.get(l) + "</value>\n");
			buf.append("            </outputname>\n");
		}
		
		if(isChildPrompt()) {
			buf.append("            <parentprompt>" + parentPrompt.getName() + "</parentprompt>\n");
		}
		
		buf.append("        </prompt>\n");
		return buf.toString();
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getOriginDataStreamName() {
		return originDataStreamName;
	}

	public void setOriginDataStreamName(String originDataStreamName) {
		this.originDataStreamName = originDataStreamName;
	}

	public String getOriginDataStreamElementName() {
		return originDataStreamElementName;
	}

	public void setOriginDataStreamElementName(String originDataStreamElementName) {
		this.originDataStreamElementName = originDataStreamElementName;
	}

	public IDataStreamElement getOrigin() {
		return origin;
	}

	public void setOrigin(IDataStreamElement origin) {
		this.origin = origin;
	}

	public String getGotoDataStreamName() {
		return gotoDataStreamName;
	}

	public void setGotoDataStreamName(String gotoDataStreamName) {
		this.gotoDataStreamName = gotoDataStreamName;
	}

	public String getGotoDataStreamElementName() {
		return gotoDataStreamElementName;
	}

	public void setGotoDataStreamElementName(String gotoDataStreamElementName) {
		this.gotoDataStreamElementName = gotoDataStreamElementName;
	}

	public IDataStreamElement getGotoDataStreamElement() {
		return gotoDataStreamElement;
	}

	public void setGotoDataStreamElement(IDataStreamElement gotoDataStreamElement) {
		this.gotoDataStreamElement = gotoDataStreamElement;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	/**
	 * The map need to be <PromptName, Value>
	 * @param parentValues Need to contains all previous level values
	 * @return
	 * @throws Exception 
	 */
	public List<String> getDistinctValues(HashMap<String, String> parentValues) throws Exception {
		
		HashMap<String, String> result = new HashMap<String, String>();
		if(parentValues != null && !parentValues.isEmpty()) {
			for(String pmpName : parentValues.keySet()) {
				String colName = findPromptColumnName(pmpName, this);
				if(colName == null) {
					throw new Exception("The prompt " + pmpName + " or his origin column couldn't be found.");
				}
				
				result.put(colName, parentValues.get(pmpName));
			}
		}
		
		return this.getOrigin().getDistinctValues(result);
	}
	
	private String findPromptColumnName(String pmpName, Prompt previousPrompt) {
		if(!previousPrompt.isChildPrompt()) {
			return null;
		}
		
		if(previousPrompt.getParentPrompt().getName().equals(pmpName)) {
			return previousPrompt.getParentPrompt().getOrigin().getOrigin().getName();
		}
		
		return findPromptColumnName(pmpName, previousPrompt.getParentPrompt());
	}

	public boolean isChildPrompt() {
		return parentPrompt != null || parentPromptName != null;
	}

	public Prompt getParentPrompt() {
		return parentPrompt;
	}

	public void setParentPrompt(Prompt parentPrompt) {
		this.parentPrompt = parentPrompt;
	}

	/**
	 * For digester
	 * @return
	 */
	public String getParentPromptName() {
		return parentPromptName;
	}
	
	/**
	 * For digester
	 * @param name
	 */
	public void setParentPromptName(String name) {
		this.parentPromptName = name;
	}

	public void setGotoSql(String gotoSql) {
		this.gotoSql = gotoSql;
	}

	public String getGotoSql() {
		return gotoSql;
	}

	public void setPromptType(String promptType) {
		this.promptType = promptType;
	}

	public String getPromptType() {
		return promptType;
	}

	public int getPromptTypeCode() {
		int i = Arrays.asList(TYPES).indexOf(promptType);
		return TYPES_CODE[i];
	}
	
}
