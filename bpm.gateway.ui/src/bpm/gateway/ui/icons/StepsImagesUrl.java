package bpm.gateway.ui.icons;

import java.net.URL;
import java.util.HashMap;

import bpm.gateway.core.transformations.AggregateTransformation;
import bpm.gateway.core.transformations.AlterationStream;
import bpm.gateway.core.transformations.CartesianProduct;
import bpm.gateway.core.transformations.ConsitencyTransformation;
import bpm.gateway.core.transformations.DeleteRows;
import bpm.gateway.core.transformations.EncryptTransformation;
import bpm.gateway.core.transformations.FieldSplitter;
import bpm.gateway.core.transformations.Filter;
import bpm.gateway.core.transformations.FilterDimension;
import bpm.gateway.core.transformations.Geoloc;
import bpm.gateway.core.transformations.GlobalDefinitionInput;
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
import bpm.gateway.core.transformations.googleanalytics.VanillaAnalyticsGoogle;
import bpm.gateway.core.transformations.inputs.CassandraInputStream;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.core.transformations.inputs.DataPreparationInput;
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
import bpm.gateway.core.transformations.inputs.MongoDbInputStream;
import bpm.gateway.core.transformations.inputs.NagiosDb;
import bpm.gateway.core.transformations.inputs.OdaInput;
import bpm.gateway.core.transformations.inputs.OracleXmlView;
import bpm.gateway.core.transformations.kml.KMLInput;
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
import bpm.gateway.core.transformations.outputs.FreemetricsKPI;
import bpm.gateway.core.transformations.outputs.GeoJsonOutput;
import bpm.gateway.core.transformations.outputs.HBaseOutputStream;
import bpm.gateway.core.transformations.outputs.InfoBrightInjector;
import bpm.gateway.core.transformations.outputs.InsertOrUpdate;
import bpm.gateway.core.transformations.outputs.MongoDbOutputStream;
import bpm.gateway.core.transformations.outputs.SlowChangingDimension1;
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
import bpm.gateway.ui.Activator;

public class StepsImagesUrl {
	private static final HashMap<Class<?>,URL> urls = new HashMap<Class<?>, URL>();
	
	public static HashMap<Class<?>,URL> getImages(){
		
		if (!urls.isEmpty()){
			return urls;
		}
		urls.put(SubTransformationFinalStep.class, IconsNames.class.getResource(IconsNames.simpleoutput_32)); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FreedashFormInput.class, IconsNames.class.getResource(IconsNames.fd_input_32)); //$NON-NLS-1$ //$NON-NLS-2$

