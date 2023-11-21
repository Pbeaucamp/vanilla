package org.fasd.i18N;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.fasd.preferences.PreferenceConstants;
import org.freeolap.FreemetricsPlugin;

/**
 * @author Belgarde
 * 
 */
public class LanguageText extends NLS {

	private static final String BUNDLE_NAME = "org.fasd.i18N.LanguagesBundle"; //$NON-NLS-1$

	public static String DialogNew_21;

	public static String ActionAddConnection_Add_DatasSource;

	public static String ActionAddConnectionODA;

	public static String ActionAddDatasetODA;

	public static String ActionEditConnectionODA;

	public static String ActionEditDatasetODA;

	public static String ActionEditMeasure_0;

	public static String ActionEditOneColumnDateDim_0;

	public static String ActionAddConnection_Create_or_Open_Schema;

	public static String ActionAddConnection_Information;

	public static String ActionAddConnection_New_Connection;

	public static String ActionAddCube_Add_Cube;

	public static String ActionAddCube_Create_or_Open_Schema;

	public static String ActionAddCube_information;

	public static String ActionAddCube_New_Cube;

	public static String ActionAddDimension_Add_Dimension;

	public static String ActionAddDimension_Create_or_Open_Schema;

	public static String ActionAddDimension_Information;

	public static String ActionAddDimension_New_Dimension;

	public static String ActionAddDimGrp_Add_Dimension_Group;

	public static String ActionAddDimGrp_Add_DimensionGroup;

	public static String ActionAddDimGrp_Create_Or_open_Schema;

	public static String ActionAddDimGrp_Information;

	public static String ActionAddDimGrp_New_DimensionGroup;

	public static String ActionAddDimGrp_New_Group;

	public static String ActionAddDimGrp_Sel_a_Cube_To_Add_To_Gp;

	public static String ActionAddDimGrp_Warning;

	public static String ActionAddDimUsage_Add_Dimension_Usage;

	public static String ActionAddDimUsage_Create_or_Open_Schema;

	public static String ActionAddDimUsage_Information;

	public static String ActionAddDimUsage_New_Dimension_Usage;

	public static String ActionAddHiera_Add_Hierarchy;

	public static String ActionAddHiera_Create_Or_Open_Schema;

	public static String ActionAddHiera_Information;

	public static String ActionAddHiera_New_Hierarchy;

	public static String ActionAddLevel_Add_Level;

	public static String ActionAddLevel_Create_Or_Open_Schema;

	public static String ActionAddLevel_Information;

	public static String ActionAddLevel_New_Level;

	public static String ActionAddMeasure_Add_Calculated_Measure;

	public static String ActionAddMeasure_Add_Measure;

	public static String ActionAddMeasureUsage_Add_MeasureUsage;

	public static String ActionAddMeasureUsage_Create_a_New_Schema;

	public static String ActionAddMeasureUsage_Information;

	public static String ActionAddMeasureUsage_New_Measure_Usage;

	public static String ActionAddMesGrp_Add_Measure_Group;

	public static String ActionAddMesGrp_Create_Or_Open_a_Schema;

	public static String ActionAddMesGrp_Information;

	public static String ActionAddMesGrp_New_Group;

	public static String ActionAddMesGrp_New_MeasureGroup;

	public static String ActionAddMesGrp_Select_a_Cube_To_Add;

	public static String ActionAddMesGrp_Warning;

	public static String ActionAddOneColumnTimeDim_0;

	public static String ActionAddOneColumnTimeDim_1;

	public static String ActionAddOneColumnTimeDim_2;

	public static String ActionAddTableToDataSource_Create_Shema;

	public static String ActionAddTableToDataSource_Information;

	public static String ActionAddTableToDataSource_New_Table;

	public static String ActionAddVirtual_New;

	public static String ActionBrowseColumn_0;

	public static String ActionBrowseColumn_Check_DS_Conn;

	public static String ActionBrowseColumn_Content_of;

	public static String ActionBrowseColumn_Driver_Name;

	public static String ActionBrowseColumn_Error;

	public static String ActionBrowseColumn_File_Not_Found;

	public static String ActionBrowseColumn_unable_Load;

	public static String ActionBrowseTable_10;

	public static String ActionBrowseTable_11;

	public static String ActionBrowseTable_12;

	public static String ActionBrowseTable_13;

	public static String ActionBrowseTable_7;

	public static String ActionBrowseTable_Browse_Table;

	public static String ActionBrowseTable_Chek_DS;

	public static String ActionBrowseTable_Driver_No_set;

	public static String ActionBrowseTable_Error;

	public static String ActionBrowseTable_File_Not_Found;

	public static String ActionBrowseTable_Information;

	public static String ActionBrowseTable_Is_empty;

	public static String ActionBrowseTable_Table;

	public static String ActionBrowseTable_unable_Load_Driver;

	public static String ActionBuildMolap_Build_Molap;

	public static String ActionBuildMolap_Build_Molap_DS;

	public static String ActionBuildMolap_Create_Cube_DS;

	public static String ActionBuildMolap_Cube_Location;

	public static String ActionBuildMolap_Error;

	public static String ActionBuildMolap_Verify;

	public static String ActionCheckCube_0;

	public static String ActionCheckCube_1;

	public static String ActionCheckCube_Check_Cube;

	public static String ActionCheckCube_Error_in_Cube;

	public static String ActionCheckCube_Information;

	public static String ActionCheckCube_No_Error;

	public static String ActionCheckSchema_0;

	public static String ActionCheckSchema_1;

	public static String ActionCheckSchema_Error_In_Project;

	public static String ActionCheckSchema_Information;

	public static String ActionCheckSchema_No_error;

	public static String ActionDeleteConnection_0;

	public static String ActionDeleteConnection_1;

	public static String ActionDeleteConnection_11;

	public static String ActionDeleteConnection_13;

	public static String ActionDeleteConnection_14;

	public static String ActionDeleteConnection_2;

	public static String ActionDeleteConnection_3;

	public static String ActionDeleteConnection_4;

	public static String ActionDeleteConnection_5;

	public static String ActionDeleteConnection_7;

	public static String ActionDeleteConnection_9;

	public static String ActionDeleteConnection_Del;

	public static String ActionDeleteConnection_Del_Cal_Column;

	public static String ActionDeleteConnection_Del_DS;

	public static String ActionDeleteConnection_Del_Rel;

	public static String ActionDeleteCubes_Del;

	public static String ActionDeleteCubes_Del_Cube;

	public static String ActionDeleteCubes_Del_DimUsage;

	public static String ActionDeleteCubes_Del_MeasureUsage;

	public static String ActionDeleteCubes_Del_Virtual_Cube;

	public static String ActionDeleteCubes_Del_Virtual_Dim;

	public static String ActionDeleteCubes_Del_Virtual_Measure;

	public static String ActionDeleteDims_Del;

	public static String ActionDeleteDims_Del_Dimension;

	public static String ActionDeleteDims_Del_Hierarchy;

	public static String ActionDeleteDims_Del_Lvl;

	public static String ActionDeleteMeasure_Del;

	public static String ActionDeleteMeasure_Del_Measure;

	public static String ActionFillMolap_Build;

	public static String ActionFillMolap_Information;

	public static String ActionModifyConnection_Back;

	public static String ActionModifyConnection_Cancel;

