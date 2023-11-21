package bpm.birt.gmaps.ui.wizardeditor;

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

import bpm.birt.gmaps.core.reportitem.GooglemapsItem;
import bpm.birt.gmaps.ui.Activator;
import bpm.vanilla.map.core.design.kml.IKmlObject;
import bpm.vanilla.map.core.design.kml.IKmlRegistry;

public class WizardPageDefinition extends WizardPage  {
	
	private GooglemapsItem vanillaGMap;
	
	protected Text txtGMapID;
//	protected Text txtGMapCenterLatitude;
//	protected Text txtGMapCenterLongitude;
//	protected Text txtGMapZoom;
	protected Text txtGMapWidth;
	protected Text txtGMapHeight;
	
	private Browser browser;
	
	private ModifyListener listener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			try{
				if (e.getSource().equals(txtGMapID)) {
					vanillaGMap.setMapID(txtGMapID.getText());
				}
				else if (e.getSource().equals(txtGMapWidth)) {
					vanillaGMap.setMapWidth(Integer.parseInt(txtGMapWidth.getText()));
				}
				else if (e.getSource().equals(txtGMapHeight)) {
					vanillaGMap.setMapHeight(Integer.parseInt(txtGMapHeight.getText()));
				}
//				else if (e.getSource().equals(txtGMapZoom)) {
//					vanillaGMap.setZoom(Integer.parseInt(txtGMapZoom.getText()));
//				}
//				else if (e.getSource().equals(txtGMapCenterLatitude)) {
//					vanillaGMap.setCenterLatitude(txtGMapCenterLatitude.getText());
//				}
//				else if (e.getSource().equals(txtGMapCenterLongitude)) {
//					vanillaGMap.setCenterLongitude(txtGMapCenterLongitude.getText());
//				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	};
	
	protected WizardPageDefinition(String pageName, GooglemapsItem item) {
		super(pageName);
		this.vanillaGMap = item;
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
		
		Label lblMapId = new Label(main, SWT.None);
		lblMapId.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblMapId.setText("GMap ID:"); //$NON-NLS-1$
		
		txtGMapID = new Text(main, SWT.BORDER);
		txtGMapID.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		txtGMapID.addModifyListener(listener);
		
////		Label lblMapZoom = new Label(main, SWT.None);
////		lblMapZoom.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
////		lblMapZoom.setText("GMap Zoom:"); //$NON-NLS-1$
////		
////		txtGMapZoom = new Text(main, SWT.BORDER);
////		txtGMapZoom.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
////		txtGMapZoom.addModifyListener(listener);
////		
////		Label lblMapCenterLatitude = new Label(main, SWT.None);
////		lblMapCenterLatitude.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
////		lblMapCenterLatitude.setText("GMap Center Latitude:"); //$NON-NLS-1$
////		
////		txtGMapCenterLatitude = new Text(main, SWT.BORDER);
////		txtGMapCenterLatitude.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
////		txtGMapCenterLatitude.addModifyListener(listener);
//		
//		Label lblMapCenterLongitude = new Label(main, SWT.None);
//		lblMapCenterLongitude.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
//		lblMapCenterLongitude.setText("GMap Center Longitude:"); //$NON-NLS-1$
//		
//		txtGMapCenterLongitude = new Text(main, SWT.BORDER);
//		txtGMapCenterLongitude.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
//		txtGMapCenterLongitude.addModifyListener(listener);
		
		Label lblMapHeight = new Label(main, SWT.None);
		lblMapHeight.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblMapHeight.setText("GMap Height:"); //$NON-NLS-1$
		
		txtGMapHeight = new Text(main, SWT.BORDER);
		txtGMapHeight.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		txtGMapHeight.addModifyListener(listener);
		
		Label lblMapWidth = new Label(main, SWT.None);
		lblMapWidth.setLayoutData(new GridData(SWT.LEFT, SWT.BEGINNING, false, false));
		lblMapWidth.setText("GMap Width:"); //$NON-NLS-1$
		
		txtGMapWidth = new Text(main, SWT.BORDER);
		txtGMapWidth.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
		txtGMapWidth.addModifyListener(listener);
		
		Button testMap = new Button(main, SWT.PUSH);
		testMap.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 4, 1));
		testMap.setText("Test the google map");
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
	
//	private int checkZoom(){
//		try{
//			return Integer.parseInt(txtGMapZoom.getText());
//		}catch (Exception e) {
//			e.printStackTrace();
//			txtGMapZoom.setText("Please, set a value between 0 and 99");
//			return 0;
//		}
//	}
	
//	private double checkLatitude(){
//		try{
//			return Double.parseDouble(txtGMapCenterLatitude.getText());
//		}catch (Exception e) {
//			e.printStackTrace();
//			txtGMapCenterLatitude.setText("Please, set a double value");
//			return 0.0;
//		}
//	}
//	
//	private double checkLongitude(){
//		try{
//			return Double.parseDouble(txtGMapCenterLongitude.getText());
//		}catch (Exception e) {
//			e.printStackTrace();
//			txtGMapCenterLongitude.setText("Please, set a double value");
//			return 0.0;
//		}
//	}
	
	private void initValues() {
//		txtGMapCenterLatitude.setText(vanillaGMap.getCenterLatitude());
//		txtGMapCenterLongitude.setText(vanillaGMap.getCenterLongitude());
//		txtGMapZoom.setText(String.valueOf(vanillaGMap.getZoom()));
		txtGMapID.setText(vanillaGMap.getMapID());
		txtGMapHeight.setText(String.valueOf(vanillaGMap.getMapHeight()));
		txtGMapWidth.setText(String.valueOf(vanillaGMap.getMapWidth()));
	}
	
	private String buildHmtl(){
//		double latitude = checkLatitude();
//		double longitude = checkLongitude();
//		int zoom = checkZoom();
		
		StringBuffer html = new StringBuffer();
		html.append("<html>\n");
		html.append("  <head>\n");
		html.append("     <script src=\"http://maps.google.com/maps/api/js?sensor=false\" type=\"text/javascript\"></script>\n");
		html.append("     <script type=\"text/javascript\">\n");
		html.append("   	var map = null;\n");
		html.append("   	function load(mapname)\n");
		html.append("   	{  \n");
		html.append("      		if (!map)\n");
		html.append("      		{\n");
		html.append("        		var latlng = new google.maps.LatLng(" + 0 + "," + 0 +");\n");
		html.append("   	 		var myOptions = {\n");
		html.append("     				zoom: " + 8 + ",\n");
		html.append("     				center: latlng,\n");
  		html.append("     				mapTypeId: google.maps.MapTypeId.ROADMAP\n");
  		html.append("   		}\n");
  		html.append("         	map = new google.maps.Map(document.getElementById(mapname), myOptions);\n");
  		html.append("        	if (!map)\n");
  		html.append("         	{\n");
  		html.append("            	alert (\"Could not create Google map\");\n");
  		html.append("            	return;\n");
  		html.append("         	}\n");
  		html.append("     	}\n");
  		html.append("  	  }\n");
  		html.append("  	  window.onload = function launchTask(){\n");
  		html.append("		load('map1')\n");
  		html.append("     };\n");
  		html.append("     </script>\n");
      	html.append("   </head>\n");
  		html.append("   <body>\n");
  		html.append("       <div id=\"map1\" style=\"width: 100%; height: 100%\"><BR><BR><BR><BR><BR><BR> <B>(Click on a Load Map Button below to see the map)</B></div>\n");
		html.append("   </body>\n");
		html.append("</html>\n");
		
		return html.toString();
	}
}
