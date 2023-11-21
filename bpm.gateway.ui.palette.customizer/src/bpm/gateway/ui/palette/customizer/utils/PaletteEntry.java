package bpm.gateway.ui.palette.customizer.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.AggregateTransformation;
import bpm.gateway.core.transformations.CartesianProduct;
import bpm.gateway.core.transformations.DeleteRows;
import bpm.gateway.core.transformations.EncryptTransformation;
import bpm.gateway.core.transformations.FieldSplitter;
import bpm.gateway.core.transformations.Filter;
import bpm.gateway.core.transformations.FilterDimension;
import bpm.gateway.core.transformations.Geoloc;
import bpm.gateway.core.transformations.Lookup;
import bpm.gateway.core.transformations.MergeStreams;
import bpm.gateway.core.transformations.Normalize;
import bpm.gateway.core.transformations.RowsFieldSplitter;
import bpm.gateway.core.transformations.SelectDistinct;
import bpm.gateway.core.transformations.SelectionTransformation;
import bpm.gateway.core.transformations.Sequence;
import bpm.gateway.core.transformations.SimpleMappingTransformation;
import bpm.gateway.core.transformations.SortTransformation;
import bpm.gateway.core.transformations.SqlLookup;
import bpm.gateway.core.transformations.SqlScript;
import bpm.gateway.core.transformations.SubTransformation;
import bpm.gateway.core.transformations.SurrogateKey;
import bpm.gateway.core.transformations.TopXTransformation;
import bpm.gateway.core.transformations.UnduplicateRows;
import bpm.gateway.core.transformations.calcul.Calculation;
import bpm.gateway.core.transformations.calcul.GeoDispatch;
import bpm.gateway.core.transformations.calcul.GeoFilter;
import bpm.gateway.core.transformations.calcul.RangingTransformation;
import bpm.gateway.core.transformations.cleansing.ValidationCleansing;
import bpm.gateway.core.transformations.files.FileFolderReader;
import bpm.gateway.core.transformations.freemetrics.KPIList;
import bpm.gateway.core.transformations.freemetrics.KPIOutput;
import bpm.gateway.core.transformations.googleanalytics.VanillaAnalyticsGoogle;
import bpm.gateway.core.transformations.inputs.CassandraInputStream;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.core.transformations.inputs.DbAccessInput;
import bpm.gateway.core.transformations.inputs.FMDTInput;
import bpm.gateway.core.transformations.inputs.FileInputCSV;
import bpm.gateway.core.transformations.inputs.FileInputPDFForm;
import bpm.gateway.core.transformations.inputs.FileInputShape;
import bpm.gateway.core.transformations.inputs.FileInputVCL;
import bpm.gateway.core.transformations.inputs.FileInputXLS;
import bpm.gateway.core.transformations.inputs.FileInputXML;
import bpm.gateway.core.transformations.inputs.HbaseInputStream;
import bpm.gateway.core.transformations.inputs.LdapInput;
import bpm.gateway.core.transformations.inputs.LdapMultipleMembers;
import bpm.gateway.core.transformations.inputs.NagiosDb;
import bpm.gateway.core.transformations.inputs.OdaInput;
import bpm.gateway.core.transformations.inputs.OdaInputWithParameters;
import bpm.gateway.core.transformations.inputs.OracleXmlView;
import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.core.transformations.kml.KMLOutput;
import bpm.gateway.core.transformations.normalisation.Denormalisation;
import bpm.gateway.core.transformations.nosql.SqoopTransformation;
import bpm.gateway.core.transformations.olap.OlapDimensionExtractor;
import bpm.gateway.core.transformations.olap.OlapFactExtractorTranformation;
import bpm.gateway.core.transformations.olap.OlapOutput;
import bpm.gateway.core.transformations.outputs.CassandraOutputStream;
import bpm.gateway.core.transformations.outputs.CielComptaOutput;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.core.transformations.outputs.FileOutputVCL;
import bpm.gateway.core.transformations.outputs.FileOutputWeka;
import bpm.gateway.core.transformations.outputs.FileOutputXLS;
import bpm.gateway.core.transformations.outputs.FileOutputXML;
import bpm.gateway.core.transformations.outputs.GeoJsonOutput;
import bpm.gateway.core.transformations.outputs.HBaseOutputStream;
import bpm.gateway.core.transformations.outputs.InfoBrightInjector;
import bpm.gateway.core.transformations.outputs.InsertOrUpdate;
import bpm.gateway.core.transformations.outputs.SlowChangingDimension2;
import bpm.gateway.core.transformations.outputs.SubTransformationFinalStep;
import bpm.gateway.core.transformations.utils.PreviousValueTransformation;
import bpm.gateway.core.transformations.vanilla.FreedashFormInput;
import bpm.gateway.core.transformations.vanilla.VanillaCommentExtraction;
import bpm.gateway.core.transformations.vanilla.VanillaCreateGroup;
import bpm.gateway.core.transformations.vanilla.VanillaCreateUser;
import bpm.gateway.core.transformations.vanilla.VanillaGroupUser;
import bpm.gateway.core.transformations.vanilla.VanillaLdapSynchro;
import bpm.gateway.core.transformations.vanilla.VanillaRoleGroupAssocaition;
import bpm.gateway.core.transformations.vanillamaps.VanillaMapAddressInput;
import bpm.gateway.core.transformations.vanillamaps.VanillaMapAddressOutput;
import bpm.gateway.core.transformations.webservice.WebServiceInput;
import bpm.gateway.core.transformations.webservice.WebServiceVanillaInput;
import bpm.gateway.core.tsbn.ConnectorAffaireXML;
import bpm.gateway.core.tsbn.ConnectorAppelXML;
import bpm.gateway.core.tsbn.ConnectorReferentielXML;
import bpm.gateway.core.tsbn.rpu.RpuConnector;
import bpm.gateway.core.tsbn.syrius.SyriusConnector;
import bpm.gateway.core.veolia.ConnectorAbonneXML;
import bpm.gateway.core.veolia.ConnectorPatrimoineXML;
import bpm.gateway.ui.palette.customizer.icons.IconsNames;

