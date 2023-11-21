package bpm.united.olap.api.tools;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataObjectOda;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.datasource.DatasourceOda;
import org.fasd.inport.DigesterFasd;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPGroup;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPMeasureGroup;
import org.fasd.olap.OLAPRelation;
import org.fasd.olap.OLAPSchema;
import org.fasd.olap.OlapDynamicMeasure;
import org.fasd.olap.Property;
import org.fasd.olap.aggregation.CalculatedAggregation;
import org.fasd.olap.aggregation.ClassicAggregation;
import org.fasd.olap.aggregation.IMeasureAggregation;
import org.fasd.olap.aggregation.LastAggregation;
import org.fasd.xmla.ISchema.SchemaType;

import bpm.united.olap.api.BadFasdSchemaModelTypeException;
import bpm.united.olap.api.datasource.Datasource;
import bpm.united.olap.api.datasource.DatasourceFactory;
import bpm.united.olap.api.datasource.Operator;
import bpm.united.olap.api.datasource.Relation;
import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.MemberProperty;
import bpm.united.olap.api.model.ModelFactory;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.model.aggregation.ILevelAggregation;
import bpm.united.olap.api.model.impl.CalculatedMeasure;
import bpm.united.olap.api.model.impl.ClosureHierarchy;
import bpm.united.olap.api.model.impl.ClosureLevel;
import bpm.united.olap.api.model.impl.DateLevel;
import bpm.united.olap.api.model.impl.DynamicMeasure;
import bpm.united.olap.api.preload.IPreloadConfig;
import bpm.united.olap.api.preload.PreloadConfig;

/**
 * convert a FASD into an united Olap schema
 * @author ludo
 *
 */
public class FasdModelConverter {

	private IPreloadConfig config;
	
	public Schema convert(InputStream is) throws Exception{
		DigesterFasd dig = new DigesterFasd(is);
		FAModel m = dig.getFAModel();
		return convert(m);
	}
	
	public static String generateUuidFromXmlDefinition(String xmlDefinition){
		return UUID.nameUUIDFromBytes(xmlDefinition.getBytes()).toString();
	}
	
	public Schema convert(FAModel faModel) throws BadFasdSchemaModelTypeException{
		
		if (faModel.getSchema().getSchemaType() == SchemaType.XMLA){
			throw new BadFasdSchemaModelTypeException(faModel, null);
		}
		
		//XXX : is it good?
		String uuid = generateUuidFromXmlDefinition(faModel.getFAXML());
		
		
//		if (faModel.getUuid() != null){
//			uuid = faModel.getUuid();
//		}
//		else{
//			uuid = UUID.randomUUID().toString();
//		}
		
		
		ModelFactory modelFactory = ModelFactory.eINSTANCE;
		
		Schema utdSchema = modelFactory.createSchema();
		utdSchema.setId(uuid);
		utdSchema.setName(faModel.getDocumentProperties().getName());
		utdSchema.setLastModificationDate(faModel.getDocumentProperties().getModification());
		
		List<Datasource> datasource = null;
		
		if(faModel.getDataSources().get(0) instanceof DatasourceOda) {		
			datasource = createDatasources(faModel);
		}
		
		else {
			datasource = createTempDatasources(faModel);
		}
		
		for(Datasource ds : datasource) {
			utdSchema.getDatasources().add(ds);
			ds.setParent(utdSchema);
		}
		
		fillSchema(utdSchema, faModel.getOLAPSchema(), modelFactory);
		
		if (faModel.getPreloadConfig() != null){
			config = new PreloadConfig();
			
			
			for(bpm.united.olap.api.model.Dimension d : utdSchema.getDimensions()){
				for(Hierarchy h : d.getHierarchies()){
					if (faModel.getPreloadConfig().getLevelNumber(h.getUname()) != null){
						config.setHierarchyLevel(h.getUname(), faModel.getPreloadConfig().getLevelNumber(h.getUname()));
					}
				}
			}
		}
		
		return utdSchema;
	}
	
