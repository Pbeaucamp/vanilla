package bpm.gateway.core.veolia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.gateway.core.veolia.abonnes.AbonnesDAO;
import bpm.gateway.core.veolia.abonnes.AbonnesDAO.ClassAbonnee;

public class ChangeXmlOrder {
	
	private static final String PATH_FILE_ABONNE = "D:/DATA/Clients/VendeeEau/XML_XSD/XSD/XML/10_ABONNE_171116.xml";
	private static final String PATH_OUTPUT = "D:/DATA/Clients/VendeeEau/XML_XSD/XSD/XML/Test/10_ABONNE_171116_order.xml";
	
	private static final String O_ABT = "<contratAbts>";
	private static final String O_AEP = "<contratAeps>";
	private static final String O_AC = "<contratAcs>";
	private static final String O_AB = "<abonnes>";
	private static final String O_FAC = "<factures>";
	private static final String O_LIG_FAC = "<ligneFactures>";
	private static final String O_BRAN = "<branchements>";
	private static final String O_PTS_FOUR = "<pointsFourniture>";
	private static final String O_COMPT = "<compteurs>";
	private static final String O_RELEVE = "<releves>";
	private static final String O_INTER = "<interventions>";
	
	private static final String C_ABT = "</contratAbts>";
	private static final String C_AEP = "</contratAeps>";
	private static final String C_AC = "</contratAcs>";
	private static final String C_AB = "</abonnes>";
	private static final String C_FAC = "</factures>";
	private static final String C_LIG_FAC = "</ligneFactures>";
	private static final String C_BRAN = "</branchements>";
	private static final String C_PTS_FOUR = "</pointsFourniture>";
	private static final String C_COMPT = "</compteurs>";
	private static final String C_RELEVE = "</releves>";
	private static final String C_INTER = "</interventions>";
	
	private static final String[] OPENS = {O_ABT, O_AEP, O_AC, O_AB, O_FAC, O_LIG_FAC, O_BRAN, O_PTS_FOUR, O_COMPT, O_RELEVE, O_INTER};
	private static final String[] CLOSE = {C_ABT, C_AEP, C_AC, C_AB, C_FAC, C_LIG_FAC, C_BRAN, C_PTS_FOUR, C_COMPT, C_RELEVE, C_INTER};

