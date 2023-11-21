package bpm.document.management.core.utils;

import java.util.ArrayList;
import java.util.List;

import bpm.document.management.core.model.Documents;

public class DocumentUtils {

	public enum FileType {
		IMAGE, AUDIO, VIDEO, TEXT, ZIP, XAKL, PDF, URL, VANILLA, DOCUMENT, SPREADSHEET, PRESENTATION, FILE, DWG, DXF, ACCESS, ORBEON;
	}

	public static final String IMAGE_DEST = "Documents/Images/";
	public static final String OFFICE_DEST = "Documents/Office/";
	public static final String AUDIO_DEST = "Documents/Audio/";
	public static final String VIDEO_DEST = "Documents/Videos/";
	public static final String OTHERS_DEST = "Documents/Others/";
	public static final String TEXT_DEST = "Documents/Text/";
	public static final String ZIP_DEST = "Documents/Zip/";

	public static final String IMAGE = "image";
	public static final String OFFICE = "office";
	public static final String AUDIO = "audio";
	public static final String VIDEO = "video";
	public static final String OTHERS = "others";
	public static final String TEXT = "text";
	public static final String ZIP = "zip";
	public static final String XAKL = "xakl";
	public static final String PDF = "pdf";
	public static final String URL = "url";
	public static final String VANILLA = "vanilla";
	public static final String DRAWING = "drawing";
	public static final String DOCUMENT = "document";
	public static final String SPREADSHEET = "spreadsheet";
	public static final String PRESENTATION = "presentation";
	public static final String FILE = "file";
	public static final String DWG = "dwg";
	public static final String DXF = "dxf";
	public static final String PDFFORM = "pdfForm";
	public static final String ORBEON = "orbeon";

	private List<String> audioExtension = new ArrayList<String>();
	private List<String> videoExtension = new ArrayList<String>();
	private List<String> imageExtension = new ArrayList<String>();
	private List<String> officeExtension = new ArrayList<String>();
	private List<String> vanillaExtension = new ArrayList<String>();
	private List<String> zipExtension = new ArrayList<String>();
	private List<String> textExtension = new ArrayList<String>();
	private List<String> xaklExtension = new ArrayList<String>();

	private List<String> wordDoc = new ArrayList<String>();
	private List<String> excelDoc = new ArrayList<String>();
	private List<String> powerPointDoc = new ArrayList<String>();
	private List<String> accessDoc = new ArrayList<String>();
	private List<String> pdfDoc = new ArrayList<String>();
	private List<String> dwgExtension = new ArrayList<String>();
	private List<String> dxfExtension = new ArrayList<String>();
	private List<String> urlExtension = new ArrayList<String>();

	public DocumentUtils() {
		imageExtension();
		audioExtension();
		videoExtension();
		officeExtension();
		vanillaExtension();
		zipExtension();
		textExtension();
		xaklExtension();
		dwgExtension();
		dxfExtension();
		urlExtension();
	}

	private List<String> zipExtension() {
		zipExtension.add("zip");
		zipExtension.add("aklabox");
		return zipExtension;
	}

	private List<String> urlExtension() {
		urlExtension.add("url");
		return urlExtension;
	}

	private List<String> textExtension() {
		textExtension.add("css");
		textExtension.add("html");
		textExtension.add("js");
		textExtension.add("txt");
		textExtension.add("xml");
		textExtension.add("csv");
		textExtension.add("txt");
		return textExtension;
	}

	private List<String> xaklExtension() {
		xaklExtension.add("xakl");
		return xaklExtension;
	}

	private List<String> imageExtension() {
		imageExtension.add("png");
		imageExtension.add("jpg");
		imageExtension.add("jpeg");
		imageExtension.add("bmp");
		imageExtension.add("gif");
		imageExtension.add("tiff");
		imageExtension.add("tif");
		imageExtension.add("svg");
		return imageExtension;
	}

