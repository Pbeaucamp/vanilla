package bpm.vanilla.platform.core.repository.services;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.repository.ReportBackground;
import bpm.vanilla.platform.core.xstream.IXmlActionType;



public interface IRepositoryReportHistoricService {
	public static enum ActionType implements IXmlActionType{
		REMOVE_ACCESS(Level.INFO), LIST_DOCS_4_GROUP(Level.DEBUG), LIST_DOCS_4_ITEM_GROUP(Level.DEBUG), LIST_DOCS_4_ITEM(Level.DEBUG), 
		LIST_GRANTED_GROUPS_ID(Level.DEBUG), DEL_HISTO_ENTRY(Level.INFO), CREATE_ACCESS(Level.INFO),
		ADD_OR_UPDATE_REPORT_BACKGROUND(Level.INFO), DEL_REPORT_BACKGROUND(Level.INFO), GET_REPORT_BACKGROUNDS(Level.DEBUG), GET_REPORT_BACKGROUND(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	public void createHistoricAccess(int directoryItemId, int groupId) throws Exception;
	
	/**
	 * 
	 * @param directoryItemId
	 * @return the DocumentIds for the generated report on the gievn directoryItemId
	 * this method should only be called for internal purpose
	 * or by an administration module because the Garnt on historic are
	 * bypassed 
	 */
	public List<Integer> getHistorizedDocumentIdFor(int directoryItemId) throws Exception;

	/**
	 * 
	 * @param directoryItemId
	 * @return the DocumentIds for the generated report on the gievn directoryItemId
	 * This THE method to call to have the docId visible for specific GroupId context
	 * 
	 * It will return the dociD for the givent Group and the ones that were generated
	 * only for a specific user
	 */
	public List<Integer> getHistorizedDocumentIdFor(int directoryItemId, int groupId) throws Exception;

	public List<Integer> removeHistoricAccess(int directoryItemId, int groupId) throws Exception;


	public List<Integer> getAuthorizedGroupId(int directoryItemId) throws Exception;


	public void deleteHistoricEntry(int gedDocumentEntryId) throws Exception;

	public List<Integer> getHistorizedDocumentIdForGroup(Integer groupId) throws Exception;
	
	public void addOrUpdateReportBackground(ReportBackground report) throws Exception;
	public void deleteReportBackground(ReportBackground report) throws Exception;
	public List<ReportBackground> getReportBackgrounds(int userId, int limit) throws Exception;
	public ReportBackground getReportBackground(int reportBackgroundId) throws Exception;
}