	public static String ActionModifyConnection_DB;

	public static String ActionModifyConnection_Desel_All;

	public static String ActionModifyConnection_Driver_;

	public static String ActionModifyConnection_DS_Name_;

	public static String ActionModifyConnection_DS_Type_;

	public static String ActionModifyConnection_Error;

	public static String ActionModifyConnection_Finish;

	public static String ActionModifyConnection_Information;

	public static String ActionModifyConnection_Modify_DS;

	public static String ActionModifyConnection_Modify_exsisting;

	public static String ActionModifyConnection_Next;

	public static String ActionModifyConnection_Password_;

	public static String ActionModifyConnection_Schema_Name_;

	public static String ActionModifyConnection_Sel_All;

	public static String ActionModifyConnection_Succes;

	public static String ActionModifyConnection_Test;

	public static String ActionModifyConnection_URL_;

	public static String ActionModifyConnection_User_;

	public static String ActionNewSchema_Error;

	public static String ActionNewSchema_Failed_Write;

	public static String ActionNewSchema_New_Schema;

	public static String ActionNewSchema_Save_Proj_As___;

	public static String ActionNewSchema_Save_Schema;

	public static String ActionNewSchema_Unable_Load;

	public static String ActionOpen_2;

	public static String ActionOpen_3;

	public static String ActionOpen_Error;

	public static String ActionOpen_Failed_Read;

	public static String ActionOpen_Open_Exsisting;

	public static String ActionOpen_Open_File___;

	public static String ActionPreloadConfig_0;

	public static String ActionPreloadConfig_1;

	public static String ActionPreloadConfig_2;

	public static String ActionSave_Error;

	public static String ActionSave_FailedToWriteData;

	public static String ActionSaveAs_Error;

	public static String ActionSaveAs_FailedToWriteData;

	public static String ActionSaveAs_SaveAs;

	public static String ActionSaveAs_SaveTheProjectAs___;

	public static String ActionUnderImplementation_Information;

	public static String ActionUnderImplementation_Under_impl;

	public static String ActionZipMolap_Error;

	public static String ActionZipMolap_Make_Archive;

	public static String ActionZipMolap_Zip_Cube;

	public static String AddLevelOperation_Containing_Only_one_Lvl;

	public static String AddLevelOperation_Unable_Add_Lvl;

	public static String AggregateHelper_1;

	public static String AggregateHelper_11;

	public static String AggregateHelper_12;

	public static String AggregateHelper_13;

	public static String AggregateHelper_2;

	public static String AggregateHelper_3;

	public static String AggregateHelper_4;

	public static String AggregateHelper_5;

	public static String AggregateHelper_6;

	public static String AggregateTransformationGenerator_17;

	public static String AggregateTransformationGenerator_18;

	public static String AggregateTransformationGenerator_19;

	public static String AggregateTransformationGenerator_20;

	public static String AggWizard_0;

	public static String AggWizard_1;

	public static String AggWizard_10;

	public static String AggWizard_11;

	public static String AggWizard_2;

	public static String AggWizard_4;

	public static String AggWizard_5;

	public static String AggWizard_7;

	public static String AggWizard_8;

	public static String AggWizard_Add_on_Measure;

	public static String AggWizard_Agg_on_Dim;

	public static String AggWizard_Agg_tbl_decla;

	public static String AggWizard_Dimension;

	public static String AggWizard_FK;

	public static String AggWizard_Measure;

	public static String ApplicationActionBarAdvisor_0;

	public static String ApplicationActionBarAdvisor_1;

	public static String ApplicationActionBarAdvisor_3;

	public static String ApplicationActionBarAdvisor_Admin;

	public static String ApplicationActionBarAdvisor_Already_In_rep;

	public static String ApplicationActionBarAdvisor_Choose_Schema;

	public static String ApplicationActionBarAdvisor_Create_Molap_DS;

	public static String ApplicationActionBarAdvisor_Designer;

	public static String ApplicationActionBarAdvisor_Designer_Mode;

	public static String ApplicationActionBarAdvisor_Dummy;

	public static String ApplicationActionBarAdvisor_Error;

	public static String ApplicationActionBarAdvisor_Export_Mondrian;

	public static String ApplicationActionBarAdvisor_Export_Mondrian_File;

	public static String ApplicationActionBarAdvisor_Export_To_FA_Server;

	public static String ApplicationActionBarAdvisor_Failed_To_Generate;

	public static String ApplicationActionBarAdvisor_Failed_Write_Data;

	public static String ApplicationActionBarAdvisor_Failed_Write_DataR;

	public static String ApplicationActionBarAdvisor_File;

	public static String ApplicationActionBarAdvisor_Fill_Molap_DS;

	public static String ApplicationActionBarAdvisor_Help;

	public static String ApplicationActionBarAdvisor_Import_Schema_Mondrian;

	public static String ApplicationActionBarAdvisor_Information;

	public static String ApplicationActionBarAdvisor_Load_Molap;

	public static String ApplicationActionBarAdvisor_Molap;

	public static String ApplicationActionBarAdvisor_Name_No_Set;

	public static String ApplicationActionBarAdvisor_New_Schema;

	public static String ApplicationActionBarAdvisor_No_cube_Sel;

	public static String ApplicationActionBarAdvisor_Not_Molap_Cube;

	public static String ApplicationActionBarAdvisor_Open_Recent___;

	public static String ApplicationActionBarAdvisor_Opening_Failed_;

	public static String ApplicationActionBarAdvisor_Parsing_Failed_;

	public static String ApplicationActionBarAdvisor_Properties;

	public static String ApplicationActionBarAdvisor_Save;

	public static String ApplicationActionBarAdvisor_Save_Project_As___;

	public static String ApplicationActionBarAdvisor_SaveAs__;

	public static String ApplicationActionBarAdvisor_Scheduler_for_Molap_Cube;

	public static String ApplicationActionBarAdvisor_Schema_Contains_Error;

	public static String ApplicationActionBarAdvisor_Secu_Conn;

	public static String ApplicationActionBarAdvisor_Security;

	public static String ApplicationActionBarAdvisor_Super_Molap;

	public static String ApplicationActionBarAdvisor_Tools;

	public static String ApplicationActionBarAdvisor_Unable_Find_repository;

	public static String ApplicationActionBarAdvisor_Validate_Cube;

	public static String ApplicationActionBarAdvisor_Validate_FASD_Proj;

	public static String ApplicationActionBarAdvisor_Validate_Proj;

	public static String ApplicationActionBarAdvisor_Warning;

	public static String ApplicationActionBarAdvisor_Zip_Molap_Cube;

	public static String ApplicationWorkbenchWindowAdvisor_8;

	public static String ApplicationWorkbenchWindowAdvisor_Confirm_Exit;

	public static String ApplicationWorkbenchWindowAdvisor_Confirmation;

	public static String ApplicationWorkbenchWindowAdvisor_Error;

	public static String ApplicationWorkbenchWindowAdvisor_Failed_To_Write_Data;

	public static String ApplicationWorkbenchWindowAdvisor_Save_Project_As___;

	public static String ApplicationWorkbenchWindowAdvisor_Save_your_schema;

	public static String ApplicationWorkbenchWindowAdvisor_Unable_to_read_security;

	public static String ApplicationWorkbenchWindowAdvisor_You_Must_restart_to_apply;

