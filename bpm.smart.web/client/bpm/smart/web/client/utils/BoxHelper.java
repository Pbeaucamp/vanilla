package bpm.smart.web.client.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.panels.CollapsePanel;
import bpm.gwt.commons.client.panels.StackNavigationPanel;
import bpm.gwt.workflow.commons.client.IWorkflowAppManager;
import bpm.gwt.workflow.commons.client.I18N.LabelsCommon;
import bpm.gwt.workflow.commons.client.workflow.ToolBox;
import bpm.gwt.workflow.commons.client.workflow.ToolBoxCategory;
import bpm.gwt.workflow.commons.client.workflow.ToolBoxFolder;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.images.Images;
import bpm.workflow.commons.beans.TypeActivity;

import com.google.gwt.resources.client.ImageResource;

public class BoxHelper {
	public static final String TYPE_TOOL = "TypeTool";

	public static final TypeActivity[] TOOLS = { TypeActivity.RSCRIPT, TypeActivity.RECODE, TypeActivity.OUTPUT_FILE, TypeActivity.CHART, TypeActivity.FIELD_SELECTION, TypeActivity.HEAD };

	public static final String CAT_DATA = "Data";
	public static final String CAT_DATA_MANIPULATION = "Data Manipulation";
	public static final String CAT_ALGORITHMS = "Algorithms";
	public static final String CAT_VISUALIZATION = "Visualization";

	public enum Category { 
		DATA(CAT_DATA), 
		DATA_MANIPULATION(CAT_DATA_MANIPULATION), 
		ALGORITHMS(CAT_ALGORITHMS), 
		VISUALIZATION(CAT_VISUALIZATION);
		
		private String name;
		
		private Category(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	};
	
	public static final String DATASET_AIR = "Dataset Air";
	public static final String DATA_IMPORT = "Data Import";
	public static final String DATA_SAVE = "Data Save";
	
	public static final String MANIP_SELECTION = "Data Selection";
	public static final String MANIP_PROCESSING = "Data Processing";
	
	public static final String FIELD_SELECTION = "Field Selection";
	public static final String FIELD_DETERMINATION = "Field Determination";
	public static final String SAMPLING = "Sampling";
	
	public static final String RECODE = "Recode";
	public static final String DISCRETIZATION = "Discretization";
	public static final String MISSING_VALUE = "Missing value";
	public static final String MATRIX = "Matrix";
	
	public static final String CLASSIFICATION = "Classification";
	public static final String CLUSTERING = "Clustering";
	public static final String DEPENDANCE_CORRELATION = "Dependance Correlation";
	public static final String FACTOR_ANALYSIS = "Factor Analysis";
	public static final String TIME_SERIES = "Time Series";
	public static final String SCORING = "Scoring";
	public static final String ASSOCIATION_RULES = "Association Rules";
	public static final String FUZZY_SET = "Fuzzy Set";
	public static final String TEXT_MINING = "Text Mining";
	public static final String PERSONAL_METHODS = "Personal Methods";
	
	public static final String DESCRIPTIVE = "Descriptive";
	public static final String DISTRIBUTION = "Distribution";

	public enum Folder { 
		DATA_DATASET_AIR(DATASET_AIR), 
		DATA_DATA_IMPORT(DATA_IMPORT), 
		DATA_DATA_SAVE(DATA_SAVE),
		
		DATA_MANIP_SELECTION(MANIP_SELECTION), 
		DATA_MANIP_SELECTION_FIELD_SELECTION(FIELD_SELECTION),
		DATA_MANIP_SELECTION_FIELD_DETERMINATION(FIELD_DETERMINATION),
		DATA_MANIP_SELECTION_SAMPLING(SAMPLING),
		
		DATA_MANIP_PROCESSING(MANIP_PROCESSING),
		DATA_MANIP_PROCESSING_RECODE(RECODE), 
		DATA_MANIP_PROCESSING_DISCRETIZATION(DISCRETIZATION),
		DATA_MANIP_PROCESSING_MISSING_VALUE(MISSING_VALUE), 
		DATA_MANIP_PROCESSING_MATRIX(MATRIX),
		
		ALGORITHMS_CLASSIFICATION(CLASSIFICATION),
		ALGORITHMS_CLUSTERING(CLUSTERING),
		ALGORITHMS_DEPENDANCE_CORRELATION(DEPENDANCE_CORRELATION),
		ALGORITHMS_FACTOR_ANALYSIS(FACTOR_ANALYSIS),
		ALGORITHMS_TIME_SERIES(TIME_SERIES),
		ALGORITHMS_SCORING(SCORING),
		ALGORITHMS_ASSOCIATION_RULES(ASSOCIATION_RULES),
		ALGORITHMS_FUZZY_SET(FUZZY_SET),
		ALGORITHMS_TEXT_MINING(TEXT_MINING),
		ALGORITHMS_PERSONAL_METHODS(PERSONAL_METHODS),
		
		VISUALIZATION_DESCRIPTIVE(DESCRIPTIVE), 
		VISUALIZATION_DISTRIBUTION(DISTRIBUTION) 
		
