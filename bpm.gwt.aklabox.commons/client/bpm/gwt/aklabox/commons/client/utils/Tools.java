package bpm.gwt.aklabox.commons.client.utils;

import bpm.document.management.core.model.Chorus;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;

public class Tools {

	/*
	 * This method is use to define label for chorus mainly.
	 * It can be use for other labels
	 */
	public static String getChorusLabel(String label) {
		if (label == null) {
			return "";
		}
		
		switch (label) {
		case Chorus.P_CPP_ID_FACTURE_CPP:
			return LabelsConstants.lblCnst.idFactureCpp();
		case Chorus.P_CPP_STATUT_CPP:
			return LabelsConstants.lblCnst.statutCpp();
		case Chorus.P_CPP_STATUT_CIBLE:
			return LabelsConstants.lblCnst.statutCibleListe();
		case Chorus.P_CPP_FOURNISSEUR:
			return LabelsConstants.lblCnst.fournisseur();
		case Chorus.P_CPP_DESTINATAIRE:
			return LabelsConstants.lblCnst.destinataire();
		case Chorus.P_CPP_SIRET:
			return LabelsConstants.lblCnst.siret();
		case Chorus.P_CPP_SERVICE_DESTINATAIRE:
			return LabelsConstants.lblCnst.serviceDestinataire();
		case Chorus.P_CPP_SERVICE_DESTINATAIRE_CODE:
			return LabelsConstants.lblCnst.serviceDestinataireCode();
		case Chorus.P_CPP_TYPE_FACTURE:
			return LabelsConstants.lblCnst.typeFacture();
		case Chorus.P_CPP_NO_FACTURE:
			return LabelsConstants.lblCnst.noFacture();
		case Chorus.P_CPP_DATE_FACTURE:
			return LabelsConstants.lblCnst.dateFacture();
		case Chorus.P_CPP_DATE_DEPOT:
			return LabelsConstants.lblCnst.dateDepot();
		case Chorus.P_CPP_MONTANT_TTC:
			return LabelsConstants.lblCnst.montantTtc();
		case Chorus.P_CPP_TYPE_IDENTIFIANT:
			return LabelsConstants.lblCnst.typeIdentifiant();
		case Chorus.P_CPP_FOURNISSEUR_RAISON_SOCIALE:
			return LabelsConstants.lblCnst.fournisseurRaisonSociale();
		case Chorus.P_CPP_DATE_MISE_A_DISPO:
			return LabelsConstants.lblCnst.dateMiseADispo();
		case Chorus.P_CPP_DATE_FIN_SUSPENSION:
			return LabelsConstants.lblCnst.dateFinSuspension();
		case Chorus.P_CPP_DATE_PASSAGE_STATUT:
			return LabelsConstants.lblCnst.datePassageStatut();
		case Chorus.P_CPP_IS_CPP:
			return LabelsConstants.lblCnst.isCpp();
		case Chorus.P_CPP_TYPE_INTEGRATION:
			return LabelsConstants.lblCnst.typeIntegration();
		case Chorus.P_CPP_FACTURE_NUMERO_ENGAGEMENT:
			return LabelsConstants.lblCnst.factureNumeroEngagement();
		case Chorus.P_CPP_FACTURE_CADRE:
			return LabelsConstants.lblCnst.factureCadre();
		case Chorus.P_CPP_ENVOI_VISA:
			return LabelsConstants.lblCnst.envoiVisa();
		case Chorus.P_CPP_IPARAPHEUR_TYPE:
			return LabelsConstants.lblCnst.iparapheurType();
		case Chorus.P_CPP_IPARAPHEUR_SOUS_TYPE:
			return LabelsConstants.lblCnst.iparapheurSousType();
		case Chorus.P_CPP_ENVOI_GED:
			return LabelsConstants.lblCnst.envoiGed();
		case Chorus.P_CPP_ENVOI_SAE:
			return LabelsConstants.lblCnst.envoiSae();
		case Chorus.P_CPP_CHECK_MISE_A_DISPO_GF:
			return LabelsConstants.lblCnst.checkMiseADispoGf();
		case Chorus.P_CPP_ENVOI_AUTO:
			return LabelsConstants.lblCnst.envoiAuto();
		default:
			break;
		}
		
		return label;
	}
}