	public static String CompositeColumn_0;

	public static String CompositeColumn_1;

	public static String CompositeColumn_12;

	public static String CompositeColumn_14;

	public static String CompositeColumn_15;

	public static String CompositeColumn_16;

	public static String CompositeColumn_17;

	public static String CompositeColumn_19;

	public static String CompositeColumn_2;

	public static String CompositeColumn_3;

	public static String CompositeColumn_4;

	public static String CompositeColumn_7;

	public static String CompositeCube_0;

	public static String CompositeCube_1;

	public static String CompositeCube_10;

	public static String CompositeCube_11;

	public static String CompositeCube_12;

	public static String CompositeCube_13;

	public static String CompositeCube_14;

	public static String CompositeCube_15;

	public static String CompositeCube_19;

	public static String CompositeCube_2;

	public static String CompositeCube_20;

	public static String CompositeCube_21;

	public static String CompositeCube_22;

	public static String CompositeCube_23;

	public static String CompositeCube_24;

	public static String CompositeCube_25;

	public static String CompositeCube_26;

	public static String CompositeCube_27;

	public static String CompositeCube_3;

	public static String CompositeCube_36;

	public static String CompositeCube_38;

	public static String CompositeCube_4;

	public static String CompositeCube_41;

	public static String CompositeCube_42;

	public static String CompositeCube_5;

	public static String CompositeCube_6;

	public static String CompositeCube_7;

	public static String CompositeCube_8;

	public static String CompositeCube_9;

	public static String CompositeDataSource_12;

	public static String CompositeDataSource_13;

	public static String CompositeDataSource_14;

	public static String CompositeDataSource_15;

	public static String CompositeDataSource_16;

	public static String CompositeDataSource_17;

	public static String CompositeDataSource_19;

	public static String CompositeDataSource_20;

	public static String CompositeDataSource_21;

	public static String CompositeDataSource_23;

	public static String CompositeDataSource_24;

	public static String CompositeDataSource_31;

	public static String CompositeDataSource_36;

	public static String CompositeDataSource_37;

	public static String CompositeDataSource_38;

	public static String CompositeDataSource_39;

	public static String CompositeDataSource_40;

	public static String CompositeDataSource_41;

	public static String CompositeDataSource_42;

	public static String CompositeDataSource_45;

	public static String CompositeDataSource_46;

	public static String CompositeDataSource_47;

	public static String CompositeDataSource_48;

	public static String CompositeDataSource_49;

	public static String CompositeDataSource_50;

	public static String CompositeDataSource_52;

	public static String CompositeDataSource_53;

	public static String CompositeDataSource_68;

	public static String CompositeDataSource_69;

	public static String CompositeDataSource_70;

	public static String CompositeDataSourceConnection_0;

	public static String CompositeDataSourceConnection_1;

	public static String CompositeDataSourceConnection_10;

	public static String CompositeDataSourceConnection_11;

	public static String CompositeDataSourceConnection_12;

	public static String CompositeDataSourceConnection_13;

	public static String CompositeDataSourceConnection_14;

	public static String CompositeDataSourceConnection_15;

	public static String CompositeDataSourceConnection_16;

	public static String CompositeDataSourceConnection_17;

	public static String CompositeDataSourceConnection_18;

	public static String CompositeDataSourceConnection_2;

	public static String CompositeDataSourceConnection_3;

	public static String CompositeDataSourceConnection_7;

	public static String CompositeDataSourceConnection_8;

	public static String CompositeDataSourceConnection_9;

	public static String CompositeDataSourceConnection2_0;

	public static String CompositeDataSourceConnection2_1;

	public static String CompositeDataSourceConnection2_18;

	public static String CompositeDataSourceConnection2_19;

	public static String CompositeDataSourceConnection2_2;

	public static String CompositeDataSourceConnection2_20;

	public static String CompositeDataSourceConnection2_21;

	public static String CompositeDataSourceConnection2_22;

	public static String CompositeDataSourceConnection2_23;

	public static String CompositeDataSourceConnection2_24;

	public static String CompositeDataSourceConnection2_25;

	public static String CompositeDataSourceConnection2_26;

	public static String CompositeDataSourceConnection2_27;

	public static String CompositeDataSourceConnection2_28;

	public static String CompositeDataSourceConnection2_29;

	public static String CompositeDataSourceConnection2_3;

	public static String CompositeDataSourceConnection2_30;

	public static String CompositeDataSourceConnection2_31;

	public static String CompositeDataSourceConnection2_32;

	public static String CompositeDataSourceConnection2_33;

	public static String CompositeDataSourceConnection2_34;

	public static String CompositeDetailRelation_0;

	public static String CompositeDetailRelation_1;

	public static String CompositeDetailRelation_2;

	public static String CompositeDetailRelation_3;

	public static String CompositeDetailRelation_4;

	public static String CompositeDetailRelation_5;

	public static String CompositeDetailRelation_6;

	public static String CompositeDetailRelation_7;

	public static String CompositeDetailRelation_8;

	public static String CompositeDimension_0;

	public static String CompositeDimension_1;

	public static String CompositeDimension_10;

	public static String CompositeDimension_11;

	public static String CompositeDimension_2;

	public static String CompositeDimension_7;

	public static String CompositeDimension_8;

	public static String CompositeDimension_9;

	public static String CompositeDimView_0;

	public static String CompositeDimView_1;

	public static String CompositeDimView_2;

	public static String CompositeDimView_4;

	public static String CompositeDimView_5;

	public static String CompositeFormula_0;

	public static String CompositeFormula_1;

	public static String CompositeFormula_2;

	public static String CompositeFormula_25;

	public static String CompositeFormula_26;

	public static String CompositeFormula_27;

	public static String CompositeGroup_0;

	public static String CompositeGroup_1;

	public static String CompositeGroup_2;

	public static String CompositeGroup_3;

	public static String CompositeGroup_4;

	public static String CompositeGroup_5;

	public static String CompositeHelpPattern_0;

	public static String CompositeHelpPattern_1;

	public static String CompositeHelpPattern_10;

	public static String CompositeHelpPattern_2;

	public static String CompositeHelpPattern_3;

	public static String CompositeHelpPattern_4;

	public static String CompositeHelpPattern_5;

	public static String CompositeHelpPattern_6;

	public static String CompositeHelpPattern_7;

	public static String CompositeHelpPattern_8;

	public static String CompositeHelpPattern_9;

	public static String CompositeHierarchy_0;

	public static String CompositeHierarchy_1;

	public static String CompositeHierarchy_2;

	public static String CompositeHierarchy_3;

	public static String CompositeHierarchy_4;

	public static String CompositeHierarchy_5;

	public static String CompositeHierarchy_6;

	public static String CompositeLevel_0;

	public static String CompositeLevel_1;

	public static String CompositeLevel_10;

	public static String CompositeLevel_2;

	public static String CompositeLevel_28;

	public static String CompositeLevel_29;

	public static String CompositeLevel_3;

	public static String CompositeLevel_30;

	public static String CompositeLevel_31;

	public static String CompositeLevel_33;

	public static String CompositeLevel_35;

	public static String CompositeLevel_37;

	public static String CompositeLevel_39;

	public static String CompositeLevel_4;

	public static String CompositeLevel_41;

	public static String CompositeLevel_42;

