package bpm.birt.fusionmaps.ui.wizardeditor;

import java.util.Arrays;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.birt.fusionmaps.core.reportitem.FusionmapsItem;
import bpm.birt.fusionmaps.ui.Activator;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapRegistry;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class WizardPageDefinition extends WizardPage  {
	public static final String P_BPM_VANILLA_URL = "bpmVanillaUrl"; //$NON-NLS-1$
	
	private FusionmapsItem vanillaMap;
	
	protected Text txtVanillaRuntimeUrl;
	protected Text txtMapID;
	protected Text txtMapWidth;
	protected Text txtMapHeight;
//	protected Text txtDrillDownLink;
//	protected Button btnIsDrillDownable;
	
	protected ComboViewer cbMaps;
//	private List<String> maps;
	
	private Browser browser;
	
	private ModifyListener listener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			try{
				if (e.getSource().equals(txtMapID)) {
					vanillaMap.setMapID(txtMapID.getText());
				}
				else if (e.getSource().equals(txtMapWidth)) {
					vanillaMap.setMapHeight(Integer.parseInt(txtMapWidth.getText()));
				}
				else if (e.getSource().equals(txtMapHeight)) {
					vanillaMap.setMapWidth(Integer.parseInt(txtMapHeight.getText()));
				}
//				else if(e.getSource().equals(txtDrillDownLink)){
//					vanillaMap.setDrillDownLink(txtDrillDownLink.getText());
//				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	};
	
	protected WizardPageDefinition(String pageName, FusionmapsItem item) {
		super(pageName);
		this.vanillaMap = item;
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(4, false));
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true , true));
		
		createContent(main);
		
		initValues( );
		
		setControl(main);
	}
	
	private void createContent(Composite main) {
		browser = new Browser(main, SWT.BORDER);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		browser.setJavascriptEnabled(true);
		
		Label lblVanillaUrl = new Label(main, SWT.None);
		lblVanillaUrl.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblVanillaUrl.setText("Vanilla Url:"); //$NON-NLS-1$
		
		txtVanillaRuntimeUrl = new Text(main, SWT.BORDER);
		txtVanillaRuntimeUrl.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		txtVanillaRuntimeUrl.addModifyListener(listener);
		
		Button loadMap = new Button(main, SWT.PUSH);
		loadMap.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		loadMap.setText("Load Map");
		loadMap.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
//				String vanillaUrl = txtVanillaRuntimeUrl.getText();
//				if(!vanillaUrl.equals("")){
//					String servletUrl = vanillaUrl + GET_MAPS_SERVLET_URL;
//					try {
//						String listMapsXml = ConnectionHelper.sendMessage("", servletUrl);
//						setComboMap(listMapsXml);
//					} catch (IOException e1) {
//						
//						e1.printStackTrace();
//					} catch (Exception e1) {
//						
//						e1.printStackTrace();
//					}
//				}
				
				IFusionMapRegistry fmRge = Activator.getDefault().getFusionMapRegistry();
				fmRge.configure(txtVanillaRuntimeUrl.getText());
				
				try{
					cbMaps.setInput(fmRge.getFusionMapObjects());
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), "Error", "Unable to load availables FusionMaps - " + ex.getMessage());
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}	
		});
		
		Label lblMaps = new Label(main, SWT.None);
		lblMaps.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblMaps.setText("Maps Available:"); //$NON-NLS-1$
		
		cbMaps = new ComboViewer(main, SWT.PUSH);
		cbMaps.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		cbMaps.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)cbMaps.getSelection();
				
				try{
					if (ss.isEmpty()){
						vanillaMap.setSwfURL("");
					}
					else{
						if(ss.getFirstElement() instanceof IFusionMapObject){
							vanillaMap.setSwfURL(((IFusionMapObject)ss.getFirstElement()).getSwfFileName());
						}
						else {
							vanillaMap.setSwfURL((String)ss.getFirstElement() + ".swf");
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), "Error", ex.getMessage());
				}
				
				
				
			}
		});
		cbMaps.setContentProvider(new ArrayContentProvider());
		cbMaps.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				try{
					if(element instanceof IFusionMapObject){
						return ((IFusionMapObject)element).getName();
					}
					else {
						return (String)element;
					}
				}catch(Exception ex){
					ex.printStackTrace();
					return "";
				}
				
			}
		});
		Label lblMapId = new Label(main, SWT.None);
		lblMapId.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblMapId.setText("Map ID:"); //$NON-NLS-1$
		
		txtMapID = new Text(main, SWT.BORDER);
		txtMapID.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		txtMapID.addModifyListener(listener);
		
		Label lblMapHeight = new Label(main, SWT.None);
		lblMapHeight.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblMapHeight.setText("Map Height:"); //$NON-NLS-1$
		
		txtMapHeight = new Text(main, SWT.BORDER);
		txtMapHeight.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		txtMapHeight.addModifyListener(listener);
		
		Label lblMapWidth = new Label(main, SWT.None);
		lblMapWidth.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblMapWidth.setText("Map Width:"); //$NON-NLS-1$
		
		txtMapWidth = new Text(main, SWT.BORDER);
		txtMapWidth.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		txtMapWidth.addModifyListener(listener);
		