	private List<String> audioExtension() {
		audioExtension.add("mpga");
		audioExtension.add("mp3");
		audioExtension.add("mp4a");
		audioExtension.add("lame");
		audioExtension.add("a52");
		audioExtension.add("a52b");
		audioExtension.add("ilbc");
		audioExtension.add("qclp");
		audioExtension.add("lpcj");
		audioExtension.add("dnet");
		audioExtension.add("sipr");
		audioExtension.add("cook");
		audioExtension.add("atrc");
		audioExtension.add("raac");
		audioExtension.add("racp");
		audioExtension.add("ralf");
		audioExtension.add("spex");
		audioExtension.add("vorb");
		audioExtension.add("dts");
		audioExtension.add("wma");
		audioExtension.add("wma1");
		audioExtension.add("wma2");
		audioExtension.add("flac");
		audioExtension.add("samr");
		audioExtension.add("sonc");
		audioExtension.add("au");
		audioExtension.add("mpg");
		audioExtension.add("mp2");
		return audioExtension;
	}

	private List<String> videoExtension() {
		videoExtension.add("mpeg");
		videoExtension.add("mp1v");
		videoExtension.add("mpg1");
		videoExtension.add("pim1");
		videoExtension.add("mp2v");
		videoExtension.add("mpg2");
		videoExtension.add("vcr2");
		videoExtension.add("hdv1");
		videoExtension.add("hdv2");
		videoExtension.add("hdv3");
		videoExtension.add("div1");
		videoExtension.add("div2");
		videoExtension.add("div3");
		videoExtension.add("col1");
		videoExtension.add("col0");
		videoExtension.add("3ivd");
		videoExtension.add("divx");
		videoExtension.add("xvid");
		videoExtension.add("mp4s");
		videoExtension.add("mp4s2");
		videoExtension.add("mp4v");
		videoExtension.add("fmp4");
		videoExtension.add("3iv2");
		videoExtension.add("smp4");
		videoExtension.add("mp4");
		videoExtension.add("h261");
		videoExtension.add("h262");
		videoExtension.add("h263");
		videoExtension.add("h264");
		videoExtension.add("s264");
		videoExtension.add("avc1");
		videoExtension.add("davc");
		videoExtension.add("h264");
		videoExtension.add("x264");
		videoExtension.add("vssh");
		videoExtension.add("svq");
		videoExtension.add("cvid");
		videoExtension.add("thra");
		videoExtension.add("wmv");
		videoExtension.add("wmv1");
		videoExtension.add("wmv2");
		videoExtension.add("wmv3");
		videoExtension.add("wmva");
		videoExtension.add("wvc1");
		videoExtension.add("vp31");
		videoExtension.add("vp30");
		videoExtension.add("vp3");
		videoExtension.add("vp50");
		videoExtension.add("vp5");
		videoExtension.add("vp51");
		videoExtension.add("vp60");
		videoExtension.add("vp61");
		videoExtension.add("vp62");
		videoExtension.add("vp6f");
		videoExtension.add("vp6a");
		videoExtension.add("fsv1");
		videoExtension.add("iv31");
		videoExtension.add("iv32");
		videoExtension.add("rv10");
		videoExtension.add("rv13");
		videoExtension.add("rv20");
		videoExtension.add("bbcd");
		videoExtension.add("webm");
		videoExtension.add("ogg");
		videoExtension.add("ogm");
		videoExtension.add("3gp");
		videoExtension.add("asf");
		videoExtension.add("rv10");
		videoExtension.add("rv13");
		videoExtension.add("rv20");
		videoExtension.add("bbcd");
		videoExtension.add("webm");
		videoExtension.add("ogg");
		videoExtension.add("ogm");
		videoExtension.add("3gp");
		videoExtension.add("asf");
		videoExtension.add("avi");
		videoExtension.add("flv");
		videoExtension.add("mov");
		videoExtension.add("mkv");
		videoExtension.add("mka");
		videoExtension.add("ts");
		videoExtension.add("mpg");
		videoExtension.add("nsc");
		videoExtension.add("nsv");
		videoExtension.add("nut");
		videoExtension.add("ra");
		videoExtension.add("ram");
		videoExtension.add("rm");
		videoExtension.add("rv");
		videoExtension.add("rmbv");
		videoExtension.add("a52");
		videoExtension.add("dts");
		videoExtension.add("aac");
		videoExtension.add("flac");
		videoExtension.add("dv");
		videoExtension.add("vid");
		videoExtension.add("tta");
		videoExtension.add("tac");
		videoExtension.add("ty");
		videoExtension.add("wav");
		videoExtension.add("dts");
		videoExtension.add("xa");
		videoExtension.add("mkv");

		return videoExtension;
	}

