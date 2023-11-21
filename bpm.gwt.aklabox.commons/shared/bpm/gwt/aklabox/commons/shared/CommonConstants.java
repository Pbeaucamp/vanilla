package bpm.gwt.aklabox.commons.shared;

public class CommonConstants {
	public static final String PREVIEW_STREAM_SERVLET = "DownloadStreamServlet";
	
	public static final String STREAM_HASHMAP_NAME = "streamHashName";
	public static final String STREAM_DOCUMENT_ID = "streamDocumentId";
	public static final String STREAM_HASHMAP_FORMAT = "streamHashFormat";
	public static final String STREAM_CHECKOUT = "streamCheckout";
	public static final String STREAM_TYPE_DOCUMENT = "streamTypeDocument";
	public static final String STREAM_TYPE_FILE = "streamTypeFile";
	public static final String STREAM_FILE_NAME = "streamFileName";
	public static final String STREAM_FILE_INDEX = "streamFileIndex";
	
	public static final int STREAM_TYPE_DOCUMENT_CLASSIC = 0;
	public static final int STREAM_TYPE_AKLADEMAT_ENTITY_RECEIPT = 1;
	public static final int STREAM_TYPE_AKLADEMAT_ENTITY_RETURN = 2;
	public static final int STREAM_TYPE_AKLADEMAT_ENTITY = 3;
	
	public final static String COOKIE_EMAIL = "cookie_email";
	public final static String CSS_ERROR = "error";
	public final static String REGEX_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public static final String SID = "sid";
	public static final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.

	public static final String AKLABOX = "Aklabox";
	public static final String AKLAD = "Aklad";
	public static final String AKLADEMAT = "AklaDemat";
	public static final String AKLAFLOW = "Aklaflow";
	public static final String AKLABOX_MANAGER = "AklaBoxManager";
}
