package bpm.gwt.commons.client.utils;

import java.util.Date;

import org.moxieapps.gwt.uploader.client.events.FileQueueErrorEvent.ErrorCode;

import bpm.gwt.commons.client.I18N.LabelsConstants;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * Helper methods for GWT
 * 
 */
public class Tools implements IsSerializable {
	
	public static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";
	public static final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy - HH:mm:ss";
	public static final String EXPORT_DATE_FORMAT = "yyyyMMdd";
	public static final String DEFAULT_TIME_FORMAT = "HH:mm";
	public static final String DEFAULT_HOUR_FORMAT = "HH";
	public static final String DEFAULT_MIN_FORMAT = "mm";

	@SuppressWarnings("deprecation")
	public static String getFullDateAsString(Date date) {
		return Tools.getDay(date.getDay()) + " " + getDateAsString(date);
	}

	@SuppressWarnings("deprecation")
	public static String getDateAsString(Date date) {
		return date.getDate() + " " + getMonthAsString(date);
	}

	@SuppressWarnings("deprecation")
	public static String getMonthAsString(Date date) {
		return Tools.getMonth(date.getMonth() + 1) + " " + getYearAsString(date);
	}

	@SuppressWarnings("deprecation")
	public static String getWeekAsString(Date date) {
		return Tools.getDay(date.getDay()) + " " + date.getDate() + " " + Tools.getMonthAsString(date);
	}

	@SuppressWarnings("deprecation")
	public static int getYearAsString(Date date) {
		return date.getYear() + 1900;
	}

	/**
	 * Get the number of days in the selected month
	 * 
	 * @param date
	 * @return number of days
	 */
	public static int getDayInMonth(Date date) {
		Date dateNextMonth = CalendarUtil.copyDate(date);
		CalendarUtil.addMonthsToDate(dateNextMonth, 1);

		return CalendarUtil.getDaysBetween(date, dateNextMonth);
	}

	/**
	 * Get week of year of the date
	 * 
	 * @param date
	 * @return week of year
	 */
	@SuppressWarnings("deprecation")
	public static int getWeekOfYear(Date date) {
		Date yearStart = new Date(date.getYear(), 0, 1);

		return (int) ((date.getTime() - yearStart.getTime()) / (7 * 24 * 60 * 60 * 1000) + 1);
	}

	/**
	 * Get the first day of week of the date
	 * 
	 * @param date
	 * @return first day of week
	 */
	@SuppressWarnings("deprecation")
	public static Date getFirstDayOfWeek(Date date) {
		int day = date.getDay();
		CalendarUtil.addDaysToDate(date, day == 0 ? -6 : -day + 1);
		return date;
	}

	/**
	 * Get the last day of week of the date
	 * 
	 * @param date
	 * @return first day of week
	 */
	@SuppressWarnings("deprecation")
	public static Date getLastDayOfWeek(Date date) {
		int day = date.getDay();
		CalendarUtil.addDaysToDate(date, day == 0 ? day : 7 - day);
		return date;
	}

	public static String getDay(int day) {
		switch (day) {
		case 0:
			return LabelsConstants.lblCnst.Sunday();
		case 1:
			return LabelsConstants.lblCnst.Monday();
		case 2:
			return LabelsConstants.lblCnst.Tuesday();
		case 3:
			return LabelsConstants.lblCnst.Wenesday();
		case 4:
			return LabelsConstants.lblCnst.Thursday();
		case 5:
			return LabelsConstants.lblCnst.Friday();
		case 6:
			return LabelsConstants.lblCnst.Saturday();
		default:
			break;
		}
		return "";
	}

	public static String getDayFirstLetter(int day) {
		switch (day) {
		case 0:
			return LabelsConstants.lblCnst.DimancheFL();
		case 1:
			return LabelsConstants.lblCnst.LundiFL();
		case 2:
			return LabelsConstants.lblCnst.MardiFL();
		case 3:
			return LabelsConstants.lblCnst.MercrediFL();
		case 4:
			return LabelsConstants.lblCnst.JeudiFL();
		case 5:
			return LabelsConstants.lblCnst.VendrediFL();
		case 6:
			return LabelsConstants.lblCnst.SamediFL();
		default:
			break;
		}
		return "";
	}

