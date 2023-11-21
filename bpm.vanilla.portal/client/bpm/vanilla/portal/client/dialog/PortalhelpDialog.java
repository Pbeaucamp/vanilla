//package bpm.vanilla.portal.client.dialog;
//
//import bpm.vanilla.portal.client.utils.PortalConstants;
//
//import com.google.gwt.dom.client.Style.TextDecoration;
//import com.google.gwt.user.client.ui.DialogBox;
//import com.google.gwt.user.client.ui.HTMLPanel;
//import com.google.gwt.user.client.ui.Tree;
//import com.google.gwt.user.client.ui.TreeItem;
//
//public class PortalhelpDialog extends DialogBox{
//
//	public PortalhelpDialog() {
//		
//		
//		HTMLPanel main = new HTMLPanel("");
//		main.addStyleName("vp");
//		main.setSize(600 + "px", 600 + "px");
//		
//		Tree index = new Tree();
//		index.getElement().getStyle().setColor("BLUE");
//		index.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
//		TreeItem intro = new TreeItem("Introduction");
//			TreeItem apropos = new TreeItem("A propos");
//			intro.addItem(apropos);
//		index.addItem(intro);
//		
//		TreeItem description = new TreeItem("Description des interfaces");
//			TreeItem accueil = new TreeItem("Page d'accueil");
//			description.addItem(accueil);
//			TreeItem portail = new TreeItem("Portail");
//				TreeItem bandeauSup = new TreeItem("Bandeau sup&eacurieur");
//				portail.addItem(bandeauSup);
//				TreeItem panneauExploG = new TreeItem("Panneau d'exploration");
//				portail.addItem(panneauExploG);
//				TreeItem paneauCen = new TreeItem("Panneau central");
//				portail.addItem(paneauCen);
//			description.addItem(portail);
//		index.addItem(description);
//		
//		TreeItem consultationObjectDec = new TreeItem("Consultations des objets");
//			TreeItem cubes = new TreeItem("Cubes");
//			consultationObjectDec.addItem(cubes);
//			TreeItem dashboard = new TreeItem("Tableaux de bords");
//			consultationObjectDec.addItem(dashboard);
//			TreeItem rapport = new TreeItem("Rapports");
//			consultationObjectDec.addItem(rapport);
//			TreeItem ged = new TreeItem("GED : Gestion Electronique de documents");
//			consultationObjectDec.addItem(ged);
//		index.addItem(consultationObjectDec);
//		
//		main.add(index);
//		this.setWidget(main);
//	}
//
//	
//}
