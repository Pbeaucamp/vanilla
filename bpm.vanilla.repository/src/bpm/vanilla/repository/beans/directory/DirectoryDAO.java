package bpm.vanilla.repository.beans.directory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;
import bpm.vanilla.repository.beans.InfoConnections;
import bpm.vanilla.repository.beans.JdbcConnectionProvider;

public class DirectoryDAO extends HibernateDaoSupport {
	
	public static final String ID = "DIR_ID";
	public static final String PARENT_ID = "DIR_PARENT_ID";
	public static final String DATE_CREATION = "DIR_DATE_CREATION";
	public static final String DATE_DELETION = "DIR_DATE_DELETION";
	public static final String OWNER_ID = "DIR_OWNER_ID";
	public static final String DELETED_BY = "DIR_DELETED_BY";
	public static final String NAME = "DIR_NAME";
	public static final String COMMENT = "DIR_COMMENT";
	public static final String VISIBLE = "DIR_VISIBLE";
	public static final String SHOWED = "DIR_SHOWED";
	public static final String PERSO = "DIR_PERSO";

	public static final String ITEM_ID = "ITEM_ID";
	public static final String ITEM_DIR_ID = "ITEM_DIR_ID";
	public static final String ITEM_TYPE = "ITEM_TYPE";
	public static final String ITEM_SUBTYPE = "ITEM_SUBTYPE";
	public static final String ITEM_NAME = "ITEM_NAME";
	public static final String ITEM_COMMENT = "ITEM_COMMENT";
	public static final String ITEM_DATE_CREATION = "ITEM_DATE_CREATION";
	public static final String ITEM_DATE_MODIFICATION = "ITEM_DATE_MODIFICATION";
	public static final String ITEM_DATE_DELETION = "ITEM_DATE_DELETION";
	public static final String ITEM_OWNER_ID = "ITEM_OWNER_ID";
	public static final String ITEM_MODIFIED_BY = "ITEM_MODIFIED_BY";
	public static final String ITEM_DELETED_BY = "ITEM_DELETED_BY";
	public static final String ITEM_LOCK_ID = "ITEM_LOCK_ID";
	public static final String ITEM_FORMATTING_VARIABLE_ID = "ITEM_FORMATTING_VARIABLE_ID";
	public static final String ITEM_DISPLAY = "ITEM_DISPLAY";
	public static final String ITEM_VISIBLE = "ITEM_VISIBLE";
	public static final String ITEM_CREATE_ENTRY = "ITEM_CREATE_ENTRY";
	public static final String ITEM_ON = "ITEM_ON";
	public static final String ITEM_AVAILABLE_GED = "ITEM_AVAILABLE_GED";
	public static final String ITEM_REALTIME_GED = "ITEM_REALTIME_GED";
	public static final String ITEM_ANDROID_SUPPORTED = "ITEM_ANDROID_SUPPORTED";
	public static final String ITEM_INTERNAL_VERSION = "ITEM_INTERNAL_VERSION";
	public static final String ITEM_PUBLIC_VERSION = "ITEM_PUBLIC_VERSION";
	public static final String ITEM_MODEL_TYPE = "ITEM_MODEL_TYPE";
	public static final String ITEM_RUNTIME_URL = "ITEM_RUNTIME_URL";
	public static final String ITEM_DEFAULT_FORMAT = "ITEM_DEFAULT_FORMAT";
	public static final String ITEM_NB_MAX_HISTO = "ITEM_NB_MAX_HISTO";
	
	@SuppressWarnings("unchecked")
	public Collection<RepositoryDirectory> findAll() {
		return getHibernateTemplate().find("from Directory");
	}

	@SuppressWarnings("unchecked")
	public List<RepositoryDirectory> findAllDelete() {
		return (List<RepositoryDirectory>) getHibernateTemplate().find("from RepositoryDirectory where visible = false");
	}
	
