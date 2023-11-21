package bpm.vanilla.platform.core.components;

import java.io.InputStream;
import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.historic.HistoricRuntimeConfiguration;
import bpm.vanilla.platform.core.components.historic.HistorizationConfig;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

/**
 * Only for historic, for the GED see "IGedComponent"
 * 
 * @author Marc Lanquetin
 *
 */
public interface ReportHistoricComponent {
//	public static final String SERVLET_REPORT_HISTORIC = "/ged/reportHistoric";
	public static enum ActionType implements IXmlActionType{
		Historize(Level.INFO), MassHistorize(Level.INFO), List(Level.DEBUG), Load(Level.DEBUG), Delete(Level.INFO), Clear(Level.INFO), 
		REMOVE_DOCUMENT_VERSION(Level.INFO), Grant_Access(Level.INFO), Remove_Access(Level.INFO), Get_Access(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}

	/**
	 * USED ONLY FOR MASS HISTORIZATION, 
	 * USE "public Integer historizeReport(HistoricRuntimeConfiguration conf, InputStream is) throws Exception;" INSTEAD
	 * 
	 * create historic entries between the model defined by the config ObjectIdentifier and the report given by the datas Stream
	 * The created entri(es) may be mapped to VanillaGroups or to user : the wonfig targetType will determine this
	 * If the traget type is Group, only one entry will be created and mappings will
	 * be associated for all the given groups Ids
	 * If it is Usertype, one private entry will be created for all user id, but no mapping
	 * for groups will be created
	 * 
	 * @param conf
	 * @param datas
	 * @throws Exception
	 * 
	 * background : 
	 *  The historic report is the following things : 
	 *   - a row within Vanilla.ged_document with he generated file(the file is located within /vanilla_files/historic)
	 *   - a row within rpy_report_histo with a GED_ID matching to the Vanilla.ged_document.id value per targetId within the Conf obect
	 * 
	 */
	public GedDocument historize(HistorizationConfig conf, InputStream datas) throws Exception;

	/**
	 * Get historic for a report.
	 * If the groupId = -1, the security is ignored.
	 * 
	 * @param identifier
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public List<GedDocument> getReportHistoric(IObjectIdentifier identifier, int groupId) throws Exception;
	
	/**
	 * load the generated report matching to the given gedHistoricDocumentId
	 * @param gedHistoricDocumentId
	 * @return
	 * @throws Exception
	 */
	public InputStream loadHistorizedDocument(Integer gedHistoricDocumentId) throws Exception;
	
	/**
	 * load the generated report matching to the given gedHistoricDocumentId
	 * @param gedHistoricDocumentId
	 * @return
	 * @throws Exception
	 */
	public InputStream loadHistorizedDocument(DocumentVersion gedHistoricDocument) throws Exception;

	/**
	 * remove all Historic entries
	 * @param entries
	 * @throws Exception
	 * 
	 * the removed entries are only removed from the Repository rpy_histo_report table
	 * they are not removed from Vanilla ged_document because they might be used by
	 * GED component for now
	 */
	public void deleteHistoricEntry(List<GedDocument> entries, int repId) throws Exception;
	
	
	/**
	 * Allow access to the historic for the group.
	 * 
	 * Replace the method from "HistoricAdministrator".
	 * 
	 * @param groupId
	 * @param directoryItem
	 * @param repositoryId
	 * @throws Exception
	 */
	public void grantHistoricAccess(int groupId, int directoryItem, int repositoryId) throws Exception;
	
	
	/**
	 * Remove access to the historic for the group
	 * 
	 * @param groupId
	 * @param directoryItem
	 * @param repositoryId
	 * @throws Exception
	 */
	public void removeHistoricAccess(int groupId, int directoryItem, int repositoryId) throws Exception;
	
	/**
	 * Get a list of groupId granted to access the historic of this directoryItem
	 * 
	 * @param directoryItem
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getGroupsAuthorizedByItemId(IObjectIdentifier identifier) throws Exception;
	
	/**
	 * Historize a simple report
	 * @param conf
	 * @param is the file as inputStream
	 * @return the new document id
	 * @throws Exception
	 */
	public Integer historizeReport(HistoricRuntimeConfiguration conf, InputStream is) throws Exception;
	
	public void removeDocumentVersion(DocumentVersion version) throws Exception;
	
	
//	/**
//	 * remove all the historic entries for the given conf OjectIdetifier
//	 * same as above, only the repository database is cleared
//	 * @param conf
//	 * @throws Exception
//	 */
//	public void clearReportHistoric(IRuntimeConfig conf) throws Exception;
}
