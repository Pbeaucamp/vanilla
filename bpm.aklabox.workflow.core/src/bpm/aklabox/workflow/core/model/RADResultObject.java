package bpm.aklabox.workflow.core.model;

import java.io.Serializable;
import java.util.List;

import bpm.aklabox.workflow.core.model.resources.FormCellResult;
import bpm.aklabox.workflow.core.model.resources.StandardForm;
import bpm.document.management.core.model.Chorus;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.FormFieldValue;
import bpm.document.management.core.model.ScanDocumentType;

public class RADResultObject implements Serializable{

	private static final long serialVersionUID = 9062892169582350305L;
	
	public static enum OCR_LANG{
		AFRIKAANS("afr", "Afrikaans"),
		ARABIC("ara", "Arabic"), 
		AZERBAIJANI("aze", "Azerbaijani"), 
		BELARUSIAN("bel", "Belarusian"),
		BENGALI("ben", "Bengali"),
		BULGARIAN("bul", "Bulgarian"),
		CATALAN("cat", "Catalan, Valencian"),
		CZECH("ces", "Czech"),
		CHINESE_SIMPLE("chi_sim", "Chinese - Simplified"),
		CHINESE_TRAD("chi_tra", "Chinese - Traditional"),
		CHEROKEE("chr", "Cherokee"),
		DANISH("dan", "Danish"),
		GERMAN("deu", "German"),
		GREEK("ell", "Greek, Modern (1453-)"),
		ENGLISH("eng", "English"),
		ENGLISH_MIDDLE("enm", "English, Middle (1100-1500)"),
		ESPERANTO("epo", "Esperanto"),
		ESTONIAN("est", "Estonian"),
		BASQUE("eus", "Basque"),
		FINNISH("fin", "Finnish"),
		FRENCH("fra", "French"),
		FRANKISH("frk", "Frankish"),
		FRENCH_MIDDLE("frm", "French, Middle (ca. 1400-1600)"),
		GALICIAN("glg", "Galician"),
		GREEK_ANCIENT("grc", "Greek, Ancient (-1453)"),
		HEBREW("heb", "Hebrew"),
		HINDI("hin", "Hindi"),
		CROATIAN("hrv", "Croatian"),
		HUNGARIAN("hun", "Hungarian"),
		INDONESIAN("ind", "Indonesian"),
		ICELANDIC("isl", "Icelandic"),
		ITALIAN("ita", "Italian"),
		ITALIAN_OLD("ita_old", "Italian - Old"),
		JAPANESE("jpn", "Japanese"),
		KANNADA("kan", "Kannada"),
		KOREAN("kor", "Korean"),
		LATVIAN("lav", "Latvian"),
		LITHUANIAN("lit", "Lithuanian"),
		MALAYALAM("mal", "Malayalam"),
		MACEDONIAN("mkd", "Macedonian"),
		MALTESE("mlt", "Maltese"),
		MALAY("msa", "Malay"),
		DUTCH("nld", "Dutch, Flemish"),
		NORWEGIAN("nor", "Norwegian"),
		POLISH("pol", "Polish"),
		PORTUGUESE("por", "Portuguese"),
		ROMANIAN("ron", "Romanian, Moldavian, Moldovan"),
		RUSSIAN("rus", "Russian"),
		SLOVAK("slk", "Slovak"),
		SLOVENIAN("slv", "Slovenian"),
		SPANISH("spa", "Spanish, Castilian"),
		SPANISH_OLD("spa_old", "Spanish, Castilian - Old"),
		ALBANIAN("sqi", "Albanian"),
		SERBIAN("srp", "Serbian"),
		SWAHILI("swa", "Swahili"),
		SWEDISH("swe", "Swedish"),
		TAMIL("tam", "Tamil"),
		TELUGU("tel", "Telugu"),
		TAGALOG("tgl", "Tagalog"),
		THAI("tha", "Thai"),
		TURKISH("tur", "Turkish"),
		UKRAINIAN("ukr", "Ukrainian"),
		VIETNAMESE("vie", "Vietnamese");
		
		private String code;
		private String label;
		private OCR_LANG(String code, String label){
			this.code = code;
			this.label = label;
		}
		public String getCode() {
			return code;
		}
		public String getLabel() {
			return label;
		}
		
		
	}
	
	private Documents document;
	private FormCellResult formCellResult;
	private StandardForm foundForm;
	private ScanDocumentType type;
	private Chorus chorus;
	private List<FormFieldValue> fieldsValues;
	
	public RADResultObject() {
		super();
	}

	public RADResultObject(Documents document, FormCellResult formCellResult, StandardForm foundForm, ScanDocumentType type) {
		super();
		this.document = document;
		this.formCellResult = formCellResult;
		this.foundForm = foundForm;
		this.type = type;
	}

	public Documents getDocument() {
		return document;
	}

	public void setDocument(Documents document) {
		this.document = document;
	}

	public FormCellResult getFormCellResult() {
		return formCellResult;
	}

	public void setFormCellResult(FormCellResult formCellResult) {
		this.formCellResult = formCellResult;
	}

	public StandardForm getFoundForm() {
		return foundForm;
	}

	public void setFoundForm(StandardForm foundForm) {
		this.foundForm = foundForm;
	}

	public ScanDocumentType getType() {
		return type;
	}

	public void setType(ScanDocumentType type) {
		this.type = type;
	}

	public Chorus getChorus() {
		return chorus;
	}

	public void setChorus(Chorus chorus) {
		this.chorus = chorus;
	}

	public List<FormFieldValue> getFieldsValues() {
		return fieldsValues;
	}

	public void setFieldsValues(List<FormFieldValue> fieldsValues) {
		this.fieldsValues = fieldsValues;
	}
	

}