public class PaletteEntry {
	public static HashMap<String, List<PaletteEntry>> defaultPalette = new HashMap<String, List<PaletteEntry>>();
	
	static{
		String key = "Inputs";
		defaultPalette.put(key, new ArrayList<PaletteEntry>());
		defaultPalette.get(key).add(new PaletteEntry(DbAccessInput.class, "MsAccess Input", "Create an input stream based on sql query on an Access DataBase"));
		defaultPalette.get(key).add(new PaletteEntry(DataBaseInputStream.class, "Sql Input", "Create an input stream based on sql query"));
		defaultPalette.get(key).add(new PaletteEntry(CassandraInputStream.class, "Cassandra Input", "Create an input stream based on cql query on a Cassandra DB"));
		defaultPalette.get(key).add(new PaletteEntry(HbaseInputStream.class, "HBase Input", "Create an input stream based on a HBase DB"));
		defaultPalette.get(key).add(new PaletteEntry(FMDTInput.class, "Metadata Input", "Create an input stream based on Metadata query"));
		defaultPalette.get(key).add(new PaletteEntry(FileFolderReader.class, "File Folder Input", "Read all files(CSV, XLS, XML) from a Folder matching to a given pattern. "));
		defaultPalette.get(key).add(new PaletteEntry(FileInputCSV.class, "CSV Input", "Create an input stream based on a CSV file"));
		defaultPalette.get(key).add(new PaletteEntry(FileInputVCL.class, "VCL Input", "Create an input stream based on a file with variable columns lengths"));
		defaultPalette.get(key).add(new PaletteEntry(FileInputXLS.class, "XLS Input", "Create an input stream based on an XLS file"));
		defaultPalette.get(key).add(new PaletteEntry(FileInputXML.class, "XML Input", "Create an input stream based on an XML file"));
		defaultPalette.get(key).add(new PaletteEntry(FileInputShape.class, "Shape Input", "Create an input stream based on an Shape file and save it to Norparena"));
		defaultPalette.get(key).add(new PaletteEntry(KMLInput.class, "KML Input", "Extract spatial informations form a KML File"));
		defaultPalette.get(key).add(new PaletteEntry(FreedashFormInput.class, "FD Form Input", "Datas are coming from a FreeDashboard Form"));
		defaultPalette.get(key).add(new PaletteEntry(LdapInput.class, "Ldap Input", "Create an input stream from a server LDAP"));
		defaultPalette.get(key).add(new PaletteEntry(LdapMultipleMembers.class, "Ldap Multiple Member Extractor", "Extract all same attribute values from a Node"));
		defaultPalette.get(key).add(new PaletteEntry(KPIList.class, "KPIList", "Select KPI from a Freemetric Server"));
		defaultPalette.get(key).add(new PaletteEntry(OlapFactExtractorTranformation.class, "OLAP Cube Input", "Extract datas from fact table filtered on Dimension"));
		defaultPalette.get(key).add(new PaletteEntry(OlapDimensionExtractor.class, "OLAP Dimension Input", "Extract datas from an Fasd Dimension"));
		defaultPalette.get(key).add(new PaletteEntry(OdaInputWithParameters.class, "OdaInput With Parameters", "Extract datas from an Oda DataSet with Parameters"));
		defaultPalette.get(key).add(new PaletteEntry(VanillaMapAddressInput.class, "Norparena Addresses", "Extract addresses from Norparena"));
		defaultPalette.get(key).add(new PaletteEntry(VanillaAnalyticsGoogle.class, "Vanilla GoogleAnalytics", "Manage Google Analytics Data"));
		defaultPalette.get(key).add(new PaletteEntry(NagiosDb.class, "Database", "Manage data from Nagios"));
		defaultPalette.get(key).add(new PaletteEntry(WebServiceInput.class, "Web Service Input", "Create an input stream based on a web service query"));
		defaultPalette.get(key).add(new PaletteEntry(WebServiceVanillaInput.class, "Web Service Vanilla Input", "Create an input stream based on the Vanilla Web Service"));
		defaultPalette.get(key).add(new PaletteEntry(OracleXmlView.class, "Oracle XML View", "Create an input stream based on oracle xml view"));
		defaultPalette.get(key).add(new PaletteEntry(ConnectorAffaireXML.class, "TSBN Affaires XML", "Load an Affaire XML file"));
		defaultPalette.get(key).add(new PaletteEntry(ConnectorAppelXML.class, "TSBN Appels XML", "Load an Appel XML file"));
		defaultPalette.get(key).add(new PaletteEntry(ConnectorReferentielXML.class, "TSBN Referentiels XML", "Load an Referentiel XML file"));
		defaultPalette.get(key).add(new PaletteEntry(SyriusConnector.class, "TSBN Syrius", "Syrius connector"));
		defaultPalette.get(key).add(new PaletteEntry(RpuConnector.class, "TSBN RPU", "RPU connector"));
		defaultPalette.get(key).add(new PaletteEntry(D4CInput.class, "D4C Input", "D4C Input File"));
		defaultPalette.get(key).add(new PaletteEntry(FileInputPDFForm.class, "PDF Form Input", "PDF Form data extractor"));

		key = "Outputs";
		defaultPalette.put(key, new ArrayList<PaletteEntry>());
		defaultPalette.get(key).add(new PaletteEntry(DataBaseOutputStream.class, "Insert Sql", "Insert datas in SQL Table"));
		defaultPalette.get(key).add(new PaletteEntry(InfoBrightInjector.class, "InfoBright Injector", "Insert datas in InfoBright Brighthouse SQL Table"));
		defaultPalette.get(key).add(new PaletteEntry(FileOutputCSV.class, "CSV Output", "CSV File"));
		defaultPalette.get(key).add(new PaletteEntry(FileOutputVCL.class, "VCL Output", "Create an Text File with specified columns lengths."));
		defaultPalette.get(key).add(new PaletteEntry(FileOutputXML.class, "XML Output", "Create an XML File"));
		defaultPalette.get(key).add(new PaletteEntry(FileOutputXLS.class, "XLS Output", "Create an XLS File"));
		defaultPalette.get(key).add(new PaletteEntry(FileOutputXLS.class, "Muti Sheets XLS Output", "Create an XLS File with multiple sheets"));
		defaultPalette.get(key).add(new PaletteEntry(FileOutputWeka.class, "Weka Output", "Create a Weka File"));
		defaultPalette.get(key).add(new PaletteEntry(KMLOutput.class, "KML Output", "Generate a KML File from spatial datas"));
		defaultPalette.get(key).add(new PaletteEntry(OlapOutput.class, "OLAP Cube Output", "Perform insertions in Olap Cube's fact table"));
		defaultPalette.get(key).add(new PaletteEntry(KPIOutput.class, "Freemetrics KPI Output", "Create Freemetrics KPI values"));
		defaultPalette.get(key).add(new PaletteEntry(InsertOrUpdate.class, "Insert or Update", "Update if datas have changed or insert a new row"));
		defaultPalette.get(key).add(new PaletteEntry(SlowChangingDimension2.class, "SCD", "Slow Changing Dimension"));
		defaultPalette.get(key).add(new PaletteEntry(SubTransformationFinalStep.class, "SimpleOutput", "A simple output that only return all rows"));
		defaultPalette.get(key).add(new PaletteEntry(VanillaMapAddressOutput.class, "Norparena Addresses", "Insert addresses into Norparena"));
		defaultPalette.get(key).add(new PaletteEntry(CassandraOutputStream.class, "Cassandra Output", "Insert data in a Cassandra Column Family"));
		defaultPalette.get(key).add(new PaletteEntry(HBaseOutputStream.class, "HBase Output", "Insert data in a HBase Table"));
		defaultPalette.get(key).add(new PaletteEntry(CielComptaOutput.class, "Output Ciel Compta", "Create import ciel compta file"));
		defaultPalette.get(key).add(new PaletteEntry(GeoJsonOutput.class, "Output Geojson", "Create a geojson file"));
		 
		 key = "Transformations";
			defaultPalette.put(key, new ArrayList<PaletteEntry>());
		 defaultPalette.get(key).add(new PaletteEntry(Denormalisation.class, "Denormalize", "Denormalize a stream"));
		 defaultPalette.get(key).add(new PaletteEntry(ValidationCleansing.class, "Field Validator", "Allow to validate a field with some patterns."));
		 defaultPalette.get(key).add(new PaletteEntry(Normalize.class, "Dimension", "Create a hierarchy from one input stream."));
		 defaultPalette.get(key).add(new PaletteEntry(FilterDimension.class, "FilterDimension", "Filter a Stream using a Dimension"));
		 defaultPalette.get(key).add(new PaletteEntry(SqlScript.class, "Sql Script", "Run a SQL script"));
		 defaultPalette.get(key).add(new PaletteEntry(SortTransformation.class, "Sort Stream", "Sort a unique Streams"));
		 defaultPalette.get(key).add(new PaletteEntry(SelectDistinct.class, "Select Distinct", "Remove all de rows with same values. The removed rows are lost."));
		 defaultPalette.get(key).add(new PaletteEntry(MergeStreams.class, "Merge Streams", "Merge multiple Streams. The streams must have the same number of columns."));
		 defaultPalette.get(key).add(new PaletteEntry(SimpleMappingTransformation.class, "SimpleMapper", "SimpleMapping"));
		 defaultPalette.get(key).add(new PaletteEntry(SelectionTransformation.class, "Selection", "Selection"));
		 defaultPalette.get(key).add(new PaletteEntry(PreviousValueTransformation.class, "Previous values", "Previous values"));
		 defaultPalette.get(key).add(new PaletteEntry(EncryptTransformation.class, "Encrypt", "Encrypt"));
		 defaultPalette.get(key).add(new PaletteEntry(Lookup.class, "Lookup", "Lookup"));
		 defaultPalette.get(key).add(new PaletteEntry(SqlLookup.class, "SQLLookup", "SQLLookup"));
		 defaultPalette.get(key).add(new PaletteEntry(CartesianProduct.class, "Cartesian Product", "Cartesian Product"));
		 defaultPalette.get(key).add(new PaletteEntry(Filter.class, "Filter", "Filter"));
		 defaultPalette.get(key).add(new PaletteEntry(AggregateTransformation.class, "Aggregate", "Aggregate on columns"));
		 defaultPalette.get(key).add(new PaletteEntry(UnduplicateRows.class, "Unduplicate Rows", "Remove rows with same value. \r\nThe removed rows can be get in the Trash Transformation"));
		 defaultPalette.get(key).add(new PaletteEntry(DeleteRows.class, "Delete Rows", "Delete Rows"));
		 defaultPalette.get(key).add(new PaletteEntry(FieldSplitter.class, "Field Spliter", "Field Spliter"));
		 defaultPalette.get(key).add(new PaletteEntry(RowsFieldSplitter.class, "Field Spliter on Rows", "Field Spliter on Rows"));
		 defaultPalette.get(key).add(new PaletteEntry(Calculation.class, "Calculation", "Calculation"));
		 defaultPalette.get(key).add(new PaletteEntry(RangingTransformation.class, "Ranging", "Ranging"));
		 defaultPalette.get(key).add(new PaletteEntry(GeoFilter.class, "GeoFilter", "GeoFilter"));
		 defaultPalette.get(key).add(new PaletteEntry(Sequence.class, "Sequence", "Sequence"));
		 defaultPalette.get(key).add(new PaletteEntry(SurrogateKey.class, "Surrogate Key", "Surrogate Key"));
		 defaultPalette.get(key).add(new PaletteEntry(SubTransformation.class, "Sub Transformation", "Sub Transformation"));
		 defaultPalette.get(key).add(new PaletteEntry(TopXTransformation.class, "Top X", "Top X"));
		 defaultPalette.get(key).add(new PaletteEntry(SqoopTransformation.class, "Sqoop Transformation", "Transfert data from HDFS to DB or from DB to HDFS"));
		 defaultPalette.get(key).add(new PaletteEntry(ConnectorAbonneXML.class, "Connecteur Abonne XML", "Transfert data from Abonne XML to DB or from DB to XML"));
		 defaultPalette.get(key).add(new PaletteEntry(ConnectorPatrimoineXML.class, "Connecteur Patrimoine XML", "Transfert data from Patrimoine XML to DB"));
		 defaultPalette.get(key).add(new PaletteEntry(Geoloc.class, "Geoloc", "Get geoloc from an adress"));
		 defaultPalette.get(key).add(new PaletteEntry(GeoDispatch.class, "Geo Dispatch", "Geographic dispatcher"));
		 
		 key = "VanillaSpecific";
			defaultPalette.put(key, new ArrayList<PaletteEntry>());
		 defaultPalette.get(key).add(new PaletteEntry(VanillaGroupUser.class, "Vanilla User/Group", "Create one Vanilla Group and user per row and affect\r\n the user in the group in the platform."));
		 defaultPalette.get(key).add(new PaletteEntry(VanillaCreateUser.class, "Vanilla User Creation", "Create Vanilla Users."));
		 defaultPalette.get(key).add(new PaletteEntry(VanillaCreateGroup.class, "Vanilla Group Creation", "Create Vanilla Groups."));
		 defaultPalette.get(key).add(new PaletteEntry(VanillaLdapSynchro.class, "Vanilla User/Group Ldap Synchro", "Synchronise the User/Group vanilla with a LDAP server"));
		 defaultPalette.get(key).add(new PaletteEntry(VanillaRoleGroupAssocaition.class, "Vanilla AssociateGroupRole", "Affect Roles to Groups"));
		 defaultPalette.get(key).add(new PaletteEntry(VanillaCommentExtraction.class, "Vanilla Comment Extraction", "Extraction model comments"));
		 
	}
	
public final static HashMap<Class, String> keyImages = new HashMap<Class, String>();
	
