package bpm.vanillahub.core;

public enum Formats {
	
	HTML(1, "html", "text/html"),
	PDF(2, "pdf", "application/pdf"),
	OOTEXT(3, "odt", "application/vnd.oasis.opendocument.text"),
	WORD(4, "doc", "application/msword"),
	OOCALC(5, "ods", "application/vnd.oasis.opendocument.spreadsheet"),
	EXCEL(6, "xls", "application/vnd.ms-excel"),
	RTF(7, "rtf", "application/rtf"),
	PPT(8, "ppt", "application/vnd.ms-powerpoint"),
	POSTSCRIPT(9, "postscript", "application/postscript"),
	TXT(10, "postscript", "application/postscript"),
	EXCELX(11, "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
	WORDX(12, "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
	PRESENTATION(13, "ppt", "application/vnd.ms-powerpoint"),
	PRESENTATIONX(14, "pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
	XML(15, "xml", "text/xml"),
	CSV(16, "csv", "text/csv"),
	ZIP(17, "zip", "multipart/x-zip"),
	TAR_GZ(18, "tar.gz", "application/x-gzip"),
	TAR(19, "tar", "application/x-tar"),
	GZ(20, "gz", "application/x-gzip"),
	JAR(21, "jar", "application/java-archive"),
	RAR(22, "rar", "application/x-rar-compressed"),
	JPG(23, "jpg", "image/jpeg"),
	JPEG(24, "jpeg", "image/jpeg"),
	GIF(25, "gif", "image/gif"),
	PNG(26, "png", "image/png"),
	SVG(27, "svg", "image/svg+xml"),
	BMP(28, "bmp", "image/bmp"),
	MPG(29, "mpg", "video/mpeg"),
	MPEG(30, "mpeg", "video/mpeg"),
	AVI(31, "avi", "video/msvideo"),
	MP4(32, "mp4", "video/mp4"),
	WMV(33, "wmv", "audio/x-ms-wmv"),
	MOV(34, "mov", "video/quicktime"),
	MKV(35, "mkv", "video/x-matroska"),
	OGG(36, "ogg", "audio/ogg"),
	MP3(37, "mp3", "audio/mpeg"),
	WAV(38, "wav", "audio/wav"),
	FLAC(39, "flac", "audio/flac"),
	WMA(40, "wma", "audio/x-ms-wma"),
	M4A(41, "m4a", "audio/mp4a-latm"),
	UNKNOWN(42, "", "");
	
	private int id;
	private String extension;
	private String mime;
	
	private Formats(int id, String extension, String mime) {
		this.id = id;
		this.extension = extension;
		this.mime = mime;
	}
	
	public int getId() {
		return id;
	}

	public String getExtension() {
		return extension;
	}

	public String getMime() {
		return mime;
	}
	
	

}