		urls.put(KMLInput.class, IconsNames.class.getResource(IconsNames.kml_input_32.endsWith(".png") ? IconsNames.kml_input_32 : IconsNames.kml_input_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(VanillaMapAddressInput.class, IconsNames.class.getResource(IconsNames.norparena_adress_input_32.endsWith(".png") ? IconsNames.norparena_adress_input_32 : IconsNames.norparena_adress_input_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(VanillaMapAddressOutput.class, IconsNames.class.getResource(IconsNames.norparena_adress_output_32.endsWith(".png") ? IconsNames.norparena_adress_output_32 : IconsNames.norparena_adress_output_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		urls.put(Normalize.class, ValidationCleansing.class.getResource(IconsNames.regex_32.endsWith(".png") ? IconsNames.regex_32 : IconsNames.regex_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(Normalize.class, IconsNames.class.getResource(IconsNames.dimension_32.endsWith(".png") ? IconsNames.dimension_32 : IconsNames.dimension_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FilterDimension.class, IconsNames.class.getResource(IconsNames.filer_dimension_32.endsWith(".png") ? IconsNames.filer_dimension_32 : IconsNames.filer_dimension_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FileInputVCL.class, IconsNames.class.getResource(IconsNames.file_vcl_32.endsWith(".png") ? IconsNames.file_vcl_32 : IconsNames.file_vcl_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FileOutputVCL.class, IconsNames.class.getResource(IconsNames.file_vcl_out_32.endsWith(".png") ? IconsNames.file_vcl_out_32 : IconsNames.file_vcl_out_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(VanillaRoleGroupAssocaition.class, IconsNames.class.getResource(IconsNames.vanilla_role_32.endsWith(".png") ? IconsNames.vanilla_role_32 : IconsNames.vanilla_role_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(Denormalisation.class, IconsNames.class.getResource(IconsNames.pivot_32 .endsWith(".png")? IconsNames.pivot_32 : IconsNames.pivot_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(LdapMultipleMembers.class, IconsNames.class.getResource(IconsNames.ldapMember_32.endsWith(".png") ? IconsNames.ldapMember_32 : IconsNames.ldapMember_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(VanillaCreateUser.class, IconsNames.class.getResource(IconsNames.vanillaUser_32.endsWith(".png") ? IconsNames.vanillaUser_32 : IconsNames.vanillaUser_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(OlapFactExtractorTranformation.class, IconsNames.class.getResource(IconsNames.cube_input_32.endsWith(".png") ? IconsNames.cube_input_32 : IconsNames.cube_input_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(OlapOutput.class, IconsNames.class.getResource(IconsNames.cube_output_32.endsWith(".png") ? IconsNames.cube_output_32 : IconsNames.cube_output_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(VanillaLdapSynchro.class, IconsNames.class.getResource(IconsNames.ldapSynchro_32.endsWith(".png") ? IconsNames.ldapSynchro_32 : IconsNames.ldapSynchro_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(VanillaGroupUser.class, IconsNames.class.getResource(IconsNames.group_32.endsWith(".png") ? IconsNames.group_32 : IconsNames.group_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(SqlScript.class, IconsNames.class.getResource(IconsNames.scriptSql_32 .endsWith(".png")? IconsNames.scriptSql_32 : IconsNames.scriptSql_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(TopXTransformation.class, IconsNames.class.getResource(IconsNames.topX_32.endsWith(".png") ? IconsNames.topX_32 : IconsNames.topX_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FileInputXML.class, IconsNames.class.getResource(IconsNames.XMLFile_32_input.endsWith(".png")? IconsNames.XMLFile_32_input : IconsNames.XMLFile_32_input + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(GlobalDefinitionInput.class, IconsNames.class.getResource(IconsNames.gid_32.endsWith(".png") ? IconsNames.gid_32 : IconsNames.gid_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(AlterationStream.class, IconsNames.class.getResource(IconsNames.transtyp_32 .endsWith(".png")? IconsNames.transtyp_32 : IconsNames.transtyp_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FileInputXML.class, IconsNames.class.getResource(IconsNames.XMLFile_32_input .endsWith(".png")? IconsNames.XMLFile_32_input : IconsNames.XMLFile_32_input + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(CartesianProduct.class, IconsNames.class.getResource(IconsNames.cartesian_32 .endsWith(".png")? IconsNames.cartesian_32 : IconsNames.cartesian_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(RowsFieldSplitter.class, IconsNames.class.getResource(IconsNames.split_row_32.endsWith(".png") ? IconsNames.split_row_32 : IconsNames.split_row_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(SlowChangingDimension2.class, IconsNames.class.getResource(IconsNames.scd2_32.endsWith(".png") ? IconsNames.scd2_32 : IconsNames.scd2_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(SlowChangingDimension1.class, IconsNames.class.getResource(IconsNames.scd_32.endsWith(".png") ? IconsNames.scd_32 : IconsNames.scd_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(InsertOrUpdate.class, IconsNames.class.getResource(IconsNames.update_32.endsWith(".png") ? IconsNames.update_32 : IconsNames.update_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(DeleteRows.class, IconsNames.class.getResource(IconsNames.del_32.endsWith(".png") ? IconsNames.del_32 : IconsNames.del_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(Sequence.class, IconsNames.class.getResource(IconsNames.sequence_32.endsWith(".png") ? IconsNames.sequence_32 : IconsNames.sequence_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(SubTransformation.class, IconsNames.class.getResource(IconsNames.gateway_32.endsWith(".png") ? IconsNames.gateway_32 : IconsNames.gateway_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(SortTransformation.class, IconsNames.class.getResource(IconsNames.sort_32.endsWith(".png") ? IconsNames.sort_32 : IconsNames.sort_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(RangingTransformation.class, IconsNames.class.getResource(IconsNames.ranging_32.endsWith(".png") ? IconsNames.ranging_32 : IconsNames.ranging_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(GeoFilter.class, IconsNames.class.getResource(IconsNames.geoFilter_32.endsWith(".png") ? IconsNames.geoFilter_32 : IconsNames.geoFilter_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(AggregateTransformation.class, IconsNames.class.getResource(IconsNames.sum_32.endsWith(".png") ? IconsNames.sum_32 : IconsNames.sum_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(DataBaseOutputStream.class, IconsNames.class.getResource(IconsNames.table_row_insert_32.endsWith(".png") ? IconsNames.table_row_insert_32 : IconsNames.table_row_insert_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(CielComptaOutput.class, IconsNames.class.getResource(IconsNames.table_row_insert_32.endsWith(".png") ? IconsNames.table_row_insert_32 : IconsNames.table_row_insert_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(DataBaseInputStream.class, IconsNames.class.getResource("table_go.png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FileInputCSV.class, IconsNames.class.getResource(IconsNames.file_go_32.endsWith(".png") ? IconsNames.file_go_32 : IconsNames.file_go_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FileOutputCSV.class, IconsNames.class.getResource(IconsNames.file_in_32.endsWith(".png") ? IconsNames.file_in_32 : IconsNames.file_in_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FileOutputWeka.class, IconsNames.class.getResource(IconsNames.WEKA_32));
		urls.put(FileInputShape.class, IconsNames.class.getResource(IconsNames.SHAPE_32.endsWith(".png") ? IconsNames.SHAPE_32 : IconsNames.SHAPE_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(Lookup.class, IconsNames.class.getResource(IconsNames.lookup_32.endsWith(".png") ? IconsNames.lookup_32 : IconsNames.lookup_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(SimpleMappingTransformation.class, IconsNames.class.getResource(IconsNames.link_32.endsWith(".png") ? IconsNames.link_32 : IconsNames.link_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(SelectionTransformation.class, IconsNames.class.getResource(IconsNames.select_all_32.endsWith(".png") ? IconsNames.select_all_32 : IconsNames.select_all_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(PreviousValueTransformation.class, IconsNames.class.getResource(IconsNames.prevval_32.endsWith(".png") ? IconsNames.prevval_32 : IconsNames.prevval_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(EncryptTransformation.class, IconsNames.class.getResource(IconsNames.anonyme_32.endsWith(".png") ? IconsNames.anonyme_32 : IconsNames.anonyme_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FMDTInput.class, IconsNames.class.getResource(IconsNames.fmdt_input_32.endsWith(".png") ? IconsNames.fmdt_input_32 : IconsNames.fmdt_input_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FileOutputXML.class, IconsNames.class.getResource(IconsNames.XMLFile_32.endsWith(".png") ? IconsNames.XMLFile_32 : IconsNames.XMLFile_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FreemetricsKPI.class, IconsNames.class.getResource(IconsNames.metric_32.endsWith(".png") ? IconsNames.metric_32 : IconsNames.metric_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(Filter.class, IconsNames.class.getResource(IconsNames.filter_32.endsWith(".png") ? IconsNames.filter_32 : IconsNames.filter_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FileOutputXLS.class, IconsNames.class.getResource(IconsNames.xls_out_32.endsWith(".png") ? IconsNames.xls_out_32 : IconsNames.xls_out_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FileInputXLS.class, IconsNames.class.getResource(IconsNames.xls_32.endsWith(".png") ? IconsNames.xls_32 : IconsNames.xls_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(FieldSplitter.class, IconsNames.class.getResource(IconsNames.split_32.endsWith(".png") ? IconsNames.split_32 : IconsNames.split_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(Calculation.class, IconsNames.class.getResource(IconsNames.calc_32.endsWith(".png") ? IconsNames.calc_32 : IconsNames.calc_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(GeoJsonOutput.class, IconsNames.class.getResource(IconsNames.geojson_32.endsWith(".png") ? IconsNames.geojson_32 : IconsNames.geojson_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(UnduplicateRows.class, IconsNames.class.getResource(IconsNames.dedoublon_32.endsWith(".png") ? IconsNames.dedoublon_32 : IconsNames.dedoublon_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(SelectDistinct.class, IconsNames.class.getResource(IconsNames.distinct_32.endsWith(".png") ? IconsNames.distinct_32 : IconsNames.distinct_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(MergeStreams.class, IconsNames.class.getResource(IconsNames.merge_32.endsWith(".png") ? IconsNames.merge_32 : IconsNames.merge_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(LdapInput.class, IconsNames.class.getResource(IconsNames.ldap_input_32.endsWith(".png") ? IconsNames.ldap_input_32 : IconsNames.ldap_input_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(InfoBrightInjector.class, IconsNames.class.getResource(IconsNames.infobright_32.endsWith(".png") ? IconsNames.infobright_32 : IconsNames.infobright_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(OdaInput.class, IconsNames.class.getResource(IconsNames.oda_32.endsWith(".png") ? IconsNames.oda_32 : IconsNames.oda_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(SurrogateKey.class, IconsNames.class.getResource(IconsNames.surrogate_32.endsWith(".png") ? IconsNames.surrogate_32 : IconsNames.surrogate_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(ConsitencyTransformation.class, IconsNames.class.getResource(IconsNames.consistency_32.endsWith(".png") ? IconsNames.consistency_32 : IconsNames.consistency_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(SqlLookup.class, IconsNames.class.getResource(IconsNames.database_lookup_32.endsWith(".png") ? IconsNames.database_lookup_32 : IconsNames.database_lookup_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(OlapDimensionExtractor.class, IconsNames.class.getResource(IconsNames.cube_dim_32.endsWith(".png") ? IconsNames.cube_dim_32 : IconsNames.cube_dim_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(VanillaCreateGroup.class, IconsNames.class.getResource(IconsNames.vanilla_group_32.endsWith(".png") ? IconsNames.vanilla_group_32 : IconsNames.vanilla_group_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(VanillaAnalyticsGoogle.class, IconsNames.class.getResource(IconsNames.googleAnalytics_32.endsWith(".png") ? IconsNames.googleAnalytics_32 : IconsNames.googleAnalytics_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(NagiosDb.class, IconsNames.class.getResource(IconsNames.nagios_32.endsWith(".png") ? IconsNames.nagios_32 : IconsNames.nagios_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(VanillaCommentExtraction.class, IconsNames.class.getResource(IconsNames.COMMENT_EXTRACTION_32.endsWith(".png") ? IconsNames.COMMENT_EXTRACTION_32 : IconsNames.COMMENT_EXTRACTION_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(CassandraInputStream.class, IconsNames.class.getResource(IconsNames.cassandra_32.endsWith(".png") ? IconsNames.cassandra_32 : IconsNames.cassandra_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(CassandraOutputStream.class, IconsNames.class.getResource(IconsNames.cassandra_32.endsWith(".png") ? IconsNames.cassandra_32 : IconsNames.cassandra_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(HbaseInputStream.class, IconsNames.class.getResource(IconsNames.HBASE_32.endsWith(".png") ? IconsNames.HBASE_32 : IconsNames.HBASE_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(HBaseOutputStream.class, IconsNames.class.getResource(IconsNames.HBASE_32.endsWith(".png") ? IconsNames.HBASE_32 : IconsNames.HBASE_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(MongoDbInputStream.class, IconsNames.class.getResource(IconsNames.MONGODB_32.endsWith(".png") ? IconsNames.MONGODB_32 : IconsNames.MONGODB_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(MongoDbOutputStream.class, IconsNames.class.getResource(IconsNames.MONGODB_32.endsWith(".png") ? IconsNames.MONGODB_32 : IconsNames.MONGODB_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(WebServiceInput.class, IconsNames.class.getResource(IconsNames.WEB_SERVICE_32.endsWith(".png") ? IconsNames.WEB_SERVICE_32 : IconsNames.WEB_SERVICE_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(WebServiceVanillaInput.class, IconsNames.class.getResource(IconsNames.WEB_SERVICE_VANILLA_32.endsWith(".png") ? IconsNames.WEB_SERVICE_VANILLA_32 : IconsNames.WEB_SERVICE_VANILLA_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(SqoopTransformation.class, IconsNames.class.getResource(IconsNames.SQOOP_32));
		urls.put(OracleXmlView.class, IconsNames.class.getResource("table_go.png")); //$NON-NLS-1$
		urls.put(ConnectorAbonneXML.class, IconsNames.class.getResource(IconsNames.XML_32));
		urls.put(ConnectorPatrimoineXML.class, IconsNames.class.getResource(IconsNames.XML_32));
		urls.put(ConnectorAffaireXML.class, IconsNames.class.getResource(IconsNames.TSBN_32));
		urls.put(ConnectorAppelXML.class, IconsNames.class.getResource(IconsNames.TSBN_32));
		urls.put(ConnectorReferentielXML.class, IconsNames.class.getResource(IconsNames.TSBN_32));
		urls.put(SyriusConnector.class, IconsNames.class.getResource(IconsNames.TSBN_32));
		urls.put(RpuConnector.class, IconsNames.class.getResource(IconsNames.TSBN_32));
		urls.put(D4CInput.class, IconsNames.class.getResource(IconsNames.d4c_32));
		urls.put(FileInputPDFForm.class, IconsNames.class.getResource(IconsNames.ic_pdf_form_32));
		urls.put(DataPreparationInput.class, IconsNames.class.getResource(IconsNames.DataPrep_32));
		urls.put(Geoloc.class, IconsNames.class.getResource(IconsNames.geojson_32.endsWith(".png") ? IconsNames.geojson_32 : IconsNames.geojson_32 + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(GeoDispatch.class, IconsNames.class.getResource(IconsNames.geodispatch_32));
		
		return urls;
	}
}
