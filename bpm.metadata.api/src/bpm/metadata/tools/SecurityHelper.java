package bpm.metadata.tools;

import bpm.metadata.ISecurizable;
import bpm.metadata.MetaData;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.resource.IResource;

public class SecurityHelper {

	public static void grantGlobal(MetaData metadata, String groupName, boolean secu) {

		// datasource
		for (IDataStream ds : metadata.getDataSources().iterator().next().getDataStreams()) {
			for (IDataStreamElement elem : ds.getElements()) {
				elem.setGranted(groupName, secu);
				elem.setVisible(groupName, secu);
			}
		}

		// business
		for (IBusinessModel model : metadata.getBusinessModels()) {
			((BusinessModel) model).setGranted(groupName, secu);
			for (IBusinessPackage pack : model.getBusinessPackages("none")) {
				pack.setGranted(groupName, secu);
				for (IBusinessTable table : pack.getBusinessTables("none")) {
					table.setGranted(groupName, secu);
				}
			}
		}

		// resources
		for (IResource res : metadata.getResources()) {
			if (res instanceof ISecurizable) {
				((ISecurizable) res).setGranted(groupName, secu);
			}
		}
	}

}
