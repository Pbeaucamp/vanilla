package bpm.gateway.ui.gef.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.Transformation;
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
import bpm.gateway.core.transformations.mdm.MdmInput;
import bpm.gateway.core.transformations.mdm.MdmOutput;
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
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.gef.commands.CopyCommand;
import bpm.gateway.ui.gef.commands.DeleteCommand;
import bpm.gateway.ui.gef.commands.LinkCommand;
import bpm.gateway.ui.gef.editpolicies.NodeEditPolicy;
import bpm.gateway.ui.gef.figure.FigureNode;
import bpm.gateway.ui.gef.model.Node;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class NodePart extends AbstractGraphicalEditPart implements PropertyChangeListener, NodeEditPart {

	private ConnectionAnchor anchor;

	public NodePart() {
		super();
		addEditPartListener(new EditPartListener() {

			@Override
			public void selectedStateChanged(EditPart editpart) {
				if (getSelected() == EditPart.SELECTED_PRIMARY) {
					try {
						LinkCommand cmd = Activator.getDefault().getActiveLinkCommand();
						if (cmd != null) {
							cmd.setTarget((Node) getModel());
							if (cmd.canExecute()) {
								cmd.execute();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					Activator.getDefault().setActiveLinkCommand(null);
				}
			}

			@Override
			public void removingChild(EditPart child, int index) {
			}

			@Override
			public void partDeactivated(EditPart editpart) {
			}

			@Override
			public void partActivated(EditPart editpart) {
			}

			@Override
			public void childAdded(EditPart child, int index) {
			}
		});
	}

	@Override
	protected IFigure createFigure() {
		IFigure figure = null;

		Image pict = null;

		Transformation o = ((Node) getModel()).getGatewayModel();
		if (o instanceof MultiFolderXLS) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.multi_xls_out_32);
		}
		else if (o instanceof VanillaMapAddressInput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.norparena_adress_input_32);
		}
		else if (o instanceof VanillaMapAddressOutput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.norparena_adress_output_32);
		}
		else if (o instanceof FileFolderReader) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.file_folder_32);
		}
		else if (o instanceof SubTransformationFinalStep) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.simpleoutput_32);
		}
		else if (o instanceof FreedashFormInput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.fd_input_32);
		}
		else if (o instanceof KMLInput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.kml_input_32);
		}
		else if (o instanceof KMLOutput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.kml_output_32);
		}
		else if (o instanceof Normalize) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.dimension_32);
		}
		else if (o instanceof FilterDimension) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.filer_dimension_32);
		}
		else if (o instanceof FileInputVCL) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.file_vcl_32);
		}
		else if (o instanceof FileOutputVCL) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.file_vcl_out_32);
		}
		else if (o instanceof VanillaRoleGroupAssocaition) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.vanilla_role_32);
		}
		else if (o instanceof Denormalisation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.pivot_32);
		}
		else if (o instanceof LdapMultipleMembers) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.ldapMember_32);
		}
		else if (o instanceof VanillaCreateUser) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.vanillaUser_32);
		}
		else if (o instanceof OlapFactExtractorTranformation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.cube_input_32);
		}
		else if (o instanceof OlapOutput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.cube_output_32);
		}
		else if (o instanceof VanillaLdapSynchro) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.ldapSynchro_32);
		}
		else if (o instanceof VanillaGroupUser) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.group_32);
		}
		else if (o instanceof SqlScript) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.scriptSql_32);
		}
		else if (o instanceof TopXTransformation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.topX_32);
		}
		else if (o instanceof FileInputXML) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.XMLFile_32_input);
		}
		else if (o instanceof GlobalDefinitionInput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.gid_32);
		}
		else if (o instanceof AlterationStream) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.transtyp_32);
		}
		else if (o instanceof FileInputXML) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.XMLFile_32_input);
		}
		else if (o instanceof CartesianProduct) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.cartesian_32);
		}
		else if (o instanceof RowsFieldSplitter) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.split_row_32);
		}
		else if (o instanceof SlowChangingDimension2) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.scd2_32);
		}
		else if (o instanceof SlowChangingDimension1) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.scd_32);
		}
		else if (o instanceof InsertOrUpdate) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.update_32);
		}
		else if (o instanceof DeleteRows) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.del_32);
		}
		else if (o instanceof Sequence) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.sequence_32);
		}
		else if (o instanceof SubTransformation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.gateway_32);
		}
		else if (o instanceof SortTransformation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.sort_32);
		}
		else if (o instanceof RangingTransformation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.ranging_32);
		}
		else if (o instanceof GeoFilter) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.geoFilter_32);
		}
		else if (o instanceof AggregateTransformation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.sum_32);
		}
		else if (o instanceof DataBaseOutputStream) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.table_row_insert_32);
		}
		else if (o instanceof CielComptaOutput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.table_row_insert_32);
		}
		else if (o instanceof DbAccessInput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.access_32);
		}
		else if (o instanceof DataBaseInputStream) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.table_go_32);
		}
		else if (o instanceof FileInputCSV) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.file_go_32);
		}
		else if (o instanceof FileOutputCSV) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.file_in_32);
		}
		else if (o instanceof FileOutputWeka) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.WEKA_32);
		}
		else if (o instanceof FileInputShape) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.SHAPE_32);
		}
		else if (o instanceof Lookup) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.lookup_32);
		}
		else if (o instanceof SimpleMappingTransformation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.link_32);
		}
		else if (o instanceof EncryptTransformation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.anonyme_32);
		}
		else if (o instanceof SelectionTransformation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.select_all_32);
		}
		else if (o instanceof PreviousValueTransformation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.prevval_32);
		}
		else if (o instanceof FMDTInput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.fmdt_input_32);
		}
		else if (o instanceof FileOutputXML) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.XMLFile_32);
		}
		else if (o instanceof FreemetricsKPI) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.metric_32);
		}
		else if (o instanceof Filter) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.filter_32);
		}
		else if (o instanceof FileOutputXLS) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.xls_out_32);
		}
		else if (o instanceof FileInputXLS) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.xls_32);
		}
		else if (o instanceof FieldSplitter) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.split_32);
		}
		else if (o instanceof Calculation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.calc_32);
		}
		else if (o instanceof GeoJsonOutput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.geojson_32);
		}
		else if (o instanceof UnduplicateRows) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.dedoublon_32);
		}
		else if (o instanceof SelectDistinct) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.distinct_32);
		}
		else if (o instanceof MergeStreams) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.merge_32);
		}
		else if (o instanceof LdapInput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.ldap_input_32);
		}
		else if (o instanceof MdmInput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.mdm_32_ext);
		}
		else if (o instanceof MdmOutput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.mdm_32_ins);
		}
		else if (o instanceof InfoBrightInjector) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.infobright_32);
		}
		else if (o instanceof OdaInput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.oda_32);
		}
		else if (o instanceof SurrogateKey) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.surrogate_32);
		}
		else if (o instanceof ConsitencyTransformation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.consistency_32);
		}
		else if (o instanceof SqlLookup) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.database_lookup_32);
		}
		else if (o instanceof OlapDimensionExtractor) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.cube_dim_32);
		}
		else if (o instanceof VanillaCreateGroup) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.vanilla_group_32);
		}
		else if (o instanceof ValidationCleansing) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.regex_32);
		}
		else if (o instanceof VanillaAnalyticsGoogle) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.googleAnalytics_32);
		}
		else if (o instanceof NagiosDb) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.nagios_32);
		}
		else if (o instanceof VanillaCommentExtraction) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.COMMENT_EXTRACTION_32);
		}
		else if (o instanceof CassandraInputStream) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.cassandra_32);
		}
		else if (o instanceof HbaseInputStream) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.HBASE_32);
		}
		else if (o instanceof CassandraOutputStream) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.cassandra_32);
		}
		else if (o instanceof HBaseOutputStream) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.HBASE_32);
		}
		else if (o instanceof MongoDbInputStream) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.MONGODB_32);
		}
		else if (o instanceof MongoDbOutputStream) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.MONGODB_32);
		}
		else if (o instanceof NullTransformation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.TRANSFO_NULL_32);
		}
		else if (o instanceof MdmContractFileInput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.TRANSFO_MDMFILE_32);
		}
		else if (o instanceof WebServiceInput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.WEB_SERVICE_32);
		}
		else if (o instanceof WebServiceVanillaInput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.WEB_SERVICE_VANILLA_32);
		}
		else if (o instanceof SqoopTransformation) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.SQOOP_32);
		}
		else if (o instanceof OracleXmlView) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.table_go_32);
		}
		else if (o instanceof ConnectorAbonneXML) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.XML_32);
		}
		else if (o instanceof ConnectorPatrimoineXML) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.XML_32);
		}
		else if (o instanceof ConnectorAffaireXML) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.TSBN_32);
		}
		else if (o instanceof ConnectorAppelXML) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.TSBN_32);
		}
		else if (o instanceof ConnectorReferentielXML) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.TSBN_32);
		}
		else if (o instanceof SyriusConnector) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.TSBN_32);
		}
		else if (o instanceof RpuConnector) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.TSBN_32);
		}
		else if (o instanceof D4CInput) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.d4c_32);
		}
		else if (o instanceof FileInputPDFForm) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.ic_pdf_form_32);
		}
		else if (o instanceof DataPreparationInput){
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.DataPrep_32);
		}
		else if (o instanceof Geoloc) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.geojson_32);
		}
		else if (o instanceof GeoDispatch) {
			pict = Activator.getDefault().getImageRegistry().get(IconsNames.geodispatch_32);
		}

		figure = new FigureNode(pict);

		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new NodeEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new GraphicalNodeEditPolicy() {

			protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
				LinkCommand cmd = (LinkCommand) request.getStartCommand();
				cmd.setTarget((Node) getHost().getModel());
				return cmd;
			}

			protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
				Node source = (Node) getHost().getModel();
				LinkCommand cmd = new LinkCommand(source);
				request.setStartCommand(cmd);
				return cmd;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getCommand
			 * (org.eclipse.gef.Request)
			 */
			@Override
			public Command getCommand(Request request) {
				return super.getCommand(request);
			}

			protected Command getReconnectSourceCommand(ReconnectRequest request) {
				// Link conn = (Link)
				// request.getConnectionEditPart().getModel();
				// Node newSource = (Node) getHost().getModel();
				// ConnectionReconnectCommand cmd = new
				// ConnectionReconnectCommand(conn);
				// cmd.setNewSource(newSource);
				// return cmd;
				return null;
			}

			protected Command getReconnectTargetCommand(ReconnectRequest request) {
				// Link conn = (Link)
				// request.getConnectionEditPart().getModel();
				// Node newTarget = (Node) getHost().getModel();
				// ConnectionReconnectCommand cmd = new
				// ConnectionReconnectCommand(conn);
				// cmd.setNewTarget(newTarget);
				// return cmd;
				return null;
			}
		});
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
			protected Command createDeleteCommand(GroupRequest deleteRequest) {
				DeleteCommand command = new DeleteCommand();
				command.setModel(getHost().getModel());
				command.setParentModel(getHost().getParent().getModel());
				return command;
			}

			@Override
			public Command getCommand(Request request) {
				if (request.getType() == ComponentEditPolicy.REQ_CLONE) {
					CopyCommand command = new CopyCommand();
					command.setModel((Node) getHost().getModel());

					return command;
				}

				return super.getCommand(request);
			}
		});
	}

	@Override
	protected void refreshVisuals() {
		Node model = (Node) getModel();
		FigureNode figure = (FigureNode) getFigure();
		figure.setName(model.getName());
		figure.setLayout(model.getLayout());
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT)) {
			refreshVisuals();
		}

		if (evt.getPropertyName().equals(Node.PROPERTY_RENAME)) {
			refreshVisuals();
		}
		if (evt.getPropertyName().equals(Node.SOURCE_CONNECTIONS_PROP)) {
			refreshSourceConnections();
		}
		if (evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP)) {
			refreshTargetConnections();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		((Node) getModel()).addPropertyChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		super.deactivate();
		((Node) getModel()).removePropertyChangeListener(this);
	}

	protected ConnectionAnchor getConnectionAnchor() {
		if (anchor == null) {
			;
			anchor = new ChopboxAnchor(((FigureNode) getFigure()).getFigure());
		}
		return anchor;
	}

	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	@Override
	protected List getModelSourceConnections() {
		return ((Node) getModel()).getSourceLink();
	}

	@Override
	protected List getModelTargetConnections() {
		return ((Node) getModel()).getTargetLink();
	}

	@Override
	public void performRequest(Request req) {

		if (req.getType().equals(RequestConstants.REQ_OPEN)) {
			Transformation t = ((Node) getModel()).getGatewayModel();

			if (t instanceof SubTransformation) {
				SubTransformation sub = (SubTransformation) t;

				GatewayEditorInput in = null;

				if (Activator.getDefault().getRepositoryContext() == null) {
					File f = new File(sub.getDefinition());
					if (!f.exists()) {
						MessageDialog.openWarning(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.NodePart_0, Messages.NodePart_1 + sub.getDefinition() + Messages.NodePart_2);
						return;
					}
					in = new GatewayEditorInput(f);
				}
				else {
					try {
						try {
							RepositoryItem it = Activator.getDefault().getRepositoryConnection().getRepositoryService().getDirectoryItem(Integer.parseInt(sub.getDefinition()));
							if (it == null) {
								throw new Exception(Messages.NodePart_3);
							}
							String xml = Activator.getDefault().getRepositoryConnection().getRepositoryService().loadModel(it);
							in = new GatewayEditorInput(IOUtils.toInputStream(xml), it.getItemName(), it);

						} catch (Exception e) {
							MessageDialog.openWarning(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.NodePart_4, e.getMessage());
						}
					} catch (Exception e) {
						MessageDialog.openWarning(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.NodePart_5, e.getMessage());
					}

				}
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					page.openEditor(in, GatewayEditorPart.ID, false);
				} catch (PartInitException e) {
					e.printStackTrace();
					MessageDialog.openWarning(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.NodePart_6, e.getMessage());
				}
			}

		}
		else {
			super.performRequest(req);
		}

	}

}
