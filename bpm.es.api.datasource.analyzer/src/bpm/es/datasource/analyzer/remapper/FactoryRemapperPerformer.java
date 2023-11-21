package bpm.es.datasource.analyzer.remapper;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import bpm.es.datasource.analyzer.remapper.internal.BirtRemapOda;
import bpm.es.datasource.analyzer.remapper.internal.DiscoRemap;
import bpm.es.datasource.analyzer.remapper.internal.FavRemap;
import bpm.es.datasource.analyzer.remapper.internal.FdDicoRemapDependancy;
import bpm.es.datasource.analyzer.remapper.internal.FdRemapDependancy;
import bpm.es.datasource.analyzer.remapper.internal.FmdtDrillerRemap;
import bpm.es.datasource.analyzer.remapper.internal.FwrRemapOda;
import bpm.es.datasource.analyzer.remapper.internal.GtwRemap;
import bpm.es.datasource.analyzer.remapper.internal.TaskListRemap;
import bpm.es.datasource.analyzer.remapper.internal.WkfRemap;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FactoryRemapperPerformer {

	public ModelRemapper createPerfomer(IRepositoryApi sourceSock, IRepositoryApi targetSock, RepositoryItem sourceItem, List<RepositoryItem[]> replacements, String targetGroupName) throws Exception {

		String modelXml = null;
		try {
			modelXml = sourceSock.getRepositoryService().loadModel(sourceItem);
		} catch(Exception ex) {
			throw new Exception("Error when loading model Xml\n" + ex.getMessage(), ex);
		}

		Document modelDocument = null;
		try {
			modelDocument = DocumentHelper.parseText(modelXml);
		} catch(Exception ex) {
			throw new Exception("Error when parsing model Xml\n" + ex.getMessage(), ex);
		}

		ModelRemapper modelRemapper = new ModelRemapper(modelDocument, sourceItem);

		for(RepositoryItem[] replacement : replacements) {
			switch(sourceItem.getType()) {
				case IRepositoryApi.FWR_TYPE:
					modelRemapper.addPerformer(new FwrRemapOda(modelDocument, replacement[0], replacement[1], sourceSock, targetSock, targetGroupName));
					break;
				case IRepositoryApi.FD_TYPE:
					modelRemapper.addPerformer(new FdRemapDependancy(modelDocument, replacement[0], replacement[1]));
					break;
				case IRepositoryApi.FD_DICO_TYPE:
					modelRemapper.addPerformer(new FdDicoRemapDependancy(modelDocument, replacement[0], replacement[1], sourceSock, targetSock, targetGroupName));
					break;
				case IRepositoryApi.FAV_TYPE:
					modelRemapper.addPerformer(new FavRemap(modelDocument, replacement[0], replacement[1]));
					break;
				case IRepositoryApi.BIW_TYPE:
					modelRemapper.addPerformer(new WkfRemap(modelDocument, replacement[0], replacement[1], sourceSock, targetSock));
					break;
				case IRepositoryApi.GTW_TYPE:
					modelRemapper.addPerformer(new GtwRemap(modelDocument, replacement[0], replacement[1], sourceSock, targetSock, targetGroupName));
					break;
				case IRepositoryApi.FMDT_DRILLER_TYPE:
					modelRemapper.addPerformer(new FmdtDrillerRemap(modelDocument, replacement[0], replacement[1]));
					break;
				case IRepositoryApi.TASK_LIST:
					modelRemapper.addPerformer(new TaskListRemap(modelDocument, replacement[0], replacement[1]));
					break;
				case IRepositoryApi.DISCONNECTED_PCKG:
					modelRemapper.addPerformer(new DiscoRemap(modelDocument, replacement[0], replacement[1]));
					break;
				case IRepositoryApi.CUST_TYPE:
					if(sourceItem.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE) {

						modelRemapper.addPerformer(new BirtRemapOda(modelDocument, replacement[0], replacement[1], sourceSock, targetSock, targetGroupName));
					}
					else {
						// throw new Exception("The items of type " + IRepositoryConnection.SUBTYPES_NAMES[dirIt.getSubTypeConstante()] + " cannot be remapped");
					}
					break;
				default:
					// throw new Exception("The items of type " + IRepositoryConnection.TYPES_NAMES[sourceItem.getRepositoryModelTypeConstant()] + " cannot be remapped");
			}
		}

		return modelRemapper;

	}

	// private IRemapperPerformer createBirtPerformer(Document modelXmlDocument, RepositoryItem orginialItem, RepositoryItem newItem, IRepositoryConnection sourceConnection, IRepositoryConnection targetConnection)throws Exception{
	// // switch(orginialItem.getRepositoryModelTypeConstant()){orginialItem.getItemName()
	// // case IRepositoryConnection.FMDT_TYPE:
	// return new BirtRemapOda(modelXmlDocument, orginialItem, newItem, sourceConnection, targetConnection);
	// // case IRepositoryConnection.EXTERNAL_DOCUMENT:
	// //// return new BirtRemapExtDocument(sourceItem, orginialItem, newItem);
	// // }
	// //
	// // throw new Exception("The BIRT items cannot remap a " + IRepositoryConnection.TYPES_NAMES[orginialItem.getRepositoryModelTypeConstant()] + " objects");
	// }
}