	public static String getMonth(int month) {
		switch (month) {
		case 1:
			return LabelsConstants.lblCnst.January();
		case 2:
			return LabelsConstants.lblCnst.February();
		case 3:
			return LabelsConstants.lblCnst.March();
		case 4:
			return LabelsConstants.lblCnst.April();
		case 5:
			return LabelsConstants.lblCnst.May();
		case 6:
			return LabelsConstants.lblCnst.June();
		case 7:
			return LabelsConstants.lblCnst.July();
		case 8:
			return LabelsConstants.lblCnst.August();
		case 9:
			return LabelsConstants.lblCnst.September();
		case 10:
			return LabelsConstants.lblCnst.October();
		case 11:
			return LabelsConstants.lblCnst.November();
		case 12:
			return LabelsConstants.lblCnst.December();
		default:
			break;
		}
		return "";
	}

	public static String getLabel(ErrorCode errorCode) {
		switch (errorCode) {
		case UNKNOWN:
			return LabelsConstants.lblCnst.Inconnue();
		case QUEUE_LIMIT_EXCEEDED:
			return LabelsConstants.lblCnst.NombreDUploadSimultaneAtteint();
		case FILE_EXCEEDS_SIZE_LIMIT:
			return LabelsConstants.lblCnst.TailleDuFichierTropImportante();
		case ZERO_BYTE_FILE:
			return LabelsConstants.lblCnst.FichierVide();
		case INVALID_FILETYPE:
			return LabelsConstants.lblCnst.TypeDeFichierIncorrect();
		default:
			break;
		}
		return null;
	}

	public static String getLabel(org.moxieapps.gwt.uploader.client.events.UploadErrorEvent.ErrorCode errorCode) {
		switch (errorCode) {
		case UNKNOWN:
			return LabelsConstants.lblCnst.Inconnue();
		case HTTP_ERROR:
			return LabelsConstants.lblCnst.ErreurHTTP();
		case MISSING_UPLOAD_URL:
			return LabelsConstants.lblCnst.URLDUploadManquante();
		case IO_ERROR:
			return LabelsConstants.lblCnst.ErreurIO();
		case SECURITY_ERROR:
			return LabelsConstants.lblCnst.ErreurDeSecurite();
		case UPLOAD_LIMIT_EXCEEDED:
			return LabelsConstants.lblCnst.LimiteDUploadDepassee();
		case UPLOAD_FAILED:
			return LabelsConstants.lblCnst.ErreurDUpload();
		case SPECIFIED_FILE_ID_NOT_FOUND:
			return LabelsConstants.lblCnst.IDDeFichierInconnu();
		case FILE_VALIDATION_FAILED:
			return LabelsConstants.lblCnst.ErreurDeValidationDuFichier();
		case FILE_CANCELLED:
			return LabelsConstants.lblCnst.AnnulationDeLUpload();
		case UPLOAD_STOPPED:
			return LabelsConstants.lblCnst.UploadStoppe();
		default:
			break;
		}
		return null;
	}
	
	/**
	 * Used for theme
	 * 
	 * @param linkElementId
	 * @param url
	 */
	public static native void setLinkHref(String linkElementId, String url) /*-{ 
    	var link = $doc.getElementById(linkElementId); 
    	if (link != null && link != undefined) { 
			link.href = url; 
		} 
	}-*/;
	
	/**
	 * This method open a new page with the given url as target
	 * 
	 * @param url the target 
	 */
	public static native void doRedirect(String url)/*-{
		$wnd.open(url);
	}-*/;

	/**
	 * This method dynamically change the current URL 
	 * @param url the new URL string
	 */
	public static native void changeCurrURL(String url)/*-{
    	$wnd.location.replace(url);
	}-*/;
}
