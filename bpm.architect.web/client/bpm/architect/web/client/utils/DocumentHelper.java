package bpm.architect.web.client.utils;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.images.Images;
import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.mdm.model.supplier.Contract;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

public class DocumentHelper {

	public static void viewCurrentDocument(IWait waitPanel, final Contract object, Integer versionId) {
		waitPanel.showWaitPart(true);

		ArchitectService.Connect.getInstance().loadDocument(object, versionId, new GwtCallbackWrapper<String>(waitPanel, true) {

			@Override
			public void onSuccess(String result) {
				if (result.indexOf("<error>") > -1) {
					String msg = result.replace("<error>", "").replace("</error>", "");
					if (msg.equalsIgnoreCase("")) {
						msg = Labels.lblCnst.NoHistoryFound() + " " + object.getFileVersions().getName();
					}

					MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), Labels.lblCnst.DocumentNotFound());
				}
				else {
					String fullUrl = GWT.getHostPageBaseURL() + result;

					ToolsGWT.doRedirect(fullUrl);
				}
			}
		}.getAsyncCallback());
	}

	public static boolean checkFiletype(String fileType, String newFileName) {
		String currentFileType = fileType;
		String newFileType = getFormat(newFileName);
		return currentFileType.equalsIgnoreCase(newFileType);
	}

	public static String getFormat(String newFileName) {
		int index = newFileName.lastIndexOf(".") + 1;
		return newFileName.substring(index);
	}

	public static ImageResource getImageFormat(Contract contract) {
		String format = getFormat(contract);
		if (format != null) {
			switch (format.toLowerCase()) {
			case "csv":
				return Images.INSTANCE.file_csv_64();
			case "kml":
				return Images.INSTANCE.kml();
			case "xls":
				return Images.INSTANCE.file_xls_64();
			case "xlsx":
				return Images.INSTANCE.file_excel_64();
			case "txt":
				return Images.INSTANCE.file_txt_64();
			case "xml":
				return Images.INSTANCE.file_txt_64();
			case "shp":
				return Images.INSTANCE.file_shp_64();
			default:
				return Images.INSTANCE.file_file_64();
			}
		}
		return Images.INSTANCE.file_file_64();
	}

	public static String getFormat(Contract contract) {
		return contract.getFileVersions() != null ? contract.getFileVersions().getCurrentVersion(contract.getVersionId()).getFormat() : "";
	}
}
