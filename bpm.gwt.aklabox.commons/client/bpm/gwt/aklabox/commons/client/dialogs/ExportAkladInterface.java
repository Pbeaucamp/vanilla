package bpm.gwt.aklabox.commons.client.dialogs;

import java.util.List;

import bpm.aklabox.workflow.core.model.resources.AkLadExportObject;

public interface ExportAkladInterface {

	public void manageSentDocuments(List<AkLadExportObject> exports);
}
