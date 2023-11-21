package bpm.vanillahub.core.beans.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class GeojsonActivity extends Activity {
	
	private static final int DEFAULT_SCORE = 70;
	
	public enum TypeOption {
		GEOLOC(0),
		GEOJSON(1);
		
		private int type;

		private static Map<Integer, TypeOption> map = new HashMap<Integer, TypeOption>();
		static {
			for (TypeOption actionType : TypeOption.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private TypeOption(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeOption valueOf(int actionType) {
			return map.get(actionType);
		}
	}
	
	public enum TypeGeoloc {
		COLUMN_LAT_AND_LONG(0),
		COLUMNS_ADDRESS(1);
		
		private int type;

		private static Map<Integer, TypeGeoloc> map = new HashMap<Integer, TypeGeoloc>();
		static {
			for (TypeGeoloc actionType : TypeGeoloc.values()) {
				map.put(actionType.getType(), actionType);
			}
		}

		private TypeGeoloc(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static TypeGeoloc valueOf(int actionType) {
			return map.get(actionType);
		}
	}

	private static final long serialVersionUID = 1L;
	
	private TypeOption typeOption = TypeOption.GEOJSON;
	
	//Geoloc Options
	private TypeGeoloc typeGeoloc = TypeGeoloc.COLUMN_LAT_AND_LONG;
	
	private String separator = ";";
	private String encoding = "UTF-8";
	
	private VariableString columnLat = new VariableString();
	private VariableString columnLong = new VariableString();
	private VariableString columnNum = new VariableString();
	private VariableString columnStreet = new VariableString();
	private VariableString columnCity = new VariableString();
	private VariableString columnAddress = new VariableString();
	private VariableString columnPostalCode = new VariableString();
	//Between 0 and 100
	private int score = DEFAULT_SCORE;

	//Geojson Options
	private VariableString columnId = new VariableString();
	private VariableString columnGeojsonCoordinate = new VariableString();
	private VariableString limit = new VariableString();
	
	public GeojsonActivity() { }
	
	public GeojsonActivity(String name) {
		super(TypeActivity.GEOJSON, name);
	}
	
	public TypeOption getTypeOption() {
		return typeOption;
	}
	
	public void setTypeOption(TypeOption typeOption) {
		this.typeOption = typeOption;
	}
	
	/** PART GEOLOC **/
	
	public TypeGeoloc getTypeGeoloc() {
		return typeGeoloc;
	}
	
	public void setTypeGeoloc(TypeGeoloc typeGeoloc) {
		this.typeGeoloc = typeGeoloc;
	}
	
	public String getSeparator() {
		return separator;
	}
	
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	
	public String getEncoding() {
		return encoding;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public VariableString getColumnLatVS() {
		return columnLat;
	}
	
	public String getColumnLatDisplay() {
		return columnLat.getStringForTextbox();
	}
	
	public void setColumnLat(VariableString outputFile) {
		this.columnLat = outputFile;
	}

	public String getColumnLat(List<Parameter> parameters, List<Variable> variables) {
		return columnLat.getString(parameters, variables);
	}
	
	public VariableString getColumnLongVS() {
		return columnLong;
	}
	
	public String getColumnLongDisplay() {
		return columnLong.getStringForTextbox();
	}
	
	public void setColumnLong(VariableString outputFile) {
		this.columnLong = outputFile;
	}

	public String getColumnLong(List<Parameter> parameters, List<Variable> variables) {
		return columnLong.getString(parameters, variables);
	}
	
	public VariableString getColumnNumVS() {
		return columnNum;
	}
	
	public String getColumnNumDisplay() {
		return columnNum.getStringForTextbox();
	}
	
	public void setColumnNum(VariableString outputFile) {
		this.columnNum = outputFile;
	}

	public String getColumnNum(List<Parameter> parameters, List<Variable> variables) {
		return columnNum.getString(parameters, variables);
	}
	
	public VariableString getColumnStreetVS() {
		return columnStreet;
	}
	
	public String getColumnStreetDisplay() {
		return columnStreet.getStringForTextbox();
	}
	
	public void setColumnStreet(VariableString outputFile) {
		this.columnStreet = outputFile;
	}

	public String getColumnStreet(List<Parameter> parameters, List<Variable> variables) {
		return columnStreet.getString(parameters, variables);
	}
	
	public VariableString getColumnCityVS() {
		return columnCity;
	}
	
	public String getColumnCityDisplay() {
		return columnCity.getStringForTextbox();
	}
	
	public void setColumnCity(VariableString outputFile) {
		this.columnCity = outputFile;
	}

	public String getColumnCity(List<Parameter> parameters, List<Variable> variables) {
		return columnCity.getString(parameters, variables);
	}
	
	public VariableString getColumnAddressVS() {
		return columnAddress;
	}
	
	public String getColumnAddressDisplay() {
		return columnAddress.getStringForTextbox();
	}
	
	public void setColumnAddress(VariableString outputFile) {
		this.columnAddress = outputFile;
	}

	public String getColumnAddress(List<Parameter> parameters, List<Variable> variables) {
		return columnAddress.getString(parameters, variables);
	}
	
	public VariableString getColumnPostalCodeVS() {
		return columnPostalCode;
	}
	
	public String getColumnPostalCodeDisplay() {
		return columnPostalCode.getStringForTextbox();
	}
	
	public void setColumnPostalCode(VariableString outputFile) {
		this.columnPostalCode = outputFile;
	}

	public String getColumnPostalCode(List<Parameter> parameters, List<Variable> variables) {
		return columnPostalCode.getString(parameters, variables);
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	
	
	/** PART GEOJSON **/
	
	public VariableString getColumnIdVS() {
		return columnId;
	}
	
	public String getColumnIdDisplay() {
		return columnId.getStringForTextbox();
	}
	
	public void setColumnId(VariableString outputFile) {
		this.columnId = outputFile;
	}

	public String getColumnId(List<Parameter> parameters, List<Variable> variables) {
		return columnId.getString(parameters, variables);
	}

	public VariableString getColumnGeojsonCoordinateVS() {
		return columnGeojsonCoordinate;
	}
	
	public String getColumnGeojsonCoordinateDisplay() {
		return columnGeojsonCoordinate.getStringForTextbox();
	}
	
	public void setColumnGeojsonCoordinate(VariableString columnCoordinate) {
		this.columnGeojsonCoordinate = columnCoordinate;
	}

	public String getColumnGeojsonCoordinate(List<Parameter> parameters, List<Variable> variables) {
		return columnGeojsonCoordinate.getString(parameters, variables);
	}

	public VariableString getLimitVS() {
		return limit;
	}
	
	public String getLimitDisplay() {
		return limit.getStringForTextbox();
	}
	
	public void setLimit(VariableString limit) {
		this.limit = limit;
	}

	public String getLimit(List<Parameter> parameters, List<Variable> variables) {
		return limit.getString(parameters, variables);
	}

	@Override
	public boolean isValid() {
		return true;
	}
	
	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		List<Variable> variables = new ArrayList<Variable>();
		variables.addAll(columnLat != null ? columnLat.getVariables() : new ArrayList<Variable>());
		variables.addAll(columnLong != null ? columnLong.getVariables() : new ArrayList<Variable>());
		variables.addAll(columnNum != null ? columnNum.getVariables() : new ArrayList<Variable>());
		variables.addAll(columnStreet != null ? columnStreet.getVariables() : new ArrayList<Variable>());
		variables.addAll(columnCity != null ? columnCity.getVariables() : new ArrayList<Variable>());
		variables.addAll(columnAddress != null ? columnAddress.getVariables() : new ArrayList<Variable>());
		variables.addAll(columnPostalCode != null ? columnPostalCode.getVariables() : new ArrayList<Variable>());
		variables.addAll(columnId != null ? columnId.getVariables() : new ArrayList<Variable>());
		variables.addAll(columnGeojsonCoordinate != null ? columnGeojsonCoordinate.getVariables() : new ArrayList<Variable>());
		variables.addAll(limit != null ? limit.getVariables() : new ArrayList<Variable>());
		return variables;
	}
	
	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.addAll(columnLat != null ? columnLat.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(columnLong != null ? columnLong.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(columnNum != null ? columnNum.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(columnStreet != null ? columnStreet.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(columnCity != null ? columnCity.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(columnAddress != null ? columnAddress.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(columnPostalCode != null ? columnPostalCode.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(columnId != null ? columnId.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(columnGeojsonCoordinate != null ? columnGeojsonCoordinate.getParameters() : new ArrayList<Parameter>());
		parameters.addAll(limit != null ? limit.getParameters() : new ArrayList<Parameter>());
		return parameters;
	}

}