	public static void main(String[] args) {
		HashMap<ClassAbonnee, List<String>> map = new HashMap<>();
		
		ClassAbonnee currentClass = null;
		
		initAndCloseFile(true);
		try (BufferedReader br = new BufferedReader(new FileReader(PATH_FILE_ABONNE))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	if (currentClass == null) {
			    	ClassAbonnee ab = containsOpen(line);
			    	if (ab != null) {
			    		currentClass = ab;
			    	}
			    	List<String> lines = new ArrayList<String>();
			    	lines.add(line);
			    	map.put(ab, lines);
		    	}
		    	else if (containsClose(line)) {
		    		List<String> lines = map.get(currentClass);
			    	lines.add(line);
			    	
		    		currentClass = null;
		    	}
		    	else {
		    		map.get(currentClass).add(line);
		    	}
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<String> lines = map.get(ClassAbonnee.TYPE_ABONNE);
		addToFile(lines);
		lines = map.get(ClassAbonnee.TYPE_BRANCHEMENT);
		addToFile(lines);
		lines = map.get(ClassAbonnee.TYPE_POINTFOURNITURE);
		addToFile(lines);
		lines = map.get(ClassAbonnee.TYPE_CONTRATAEP);
		addToFile(lines);
		lines = map.get(ClassAbonnee.TYPE_CONTRATAC);
		addToFile(lines);
		lines = map.get(ClassAbonnee.TYPE_CONTRATABT);
		addToFile(lines);
		lines = map.get(ClassAbonnee.TYPE_FACTURE);
		addToFile(lines);
		lines = map.get(ClassAbonnee.TYPE_LIGNEFACTURE);
		addToFile(lines);
		lines = map.get(ClassAbonnee.TYPE_COMPTEUR);
		addToFile(lines);
		lines = map.get(ClassAbonnee.TYPE_RELEVE);
		addToFile(lines);
		lines = map.get(ClassAbonnee.TYPE_INTERVENTION);
		addToFile(lines);
		
		initAndCloseFile(false);
	}

//	private static String getFileName(ClassAbonnee currentClass) {
//		switch (currentClass) {
//		case TYPE_ABONNE:
//			return AbonnesDAO.TYPE_ABONNE;
//		case TYPE_BRANCHEMENT:
//			return AbonnesDAO.TYPE_BRANCHEMENT;
//		case TYPE_COMPTEUR:
//			return AbonnesDAO.TYPE_COMPTEUR;
//		case TYPE_CONTRATABT:
//			return AbonnesDAO.TYPE_CONTRATABT;
//		case TYPE_CONTRATAC:
//			return AbonnesDAO.TYPE_CONTRATAC;
//		case TYPE_CONTRATAEP:
//			return AbonnesDAO.TYPE_CONTRATAEP;
//		case TYPE_FACTURE:
//			return AbonnesDAO.TYPE_FACTURE;
//		case TYPE_INTERVENTION:
//			return AbonnesDAO.TYPE_INTERVENTION;
//		case TYPE_LIGNEFACTURE:
//			return AbonnesDAO.TYPE_LIGNEFACTURE;
//		case TYPE_POINTFOURNITURE:
//			return AbonnesDAO.TYPE_POINTFOURNITURE;
//		case TYPE_RELEVE:
//			return AbonnesDAO.TYPE_RELEVE;
//		default:
//			break;
//		}
//		return null;
//	}

	private static ClassAbonnee containsOpen(String line) {
		for (String open : OPENS) {
			if (line.contains(open)) {
				return getClasseAbonnee(open);
			}
		}
		return null;
	}
	
	private static boolean containsClose(String line) {
		for (String close : CLOSE) {
			if (line.contains(close)) {
				return true;
			}
		}
		return false;
	}
	
	private static ClassAbonnee getClasseAbonnee(String balise) {
		switch (balise) {
		case O_ABT:
		case C_ABT:
			return ClassAbonnee.TYPE_CONTRATABT;
		case O_AEP:
		case C_AEP:
			return ClassAbonnee.TYPE_CONTRATAEP;
		case O_AC:
		case C_AC:
			return ClassAbonnee.TYPE_CONTRATAC;
		case O_AB:
		case C_AB:
			return ClassAbonnee.TYPE_ABONNE;
		case O_FAC:
		case C_FAC:
			return ClassAbonnee.TYPE_FACTURE;
		case O_LIG_FAC:
		case C_LIG_FAC:
			return ClassAbonnee.TYPE_LIGNEFACTURE;
		case O_BRAN:
		case C_BRAN:
			return ClassAbonnee.TYPE_BRANCHEMENT;
		case O_PTS_FOUR:
		case C_PTS_FOUR:
			return ClassAbonnee.TYPE_POINTFOURNITURE;
		case O_COMPT:
		case C_COMPT:
			return ClassAbonnee.TYPE_COMPTEUR;
		case O_RELEVE:
		case C_RELEVE:
			return ClassAbonnee.TYPE_RELEVE;
		case O_INTER:
		case C_INTER:
			return ClassAbonnee.TYPE_INTERVENTION;
		default:
			break;
		}
		return null;
	}

	private static void addToFile(List<String> lines) {
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_OUTPUT, true))) {
			for (String line : lines) {
				bw.write(line);
				bw.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void initAndCloseFile(boolean init) {
		if (init) {
			File file = new File(PATH_OUTPUT);
			if (file.exists()) {
				file.delete();
			}
		}
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(PATH_OUTPUT, true))) {
			if (init) {
				bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				bw.newLine();
				bw.write("<GRC>");
				bw.newLine();
			}
			else {
				bw.newLine();
				bw.write("</GRC>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