	public List<RepositoryDirectory> findByPrimaryKey(int repositoryId, int key) throws Exception {
		String query = "select d.id as " + RepositoryDirectory.ID 
			+ ", d.parent_id as " + RepositoryDirectory.PARENT_ID 
			+ ", d.creation_date as " + RepositoryDirectory.DATE_CREATION 
			+ ", d.delete_date as " + RepositoryDirectory.DATE_DELETION 
			+ ", d.owner_id as " + RepositoryDirectory.OWNER_ID 
			+ ", d.deleted_by as " + RepositoryDirectory.DELETED_BY 
			+ ", d.directory_name as " + RepositoryDirectory.NAME 
			+ ", d.directory_comment as " + RepositoryDirectory.COMMENT 
			+ ", d.is_visible as " + RepositoryDirectory.VISIBLE 
			+ ", d.is_showed as " + RepositoryDirectory.SHOWED 
			+ ", d.is_perso as " + RepositoryDirectory.PERSO 
			+ " from rpy_directory d where id=" + key;
		
		InfoConnections info = JdbcConnectionProvider.getInstance().getInfoConnection(repositoryId);
		
		VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(info.getRepositoryDBUrl(), info.getLogin(), info.getPassword(), info.getDriver());
		
		VanillaPreparedStatement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		List<RepositoryDirectory> dirs = myConverterDirectory(resultSet);
		resultSet.close();
		statement.close();
		ConnectionManager.getInstance().returnJdbcConnection(connection);

		return dirs;
	}