	static{
		keyImages.put(VanillaRoleGroupAssocaition.class,IconsNames.vanilla_role_16);
		keyImages.put(DbAccessInput.class,IconsNames.access_16);
		keyImages.put(FileFolderReader.class,IconsNames.file_folder_16);
		
		keyImages.put(VanillaLdapSynchro.class,IconsNames.ldapSynchro_16);
		keyImages.put(VanillaCreateGroup.class,IconsNames.vanilla_group_16);
		keyImages.put(VanillaCreateUser.class,IconsNames.vanillaUser_16);
		keyImages.put(VanillaGroupUser.class,IconsNames.group_16);
		

		
		keyImages.put(TopXTransformation.class,IconsNames.topX_16);
		keyImages.put(SubTransformation.class,IconsNames.gateway_16);
		keyImages.put(SurrogateKey.class,IconsNames.surrogate_16);
		keyImages.put( Sequence.class,IconsNames.sequence_16);
		keyImages.put( RangingTransformation.class,IconsNames.ranging_16);
		keyImages.put( GeoFilter.class,IconsNames.geoFilter16);
		keyImages.put( Calculation.class,IconsNames.calc_16);
		keyImages.put( GeoJsonOutput.class,IconsNames.geojson_16);
		keyImages.put(RowsFieldSplitter.class,IconsNames.split_row_16);
		keyImages.put(FieldSplitter.class,IconsNames.split_16);
		keyImages.put(DeleteRows.class,IconsNames.del_16);
		keyImages.put(UnduplicateRows.class,IconsNames.dedoublon_16);
		keyImages.put(AggregateTransformation.class,IconsNames.sum_16);
		keyImages.put(Filter.class,IconsNames.filter_16);
		keyImages.put(CartesianProduct.class,IconsNames.cartesian_16);
		keyImages.put(SqlLookup.class,IconsNames.database_lookup_16);
		keyImages.put(Lookup.class,IconsNames.lookup_16);
		keyImages.put(SelectionTransformation.class,IconsNames.select_all_16);
		keyImages.put(PreviousValueTransformation.class,IconsNames.prevval_16);
		keyImages.put(EncryptTransformation.class,IconsNames.anonyme_16);
		keyImages.put(SimpleMappingTransformation.class,IconsNames.link_16);
		keyImages.put(MergeStreams.class, IconsNames.merge_16);
		keyImages.put(SelectDistinct.class,IconsNames.distinct_16);
		keyImages.put(SortTransformation.class,IconsNames.sort_16);
		keyImages.put(SqlScript.class,IconsNames.scriptSql_16);
		keyImages.put(FilterDimension.class,IconsNames.filer_dimension);
		keyImages.put(Normalize.class,IconsNames.dimension);
		keyImages.put(Denormalisation.class,IconsNames.pivot_16);
		
		
		
		
		keyImages.put(DataBaseOutputStream.class,IconsNames.table_row_insert_16);
		keyImages.put(InfoBrightInjector.class,IconsNames.infobright_16);
		keyImages.put(FileOutputCSV.class,IconsNames.file_in_16);
		keyImages.put(FileOutputVCL.class,IconsNames.file_vcl_out);
		keyImages.put(FileOutputXML.class,IconsNames.XMLFile_16);
		keyImages.put(FileOutputXLS.class,IconsNames.xls_out_16);
		keyImages.put(FileOutputWeka.class,IconsNames.WEKA_16);
		keyImages.put(KMLOutput.class,IconsNames.kml_output);
		keyImages.put(OlapOutput.class,IconsNames.cube_output_16);
		keyImages.put(KPIOutput.class,IconsNames.metric_16);
		keyImages.put(InsertOrUpdate.class,IconsNames.update_16);
		keyImages.put(SlowChangingDimension2.class,IconsNames.scd2_16);
		keyImages.put(SubTransformationFinalStep.class,IconsNames.simpleoutput_16);
		keyImages.put(CielComptaOutput.class,IconsNames.table_row_insert_16);
		
		
		keyImages.put(OdaInputWithParameters.class,IconsNames.oda_16);
		keyImages.put(OdaInput.class,IconsNames.oda_16);
		keyImages.put(OlapDimensionExtractor.class,IconsNames.cube_dim_16);
		keyImages.put(OlapFactExtractorTranformation.class,IconsNames.cube_input_16);
		keyImages.put(KPIList.class,IconsNames.metric_16);
//		keyImages.put(GlobalDefinitionInput.class,IconsNames.gid_16);
		keyImages.put(FreedashFormInput.class,IconsNames.fd_input);
		keyImages.put(KMLInput.class,IconsNames.kml_input);
		keyImages.put(FileInputXML.class,IconsNames.XMLFile_16_input);
		keyImages.put(FileInputXLS.class,IconsNames.xls_16);
		keyImages.put(FileInputVCL.class,IconsNames.file_vcl);
		keyImages.put(FileInputCSV.class,IconsNames.file_go_16);
		keyImages.put(FileInputShape.class, IconsNames.SHAPE_16);
		keyImages.put(FMDTInput.class,IconsNames.fmdt_input_16);
		keyImages.put(DataBaseInputStream.class,IconsNames.table_go_16);
		keyImages.put(OracleXmlView.class,IconsNames.table_go_16);
		keyImages.put(ConnectorAbonneXML.class,IconsNames.VEOLIA_16);
		keyImages.put(ConnectorPatrimoineXML.class,IconsNames.VEOLIA_16);
		keyImages.put(D4CInput.class, IconsNames.d4c_16);
		keyImages.put(FileInputPDFForm.class, IconsNames.ic_pdf_form_16);
		
		keyImages.put(LdapInput.class,IconsNames.ldap_input_16);
		keyImages.put(LdapMultipleMembers.class,IconsNames.ldapMember_16);
		keyImages.put(VanillaAnalyticsGoogle.class, IconsNames.googleAnalytics_16);
		keyImages.put(VanillaCommentExtraction.class, IconsNames.COMMENT_EXTRACTION_16);
		keyImages.put(CassandraInputStream.class, IconsNames.cassandra_16);
		keyImages.put(CassandraOutputStream.class, IconsNames.cassandra_16);
		keyImages.put(WebServiceInput.class, IconsNames.WEB_SERVICE_16);
		keyImages.put(WebServiceVanillaInput.class, IconsNames.WEB_SERVICE_VANILLA_16);
		keyImages.put(Geoloc.class, IconsNames.geojson_16);
		keyImages.put(GeoDispatch.class, IconsNames.geodispatch_16);
		
		keyImages.put(SqoopTransformation.class, IconsNames.SQOOP_16);
		keyImages.put(ConnectorAffaireXML.class, IconsNames.SQOOP_16);
		keyImages.put(ConnectorAppelXML.class, IconsNames.SQOOP_16);
		keyImages.put(ConnectorReferentielXML.class, IconsNames.SQOOP_16);
		keyImages.put(SyriusConnector.class, IconsNames.SQOOP_16);
		keyImages.put(RpuConnector.class, IconsNames.SQOOP_16);
	}
	
	private Class<? extends Transformation> transformationClass;
	private String entryName;
	private String entryDescription;
	/**
	 * @param transformationClass
	 * @param entryName
	 * @param entryDescription
	 */
	public PaletteEntry(Class<? extends Transformation> transformationClass,
			String entryName, String entryDescription) {
		super();
		this.transformationClass = transformationClass;
		this.entryName = entryName;
		this.entryDescription = entryDescription;
	}
	public PaletteEntry(PaletteEntry e) {
		this.entryDescription = new String(e.getEntryDescription());
		this.entryName = new String(e.getEntryName());
		this.transformationClass = e.getTransformationClass();
	}
	/**
	 * @return the transformationClass
	 */
	public Class<? extends Transformation> getTransformationClass() {
		return transformationClass;
	}
	/**
	 * @return the entryName
	 */
	public String getEntryName() {
		return entryName;
	}
	/**
	 * @return the entryDescription
	 */
	public String getEntryDescription() {
		return entryDescription;
	}
	
	
}
