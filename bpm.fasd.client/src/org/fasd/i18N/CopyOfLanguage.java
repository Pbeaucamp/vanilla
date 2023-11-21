package org.fasd.i18N;

//Fichier de configuration et de langage
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import org.eclipse.jface.preference.IPreferenceStore;
import org.fasd.preferences.PreferenceConstants;
import org.freeolap.FreemetricsPlugin;

public final class CopyOfLanguage {

	// langue et pays courant
	private static String m_strLangCour;
	private static String m_strCountryCour;
	private static Locale m_locLocale;

	// Getters and Setters
	/**
	 * Return current language
	 */
	public static final String getCurrentLanguage() {
		langChoisi();
		return m_locLocale.getLanguage();
	}

	//
	/**
	 * Return current country
	 * 
	 * @return current country
	 */
	public static final String getCurrentCountry() {
		langChoisi();
		return m_locLocale.getCountry();
	}

	//
	/**
	 * @param p_strKey
	 *            la cl� a traduire
	 * @return la traduction dans la langue prefere
	 */
	public static String getAnInternationalizeString(String p_strKey) {
		String configKey = init();

		GestionLang gestLan = new GestionLang("Configuration.properties"); //$NON-NLS-1$
		String nomFich = gestLan.getNomConfLang(configKey);// la langue stock�e
															// dans prefs donc
															// la cle du fichier
															// de config dans
															// Configuration.properties
		return gestLan.getConfiguration(nomFich + ".properties", p_strKey); //$NON-NLS-1$
	}

	//
	/**
	 * @param p_oDate
	 *            date
	 * @return String representing the date in the current locale lang
	 */
	public static String displayDate(Date p_oDate) {
		langChoisi();
		Locale p_oLocale = m_locLocale;
		DateFormat d_format = DateFormat.getDateInstance(DateFormat.SHORT, p_oLocale);

		return d_format.format(p_oDate);
	}

	//
	/**
	 * @param p_oNum
	 *            an object representing a numeric
	 * @return String representing a numeric in the current locale lang if isn't
	 *         displayable may return empty string
	 */
	public static String displayNumber(Object p_oNum) {
		String res = ""; //$NON-NLS-1$
		langChoisi();
		Locale p_oLocale = m_locLocale;
		NumberFormat numForm = NumberFormat.getNumberInstance(p_oLocale);
		if (p_oNum != null) {
			if (p_oNum.equals(Integer.class)) {
				res = numForm.format(p_oNum);
			} else if (p_oNum.equals(Double.class)) {
				res = numForm.format(p_oNum);
			}
		}
		return res;
	}

	//
	/**
	 * @param p_oCurrency
	 *            currency's value to display
	 * @return String representing a currency in the current locale lang if
	 *         isn't displayable may return empty string
	 */
	public static String displayCurrency(Double p_oCurrency) {
		String res = ""; //$NON-NLS-1$
		langChoisi();
		Locale p_oLocale = m_locLocale;
		NumberFormat numForm = NumberFormat.getCurrencyInstance(p_oLocale);
		if (p_oCurrency != null) {
			res = numForm.format(p_oCurrency);
		}
		return res;
	}

	//
	/**
	 * @param p_oCurrency
	 *            string representing currency's value to display
	 * @return String representing a currency in the current locale lang if
	 *         isn't displayable may return empty string
	 */
	public static String displayCurrency(String p_oCurrency) {
		String res = ""; //$NON-NLS-1$
		if (p_oCurrency != null && !p_oCurrency.trim().equalsIgnoreCase("")) { //$NON-NLS-1$
			langChoisi();
			try {
				res = displayCurrency(Double.parseDouble(p_oCurrency));
			} catch (Exception e) {
			}
		}
		return res;
	}

	//
	/**
	 * @param p_oPercent
	 *            percent's value to display
	 * @return representing a percentage in the current locale lang if isn't
	 *         displayable may return empty string
	 */
	public static String displayPercent(Double p_oPercent) {
		String res = ""; //$NON-NLS-1$
		langChoisi();
		Locale p_oLocale = m_locLocale;
		NumberFormat numForm = NumberFormat.getPercentInstance(p_oLocale);
		if (p_oPercent != null) {
			res = numForm.format(p_oPercent);
		}
		return res;
	}

	//
	/**
	 * @param p_oPercent
	 *            string representing percent's value to display
	 * @return String representing a percentage in the current locale lang if
	 *         isn't displayable may return empty string
	 */
	public static String displayPercent(String p_oPercent) {
		String res = ""; //$NON-NLS-1$
		if (p_oPercent != null && !p_oPercent.trim().equalsIgnoreCase("")) { //$NON-NLS-1$
			langChoisi();
			try {
				res = displayPercent(Double.parseDouble(p_oPercent));
			} catch (Exception e) {
			}
		}
		return res;
	}

	//
	/**
	 * cree le ressource bundle corresspondant a la langue prefere
	 */
	private static void langChoisi() {

		GestionLang g = new GestionLang();
		String[] param = extractParamLangBundl(g.getNomConfLang(CopyOfLanguage.init()));

		m_strLangCour = param[0];
		m_strCountryCour = param[1];

		m_locLocale = new Locale(m_strLangCour, m_strCountryCour);
	}

	//
	/**
	 * @param p_strFileBundl
	 *            la langue choisi par l'utilisateur
	 * @return le couple iso 639 et 3166 de la langue et du pays choisi dans un
	 *         tableau de string
	 */
	private static String[] extractParamLangBundl(String p_strFileBundl) {
		String[] para = new String[2];
		if (p_strFileBundl != null && !p_strFileBundl.trim().equalsIgnoreCase("")) { //$NON-NLS-1$
			para[0] = p_strFileBundl.split("_")[1]; //$NON-NLS-1$
			para[1] = p_strFileBundl.split("_")[2]; //$NON-NLS-1$
		} else {
			para[0] = "en"; //$NON-NLS-1$
			para[1] = "US"; //$NON-NLS-1$
		}
		return para;
	}

	//
	/**
	 * @return la langue prefere
	 */
	private static String init() {
		String lang = "English"; //$NON-NLS-1$
		// pour initialiser en cas de premiere utilisation
		try {
			IPreferenceStore store = FreemetricsPlugin.getDefault().getPreferenceStore();

			lang = store.getString(PreferenceConstants.P_LANG);
			if (lang.trim().equalsIgnoreCase("") || lang == null) { //$NON-NLS-1$
				store.setValue(PreferenceConstants.P_LANG, "English"); //$NON-NLS-1$
				store = FreemetricsPlugin.getDefault().getPreferenceStore();
				lang = store.getString(PreferenceConstants.P_LANG);

				m_strLangCour = "en"; //$NON-NLS-1$
				m_strCountryCour = "US"; //$NON-NLS-1$

				m_locLocale = new Locale(m_strLangCour, m_strCountryCour);

			}
		} catch (Exception e) {
		}
		return lang;
	}
}