		;
		
		
		
		private String name;
		
		private Folder(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	};

	public static List<StackNavigationPanel> createCategories(IWorkflowAppManager appManager, CollapsePanel collapsePanel) {
		List<StackNavigationPanel> cats = new ArrayList<>();
		boolean first = true;
		for (Category category : Category.values()) {
			cats.add(new ToolBoxCategory(collapsePanel, category.getName(), first, buildFolders(appManager, category)));
			first = false;
		}
		return cats;
	}

	private static List<ToolBoxFolder> buildFolders(IWorkflowAppManager appManager, Category category) {
		List<ToolBoxFolder> folders = new ArrayList<>();
		
		switch (category) {
		case DATA:
			folders.add(new ToolBoxFolder(Folder.DATA_DATASET_AIR.getName(), null, buildTools(appManager, Folder.DATA_DATASET_AIR), 1));
			folders.add(new ToolBoxFolder(Folder.DATA_DATA_IMPORT.getName(), null, buildTools(appManager, Folder.DATA_DATA_IMPORT), 1));
			folders.add(new ToolBoxFolder(Folder.DATA_DATA_SAVE.getName(), null, buildTools(appManager, Folder.DATA_DATA_SAVE), 1));
			break;
		case DATA_MANIPULATION:
			folders.add(new ToolBoxFolder(Folder.DATA_MANIP_SELECTION.getName(), null, buildTools(appManager, Folder.DATA_MANIP_SELECTION), 1));
			folders.add(new ToolBoxFolder(Folder.DATA_MANIP_PROCESSING.getName(), buildFolders(appManager, Folder.DATA_MANIP_PROCESSING, 2), null, 1));
			break;
		case ALGORITHMS:
			folders.add(new ToolBoxFolder(Folder.ALGORITHMS_TIME_SERIES.getName(), null, buildTools(appManager, Folder.ALGORITHMS_TIME_SERIES), 1));
			folders.add(new ToolBoxFolder(Folder.ALGORITHMS_CLUSTERING.getName(), null, buildTools(appManager, Folder.ALGORITHMS_CLUSTERING), 1));
			folders.add(new ToolBoxFolder(Folder.ALGORITHMS_DEPENDANCE_CORRELATION.getName(), null, buildTools(appManager, Folder.ALGORITHMS_DEPENDANCE_CORRELATION), 1));
			folders.add(new ToolBoxFolder(Folder.ALGORITHMS_CLASSIFICATION.getName(), null, buildTools(appManager, Folder.ALGORITHMS_CLASSIFICATION), 1));
			break;
		case VISUALIZATION:
			folders.add(new ToolBoxFolder(Folder.VISUALIZATION_DESCRIPTIVE.getName(), null, buildTools(appManager, Folder.VISUALIZATION_DESCRIPTIVE), 1));
			folders.add(new ToolBoxFolder(Folder.VISUALIZATION_DISTRIBUTION.getName(), null, buildTools(appManager, Folder.VISUALIZATION_DISTRIBUTION), 1));
			break;

		default:
			break;
		}
		return folders;
	}

	private static List<ToolBoxFolder> buildFolders(IWorkflowAppManager appManager, Folder folder, int level) {
		List<ToolBoxFolder> folders = new ArrayList<>();
		
		switch (folder) {
		case DATA_MANIP_PROCESSING:
			folders.add(new ToolBoxFolder(Folder.DATA_MANIP_PROCESSING_RECODE.getName(), null, buildTools(appManager, Folder.DATA_MANIP_PROCESSING_RECODE), level));
			folders.add(new ToolBoxFolder(Folder.DATA_MANIP_PROCESSING_DISCRETIZATION.getName(), null, buildTools(appManager, Folder.DATA_MANIP_PROCESSING_DISCRETIZATION), level));
			folders.add(new ToolBoxFolder(Folder.DATA_MANIP_PROCESSING_MISSING_VALUE.getName(), null, buildTools(appManager, Folder.DATA_MANIP_PROCESSING_MISSING_VALUE), level));
			folders.add(new ToolBoxFolder(Folder.DATA_MANIP_PROCESSING_MATRIX.getName(), null, buildTools(appManager, Folder.DATA_MANIP_PROCESSING_MATRIX), level));
			break;

		default:
			break;
		}
		return folders;
	}