	private List<String> dwgExtension() {
		dwgExtension.add("dwg");
		return dwgExtension;
	}

	private List<String> dxfExtension() {
		dxfExtension.add("dxf");
		return dxfExtension;
	}

	private List<String> officeExtension() {
		// WORD
		officeExtension.add("doc");
		officeExtension.add("docx");
		wordDoc.add("doc");
		wordDoc.add("docx");
		officeExtension.add("odt");
		officeExtension.add("odt");
		wordDoc.add("odt");
		wordDoc.add("odt");
		// EXCEL
		officeExtension.add("xls");
		officeExtension.add("xlsx");
		excelDoc.add("xls");
		excelDoc.add("xlsx");
		officeExtension.add("ods");
		officeExtension.add("ods");
		excelDoc.add("ods");
		excelDoc.add("ods");
		// POWERPOINT
		officeExtension.add("ppt");
		officeExtension.add("pptx");
		powerPointDoc.add("ppt");
		powerPointDoc.add("pptx");
		officeExtension.add("odp");
		powerPointDoc.add("odp");
		// ACCESS
		officeExtension.add("mdb");
		officeExtension.add("accdb");
		accessDoc.add("mdb");
		accessDoc.add("accdb");
		// PDF
		officeExtension.add("pdf");
		pdfDoc.add("pdf");
		return officeExtension;
	}

	private List<String> vanillaExtension() {
		// PACKAGE
		vanillaExtension.add("vanillapackage");
		// REPORTS
		vanillaExtension.add("rptdesign");
		// DASHBOARD
		vanillaExtension.add("freedashboard");
		// CUBE
		vanillaExtension.add("fasd");
		// BI GATEWAY
		vanillaExtension.add("gateway");
		return vanillaExtension;
	}

