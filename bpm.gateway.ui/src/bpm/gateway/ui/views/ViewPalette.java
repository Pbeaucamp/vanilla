package bpm.gateway.ui.views;

import java.util.HashMap;
import java.util.List;

import org.eclipse.draw2d.Graphics;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.wizards.ImportWizardRegistry;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.wizards.IWizardDescriptor;

import bpm.gateway.core.Comment;
import bpm.gateway.core.transformations.AggregateTransformation;
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
import bpm.gateway.core.transformations.freemetrics.KPIList;
import bpm.gateway.core.transformations.freemetrics.KPIOutput;
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
import bpm.gateway.core.transformations.inputs.OdaInputWithParameters;
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
import bpm.gateway.core.transformations.outputs.GeoJsonOutput;
import bpm.gateway.core.transformations.outputs.HBaseOutputStream;
import bpm.gateway.core.transformations.outputs.InfoBrightInjector;
import bpm.gateway.core.transformations.outputs.InsertOrUpdate;
import bpm.gateway.core.transformations.outputs.MongoDbOutputStream;
import bpm.gateway.core.transformations.outputs.MultiFolderXLS;
import bpm.gateway.core.transformations.outputs.SlowChangingDimension2;
import bpm.gateway.core.transformations.outputs.SubTransformationFinalStep;
import bpm.gateway.core.transformations.utils.PreviousValueTransformation;
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
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.gef.factories.NodeFactory;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.palette.customizer.wizard.loader.PaletteLoaderWizard;
import bpm.vanilla.platform.core.config.Customer;

public class ViewPalette extends ViewPart {
	public static final int TOOL_ENTRY_SELECT = 1;
	public static final int TOOL_ENTRY_LINK = 2;
	
	private ToolEntry selectionToolEntry, linkToolEntry;
	
	public static final String ID = "bpm.gateway.ui.views.ViewPalette"; //$NON-NLS-1$
	private PaletteViewer viewer;
	
	private Button stdOrg, custOrg;
	
	private Customer customer;
	
	public ViewPalette() { }