	private static List<ToolBox> buildTools(IWorkflowAppManager appManager, Folder folder) {
		List<ToolBox> tools = new ArrayList<>();
		
		switch (folder) {
		case DATA_DATASET_AIR:
			tools.add(new ToolBox(appManager, TypeActivity.RSCRIPT));
			break;
		case DATA_DATA_SAVE:
			tools.add(new ToolBox(appManager, TypeActivity.OUTPUT_FILE));
			break;
		case DATA_MANIP_SELECTION:
			tools.add(new ToolBox(appManager, TypeActivity.FIELD_SELECTION));
			break;
		case DATA_MANIP_PROCESSING_RECODE:
			tools.add(new ToolBox(appManager, TypeActivity.RECODE));
			tools.add(new ToolBox(appManager, TypeActivity.FILTER_ACTIVITY));
			tools.add(new ToolBox(appManager, TypeActivity.SORTING_ACTIVITY));
			break;
		case ALGORITHMS_TIME_SERIES:
			tools.add(new ToolBox(appManager, TypeActivity.SIMPLE_LINEAR_REG));
			break;
		case ALGORITHMS_CLUSTERING:
			tools.add(new ToolBox(appManager, TypeActivity.HAC));
			tools.add(new ToolBox(appManager, TypeActivity.KMEANS));
			break;
		case ALGORITHMS_CLASSIFICATION:
			tools.add(new ToolBox(appManager, TypeActivity.DECISION_TREE));
			break;
		case ALGORITHMS_DEPENDANCE_CORRELATION:
			tools.add(new ToolBox(appManager, TypeActivity.CORRELATION_MATRIX));
			break;
		case VISUALIZATION_DESCRIPTIVE:
			tools.add(new ToolBox(appManager, TypeActivity.HEAD));
			break;
		case VISUALIZATION_DISTRIBUTION:
			tools.add(new ToolBox(appManager, TypeActivity.CHART));
			break;
		default:
			break;
		}
		return tools;
	}

	public static ImageResource getImage(TypeActivity type, boolean black) {
		switch (type) {
		case START:
			return bpm.gwt.workflow.commons.client.images.Images.INSTANCE.start_24dp();
		case STOP:
			return bpm.gwt.workflow.commons.client.images.Images.INSTANCE.stop_24dp();
		case RSCRIPT:
			return black ? Images.INSTANCE.ic_rscript_black_24dp() : Images.INSTANCE.ic_rscript_white_24dp();
		case RECODE:
			return black ? Images.INSTANCE.ic_rscript_black_24dp() : Images.INSTANCE.ic_rscript_white_24dp();
		case OUTPUT_FILE:
			return black ? Images.INSTANCE.output_file() : Images.INSTANCE.ic_my_library_books_white_24dp();
		case CHART:
			return black ? Images.INSTANCE.chart_activity() : Images.INSTANCE.ic_my_library_books_white_24dp();
		case FIELD_SELECTION:
			return black ? Images.INSTANCE.selection_activity() : Images.INSTANCE.ic_my_library_books_white_24dp();
		case HEAD:
			return black ? Images.INSTANCE.ic_my_library_books_black_24dp() : Images.INSTANCE.ic_my_library_books_white_24dp();
		case SORTING_ACTIVITY:
			return black ? Images.INSTANCE.sort_activity() : Images.INSTANCE.ic_my_library_books_white_24dp();
		case FILTER_ACTIVITY:
			return black ? Images.INSTANCE.ic_my_library_books_black_24dp() : Images.INSTANCE.ic_my_library_books_white_24dp();
		case SIMPLE_LINEAR_REG:
			return black ? Images.INSTANCE.ic_simple_lin_reg_black_36() : Images.INSTANCE.ic_simple_lin_reg_white_36();
		case HAC:
			return Images.INSTANCE.ic_hac_black_36();
		case KMEANS:
			return Images.INSTANCE.ic_kmeans_black_36();
		case DECISION_TREE:
			return Images.INSTANCE.ic_hac_black_36();
		case CORRELATION_MATRIX:
			return Images.INSTANCE.ic_cormatrix_black_36();
		default:
			return bpm.gwt.workflow.commons.client.images.Images.INSTANCE.stop_24dp();
		}
	}

	public static String getLabel(TypeActivity type) {
		switch (type) {
		case START:
			return LabelsCommon.lblCnst.Start();
		case STOP:
			return LabelsCommon.lblCnst.Stop();
		case RSCRIPT:
			return LabelsConstants.lblCnst.AirScript();
		case RECODE:
			return LabelsConstants.lblCnst.ActivityRecode();
		case OUTPUT_FILE:
			return LabelsConstants.lblCnst.OutputFile();
		case CHART:
			return LabelsConstants.lblCnst.ActivityChart();
		case FIELD_SELECTION:
			return LabelsConstants.lblCnst.ActivityFieldSelection();
		case HEAD:
			return LabelsConstants.lblCnst.ActivityHead();
		case SORTING_ACTIVITY:
			return LabelsConstants.lblCnst.ActivitySorting();
		case FILTER_ACTIVITY:
			return LabelsConstants.lblCnst.ActivityFilter();
		case SIMPLE_LINEAR_REG:
			return LabelsConstants.lblCnst.ActivitySimpleLinearReg();
		case HAC:
			return LabelsConstants.lblCnst.ActivityHACClustering();
		case KMEANS:
			return LabelsConstants.lblCnst.ActivityKMeans();
		case CORRELATION_MATRIX:
			return LabelsConstants.lblCnst.ActivityCorrelationMatrix();
		case DECISION_TREE:
			return LabelsConstants.lblCnst.ActivityDecisionTree();
		default:
			return LabelsCommon.lblCnst.Unknown();
		}
	}
}
