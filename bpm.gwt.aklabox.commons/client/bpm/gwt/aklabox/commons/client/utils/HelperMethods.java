package bpm.gwt.aklabox.commons.client.utils;

import bpm.document.management.core.model.FormField.FormFieldType;
import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;

import com.google.gwt.i18n.client.NumberFormat;

public class HelperMethods {

	private static final long M = 1024;
	private static final long G = M * M;
	private static final long T = G * M;

	public static String byteCount(int fileSize) {
		final long[] dividers = new long[] { T, G, M, 1 };
		final String[] units = new String[] { "TB", "GB", "MB", "KB" };
		if (fileSize < 1)
			return "0 KB";
		String result = null;
		for (int i = 0; i < dividers.length; i++) {
			final long divider = dividers[i];
			if (fileSize >= divider) {
				result = format(fileSize, divider, units[i]);
				break;
			}
		}
		return result;
	}

	private static String format(final long value, final long divider, final String unit) {
		final double result = divider > 1 ? (double) value / (double) divider : (double) value;
		return NumberFormat.getDecimalFormat().format(result) + " " + unit;
	}
	
	public static String getFormFieldTypeLabel(FormFieldType type) {
		if (type == null) {
			return LabelsConstants.lblCnst.Unknown();
		}
		
		switch (type) {
		case ADDRESS:
			return LabelsConstants.lblCnst.Address();
		case BOOLEAN:
			return LabelsConstants.lblCnst.Boolean();
		case CALENDAR_TYPE:
			return LabelsConstants.lblCnst.CalendarType();
		case DATE:
			return LabelsConstants.lblCnst.Date();
		case DEED_CLASSIFICATION:
			return LabelsConstants.lblCnst.DeedClassification();
		case INTEGER:
			return LabelsConstants.lblCnst.Integer();
		case LOV:
			return LabelsConstants.lblCnst.Lov();
		case NAME:
			return LabelsConstants.lblCnst.Name();
		case OPTIONS:
			return LabelsConstants.lblCnst.Options();
		case STRING:
			return LabelsConstants.lblCnst.String();
		case THRESHOLD:
			return LabelsConstants.lblCnst.Threshold();
		case UPLOAD_DOCUMENT:
			return LabelsConstants.lblCnst.UploadDocument();
		default:
			break;
		}
		return LabelsConstants.lblCnst.Unknown();
	}
}
