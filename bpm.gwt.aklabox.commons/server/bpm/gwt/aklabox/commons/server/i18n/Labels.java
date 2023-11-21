package bpm.gwt.aklabox.commons.server.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class Labels {

	public static String getLabel(Locale currentLocale, String key) {
		if (currentLocale == null) {
			currentLocale = new Locale("en");
		}

		ResourceBundle res = ResourceBundle.getBundle("bpm.gwt.aklabox.commons.server.i18n.Labels", currentLocale);
		return res.getString(key);
	}

	public static final String Test = "Test";
	public static final String TheDocument = "TheDocument";
	public static final String IsNowOnVersion = "IsNowOnVersion";
	public static final String HasBeenDoneOnDocument = "HasBeenDoneOnDocument";
	public static final String By = "By";
	public static final String HasUpdatedDocument = "HasUpdatedDocument";
	public static final String PermissionToPublic = "PermissionToPublic";
	public static final String ToBeSharedToYou = "ToBeSharedToYou";
	public static final String SharedToYourGroup = "SharedToYourGroup";
	public static final String HasUpdatedFolder = "HasUpdatedFolder";
	public static final String UpdatedTheCampaign = "UpdatedTheCampaign";
	public static final String AddedANoteOnTheCampaign = "AddedANoteOnTheCampaign";
	public static final String OrganizedBy = "OrganizedBy";
	public static final String Status = "Status";
	public static final String UploadedTheDocument = "AddedANoteOnTheCampaign";
	public static final String CreateAFolder = "CreateAFolder";
	public static final String UpdatedANewVersionOfDocument = "UpdatedANewVersionOfDocument";
	public static final String DeletedTheDocument = "DeletedTheDocument";
	public static final String DownloadedTheDocument = "DownloadedTheDocument";
	public static final String InsideYourFolder = "InsideYourFolder";
	public static final String ViewedTheDocument = "ViewedTheDocument";
	public static final String PlacedACommentOnTheDocument = "PlacedACommentOnTheDocument";
	public static final String YourKeyWord = "YourKeyWord";
	public static final String MatchTheDocument = "MatchTheDocument";
	public static final String UploadedBy = "UploadedBy";
	public static final String HasGivenYouATask = "HasGivenYouATask";
	public static final String HasGivenYouATaskOnDocument = "HasGivenYouATaskOnDocument";
	public static final String HasAddedAFolder = "HasAddedAFolder";
	public static final String IsAskingYourPermissionToValidate = "IsAskingYourPermissionToValidate";
	public static final String HasAddedDocument = "HasAddedDocument";
	public static final String HasCommentedOnDocument = "HasCommentedOnDocument";
	public static final String HasCommentedAboutYouOnDocument = "HasCommentedAboutYouOnDocument";
	public static final String AnExternalUserWithTheMail = "AnExternalUserWithTheMail";
	public static final String HasDowLoadedYourFolder = "HasDowLoadedYourFolder";
	public static final String HasPrintedTheDocument = "HasPrintedTheDocument";
	public static final String HasDeletedThisDdocumentBecauseItIsADuplicateOf = "HasDeletedThisDdocumentBecauseItIsADuplicateOf";
	public static final String HasChekinTheDocument = "HasChekinTheDocument";
	public static final String HasCreatedANewEvent = "HasCreatedANewEvent";
	
}
