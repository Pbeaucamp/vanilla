package bpm.vanilla.platform.core.beans.data;

import bpm.vanilla.platform.core.beans.chart.IChartColumn;
import bpm.vanilla.platform.core.beans.data.JDBCType.Types;

public class DataColumn implements IChartColumn {

//	static XStream xstream = new XStream();
	
	public  enum FunctionalType {
		
		//RUE,CODE_POSTAL,VILLE,PAYS,CONTINENT,DATE_DE_NAISSANCE,DATE_DE_DECES,DATE_DENGAGEMENT,DATE_DE_VENTE,DIMENSION,MESURE
		CONTINENT,PAYS,REGION,ADRESSE, GEOLOCAL, LATITUDE, LONGITUDE,ANNEE,TRIMESTRE,MOIS,SEMAINE,SUM,AVG,COUNT,MIN,MAX,DIMENSION,
		CODE_POSTAL,COMMUNE,ZONEID, EXCLUSIF, NON_EXCLUSIF
	}

	private static final long serialVersionUID = 1L;

	private int id;
	private int idDataset;

	private String columnName;
	private String columnLabel;
	private String description;

	private String columnTypeName;
	private int columnType;

	private FunctionalType Ft;
	
	private DataType columnDataType;
	private DataType customDataType;
	
	private D4CTypes types = new D4CTypes();
	private String typesXml;

	public String getTypesXml() {
		return typesXml;
	}

	public void setTypesXml(String typesXml) {
		this.typesXml = typesXml;
	}

	public DataColumn() {
		super();
	}

	public DataColumn(String columnName, String columnTypeName, int columnType, String columnLabel) {
		super();
		this.columnName = columnName;
		this.columnTypeName = columnTypeName;
		this.columnType = columnType;
		this.columnLabel = columnLabel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnTypeName() {
		return columnTypeName;
	}

	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
	}

	public int getColumnType() {
		return columnType;
	}

	public void setColumnType(int columnType) {
		this.columnType = columnType;
		
		//XXX set the customtype
		switch(columnType) {
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.SMALLINT:
			case Types.TINYINT:
				columnDataType = DataType.INT;
				break;
			case Types.DECIMAL:
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.NUMERIC:
				columnDataType = DataType.DECIMAL;
				break;
			case Types.DATE:
			case Types.TIMESTAMP:
				columnDataType = DataType.DATE;
				break;
			default:
				columnDataType = DataType.STRING;
				break;
		}
	}

	public String getColumnLabel() {
		return columnLabel;
	}

	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}

	public int getIdDataset() {
		return idDataset;
	}

	public void setIdDataset(int idDataset) {
		this.idDataset = idDataset;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public boolean equals(Object obj) {
		if(getColumnName().equals(((DataColumn) obj).getColumnName())) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return this.getColumnLabel();
	}

	public FunctionalType getFt() {
		return Ft;
	}

	public void setFt(FunctionalType ft) {
		Ft = ft;
	}

	public DataType getColumnDataType() {
		if(columnDataType == null) {
			columnDataType = DataType.STRING;
		}
		
		return columnDataType;
	}

	public void setColumnDataType(DataType columnDataType) {
		this.columnDataType = columnDataType;
	}

	public DataType getCustomDataType() {
		return customDataType;
	}

	public void setCustomDataType(DataType customDataType) {
		this.customDataType = customDataType;
	}

	public D4CTypes getTypes() {
		return types;
	}

	public void setTypes(D4CTypes types) {
		this.types = types;
	}
	
}