//		Label lblIsDrillDownable = new Label(main, SWT.None);
//		lblIsDrillDownable.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
//		lblIsDrillDownable.setText("Allow Drilldown: "); //$NON-NLS-1$
//		
//		btnIsDrillDownable = new Button(main, SWT.CHECK);
//		btnIsDrillDownable.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
//		btnIsDrillDownable.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				try {
//					if(btnIsDrillDownable.getSelection()){
//						vanillaMap.setIsDrillDownable(true);
//						txtDrillDownLink.setEnabled(true);
//					}
//					else{
//						vanillaMap.setIsDrillDownable(false);
//						txtDrillDownLink.setEnabled(false);
//					}
//				} catch (SemanticException e1) {
//					
//					e1.printStackTrace();
//				}
//			}
//		});
//		
//		Label lblDrillDownLink = new Label(main, SWT.None);
//		lblDrillDownLink.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
//		lblDrillDownLink.setText("Drilldown Link:"); //$NON-NLS-1$
//		
//		txtDrillDownLink = new Text(main, SWT.BORDER);
//		txtDrillDownLink.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
//		txtDrillDownLink.addModifyListener(listener);
		
		Button testMap = new Button(main, SWT.PUSH);
		testMap.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 4, 1));
		testMap.setText("Test the flash map");
		testMap.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String html = buildHmtl();
				browser.setText(html);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}	
		});
	}
	
	private void initValues() {
		String vanillaUrlPreference = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL) != null ? ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL) : "http://localhost:7171/VanillaRuntime";
		
		txtVanillaRuntimeUrl.setText(vanillaUrlPreference);
		txtMapID.setText(vanillaMap.getMapID());
		txtMapHeight.setText(String.valueOf(vanillaMap.getMapHeight()));
		txtMapWidth.setText(String.valueOf(vanillaMap.getMapWidth()));
		
		String mapName = vanillaMap.getSwfURL();
		if(mapName != null && !mapName.equals("")){
			try {
				mapName = mapName.replace(".swf", "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			cbMaps.setInput(Arrays.asList(new String[]{mapName}));
			cbMaps.getCombo().select(0);
		}
//		txtDrillDownLink.setText(String.valueOf(vanillaMap.getDrillDownLink()));
//		if(!vanillaMap.isDrillDownable()){
//			btnIsDrillDownable.setSelection(false);
//			txtDrillDownLink.setEnabled(false);
//		}
//		else{
//			btnIsDrillDownable.setSelection(true);
//		}
//		setComboMap(vanillaMap.getListMapXML(), false);
//		cbMaps.setText(vanillaMap.getSwfURL());
	}
	
//	private String getMapName(String xml) {
//		int indexBegin = xml.indexOf("<mapname>");
//		if(indexBegin != -1){
//			int indexEnd = xml.indexOf("</mapname>");
//			return xml.substring(indexBegin + "<mapname>".length(), indexEnd);
//		}
//		
//		return null;
//	}
	
	private String buildHmtl(){
		String bis = "";
		bis += "<html>";
		bis += "    <head>";
		bis += "        <title>Test Map</title>";
		bis += "        <script language=\"JavaScript\" src=\"" + txtVanillaRuntimeUrl.getText() + "/fusionMap/Maps/FusionMaps.js" +  "\"></script>";
		bis += "    </head>";
		bis += "    <body>";
		bis += "        <table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\">";
		bis += "            <tr>";
		bis += "                <td valign=\"top\" class=\"text\" align=\"center\">";
		bis += "                    <div id=\"mapdiv\" align=\"center\">";
		bis += "                        FusionMaps.";
		bis += "                    </div>";
		bis += "                    <script type=\"text/javascript\">";
		bis += "                        var map = new FusionMaps(\"" + txtVanillaRuntimeUrl.getText() + "/fusionMap/Maps/" + vanillaMap.getSwfURL() + "\", \"" + txtMapID.getText() + "\", \"" + txtMapWidth.getText() + "\", \"" + txtMapHeight.getText() + "\", \"0\", \"0\");";
		bis += "                        map.setDataXML(\"<map></map>\");";
		bis += "                        map.render(\"mapdiv\");";
		bis += "                    </script>";
		bis += "                </td>";
		bis += "             </tr>";
		bis += "         </table>";
		bis += "     </body>";
		bis += "</html>";
		
		return bis;
	}
}
