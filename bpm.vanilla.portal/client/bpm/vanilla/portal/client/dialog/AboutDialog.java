package bpm.vanilla.portal.client.dialog;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AboutDialog extends AbstractDialogBox {

	public static String version = "	Vanilla 8"; 

	private static AboutDialogUiBinder uiBinder = GWT.create(AboutDialogUiBinder.class);

	interface AboutDialogUiBinder extends UiBinder<Widget, AboutDialog> {
	}
	
	interface MyStyle extends CssResource {
		String mainPanel();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	HTMLPanel contentPanel;
	
	@UiField
	HTML txtAbout;

	public AboutDialog() {
		super(version, true, false);
		setWidget(uiBinder.createAndBindUi(this));
		
		try {
			if (LocaleInfo.getCurrentLocale().getLocaleName().equalsIgnoreCase("fr")) {
				txtAbout.setHTML(aboutTextFR);
			}
			else {
				txtAbout.setHTML(aboutText);
			}
		} catch (Exception e) {
			txtAbout.setHTML(aboutText);
		}
	}

	private String aboutText = "<p align=\"justify\">" +
	"<br>" +
	"Vanilla is the leading Open Source Business Intelligence Platform offering state of the art BI features for a fraction of " +
	"the price of commercial alternatives.<br><br>" +
	"It features all the standard functionalities of BI such a Reporting, Analysis, Dashboards, ETL and KPIs. We provide both web " + 
	"and heavy designers as well as web 2.0 renderers as well as our revolutionary Metadata.<br><br>" +
	"Those are not separate entities and documents and are tightly integrated in the Vanilla Platform enabling you too access " + 
	"your BI Documents seamlessly accross platforms and networks while enjoying advanced features such a Document management, " +
	"Schedulers, a powerfull search engine and collaboration tools (notes and versionning).<br><br>" +
	"The development team and the headquarters are located in Lyon - FRANCE. We provide support and technical features for french " + 
	"companies and administrations and foreign ones alike.<br><br>" +
	"<br>" +
	"<a href=\"http://www.bpm-conseil.com\" target=\"_blank\"><font color=\"#3333FF\"><I>Come and visit us here.</I></font></a>" +
	"<br>" +
	"<a href=\"http://www.bpm-conseil.com/forum\" target=\"_blank\"><font color=\"#3333FF\"><I>Our forum</I></font></a>" +
	"<br>" +
	"<a href=\"http://www.bpm-conseil.com/content/downloads/vanilla-5/documentations\" target=\"_blank\"><font color=\"#3333FF\"><I>Documentations</I></font></a>" +
	"<br>" +
	"<a href =\"mailto:info@bpm-conseil.com\"><font color=\"#3333FF\"><I>For comments and consulting</I></font></a>" +
	"</p>" +
	"<br>" +
	"<br>" +
	"<p align=\"justify\">" +
	"The Vanilla Project would like to thanks<br><br>" +
	"Bpm Team, in charge of version 7 : <br><br>" + 
	"	Patrick Beaucamp, Sébastien Vigroux, Cyprien Rauch, Anastasiia Zakharova, Raphaël Cluzeaud<br><br><br>" +
	"</p>";
	
	private String aboutTextFR = "<p align=\"justify\">" +
	"    Vanilla est la seule vraie plateforme décisionnelle Open Source offrant les dernières 'features' de la BI pour une fraction " +
	"des prix proposés par les plateformes 'commerciales'.<br><br>" +
	"Celle-ci dispose de toutes les fonctionalités BI standard telle que le Reporting, l'Analyse (cubes OLAP), des Tableaux de bords, "+
	"des Metriques ainsi qu'un ETL performant. Nous fournissons à la fois des 'designers' lourds et web, des rendus et visualisations "+
	"web 2.0 via la plateforme Vanilla ainsi qu'un Metadata révolutionnaire.<br><br>" +
	"L'un des nombreux avantages de la solution Vanilla est l'intégration complète des ces éléments trop souvent dispersés dans différents "+
	"clients et serveurs. Ainsi tous vos documents BI sont regroupés et sécurisés de façon uniforme dans une plateforme unique qui met à "+
	"votre disposition des fonctionnalités avancées de gestion et de production (GED, Ordonnanceur de tâches, Recherche avancée, Historisation "+
	", et des outils de collaboration (notes et versionnage des documents)).<br><br>" +
	"L'équipe de developement ainsi que le siège social sont situés à Lyon - France. Nous fournissons du support, consulting et développement spécifique " + 
	"en France et à l'etranger. <br><br>" +
	"<br>" +
	"<a href=\"http://www.bpm-conseil.com\" target=\"_blank\"><font color=\"#3333FF\"><I>Venez nous rendre visite.</I></font></a>" +
	"<br>" +
	"<a href=\"http://www.bpm-conseil.com/forum\" target=\"_blank\"><font color=\"#3333FF\"><I>Notre forum</I></font></a>" +
	"<br>" +
	"<a href=\"http://www.bpm-conseil.com/content/downloads/vanilla-5/documentations\" target=\"_blank\"><font color=\"#3333FF\"><I>Documentations</I></font></a>" +
	"<br>" +
	"<a href =\"mailto:info@bpm-conseil.com\"><font color=\"#3333FF\"><I>Contact</I></font></a>" +
	"</p>" +
	"<br>" +
	"<br>" +
	"<p align=\"justify\">" +
	"Le projet Vanilla aimerait remercier<br><br>" +
	"La Team BPM en charge de la version 7 : <br><br>" + 
	"	Patrick Beaucamp, Sébastien Vigroux, Cyprien Rauch, Anastasiia Zakharova, Raphaël Cluzeaud<br><br><br>" +
	"</p>";

	@Override
	public void maximize(boolean maximize) {
		if(maximize) {
			contentPanel.removeStyleName(style.mainPanel());
		}
		else {
			contentPanel.addStyleName(style.mainPanel());
		}
	}
}