	public static String CompositeLevel_5;

	public static String CompositeLevel_6;

	public static String CompositeLevel_7;

	public static String CompositeLevel_8;

	public static String CompositeLevel_9;

	public static String CompositeMeasure_0;

	public static String CompositeMeasure_1;

	public static String CompositeMeasure_11;

	public static String CompositeMeasure_12;

	public static String CompositeMeasure_13;

	public static String CompositeMeasure_14;

	public static String CompositeMeasure_15;

	public static String CompositeMeasure_16;

	public static String CompositeMeasure_17;

	public static String CompositeMeasure_2;

	public static String CompositeMeasure_23;

	public static String CompositeMeasure_25;

	public static String CompositeMeasure_26;

	public static String CompositeMeasure_28;

	public static String CompositeMeasure_3;

	public static String CompositeMeasure_46;

	public static String CompositeMeasure_47;

	public static String CompositeMeasure_48;

	public static String CompositeMeasure_49;

	public static String CompositeMeasure_53;

	public static String CompositeMeasure_60;

	public static String CompositeMeasure_8;

	public static String CompositeMeasure_9;

	public static String CompositeMember_0;

	public static String CompositeMember_10;

	public static String CompositeMember_11;

	public static String CompositeMember_12;

	public static String CompositeMember_13;

	public static String CompositeMember_14;

	public static String CompositeMember_8;

	public static String CompositeMember_9;

	public static String CompositePreloadConfig_0;

	public static String CompositePreloadConfig_1;

	public static String CompositePreloadConfig_2;

	public static String CompositePreloadConfig_3;

	public static String CompositePreloadConfig_4;

	public static String CompositePreloadDimension_0;

	public static String CompositePreloadDimension_1;

	public static String CompositePreloadDimension_2;

	public static String CompositeRelation_0;

	public static String CompositeRelation_1;

	public static String CompositeRelation_2;

	public static String CompositeRelation_3;

	public static String CompositeRelation_4;

	public static String CompositeRelation_5;

	public static String CompositeRelation_6;

	public static String CompositeRelation_7;

	public static String CompositeSecurityProvider_0;

	public static String CompositeSecurityProvider_1;

	public static String CompositeSecurityProvider_10;

	public static String CompositeSecurityProvider_2;

	public static String CompositeSecurityProvider_5;

	public static String CompositeSecurityProvider_6;

	public static String CompositeSecurityProvider_7;

	public static String CompositeSecurityProvider_8;

	public static String CompositeSecurityProvider_9;

	public static String CompositeServerConnection_0;

	public static String CompositeServerConnection_1;

	public static String CompositeServerConnection_10;

	public static String CompositeServerConnection_11;

	public static String CompositeServerConnection_2;

	public static String CompositeServerConnection_7;

	public static String CompositeServerConnection_8;

	public static String CompositeServerConnection_9;

	public static String CompositeTable_0;

	public static String CompositeTable_1;

	public static String CompositeTable_2;

	public static String CompositeTable_3;

	public static String CompositeTable_4;

	public static String CompositeView_0;

	public static String CompositeView_1;

	public static String CompositeView_2;

	public static String CompositeView_3;

	public static String CompositeView_4;

	public static String CompositeView_5;

	public static String CompositeView_6;

	public static String CompositeView_7;

	public static String CompositeView_8;

	public static String CompositeVirtualCube_0;

	public static String CompositeVirtualCube_1;

	public static String CompositeVirtualCube_10;

	public static String CompositeVirtualCube_12;

	public static String CompositeVirtualCube_2;

	public static String CompositeVirtualCube_3;

	public static String CompositeVirtualCube_4;

	public static String CompositeVirtualCube_5;

	public static String CompositeVirtualCube_6;

	public static String CompositeVirtualCube_7;

	public static String CompositeVirtualCube_8;

	public static String CompositeVirtualCube_9;

	public static String CompositeXMLA_0;

	public static String CompositeXMLA_1;

	public static String CompositeXMLA_10;

	public static String CompositeXMLA_11;

	public static String CompositeXMLA_12;

	public static String CompositeXMLA_13;

	public static String CompositeXMLA_14;

	public static String CompositeXMLA_15;

	public static String CompositeXMLA_16;

	public static String CompositeXMLA_2;

	public static String CompositeXMLA_3;

	public static String CompositeXMLA_4;

	public static String CompositeXMLA_5;

	public static String CompositeXMLA_6;

	public static String CompositeXMLA_7;

	public static String CompositeXMLA_8;

	public static String CompositeXMLA_9;

	public static String CompositeXMLDesign_TableName;

	public static String CubeView_0;

	public static String CubeView_1;

	public static String CubeView_8;

	public static String CubeView_9;

	public static String CubeView_AddCube;

	public static String CubeView_AddDimension;

	public static String CubeView_AddMeasure;

	public static String CubeView_AddVirtualCube;

	public static String CubeView_Cubes;

	public static String CubeView_DefineAggregateTable;

	public static String CubeView_DeleteItem;

	public static String CubeView_DimensionBrowser;

	public static String CubeView_Information;

	public static String CubeView_SaveProjectBeforeBrowseCube;

	public static String CubeView_SelectACubeToBrowseIt;

	public static String CubeView_VirtualCubes;

	public static String DataSourceWizard_ChooseDataBase;

	public static String DataSourceWizard_ChooseTbToAddFromDBToDataSource;

	public static String DataSourceWizard_Connection;

	public static String DataSourceWizard_ConnectionDef;

	public static String DataSourceWizard_ConnectionToDatasource;

	public static String DataSourceWizard_CreateANewConnectionOrChoose;

	public static String DataSourceWizard_DataSourceWizard;

	public static String DataSourceWizard_DefineRelations;

	public static String DataSourceWizard_DefineRelationsBetweenTables;

	public static String DataSourceWizard_DefineTheConnToYourDB;

	public static String DataSourceWizard_Error;

	public static String DataSourceWizard_JarFileNotFound;

	public static String DataSourceWizard_SelectTables;

	public static String DataSourceWizard_UnableToLoadXmiFile;

	public static String DateDimensionInfosPage_0;

	public static String DateDimensionInfosPage_1;

	public static String DateDimensionInfosPage_2;

	public static String DateDimensionInfosPage_4;

	public static String DateDimensionInfosPage_9;

	public static String DateDimensionLevelsPage_0;

	public static String DateDimensionLevelsPage_1;

	public static String DateDimensionLevelsPage_10;

	public static String DateDimensionLevelsPage_9;

	public static String DateDimensionWizard_0;

	public static String DateDimensionWizard_2;

	public static String DateDimensionWizard_3;

	public static String DateDimensionWizard_5;

	public static String DateDimensionWizard_6;

	public static String DetailView_1;

	public static String DetailView_2;

	public static String DetailView_6;

	public static String DialogAddColumn_Add_Calculated;

	public static String DialogAddColumn_Attr;

	public static String DialogAddColumn_Name;

	public static String DialogAddColumn_SQL_Type;

	public static String DialogAddDimension_Desc_;

	public static String DialogAddDimension_Is_Date;

	public static String DialogAddDimension_Lood_Method;

	public static String DialogAddDimension_Name_;

	public static String DialogAddDimension_New;

	public static String DialogAddDimension_Order_;

	public static String DialogAddDimension_Widget_;

