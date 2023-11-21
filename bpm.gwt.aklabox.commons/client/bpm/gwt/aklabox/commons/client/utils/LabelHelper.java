package bpm.gwt.aklabox.commons.client.utils;

import bpm.document.management.core.model.OrganigramElementSecurity.Fonction;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;

public class LabelHelper {
	
	public static String functionToLabel(Fonction fonction) {
		if(fonction == null) fonction = Fonction.AUTRE;
		switch(fonction){
		case DIRECTEUR:
			return LabelsConstants.lblCnst.Director();
		case DIRECTEURSERVICE:
			return LabelsConstants.lblCnst.ServiceDirector();
		case CHEFSERVICE:
			return LabelsConstants.lblCnst.ServiceManager();
		case COLLABORATEUR:
			return LabelsConstants.lblCnst.Collaborater();
		case COORDINATEURVOYAGE:
			return LabelsConstants.lblCnst.TravelCoordinator();
		case PRESCRIPTEURMARCHE:
			return LabelsConstants.lblCnst.MarketPrescriber();
		case ASSISTANTDIRECTION:
			return LabelsConstants.lblCnst.ExecutiveAssistant();
		case AUTRE:
			return LabelsConstants.lblCnst.Other();
		default:
			return "";
		}
	}
	
	public static Fonction getFunctionFromList(String label){
		if(label.equals(LabelsConstants.lblCnst.Director())){
			return Fonction.DIRECTEUR;
		} else if(label.equals(LabelsConstants.lblCnst.ServiceDirector())){
			return Fonction.DIRECTEURSERVICE;
		} else if(label.equals(LabelsConstants.lblCnst.ServiceManager())){
			return Fonction.CHEFSERVICE;
		} else if(label.equals(LabelsConstants.lblCnst.Collaborater())){
			return Fonction.COLLABORATEUR;
		} else if(label.equals(LabelsConstants.lblCnst.TravelCoordinator())){
			return Fonction.COORDINATEURVOYAGE;
		} else if(label.equals(LabelsConstants.lblCnst.MarketPrescriber())){
			return Fonction.PRESCRIPTEURMARCHE;
		} else if(label.equals(LabelsConstants.lblCnst.ExecutiveAssistant())){
			return Fonction.ASSISTANTDIRECTION;
		} else if(label.equals(LabelsConstants.lblCnst.Other())){
			return Fonction.AUTRE;
		} else {
			return null;
		}
		
	}
}
