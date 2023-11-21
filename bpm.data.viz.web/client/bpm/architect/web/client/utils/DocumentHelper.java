package bpm.architect.web.client.utils;

import bpm.architect.web.client.I18N.Labels;
import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.mdm.model.supplier.Contract;

import com.google.gwt.core.client.GWT;

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
}