	@Override
	public void createPartControl(Composite parent) {
		this.customer = Activator.getDefault().getCurrentCustomer();
		
		Composite f = new Composite(parent, SWT.NONE);//toolkit.createForm(parent);
		
		f.setLayoutData(new GridData(GridData.FILL_BOTH));
		f.setLayout(new GridLayout(2, true));
		
		Section section = new Section(f, Section.TITLE_BAR | Section.TWISTIE);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		section.setText(Messages.ViewPalette_1);
//		section.setExpanded(false);
		section.setTitleBarBackground(parent.getDisplay().getDefault().getSystemColor(SWT.COLOR_BLUE));
		
		Composite _c = new Composite(section, SWT.NONE);
		_c.setLayout(new GridLayout(2, true));
		_c.setLayoutData(new GridData(GridData.FILL_BOTH));
		_c.setBackground(f.getBackground());
		section.setClient(_c);

		/*
		 * Buttons to Manage Palette Organization
		 */
		stdOrg = new Button(_c, SWT.TOGGLE);
		stdOrg.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false));
		stdOrg.setToolTipText(Messages.ViewPalette_2);
		stdOrg.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.default_palette));
		stdOrg.setSelection(true);
		stdOrg.setEnabled(false);
		stdOrg.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				custOrg.setSelection(!stdOrg.getSelection());
				
				stdOrg.setEnabled(!stdOrg.getSelection());
				custOrg.setEnabled(!custOrg.getSelection());
				
				viewer.setPaletteRoot(getPaletteRoot());
				viewer.flush();
				
			}
		});
		
		custOrg = new Button(_c, SWT.TOGGLE);
		custOrg.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false));
		custOrg.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.custom_palette));
		custOrg.setToolTipText(Messages.ViewPalette_3);
		custOrg.setSelection(false);
		custOrg.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try{
					viewer.setPaletteRoot(getCustomPaletteRoot());
					stdOrg.setSelection(!custOrg.getSelection());
					
					stdOrg.setEnabled(!stdOrg.getSelection());
					custOrg.setEnabled(!custOrg.getSelection());
					viewer.flush();
				}catch(Exception ex){
					ex.printStackTrace();
					stdOrg.setSelection(true);
					custOrg.setSelection(false);
				}
				
			}
		});
		
		
		viewer = new PaletteViewer();
		
		viewer.createControl(f);
		f.setBackground(viewer.getControl().getBackground());
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,true, 2, 1));
		viewer.setPaletteRoot(getPaletteRoot());

		viewer.getControl().addMouseTrackListener(new MouseTrackListener(){

			public void mouseEnter(MouseEvent e) {
				if (viewer.getActiveTool() instanceof ConnectionCreationToolEntry){
					viewer.setActiveTool(viewer.getPaletteRoot().getDefaultEntry());
				}
			}

			public void mouseExit(MouseEvent e) { }

			public void mouseHover(MouseEvent e) { }
			
		});
		
		getSite().getWorkbenchWindow().getActivePage().addPartListener(new IPartListener(){

			public void partActivated(IWorkbenchPart part) {
				if (part instanceof GatewayEditorPart){
					if (viewer.getEditDomain() != ((GatewayEditorPart)part).getDomain()){
						try{
							
							viewer.setActiveTool(null);
							viewer.setEditDomain(((GatewayEditorPart)part).getDomain());
							((GatewayEditorPart)part).setPaletteViewer(viewer);
							((GatewayEditorPart)part).getDomain().setPaletteRoot(viewer.getPaletteRoot());
							((GatewayEditorPart)part).getDomain().setPaletteViewer(viewer);
							
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
				}
				
			}

			public void partBroughtToTop(IWorkbenchPart part) {
				if (part instanceof GatewayEditorPart){
					if (viewer.getEditDomain() != ((GatewayEditorPart)part).getDomain()){
						try{
							viewer.setEditDomain(((GatewayEditorPart)part).getDomain());
							((GatewayEditorPart)part).setPaletteViewer(viewer);
							((GatewayEditorPart)part).getDomain().setPaletteRoot(viewer.getPaletteRoot());
							((GatewayEditorPart)part).getDomain().setPaletteViewer(viewer);
							
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					
				}
				
			}

			public void partClosed(IWorkbenchPart part) { }

			public void partDeactivated(IWorkbenchPart part) { }

			public void partOpened(IWorkbenchPart part) { }
			
		});

	}

	@Override
	public void setFocus() { }

	
	public void activateToolEntry(int id){
		switch(id){
		case TOOL_ENTRY_LINK :
			viewer.setActiveTool(linkToolEntry);
			break;
		case TOOL_ENTRY_SELECT :
			viewer.setActiveTool(selectionToolEntry);
			break;
		}
	}
	
	protected PaletteRoot getCustomPaletteRoot() throws Exception{
		PaletteRoot root = new PaletteRoot();
		PaletteDrawer manipGroup = new PaletteDrawer(Messages.ViewPalette_4);
		selectionToolEntry = new PanningSelectionToolEntry();
		manipGroup.add(selectionToolEntry);
		
		manipGroup.add(linkToolEntry = new ConnectionCreationToolEntry("Link", //$NON-NLS-1$
				Messages.ViewPalette_6,
				new CreationFactory() {
					public Object getNewObject() { 
						return null;
					}
					// see ShapeEditPart#createEditPolicies() 
					// this is abused to transmit the desired line style 
					public Object getObjectType() { 
						return Graphics.LINE_SOLID; 
						}
				},
				Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.relation_16),
				null));

		 manipGroup.add(new CreationToolEntry(Messages.ViewPalette_7, Messages.ViewPalette_8,
                 new NodeFactory(Comment.class),
                 Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.comment_16),
                 null));
		manipGroup.setInitialState(PaletteDrawer.INITIAL_STATE_PINNED_OPEN);
		root.add(manipGroup);
		
		HashMap<String, List<bpm.gateway.ui.palette.customizer.utils.PaletteEntry>> map = null;
		IWizardDescriptor desc =  ImportWizardRegistry.getInstance().findWizard("bpm.gateway.ui.palette.customizer.wizard.loader.PaletteLoaderWizard"); //$NON-NLS-1$
			
		IWizard wiz = desc.createWizard();
		WizardDialog dial = new WizardDialog(getSite().getShell(), wiz);
		if (dial.open() == WizardDialog.OK){
			map = ((PaletteLoaderWizard)wiz).getPalette();
		}
		else{
			throw new Exception(Messages.ViewPalette_10);
		}
		PaletteDrawer group = null;
		
		for(String key : map.keySet()){
			group = new PaletteDrawer(key);
			root.add(group);
		       
	        for(bpm.gateway.ui.palette.customizer.utils.PaletteEntry entry : map.get(key)){
	        	group.add(new CreationToolEntry(entry.getEntryName(), entry.getEntryDescription(),
                        new NodeFactory(entry.getTransformationClass()),
                        bpm.gateway.ui.palette.customizer.Activator.getDefault().getImageRegistry().getDescriptor(bpm.gateway.ui.palette.customizer.utils.PaletteEntry.keyImages.get(entry.getTransformationClass())),
                        null));
	        }
			
		}
		
		return root;	
	}
	
	protected PaletteRoot getPaletteRoot() {
		PaletteRoot root = new PaletteRoot();
		PaletteDrawer manipGroup = new PaletteDrawer(Messages.ViewPalette_11);
		
		selectionToolEntry = new PanningSelectionToolEntry();
		manipGroup.add(selectionToolEntry);
		
		manipGroup.add(linkToolEntry = new ConnectionCreationToolEntry("Link", //$NON-NLS-1$
				Messages.ViewPalette_13,
				new CreationFactory() {
					public Object getNewObject() { 
						return null;
					}
					// see ShapeEditPart#createEditPolicies() 
					// this is abused to transmit the desired line style 
					public Object getObjectType() { 
						return Graphics.LINE_SOLID; 
						}
				},
				Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.relation_16),
				null));

		 manipGroup.add(new CreationToolEntry("Comment", Messages.ViewPalette_15, //$NON-NLS-1$
                 new NodeFactory(Comment.class),
                 Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.comment_16),
                 null));
		
		
		manipGroup.setInitialState(PaletteDrawer.INITIAL_STATE_PINNED_OPEN);
		root.add(manipGroup);
		
		PaletteSeparator sep2 = new PaletteSeparator();
        root.add(sep2);

        PaletteDrawer inputGroup = new PaletteDrawer(Messages.ViewPalette_16);
        root.add(inputGroup);
       
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_41, Messages.ViewPalette_18,
                        new NodeFactory(DataBaseInputStream.class),
                        Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.table_go_16),
                        null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_43, Messages.ViewPalette_17,
                new NodeFactory(DbAccessInput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.access_16),
                null));
       
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_47, Messages.ViewPalette_20,
                new NodeFactory(FMDTInput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.fmdt_input_16),
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_49, Messages.ViewPalette_22,
                new NodeFactory(FileInputCSV.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.file_go_16), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_51, Messages.ViewPalette_24,
                new NodeFactory(FileInputVCL.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.file_vcl), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_53, Messages.ViewPalette_26,
                new NodeFactory(FileInputXLS.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.xls_16), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_55, Messages.ViewPalette_28,
                new NodeFactory(FileInputXML.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.XMLFile_16_input), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_57, Messages.ViewPalette_59,
                new NodeFactory(FileInputShape.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.SHAPE_16), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_19, Messages.ViewPalette_21, 
                new NodeFactory(FileFolderReader.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.file_folder_16), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_61, Messages.ViewPalette_30,
                new NodeFactory(KMLInput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.kml_input), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_63, Messages.ViewPalette_32,
                new NodeFactory(LdapInput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.ldap_input_16), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_65, Messages.ViewPalette_34,
                new NodeFactory(LdapMultipleMembers.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.ldapMember_16), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_23, Messages.ViewPalette_25,
                new NodeFactory(GlobalDefinitionInput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.gid_16), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_67, Messages.ViewPalette_36,
                new NodeFactory(KPIList.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.metric_16), 
                null));
		
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_69, Messages.ViewPalette_38,
                new NodeFactory(OlapFactExtractorTranformation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.cube_input_16), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_72, Messages.ViewPalette_40,
                new NodeFactory(OlapDimensionExtractor.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.cube_dim_16), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_74, Messages.ViewPalette_42,
                new NodeFactory(OdaInput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.oda_16), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_76, Messages.ViewPalette_44,
                new NodeFactory(OdaInputWithParameters.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.oda_16), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_78, Messages.ViewPalette_27,
                new NodeFactory(VanillaMapAddressInput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.norparena_adress_input_16), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_80, Messages.ViewPalette_14, 
                new NodeFactory(VanillaAnalyticsGoogle.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.googleAnalytics_16),
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_82, Messages.ViewPalette_37, 
                new NodeFactory(NagiosDb.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.nagios_16),
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_84, Messages.ViewPalette_86,
                new NodeFactory(MdmInput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.mdm_16_ext), 
                null));
       
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_88, Messages.ViewPalette_90,
                        new NodeFactory(CassandraInputStream.class),
                        Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.cassandra_16),
                        null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_92, Messages.ViewPalette_94,
                new NodeFactory(HbaseInputStream.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.HBASE_16),
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_96, Messages.ViewPalette_98,
                new NodeFactory(MongoDbInputStream.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.MONGODB_16),
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_100, Messages.ViewPalette_102,
                new NodeFactory(MdmContractFileInput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.TRANSFO_MDMFILE_16),
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_204, Messages.ViewPalette_205,
                new NodeFactory(D4CInput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.d4c_16),
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_206, Messages.ViewPalette_207,
                new NodeFactory(FileInputPDFForm.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.ic_pdf_form_16), 
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_218, Messages.ViewPalette_237,
                new NodeFactory(DataPreparationInput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.DataPrep_16),
                null));
       
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_103, Messages.ViewPalette_104,
                new NodeFactory(WebServiceInput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.WEB_SERVICE_16),
                null));
       
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_105, Messages.ViewPalette_106,
                new NodeFactory(WebServiceVanillaInput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.WEB_SERVICE_VANILLA_16),
                null));
        
        inputGroup.add(new CreationToolEntry(Messages.ViewPalette_208, Messages.ViewPalette_209,
                new NodeFactory(OracleXmlView.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.table_go_16),
                null));

        PaletteDrawer outputGroup = new PaletteDrawer(Messages.ViewPalette_45);
        outputGroup.setId(Messages.ViewPalette_46);
        root.add(outputGroup);
       
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_107, Messages.ViewPalette_48,
                        new NodeFactory(DataBaseOutputStream.class),
                        Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.table_row_insert_16), 
                        null));
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_108, Messages.ViewPalette_50,
                new NodeFactory(InfoBrightInjector.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.infobright_16), 
                null));
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_109, Messages.ViewPalette_52,
                new NodeFactory(FileOutputCSV.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.file_in_16), 
                null));
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_110, Messages.ViewPalette_54,
                new NodeFactory(FileOutputVCL.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.file_vcl_out), 
                null));
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_111, Messages.ViewPalette_56,
                new NodeFactory(FileOutputXML.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.XMLFile_16), 
                null));
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_112, Messages.ViewPalette_58,
                new NodeFactory(FileOutputXLS.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.xls_out_16), 
                null));
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_29, Messages.ViewPalette_31, 
                new NodeFactory(MultiFolderXLS.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.multi_xls_out_16), 
                null));
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_210, Messages.ViewPalette_211,
                new NodeFactory(FileOutputWeka.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.WEKA_16), 
                null));
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_114, Messages.ViewPalette_60,
                new NodeFactory(KMLOutput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.kml_output), 
                null));
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_116, Messages.ViewPalette_62,
                new NodeFactory(OlapOutput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.cube_output_16), 
                null));
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_118, Messages.ViewPalette_64,
                new NodeFactory(KPIOutput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.metric_16),
                null));
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_121, Messages.ViewPalette_66,
                new NodeFactory(InsertOrUpdate.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.update_16),
                null));
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_123,Messages.ViewPalette_68, 
                new NodeFactory(SlowChangingDimension2.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.scd2_16),
                null));
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_125,Messages.ViewPalette_70, 
                new NodeFactory(SubTransformationFinalStep.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.simpleoutput_16),
                null));
        
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_127, Messages.ViewPalette_33,
                new NodeFactory(VanillaMapAddressOutput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.norparena_adress_output_16), 
                null));

        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_129, Messages.ViewPalette_132,
                new NodeFactory(MdmOutput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.mdm_16_ins), 
                null));
       
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_133, Messages.ViewPalette_134,
                        new NodeFactory(CassandraOutputStream.class),
                        Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.cassandra_16),
                        null));
       
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_135, Messages.ViewPalette_136,
                        new NodeFactory(HBaseOutputStream.class),
                        Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.HBASE_16),
                        null));
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_137, Messages.ViewPalette_138,
                new NodeFactory(MongoDbOutputStream.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.MONGODB_16),
                null));
        
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_212, Messages.ViewPalette_213,
                new NodeFactory(CielComptaOutput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.table_row_insert_16), 
                null));
        outputGroup.add(new CreationToolEntry(Messages.ViewPalette_214, Messages.ViewPalette_215,
                new NodeFactory(GeoJsonOutput.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.geojson_16), 
                null));
        
        PaletteDrawer transfoGroup = new PaletteDrawer(Messages.ViewPalette_71);
        root.add(transfoGroup);

        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_139, Messages.ViewPalette_35,
                new NodeFactory(ValidationCleansing.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.regex_16), 
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_140, Messages.ViewPalette_73,
                new NodeFactory(Denormalisation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.pivot_16), 
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_141, Messages.ViewPalette_75,
                new NodeFactory(Normalize.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.dimension), 
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_142, Messages.ViewPalette_77,
                new NodeFactory(FilterDimension.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.filer_dimension), 
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_143, Messages.ViewPalette_79,
                new NodeFactory(SqlScript.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.scriptSql_16), 
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_144, Messages.ViewPalette_81,
                new NodeFactory(SortTransformation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.sort_16), 
                null));
        
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_145, Messages.ViewPalette_83,
                new NodeFactory(SelectDistinct.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.distinct_16), 
                null));
        
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_146, Messages.ViewPalette_85,
                new NodeFactory(MergeStreams.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.merge_16), 
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_147, Messages.ViewPalette_87,
                new NodeFactory(SimpleMappingTransformation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.link_16), 
                null));
        
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_148, Messages.ViewPalette_89,
                new NodeFactory(SelectionTransformation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.select_all_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_216, Messages.ViewPalette_217,
                new NodeFactory(PreviousValueTransformation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.prevval_16),
                null));
        
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_149, Messages.ViewPalette_150,
                new NodeFactory(EncryptTransformation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.anonyme_16),
                null));
		
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_151, Messages.ViewPalette_91,
                new NodeFactory(Lookup.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.lookup_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_152, Messages.ViewPalette_93,
                new NodeFactory(SqlLookup.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.database_lookup_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_153, Messages.ViewPalette_95,
                new NodeFactory(CartesianProduct.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.cartesian_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_154, Messages.ViewPalette_97,
                new NodeFactory(Filter.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.filter_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_155, Messages.ViewPalette_99,
                new NodeFactory(AggregateTransformation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.sum_16),
                null));
        
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_156, Messages.ViewPalette_101,
                new NodeFactory(UnduplicateRows.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.dedoublon_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_157, Messages.ViewPalette_0,
                new NodeFactory(DeleteRows.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.del_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_158, Messages.ViewPalette_5,
                new NodeFactory(FieldSplitter.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.split_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_159, Messages.ViewPalette_9,
                new NodeFactory(RowsFieldSplitter.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.split_row_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_160, "", //$NON-NLS-1$
                new NodeFactory(Calculation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.calc_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_161, Messages.ViewPalette_12,
                new NodeFactory(RangingTransformation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.ranging_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_240, Messages.ViewPalette_241,
                new NodeFactory(GeoFilter.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.geoFilter),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_242, Messages.ViewPalette_243,
                new NodeFactory(GeoDispatch.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.geodispatch_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_162, Messages.ViewPalette_113,
                new NodeFactory(Sequence.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.sequence_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_163, Messages.ViewPalette_115,
                new NodeFactory(SurrogateKey.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.surrogate_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_219, Messages.ViewPalette_220,
                new NodeFactory(ConsitencyTransformation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.consistency_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_164, Messages.ViewPalette_117,
                new NodeFactory(SubTransformation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.gateway_16),
                null));
        
        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_165, Messages.ViewPalette_119,
                new NodeFactory(TopXTransformation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.topX_16),
                null));

        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_166, Messages.ViewPalette_131,
                new NodeFactory(NullTransformation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.TRANSFO_NULL_16),
                null));

        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_221, Messages.ViewPalette_222,
                new NodeFactory(SqoopTransformation.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.SQOOP_16),
                null));

        transfoGroup.add(new CreationToolEntry(Messages.ViewPalette_238, Messages.ViewPalette_239,
                new NodeFactory(Geoloc.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.geojson_16),
                null));
        
        PaletteDrawer vanillaGroup = new PaletteDrawer(Messages.ViewPalette_120);
        root.add(vanillaGroup);
        vanillaGroup.add(new CreationToolEntry(Messages.ViewPalette_167,Messages.ViewPalette_122, 
                new NodeFactory(VanillaGroupUser.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.group_16),
                null));
        
        vanillaGroup.add(new CreationToolEntry(Messages.ViewPalette_168,Messages.ViewPalette_124, 
                new NodeFactory(VanillaCreateUser.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.vanillaUser_16),
                null));
        
        vanillaGroup.add(new CreationToolEntry(Messages.ViewPalette_169,Messages.ViewPalette_126, 
                new NodeFactory(VanillaCreateGroup.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.vanilla_group_16),
                null));
        
        vanillaGroup.add(new CreationToolEntry(Messages.ViewPalette_170,Messages.ViewPalette_128, 
                new NodeFactory(VanillaLdapSynchro.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.ldapSynchro_16),
                null));
        
        vanillaGroup.add(new CreationToolEntry(Messages.ViewPalette_171,Messages.ViewPalette_130, 
                new NodeFactory(VanillaRoleGroupAssocaition.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.vanilla_role_16),
                null));
        
        vanillaGroup.add(new CreationToolEntry(Messages.ViewPalette_172,Messages.ViewPalette_173, 
                new NodeFactory(VanillaCommentExtraction.class),
                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.COMMENT_EXTRACTION_16),
                null));

        if (customer == Customer.TSBN) {
	        vanillaGroup.add(new CreationToolEntry(Messages.ViewPalette_223, Messages.ViewPalette_224,
	                new NodeFactory(ConnectorAffaireXML.class),
	                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.TSBN_16),
	                null));
	
	        vanillaGroup.add(new CreationToolEntry(Messages.ViewPalette_225, Messages.ViewPalette_226,
	                new NodeFactory(ConnectorAppelXML.class),
	                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.TSBN_16),
	                null));
	
	        vanillaGroup.add(new CreationToolEntry(Messages.ViewPalette_227, Messages.ViewPalette_228,
	                new NodeFactory(ConnectorReferentielXML.class),
	                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.TSBN_16),
	                null));
	        
	        vanillaGroup.add(new CreationToolEntry(Messages.ViewPalette_229, Messages.ViewPalette_230,
	                new NodeFactory(SyriusConnector.class),
	                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.TSBN_16),
	                null));
	        
	        vanillaGroup.add(new CreationToolEntry(Messages.ViewPalette_231, Messages.ViewPalette_232,
	                new NodeFactory(RpuConnector.class),
	                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.TSBN_16),
	                null));
        }
        else if (customer == Customer.VE) {
	        vanillaGroup.add(new CreationToolEntry(Messages.ViewPalette_233, Messages.ViewPalette_234,
	                new NodeFactory(ConnectorAbonneXML.class),
	                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.XML_16),
	                null));
	
	        vanillaGroup.add(new CreationToolEntry(Messages.ViewPalette_235, Messages.ViewPalette_236,
	                new NodeFactory(ConnectorPatrimoineXML.class),
	                Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.XML_16),
	                null));
        }
                
		root.setDefaultEntry(selectionToolEntry);
		return root;
	}
}