	public int save(RepositoryDirectory d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(RepositoryDirectory d) {
		getHibernateTemplate().update(d);
	}

	public void purge(RepositoryDirectory d) {
		getHibernateTemplate().delete(d);
	}

	public void restore(RepositoryDirectory d) {
		getHibernateTemplate().update(d);
	}

	public void update(RepositoryDirectory d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public List<RepositoryDirectory> findByParentId(int repositoryId, int parentId, int userId, boolean isSuperUser) throws Exception {
		if (parentId != 0) {
			List<RepositoryDirectory> dirParent = findByPrimaryKey(repositoryId, parentId);
			if (!isSuperUser && dirParent != null && !dirParent.isEmpty() && (dirParent.get(0).isPerso() || dirParent.get(0).getId() == 1)) {
				return (List<RepositoryDirectory>) getHibernateTemplate().find("from RepositoryDirectory where visible = true AND parentId=" + parentId + " AND ownerId = " + userId);
			}
		}

		return (List<RepositoryDirectory>) getHibernateTemplate().find("from RepositoryDirectory where visible = true AND parentId=" + parentId);
	}
	
	public List<RepositoryDirectory> getChildDirectories(int repositoryId, int groupId, int repositoryDirectoryId, int userId, boolean isSuperUser) throws Exception {

		String query = "";
		
		if (groupId > 0) {

			List<RepositoryDirectory> dirParent = findByPrimaryKey(repositoryId, repositoryDirectoryId);
			if (!isSuperUser && dirParent != null && !dirParent.isEmpty() && (dirParent.get(0).isPerso() || dirParent.get(0).getId() == 1)) {
				
				query = "select d.id as " + RepositoryDirectory.ID 
					+ ", d.parent_id as " + RepositoryDirectory.PARENT_ID 
					+ ", d.creation_date as " + RepositoryDirectory.DATE_CREATION 
					+ ", d.delete_date as " + RepositoryDirectory.DATE_DELETION 
					+ ", d.owner_id as " + RepositoryDirectory.OWNER_ID 
					+ ", d.deleted_by as " + RepositoryDirectory.DELETED_BY 
					+ ", d.directory_name as " + RepositoryDirectory.NAME 
					+ ", d.directory_comment as " + RepositoryDirectory.COMMENT 
					+ ", d.is_visible as " + RepositoryDirectory.VISIBLE 
					+ ", d.is_showed as " + RepositoryDirectory.SHOWED 
					+ ", d.is_perso as " + RepositoryDirectory.PERSO 
					+ " from rpy_directory d, sec_directory_secured secDir where d.parent_id=" + repositoryDirectoryId 
					+ " and secDir.directory_id=d.id and secDir.group_id=" + groupId + " and d.is_visible = true and d.owner_id = " + userId + "  order by d.directory_name";

			}
			else {
				query = "select d.id as " + RepositoryDirectory.ID 
					+ ", d.parent_id as " + RepositoryDirectory.PARENT_ID 
					+ ", d.creation_date as " + RepositoryDirectory.DATE_CREATION 
					+ ", d.delete_date as " + RepositoryDirectory.DATE_DELETION 
					+ ", d.owner_id as " + RepositoryDirectory.OWNER_ID 
					+ ", d.deleted_by as " + RepositoryDirectory.DELETED_BY 
					+ ", d.directory_name as " + RepositoryDirectory.NAME 
					+ ", d.directory_comment as " + RepositoryDirectory.COMMENT 
					+ ", d.is_visible as " + RepositoryDirectory.VISIBLE 
					+ ", d.is_showed as " + RepositoryDirectory.SHOWED 
					+ ", d.is_perso as " + RepositoryDirectory.PERSO 
					+ " from rpy_directory d, sec_directory_secured secDir where d.parent_id=" + repositoryDirectoryId 
					+ " and secDir.directory_id=d.id and secDir.group_id=" + groupId + " and d.is_visible = true  order by d.directory_name";
				
			}
		}
		else {
			
			String ownerId = "";
			List<RepositoryDirectory> dirParent = findByPrimaryKey(repositoryId, repositoryDirectoryId);
			if (!isSuperUser && dirParent != null && !dirParent.isEmpty() && (dirParent.get(0).isPerso() || dirParent.get(0).getId() == 1)) {
				ownerId = "and d.owner_id=" + userId;
			}

			query = "select d.id as " + RepositoryDirectory.ID 
				+ ", d.parent_id as " + RepositoryDirectory.PARENT_ID 
				+ ", d.creation_date as " + RepositoryDirectory.DATE_CREATION 
				+ ", d.delete_date as " + RepositoryDirectory.DATE_DELETION 
				+ ", d.owner_id as " + RepositoryDirectory.OWNER_ID 
				+ ", d.deleted_by as " + RepositoryDirectory.DELETED_BY 
				+ ", d.directory_name as " + RepositoryDirectory.NAME 
				+ ", d.directory_comment as " + RepositoryDirectory.COMMENT 
				+ ", d.is_visible as " + RepositoryDirectory.VISIBLE 
				+ ", d.is_showed as " + RepositoryDirectory.SHOWED 
				+ ", d.is_perso as " + RepositoryDirectory.PERSO 
				+ " from rpy_directory d where d.parent_id=" + repositoryDirectoryId 
				+ " and d.is_visible='1' " + ownerId + " order by d.directory_name";
		}
		
		InfoConnections info = JdbcConnectionProvider.getInstance().getInfoConnection(repositoryId);
		
		VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(info.getRepositoryDBUrl(), info.getLogin(), info.getPassword(), info.getDriver());
		
		VanillaPreparedStatement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		List<RepositoryDirectory> dirs = myConverterDirectory(resultSet);
		resultSet.close();
		statement.close();
		ConnectionManager.getInstance().returnJdbcConnection(connection);

		return dirs;
	}

	private List<RepositoryDirectory> myConverterDirectory(ResultSet rs) throws Exception {
		List<RepositoryDirectory> dirs = new ArrayList<RepositoryDirectory>();
		while (rs.next()) {
			int id = rs.getInt(RepositoryDirectory.ID);
			int parentId = rs.getInt(RepositoryDirectory.PARENT_ID);
			Date dateCreation = rs.getDate(RepositoryDirectory.DATE_CREATION);
			Date dateDeletion = rs.getDate(RepositoryDirectory.DATE_DELETION);
			int ownerId = rs.getInt(RepositoryDirectory.OWNER_ID);
			int deletedBy = rs.getInt(RepositoryDirectory.DELETED_BY);
			String name = rs.getString(RepositoryDirectory.NAME);
			String comment = rs.getString(RepositoryDirectory.COMMENT);
			boolean visible = rs.getBoolean(RepositoryDirectory.VISIBLE);
			boolean showed = rs.getBoolean(RepositoryDirectory.SHOWED);
			boolean perso = rs.getBoolean(RepositoryDirectory.PERSO);
			dirs.add(new RepositoryDirectory(id, parentId, dateCreation, dateDeletion, ownerId, deletedBy, name, comment, visible, showed, perso));
		}
		return dirs;
	}
	
	public List<RepositoryItem> getDirectoryContent(int repositoryId, int groupId, int directoryId) throws Exception {

		String query = "";
		
		List<RepositoryItem> items = new ArrayList<RepositoryItem>();
		if (groupId > 0) {
			
			query = "select item.id as " + RepositoryItem.ID 
			+ ", item.directory_id as " + RepositoryItem.DIR_ID 
			+ ", item.type as " + RepositoryItem.TYPE 
			+ ", item.subtype as " + RepositoryItem.SUBTYPE 
			+ ", item.item_name as " + RepositoryItem.NAME 
			+ ", item.item_comment as " + RepositoryItem.COMMENT 
			+ ", item.date_creation as " + RepositoryItem.DATE_CREATION 
			+ ", item.date_modification as " + RepositoryItem.DATE_MODIFICATION 
			+ ", item.date_deletion as " + RepositoryItem.DATE_DELETION 
			+ ", item.owner_id as " + RepositoryItem.OWNER_ID 
			+ ", item.modified_by as " + RepositoryItem.MODIFIED_BY 
			+ ", item.deleted_by as " + RepositoryItem.DELETED_BY 
			+ ", item.lock_id as " + RepositoryItem.LOCK_ID 
			+ ", item.formatting_variable_id as " + RepositoryItem.FORMATTING_VARIABLE_ID 
			+ ", item.is_display as " + RepositoryItem.DISPLAY 
			+ ", item.is_visible as " + RepositoryItem.VISIBLE 
			+ ", item.create_entry as " + RepositoryItem.CREATE_ENTRY 
			+ ", item.is_on as " + RepositoryItem.ON 
			+ ", item.is_available_ged as " + RepositoryItem.AVAILABLE_GED 
			+ ", item.is_realtime_ged as " + RepositoryItem.REALTIME_GED 
			+ ", item.is_android_supported as " + RepositoryItem.ANDROID_SUPPORTED 
			+ ", item.internal_version as " + RepositoryItem.INTERNAL_VERSION 
			+ ", item.public_version as " + RepositoryItem.PUBLIC_VERSION 
			+ ", item.model_type as " + RepositoryItem.MODEL_TYPE 
			+ ", item.runtime_url as " + RepositoryItem.RUNTIME_URL 
			+ ", item.default_format as " + RepositoryItem.DEFAULT_FORMAT 
			+ ", item.nb_max_histo as " + RepositoryItem.NB_MAX_HISTO 
			+ ", item.is_available_odata as " + RepositoryItem.AVAILABLE_ODATA
			+ " from rpy_repository_item item, sec_directory_secured secDir, rpy_directory directory, sec_object_secured secItem" 
			+ " where item.directory_id = " + directoryId 
			+ " and secItem.group_id = " + groupId 
			+ " and secItem.group_id = secDir.group_id" 
			+ " and secItem.item_id=item.id" 
			+ " and item.is_visible = true" 
			+ " and secDir.directory_id=item.directory_id" 
			+ " and directory.id=item.directory_id" 
			+ " and directory.is_visible = true" 
			+ " order by item.item_name";
		}
		else {
			
			query = "select item.id as " + RepositoryItem.ID 
			+ ", item.directory_id as " + RepositoryItem.DIR_ID 
			+ ", item.type as " + RepositoryItem.TYPE 
			+ ", item.subtype as " + RepositoryItem.SUBTYPE 
			+ ", item.item_name as " + RepositoryItem.NAME 
			+ ", item.item_comment as " + RepositoryItem.COMMENT 
			+ ", item.date_creation as " + RepositoryItem.DATE_CREATION 
			+ ", item.date_modification as " + RepositoryItem.DATE_MODIFICATION 
			+ ", item.date_deletion as " + RepositoryItem.DATE_DELETION 
			+ ", item.owner_id as " + RepositoryItem.OWNER_ID 
			+ ", item.modified_by as " + RepositoryItem.MODIFIED_BY 
			+ ", item.deleted_by as " + RepositoryItem.DELETED_BY 
			+ ", item.lock_id as " + RepositoryItem.LOCK_ID 
			+ ", item.formatting_variable_id as " + RepositoryItem.FORMATTING_VARIABLE_ID 
			+ ", item.is_display as " + RepositoryItem.DISPLAY 
			+ ", item.is_visible as " + RepositoryItem.VISIBLE 
			+ ", item.create_entry as " + RepositoryItem.CREATE_ENTRY 
			+ ", item.is_on as " + RepositoryItem.ON 
			+ ", item.is_available_ged as " + RepositoryItem.AVAILABLE_GED 
			+ ", item.is_realtime_ged as " + RepositoryItem.REALTIME_GED 
			+ ", item.is_android_supported as " + RepositoryItem.ANDROID_SUPPORTED 
			+ ", item.internal_version as " + RepositoryItem.INTERNAL_VERSION 
			+ ", item.public_version as " + RepositoryItem.PUBLIC_VERSION 
			+ ", item.model_type as " + RepositoryItem.MODEL_TYPE 
			+ ", item.runtime_url as " + RepositoryItem.RUNTIME_URL 
			+ ", item.default_format as " + RepositoryItem.DEFAULT_FORMAT 
			+ ", item.nb_max_histo as " + RepositoryItem.NB_MAX_HISTO 
			+ ", item.is_available_odata as " + RepositoryItem.AVAILABLE_ODATA
			+ " from rpy_repository_item item, rpy_directory directory where item.directory_id=" + directoryId 
						+ " and item.is_visible=true and directory.id=item.directory_id and directory.is_visible=true order by item.item_name";
		}

		InfoConnections info = JdbcConnectionProvider.getInstance().getInfoConnection(repositoryId);
		
		VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(info.getRepositoryDBUrl(), info.getLogin(), info.getPassword(), info.getDriver());
		
		VanillaPreparedStatement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(query);
		items = myConverter(resultSet);
		resultSet.close();
		statement.close();
		ConnectionManager.getInstance().returnJdbcConnection(connection);

		return items;

	}

	private List<RepositoryItem> myConverter(ResultSet rs) throws Exception {
		List<RepositoryItem> items = new ArrayList<RepositoryItem>();
		while (rs.next()) {
			int id = rs.getInt(RepositoryItem.ID);
			int directoryId = rs.getInt(RepositoryItem.DIR_ID);
			int type = rs.getInt(RepositoryItem.TYPE);
			int subtype = rs.getInt(RepositoryItem.SUBTYPE);
			String itemName = rs.getString(RepositoryItem.NAME);
			String comment = rs.getString(RepositoryItem.COMMENT);
			Date dateCreation = rs.getDate(RepositoryItem.DATE_CREATION);
			Date dateModification = rs.getDate(RepositoryItem.DATE_MODIFICATION);
			Date dateDeletion = rs.getDate(RepositoryItem.DATE_DELETION);
			int ownerId = rs.getInt(RepositoryItem.OWNER_ID);
			int modifiedBy = rs.getInt(RepositoryItem.MODIFIED_BY);
			int deletedBy = rs.getInt(RepositoryItem.DELETED_BY);
			int lockId = rs.getInt(RepositoryItem.LOCK_ID);
			int formattingVariableId = rs.getInt(RepositoryItem.FORMATTING_VARIABLE_ID);
			boolean display = rs.getBoolean(RepositoryItem.DISPLAY);
			boolean visible = rs.getBoolean(RepositoryItem.VISIBLE);
			boolean createEntry = rs.getBoolean(RepositoryItem.CREATE_ENTRY);
			boolean on = rs.getBoolean(RepositoryItem.ON);
			boolean availableGed = rs.getBoolean(RepositoryItem.AVAILABLE_GED);
			boolean realtimeGed = rs.getBoolean(RepositoryItem.REALTIME_GED);
			boolean androidSupported = rs.getBoolean(RepositoryItem.ANDROID_SUPPORTED);
			String internalVersion = rs.getString(RepositoryItem.INTERNAL_VERSION);
			String publicVersion = rs.getString(RepositoryItem.PUBLIC_VERSION);
			String modelType = rs.getString(RepositoryItem.MODEL_TYPE);
			String runtimeUrl = rs.getString(RepositoryItem.RUNTIME_URL);
			String defaultFormat = rs.getString(RepositoryItem.DEFAULT_FORMAT);
			int nbMaxHisto = rs.getInt(RepositoryItem.NB_MAX_HISTO);
			boolean availableOdata = rs.getBoolean(RepositoryItem.AVAILABLE_ODATA);
			items.add(new RepositoryItem(id, directoryId, type, subtype, itemName, comment, dateCreation, dateModification, dateDeletion, ownerId, modifiedBy, deletedBy, lockId, formattingVariableId, display, visible, createEntry, on, availableGed, realtimeGed, androidSupported, internalVersion, publicVersion, modelType, runtimeUrl, defaultFormat, nbMaxHisto, availableOdata));
		}
		return items;
	}

	@SuppressWarnings("unchecked")
	public RepositoryDirectory getDirectory(int id) {
		List<RepositoryDirectory> l = (List<RepositoryDirectory>) getHibernateTemplate().find("from RepositoryDirectory where id = " + id + " and visible=true");
		if (l.isEmpty()) {
			return null;
		}
		return (RepositoryDirectory) l.get(0);
	}

	@SuppressWarnings("unchecked")
	public RepositoryDirectory getDirectoryDelete(int id) {
		List<RepositoryDirectory> l = (List<RepositoryDirectory>) getHibernateTemplate().find("from RepositoryDirectory where id = " + id);
		if (l.isEmpty()) {
			return null;
		}
		return (RepositoryDirectory) l.get(0);
	}

}