	private List<Datasource> createDatasources(FAModel faModel) {
		
		DatasourceFactory datasourceFactory = DatasourceFactory.eINSTANCE;
		
		List<Datasource> results = new ArrayList<Datasource>();
		
		for(DataSource mondrianDs : faModel.getDataSources()) {
			
			if(mondrianDs instanceof DatasourceOda) {
				
				DatasourceOda fasdDs = (DatasourceOda) mondrianDs;
				
				Datasource utdDatasource = datasourceFactory.createDatasource();
				
				utdDatasource.setDatasourceExtensionId(fasdDs.getOdaDatasourceExtensionId());
				
				utdDatasource.setPublicProperties(fasdDs.getPublicProperties());
				utdDatasource.setPrivateProperties(fasdDs.getPrivateProperties());
				
				utdDatasource.setId(fasdDs.getId());
				utdDatasource.setName(fasdDs.getId());
				
				for(DataObject mondrianObjecttt : fasdDs.getDataObjects()) {
					
					DataObjectOda mondrianObject = (DataObjectOda) mondrianObjecttt;					
					
					bpm.united.olap.api.datasource.DataObject utdDataObj = datasourceFactory.createDataObject();
					
					utdDataObj.setId(mondrianObjecttt.getId());
					utdDataObj.setName(mondrianObject.getName());
					utdDataObj.setQueryText(mondrianObject.getQueryText());
					if(mondrianObject.getDataObjectType().equalsIgnoreCase("fact")) {
						utdDataObj.setIsFact(true);
					}
					
					for(DataObjectItem mondrianItem : mondrianObject.getColumns()) {
						
						bpm.united.olap.api.datasource.DataObjectItem utdDataObjItem = datasourceFactory.createDataObjectItem();
						
						utdDataObjItem.setId(mondrianItem.getId());
						utdDataObjItem.setName(mondrianItem.getOrigin());
						
						utdDataObj.getItems().add(utdDataObjItem);
					}
					
					utdDatasource.getDataObjects().add(utdDataObj);
					utdDataObj.setParent(utdDatasource);
					
				}
				
				results.add(utdDatasource);
				
			}
		}
		
		Datasource utdDatasource = results.get(0);
		
		for(OLAPRelation mondrianRel : faModel.getRelations()) {
			
			Relation utdRelation = datasourceFactory.createRelation();
			
			Operator operator = datasourceFactory.createOperator();
			operator.setType(mondrianRel.getOperator());
			
			boolean leftFinded = false;
			boolean rightFinded = false;
			if (mondrianRel.getName().contains("temps.id")){
			}
			for(Datasource ds : results) {
				
				for(bpm.united.olap.api.datasource.DataObject dataObj : ds.getDataObjects()) {
					
					for(bpm.united.olap.api.datasource.DataObjectItem item : dataObj.getItems()) {
						
//						if(item.getId().equals(mondrianRel.getLeftItemId())) {
//							utdRelation.setLeftItem(item);
//							leftFinded = true;
//						}
//						if(item.getId().equals(mondrianRel.getRightItemId())) {
//							utdRelation.setRightItem(item);
//							rightFinded = true;
//						}
						if(item.getId().equals(mondrianRel.getLeftItemId()) ||
								mondrianRel.getLeftObjectItem() != null && item.getId().equals(mondrianRel.getLeftObjectItem().getId())) {
								utdRelation.setLeftItem(item);
								leftFinded = true;
								break;
							}
						if(item.getId().equals(mondrianRel.getRightItemId())||
								mondrianRel.getRightObjectItem() != null && item.getId().equals(mondrianRel.getRightObjectItem().getId())) {
							utdRelation.setRightItem(item);
							rightFinded = true;
							break;
						}
						if(leftFinded && rightFinded) {
							break;
						}
					}
					
					if(leftFinded && rightFinded) {
						if(utdRelation.getLeftItem().getParent().isIsFact()) {
							utdRelation.getRightItem().setIsKey(true);
						}
						else if(utdRelation.getRightItem().getParent().isIsFact()) {
							utdRelation.getLeftItem().setIsKey(true);
						}
						break;
					}
				}
				if(leftFinded && rightFinded) {
					if(utdRelation.getLeftItem().getParent().isIsFact()) {
						utdRelation.getRightItem().setIsKey(true);
					}
					else if(utdRelation.getRightItem().getParent().isIsFact()) {
						utdRelation.getLeftItem().setIsKey(true);
					}
					break;
				}
			}
			utdDatasource.getRelations().add(utdRelation);
		}
		
		return results;
	}
	
	
	private List<Datasource> createTempDatasources(FAModel faModel) {
		//TODO replace by the true datasources creation
		//TODO temporary transformation mondrian --> oda
		
		DatasourceFactory datasourceFactory = DatasourceFactory.eINSTANCE;
		
		List<Datasource> results = new ArrayList<Datasource>();
		
		for(DataSource mondrianDs : faModel.getDataSources()) {
			
			DataSourceConnection mondrianDriver = mondrianDs.getDriver();
			
			Datasource utdDatasource = datasourceFactory.createDatasource();
			
			utdDatasource.setDatasourceExtensionId("org.eclipse.birt.report.data.oda.jdbc");
			
			Properties publicProps = new Properties();
			publicProps.put("odaURL", mondrianDriver.getUrl());
			publicProps.put("odaDriverClass", mondrianDriver.getDriver());
			publicProps.put("odaPassword", mondrianDriver.getPass());
			publicProps.put("odaUser", mondrianDriver.getUser());
			
			utdDatasource.setPublicProperties(publicProps);
			utdDatasource.setId(mondrianDs.getId());
			utdDatasource.setName(mondrianDs.getId());
			
			for(DataObject mondrianObject : mondrianDs.getDataObjects()) {
				
				bpm.united.olap.api.datasource.DataObject utdDataObj = datasourceFactory.createDataObject();
				
				utdDataObj.setName(mondrianObject.getName());
				utdDataObj.setQueryText(mondrianObject.getSelectStatement());
				
				boolean first = true;
				for(DataObjectItem mondrianItem : mondrianObject.getColumns()) {
					
					bpm.united.olap.api.datasource.DataObjectItem utdDataObjItem = datasourceFactory.createDataObjectItem();
					
					utdDataObjItem.setId(mondrianItem.getId());
					utdDataObjItem.setName(mondrianItem.getName());
					
					if(first) {
						utdDataObjItem.setIsKey(true);
						first = false;
					}
					
					utdDataObj.getItems().add(utdDataObjItem);
				}
				
				utdDatasource.getDataObjects().add(utdDataObj);
				
			}
			
			for(OLAPRelation mondrianRel : faModel.getRelations()) {
				
				Relation utdRelation = datasourceFactory.createRelation();
				
				Operator operator = datasourceFactory.createOperator();
				operator.setType(mondrianRel.getOperator());
				
				for(bpm.united.olap.api.datasource.DataObject dataObj : utdDatasource.getDataObjects()) {
					
					boolean leftFinded = false;
					boolean rightFinded = false;
					for(bpm.united.olap.api.datasource.DataObjectItem item : dataObj.getItems()) {
						
						if(item.getId().equals(mondrianRel.getLeftItemId()) ||
							mondrianRel.getLeftObjectItem() != null && item.getId().equals(mondrianRel.getLeftObjectItem().getId())) {
							utdRelation.setLeftItem(item);
							leftFinded = true;
							break;
						}
						if(item.getId().equals(mondrianRel.getRightItemId())||
								mondrianRel.getRightObjectItem() != null && item.getId().equals(mondrianRel.getRightObjectItem().getId())) {
							utdRelation.setRightItem(item);
							rightFinded = true;
							break;
						}
					}
					
					if(leftFinded && rightFinded) {
						break;
					}
				}
				
				utdDatasource.getRelations().add(utdRelation);
			}
			
			results.add(utdDatasource);
			
		}
		
		return results;
	}
	
	
	/**
	 * create dimensions, measures, cubes,...
	 * @param utdSchema
	 * @param mondrianSchema
	 */
	private void fillSchema(Schema utdSchema, OLAPSchema mondrianSchema, ModelFactory factory) {
		
		for(OLAPDimension mondrianDim : mondrianSchema.getDimensions()) {
			
			bpm.united.olap.api.model.Dimension utdDim = null;
			
				utdDim = factory.createDimension();
				
				utdDim.setCaption(mondrianDim.getCaption());
				utdDim.setName(mondrianDim.getName());
				utdDim.setUname("["+utdDim.getName()+"]");
				utdDim.setIsOneColumnDate(mondrianDim.isOneColumnDate());
				utdDim.setIsDate(mondrianDim.isDate());
				utdDim.setIsGeolocalizable(mondrianDim.isGeolocalisable());
				for(OLAPHierarchy mondrianHiera : mondrianDim.getHierarchies()) {
					
					Hierarchy utdHiera = null;
					
					if(mondrianHiera.getLevels().size() == 1 && mondrianHiera.getLevels().get(0).getClosureParentCol() != null)  {
						createClosureHierarchy(mondrianHiera, mondrianSchema, utdSchema, utdDim);
					}
					
					else {
						utdHiera = factory.createHierarchy();
						utdHiera.setCaption(mondrianHiera.getCaption());
						utdHiera.setName(mondrianHiera.getName());
						utdHiera.setUname("["+utdDim.getName()+"."+utdHiera.getName()+"]");
						utdHiera.setAllMember(mondrianHiera.getAllMember());
						
						Level previousLevel = null;
						for(OLAPLevel mondrianLevel : mondrianHiera.getLevels()) {
							
							Level utdLevel = null;
							
							if(mondrianLevel.isOneColumnDate()) {
								utdLevel = new DateLevel();
								((DateLevel)utdLevel).setDatePart(mondrianLevel.getDateColumnPart());
								((DateLevel)utdLevel).setDateType(mondrianLevel.getDateColumnType());
								((DateLevel)utdLevel).setDatePattern(mondrianLevel.getDateColumnPattern());
							}
							else {
								utdLevel = factory.createLevel();
							}
							
							utdLevel.setCaption(mondrianLevel.getCaption());
							utdLevel.setName(mondrianLevel.getName());
							utdLevel.setUname(utdHiera.getUname()+".["+utdLevel.getName()+"]");
							
							if(mondrianLevel.getProperties() != null && mondrianLevel.getProperties().size() > 0) {
								for(Property prop : mondrianLevel.getProperties()) {
									MemberProperty utdProp = factory.createMemberProperty();
									utdProp.setName(prop.getName());
									utdProp.setType(prop.getType());
									DS:for(Datasource ds : utdSchema.getDatasources()) {
										for(bpm.united.olap.api.datasource.DataObject dataObj : ds.getDataObjects()) {
											for(bpm.united.olap.api.datasource.DataObjectItem doi : dataObj.getItems()) {
												if(doi.getId().equals(prop.getColumn().getId())) {
													utdProp.setValueItem(doi);
													break DS;
												}
											}
										}
									}
									utdLevel.getMemberProperties().add(utdProp);
								}
							}
							
							if(previousLevel != null) {
								utdLevel.setParentLevel(previousLevel);
							}
							previousLevel = utdLevel;
							
							int itemToFind = 1;
							if(mondrianLevel.getOrderItemId() != null) {
								itemToFind++;
							}
							if(mondrianLevel.getColumnLabelId() != null) {
								itemToFind++;
							}
							
							LOOK:for(Datasource ds : utdSchema.getDatasources()) {
								for(bpm.united.olap.api.datasource.DataObject dataObj : ds.getDataObjects()) {
									for(bpm.united.olap.api.datasource.DataObjectItem doi : dataObj.getItems()) {
										if(doi.getId().equals(mondrianLevel.getItem().getId())) {
											utdLevel.setItem(doi);
											utdDim.setDataObject(doi.getParent());
											itemToFind--;
											if(itemToFind <= 0) {
												break LOOK;
											}
										}
										
										if(mondrianLevel.getOrderItemId() != null && doi.getId().equals(mondrianLevel.getOrderItemId())) {
											utdLevel.setOrderItem(doi);
											itemToFind--;
											if(itemToFind <= 0) {
												break LOOK;
											}
										}
										
										if(mondrianLevel.getColumnLabelId() != null && doi.getId().equals(mondrianLevel.getColumnLabelId())) {
											utdLevel.setLabelItem(doi);
											itemToFind--;
											if(itemToFind <= 0) {
												break LOOK;
											}
										}
										
									}
								}
							}
							
							if(utdLevel instanceof DateLevel) {
								if(mondrianLevel.getDateColumnOrderPart() != null && !mondrianLevel.getDateColumnOrderPart().equals("NONE")) {
									((DateLevel)utdLevel).setDateOrder(mondrianLevel.getDateColumnOrderPart());
								}
							}
							
							utdHiera.getLevels().add(utdLevel);
						}
						
						utdDim.getHierarchies().add(utdHiera);
					}
				}
				
				utdSchema.getDimensions().add(utdDim);
			}	
		
		for(OLAPMeasure mondrianMes : mondrianSchema.getMeasures()) {
			Measure utdMes = null;
			if(mondrianMes instanceof OlapDynamicMeasure) {
				utdMes = createDynamicMeasure(utdSchema, (OlapDynamicMeasure) mondrianMes, factory, mondrianSchema);
			}
			else {
				if(mondrianMes.getType().equals("calculated")) {
					utdMes = createCalculatedMeasure(utdSchema, mondrianMes);
				}
				else {
					utdMes = createClassicMeasure(utdSchema, mondrianMes, factory, mondrianSchema);
				}
			}
			
			utdSchema.getMeasures().add(utdMes);
		}
		for(OLAPMeasureGroup g : mondrianSchema.getMeasureGroups()){
			extractMeasureGroup(utdSchema, g, factory, mondrianSchema);
		}
		
		for(org.fasd.olap.OLAPCube mondrianCube : mondrianSchema.getCubes()) {
			
			Cube utdCube = factory.createCube();
			utdCube.setName(mondrianCube.getName());
			
			
			List<OLAPDimension> l = mondrianCube.getDims();
//			final org.fasd.olap.OLAPCube c = mondrianCube; 
//			Collections.sort(l, new Comparator<OLAPDimension>() {
//				public int compare(OLAPDimension arg0, OLAPDimension arg1) {
//					List<OLAPDimension> d = c.getStartupDimensions();
//					Integer i =  new Integer(d.indexOf(arg0));
//					if (i == -1){
//						return 1;
//					}
//					return i.compareTo(d.indexOf(arg1));
//				};
//			});
			
			for(OLAPDimension mondrianDim : l) {
				for(bpm.united.olap.api.model.Dimension utdDim : utdSchema.getDimensions()) {
					if(mondrianDim.getName().replace(".", "_").equals(utdDim.getName())) {
						utdCube.getDimensions().add(utdDim);
						break;
					}
					
				}
			}
			
			for(OLAPMeasure mondrianMes : mondrianCube.getMes()) {
				for(Measure utdMes : utdSchema.getMeasures()) {
					if(mondrianMes.getName().replace(".", "_").equals(utdMes.getName())) {
						utdCube.getMeasures().add(utdMes);
						break;
					}
				}
			}
			
			for(OLAPMeasureGroup g : mondrianCube.getMesGroups()){
				extractMeasureGroup(utdSchema, utdCube, g, factory, mondrianSchema);
			}
			
			for(Datasource ds : utdSchema.getDatasources()) {
				for(bpm.united.olap.api.datasource.DataObject dataObj : ds.getDataObjects()) {
					if(dataObj.getId().equals(mondrianCube.getFactDataObjectId())) {
						utdCube.setFactTable(dataObj);
						break;
					}
				}
			}
			utdSchema.getCubes().add(utdCube);
			utdCube.setParentSchema(utdSchema);
		}
	}
	
