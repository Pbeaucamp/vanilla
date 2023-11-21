package bpm.gateway.ui.viewer;



import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

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
import bpm.gateway.core.transformations.NullTransformation;
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
import bpm.gateway.core.transformations.googleanalytics.VanillaAnalyticsGoogle;
import bpm.gateway.core.transformations.inputs.CassandraInputStream;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.core.transformations.inputs.DataPreparationInput;
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
import bpm.gateway.core.transformations.inputs.MongoDbInputStream;
import bpm.gateway.core.transformations.inputs.NagiosDb;
import bpm.gateway.core.transformations.inputs.OdaInput;
import bpm.gateway.core.transformations.inputs.OracleXmlView;
import bpm.gateway.core.transformations.kml.KMLInput;
import bpm.gateway.core.transformations.kml.KMLOutput;
import bpm.gateway.core.transformations.mdm.MdmContractFileInput;
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
import bpm.gateway.core.transformations.outputs.HBaseOutputStream;
import bpm.gateway.core.transformations.outputs.InfoBrightInjector;
import bpm.gateway.core.transformations.outputs.InsertOrUpdate;
import bpm.gateway.core.transformations.outputs.MongoDbOutputStream;
import bpm.gateway.core.transformations.outputs.MultiFolderXLS;
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
import bpm.gateway.ui.icons.IconsNames;
import bpm.vanilla.platform.core.IRepositoryApi;




public class TreeLabelProvider extends LabelProvider {	
	@Override
	public String getText(Object obj) {
		return obj.toString();
	}
	@Override
	public Image getImage(Object obj) {
		
		if (obj instanceof TreeStaticObject){
			return Activator.getDefault().getImageRegistry().get(IconsNames.folder_16);
		}
		if (obj instanceof RangingTransformation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.ranging_16);
		}
		if (obj instanceof GeoFilter){
			return Activator.getDefault().getImageRegistry().get(IconsNames.geoFilter);
		}
		