	public static String DialogAddHiera_0;

	public static String DialogAddHiera_All;

	public static String DialogAddHiera_All_;

	public static String DialogAddHiera_All_Memb_;

	public static String DialogAddHiera_Default;

	public static String DialogAddHiera_Desc_;

	public static String DialogAddHiera_Name_;

	public static String DialogAddHiera_New_Hierar;

	public static String DialogAddLevel_6;

	public static String DialogAddLevel_Close_DataObj;

	public static String DialogAddLevel_Col_Item_;

	public static String DialogAddLevel_DefaultLvl;

	public static String DialogAddLevel_Desc_Item_;

	public static String DialogAddLevel_Geolocalizable_Item_;

	public static String DialogAddLevel_Descr_;

	public static String DialogAddLevel_Label_Item_;

	public static String DialogAddLevel_Name_;

	public static String DialogAddLevel_new;

	public static String DialogAddLevel_Null_Parent_;

	public static String DialogAddLevel_Parent_Col_;

	public static String DialogAddLevel_Single;

	public static String DialogAddTable_0;

	public static String DialogAddTable_1;

	public static String DialogAddTable_2;

	public static String DialogAddTable_3;

	public static String DialogAddTable_Browse_100;

	public static String DialogAddTable_Browse_X;

	public static String DialogAddTable_Closure_Table__;

	public static String DialogAddTable_Dim_table;

	public static String DialogAddTable_Error;

	public static String DialogAddTable_Fact_Table__;

	public static String DialogAddTable_Information;

	public static String DialogAddTable_Lbl_Tbl__;

	public static String DialogAddTable_New_Tbl;

	public static String DialogAddTable_Remove_Tbl__;

	public static String DialogAddTable_Select_tbl;

	public static String DialogBigAggregate_0;

	public static String DialogBigAggregate_1;

	public static String DialogBigAggregate_10;

	public static String DialogBigAggregate_11;

	public static String DialogBigAggregate_12;

	public static String DialogBigAggregate_13;

	public static String DialogBigAggregate_14;

	public static String DialogBigAggregate_15;

	public static String DialogBigAggregate_16;

	public static String DialogBigAggregate_17;

	public static String DialogBigAggregate_19;

	public static String DialogBigAggregate_2;

	public static String DialogBigAggregate_3;

	public static String DialogBigAggregate_4;

	public static String DialogBigAggregate_5;

	public static String DialogBigAggregate_6;

	public static String DialogBigAggregate_7;

	public static String DialogBrowseColumn_0;

	public static String DialogBrowseColumn_All_Values;

	public static String DialogBrowseColumn_Content;

	public static String DialogBrowseColumn_Count;

	public static String DialogBrowseColumn_Distinct_Values;

	public static String DialogClosure_Close_tbl_;

	public static String DialogClosure_def_Closure;

	public static String DialogClosure_tbl_Child_Col;

	public static String DialogClosure_Tbl_Par_Col;

	public static String DialogCombo_0;

	public static String DialogCube_0;

	public static String DialogCube_2;

	public static String DialogCube_3;

	public static String DialogCube_Add;

	public static String DialogCube_Add_All;

	public static String DialogCube_Cube_Loc;

	public static String DialogCube_Descr_;

	public static String DialogCube_Dimensions;

	public static String DialogCube_Fact_DataObj;

	public static String DialogCube_Groups;

	public static String DialogCube_Informations;

	public static String DialogCube_Measures;

	public static String DialogCube_Name_;

	public static String DialogCube_New_Cube;

	public static String DialogCube_Phy_Name_;

	public static String DialogCube_Remove;

	public static String DialogCube_Secu_GP;

	public static String DialogCube_Select_A_DataObj;

	public static String DialogCube_Type_;

	public static String DialogCube_Warning;

	public static String DialogDataObject_Descr_;

	public static String DialogDataObject_File_Name_;

	public static String DialogDataObject_Name_;

	public static String DialogDataObject_New;

	public static String DialogDataObject_Select_Statement;

	public static String DialogDataObject_Server_;

	public static String DialogDataObject_TTrans_Name_;

	public static String DialogDataObject_Type_;

	public static String DialogDataObjectItem_Attr;

	public static String DialogDataObjectItem_Class_;

	public static String DialogDataObjectItem_Descr_;

	public static String DialogDataObjectItem_Name_;

	public static String DialogDataObjectItem_New;

	public static String DialogDataObjectItem_Origin;

	public static String DialogDataObjectItem_Parent_;

	public static String DialogDataObjectItem_Type;

	public static String DialogDataSourceConnection_0;

	public static String DialogDimBrowser_Dim;

	public static String DialogDimBrowser_Dim_Bro;

	public static String DialogDimBrowser_Dim_Browser_avialable;

	public static String DialogDimBrowser_Dim_Graph_Browser;

	public static String DialogDimBrowser_Dim_Tree_Browser;

	public static String DialogDimBrowser_Hiera;

	public static String DialogDimBrowser_Info;

	public static String DialogDimSecu_45;

	public static String DialogDimSecu_Allow_Acces;

	public static String DialogDimSecu_Conn;

	public static String DialogDimSecu_Descr;

	public static String DialogDimSecu_Dim_Mem;

	public static String DialogDimSecu_Dim_Secu;

	public static String DialogDimSecu_DimBrow_No_Snowflakes;

	public static String DialogDimSecu_Full_Acces;

	public static String DialogDimSecu_Hiera;

	public static String DialogDimSecu_Information;

	public static String DialogDimSecu_MemBer_Restriction;

	public static String DialogDimSecu_Name;

	public static String DialogDimSecu_SecurityGP;

	public static String DialogDrill_0;

	public static String DialogDrill_1;

	public static String DialogDrill_12;

	public static String DialogDrill_13;

	public static String DialogDrill_14;

	public static String DialogDrill_15;

	public static String DialogDrill_16;

	public static String DialogDrill_2;

	public static String DialogDrill_3;

	public static String DialogDrill_4;

	public static String DialogDrill_7;

	public static String DialogDrill_8;

	public static String DialogDrill_9;

	public static String DialogDrillCube_0;

	public static String DialogDrillCube_1;

	public static String DialogDrillCube_3;

	public static String DialogDrillElement_0;

	public static String DialogDrillElement_1;

	public static String DialogDrillElement_2;

	public static String DialogDrillElement_3;

	public static String DialogDrillElement_4;

	public static String DialogDrillReport_0;

	public static String DialogDrillReport_1;

	public static String DialogDrillReport_3;

	public static String DialogDrillReport_4;

	public static String DialogDrillReport_5;

	public static String DialogFillingMolap_Filling;

	public static String DialogFormula_2;

	public static String DialogFormula_Agg_;

	public static String DialogFormula_Descr_;

	public static String DialogFormula_Format_String_;

	public static String DialogFormula_Formula;

	public static String DialogFormula_Formula_Not_Def;

	public static String DialogFormula_Information;

	public static String DialogFormula_Lbl_;

	public static String DialogFormula_MeasureOrigin;

	public static String DialogFormula_Name;

	public static String DialogFormula_New_Measure;

	public static String DialogFormula_Order_;

	public static String DialogFormula_Origin_;

	public static String DialogFormula_Std;

	public static String DialogFormula_Type_;

	public static String DialogFormula_Visible;