	private void extractMeasureGroup(Schema utdSchema, OLAPMeasureGroup group, ModelFactory factory, OLAPSchema mondrianSchema){
		for(OLAPMeasure mondrianMes : group.getMeasures()) {
			Measure utdMes = null;
			if(mondrianMes instanceof OlapDynamicMeasure) {
				utdMes = createDynamicMeasure(utdSchema, (OlapDynamicMeasure) mondrianMes, factory, mondrianSchema);
			}
			else {
				if(mondrianMes.getType().equals("calculated")) {
					utdMes = createCalculatedMeasure(utdSchema, mondrianMes);
				}
				else {
					utdMes = createClassicMeasure(utdSchema, mondrianMes, factory, mondrianSchema);
				}
			}
			utdSchema.getMeasures().add(utdMes);
			for(OLAPGroup c : group.getChilds()){
				extractMeasureGroup(utdSchema, (OLAPMeasureGroup)c, factory, mondrianSchema);
			}
			
		}
	}
	
	private void extractMeasureGroup(Schema schema, Cube utdCube, OLAPMeasureGroup group, ModelFactory factory, OLAPSchema mondrianSchema){
		for(OLAPMeasure mondrianMes : group.getMeasures()) {
	
			for(Measure m : schema.getMeasures()){
				if (m.getName().equals(mondrianMes.getName().replace(".", "_"))){
					utdCube.getMeasures().add(m);
					break;
				}
			}
			
			
			for(OLAPGroup c : group.getChilds()){
				extractMeasureGroup(schema, utdCube, (OLAPMeasureGroup)c, factory, mondrianSchema);
			}
			
		}
	}
	