		if (obj instanceof TreeDirectory){
			return Activator.getDefault().getImageRegistry().get(IconsNames.folder_16);
		}
		if (obj instanceof TreeItem){
			if(((TreeItem)obj).getItem().getType() == IRepositoryApi.CUST_TYPE){
				return Activator.getDefault().getImageRegistry().get(IconsNames.forms_16);
			}
			else if (((TreeItem)obj).getItem().getType() == IRepositoryApi.FMDT_TYPE){
				return Activator.getDefault().getImageRegistry().get(IconsNames.fmdt_16);
			}
			else if (((TreeItem)obj).getItem().getType() == IRepositoryApi.GTW_TYPE){
				return Activator.getDefault().getImageRegistry().get(IconsNames.gateway_16);
			}
			else{
				return Activator.getDefault().getImageRegistry().get(IconsNames.fmdt_16);
			}
			
		}
		if (obj instanceof MultiFolderXLS){
			return Activator.getDefault().getImageRegistry().get(IconsNames.multi_xls_out_16);
		}
		if (obj instanceof VanillaMapAddressInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.norparena_adress_input_16);
		}
		if (obj instanceof VanillaMapAddressOutput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.norparena_adress_output_16);
		}
		if (obj instanceof KMLInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.kml_input);
		}
		if (obj instanceof DbAccessInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.access_16);
		}
		if (obj instanceof FileFolderReader){
			return Activator.getDefault().getImageRegistry().get(IconsNames.file_folder_16);
		}
		if (obj instanceof ValidationCleansing){
			return Activator.getDefault().getImageRegistry().get(IconsNames.regex_16);
		}
		if (obj instanceof KMLOutput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.kml_output);
		}
		if (obj instanceof FreedashFormInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.fd_input);
		}
		if (obj instanceof OdaInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.oda_32);
		}
		if (obj instanceof FileInputVCL){
			return Activator.getDefault().getImageRegistry().get(IconsNames.file_vcl);
		}
		if (obj instanceof FileOutputVCL){
			return Activator.getDefault().getImageRegistry().get(IconsNames.file_vcl_out);
		}
		if (obj instanceof Normalize){
			return Activator.getDefault().getImageRegistry().get(IconsNames.dimension);
		}
		if (obj instanceof FilterDimension){
			return Activator.getDefault().getImageRegistry().get(IconsNames.filer_dimension);
		}
		if (obj instanceof VanillaRoleGroupAssocaition){
			return Activator.getDefault().getImageRegistry().get(IconsNames.vanilla_role_32);
		}
		if (obj instanceof VanillaCreateGroup){
			return Activator.getDefault().getImageRegistry().get(IconsNames.vanilla_group_16);
		}
		if (obj instanceof SubTransformationFinalStep){
			return Activator.getDefault().getImageRegistry().get(IconsNames.simpleoutput_16);
		}
		if (obj instanceof VanillaCreateUser){
			return Activator.getDefault().getImageRegistry().get(IconsNames.vanillaUser_16);
		}
		if (obj instanceof LdapMultipleMembers){
			return Activator.getDefault().getImageRegistry().get(IconsNames.ldapMember_16);
		}
		if (obj instanceof SurrogateKey){
			return  Activator.getDefault().getImageRegistry().get(IconsNames.surrogate_32);
		}
		if (obj instanceof ConsitencyTransformation){
			return  Activator.getDefault().getImageRegistry().get(IconsNames.consistency_32);
		}
		if ( obj instanceof SqlLookup){
			return  Activator.getDefault().getImageRegistry().get(IconsNames.database_lookup_32);
		}
		if (obj instanceof OlapDimensionExtractor){
			return  Activator.getDefault().getImageRegistry().get(IconsNames.cube_dim_32);
		}
		if (obj instanceof InfoBrightInjector){
			return Activator.getDefault().getImageRegistry().get(IconsNames.infobright_16);
		}
		if (obj instanceof OlapFactExtractorTranformation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.cube_input_16);
		}
		if (obj instanceof OlapOutput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.cube_output_16);
		}
		if (obj instanceof VanillaLdapSynchro){
			return Activator.getDefault().getImageRegistry().get(IconsNames.ldapSynchro_16);
		}
		if (obj instanceof VanillaGroupUser){
			return Activator.getDefault().getImageRegistry().get(IconsNames.group_16);
		}
		if (obj instanceof SqlScript){
			return Activator.getDefault().getImageRegistry().get(IconsNames.scriptSql_16);
		}
		if (obj instanceof Denormalisation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.pivot_16);
		}
		if (obj instanceof AlterationStream){
			return Activator.getDefault().getImageRegistry().get(IconsNames.transtyp_16);
		}
		if (obj instanceof GlobalDefinitionInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.gid_16);
		}
		if (obj instanceof FreemetricsKPI){
			return Activator.getDefault().getImageRegistry().get(IconsNames.metric_16);
		}
		if (obj instanceof CartesianProduct){
			return Activator.getDefault().getImageRegistry().get(IconsNames.cartesian_16);
		}
		if (obj instanceof RowsFieldSplitter){
			return Activator.getDefault().getImageRegistry().get(IconsNames.split_row_16);
		}
		
		if (obj instanceof LdapInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.ldap_server_16);
		}
		if (obj instanceof FileInputXML){
			return Activator.getDefault().getImageRegistry().get(IconsNames.XMLFile_16_input);
		}
		if (obj instanceof SlowChangingDimension2){
			return Activator.getDefault().getImageRegistry().get(IconsNames.scd2_16);
		}
		if (obj instanceof SlowChangingDimension1){
			return Activator.getDefault().getImageRegistry().get(IconsNames.scd_16);
		}
		if (obj instanceof InsertOrUpdate){
			return Activator.getDefault().getImageRegistry().get(IconsNames.update_16);
		}
		if (obj instanceof SortTransformation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.sort_16);
		}
		if (obj instanceof Sequence){
			return Activator.getDefault().getImageRegistry().get(IconsNames.sequence_16);
		}
		if (obj instanceof SubTransformation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.gateway_16);
		}
		if (obj instanceof DeleteRows){
			return Activator.getDefault().getImageRegistry().get(IconsNames.del_16);
		}
		if (obj instanceof FieldSplitter){
			return Activator.getDefault().getImageRegistry().get(IconsNames.split_16);
		}
		if (obj instanceof SimpleMappingTransformation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.link_16);
		}
		if (obj instanceof SelectionTransformation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.select_all_16);
		}
		if (obj instanceof PreviousValueTransformation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.prevval_16);
		}
		if (obj instanceof EncryptTransformation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.anonyme_16);
		}
		if (obj instanceof FileOutputCSV){
			return Activator.getDefault().getImageRegistry().get(IconsNames.file_in_16);
		}
		if (obj instanceof FileOutputWeka){
			return Activator.getDefault().getImageRegistry().get(IconsNames.WEKA_16);
		}
		if (obj instanceof AggregateTransformation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.sum_16);
		}
		if (obj instanceof FileInputCSV){
			return Activator.getDefault().getImageRegistry().get(IconsNames.file_go_16);
		}
		if (obj instanceof FileInputShape){
			return Activator.getDefault().getImageRegistry().get(IconsNames.SHAPE_16);
		}
		if (obj instanceof DataBaseOutputStream){
			return Activator.getDefault().getImageRegistry().get(IconsNames.table_row_insert_16);
		}
		if (obj instanceof CielComptaOutput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.table_row_insert_16);
		}
		if (obj instanceof DataBaseInputStream){
			return Activator.getDefault().getImageRegistry().get(IconsNames.table_go_16);
		}
		if (obj instanceof FMDTInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.fmdt_input_16);
		}
		if (obj instanceof FileOutputXML){
			return Activator.getDefault().getImageRegistry().get(IconsNames.XMLFile_16);
		}
		if (obj instanceof Lookup){
			return Activator.getDefault().getImageRegistry().get(IconsNames.lookup_16);
		}
		if (obj instanceof Filter){
			return Activator.getDefault().getImageRegistry().get(IconsNames.filter_16);
		}
		if (obj instanceof FileOutputXLS){
			return Activator.getDefault().getImageRegistry().get(IconsNames.xls_out_16);
		}
		if (obj instanceof FileInputXLS){
			return Activator.getDefault().getImageRegistry().get(IconsNames.xls_16);
		}
		if (obj instanceof Calculation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.calc_16);
		}
		if (obj instanceof TopXTransformation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.topX_16);
		}
		if (obj instanceof UnduplicateRows){
			return Activator.getDefault().getImageRegistry().get(IconsNames.dedoublon_16);
		}
		if (obj instanceof SelectDistinct){
			return  Activator.getDefault().getImageRegistry().get(IconsNames.distinct_16);
		}
		if (obj instanceof MergeStreams){
			return Activator.getDefault().getImageRegistry().get(IconsNames.merge_16);
		}
		if (obj instanceof VanillaAnalyticsGoogle){
			return Activator.getDefault().getImageRegistry().get(IconsNames.googleAnalytics_16);
		}
		if (obj instanceof NagiosDb){
			return Activator.getDefault().getImageRegistry().get(IconsNames.nagios_16);
		}
		if (obj instanceof VanillaCommentExtraction){
			return Activator.getDefault().getImageRegistry().get(IconsNames.COMMENT_EXTRACTION_16);
		}
		if (obj instanceof CassandraInputStream){
			return Activator.getDefault().getImageRegistry().get(IconsNames.cassandra_16);
		}
		if (obj instanceof CassandraOutputStream){
			return Activator.getDefault().getImageRegistry().get(IconsNames.cassandra_16);
		}
		if (obj instanceof HbaseInputStream){
			return Activator.getDefault().getImageRegistry().get(IconsNames.HBASE_16);
		}
		if (obj instanceof HBaseOutputStream){
			return Activator.getDefault().getImageRegistry().get(IconsNames.HBASE_16);
		}
		if (obj instanceof MongoDbInputStream){
			return Activator.getDefault().getImageRegistry().get(IconsNames.MONGODB_16);
		}
		if (obj instanceof MongoDbOutputStream){
			return Activator.getDefault().getImageRegistry().get(IconsNames.MONGODB_16);
		}
		if (obj instanceof NullTransformation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.TRANSFO_NULL_16);
		}
		if (obj instanceof MdmContractFileInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.TRANSFO_MDMFILE_16);
		}
		if (obj instanceof DataPreparationInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.DataPrep_16);
		}
		if (obj instanceof WebServiceInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.WEB_SERVICE_16);
		}
		if (obj instanceof WebServiceVanillaInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.WEB_SERVICE_VANILLA_16);
		}
		if (obj instanceof SqoopTransformation){
			return Activator.getDefault().getImageRegistry().get(IconsNames.SQOOP_16);
		}
		if (obj instanceof OracleXmlView){
			return Activator.getDefault().getImageRegistry().get(IconsNames.table_go_16);
		}
		if (obj instanceof ConnectorAbonneXML){
			return Activator.getDefault().getImageRegistry().get(IconsNames.XML_16);
		}
		if (obj instanceof ConnectorPatrimoineXML){
			return Activator.getDefault().getImageRegistry().get(IconsNames.XML_16);
		}
		if (obj instanceof ConnectorAffaireXML){
			return Activator.getDefault().getImageRegistry().get(IconsNames.TSBN_16);
		}
		if (obj instanceof ConnectorAppelXML){
			return Activator.getDefault().getImageRegistry().get(IconsNames.TSBN_16);
		}
		if (obj instanceof ConnectorReferentielXML){
			return Activator.getDefault().getImageRegistry().get(IconsNames.TSBN_16);
		}
		if (obj instanceof SyriusConnector){
			return Activator.getDefault().getImageRegistry().get(IconsNames.TSBN_16);
		}
		if (obj instanceof RpuConnector){
			return Activator.getDefault().getImageRegistry().get(IconsNames.TSBN_16);
		}
		if (obj instanceof D4CInput){
			return Activator.getDefault().getImageRegistry().get(IconsNames.d4c_16);
		}
		if (obj instanceof FileInputPDFForm){
			return Activator.getDefault().getImageRegistry().get(IconsNames.ic_pdf_form_16);
		}
		if (obj instanceof Geoloc){
			return Activator.getDefault().getImageRegistry().get(IconsNames.geojson_16);
		}
		if (obj instanceof GeoDispatch){
			return Activator.getDefault().getImageRegistry().get(IconsNames.geodispatch_16);
		}
		return null;
	}

}