	public static String DialogFormula_Warning;

	public static String DialogGraphRelations_Graph_Rep;

	public static String DialogGraphRelations_Info;

	public static String DialogGraphRelations_No_Rel;

	public static String DialogNew_0;

	public static String DialogNew_10;

	public static String DialogNew_12;

	public static String DialogNew_13;

	public static String DialogNew_14;

	public static String DialogNew_15;

	public static String DialogNew_16;

	public static String DialogNew_17;

	public static String DialogNew_18;

	public static String DialogNew_19;

	public static String DialogNew_2;

	public static String DialogNew_20;

	public static String DialogNew_4;

	public static String DialogNew_8;

	public static String DialogNew_9;

	public static String DialogPickCol_0;

	public static String DialogPickCol_1;

	public static String DialogPickCol_2;

	public static String DialogPickCol_3;

	public static String DialogPickColGeoloc_0;

	public static String DialogPickColGeoloc_1;

	public static String DialogPickColGeoloc_2;

	public static String DialogPreload_0;

	public static String DialogPreload_1;

	public static String DialogPreload_5;

	public static String DialogPreload_6;

	public static String DialogPreload_7;

	public static String DialogPreload_8;

	public static String DialogRelation_Relations;

	public static String DialogSelectDataObject_Data_Obj;

	public static String DialogSelectDataObjectItem_Data_Obj_;

	public static String DialogSelectDataObjectItem_data_Obj_Item;

	public static String DialogSelectDataObjectItem_DS_;

	public static String DialogSelectDataObjectItem_Select;

	public static String DialogSelectDimension_0;

	public static String DialogSelectDimension_Select_Dim;

	public static String DialogSelectMeasure_0;

	public static String DialogSelectMeasure_Select_Meas;

	public static String DialogSelectRepositoryItem_1;

	public static String DialogSqlSelect_0;

	public static String DialogSqlSelect_1;

	public static String DialogSqlSelect_10;

	public static String DialogSqlSelect_11;

	public static String DialogSqlSelect_3;

	public static String DialogSqlSelect_4;

	public static String DialogSqlSelect_5;

	public static String DialogSqlSelect_6;

	public static String DialogSqlSelect_7;

	public static String DialogSqlSelect_8;

	public static String DialogTableBrowser_0;

	public static String DialogTimeDimension_Col;

	public static String DialogTimeDimension_Day;

	public static String DialogTimeDimension_Dim_table;

	public static String DialogTimeDimension_Lvl_Type;

	public static String DialogTimeDimension_Month;

	public static String DialogTimeDimension_Quarter;

	public static String DialogTimeDimension_Time_Dim;

	public static String DialogTimeDimension_Year;

	public static String DialogVirtualCube_Add;

	public static String DialogVirtualCube_Add_All;

	public static String DialogVirtualCube_Create_Virtual_Cube;

	public static String DialogVirtualCube_Dim_R_Needed;

	public static String DialogVirtualCube_Dimensions;

	public static String DialogVirtualCube_DS;

	public static String DialogVirtualCube_General;

	public static String DialogVirtualCube_Info;

	public static String DialogVirtualCube_Name_;

	public static String DialogVirtualCube_Remove;

	public static String DialogVirtualCube_Secu;

	public static String DialogXMLBrowser_Tbl_Brow;

	public static String DialogXMLColumnBrowser_All;

	public static String DialogXMLColumnBrowser_Distinct;

	public static String DimensionView_0;

	public static String DimensionView_1;

	public static String DimensionView_2;

	public static String DimensionView_3;

	public static String DimensionView_4;

	public static String DimensionView_5;

	public static String DimensionView_6;

	public static String DimensionView_AddDimension;

	public static String DimensionView_AddDimensionGroup;

	public static String DimensionView_AddHierarchy;

	public static String DimensionView_AddLevel;

	public static String DimensionView_AddToDimensionGroup;

	public static String DimensionView_BrowseDimension;

	public static String DimensionView_DefineDimensionSecurity;

	public static String DimensionView_DelItem;

	public static String DimensionView_Dimensions;

	public static String DimensionView_HierarchyContainingChildWithonly1Level;

	public static String DimensionView_TimeDimensionHelper;

	public static String DimensionView_UnableToAddThisLevel;

	public static String FMDTMapper_0;

	public static String FreemetricsPlugin_10;

	public static String FreemetricsPlugin_4;

	public static String FreemetricsPlugin_5;

	public static String FreemetricsPlugin_6;

	public static String FreemetricsPlugin_7;

	public static String FreemetricsPlugin_8;

	public static String FreemetricsPlugin_9;

	public static String FreemetricsPlugin_Advertissement;

	public static String FreemetricsPlugin_Choose_Mondrian_Schema;

	public static String FreemetricsPlugin_DummyDs;

	public static String FreemetricsPlugin_Error;

	public static String FreemetricsPlugin_Error_Saving_Security_xml;

	public static String FreemetricsPlugin_Opening_Exsisting_FA_Proj;

	public static String FreemetricsPlugin_Opening_Failed_;

	public static String FreemetricsPlugin_Parsing_Failed_;

	public static String FreemetricsPlugin_Schema_Contains_errors;

	public static String FreemetricsPlugin_Unable_to_Load_Security;

	public static String FreemetricsPlugin_Under_Implementation;

	public static String FreemetricsPlugin_Warning;

	public static String InlineWizard_Define_Inline_Structure;

	public static String InlineWizard_Fill;

	public static String InlineWizard_Inline_Table_Datas;

	public static String InlineWizard_Inline_Table_Structure;

	public static String InlineWizard_Inline_Table_Wizard;

	public static String Kalendar_Calendar;

	public static String Kalendar_DblClkToGetCurrDate;

	public static String Kalendar_Friday;

	public static String Kalendar_Monday;

	public static String Kalendar_Saturday;

	public static String Kalendar_Sunday;

	public static String Kalendar_Thursday;

	public static String Kalendar_Tuesday;

	public static String Kalendar_Wednesday;

	public static String MeasureView_0;

	public static String MeasureView_1;

	public static String MeasureView_2;

	public static String MeasureView_3;

	public static String MeasureView_4;

	public static String MeasureView_AddMeasure;

	public static String MeasureView_AddMeasureGp;

	public static String MeasureView_DelMeasure;

	public static String MeasureView_Measures;

	public static String OdaDatasetWizard_1;

	public static String OdaDatasetWizard_12;

	public static String OdaDatasetWizard_14;

	public static String OdaDatasetWizard_15;

	public static String OdaDatasetWizard_2;

	public static String OdaDatasetWizard_4;

	public static String OdaDatasetWizard_9;

	public static String OdaDatasourceSelectionPage_0;

	public static String OdaDatasourceSelectionPage_2;

	public static String OdaDatasourceSelectionPage_3;

	public static String OdaDatasourceSelectionPage_4;

	public static String OdaDatasourceWizard_1;

	public static String OdaDatasourceWizard_2;

	public static String OpenViewAction_Error;

	public static String OpenViewAction_Error_Opening_View;

	public static String PageConnection_Error;

	public static String PageConnection_SelectAFile;

	public static String PageCubeAttachment_0;

	public static String PageCubeAttachment_2;

	public static String PageCubeAttachment_3;

	public static String PageCubeAttachment_4;