	/**
	 * Create a unitedOlap dynamic measure
	 * @param utdSchema
	 * @param mondrianMes
	 * @param factory
	 * @param mondrianSchema
	 * @return
	 */
	private Measure createDynamicMeasure(Schema utdSchema, OlapDynamicMeasure mondrianMes, ModelFactory factory, OLAPSchema mondrianSchema) {
		DynamicMeasure utdMes = new DynamicMeasure();
		
		utdMes.setCaption(mondrianMes.getCaption());
		utdMes.setName(mondrianMes.getName());
		utdMes.setUname("[Measures].["+utdMes.getName()+"]");
		utdMes.setCalculationType(mondrianMes.getAggregator());
		utdMes.setCalculatdFormula(mondrianMes.getFormula());
		
		if(mondrianMes.getAggregator().equals("last")) {
			String lastFormula = getLastFormula(mondrianMes, mondrianSchema, true);
			utdMes.setCalculatdFormula(lastFormula);
		}
		
		else if(mondrianMes.getAggregator().equals("first")) {
			String lastFormula = getLastFormula(mondrianMes, mondrianSchema, false);
			utdMes.setCalculatdFormula(lastFormula);
		}
		
		bpm.united.olap.api.datasource.DataObject utdFactDataObject = null;
		
		for(Datasource ds : utdSchema.getDatasources()) {
			for(bpm.united.olap.api.datasource.DataObject dataObj : ds.getDataObjects()) {
				if(dataObj.isIsFact()) {
					utdFactDataObject = dataObj;
					break;
				}
			}
		}
		
		if(mondrianMes.getOriginId() != null && !mondrianMes.getOriginId().equals("")) {
			ClassicAggregation agg = new ClassicAggregation();
			agg.setOriginId(mondrianMes.getOriginId());
			bpm.united.olap.api.datasource.DataObjectItem item = findDataObjectItemForAggregation(agg, utdFactDataObject);
			utdMes.setItem(item);
		}
		
		for(IMeasureAggregation mondAgg : mondrianMes.getAggregations()) {
			ILevelAggregation utdAgg = null;
			
			if(mondAgg instanceof ClassicAggregation) {
				utdAgg = new bpm.united.olap.api.model.aggregation.ClassicAggregation();
				((bpm.united.olap.api.model.aggregation.ClassicAggregation)utdAgg).setAggregator(((ClassicAggregation)mondAgg).getAggregator());
				((bpm.united.olap.api.model.aggregation.ClassicAggregation)utdAgg).setLevel(((ClassicAggregation)mondAgg).getLevel());
				
				bpm.united.olap.api.datasource.DataObjectItem item = findDataObjectItemForAggregation(mondAgg, utdFactDataObject);
				((bpm.united.olap.api.model.aggregation.ClassicAggregation)utdAgg).setOrigin(item);
			}
			
			else if(mondAgg instanceof LastAggregation) {
				utdAgg = new bpm.united.olap.api.model.aggregation.LastAggregation();
				((bpm.united.olap.api.model.aggregation.LastAggregation)utdAgg).setAggregator(((LastAggregation)mondAgg).getAggregator());
				((bpm.united.olap.api.model.aggregation.LastAggregation)utdAgg).setLevel(((LastAggregation)mondAgg).getLevel());
				((bpm.united.olap.api.model.aggregation.LastAggregation)utdAgg).setRelatedDimension(((LastAggregation)mondAgg).getRelatedDimension());
				
				bpm.united.olap.api.datasource.DataObjectItem item = findDataObjectItemForAggregation(mondAgg, utdFactDataObject);
				((bpm.united.olap.api.model.aggregation.LastAggregation)utdAgg).setOrigin(item);
			}
			
			else if(mondAgg instanceof CalculatedAggregation) {
				utdAgg = new bpm.united.olap.api.model.aggregation.CalculatedAggregation();
				((bpm.united.olap.api.model.aggregation.CalculatedAggregation)utdAgg).setFormula(((CalculatedAggregation)mondAgg).getFormula());
				((bpm.united.olap.api.model.aggregation.CalculatedAggregation)utdAgg).setLevel(((CalculatedAggregation)mondAgg).getLevel());
			}
			
			utdMes.addAggregation(utdAgg);
		}
		
		
		return utdMes;
	}
	
