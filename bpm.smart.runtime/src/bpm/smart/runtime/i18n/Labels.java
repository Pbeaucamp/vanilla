package bpm.smart.runtime.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class Labels {
	
	public static String getLabel(Locale currentLocale, String key) {
		if (currentLocale == null) {
			currentLocale = new Locale("en");
		}

		ResourceBundle res = ResourceBundle.getBundle("bpm.smart.runtime.i18n.Labels", currentLocale);
		return res.getString(key);
	}
	
	public static final String StartActivity = "StartActivity";
	public static final String EndActivity = "EndActivity";
	public static final String ScriptCorrect = "ScriptCorrect";
	public static final String ConnectedToFtp = "ConnectedToFtp";
	public static final String ConnectionFtpImpossible = "ConnectionFtpImpossible";
	public static final String SendingFileFtpImpossible = "SendingFileFtpImpossible";
	public static final String FtpActionCannotBeDone = "FtpActionCannotBeDone";
	public static final String ActionFtpImpossible = "ActionFtpImpossible";
	public static final String Reason = "Reason";
	public static final String NoFolderDefine = "NoFolderDefine";
	public static final String UnableToCreateFolder = "UnableToCreateFolder";
	public static final String UnableToAccessFolder = "UnableToAccessFolder";
	public static final String SavingFileImpossible = "SavingFileImpossible";
	public static final String FolderPlacement = "FolderPlacement";
	public static final String FtpUpload = "FtpUpload";
	public static final String PutOnFtp = "PutOnFtp";
	public static final String MovedFromFolder = "MovedFromFolder";
	public static final String ImpossibleToDelete = "ImpossibleToDelete";
	public static final String ImpossibleToMove = "ImpossibleToMove";
	public static final String FromFolder = "FromFolder";
	public static final String ToFolder = "ToFolder";
	public static final String DeleteFromFolder = "DeleteFromFolder";
	public static final String FileAlreadyExist = "FileAlreadyExist";
}