	public static String PageDatas_Add_Row;

	public static String PageDatas_Remove_Row;

	public static String PageDatas_Undefined;

	public static String PageDataSource_Browse100Lines;

	public static String PageDataSource_BrowseXLines;

	public static String PageDataSource_ClosureTable___;

	public static String PageDataSource_DimensionTable;

	public static String PageDataSource_FactTable___;

	public static String PageDataSource_FactTable_____;

	public static String PageDataSource_Information;

	public static String PageDataSource_LabelTable;

	public static String PageDataSource_LabelTable____;

	public static String PageDataSource_PentahoMetaDataXMI;

	public static String PageDataSource_RelationalDB;

	public static String PageDataSource_RemoveTable___;

	public static String PageDataSource_SelTableToBrowseIt;

	public static String PageDefinition_0;

	public static String PageDefinition_1;

	public static String PageDefinition_10;

	public static String PageDefinition_11;

	public static String PageDefinition_12;

	public static String PageDefinition_13;

	public static String PageDefinition_14;

	public static String PageDefinition_15;

	public static String PageDefinition_16;

	public static String PageDefinition_17;

	public static String PageDefinition_18;

	public static String PageDefinition_19;

	public static String PageDefinition_2;

	public static String PageDefinition_21;

	public static String PageDefinition_24;

	public static String PageDefinition_25;

	public static String PageDefinition_26;

	public static String PageDefinition_28;

	public static String PageDefinition_4;

	public static String PageDefinition_5;

	public static String PageDefinition_6;

	public static String PageDefinition_7;

	public static String PageDefinition_8;

	public static String PageDefinition_9;

	public static String PageDimension_Add;

	public static String PageDimension_Agg_Col;

	public static String PageDimension_DimensionName;

	public static String PageDimension_Dimenson;

	public static String PageDimension_Hiearchy;

	public static String PageDimension_HierarchyName;

	public static String PageDimension_Level;

	public static String PageDimension_NewHierarchy;

	public static String PageDimension_Remove;

	public static String PageDimension_SelectHierarchyTable;

	public static String PageFolder_BrowseFirstXLines;

	public static String PageFolder_FreeAnalysisPath;

	public static String PageFolder_SchemaFolder;

	public static String PageForeignKey_0;

	public static String PageForeignKey_1;

	public static String PageForeignKey_2;

	public static String PageForeignKey_3;

	public static String PageForeignKey_4;

	public static String PageForeignKey_Add;

	public static String PageForeignKey_Agg_Tbl_Col;

	public static String PageForeignKey_Fact8tbl_Col;

	public static String PageForeignKey_Relations_between;

	public static String PageForeignKey_Remove;

	public static String PageIndex_0;

	public static String PageIndex_1;

	public static String PageIndex_2;

	public static String PageIndex_3;

	public static String PageIndex_CreateNewConnection;

	public static String PageIndex_Database;

	public static String PageIndex_Error;

	public static String PageIndex_SelectAnExsistingConnection;

	public static String PageIndex_UnableToConnectDataBase;

	public static String PageIndex_UnableToLoadJDBCDriver;

	public static String PageIndex_UnableToReadJdbcProp;

	public static String PageLanguage_ChooseYourLang_;

	public static String PageMeasure_Add;

	public static String PageMeasure_Agg_Col;

	public static String PageMeasure_Matching;

	public static String PageMeasure_Measure;

	public static String PageMeasure_Remove;

	public static String PageRelation_CreateRelation;

	public static String PageRelation_LeftObj;

	public static String PageRelation_LeftObjItem;

	public static String PageRelation_RemoveRelation;

	public static String PageRelation_RightObj;

	public static String PageRelation_RightObjItem;

	public static String PageRoot_1;

	public static String PageRoot_2;

	public static String PageRoot_Agg_Table;

	public static String PageStartup_ShowNewDialogAtStartUp;

	public static String PageStructure_Add;

	public static String PageStructure_Col_Name;

	public static String PageStructure_Col_Type;

	public static String PageStructure_Remove;

	public static String PageStructure_Table_Name;

	public static String PropertiesDialog_0;

	public static String PropertiesDialog_1;

	public static String PropertiesDialog_14;

	public static String PropertiesDialog_2;

	public static String PropertiesDialog_20;

	public static String PropertiesDialog_21;

	public static String PropertiesDialog_3;

	public static String PropertiesDialog_4;

	public static String PropertiesDialog_6;

	public static String PropertiesDialog_7;

	public static String PropertiesDialog_8;

	public static String Repository_TheProjectAlreadyExsistinTheRepository;

	public static String RoleView_0;

	public static String RoleView_1;

	public static String RoleView_12;

	public static String RoleView_14;

	public static String RoleView_AccesMethod;

	public static String RoleView_AddView;

	public static String RoleView_Description;

	public static String RoleView_DimensionsViews;

	public static String RoleView_FullAcces;

	public static String RoleView_Hierarchy;

	public static String RoleView_MemberRestriction;

	public static String RoleView_NoneAccess;

	public static String RoleView_SecurityGroup;

	public static String SecurityProviderView_0;

	public static String SecurityProviderView_1;

	public static String SecurityProviderView_AddSecurityProvider;

	public static String SecurityProviderView_DelServerConnection;

	public static String SecurityProviderView_NewSecurityProvider;

	public static String SecurityView_0;

	public static String SecurityView_1;

	public static String SecurityView_2;

	public static String SecurityView_3;

	public static String SecurityView_Add_Dimension_View;

	public static String SecurityView_Add_Security_Group;

	public static String SecurityView_Add_View;

	public static String SecurityView_Del_Item;

	public static String SecurityView_Dimension_Views;

	public static String SecurityView_New_Dimension_View;

	public static String SecurityView_New_Security_Group;

	public static String SecurityView_New_View;

	public static String SecurityView_SecurityG_p;

	public static String ServerView_0;

	public static String ServerView_1;

	public static String ServerView_Add_Server_Connection;

	public static String ServerView_Del_Server_Connection;

	public static String ServerView_New_Server_Connection;

	public static String SQLView_0;

	public static String SQLView_1;

	public static String SQLView_2;

	public static String SQLView_3;

	public static String SQLView_4;

	public static String SQLView_Add_DataObjitem;

	public static String SQLView_Add_DataSource;

	public static String SQLView_Add_Relation;

	public static String SQLView_Add_Table;

	public static String SQLView_Browse_100_lines;

	public static String SQLView_Browse_X_Lines;

	public static String SQLView_Calculated_Column;

	public static String SQLView_Create_An_inline_Tbl;

	public static String SQLView_DataSource;

	public static String SQLView_Error;

	public static String SQLView_Information;

	public static String SQLView_New_Relation;

	public static String SQLView_Relation_Graphe;

	public static String SQLView_Relations;

	public static String SQLView_Remove_Item;

	public static String SQLView_Select_a_tbl_To_Browse_It;

	public static String BROWSE_DIM_X_LINES_PREF;

	public static String CUBE_EXPLORER_X_LINES_PREF;

	public static String UnitedOlapConversionWizard_0;

	public static String UnitedOlapConversionWizard_1;

	public static String UnitedOlapConversionWizard_3;

	public static String UnitedOlapConversionWizard_4;

	public static String UnitedOlapConversionWizard_5;

	public static String UnitedOlapConversionWizard_7;