	private bpm.united.olap.api.datasource.DataObjectItem findDataObjectItemForAggregation(IMeasureAggregation mondAgg,	bpm.united.olap.api.datasource.DataObject utdFactDataObject) {
		bpm.united.olap.api.datasource.DataObjectItem result = null;
		
		String itemId = null;
		if(mondAgg instanceof ClassicAggregation) {
			itemId = ((ClassicAggregation)mondAgg).getOriginId();
		}
		else if(mondAgg instanceof LastAggregation) {
			itemId = ((LastAggregation)mondAgg).getOriginId();
		}
		
		for(bpm.united.olap.api.datasource.DataObjectItem uIt : utdFactDataObject.getItems()) {
			if(uIt.getId().equals(itemId)) {
				result = uIt;
				break;
			}
		}
		
		return result;
	}

	private Measure createClassicMeasure(Schema utdSchema, OLAPMeasure mondrianMes, ModelFactory factory, OLAPSchema mondrianSchema) {
		Measure utdMes = factory.createMeasure();
		utdMes.setCaption(mondrianMes.getCaption());
		utdMes.setName(mondrianMes.getName());
		utdMes.setUname("[Measures].["+utdMes.getName()+"]");
		utdMes.setCalculationType(mondrianMes.getAggregator());
		
		if(mondrianMes.getAggregator().equals("last")) {
			String lastFormula = getLastFormula(mondrianMes, mondrianSchema, true);
			utdMes.setCalculatdFormula(lastFormula);
			utdMes.setLastDimensionName(mondrianMes.getLastTimeDimensionName());
		}
		
		else if(mondrianMes.getAggregator().equals("first")) {
			String lastFormula = getLastFormula(mondrianMes, mondrianSchema, false);
			utdMes.setCalculatdFormula(lastFormula);
			utdMes.setLastDimensionName(mondrianMes.getLastTimeDimensionName());
		}
		
		boolean finded = false;
		for(Datasource ds : utdSchema.getDatasources()) {
			for(bpm.united.olap.api.datasource.DataObject dataObj : ds.getDataObjects()) {
				for(bpm.united.olap.api.datasource.DataObjectItem doi : dataObj.getItems()) {
					if(doi.getName().equals(mondrianMes.getOrigin().getOrigin())) {
						utdMes.setItem(doi);
						finded = true;
						break;
					}
				}
				if(finded) {
					break;
				}
			}
			if(finded) {
				break;
			}
		}
		return utdMes;
	}
	
