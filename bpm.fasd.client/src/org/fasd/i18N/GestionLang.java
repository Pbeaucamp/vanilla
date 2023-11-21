package org.fasd.i18N;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;

public class GestionLang {

	private final int M_INT_MAXLANGUES = 100;
	// Nom du fichier de configuration
	private String m_strNomFichier = "Configuration.properties"; //$NON-NLS-1$
	// Chemin de la racine du programme
	private String m_strChemin;
	// Separateur du systeme de fichier
	private String m_strSeparateur;
	// Nom du dossier contenant les fichiers de configuration
	private static final String m_strDossierConfiguration = "Lang"; //$NON-NLS-1$

	// Constructeurs :

	// Sans paramettre
	public GestionLang() {
		this.Initialise();
	}

	// Avec nom du fichier
	public GestionLang(String p_strNomFichier) {
		this.setNomFichier(p_strNomFichier);
		this.Initialise();
	}

	// Initialisation
	private void Initialise() {
		this.setSeparateur(this.findSeparateur());
		this.setChemin(this.findChemin());
	}

	//
	// Retourne la valeur correspondant � la clef
	private String getConfiguration(String p_strClef) {
		// Cree un objet Properties
		Properties oProperties = new Properties();

		// Cree un objet FileInputStream
		FileInputStream oFileInputStream;

		try {
			// Ouvre le fichier de configuration

			String path = this.findCheminComplet();

			oFileInputStream = new FileInputStream(path);

			// Lit et charge les proprietes du fichier de configuration
			oProperties.load(oFileInputStream);

			// Ferme le fichier de configuration
			oFileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String key = p_strClef;

		// Recupere la valeur de la propriete p_strClef
		String result = oProperties.getProperty(key);
		return result;
	}// getConfiguration

	//
	/** Retourne le nom du dossier contenant les fichiers de configuration */
	private static final String getDossierConfiguration() {
		return GestionLang.m_strDossierConfiguration;
	}

	//
	/**
	 * @return le chemin complet vers le fichier et peu importe l'os
	 */
	private final String findCheminComplet() {
		return this.m_strChemin + this.m_strSeparateur + GestionLang.getDossierConfiguration() + this.m_strSeparateur + this.m_strNomFichier;
	}

	//
	/**
	 * Retourne le chemin courant correspondant au systeme de fichier utiliser
	 * par le systeme d'explotation.
	 */
	private final String findChemin() {
		return Platform.getInstallLocation().getURL().getPath();

	}

	//
	/**
	 * Retourne le separateur correspondant au systeme de fichier utiliser par
	 * le systeme d'explotation.
	 */
	private final String findSeparateur() {
		return System.getProperty("file.separator"); //$NON-NLS-1$
	}

	//
	/**
	 * Retourne la valeur correspondant � la clef en specifiant le nom du
	 * fichier de configuration
	 * 
	 * @param p_strNomFichier
	 *            le nom du fichier a traiter
	 * @param p_strClef
	 *            la clef dont on veut recuperer la valeur
	 * @return la valeur correspondant � la clef en specifiant le nom du fichier
	 *         de configuration
	 */
	public String getConfiguration(String p_strNomFichier, String p_strClef) {
		this.setNomFichier(p_strNomFichier);

		String resultat = this.getConfiguration(p_strClef);

		return resultat;
	}

	//
	/**
	 * @param p_strKey
	 *            est le libelle de la langue tel qu'elle est dans
	 *            "configuration.properties"
	 * @return le nom exacte du fichier LangageBundle correspondant a la langue
	 */
	public String getNomConfLang(String p_strKey) {
		String s = ""; //$NON-NLS-1$
		if (p_strKey != null && !p_strKey.trim().equalsIgnoreCase("")) { //$NON-NLS-1$
			s = getConfiguration(m_strNomFichier, p_strKey);
		}
		return s;
	}

	//
	/**
	 * @return la liste des differentes langues traduite
	 */
	public String[] getKeys() {
		String[] listKey = new String[M_INT_MAXLANGUES];
		int i = 0;
		Properties props = new Properties();

		FileInputStream in;
		try {
			in = new FileInputStream(findCheminComplet());
			props.load(in);
			in.close();

			Enumeration keys = props.keys();

			while (keys.hasMoreElements()) {
				String element = ((String) keys.nextElement()).trim();

				String key = ConvertUtf.decodeCyrillic(element).toString();
				listKey[i] = key;
				i++;

			}

		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String[] tab = new String[i];
		for (int a = 0; a < i; a++) {
			if (listKey[a] != null) {
				tab[a] = listKey[a];
			}
		}
		return tab;
	}

	//
	/**
	 * Defini le nom du fichier
	 * 
	 * @param p_strNomFichier
	 *            chaine representant le nom du fichier de configuration
	 */
	private void setNomFichier(String p_strNomFichier) {
		if (p_strNomFichier != null && !p_strNomFichier.equalsIgnoreCase("")) { //$NON-NLS-1$
			this.m_strNomFichier = p_strNomFichier;
		}
	}

	//
	/**
	 * Defini le Chemin
	 * 
	 * @param p_strChemin
	 *            chaine representant le chemin
	 */
	private void setChemin(String p_strChemin) {
		if (p_strChemin != null && !p_strChemin.trim().equalsIgnoreCase("")) { //$NON-NLS-1$
			this.m_strChemin = p_strChemin;
		}
	}

	//
	/**
	 * Defini le separateur
	 * 
	 * @param p_strSeparateur
	 *            chaine representant le separateur
	 */
	private void setSeparateur(String p_strSeparateur) {
		if (p_strSeparateur != null && !p_strSeparateur.trim().equalsIgnoreCase("")) { //$NON-NLS-1$
			this.m_strSeparateur = p_strSeparateur;
		}
	}
}