	public static String WizardAggregate_0;

	public static String WizardAggregate_1;

	public static String WizardAggregate_2;

	public static String WizardAggregate_3;

	public static String WizardAggregate_4;

	public static String WizardAggregate_5;

	public static String WizardAggregate_6;

	public static String XMLAView_1;

	public static String XMLAView_2;

	// langue et pays courant
	private static String m_strLangCour;
	private static String m_strCountryCour;
	private static Locale m_locLocale;

	static {

		NLS.initializeMessages(BUNDLE_NAME, LanguageText.class);

	}

	private static void langChoisi() {

		GestionLang g = new GestionLang();
		String[] param = extractParamLangBundl(g.getNomConfLang(LanguageText.init()));

		if (param != null) {
			m_strLangCour = param[0];
			m_strCountryCour = param[1];

			m_locLocale = new Locale(m_strLangCour, m_strCountryCour);

		} else {
			m_locLocale = new Locale(System.getProperty("user.language"), System.getProperty("user.country")); //$NON-NLS-1$ //$NON-NLS-2$
		}

		Locale.setDefault(m_locLocale);
	}

	private LanguageText() {
	}

	// Getters and Setters
	/**
	 * Return current language
	 */
	public static final String getCurrentLanguage() {
		langChoisi();
		return m_locLocale.getLanguage();
	}

	//
	/**
	 * Return current country
	 * 
	 * @return current country
	 */
	public static final String getCurrentCountry() {
		langChoisi();
		return m_locLocale.getCountry();
	}

	//
	/**
	 * @param p_strKey
	 *            la clef a traduire
	 * @return la traduction dans la langue prefere
	 */
	public static String getString(String p_strKey) {
		String configKey = init();
		GestionLang gestLan = new GestionLang("Configuration.properties"); //$NON-NLS-1$
		String nomFich = gestLan.getNomConfLang(configKey);

		String value = gestLan.getConfiguration(nomFich + ".properties", p_strKey); //$NON-NLS-1$

		String trad = ConvertUtf.decodeCyrillic(value);
		return trad;
	}

	//
	/**
	 * @param p_oDate
	 *            date
	 * @return String representing the date in the current locale lang
	 */
	public static String displayDate(Date p_oDate) {
		langChoisi();
		Locale p_oLocale = m_locLocale;
		DateFormat d_format = DateFormat.getDateInstance(DateFormat.SHORT, p_oLocale);

		return d_format.format(p_oDate);
	}

	//
	/**
	 * @param p_oNum
	 *            an object representing a numeric
	 * @return String representing a numeric in the current locale lang if isn't
	 *         displayable may return empty string
	 */
	public static String displayNumber(Object p_oNum) {
		String res = ""; //$NON-NLS-1$
		langChoisi();
		Locale p_oLocale = m_locLocale;
		NumberFormat numForm = NumberFormat.getNumberInstance(p_oLocale);
		if (p_oNum != null) {
			if (p_oNum.equals(Integer.class)) {
				res = numForm.format(p_oNum);
			} else if (p_oNum.equals(Double.class)) {
				res = numForm.format(p_oNum);
			}
		}
		return res;
	}

	//
	/**
	 * @param p_oCurrency
	 *            currency's value to display
	 * @return String representing a currency in the current locale lang if
	 *         isn't displayable may return empty string
	 */
	public static String displayCurrency(Double p_oCurrency) {
		String res = ""; //$NON-NLS-1$
		langChoisi();
		Locale p_oLocale = m_locLocale;
		NumberFormat numForm = NumberFormat.getCurrencyInstance(p_oLocale);
		if (p_oCurrency != null) {
			res = numForm.format(p_oCurrency);
		}
		return res;
	}// displayCurrency

	//
	/**
	 * @param p_oCurrency
	 *            string representing currency's value to display
	 * @return String representing a currency in the current locale lang if
	 *         isn't displayable may return empty string
	 */
	public static String displayCurrency(String p_oCurrency) {
		String res = ""; //$NON-NLS-1$
		if (p_oCurrency != null && !p_oCurrency.trim().equalsIgnoreCase("")) { //$NON-NLS-1$
			langChoisi();
			try {
				res = displayCurrency(Double.parseDouble(p_oCurrency));
			} catch (Exception e) {
			}
		}
		return res;
	}

	//
	/**
	 * @param p_oPercent
	 *            percent's value to display
	 * @return representing a percentage in the current locale lang if isn't
	 *         displayable may return empty string
	 */
	public static String displayPercent(Double p_oPercent) {
		String res = ""; //$NON-NLS-1$
		langChoisi();
		Locale p_oLocale = m_locLocale;
		NumberFormat numForm = NumberFormat.getPercentInstance(p_oLocale);
		if (p_oPercent != null) {
			res = numForm.format(p_oPercent);
		}
		return res;
	}

	//
	/**
	 * @param p_oPercent
	 *            string representing percent's value to display
	 * @return String representing a percentage in the current locale lang if
	 *         isn't displayable may return empty string
	 */
	public static String displayPercent(String p_oPercent) {
		String res = ""; //$NON-NLS-1$
		if (p_oPercent != null && !p_oPercent.trim().equalsIgnoreCase("")) { //$NON-NLS-1$
			langChoisi();
			try {
				res = displayPercent(Double.parseDouble(p_oPercent));
			} catch (Exception e) {
			}
		}
		return res;
	}

	//
	/**
	 * @param p_strFileBundl
	 *            la langue choisi par l'utilisateur
	 * @return le couple iso 639 et 3166 de la langue et du pays choisi dans un
	 *         tableau de string
	 */
	private static String[] extractParamLangBundl(String p_strFileBundl) {
		String[] para = new String[2];
		if (p_strFileBundl != null && !p_strFileBundl.trim().equalsIgnoreCase("")) { //$NON-NLS-1$
			para[0] = p_strFileBundl.split("_")[1]; //$NON-NLS-1$
			para[1] = p_strFileBundl.split("_")[2]; //$NON-NLS-1$
		} else {
			para[0] = "en"; //$NON-NLS-1$
			para[1] = "US"; //$NON-NLS-1$
		}
		return para;
	}

	//
	/**
	 * @return a string representing the stored user's favorite language
	 */
	private static String init() {
		String lang = "English"; //$NON-NLS-1$

		try {
			IPreferenceStore store = FreemetricsPlugin.getDefault().getPreferenceStore();

			lang = store.getString(PreferenceConstants.P_LANG);
			if (lang.trim().equalsIgnoreCase("") || lang == null) { //$NON-NLS-1$
				store.setValue(PreferenceConstants.P_LANG, "English"); //$NON-NLS-1$
				store = FreemetricsPlugin.getDefault().getPreferenceStore();
				lang = store.getString(PreferenceConstants.P_LANG);

				m_strLangCour = "en"; //$NON-NLS-1$
				m_strCountryCour = "US"; //$NON-NLS-1$

				m_locLocale = new Locale(m_strLangCour, m_strCountryCour);

				Locale.setDefault(m_locLocale);

			} else {

				m_locLocale = new Locale(System.getProperty("user.language"), System.getProperty("user.country")); //$NON-NLS-1$ //$NON-NLS-2$

				Locale.setDefault(m_locLocale);
			}
		} catch (Exception e) {
		}
		return lang;
	}

}