	private bpm.united.olap.api.model.Hierarchy createClosureHierarchy(OLAPHierarchy mondrianHiera, OLAPSchema mondrianSchema, Schema utdSchema2, bpm.united.olap.api.model.Dimension utdDim) {
		
		ClosureHierarchy closHiera = new ClosureHierarchy();
		
		closHiera.setCaption(mondrianHiera.getCaption());
		closHiera.setName(mondrianHiera.getName());
		closHiera.setUname("["+utdDim.getName()+"."+closHiera.getName()+"]");
		
		OLAPLevel clLvl = mondrianHiera.getLevels().get(0);
		DataObjectItem parentItem = null;
		DataObjectItem childItem = null;
		parentItem = clLvl.getClosureParentCol();
		childItem = clLvl.getClosureChildCol();
		
		DataObjectItem lvlIt = clLvl.getItem();
		
		ClosureLevel closureLevel = new ClosureLevel();
		
		DS:for(Datasource ds : utdSchema2.getDatasources()) {
			for(bpm.united.olap.api.datasource.DataObject dObj : ds.getDataObjects()) {
				for(bpm.united.olap.api.datasource.DataObjectItem itm : dObj.getItems()) {
					if(parentItem.getId().equals(itm.getId())) {
						closHiera.setParentItem(itm);
					}
					if(childItem.getId().equals(itm.getId())) {
						closHiera.setChildItem(itm);
					}
					if(lvlIt.getId().equals(itm.getId())) {
						utdDim.setDataObject(itm.getParent());
						closHiera.setItem(itm);
					}
					
					if(closHiera.getChildItem() != null && closHiera.getItem() != null && closHiera.getParentItem() != null) {
						break DS;
					}
				} 
			}
		}
		
		utdDim.getHierarchies().add(closHiera);
		closureLevel.setParentItem(closHiera.getParentItem());
		closureLevel.setChildItem(closHiera.getChildItem());
		closureLevel.setParentHierarchy(closHiera);
		closureLevel.setItem(closHiera.getItem());
		closureLevel.setName(clLvl.getName());
		closureLevel.setCaption(clLvl.getCaption());
		closureLevel.setUname(closHiera.getUname() + ".[" + closureLevel.getName() + "]");
		closHiera.getLevels().add(closureLevel);
		
		return closHiera;
	}





