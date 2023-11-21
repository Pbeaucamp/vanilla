package bpm.vanilla.map.design.ui.dialogs;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;
import bpm.vanilla.map.core.design.kml.KmlParser;


public class DialogBrowseKml extends Dialog {

	private IMapDefinition mapDefinition;
	
	private Browser browser;
	private boolean wantToSeeKml;
	
	public DialogBrowseKml(Shell parentShell, IMapDefinition mapDefinition, boolean wantToSeeKml) {
		super(parentShell);
		this.mapDefinition = mapDefinition;
		this.wantToSeeKml =wantToSeeKml;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(500, 400);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		browser = new Browser(main, SWT.BORDER);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		String html = ""; //$NON-NLS-1$
		if(wantToSeeKml){
			html = buildMapKml();
		}
		else{
			html = buildFusionMap();
		}
		refreshBrowser(html);
		
		return main;
	}
	
	private void refreshBrowser(String html) {
		browser.setText(html);
	}
	
	private String buildMapKml() {
		String kmlFilePath = Activator.getDefault().getKmlPathUrl() + mapDefinition.getKmlObject().getKmlFileName();
		KmlParser kmlParser = new KmlParser(kmlFilePath, new ArrayList<ColorRange>());
		String xmlData = kmlParser.close();
		double latitude = kmlParser.getLatitudeCenter();
		double longitude = kmlParser.getLongitudeCenter();
		
		StringBuffer html = new StringBuffer();
		html.append("<html>\n"); //$NON-NLS-1$
		html.append("  <head>\n"); //$NON-NLS-1$
		html.append("     <script src=\"http://maps.google.com/maps/api/js?sensor=false\" type=\"text/javascript\"></script>\n"); //$NON-NLS-1$
		html.append("     <script type=\"text/javascript\">\n"); //$NON-NLS-1$
		html.append("   	var map = null;\n"); //$NON-NLS-1$
		html.append("   	function load(mapname)\n"); //$NON-NLS-1$
		html.append("   	{  \n"); //$NON-NLS-1$
		html.append("      		if (!map)\n"); //$NON-NLS-1$
		html.append("      		{\n"); //$NON-NLS-1$
		html.append("        		var latlng = new google.maps.LatLng(" + latitude + "," + longitude +");\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		html.append("   	 		var myOptions = {\n"); //$NON-NLS-1$
		html.append("     				zoom: 8,\n"); //$NON-NLS-1$
		html.append("     				center: latlng,\n"); //$NON-NLS-1$
  		html.append("     				mapTypeId: google.maps.MapTypeId.ROADMAP\n"); //$NON-NLS-1$
  		html.append("   		}\n"); //$NON-NLS-1$
  		html.append("         	map = new google.maps.Map(document.getElementById(mapname), myOptions);\n"); //$NON-NLS-1$
  		html.append("        	if (!map)\n"); //$NON-NLS-1$
  		html.append("         	{\n"); //$NON-NLS-1$
  		html.append("            	alert (\"Could not create Google map\");\n"); //$NON-NLS-1$
  		html.append("            	return;\n"); //$NON-NLS-1$
  		html.append("         	}\n"); //$NON-NLS-1$
		html.append(xmlData);
  		html.append("     	}\n"); //$NON-NLS-1$
  		html.append("  	  }\n"); //$NON-NLS-1$
  		html.append("  	  window.onload = function launchTask(){\n"); //$NON-NLS-1$
  		html.append("		load('map1')\n"); //$NON-NLS-1$
  		html.append("     };\n"); //$NON-NLS-1$
  		html.append("     </script>\n"); //$NON-NLS-1$
      	html.append("   </head>\n"); //$NON-NLS-1$
  		html.append("   <body>\n"); //$NON-NLS-1$
  		html.append("       <div id=\"map1\" style=\"width: 100%; height: 100%\"><BR><BR><BR><BR><BR><BR> <B>(Click on a Load Map Button below to see the map)</B></div>\n"); //$NON-NLS-1$
		html.append("   </body>\n"); //$NON-NLS-1$
		html.append("</html>\n"); //$NON-NLS-1$
		
		return html.toString();
	}
	
	private String buildFusionMap() {
		String fusionMapPathJS = Activator.getDefault().getFusionMapPathUrl() + "FusionMaps.js"; //$NON-NLS-1$
		String fusionMapPath = Activator.getDefault().getFusionMapPathUrl() + mapDefinition.getFusionMapObject().getSwfFileName();
		
		StringBuffer html = new StringBuffer();
		html.append("<html>"); //$NON-NLS-1$
		html.append("    <head>"); //$NON-NLS-1$
		html.append("        <title>Test Map</title>"); //$NON-NLS-1$
		html.append("        <script language=\"JavaScript\" src=\"" + fusionMapPathJS +  "\"></script>"); //$NON-NLS-1$ //$NON-NLS-2$
		html.append("    </head>"); //$NON-NLS-1$
		html.append("    <body>"); //$NON-NLS-1$
		html.append("        <table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\">"); //$NON-NLS-1$
		html.append("            <tr>"); //$NON-NLS-1$
		html.append("                <td valign=\"top\" class=\"text\" align=\"center\">"); //$NON-NLS-1$
		html.append("                    <div id=\"mapdiv\" align=\"center\">"); //$NON-NLS-1$
		html.append("                        FusionMaps."); //$NON-NLS-1$
		html.append("                    </div>"); //$NON-NLS-1$
		html.append("                    <script type=\"text/javascript\">"); //$NON-NLS-1$
		html.append("                        var map = new FusionMaps(\"" + fusionMapPath + "\", \"MapID1\", \"500\", \"400\", \"0\", \"0\");"); //$NON-NLS-1$ //$NON-NLS-2$
		html.append("                        map.setDataXML(\"<map></map>\");"); //$NON-NLS-1$
		html.append("                        map.render(\"mapdiv\");"); //$NON-NLS-1$
		html.append("                    </script>"); //$NON-NLS-1$
		html.append("                </td>"); //$NON-NLS-1$
		html.append("             </tr>"); //$NON-NLS-1$
		html.append("         </table>"); //$NON-NLS-1$
		html.append("     </body>"); //$NON-NLS-1$
		html.append("</html>"); //$NON-NLS-1$
		
		return html.toString();
	}

}
