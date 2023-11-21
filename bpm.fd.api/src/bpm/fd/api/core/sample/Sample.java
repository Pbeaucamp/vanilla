package bpm.fd.api.core.sample;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.FdProjectDescriptor;
import bpm.fd.api.core.model.FdVanillaFormModel;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.buttons.ButtonOptions;
import bpm.fd.api.core.model.components.definition.buttons.ComponentButtonDefinition;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.filter.DropDownOptions;
import bpm.fd.api.core.model.components.definition.filter.FilterData;
import bpm.fd.api.core.model.components.definition.filter.FilterOptions;
import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.api.core.model.components.definition.text.LabelComponent;
import bpm.fd.api.core.model.components.definition.text.LabelOptions;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.core.model.resources.FileProperties;
import bpm.fd.api.core.model.resources.exception.DictionaryException;
import bpm.fd.api.core.model.structure.Cell;
import bpm.fd.api.core.model.structure.FactoryStructure;
import bpm.fd.api.core.model.structure.Table;
import bpm.fd.api.core.model.tools.ModelLoader;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class Sample {

	
	private static FdProject createProject(){
		/*
		 * create The ProjectDescriptor
		 */
		FdProjectDescriptor projectDescriptor = new FdProjectDescriptor();
		projectDescriptor.setAuthor("LCA");
		projectDescriptor.setDictionaryName("sampleDictionary");
		projectDescriptor.setModelName("sampleModel");
		projectDescriptor.setProjectName("sampleProject2");
		
		/*
		 * create the Project with a VanillaFormModel
		 */
		FdProject project = new MultiPageFdProject(projectDescriptor, new FdVanillaFormModel(new FactoryStructure()));
		
		return project;
	}
	
	
	private static void createDictionaryContent(Dictionary dictionary){
		/*
		 * create a simpleInlineDataSOurce
		 */
		Properties publicProp = new Properties();
		publicProp.setProperty("P_COLUMN_TYPE", "Integer;String;Boolean;");
		publicProp.setProperty("P_COLUMN_VALUES", "1;True;false;2;False;true;");
		publicProp.setProperty("P_COLUMN_NAMES", "NUMBER;ANSWER;ISRIGHT;");
		publicProp.setProperty("P_COLUMN_NUMBER", "3");
		
		Properties privateProp = new Properties();
		
		DataSource dataSourceInline1 = new DataSource(dictionary, "inlineQuestion1DataSource", "bpm.inlinedatas.oda.driver.runtime", "bpm.inlinedatas.oda.driver.runtime", publicProp, privateProp);
		
		/*
		 * add the DataSource to the projectDictionary
		 */
		try {
			dictionary.addDataSource(dataSourceInline1);
		} catch (DictionaryException e2) {
			
			e2.printStackTrace();
		}
		
		/*
		 * create a DataSet on the previously created DataSOurce
		 */
		publicProp = new Properties();
		privateProp = new Properties();
		
		DataSet dataSetInline1 = new DataSet("inlineDataSet1", "", "", publicProp, privateProp, "NUMBER;ANSWER;ISRIGHT;:++No filter++:", dataSourceInline1);
		
		//build the DataSet description
		try {
			dataSetInline1.buildDescriptor(dataSourceInline1);
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		/*
		 * add The DataSet to the Dictionary
		 */
		try {
			dictionary.addDataSet(dataSetInline1);
		} catch (DictionaryException e1) {
			
			e1.printStackTrace();
		}
		
		
		/*
		 * Component Label Creation
		 */
		LabelComponent labelComponent = new LabelComponent("question1", dictionary);
		((LabelOptions)labelComponent.getOptions(LabelOptions.class)).setText("Is Fd really a nice tool?");
		
		try {
			dictionary.addComponent(labelComponent);
		} catch (DictionaryException e) {
			e.printStackTrace();
		}
		
		/*
		 * ComponentFilter Creation
		 */
		ComponentFilterDefinition filterComponent = new ComponentFilterDefinition("answer1", dictionary);
		
		// define the renderer Type for this filter component
		try {
			filterComponent.setRenderer(FilterRenderer.getRenderer(FilterRenderer.DROP_DOWN_LIST_BOX));
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		// create FilterComponent Datas
		FilterData filterDatas = new FilterData();
		filterDatas.setDataSet(dataSetInline1);
		filterDatas.setColumnLabelIndex(2);
		filterDatas.setColumnValueIndex(1);
		filterComponent.setComponentDatas(filterDatas);
		
		((DropDownOptions)filterComponent.getOptions(DropDownOptions.class)).setSize(1);
		((FilterOptions)filterComponent.getOptions(FilterOptions.class)).setInitParameterWithFirstValue(true);
		((FilterOptions)filterComponent.getOptions(FilterOptions.class)).setSubmitOnChange(false);
		
		
		try {
			dictionary.addComponent(filterComponent);
		} catch (DictionaryException e) {
			
			e.printStackTrace();
		}
		
		/*
		 * create a submit Button
		 */
		
		ComponentButtonDefinition buttonComponent = new ComponentButtonDefinition("submitButton", dictionary);
		((ButtonOptions)buttonComponent.getOptions(ButtonOptions.class)).setLabel("Submit Forms");
		
		try {
			dictionary.addComponent(buttonComponent);
		} catch (DictionaryException e) {
			
			e.printStackTrace();
		}
	}
	
	
	private static void createFdForm(FdProject project){
		/*
		 * create a Main Table container to hold all our component inside the VanillaForm 
		 * by default a Table has One row and one column (just one Cell)
		 */
		
		FactoryStructure structureFactory = new FactoryStructure();
		
		Table mainTable = structureFactory.createTable("mainTable");
		mainTable.addDetailsRow(structureFactory);
		//add our table to our VanillaFormModel
		project.getFdModel().addToContent(mainTable);
		
		// add Column to the table
		mainTable.addColumn(structureFactory);
		
		/*
		 * add Components to our table (current table size 1 row, 2 columns)
		 */
		List<Cell> firstRow = mainTable.getDetailsRows().get(0);
		Cell c00 = firstRow.get(0);
		
		// add the Label named question1
		c00.addBaseElementToContent(project.getDictionary().getComponent("question1"));
		
		// add the filter named answer1
		Cell c01 = firstRow.get(1);
		c01.addBaseElementToContent(project.getDictionary().getComponent("answer1"));
		
		
		/*
		 * create a new Row to store our submit button
		 */
		
		mainTable.addDetailsRow(structureFactory);
		
		/*
		 * mainTable has now 2 columns and 2 rows
		 * we only wnat a single column on the second row  
		 */
		List<Cell> row1 = mainTable.getDetailsRows().get(1);
		mainTable.mergeHorizontalCells(structureFactory, 1, row1);
		
		/*
		 * we add our Submit Button to the mainTable
		 */
		row1 = mainTable.getDetailsRows().get(1);
		Cell c11 = row1.get(0);
		
		c11.addBaseElementToContent(project.getDictionary().getComponent("submitButton"));
		
		
	}
	
	private static void saveOnRepository(String folderPath, FdProject project, IRepositoryApi sock, RepositoryDirectory directory) throws Exception{
		// create a specific file for internationalization
		// do not forget it or all component wont have any label

		
		Properties prop = new Properties();
		for(IComponentDefinition def : project.getDictionary().getComponents()){
			for(IComponentOptions opt : def.getOptions()){
				for(String s : opt.getInternationalizationKeys()){
					try{
						prop.setProperty(def.getName() + "." + s, opt.getDefaultLabelValue(s));
					}catch(Exception ex){
						ex.printStackTrace();
						System.err.println(def.getName());
					}
				}
			}
			
		}
		
		File f = new File(folderPath +"/component.properties");
		prop.save(new FileOutputStream(f), "");
		FileProperties propertiesFile = new FileProperties("component.properties", Locale.getDefault().getDisplayName(), f);
		
		project.addResource(propertiesFile);
		
		
		ModelLoader.save(project, sock, directory, "System", project.getProjectDescriptor().getProjectName(), project.getProjectDescriptor().getDictionaryName());
		
		//delete the file
		f.delete();
		
	}
	
	
	
	
	
	/**
	 * @param args
	 */
	public static void runSample() {
		
		// create out project
		FdProject vanillaFormProject = createProject();
		
		// create component and datas in the dictionary
		createDictionaryContent(vanillaFormProject.getDictionary());
		
		//create the dashboard structure and fill it with dictionary's components
		createFdForm(vanillaFormProject);
		
		
		//save on Repository
		
//		
//		IRepositoryConnection sock = FactoryRepository.getInstance().getConnection(FactoryRepository.AXIS_CLIENT, "http://localhost:8080/BIRepository", "system", "system", null, 1);
//		IRepository rep;
//		try {
//			rep = sock.getRepository();
//			IDirectory target = rep.getDirectories().get(0);
//			saveOnRepository("c:/tmp/", vanillaFormProject, sock, target);
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
		
		
		
		
		
		
		
	}

}