	private String getLastFormula(OLAPMeasure mondrianMes, OLAPSchema mondrianSchema, boolean last) {
		String res = "";
		for(OLAPDimension d : mondrianSchema.getDimensions()){
			try{
				if (d.getName().equals(mondrianMes.getLastTimeDimensionName())){
					OLAPLevel l = d.getHierarchies().get(0).getLevels().get(d.getHierarchies().get(0).getLevels().size() - 1);
					
					String function = "";
					if(last) {
						function = "ClosingPeriod";
					}
					else {
						function = "OpeningPeriod";
					}
					
					res = "([Measures].[" + mondrianMes.getName().replace(".", "_") +  "]," + function + "([" + d.getName().replace(".", "_") +  "."+ "" + d.getHierarchies().get(0).getName().replace(".", "_") + "]" + ".[" + l.getName().replace(".", "_") + "],[" + d.getName().replace(".", "_") + "].CurrentMember))";
					break;
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		
		
		return res;
	}
	private Measure createCalculatedMeasure(Schema utdSchema, OLAPMeasure mondrianMes) {
		
		Measure mes = new CalculatedMeasure();
		mes.setName(mondrianMes.getName());
		mes.setUname("[Measures].["+mes.getName()+"]");
		mes.setCaption(mondrianMes.getCaption());
		mes.setCalculatdFormula(mondrianMes.getFormula());
		
		return mes;
	}
	public IPreloadConfig getPreloadConfig() {
		return config;
	}
}