	public boolean isImage(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : imageExtension) {
			if (extension.equals(e)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAudio(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : audioExtension) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isUrl(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : urlExtension) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isVideo(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : videoExtension) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isOffice(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : officeExtension) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isWordDocument(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : wordDoc) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isDwgDocument(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : dwgExtension) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isDxfDocument(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : dxfExtension) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isExcelDocument(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : excelDoc) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isPowerPointDocument(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : powerPointDoc) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAccessDocument(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : accessDoc) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isPDFDocument(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : pdfDoc) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isVanilla(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : vanillaExtension) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isZip(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : zipExtension) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isXaklFile(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : xaklExtension) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public boolean isTextFile(String fileName) {
		String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
		for (String e : textExtension) {
			if (e.equals(extension)) {
				return true;
			}
		}
		return false;
	}

	public List<String> getDocumentTypes() {
		List<String> types = new ArrayList<String>();
		types.add("dossier");//AklaboxConstant.FOLDER);
		types.add(OFFICE);
		types.add(IMAGE);
		types.add(AUDIO);
		types.add(VIDEO);
		types.add(VANILLA);
		types.add(ZIP);
		types.add("texte");//TEXT);
		types.add(XAKL);
		return types;
	}

	public List<String> getAudioExtension() {
		return audioExtension;
	}

	public List<String> getVideoExtension() {
		return videoExtension;
	}

	public List<String> getImageExtension() {
		return imageExtension;
	}

	public List<String> getOfficeExtension() {
		return officeExtension;
	}

	public List<String> getVanillaExtension() {
		return vanillaExtension;
	}

	public List<String> getZipExtension() {
		return zipExtension;
	}

	public void setZipExtension(List<String> zipExtension) {
		this.zipExtension = zipExtension;
	}

	public List<String> getTextExtension() {
		return textExtension;
	}

	public void setTextExtension(List<String> textExtension) {
		this.textExtension = textExtension;
	}

	public List<String> getXaklExtension() {
		return xaklExtension;
	}

	public void setXaklExtension(List<String> xaklExtension) {
		this.xaklExtension = xaklExtension;
	}

	public static String getCodePattern(Documents doc, String codePattern) {
		if (codePattern.equals("{DOCUMENT_NAME}")) {
			return doc.getName();
		}
		if (codePattern.equals("{CREATION_DATE}")) {
			return doc.getCreationDate().toString();
		}
		if (codePattern.equals("{UPLOAD_DATE}")) {
			return doc.getUploadDate().toString();
		}
		if (codePattern.equals("{LAST_MODIFIED}")) {
			return doc.getLastModified().toString();
		}
		if (codePattern.equals("{TYPE}")) {
			return doc.getType().toString();
		}
		if (codePattern.equals("{FILE_EXTENSION}")) {
			return doc.getFileExtension().toString();
		}
//		if (codePattern.equals("{ITEM_TYPE_ID}")) {
//			return ((Integer) doc.getItemTypeId()).toString();
//		}
		if (codePattern.equals("{USER_ID}")) {
			return ((Integer) doc.getUserId()).toString();
		}
//		if (codePattern.equals("{SECURITY_STATUS}")) {
//			return doc.getSecurityStatus().toString();
//		}
		if (codePattern.equals("{ANNOTATION}")) {
			return doc.getAnnotation().toString();
		}
		if (codePattern.equals("{LAST_MODIFIED_BY}")) {
			return doc.getLastModifiedBy().toString();
		}
		if (codePattern.equals("{AUTHOR_NAME}")) {
			return doc.getAuthorName().toString();
		}
		if (codePattern.equals("{VALIDATION_DATE}")) {
			return doc.getValidationDate().toString();
		}
		if (codePattern.equals("{VALIDATION_STATUS}")) {
			return doc.getValidationStatus().toString();
		}
		return "";
	}

	public FileType getFileType(String fileName) {
		try {
			String extension = fileName.replace(fileName.substring(0, fileName.lastIndexOf(".") + 1), "").toLowerCase();
			for (String e : imageExtension) {
				if (extension.equals(e)) {
					return FileType.IMAGE;
				}
			}
	
			for (String e : audioExtension) {
				if (e.equals(extension)) {
					return FileType.AUDIO;
				}
			}
	
			for (String e : urlExtension) {
				if (e.equals(extension)) {
					return FileType.URL;
				}
			}
	
			for (String e : videoExtension) {
				if (e.equals(extension)) {
					return FileType.VIDEO;
				}
			}
	
			for (String e : wordDoc) {
				if (e.equals(extension)) {
					return FileType.DOCUMENT;
				}
			}
	
			for (String e : dwgExtension) {
				if (e.equals(extension)) {
					return FileType.DWG;
				}
			}
	
			for (String e : dxfExtension) {
				if (e.equals(extension)) {
					return FileType.DXF;
				}
			}
	
			for (String e : excelDoc) {
				if (e.equals(extension)) {
					return FileType.SPREADSHEET;
				}
			}
	
			for (String e : powerPointDoc) {
				if (e.equals(extension)) {
					return FileType.PRESENTATION;
				}
			}
	
			for (String e : accessDoc) {
				if (e.equals(extension)) {
					return FileType.ACCESS;
				}
			}
	
			for (String e : pdfDoc) {
				if (e.equals(extension)) {
					return FileType.PDF;
				}
			}
	
			for (String e : vanillaExtension) {
				if (e.equals(extension)) {
					return FileType.VANILLA;
				}
			}
	
			for (String e : zipExtension) {
				if (e.equals(extension)) {
					return FileType.ZIP;
				}
			}
	
			for (String e : xaklExtension) {
				if (e.equals(extension)) {
					return FileType.XAKL;
				}
			}
	
			for (String e : textExtension) {
				if (e.equals(extension)) {
					return FileType.TEXT;
				}
			}
			
			return FileType.FILE;
		} catch(Exception e) {
			e.printStackTrace();
			return FileType.FILE;
		}
	}
}
