package bpm.document.management.remote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;

import bpm.document.management.core.IVdmManager;
import bpm.document.management.core.model.*;
import bpm.document.management.core.model.Comments.CommentStatus;
import bpm.document.management.core.model.Enterprise.TypeUser;
import bpm.document.management.core.model.Form.FormType;
import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.document.management.core.model.ItemValidation.ItemType;
import bpm.document.management.core.model.Log.LogType;
import bpm.document.management.core.model.MetadataLink.LinkType;
import bpm.document.management.core.model.OrganigramElementSecurity.Fonction;
import bpm.document.management.core.model.Permission.ShareType;
import bpm.document.management.core.model.aklad.AkladSettings;
import bpm.document.management.core.utils.IOWriter;
import bpm.document.management.core.utils.MailConfig;
import bpm.document.management.core.utils.TreatmentImageObject;
import bpm.document.management.core.xstream.XmlAction;
import bpm.document.management.core.xstream.XmlArgumentsHolder;
import bpm.document.management.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.xstream.DateConverter;

import com.thoughtworks.xstream.XStream;

public class RemoteVdmManager implements IVdmManager {

	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private XStream xstream;

	public RemoteVdmManager(VdmContext ctx) {
		httpCommunicator.init(ctx.getVdmUrl(), ctx.getMail(), ctx.getPassword(), null);
		xstream = new XStream();
		xstream.registerConverter(new DateConverter());
	}

	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public Integer manageItem(Serializable item, ManagerAction action) throws Exception {
		XmlAction op = new XmlAction(createArguments(item, action), IVdmManager.ActionType.MANAGE_ITEM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (Integer) xstream.fromXML(xml) : null;
	}

	@Override
	public User connect(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IVdmManager.ActionType.CONNECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (User) xstream.fromXML(xml);
	}

	@Override
	public Boolean createUser(User user, String verifyUrl, boolean fromAndroid, String licenceFile, UserRole userRole, UserConnectionProperty userConnectionProperty) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, verifyUrl, fromAndroid, licenceFile, userRole, userConnectionProperty), IVdmManager.ActionType.CREATE_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public void addGroup(Group group, List<User> users) throws Exception {
		XmlAction op = new XmlAction(createArguments(group, users), IVdmManager.ActionType.ADD_GROUP);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Group> getGroups() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Group>) xstream.fromXML(xml);
	}

	@Override
	public List<Group> getGroupsByUser(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_GROUP_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Group>) xstream.fromXML(xml);
	}

	@Override
	public void deleteGroup(Group group) throws Exception {
		XmlAction op = new XmlAction(createArguments(group), IVdmManager.ActionType.DELETE_GROUP);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateGroup(Group group, List<User> users) throws Exception {
		XmlAction op = new XmlAction(createArguments(group, users), IVdmManager.ActionType.UPDATE_GROUP);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public User updateUser(User user, String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, email), IVdmManager.ActionType.UPDATE_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (User) xstream.fromXML(xml);
	}

	@Override
	public Boolean deleteUser(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IVdmManager.ActionType.DELETE_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public Boolean resetPassword(User user, String url) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, url), IVdmManager.ActionType.RESET_PASSWORD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public List<UserLogs> getLogs(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IVdmManager.ActionType.GET_LOGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<UserLogs>) xstream.fromXML(xml);
	}

	@Override
	public Tree deleteDirectory(Tree tree, String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(tree, email), IVdmManager.ActionType.DELETE_DIRECTORY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		if (xml.isEmpty()) {
			return null;
		}
		return (Tree) xstream.fromXML(xml);
	}

	@Override
	public Tree updateDirectory(Tree tree) throws Exception {
		XmlAction op = new XmlAction(createArguments(tree), IVdmManager.ActionType.UPDATE_DIRECTORY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Tree) xstream.fromXML(xml);
	}

	@Override
	public Locale addLocale(Locale locale) throws Exception {
		XmlAction op = new XmlAction(createArguments(locale), IVdmManager.ActionType.ADD_LOCALE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Locale) xstream.fromXML(xml);
	}

	@Override
	public Locale deleteLocale(Locale locale) throws Exception {
		XmlAction op = new XmlAction(createArguments(locale), IVdmManager.ActionType.DELETE_LOCALE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Locale) xstream.fromXML(xml);
	}

	@Override
	public Locale updateLocale(Locale locale) throws Exception {
		XmlAction op = new XmlAction(createArguments(locale), IVdmManager.ActionType.UPDATE_LOCALE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Locale) xstream.fromXML(xml);
	}

	@Override
	public List<Roles> getRoles() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ROLES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Roles>) xstream.fromXML(xml);
	}

	@Override
	public Roles addRole(Roles role) throws Exception {
		XmlAction op = new XmlAction(createArguments(role), IVdmManager.ActionType.ADD_ROLE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Roles) xstream.fromXML(xml);
	}

	@Override
	public Roles updateRole(Roles role) throws Exception {
		XmlAction op = new XmlAction(createArguments(role), IVdmManager.ActionType.UPDATE_ROLE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Roles) xstream.fromXML(xml);
	}

	@Override
	public Boolean deleteRole(Roles role) throws Exception {
		XmlAction op = new XmlAction(createArguments(role), IVdmManager.ActionType.DELETE_ROLE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public List<Country> getCountry() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_COUNTRY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Country>) xstream.fromXML(xml);
	}

	@Override
	public User validateUser(String verifierCode) throws Exception {
		XmlAction op = new XmlAction(createArguments(verifierCode), IVdmManager.ActionType.VALIDATE_EMAIL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (User) xstream.fromXML(xml);
	}

	@Override
	public void updateValidated(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IVdmManager.ActionType.UPDATE_VALIDATED);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public User getUserInfo(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (User) xstream.fromXML(xml);
	}

	@Override
	public List<City> getCities(Country country) throws Exception {
		XmlAction op = new XmlAction(createArguments(country), IVdmManager.ActionType.GET_CITIES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<City>) xstream.fromXML(xml);
	}

	@Override
	public List<User> getOtherUsers(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_OTHERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<User>) xstream.fromXML(xml);
	}

	@Override
	public Tree saveDirectory(Tree tree) throws Exception {
		XmlAction op = new XmlAction(createArguments(tree), IVdmManager.ActionType.SAVE_DIRECTORY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Tree) xstream.fromXML(xml);
	}

	@Override
	public Tree getDirectoryInfo(Tree tree) throws Exception {
		XmlAction op = new XmlAction(createArguments(tree), IVdmManager.ActionType.GET_DIRECTORY_INFO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Tree) xstream.fromXML(xml);
	}

	@Override
	public List<Documents> saveDocuments(List<Documents> documents) throws Exception {
		XmlAction op = new XmlAction(createArguments(documents), IVdmManager.ActionType.SAVE_DOCUMENTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public Downloads downloadsFolders(Downloads downloads) throws Exception {
		XmlAction op = new XmlAction(createArguments(downloads), IVdmManager.ActionType.DOWNLOADS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Downloads) xstream.fromXML(xml);
	}

	@Override
	public List<Tree> getSubdirectories(Tree tree) throws Exception {
		XmlAction op = new XmlAction(createArguments(tree), IVdmManager.ActionType.GET_SUBDIRECTORIES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Tree>) xstream.fromXML(xml);
	}

	@Override
	public List<IObject> getAllSubIObject(int folderId, PermissionItem permissionParent) throws Exception {
		XmlAction op = new XmlAction(createArguments(folderId, permissionParent), IVdmManager.ActionType.GET_SUB_IOBJECTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<IObject>) xstream.fromXML(xml);
	}

	@Override
	public void savePages(List<DocPages> pages) throws Exception {
		XmlAction op = new XmlAction(createArguments(pages), IVdmManager.ActionType.SAVE_PAGES);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<DocPages> getPages(Versions docVersion) throws Exception {
		XmlAction op = new XmlAction(createArguments(docVersion), IVdmManager.ActionType.GET_PAGES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DocPages>) xstream.fromXML(xml);
	}

	@Override
	public List<Documents> getSubDocs(Tree folder) throws Exception {
		XmlAction op = new XmlAction(createArguments(folder), IVdmManager.ActionType.GET_SUB_DOCS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public Documents getDocInfo(int docId) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId), IVdmManager.ActionType.GET_DOC_INFO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public void indexDocument(Documents doc, InputStream fileInputStream) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc, encodeInputStream(fileInputStream)), IVdmManager.ActionType.INDEX_DOCUMENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));

	}

	private byte[] encodeInputStream(InputStream inputStream) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(inputStream, bos, true, true);

		return Base64.encodeBase64(bos.toByteArray());
	}

	@Override
	public List<Documents> searchIndexedDocuments(String searchString) throws Exception {
		XmlAction op = new XmlAction(createArguments(searchString), IVdmManager.ActionType.SEARCH_INDEXED_DOCUMENTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public void deleteDocument(Documents doc, String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc, email), IVdmManager.ActionType.DELETE_ITEM);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteDocumentFromTrash(Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc), IVdmManager.ActionType.DELETE_ITEM_FROM_TRASH);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void restoreDocument(Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc), IVdmManager.ActionType.RESTORE_ITEM);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Tree> getDirectoriesPerUser(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_DIRS_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Tree>) xstream.fromXML(xml);
	}

	@Override
	public void deleteForeverFolder(Tree folder) throws Exception {
		XmlAction op = new XmlAction(createArguments(folder), IVdmManager.ActionType.DELETE_FOREVER_FOLDER);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void restoreFolder(Tree folder) throws Exception {
		XmlAction op = new XmlAction(createArguments(folder), IVdmManager.ActionType.RESTORE_FOLDER);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveFav(Favorites fav) throws Exception {
		XmlAction op = new XmlAction(createArguments(fav), IVdmManager.ActionType.SAVE_FAV);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<IObject> getFav(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_FAV);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<IObject>) xstream.fromXML(xml);
	}

	@Override
	public Documents addCode(Documents documents) throws Exception {
		XmlAction op = new XmlAction(createArguments(documents), IVdmManager.ActionType.ADD_DOC_CODE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public Documents getCode(String uniqueCode) throws Exception {
		XmlAction op = new XmlAction(createArguments(uniqueCode), IVdmManager.ActionType.GET_DOC_CODE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public List<Documents> getAllDocuments(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_ALL_DOCUMENTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public List<Currency> getCurrencies() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_CURRENCY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Currency>) xstream.fromXML(xml);
	}

	@Override
	public List<MemoryStorage> getMemoryStorage() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_STORAGE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MemoryStorage>) xstream.fromXML(xml);
	}

	@Override
	public List<UserStorage> getUserStorage() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_STORAGE_USERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<UserStorage>) xstream.fromXML(xml);
	}

	@Override
	public List<Documents> getDocList() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_LIST_DOCS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public List<IObject> getUserShare(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_USER_SHARE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<IObject>) xstream.fromXML(xml);
	}

	@Override
	public List<Group> getGroupsForUser(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_GROUPS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Group>) xstream.fromXML(xml);
	}

	@Override
	public Versions saveDocVersion(Versions docVersion) throws Exception {
		XmlAction op = new XmlAction(createArguments(docVersion), IVdmManager.ActionType.SAVE_DOC_VERSION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Versions) xstream.fromXML(xml);
	}

	@Override
	public void removeFav(Favorites fav) throws Exception {
		XmlAction op = new XmlAction(createArguments(fav), IVdmManager.ActionType.REMOVE_FAV);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Favorites getFavInfo(IObject iobject) throws Exception {
		XmlAction op = new XmlAction(createArguments(iobject), IVdmManager.ActionType.GET_FAV_INFO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (Favorites) xstream.fromXML(xml) : null;
	}

	@Override
	public List<Versions> getVersions(Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc), IVdmManager.ActionType.GET_VERSIONS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Versions>) xstream.fromXML(xml);
	}

	@Override
	public User getUserInfoThroughId(String userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_USER_INFO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (User) xstream.fromXML(xml);
	}

	@Override
	public Country getCountryById(int countryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(countryId), IVdmManager.ActionType.GET_COUNTRY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Country) xstream.fromXML(xml);
	}

	@Override
	public City getCityById(int cityId) throws Exception {
		XmlAction op = new XmlAction(createArguments(cityId), IVdmManager.ActionType.GET_CITY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (City) xstream.fromXML(xml);
	}

	@Override
	public Versions getSpecificVersion(Versions version) throws Exception {
		XmlAction op = new XmlAction(createArguments(version), IVdmManager.ActionType.GET_SPECIFIC_VERSION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Versions) xstream.fromXML(xml);
	}

	@Override
	public Tree getDirById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_DIR_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Tree) xstream.fromXML(xml);
	}

	@Override
	public Documents getDocById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_DOC_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public void deleteAllFromTrash(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.DELETE_ALL);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveComments(Comments comment) throws Exception {
		XmlAction op = new XmlAction(createArguments(comment), IVdmManager.ActionType.SAVE_COMMENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void removeComments(Comments comment) throws Exception {
		XmlAction op = new XmlAction(createArguments(comment), IVdmManager.ActionType.REMOVE_COMMENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Comments> getComments(int docId, CommentStatus status) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId, status), IVdmManager.ActionType.GET_COMMENTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Comments>) xstream.fromXML(xml);
	}

	@Override
	public Comments getSpecificCommment(int commentId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateComment(Comments comment) throws Exception {
		XmlAction op = new XmlAction(createArguments(comment), IVdmManager.ActionType.UPDATE_COMMENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveDefaultMemory(DocumentMemoryUsage memory) throws Exception {
		XmlAction op = new XmlAction(createArguments(memory), IVdmManager.ActionType.SAVE_DEFAULT_MEMORY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateMemory(DocumentMemoryUsage memory) throws Exception {
		XmlAction op = new XmlAction(createArguments(memory), IVdmManager.ActionType.UPDATE_MEMORY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public DocumentMemoryUsage getMemoryUsage(String user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IVdmManager.ActionType.GET_MEMORY_USAGE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (DocumentMemoryUsage) xstream.fromXML(xml);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void saveRate(Rate rate) throws Exception {
		XmlAction op = new XmlAction(createArguments(rate), IVdmManager.ActionType.SAVE_RATE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateRate(Rate rate) throws Exception {
		XmlAction op = new XmlAction(createArguments(rate), IVdmManager.ActionType.UPDATE_RATE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Rate getRate(String user, int docId) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, docId), IVdmManager.ActionType.GET_RATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Rate) xstream.fromXML(xml);
	}

	@Override
	public List<Rate> getRateByUser(String user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, user), IVdmManager.ActionType.GET_RATES_BY_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Rate>) xstream.fromXML(xml);
	}

	@Override
	public List<Notifications> getNotifications(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_NOTIFICATIONS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Notifications>) xstream.fromXML(xml);
	}

	@Override
	public List<Notifications> markAsRead(Notifications notification) throws Exception {
		XmlAction op = new XmlAction(createArguments(notification), IVdmManager.ActionType.MARK_AS_READ);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Notifications>) xstream.fromXML(xml);
	}

	@Override
	public List<Notifications> markAsUnread(Notifications notification) throws Exception {
		XmlAction op = new XmlAction(createArguments(notification), IVdmManager.ActionType.MARK_AS_UNREAD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Notifications>) xstream.fromXML(xml);
	}

	@Override
	public void saveNotification(Notifications notification) throws Exception {
		XmlAction op = new XmlAction(createArguments(notification), IVdmManager.ActionType.SAVE_NOTIFICATION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void changeDocumentParent(int itemId, IObject.ItemType type, int parentId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, type, parentId), IVdmManager.ActionType.UPDATE_DOC_PARENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Notifications> getAllNotifs(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_ALL_NOTIFS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Notifications>) xstream.fromXML(xml);
	}

	@Override
	public List<User> getAllUsers() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_USERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<User>) xstream.fromXML(xml);
	}

	@Override
	public List<User> allUsers(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.ALL_USERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<User>) xstream.fromXML(xml);
	}

	@Override
	public WorkFlow saveWorkFlow(WorkFlow workFlow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workFlow), IVdmManager.ActionType.SAVE_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (WorkFlow) xstream.fromXML(xml);
	}

	@Override
	public void updateWorkFlow(WorkFlow workFlow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workFlow), IVdmManager.ActionType.UPDATE_WORKFLOW);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveFolderWorkFlow(WorkFlowFolderCondition folderCondition) throws Exception {
		XmlAction op = new XmlAction(createArguments(folderCondition), IVdmManager.ActionType.SAVE_FOLDER_WORKFLOW);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<WorkFlow> getWorkFlow(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<WorkFlow>) xstream.fromXML(xml);
	}

	@Override
	public WorkFlow getSpecificWorkFlowEachUser(int userId, int docId, boolean isDocument) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId, docId, isDocument), IVdmManager.ActionType.GET_SPECIFIC_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (WorkFlow) xstream.fromXML(xml);
	}

//	@Override
//	public WorkFlow getSpecificWorkFlow(int docId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(docId), IVdmManager.ActionType.GET_SPECIFIC_WORKFLOW_BY_DOCID);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (WorkFlow) xstream.fromXML(xml);
//	}

	@Override
	public WorkFlowFolderCondition getFolderCondition(int workflowId) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowId), IVdmManager.ActionType.GET_WORKFLOW_FOLDER_CONDITION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (WorkFlowFolderCondition) xstream.fromXML(xml);
	}

	@Override
	public void saveDocWorkFlow(WorkFlowDocumentCondition docCondition) throws Exception {
		XmlAction op = new XmlAction(createArguments(docCondition), IVdmManager.ActionType.SAVE_DOC_WORKFLOW);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateFolderWorkFlow(WorkFlowFolderCondition folderCondition) throws Exception {
		XmlAction op = new XmlAction(createArguments(folderCondition), IVdmManager.ActionType.UPDATE_FOLDER_WORKFLOW);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateDocWorkFlow(WorkFlowDocumentCondition docCondition) throws Exception {
		XmlAction op = new XmlAction(createArguments(docCondition), IVdmManager.ActionType.UPDATE_DOC_WORKFLOW);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public WorkFlowDocumentCondition getDocCondition(int workflowId) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflowId), IVdmManager.ActionType.GET_WORKFLOW_DOC_CONDITION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (WorkFlowDocumentCondition) xstream.fromXML(xml);
	}

	@Override
	public List<Enterprise> getEnterprises() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ENTERPRISES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Enterprise>) xstream.fromXML(xml);
	}

	@Override
	public int saveEnterprise(Enterprise enterprise, List<User> users, String email, boolean isFromAdmin, List<FolderHierarchy> hierarchies) throws Exception {
		XmlAction op = new XmlAction(createArguments(enterprise, users, email, isFromAdmin, hierarchies), IVdmManager.ActionType.SAVE_ENTERPRISE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public boolean deleteEnterprise(Enterprise enterprise) throws Exception {
		XmlAction op = new XmlAction(createArguments(enterprise), IVdmManager.ActionType.DELETE_ENTERPRISE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public void updateEnterprise(Enterprise enterprise, List<User> users) throws Exception {
		XmlAction op = new XmlAction(createArguments(enterprise, users), IVdmManager.ActionType.UPDATE_ENTERPRISE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
	
	@Override
	public void updateEnterpriseUsers(int enterpriseId, List<User> users, TypeUser typeUser) throws Exception {
		XmlAction op = new XmlAction(createArguments(enterpriseId, users, typeUser), IVdmManager.ActionType.UPDATE_ENTERPRISE_USERS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveTask(Set<User> setUsers, Documents doc, String taskTitle, String taskTitleDesc, Date taskDueDate, String email, String taskStatus, String groupTree, int taskStatusType) throws Exception {
		XmlAction op = new XmlAction(createArguments(setUsers, doc, taskTitle, taskTitleDesc, taskDueDate, email, taskStatus, groupTree, taskStatusType), IVdmManager.ActionType.SAVE_TASK);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Tasks> getTask(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_TASK);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Tasks>) xstream.fromXML(xml);
	}

	@Override
	public List<Tasks> getTaskGiven(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_TASK_GIVEN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Tasks>) xstream.fromXML(xml);
	}

	@Override
	public void deleteTask(Tasks task) throws Exception {
		XmlAction op = new XmlAction(createArguments(task), IVdmManager.ActionType.DELETE_TASK);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteWorkFlow(WorkFlow workFlow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workFlow), IVdmManager.ActionType.DELETE_WORKFLOW);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<UserGroup> getUserGroups(Group group) throws Exception {
		XmlAction op = new XmlAction(createArguments(group), IVdmManager.ActionType.GET_USER_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<UserGroup>) xstream.fromXML(xml);
	}

	@Override
	public List<User> getUsersByGroup(List<Group> groups, Integer enterpriseId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groups, enterpriseId), IVdmManager.ActionType.GET_USER_BY_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<User>) xstream.fromXML(xml);
	}

	@Override
	public void updateUserGroups(String email, List<Group> groups) throws Exception {
		XmlAction op = new XmlAction(createArguments(email, groups), IVdmManager.ActionType.UPDATE_USER_GROUP);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Documents savePrivateDocs(Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc), IVdmManager.ActionType.SAVE_PRIVATE_DOCS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public Group getGrouById(int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId), IVdmManager.ActionType.GET_GROUP_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Group) xstream.fromXML(xml);
	}

	@Override
	public void saveKeywordWorkFlow(Keywords keyword) throws Exception {
		XmlAction op = new XmlAction(createArguments(keyword), IVdmManager.ActionType.SAVE_KEYWORD_WORKFLOW);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateKeywordWorkFlow(Keywords keyword) throws Exception {
		XmlAction op = new XmlAction(createArguments(keyword), IVdmManager.ActionType.UPDATE_KEYWORD_WORKFLOW);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteKeywordWowkFlow(Keywords keyword) throws Exception {
		XmlAction op = new XmlAction(createArguments(keyword), IVdmManager.ActionType.DELETE_KEYWORD_WORKFLOW);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Keywords> getAllKeywordsPerWorkFlow(WorkFlow workflow) throws Exception {
		XmlAction op = new XmlAction(createArguments(workflow), IVdmManager.ActionType.GET_ALL_KEYWORD_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Keywords>) xstream.fromXML(xml);
	}

	@Override
	public List<Enterprise> getEnterprisePerUser(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.LOAD_ENTERPRISE_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Enterprise>) xstream.fromXML(xml);
	}

	@Override
	public List<Tree> getFilesPerEnterprise(int enterpriseId, boolean lightweight) throws Exception {
		XmlAction op = new XmlAction(createArguments(enterpriseId, lightweight), IVdmManager.ActionType.GET_FOLDERS_ENTERPRISE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Tree>) xstream.fromXML(xml);
	}

	@Override
	public void saveWorkFlowEvent(WorkFlowEvents event) throws Exception {
		XmlAction op = new XmlAction(createArguments(event), IVdmManager.ActionType.SAVE_WORKFLOW_EVENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<WorkFlowEvents> getAllWorkFlowEvents(int email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_ALL_WORKFLOW_EVENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<WorkFlowEvents>) xstream.fromXML(xml);
	}

	@Override
	public void deleteWorkFlowEvent(WorkFlowEvents event) throws Exception {
		XmlAction op = new XmlAction(createArguments(event), IVdmManager.ActionType.DELETE_WORKFLOW_EVENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveKeywordResults(KeywordResults result) throws Exception {
		XmlAction op = new XmlAction(createArguments(result), IVdmManager.ActionType.SAVE_KEYWORD_RESULT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteKeywordResults(KeywordResults result) throws Exception {
		XmlAction op = new XmlAction(createArguments(result), IVdmManager.ActionType.DELETE_KEYWORD_RESULT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<KeywordResults> getAllKeywordResults(int keywordId) throws Exception {
		XmlAction op = new XmlAction(createArguments(keywordId), IVdmManager.ActionType.GET_ALL_KEYWORD_RESULT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<KeywordResults>) xstream.fromXML(xml);
	}

	@Override
	public void saveChatMessage(ChatMessage message) throws Exception {
		XmlAction op = new XmlAction(createArguments(message), IVdmManager.ActionType.SAVE_CHAT_MESSAGE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteChatMessage(ChatMessage message) throws Exception {
		XmlAction op = new XmlAction(createArguments(message), IVdmManager.ActionType.DELETE_CHAT_MESSAGE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<ChatMessage> getAllChatMessageByUser(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_ALL_CHAT_MESSAGE_BY_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<ChatMessage>) xstream.fromXML(xml);
	}

	@Override
	public List<ChatMessage> getAllChatMessageByReceiver(String receiver) throws Exception {
		XmlAction op = new XmlAction(createArguments(receiver), IVdmManager.ActionType.GET_ALL_CHAT_MESSAGE_BY_RECEIVER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<ChatMessage>) xstream.fromXML(xml);
	}

	@Override
	public void updateTask(Tasks tasks) throws Exception {
		XmlAction op = new XmlAction(createArguments(tasks), IVdmManager.ActionType.UPDATE_TASK);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Enterprise getEnterpriseParent(int itemId, bpm.document.management.core.model.IObject.ItemType type) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, type), IVdmManager.ActionType.GET_ENTERPRISE_PARENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Enterprise) xstream.fromXML(xml);
	}

	@Override
	public void updateChatMessage(ChatMessage m) throws Exception {
		XmlAction op = new XmlAction(createArguments(m), IVdmManager.ActionType.UPDATE_CHAT_MESSAGE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<WorkFlow> getAllWorkFlow(int docId, boolean isDocument) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId, isDocument), IVdmManager.ActionType.GET_ALL_WORKFLOW);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<WorkFlow>) xstream.fromXML(xml);
	}

	@Override
	public List<Tasks> getTaskByDoc(Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc), IVdmManager.ActionType.GET_TASK_BY_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Tasks>) xstream.fromXML(xml);
	}

	@Override
	public int checkLicence(String location) throws Exception {
		XmlAction op = new XmlAction(createArguments(location), IVdmManager.ActionType.CHECK_LICENSE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public void deleteDocPage(int docId, int versionNumber) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId, versionNumber), IVdmManager.ActionType.DELETE_DOCPAGE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveTaskComment(User user, String message, int docId, Date commentDate) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, message, docId, commentDate), IVdmManager.ActionType.SAVE_TASK_COMMENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteTaskComment(TasksComment tasksComment) throws Exception {
		XmlAction op = new XmlAction(createArguments(tasksComment), IVdmManager.ActionType.DELETE_TASK_COMMENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<TasksComment> getTaskComment(int docId) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId), IVdmManager.ActionType.GET_TASK_COMMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<TasksComment>) xstream.fromXML(xml);
	}

	@Override
	public void updateTaskComment(TasksComment tasksComment) throws Exception {
		XmlAction op = new XmlAction(createArguments(tasksComment), IVdmManager.ActionType.UPDATE_TASK_COMMENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Tags> getTags(int id, String type) throws Exception {
		XmlAction op = new XmlAction(createArguments(id, type), IVdmManager.ActionType.GET_TAGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Tags>) xstream.fromXML(xml);
	}

	@Override
	public void saveTag(Tags tags, int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(tags, userId), IVdmManager.ActionType.SAVE_TAG);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteTag(Tags tags) throws Exception {
		XmlAction op = new XmlAction(createArguments(tags), IVdmManager.ActionType.DELETE_TAG);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<FacetValue> getTagsByPopularity() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_TAGS_BY_POPULARITY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FacetValue>) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ProposedTag> getTagLibrary() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_TAGS_LIBRARY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<ProposedTag>) xstream.fromXML(xml);
	}

	@Override
	public void saveTagLibrary(ProposedTag proposedTag) throws Exception {
		XmlAction op = new XmlAction(createArguments(proposedTag), IVdmManager.ActionType.SAVE_TAG_LIBRARY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteTagLibrary(ProposedTag proposedTag) throws Exception {
		XmlAction op = new XmlAction(createArguments(proposedTag), IVdmManager.ActionType.DELETE_TAG_LIBRARY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateTagLibrary(ProposedTag proposedTag) throws Exception {
		XmlAction op = new XmlAction(createArguments(proposedTag), IVdmManager.ActionType.UPDATE_TAG_LIBRARY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Message> getAllMessages(String owner) throws Exception {
		XmlAction op = new XmlAction(createArguments(owner), IVdmManager.ActionType.GET_ALL_MESSAGES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Message>) xstream.fromXML(xml);
	}

	@Override
	public List<IObject> searchTags(String word) throws Exception {
		XmlAction op = new XmlAction(createArguments(word), IVdmManager.ActionType.SEARCH_TAGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<IObject>) xstream.fromXML(xml);
	}

	@Override
	public void saveMessage(String sender, List<String> receiver, String message, Date date, String status, String subject, List<Documents> attachments, String whoFirst, boolean isCopyRequest) throws Exception {
		XmlAction op = new XmlAction(createArguments(sender, receiver, message, date, status, subject, attachments, whoFirst, isCopyRequest), IVdmManager.ActionType.SAVE_MESSAGE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Message> getMessage(String status) throws Exception {
		XmlAction op = new XmlAction(createArguments(status), IVdmManager.ActionType.GET_MESSAGE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Message>) xstream.fromXML(xml);
	}

	@Override
	public void deleteMessage(Message message) throws Exception {
		XmlAction op = new XmlAction(createArguments(message), IVdmManager.ActionType.DELETE_MESSAGE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateMessage(Message message) throws Exception {
		XmlAction op = new XmlAction(createArguments(message), IVdmManager.ActionType.UPDATE_MESSAGE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveChatBuddy(ChatBuddy buddy) throws Exception {
		XmlAction op = new XmlAction(createArguments(buddy), IVdmManager.ActionType.SAVE_CHAT_BUDDY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteChatBuddy(ChatBuddy buddy) throws Exception {
		XmlAction op = new XmlAction(createArguments(buddy), IVdmManager.ActionType.DELETE_CHAT_BUDDY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<ChatBuddy> getAllChatBuddies(String user) throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_CHAT_BUDDIES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<ChatBuddy>) xstream.fromXML(xml);
	}

	@Override
	public void saveLicense(LicenceKey license) throws Exception {
		XmlAction op = new XmlAction(createArguments(license), IVdmManager.ActionType.SAVE_LICENSE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public LicenceKey getLicense() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_LICENSE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (LicenceKey) xstream.fromXML(xml);
	}

	@Override
	public void saveUserWarning(Warnings warning) throws Exception {
		XmlAction op = new XmlAction(createArguments(warning), IVdmManager.ActionType.SAVE_USER_WARNING);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Warnings getWarning(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_USER_WARNING);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Warnings) xstream.fromXML(xml);
	}

	@Override
	public void changeWarning(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.CHANGE_USER_WARNING);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveCampaign(Campaign campaign, List<CampaignLoader> loaders) throws Exception {
		XmlAction op = new XmlAction(createArguments(campaign, loaders), IVdmManager.ActionType.SAVE_CAMPAIGN);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteCampaign(Campaign campaign) throws Exception {
		XmlAction op = new XmlAction(createArguments(campaign), IVdmManager.ActionType.DELETE_CAMPAIGN);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateCampaign(Campaign campaign) throws Exception {
		XmlAction op = new XmlAction(createArguments(campaign), IVdmManager.ActionType.UPDATE_CAMPAIGN);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Campaign> getAllCampaigns() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_CAMPAIGNS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Campaign>) xstream.fromXML(xml);
	}

	@Override
	public List<CampaignLoader> getAllCampaignLoadersByUser(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_ALL_CAMPAIGN_LOADER_BY_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<CampaignLoader>) xstream.fromXML(xml);
	}

	@Override
	public void updateCampaignLoader(CampaignLoader loader) throws Exception {
		XmlAction op = new XmlAction(createArguments(loader), IVdmManager.ActionType.UPDATE_CAMPAIGN_LOADER);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<CampaignLoader> getAllCampaignLoadersByCampaign(Campaign campaign) throws Exception {
		XmlAction op = new XmlAction(createArguments(campaign), IVdmManager.ActionType.GET_ALL_CAMPAIGN_LOADER_BY_CAMPAIGN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<CampaignLoader>) xstream.fromXML(xml);
	}

	@Override
	public void saveCampaignNotes(CampaignNotes note) throws Exception {
		XmlAction op = new XmlAction(createArguments(note), IVdmManager.ActionType.SAVE_CAMPAIGN_NOTES);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<CampaignNotes> getCampaignNotes(CampaignLoader loader) throws Exception {
		XmlAction op = new XmlAction(createArguments(loader), IVdmManager.ActionType.GET_CAMPAIGN_NOTES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<CampaignNotes>) xstream.fromXML(xml);
	}

	@Override
	public void encryptFile(InputStream doc, String password, String url) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc, password, url), IVdmManager.ActionType.FILE_ENCRYPT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void decryptFile(InputStream doc, String password, String url) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc, password, url), IVdmManager.ActionType.FILE_DECRYPT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void editThisVersion(Versions version) throws Exception {
		XmlAction op = new XmlAction(createArguments(version), IVdmManager.ActionType.EDIT_VERSION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public LicenceKey getLicenceFromFile(String url) throws Exception {
		XmlAction op = new XmlAction(createArguments(url), IVdmManager.ActionType.GET_LICENCE_KEY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (LicenceKey) xstream.fromXML(xml);
	}

	@Override
	public Documents uploadFiles(HashMap<String, InputStream> files, Documents doc) throws Exception {
		HashMap map = new HashMap<String, byte[]>();
		for (String file : files.keySet()) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			IOWriter.write(files.get(file), bos, true, true);
			byte[] rawBytes = Base64.encodeBase64(bos.toByteArray());
			map.put(file, rawBytes);
		}
		XmlAction op = new XmlAction(createArguments(map, doc), IVdmManager.ActionType.UPLOAD_FROM_ANDROID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public List<IObject> getAllFilesbyUser(User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(user), IVdmManager.ActionType.GET_ALL_PUBLIC_FILES_BY_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<IObject>) xstream.fromXML(xml);
	}

	@Override
	public List<UserLogs> getAllUserLogs() throws Exception {

		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_USER_LOGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<UserLogs>) xstream.fromXML(xml);
	}

	@Override
	public List<Tasks> getAllTasks() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_TASKS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Tasks>) xstream.fromXML(xml);
	}

	@Override
	public List<Message> getOverallMessage() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_OVERALL_MESSAGES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Message>) xstream.fromXML(xml);
	}

	@Override
	public List<Comments> getOverallComments() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_OVERALL_COMMENTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		System.out.println(xml);
		return (List<Comments>) xstream.fromXML(xml);
	}

	@Override
	public void saveAnnouncement(Announcements a) throws Exception {
		XmlAction op = new XmlAction(createArguments(a), IVdmManager.ActionType.SAVE_ANNOUNCEMENTS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateAnnouncement(Announcements a) throws Exception {
		XmlAction op = new XmlAction(createArguments(a), IVdmManager.ActionType.UPDATE_ANNOUNCEMENTS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteAnnouncement(Announcements a) throws Exception {
		XmlAction op = new XmlAction(createArguments(a), IVdmManager.ActionType.DELETE_ANNOUNCEMENTS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Announcements> getAnnouncements() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ANNOUNCEMENTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Announcements>) xstream.fromXML(xml);
	}

	@Override
	public List<Downloads> getDownloads() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_DOWNLOADS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Downloads>) xstream.fromXML(xml);
	}

	@Override
	public void saveCountry(Country c) throws Exception {
		XmlAction op = new XmlAction(createArguments(c), IVdmManager.ActionType.SAVE_COUNTRY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateCountry(Country c) throws Exception {
		XmlAction op = new XmlAction(createArguments(c), IVdmManager.ActionType.UPDATE_COUNTRY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteCountry(Country c) throws Exception {
		XmlAction op = new XmlAction(createArguments(c), IVdmManager.ActionType.DELETE_COUNTRY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<City> getCity() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_CITY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<City>) xstream.fromXML(xml);
	}

	@Override
	public void saveCity(City c) throws Exception {
		XmlAction op = new XmlAction(createArguments(c), IVdmManager.ActionType.SAVE_CITY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateCity(City c) throws Exception {
		XmlAction op = new XmlAction(createArguments(c), IVdmManager.ActionType.UPDATE_CITY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteCity(City c) throws Exception {
		XmlAction op = new XmlAction(createArguments(c), IVdmManager.ActionType.DELETE_CITY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveCountryCities(List<City> cc, int countryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(cc, countryId), IVdmManager.ActionType.SAVE_COUNTRY_CITY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public XaklFiles saveXakl(XaklFiles xaklFiles) throws Exception {
		XmlAction op = new XmlAction(createArguments(xaklFiles), IVdmManager.ActionType.SAVE_XAKL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (XaklFiles) xstream.fromXML(xml);
	}

	@Override
	public void deleteXakl(int xaklId) throws Exception {
		XmlAction op = new XmlAction(createArguments(xaklId), IVdmManager.ActionType.DELETE_XAKL);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Documents saveXaklDocs(Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc), IVdmManager.ActionType.SAVE_XAKL_DOCS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public void saveXaklAklaBox(List<XaklAklaBox> aklaboxDocs) throws Exception {
		XmlAction op = new XmlAction(createArguments(aklaboxDocs), IVdmManager.ActionType.SAVE_XAKL_AKLABOX);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteXaklAklaBox(List<XaklAklaBox> aklaboxDocs) throws Exception {
		XmlAction op = new XmlAction(createArguments(aklaboxDocs), IVdmManager.ActionType.DELETE_XAKL_AKLABOX);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public XaklFiles updateXakl(XaklFiles xaklFiles) throws Exception {
		XmlAction op = new XmlAction(createArguments(xaklFiles), IVdmManager.ActionType.UPDATE_XAKL_DOCS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (XaklFiles) xstream.fromXML(xml);
	}

	@Override
	public Documents searchDocsOfXakl(Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc), IVdmManager.ActionType.SEARCH_DOC_OF_XAKL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public List<XaklFiles> getXaklTree(Versions v) throws Exception {
		XmlAction op = new XmlAction(createArguments(v), IVdmManager.ActionType.GET_XAKL_TREE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<XaklFiles>) xstream.fromXML(xml);
	}

	@Override
	public Message getMessageById(int messageId) throws Exception {
		XmlAction op = new XmlAction(createArguments(messageId), IVdmManager.ActionType.GET_MESSAGE_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Message) xstream.fromXML(xml);
	}

	@Override
	public List<XaklFiles> getXaklFiles(int docId) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId), IVdmManager.ActionType.GET_XAKLFILES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<XaklFiles>) xstream.fromXML(xml);
	}

	@Override
	public List<XaklAklaBox> getXaklDocs(XaklFiles xaklFiles) throws Exception {
		XmlAction op = new XmlAction(createArguments(xaklFiles), IVdmManager.ActionType.GET_XAKLDOCS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<XaklAklaBox>) xstream.fromXML(xml);
	}

	@Override
	public void saveDocViews(DocViews docViews) throws Exception {
		XmlAction op = new XmlAction(createArguments(docViews), IVdmManager.ActionType.SAVE_DOC_VIEWS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<DocViews> getDocViews(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_DOC_VIEWS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DocViews>) xstream.fromXML(xml);
	}

	@Override
	public void shareDocument(String documentName, String typeShare, List<User> users, List<Group> groups, InputStream stream, User user, int directoryId) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(stream, bos, true, true);
		byte[] rawBytes = Base64.encodeBase64(bos.toByteArray());
		XmlAction op = new XmlAction(createArguments(documentName, typeShare, users, groups, rawBytes, user, directoryId), IVdmManager.ActionType.SHARE_DOCUMENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));

	}

	@Override
	public UserLogs getUserLastLogin(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.USER_LAST_LOGIN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (UserLogs) xstream.fromXML(xml);
	}

	@Override
	public UserDrivers getDriverInfo(int userId, String driverType) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId, driverType), IVdmManager.ActionType.GET_DRIVER_INFO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (UserDrivers) xstream.fromXML(xml);
	}

	@Override
	public UserDrivers saveUserDriver(UserDrivers userDrivers) throws Exception {
		XmlAction op = new XmlAction(createArguments(userDrivers), IVdmManager.ActionType.SAVE_USER_DRIVER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (UserDrivers) xstream.fromXML(xml);
	}

	@Override
	public UserDrivers updateUserDriver(UserDrivers userDrivers) throws Exception {
		XmlAction op = new XmlAction(createArguments(userDrivers), IVdmManager.ActionType.UPDATE_USER_DRIVER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (UserDrivers) xstream.fromXML(xml);
	}

	@Override
	public HashMap<Distribution, List<DistributionLoader>> getAllDistribution() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_DISTRIBUTION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (HashMap<Distribution, List<DistributionLoader>>) xstream.fromXML(xml);
	}

	@Override
	public void saveDistribution(Distribution distribution, HashMap<DistributionLoader, List<Documents>> dispatchedMap, DistributionRunLog log) throws Exception {
		XmlAction op = new XmlAction(createArguments(distribution, dispatchedMap, log), IVdmManager.ActionType.SAVE_DISTRIBUTION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void preSaveDistribution(Distribution distribution, List<DistributionLoader> listLoaders) throws Exception {
		XmlAction op = new XmlAction(createArguments(distribution, listLoaders), IVdmManager.ActionType.PRE_SAVE_DISTRIBUTION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateDistributionLoader(DistributionLoader loader) throws Exception {
		XmlAction op = new XmlAction(createArguments(loader), IVdmManager.ActionType.UPDATE_DISTRIBUTIONLOADER);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateDistribution(Distribution distribution) throws Exception {
		XmlAction op = new XmlAction(createArguments(distribution), IVdmManager.ActionType.UPDATE_DISTRIBUTION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveDistributionLog(DistributionRunLog log) throws Exception {
		XmlAction op = new XmlAction(createArguments(log), IVdmManager.ActionType.SAVE_DISTRIBUTION_LOG);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<DistributionRunLog> getDistributionRunLogs(int distributionId) throws Exception {
		XmlAction op = new XmlAction(createArguments(distributionId), IVdmManager.ActionType.GET_DISTRIBUTION_RUN_LOGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DistributionRunLog>) xstream.fromXML(xml);
	}

	@Override
	public List<DistributionRunLogLoaders> getDistributionLogLoaders(DistributionRunLog log) throws Exception {
		XmlAction op = new XmlAction(createArguments(log), IVdmManager.ActionType.GET_DISTRIBUTION_RUN_LOGS_LOADERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DistributionRunLogLoaders>) xstream.fromXML(xml);
	}

	@Override
	public void saveDistributionLoader(DistributionLoader loader) throws Exception {
		XmlAction op = new XmlAction(createArguments(loader), IVdmManager.ActionType.SAVE_DISTIRIBUTION_LOADER);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveUserGroup(List<User> users, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(users, groupId), IVdmManager.ActionType.SAVE_USERGROUP);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteDistributionLoader(DistributionLoader loader) throws Exception {
		XmlAction op = new XmlAction(createArguments(loader), IVdmManager.ActionType.DELETE_DISTRIBUTION_LOADER);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<DistributionLoader> getAllDistributionLoaders(Distribution distribution) throws Exception {
		XmlAction op = new XmlAction(createArguments(distribution), IVdmManager.ActionType.GET_ALL_DISTRIBUTION_LOADER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DistributionLoader>) xstream.fromXML(xml);
	}

	@Override
	public void addFeedback(FeedBack feedback) throws Exception {
		XmlAction op = new XmlAction(createArguments(feedback), IVdmManager.ActionType.ADD_FEEDBACK);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public InputStream loadfile(String id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.LOAD_FILE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (InputStream) xstream.fromXML(xml);
	}

	public String uploadfile(InputStream file, String parentid, String name, String path, User user, Date lastModified) throws Exception {
		XmlAction op = new XmlAction(createArguments(file, parentid, name, path, user, lastModified), IVdmManager.ActionType.UPLOAD_FILE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	public String uploadNewVersionFile(Documents doc, InputStream inputStream, String filename, User user, Date lastModifiedDate) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc, inputStream, filename, user, lastModifiedDate), IVdmManager.ActionType.UPLOAD_VERSION_FILE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	@Override
	public String createDirectory(User user, String name) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, name), IVdmManager.ActionType.CREATE_DIRECTORY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	@Override
	public String createSubDirectory(String name, String parentId, User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(name, parentId, user), IVdmManager.ActionType.CREATE_SUB_DIRECTORY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	@Override
	public List<Documents> getCheckoutDocs() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_CHECKOUT_DOCS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public void savePage(DocPages page) throws Exception {
		XmlAction op = new XmlAction(createArguments(page), IVdmManager.ActionType.SAVE_PAGE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<XaklFiles> getXaklFilesById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_XAKLFILES_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<XaklFiles>) xstream.fromXML(xml);
	}

	@Override
	public Tree getFolderCode(String code) throws Exception {
		XmlAction op = new XmlAction(createArguments(code), IVdmManager.ActionType.GET_FOLDER_CODE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Tree) xstream.fromXML(xml);
	}

	@Override
	public void saveState(StandardAddress sAddress) throws Exception {
		XmlAction op = new XmlAction(createArguments(sAddress), IVdmManager.ActionType.SAVE_STATE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateState(StandardAddress sAddress) throws Exception {
		XmlAction op = new XmlAction(createArguments(sAddress), IVdmManager.ActionType.UPDATE_STATE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<StandardAddress> getStates() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_STATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<StandardAddress>) xstream.fromXML(xml);
	}

	@Override
	public void deleteState(StandardAddress sAddress) throws Exception {
		XmlAction op = new XmlAction(createArguments(sAddress), IVdmManager.ActionType.DELETE_STATE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void convertToPDF(IObject o) throws Exception {
		XmlAction op = new XmlAction(createArguments(o), IVdmManager.ActionType.CONVERT_TO_PDF);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveDetailedAddress(DetailedAddress dAddress) throws Exception {
		XmlAction op = new XmlAction(createArguments(dAddress), IVdmManager.ActionType.SAVE_DETAILED_ADDRESS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateDetailedAddress(DetailedAddress dAddress, String originalReference, int saId) throws Exception {
		XmlAction op = new XmlAction(createArguments(dAddress, originalReference, saId), IVdmManager.ActionType.UPDATE_DETAILED_ADDRESS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteDetailedAddress(DetailedAddress dAddress) throws Exception {
		XmlAction op = new XmlAction(createArguments(dAddress), IVdmManager.ActionType.DELETE_DETAILED_ADDRESS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<DetailedAddress> getDetailedAddress() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_DETAILED_ADDRESS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DetailedAddress>) xstream.fromXML(xml);
	}

	@Override
	public void addLevel(DetailedLevel level) throws Exception {
		XmlAction op = new XmlAction(createArguments(level), IVdmManager.ActionType.ADD_DETAILED_LEVEL);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<DetailedLevel> getLevels(int daId) throws Exception {
		XmlAction op = new XmlAction(createArguments(daId), IVdmManager.ActionType.GET_DETAILED_LEVEL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DetailedLevel>) xstream.fromXML(xml);
	}

	@Override
	public void saveFileLocation(FileLocation fileLocation) throws Exception {
		XmlAction op = new XmlAction(createArguments(fileLocation), IVdmManager.ActionType.SAVE_FILE_LOCATION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<FileLocation> getPhysicalLocations() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_PHYSICAL_LOCATIONS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FileLocation>) xstream.fromXML(xml);
	}

	@Override
	public List<String> aggregateDocuments(List<IObject> files, int aklaBoxDirectory, String path, String fileName, int userId, String type, String orientation) throws Exception {
		XmlAction op = new XmlAction(createArguments(files, aklaBoxDirectory, path, fileName, userId, type, orientation), IVdmManager.ActionType.AGGREGATE_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<String>) xstream.fromXML(xml);
	}

	@Override
	public void deletePhysicalLocation(FileLocation fileLocation) throws Exception {
		XmlAction op = new XmlAction(createArguments(fileLocation), IVdmManager.ActionType.DELETE_PHYSICAL_LOCATION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveOfficeNo(OfficeAddress officeAddress) throws Exception {
		XmlAction op = new XmlAction(createArguments(officeAddress), IVdmManager.ActionType.SAVE_OFFICE_NO);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<OfficeAddress> getOfficeAddress(int levelNo, int daId) throws Exception {
		XmlAction op = new XmlAction(createArguments(levelNo, daId), IVdmManager.ActionType.GET_OFFICE_NO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<OfficeAddress>) xstream.fromXML(xml);
	}

	@Override
	public void deleteLevel(DetailedLevel dLevel) throws Exception {
		XmlAction op = new XmlAction(createArguments(dLevel), IVdmManager.ActionType.DELETE_LEVEL);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateLevel(DetailedLevel dLevel) throws Exception {
		XmlAction op = new XmlAction(createArguments(dLevel), IVdmManager.ActionType.UPDATE_LEVEL);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteOffice(OfficeAddress oAddress) throws Exception {
		XmlAction op = new XmlAction(createArguments(oAddress), IVdmManager.ActionType.DELETE_OFFICE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateOffice(OfficeAddress oAddress) throws Exception {
		XmlAction op = new XmlAction(createArguments(oAddress), IVdmManager.ActionType.UPDATE_OFFICE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateFileLocation(FileLocation location) throws Exception {
		XmlAction op = new XmlAction(createArguments(location), IVdmManager.ActionType.UPDATE_FILE_LOCATION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public String sendEmail(MailConfig mailConfig, HashMap<String, InputStream> attachements) throws Exception {
		HashMap<String, byte[]> serializedAttachements = new HashMap<String, byte[]>();
		try {
			for (String attachementName : attachements.keySet()) {
				InputStream stream = attachements.get(attachementName);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int sz = 0;
				byte[] buf = new byte[1024];
				while ((sz = stream.read(buf)) >= 0) {
					bos.write(buf, 0, sz);
				}
				stream.close();

				byte[] streamDatas = Base64.encodeBase64(bos.toByteArray());

				serializedAttachements.put(attachementName, streamDatas);
			}
		} catch (IOException ex) {
			throw new IOException("Failed to serialize email attachements : " + ex.getMessage());
		}
		XmlAction op = new XmlAction(createArguments(mailConfig, serializedAttachements), IVdmManager.ActionType.SEND_MAIL);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op));

		return result;
	}

	@Override
	public void sendEmails(List<EmailInfo> mails) throws Exception {
		XmlAction op = new XmlAction(createArguments(mails), IVdmManager.ActionType.SEND_MAILS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void copyPaste(IObject doc, Tree t, User us) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc, t, us), IVdmManager.ActionType.COPY_PASTE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteDistribution(Distribution distribution) throws Exception {
		XmlAction op = new XmlAction(createArguments(distribution), IVdmManager.ActionType.DELETE_DISTRIBUTION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteVersion(Versions version) throws Exception {
		XmlAction op = new XmlAction(createArguments(version), IVdmManager.ActionType.DELETE_DOC_VERSION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateVersion(Versions version) throws Exception {
		XmlAction op = new XmlAction(createArguments(version), IVdmManager.ActionType.UPDATE_DOC_VERSION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void initSessionFromVanilla(String hash, String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(hash, email), IVdmManager.ActionType.INIT_VANILLA_CONNECTION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public User getUserFromVanilla(String hash) throws Exception {
		XmlAction op = new XmlAction(createArguments(hash), IVdmManager.ActionType.GET_USER_FROM_VANILLA);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (User) xstream.fromXML(xml);
	}

	@Override
	public CampaignLoader getCampaignLoaderById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_CAMPAIGN_LOADER_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (CampaignLoader) xstream.fromXML(xml);
	}

	@Override
	public void saveSearchResult(SearchResult result) throws Exception {
		XmlAction op = new XmlAction(createArguments(result), IVdmManager.ActionType.SAVE_SEARCH_RESULT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteSearchResult(SearchResult result) throws Exception {
		XmlAction op = new XmlAction(createArguments(result), IVdmManager.ActionType.DELETE_SEARCH_RESULT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<SearchResult> getAllSearchResult(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_ALL_SEARCH_RESULT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<SearchResult>) xstream.fromXML(xml);
	}

	@Override
	public List<Tags> getTagByUser(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_TAG_BY_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Tags>) xstream.fromXML(xml);
	}

	@Override
	public void savePlatform(Platform platform) throws Exception {
		XmlAction op = new XmlAction(createArguments(platform), IVdmManager.ActionType.SAVE_PLATFORM);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updatePlatform(Platform platform) throws Exception {
		XmlAction op = new XmlAction(createArguments(platform), IVdmManager.ActionType.UPDATE_PLATFORM);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Platform getPlatform() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_PLATFORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Platform) xstream.fromXML(xml);
	}

	@Override
	public LoginDetail saveLoginDetail(LoginDetail loginDetail) throws Exception {
		XmlAction op = new XmlAction(createArguments(loginDetail), IVdmManager.ActionType.SAVE_LOGIN_DETAIL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (LoginDetail) xstream.fromXML(xml);
	}

	@Override
	public List<LoginDetail> getAllLoginDetails() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_LOGIN_DETAIL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<LoginDetail>) xstream.fromXML(xml);
	}

	@Override
	public void updateLoginDetail(LoginDetail loginDetail) throws Exception {
		XmlAction op = new XmlAction(createArguments(loginDetail), IVdmManager.ActionType.UPDATE_LOGIN_DETAIL);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveUserRole(UserRole userRole) throws Exception {
		XmlAction op = new XmlAction(createArguments(userRole), IVdmManager.ActionType.SAVE_USER_ROLE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteUserRole(UserRole userRole) throws Exception {
		XmlAction op = new XmlAction(createArguments(userRole), IVdmManager.ActionType.DELETE_USER_ROLE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public UserRole getUserRole(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_ALL_USER_ROLES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (UserRole) xstream.fromXML(xml);
	}

	@Override
	public void saveDocName(DocName docName) throws Exception {
		XmlAction op = new XmlAction(createArguments(docName), IVdmManager.ActionType.SAVE_DOC_NAME);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateDocName(DocName docName) throws Exception {
		XmlAction op = new XmlAction(createArguments(docName), IVdmManager.ActionType.UPDATE_DOC_NAME);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public DocName getDocNameByFolder(int folderId) throws Exception {
		XmlAction op = new XmlAction(createArguments(folderId), IVdmManager.ActionType.GET_DOC_NAME_BY_FOLDER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (DocName) xstream.fromXML(xml);
	}

	@Override
	public void saveDocLog(DocLogs log) throws Exception {
		XmlAction op = new XmlAction(createArguments(log), IVdmManager.ActionType.SAVE_DOC_LOG);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateDocLog(DocLogs log) throws Exception {
		XmlAction op = new XmlAction(createArguments(log), IVdmManager.ActionType.UPDATE_DOC_LOG);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteDocLog(DocLogs log) throws Exception {
		XmlAction op = new XmlAction(createArguments(log), IVdmManager.ActionType.DELETE_DOC_LOG);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<DocLogs> getAllDocLogs(int docId, String type) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId, type), IVdmManager.ActionType.GET_ALL_DOC_LOGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DocLogs>) xstream.fromXML(xml);
	}

	@Override
	public void saveUserConnectionProperty(UserConnectionProperty connProperty) throws Exception {
		XmlAction op = new XmlAction(createArguments(connProperty), IVdmManager.ActionType.SAVE_USER_CONNECTION_PROPERTY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateUserConnectionProperty(UserConnectionProperty connProperty) throws Exception {
		XmlAction op = new XmlAction(createArguments(connProperty), IVdmManager.ActionType.UPDATE_USER_CONNECTION_PROPERTY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public UserConnectionProperty getConnectionPropertyByUser(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_USER_CONNECTION_PROPERTY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (UserConnectionProperty) xstream.fromXML(xml);
	}

	@Override
	public List<RMFolder> getAllRMFoldersByUser(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_ALL_RM_FOLDERS_BY_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<RMFolder>) xstream.fromXML(xml);
	}

	@Override
	public List<RMField> getAllRMFieldsByFolder(int rmFolderId) throws Exception {
		XmlAction op = new XmlAction(createArguments(rmFolderId), IVdmManager.ActionType.GET_ALL_RM_FIELDS_BY_FOLDER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<RMField>) xstream.fromXML(xml);
	}

	@Override
	public void saveRMDoc(RMDocument rmDoc) throws Exception {
		XmlAction op = new XmlAction(createArguments(rmDoc), IVdmManager.ActionType.SAVE_RM_DOC);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<RMDocument> getAllRMDocByFolder(int rmFolderId) throws Exception {
		XmlAction op = new XmlAction(createArguments(rmFolderId), IVdmManager.ActionType.GET_ALL_RM_DOC_BY_FOLDER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<RMDocument>) xstream.fromXML(xml);
	}

	@Override
	public RMFolder getRMFolder(int folderId) throws Exception {
		XmlAction op = new XmlAction(createArguments(folderId), IVdmManager.ActionType.GET_RM_FOLDER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (RMFolder) xstream.fromXML(xml);
	}

	@Override
	public RMField getRMField(int rmFieldId) throws Exception {
		XmlAction op = new XmlAction(createArguments(rmFieldId), IVdmManager.ActionType.GET_RM_FIELD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (RMField) xstream.fromXML(xml);
	}

	@Override
	public void checkRMScheduleDeadline(Documents doc, int currentUser) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc, currentUser), IVdmManager.ActionType.CHECK_RM_SCHEDULE_DEADLINE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveLoginAttempt(LoginAttempt attempt) throws Exception {
		XmlAction op = new XmlAction(createArguments(attempt), IVdmManager.ActionType.SAVE_LOGIN_ATTEMPT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<LoginAttempt> getAllLoginAttempts() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_LOGIN_ATTEMPTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<LoginAttempt>) xstream.fromXML(xml);
	}

	@Override
	public List<UserLogs> getUserLogsByUser(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_USER_LOGS_BY_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<UserLogs>) xstream.fromXML(xml);
	}

	@Override
	public void saveUserLog(UserLogs userLog) throws Exception {
		XmlAction op = new XmlAction(createArguments(userLog), IVdmManager.ActionType.SAVE_USER_LOG);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public int getDocDownloads(int docId) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId), IVdmManager.ActionType.GET_DOC_DOWNLOADS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public int getFolderDownloads(int folderId) throws Exception {
		XmlAction op = new XmlAction(createArguments(folderId), IVdmManager.ActionType.GET_FOLDER_DOWNLOADS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public List<TopDownloads> getTopMostDownloads(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_TOP_MOST_DOWNLOADS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<TopDownloads>) xstream.fromXML(xml);
	}

	@Override
	public void saveLinkedDoc(CopyDocuments linkedDoc) throws Exception {
		XmlAction op = new XmlAction(createArguments(linkedDoc), IVdmManager.ActionType.SAVE_LINKED_DOC);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<CopyDocuments> getLinkedDocuments(int toFolder) throws Exception {
		XmlAction op = new XmlAction(createArguments(toFolder), IVdmManager.ActionType.GET_LINKED_DOCUMENTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<CopyDocuments>) xstream.fromXML(xml);
	}

	@Override
	public void saveAttachDoc(ReferenceDocuments refDoc) throws Exception {
		XmlAction op = new XmlAction(createArguments(refDoc), IVdmManager.ActionType.SAVE_REFERENCE_DOC);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<ReferenceDocuments> getAttachDocuments(int docId) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId), IVdmManager.ActionType.GET_REFERENCE_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<ReferenceDocuments>) xstream.fromXML(xml);
	}

	@Override
	public void deleteCopyDoc(CopyDocuments doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc), IVdmManager.ActionType.DELETE_COPY_DOC);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<CopyDocuments> getCopyDocument(int docId, int folderId) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId, folderId), IVdmManager.ActionType.GET_COPY_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<CopyDocuments>) xstream.fromXML(xml);
	}

	@Override
	public DocName getCustomNameByFolder(int folderId) throws Exception {
		XmlAction op = new XmlAction(createArguments(folderId), IVdmManager.ActionType.GET_CUSTOM_NAME_BY_FOLDER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (DocName) xstream.fromXML(xml);
	}

	@Override
	public List<DocName> getAllAutomaticCustomName() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_AUTOMATIC_CUSTOM_NAME);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DocName>) xstream.fromXML(xml);
	}

	@Override
	public LOV saveLov(LOV lov) throws Exception {
		XmlAction op = new XmlAction(createArguments(lov), IVdmManager.ActionType.SAVE_LOV);
		String xml =httpCommunicator.executeAction(op, xstream.toXML(op));
		return (LOV) xstream.fromXML(xml);
	}
	
	@Override
	public void deleteLov(LOV lov) throws Exception {
		XmlAction op = new XmlAction(createArguments(lov), IVdmManager.ActionType.DELETE_LOV);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<LOV> getAllLov() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_LOV);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<LOV>) xstream.fromXML(xml);
	}

	@Override
	public List<LOV> getAllDistinctLov() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_DISTINCT_LOV);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<LOV>) xstream.fromXML(xml);
	}

	@Override
	public void saveFolderStorageLocation(FolderStorageLocation fsl) throws Exception {
		XmlAction op = new XmlAction(createArguments(fsl), IVdmManager.ActionType.SAVE_FOLDER_STORAGE_LOCATION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public FolderStorageLocation getFolderStorageLocation(int folderId) throws Exception {
		XmlAction op = new XmlAction(createArguments(folderId), IVdmManager.ActionType.GET_FOLDER_STORAGE_LOCATION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FolderStorageLocation) xstream.fromXML(xml);
	}

	@Override
	public void saveAddressField(AddressField address) throws Exception {
		XmlAction op = new XmlAction(createArguments(address), IVdmManager.ActionType.SAVE_ADDRESS_FIELD);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<AddressField> getAllAddressField() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_ADDRESS_FIELD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AddressField>) xstream.fromXML(xml);
	}

	@Override
	public void deleteAddressField(AddressField address) throws Exception {
		XmlAction op = new XmlAction(createArguments(address), IVdmManager.ActionType.DELETE_ADDRESS_FIELD);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Form saveForm(Form form) throws Exception {
		XmlAction op = new XmlAction(createArguments(form), IVdmManager.ActionType.SAVE_FORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Form) xstream.fromXML(xml);
	}

	@Override
	public void updateForm(Form form) throws Exception {
		XmlAction op = new XmlAction(createArguments(form), IVdmManager.ActionType.UPDATE_FORM);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteForm(Form form) throws Exception {
		XmlAction op = new XmlAction(createArguments(form), IVdmManager.ActionType.DELETE_FORM);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Form> getAllForm() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_FORMS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Form>) xstream.fromXML(xml);
	}

	@Override
	public FormField saveFormField(FormField formField) throws Exception {
		XmlAction op = new XmlAction(createArguments(formField), IVdmManager.ActionType.SAVE_FORM_FIELD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FormField) xstream.fromXML(xml);
	}

	@Override
	public void updateFormField(FormField formField) throws Exception {
		XmlAction op = new XmlAction(createArguments(formField), IVdmManager.ActionType.UPDATE_FORM_FIELD);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteFormField(FormField formField) throws Exception {
		XmlAction op = new XmlAction(createArguments(formField), IVdmManager.ActionType.DELETE_FORM_FIELD);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<FormField> getAllFormFields(int formId) throws Exception {
		XmlAction op = new XmlAction(createArguments(formId), IVdmManager.ActionType.GET_ALL_FORM_FIELD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormField>) xstream.fromXML(xml);
	}

	@Override
	public HashMap<String, String> getAllLovByTableCode(LOV lov) throws Exception {
		XmlAction op = new XmlAction(createArguments(lov), IVdmManager.ActionType.GET_ALL_LOV_BY_TABLE_CODE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (HashMap<String, String>) xstream.fromXML(xml);
	}

	@Override
	public void saveFolderForm(FormFolder formFolder) throws Exception {
		XmlAction op = new XmlAction(createArguments(formFolder), IVdmManager.ActionType.SAVE_FOLDER_FORM);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<FormFolder> getFolderForms(int folderId) throws Exception {
		XmlAction op = new XmlAction(createArguments(folderId), IVdmManager.ActionType.GET_FOLDER_FORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormFolder>) xstream.fromXML(xml);
	}

	@Override
	public List<Form> getFolderFormsByUser(int folderId, int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(folderId, userId), IVdmManager.ActionType.GET_FOLDER_FORM_BY_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Form>) xstream.fromXML(xml);
	}

	@Override
	public void saveFormFieldValue(int docId, List<FormFieldValue> values) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId, values), IVdmManager.ActionType.SAVE_FORM_FIELD_VALUE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<FormFieldValue> getAllFormFieldValues(int formId) throws Exception {
		XmlAction op = new XmlAction(createArguments(formId), IVdmManager.ActionType.GET_ALL_FORM_FIELD_VALUES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormFieldValue>) xstream.fromXML(xml);
	}

	@Override
	public List<FileType> getAllFilesType() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_TYPES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FileType>) xstream.fromXML(xml);
	}

	@Override
	public FileType getFirstType() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_FIRST_TYPE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FileType) xstream.fromXML(xml);
	}

	@Override
	public FormField getFormField(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_FORM_FIELD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FormField) xstream.fromXML(xml);
	}

	@Override
	public void updateFormFieldValue(FormFieldValue value) throws Exception {
		XmlAction op = new XmlAction(createArguments(value), IVdmManager.ActionType.UPDATE_FORM_FIELD_VALUE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public FormFieldValue getFormFieldValue(int ffValueId) throws Exception {
		XmlAction op = new XmlAction(createArguments(ffValueId), IVdmManager.ActionType.GET_FORM_FIELD_VALUE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FormFieldValue) xstream.fromXML(xml);
	}

	@Override
	public FormFieldValue getFormFieldValueByDoc(int ffId, int userId, int docId) throws Exception {
		XmlAction op = new XmlAction(createArguments(ffId, userId, docId), IVdmManager.ActionType.GET_FORM_FIELD_VALUE_BY_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FormFieldValue) xstream.fromXML(xml);
	}

	@Override
	public void saveFileTypes(List<FileType> fileTypes) throws Exception {
		XmlAction op = new XmlAction(createArguments(fileTypes), IVdmManager.ActionType.SAVE_FILES_TYPES);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void addReply(Reply reply) throws Exception {
		XmlAction op = new XmlAction(createArguments(reply), IVdmManager.ActionType.ADD_REPLY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void modifyReply(Reply reply) throws Exception {
		XmlAction op = new XmlAction(createArguments(reply), IVdmManager.ActionType.MODIFY_REPLY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteReply(Reply reply) throws Exception {
		XmlAction op = new XmlAction(createArguments(reply), IVdmManager.ActionType.DELETE_REPLY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Reply> getAllReplies() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_REPLIES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Reply>) xstream.fromXML(xml);
	}

	@Override
	public void saveTask(Tasks task) throws Exception {
		XmlAction op = new XmlAction(createArguments(task), IVdmManager.ActionType.ADD_TASK);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Documents> getDocumentForArchiving(Archiving a, Date dateLimit) throws Exception {
		XmlAction op = new XmlAction(createArguments(a, dateLimit), IVdmManager.ActionType.GET_ARCHIVING_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public List<Archiving> getAllArchiving() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_ARCHIVING);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Archiving>) xstream.fromXML(xml);
	}

	@Override
	public Archiving getArchivingbyId(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_ARCHIVING_BYID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Archiving) xstream.fromXML(xml);
	}

	@Override
	public Archiving saveArchiving(Archiving archiving) throws Exception {
		XmlAction op = new XmlAction(createArguments(archiving), IVdmManager.ActionType.SAVE_ARCHIVING);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Archiving) xstream.fromXML(xml);
	}

	@Override
	public List<MassImport> getAllMassImport() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_MASSIMPORT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MassImport>) xstream.fromXML(xml);
	}

	@Override
	public MassImport getMassImportbyId(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_MASSIMPORT_BYID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (MassImport) xstream.fromXML(xml);
	}

	@Override
	public MassImport saveMassImport(MassImport massImport) throws Exception {
		XmlAction op = new XmlAction(createArguments(massImport), IVdmManager.ActionType.SAVE_MASSIMPORT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (MassImport) xstream.fromXML(xml);
	}

	@Override
	public void deleteArchiving(Archiving archiving) throws Exception {
		XmlAction op = new XmlAction(createArguments(archiving), IVdmManager.ActionType.DELETE_ARCHIVING);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));

	}

	@Override
	public void deleteMassImport(MassImport massImport) throws Exception {
		XmlAction op = new XmlAction(createArguments(massImport), IVdmManager.ActionType.DELETE_MASSIMPORT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<FormFieldValue> searchOnFormField(int formFieldId, String value, String operator) throws Exception {
		XmlAction op = new XmlAction(createArguments(formFieldId, value, operator), IVdmManager.ActionType.SEARCH_ON_FORM_FIELD);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormFieldValue>) xstream.fromXML(xml);
	}

	@Override
	public List<IObject> getMailRootDirectories() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_MAIL_ROOT_DIRECTORIES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<IObject>) xstream.fromXML(xml);
	}

	@Override
	public Documents saveMailDocument(Documents doc, List<Tasks> task, List<FormFieldValue> values) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc, task, values), IVdmManager.ActionType.SAVE_MAIL_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}


	@Override
	public List<SourceConnection> getAllConnection() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_CONNECTION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<SourceConnection>) xstream.fromXML(xml);
	}

	@Override
	public SourceConnection getSourceConnectionbyId(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_CONNECTION_BYID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (SourceConnection) xstream.fromXML(xml);
	}

	@Override
	public SourceConnection saveSourceConnection(SourceConnection connection)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(connection), IVdmManager.ActionType.SAVE_CONNECTION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (SourceConnection) xstream.fromXML(xml);
	}

	@Override
	public void deleteSourceConnection(SourceConnection connection)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(connection), IVdmManager.ActionType.DELETE_CONNECTION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
	}

	@Override
	public Boolean testConnection(SourceConnection connection) throws Exception {
		XmlAction op = new XmlAction(createArguments(connection), IVdmManager.ActionType.TEST_CONNECTION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public List<String> getTable(SourceConnection connection) throws Exception {
		XmlAction op = new XmlAction(createArguments(connection), IVdmManager.ActionType.GET_TABLESNAME);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<String>) xstream.fromXML(xml);
	}

	@Override
	public List<String> getColumns(SourceConnection connection, String tableName) throws Exception {
		XmlAction op = new XmlAction(createArguments(connection, tableName), IVdmManager.ActionType.GET_COLUMNSNAME);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<String>) xstream.fromXML(xml);
	}


	@Override
	public List<OrganigramElement> getOrganigram() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ORGANIGRAM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<OrganigramElement>) xstream.fromXML(xml);
	}

	@Override
	public void saveOrganigram(List<OrganigramElement> elements) throws Exception {
		XmlAction op = new XmlAction(createArguments(elements), IVdmManager.ActionType.SAVE_ORGANIGRAM);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<IObject> getOrganigramFolderContent(int parentId) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId), IVdmManager.ActionType.GET_ORGANIGRAM_FOLDER_CONTENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<IObject>) xstream.fromXML(xml);
	}

	@Override
	public List<MailSearchResult> getMailSearchResult(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_MAIL_SEARCH_RESULT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		if (xml.isEmpty()) {
			return new ArrayList<MailSearchResult>();
		}
		return (List<MailSearchResult>) xstream.fromXML(xml);
	}

	@Override
	public void saveMailSearchResult(MailSearchResult mailSearchResult) throws Exception {
		XmlAction op = new XmlAction(createArguments(mailSearchResult), IVdmManager.ActionType.SAVE_MAIL_SEARCH_RESULT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteMailSearchResult(MailSearchResult mailSearchResult) throws Exception {
		XmlAction op = new XmlAction(createArguments(mailSearchResult), IVdmManager.ActionType.DELETE_MAIL_SEARCH_RESULT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveCommentAndUpdateTasks(Comments comment, Tasks task) throws Exception {
		XmlAction op = new XmlAction(createArguments(comment, task), IVdmManager.ActionType.SAVE_COMMENT_UPDATE_TASK);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void approveComment(Comments comment, Tasks task) throws Exception {
		XmlAction op = new XmlAction(createArguments(comment, task), IVdmManager.ActionType.APPROVE_COMMENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void validateTask(Tasks task) throws Exception {
		XmlAction op = new XmlAction(createArguments(task), IVdmManager.ActionType.VALIDATE_TASK);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void closeTask(Tasks t) throws Exception {
		XmlAction op = new XmlAction(createArguments(t), IVdmManager.ActionType.CLOSE_TASK);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void reviveTask(Tasks task) throws Exception {
		XmlAction op = new XmlAction(createArguments(task), IVdmManager.ActionType.REVIVE_TASK);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public AklaboxConfig getConfig() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_CONFIG);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AklaboxConfig) xstream.fromXML(xml);
	}

	@Override
	public void saveConfig(AklaboxConfig config) throws Exception {
		XmlAction op = new XmlAction(createArguments(config), IVdmManager.ActionType.SAVE_CONFIG);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void relance(Tasks task) throws Exception {
		XmlAction op = new XmlAction(createArguments(task), IVdmManager.ActionType.RELANCE_TASK);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Tasks getPreviousTask(Tasks task) throws Exception {
		XmlAction op = new XmlAction(createArguments(task), IVdmManager.ActionType.GET_PREVIOUS_TASK);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Tasks) xstream.fromXML(xml);
	}

	@Override
	public void addListValue(LOV listOfValues, String value) throws Exception {
		XmlAction op = new XmlAction(createArguments(listOfValues, value), IVdmManager.ActionType.ADD_LIST_VALUE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void addResponse(Tasks task, Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(task, doc), IVdmManager.ActionType.ADD_RESPONSE_DOC);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteFileType(FileType object) throws Exception {
		XmlAction op = new XmlAction(createArguments(object), IVdmManager.ActionType.DELETE_FILE_TYPE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
	
	@Override
	public Tasks getTaskById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_TASK_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Tasks) xstream.fromXML(xml);
	}

	@Override
	public List<Departement> getDepartements(int countryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(countryId), IVdmManager.ActionType.GET_ALL_DEPARTEMENTS_BY_COUNTRY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Departement>) xstream.fromXML(xml);
	}
	@Override
	public List<LogMassImport> getLogMassImports(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_LOG_MASSIMPORT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<LogMassImport>) xstream.fromXML(xml);
	}
	@Override
	public List<City> getCities(int depId) throws Exception {
		XmlAction op = new XmlAction(createArguments(depId), IVdmManager.ActionType.GET_CITIES_BY_DEP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<City>) xstream.fromXML(xml);
	}

	@Override
	public void saveDepartement(Departement dep) throws Exception {
		XmlAction op = new XmlAction(createArguments(dep), IVdmManager.ActionType.SAVE_DEPARTEMENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Departement> getDepartements() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_DEPARTEMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Departement>) xstream.fromXML(xml);
	}


	@Override
	public LogMassImport saveLogMassImport(LogMassImport logMassImport)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(logMassImport), IVdmManager.ActionType.SAVE_LOG_MASSIMPORT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (LogMassImport) xstream.fromXML(xml);
	}

	@Override
	public void updateLogMassImport(LogMassImport logMassImport)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(logMassImport), IVdmManager.ActionType.UPDATE_LOG_MASSIMPORT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));	
	}

	@Override
	public void deleteLogMassImport(LogMassImport logMassImport)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(logMassImport), IVdmManager.ActionType.DELETE_LOG_MASSIMPORT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));	
	}

	@Override
	public void updateLov(LOV lov) throws Exception {
		XmlAction op = new XmlAction(createArguments(lov), IVdmManager.ActionType.UPDATE_LOV);
		String xml =httpCommunicator.executeAction(op, xstream.toXML(op));	
	}

	@Override
	public Form getForm(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_FORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Form) xstream.fromXML(xml);
	}

	@Override
	public List<Documents> getAllDocOrganigram(int parentId) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId), IVdmManager.ActionType.GET_ALL_DOC_ORGANIGRAM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public List<AlfrescoExport> getAllAlfrescoExport() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_ALFRESCO_EXPORT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AlfrescoExport>) xstream.fromXML(xml);
	}

	@Override
	public AlfrescoExport getAlfrescoExportbyId(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_ALFR_EXPORT_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AlfrescoExport) xstream.fromXML(xml);
	}

	@Override
	public AlfrescoExport saveAlfrescoExport(AlfrescoExport export)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(export), IVdmManager.ActionType.SAVE_ALFRESCO_EXPORT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AlfrescoExport) xstream.fromXML(xml);
	}

	@Override
	public void deleteAlfrescoExport(AlfrescoExport export) throws Exception {
		XmlAction op = new XmlAction(createArguments(export), IVdmManager.ActionType.DELETE_ALFRESCO_EXPORT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void mutateUser(OrganigramElementSecurity object, User user, boolean allTasks) throws Exception {
		XmlAction op = new XmlAction(createArguments(object, user, allTasks), IVdmManager.ActionType.MUTATE_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<RuleAlim> getRuleAlim(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_RULE_ALIM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<RuleAlim>) xstream.fromXML(xml);
	}

	@Override
	public RuleAlim saveRuleAlim(RuleAlim rule) throws Exception {
		XmlAction op = new XmlAction(createArguments(rule), IVdmManager.ActionType.SAVE_RULE_ALIM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (RuleAlim) xstream.fromXML(xml);
	}

	@Override
	public List<RuleAlim> getAllRuleAlim() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRuleAlim(RuleAlim rule) throws Exception {
		XmlAction op = new XmlAction(createArguments(rule), IVdmManager.ActionType.DELETE_RULE_ALIM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Tasks> getTaskWithDoc(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_TASKS_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Tasks>) xstream.fromXML(xml);
	}
	
	@Override
	public void suspendTasks(OrganigramElementSecurity object, boolean allTasks, Date dateExpiration) throws Exception {
		XmlAction op = new XmlAction(createArguments(object, allTasks, dateExpiration), IVdmManager.ActionType.MUTATE_SUSPEND);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void attribTasks(String userMail, User newUser) throws Exception {
		XmlAction op = new XmlAction(createArguments(userMail, newUser), IVdmManager.ActionType.ATTRIB_SUSPEND);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<SuspendedTasks> getSuspendedTasks() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_SUSPENDED_TASKS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<SuspendedTasks>) xstream.fromXML(xml);
	}

	@Override
	public List<HistoMutation> getHistoMutations(String userEmail) throws Exception {
		XmlAction op = new XmlAction(createArguments(userEmail), IVdmManager.ActionType.GET_HISTO_MUTATION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<HistoMutation>) xstream.fromXML(xml);
	}

	@Override
	public List<Tasks> getTaskGivenWithDoc(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_GIVEN_TASKS_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Tasks>) xstream.fromXML(xml);
	}

	@Override
	public List<Documents> getAllMails() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_MAILS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public List<Tasks> getAllTaskWithDoc() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_TASKSDOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Tasks>) xstream.fromXML(xml);
	}
	@Override
	public List<Tasks> getTaskForDoc(String email) throws Exception {
		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_TASK_FOR_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Tasks>) xstream.fromXML(xml);
	}

	@Override
	public List<FolderHierarchy> getHierarchies() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_HIERARCHIES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FolderHierarchy>) xstream.fromXML(xml);
	}

	@Override
	public void updateHierarchy(FolderHierarchy hierarchy) throws Exception {
		XmlAction op = new XmlAction(createArguments(hierarchy), IVdmManager.ActionType.UPDATE_HIERARCHY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteHierarchy(FolderHierarchy hierarchy) throws Exception {
		XmlAction op = new XmlAction(createArguments(hierarchy), IVdmManager.ActionType.DELETE_HIERARCHY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void addThesaurus(Thesaurus thesaurus) throws Exception {
		XmlAction op = new XmlAction(createArguments(thesaurus), IVdmManager.ActionType.ADD_THESAURUS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Thesaurus> getThesauruses() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_THESAURUSES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Thesaurus>) xstream.fromXML(xml);
	}

	@Override
	public void deleteThesaurus(Thesaurus object) throws Exception {
		XmlAction op = new XmlAction(createArguments(object), IVdmManager.ActionType.DELETE_THESAURUS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<UserLogs> getLogsByDocument(int documentId) throws Exception {
		XmlAction op = new XmlAction(createArguments(documentId), IVdmManager.ActionType.GET_LOGS_BY_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<UserLogs>) xstream.fromXML(xml);
	}

	@Override
	public void updateDocument(Documents document) throws Exception {
		XmlAction op = new XmlAction(createArguments(document), IVdmManager.ActionType.UPDATE_DOCUMENT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void addDocumentArchive(DocumentArchives docarc) throws Exception {
		XmlAction op = new XmlAction(createArguments(docarc), IVdmManager.ActionType.ADD_DOCUMENT_ARCHIVE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<DocumentArchives> getAllDocumentArchives() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_DOCUMENT_ARCHIVES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DocumentArchives>) xstream.fromXML(xml);
	}

	@Override
	public List<DocumentArchives> getDocumentArchivesbyArchiving(int idArchiving) throws Exception {
		XmlAction op = new XmlAction(createArguments(idArchiving), IVdmManager.ActionType.GET_DOCUMENT_ARCHIVES_BY_ARCHIVING);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DocumentArchives>) xstream.fromXML(xml);
	}

	@Override
	public List<DocumentArchives> getDocumentArchivesbyDocument(int documentId) throws Exception {
		XmlAction op = new XmlAction(createArguments(documentId), IVdmManager.ActionType.GET_DOCUMENT_ARCHIVES_BY_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DocumentArchives>) xstream.fromXML(xml);
	}

	@Override
	public Enterprise getEnterprise(int enterpriseId) throws Exception {
		XmlAction op = new XmlAction(createArguments(enterpriseId), IVdmManager.ActionType.GET_ENTERPRISE_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Enterprise) xstream.fromXML(xml);
	}

	@Override
	public List<XaklFiles> getXaklContainingDoc(int id) throws Exception {	
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_XAKL_CONTAINING_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<XaklFiles>) xstream.fromXML(xml);
	}

	@Override
	public List<ModelEnterprise> getAllModelEnterprise(int enterpriseId) throws Exception {
		XmlAction op = new XmlAction(createArguments(enterpriseId), IVdmManager.ActionType.GET_ALL_MODEL_ENTERPRISE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<ModelEnterprise>) xstream.fromXML(xml);
	}

	@Override
	public ModelEnterprise saveModelEnterprise(ModelEnterprise me) throws Exception {
		XmlAction op = new XmlAction(createArguments(me), IVdmManager.ActionType.SAVE_MODEL_ENTERPRISE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ModelEnterprise) xstream.fromXML(xml);
	}

	@Override
	public List<ModelEnterprise> getAllModelEnterprisebyFolder(int folderId) throws Exception {
		XmlAction op = new XmlAction(createArguments(folderId), IVdmManager.ActionType.GET_ALL_MODEL_ENTERPRISE_BY_FOLDER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<ModelEnterprise>) xstream.fromXML(xml);
	}

	@Override
	public void saveTypeTask(TypeTask typeTask) throws Exception {
		XmlAction op = new XmlAction(createArguments(typeTask), IVdmManager.ActionType.SAVE_TYPE_TASK);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<TypeTask> getTypeTask() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_TYPE_TASK);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<TypeTask>) xstream.fromXML(xml);
	}

	@Override
	public void deleteModeleEnterprise(ModelEnterprise modelEnterprise) throws Exception {
		XmlAction op = new XmlAction(createArguments(modelEnterprise), IVdmManager.ActionType.DELETE_MODEL_ENTERPRISE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
	
	@Override
	public String getFileContent(String filePath) throws Exception {
		XmlAction op = new XmlAction(createArguments(filePath), IVdmManager.ActionType.GET_CONTENT_FILEPATH);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	@Override
	public Group getGroupByAdress(String mail) throws Exception {
		XmlAction op = new XmlAction(createArguments(mail), IVdmManager.ActionType.ADRESS_MAIL_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Group) xstream.fromXML(xml);
	}

	@Override
	public void saveOrUpdateValidationStatus(ValidationStatus vs) throws Exception {
		XmlAction op = new XmlAction(createArguments(vs), IVdmManager.ActionType.SAVE_VALIDATION_STATUS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteValidationStatus(ValidationStatus vs) throws Exception {
		XmlAction op = new XmlAction(createArguments(vs), IVdmManager.ActionType.DELETE_VALIDATION_STATUS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<ValidationStatus> getAllValidationStatus() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_VALIDATION_STATUS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<ValidationStatus>) xstream.fromXML(xml);
	}

	@Override
	public ValidationStatus getValidationStatusbyId(int idVS) throws Exception {
		XmlAction op = new XmlAction(createArguments(idVS), IVdmManager.ActionType.GET_VALIDATION_STATUS_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ValidationStatus) xstream.fromXML(xml);
	}

	@Override
	public List<Form> getAllFormByType(String formType) throws Exception {
		XmlAction op = new XmlAction(createArguments(formType), IVdmManager.ActionType.GET_ALL_FORM_BY_TYPE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Form>) xstream.fromXML(xml);		
	}

	@Override
	public Documents getOriginalOfDocument(Documents document) throws Exception {
		XmlAction op = new XmlAction(createArguments(document), IVdmManager.ActionType.GET_ORIGINAL_OF_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (Documents) xstream.fromXML(xml) : null;
	}

	@Override
	public int saveDelegation(Delegation delegation) throws Exception {
		XmlAction op = new XmlAction(createArguments(delegation), IVdmManager.ActionType.SAVE_DELEGATION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public List<Delegation> getAllDelegation(String emailDelegator) throws Exception{
		XmlAction op = new XmlAction(createArguments(emailDelegator), IVdmManager.ActionType.GET_ALL_DELEGATION_BY_DELEGATOR);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Delegation>) xstream.fromXML(xml);
	}

	@Override
	public void removeDelegation(List<Delegation> removeDelegationList)	throws Exception {
		XmlAction op = new XmlAction(createArguments(removeDelegationList), IVdmManager.ActionType.REMOVE_DELEGATION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Delegation> getAllDelegationByDelegate(String emailDelegate) throws Exception {
		XmlAction op = new XmlAction(createArguments(emailDelegate), IVdmManager.ActionType.GET_ALL_DELEGATION_BY_DELEGATE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Delegation>) xstream.fromXML(xml);
	}
	
	@Override
	public void saveUserSuccessor(List<UserSuccessor> activeUserSuccessorList) throws Exception {
		XmlAction op = new XmlAction(createArguments(activeUserSuccessorList), IVdmManager.ActionType.SAVE_USER_SUCCESSOR);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<UserSuccessor> getUserSuccessor(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_USER_SUCCESSOR);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<UserSuccessor>) xstream.fromXML(xml);
	}

	@Override
	public void removeUserSuccessor(List<UserSuccessor> removeUserSuccessorList) throws Exception {
		XmlAction op = new XmlAction(createArguments(removeUserSuccessorList), IVdmManager.ActionType.REMOVE_USER_SUCCESSOR);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
	
	@Override
	public List<InformationsNews> getAllActualNewsInfos() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_ACTUAL_NEWS_INFOS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<InformationsNews>) xstream.fromXML(xml);
	}

	@Override
	public void removeInformationsNews(List<InformationsNews> informationsNews)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(informationsNews), IVdmManager.ActionType.REMOVE_INFORMATION_NEWS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveInformationsNews(List<InformationsNews> informationsNews)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(informationsNews), IVdmManager.ActionType.SAVE_INFORMATION_NEWS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public SubjectIdeaBox createSubjectIdeaBox(SubjectIdeaBox s)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(s), IVdmManager.ActionType.CREATE_SUBJECT_IDEA_BOX);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (SubjectIdeaBox)xstream.fromXML(xml);
	}

	@Override
	public MessageIdeaBox createMessageIdeaBox(MessageIdeaBox m)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(m), IVdmManager.ActionType.CREATE_MESSAGE_IDEA_BOX);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (MessageIdeaBox)xstream.fromXML(xml);
	}

	@Override
	public List<MessageIdeaBox> getMessageIdeaBoxBySubjectId(int idSubject)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(idSubject), IVdmManager.ActionType.GET_MESSAGE_IDEA_BOX_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MessageIdeaBox>)xstream.fromXML(xml);
	}

	@Override
	public List<SubjectIdeaBox> getAllSubjectIdeaBox() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_SUBJECT_IDEA_BOX);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<SubjectIdeaBox>)xstream.fromXML(xml);
	}

	@Override
	public SubjectIdeaBox getSubjectIdeaBoxById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_SUBJECT_IDEA_BOX_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (SubjectIdeaBox)xstream.fromXML(xml);
	}

	@Override
	public void addVoteToSubject(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.ADD_VOTE_TO_SUBJECT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Comments> getCommentToApprove() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_COMMENTS_TO_APPROVE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Comments>) xstream.fromXML(xml);
	}

	@Override
	public List<FormFieldValue> getFormFieldValueByDoc(int docId,int formId)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(docId,formId), IVdmManager.ActionType.GET_FORM_FIELD_VALUE_BY_DOC_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormFieldValue>) xstream.fromXML(xml);
	}

	@Override
	public String createBarCodeImage(Documents d,String format, String data)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(d,format,data), IVdmManager.ActionType.CREATE_BAR_CODE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	@Override
	public List<String> parsePDFForm(String filePDFForm) throws Exception {
		XmlAction op = new XmlAction(createArguments(filePDFForm), IVdmManager.ActionType.PARSE_PDF_FORM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<String>) xstream.fromXML(xml);
	}

	@Override
	public SurveyIdeaBox createSurvey(SurveyIdeaBox survey) throws Exception {
		XmlAction op = new XmlAction(createArguments(survey), IVdmManager.ActionType.CREATE_SURVEY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (SurveyIdeaBox) xstream.fromXML(xml);
	}

	@Override
	public SurveyIdeaBox getSurveyBySubjectId(int idSubject) throws Exception {
		XmlAction op = new XmlAction(createArguments(idSubject), IVdmManager.ActionType.GET_SURVEY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (SurveyIdeaBox) xstream.fromXML(xml);
	}

	@Override
	public SubjectIdeaBox updateSubject(SubjectIdeaBox subject)	throws Exception {
		XmlAction op = new XmlAction(createArguments(subject), IVdmManager.ActionType.UPDATE_SUBJECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (SubjectIdeaBox) xstream.fromXML(xml);
	}

	@Override
	public String createVoteSurveyIdeaBox(SurveyIdeaBox survey,ChoiceIdeaBox choice, User u) throws Exception {
		XmlAction op = new XmlAction(createArguments(survey,choice,u), IVdmManager.ActionType.CREATE_VOTE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	@Override
	public Map<String, Integer> getResultBySurvey(SurveyIdeaBox survey) throws Exception{
		XmlAction op = new XmlAction(createArguments(survey), IVdmManager.ActionType.GET_RESULT_SURVEY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Map<String,Integer>) xstream.fromXML(xml);
	}

	@Override
	public List<InformationsNews> getAllNewsInfos() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_NEWS_INFO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<InformationsNews>) xstream.fromXML(xml);
	}

	@Override
	public void updateFolderBackground(Tree folder, String background) throws Exception {
		XmlAction op = new XmlAction(createArguments(folder,background), IVdmManager.ActionType.UPDATE_BACKGROUND_FOLDER);
		httpCommunicator.executeAction(op, xstream.toXML(op));
		
	}

	@Override
	public ScanMetaObject generateScanMetaObjectFromZip(InputStream stream) throws Exception {
		XmlAction op = new XmlAction(createArguments(stream), IVdmManager.ActionType.GENERATE_SCAN_OBJECT_FROM_ZIP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ScanMetaObject) xstream.fromXML(xml);
	}

	@Override
	public void transfertToGroup(Group fromGroup, Group toGroup) throws Exception {
		XmlAction op = new XmlAction(createArguments(fromGroup,toGroup), IVdmManager.ActionType.TRANSFERT_GROUP);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
	
	@Override
	public void transfertToEnterprise(Enterprise fromEnterprise, Enterprise toEnterprise) throws Exception {
		XmlAction op = new XmlAction(createArguments(fromEnterprise,toEnterprise), IVdmManager.ActionType.TRANSFERT_ENTERPRISE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
	
	@Override
	public void addAbsence(Absence absence) throws Exception {
		XmlAction op = new XmlAction(createArguments(absence), IVdmManager.ActionType.ADD_ABSENCE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void removeAbsence(Absence absence) throws Exception {
		XmlAction op = new XmlAction(createArguments(absence), IVdmManager.ActionType.REMOVE_ABSENCE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<Absence> getAbsences(Integer userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_ABSENCES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Absence>) xstream.fromXML(xml);
	}

	@Override
	public void treatImage(TreatmentImageObject treatment, Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(treatment, doc), IVdmManager.ActionType.TREAT_IMAGE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public ScanMetaObject generateScanMetaObjectFromFiles(List<InputStream> files, List<String> names, List<Integer> sizes) throws Exception {
		XmlAction op = new XmlAction(createArguments(files, names, sizes), IVdmManager.ActionType.GENERATE_SCAN_OBJECT_FROM_FILES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ScanMetaObject) xstream.fromXML(xml);
	}

	@Override
	public void saveScanDocumentType(ScanDocumentType docType) throws Exception {
		XmlAction op = new XmlAction(createArguments(docType), IVdmManager.ActionType.SAVE_SCAN_DOCUMENT_TYPE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateScanDocumentType(ScanDocumentType docType)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(docType), IVdmManager.ActionType.UPDATE_SCAN_DOCUMENT_TYPE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<ScanDocumentType> getAllScanDocumentType() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_SCAN_DOCUMENT_TYPES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<ScanDocumentType>) xstream.fromXML(xml);
	}

	@Override
	public void deleteScanDocumentType(ScanDocumentType docType)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(docType), IVdmManager.ActionType.DELETE_SCAN_DOCUMENT_TYPE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
	
	@Override
	public InputStream getDocumentStream(int docId) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId), IVdmManager.ActionType.LOAD_DOCUMENT_STREAM);
		return httpCommunicator.executeActionAsStream(op, xstream.toXML(op), false);
	}

	@Override
	public void saveScanKeyWord(ScanKeyWord keyword) throws Exception {
		XmlAction op = new XmlAction(createArguments(keyword), IVdmManager.ActionType.SAVE_SCAN_KEYWORD);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateScanKeyWord(ScanKeyWord keyword) throws Exception {
		XmlAction op = new XmlAction(createArguments(keyword), IVdmManager.ActionType.UPDATE_SCAN_KEYWORD);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<ScanKeyWord> getAllScanKeyWordsbyDocType(ScanDocumentType docType) throws Exception {
		XmlAction op = new XmlAction(createArguments(docType), IVdmManager.ActionType.GET_ALL_SCAN_KEYWORDS_BY_DOC_TYPE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<ScanKeyWord>) xstream.fromXML(xml);
	}

	@Override
	public void deleteScanKeyWord(ScanKeyWord keyword) throws Exception {
		XmlAction op = new XmlAction(createArguments(keyword), IVdmManager.ActionType.DELETE_SCAN_KEYWORD);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public LOV getLov(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_LOV);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (LOV) xstream.fromXML(xml);
	}

	@Override
	public List<MetadataLink> getMetadatas(IObject item, LinkType type, boolean fetchParent) throws Exception {
		XmlAction op = new XmlAction(createArguments(item, type, fetchParent), IVdmManager.ActionType.GET_METADATAS_BY_ITEM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MetadataLink>) xstream.fromXML(xml);
	}

	@Override
	public List<Form> getMetadatas() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_METADATAS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Form>) xstream.fromXML(xml);
	}

	@Override
	public Integer saveMetadataLink(MetadataLink link, boolean deleteOldLink) throws Exception {
		XmlAction op = new XmlAction(createArguments(link, deleteOldLink), IVdmManager.ActionType.SAVE_METADATA_LINK);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public void saveMetadataValues(int itemId, HashMap<Form, List<FormFieldValue>> metadataValues, LinkType type, boolean applyToChild) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, metadataValues, type, applyToChild), IVdmManager.ActionType.SAVE_METADATA_VALUES);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<FormFieldValue> getMetadataValues(MetadataLink link, Integer docId) throws Exception {
		XmlAction op = new XmlAction(createArguments(link, docId), IVdmManager.ActionType.GET_METADATA_VALUES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormFieldValue>) xstream.fromXML(xml);
	}

	@Override
	public void updateMetadataValues(HashMap<Form, List<FormFieldValue>> metadataValues) throws Exception {
		XmlAction op = new XmlAction(createArguments(metadataValues), IVdmManager.ActionType.UPDATE_METADATA_VALUES);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Deed getDeedInformationsFromMetadata(Documents doc, int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc, userId), IVdmManager.ActionType.IS_FORM_COMPLETE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Deed) xstream.fromXML(xml);
	}

	@Override
	public void updatePage(DocPages page) throws Exception {
		XmlAction op = new XmlAction(createArguments(page), IVdmManager.ActionType.UPDATE_PAGE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveAkladSettings(AkladSettings settings) throws Exception {
		XmlAction op = new XmlAction(createArguments(settings), IVdmManager.ActionType.SAVE_AKLAD_SETTINGS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public AkladSettings getAkladSettingbyUser(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_AKLAD_SETTING);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AkladSettings) xstream.fromXML(xml);
	}

	@Override
	public ScanMetaObject generateScanMetaObjectFromZip(String path)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(path), IVdmManager.ActionType.GENERATE_SCAN_OBJECT_FROM_ZIP2);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ScanMetaObject) xstream.fromXML(xml);
	}

	@Override
	public User getRequestUser() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_REQUEST_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (User) xstream.fromXML(xml);
	}

	@Override
	public List<Documents> pdfToDocs(String name, String path, int size) throws Exception {
		XmlAction op = new XmlAction(createArguments(name, path, size), IVdmManager.ActionType.PDF_TO_DOCS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public Log<? extends ILog> addOrUpdateActionLog(Log<? extends ILog> log) throws Exception {
		XmlAction op = new XmlAction(createArguments(log), IVdmManager.ActionType.ADD_ACTION_LOG);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Log<? extends ILog>) xstream.fromXML(xml);
	}

	@Override
	public List<Log<? extends ILog>> getActionLogs(LogType type) throws Exception {
		XmlAction op = new XmlAction(createArguments(type), IVdmManager.ActionType.GET_ACTION_LOGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Log<? extends ILog>>) xstream.fromXML(xml);
	}

	@Override
	public List<IObject> findChildrenEnterprise(Enterprise enterprise, Tree parentFolder, boolean lightWeight) throws Exception {
		XmlAction op = new XmlAction(createArguments(enterprise, parentFolder, lightWeight), IVdmManager.ActionType.GET_ENTERPRISE_FOLDER_CHILDS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		if (xml.isEmpty()) {
			return new ArrayList<IObject>();
		}
		return (List<IObject>) xstream.fromXML(xml);
	}

	@Override
	public String createWordFromPdf(Documents document) throws Exception {
		XmlAction op = new XmlAction(createArguments(document), IVdmManager.ActionType.CREATE_WORD_FROM_PDF);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	@Override
	public List<User> searchUsers(String filter) throws Exception {
		XmlAction op = new XmlAction(createArguments(filter), IVdmManager.ActionType.SEARCH_USERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		if (xml.isEmpty()) {
			return new ArrayList<User>();
		}
		return (List<User>) xstream.fromXML(xml);
	}

	@Override
	public ItemValidation manageItemValidation(ItemValidation validation, ManagerAction action) throws Exception {
		XmlAction op = new XmlAction(createArguments(validation, action), IVdmManager.ActionType.MANAGE_ITEM_VALIDATION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ItemValidation) xstream.fromXML(xml);
	}

	@Override
	public List<ItemValidation> getItemValidations(int itemId, ItemType type) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, type), IVdmManager.ActionType.GET_ITEM_VALIDATIONS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		if (xml.isEmpty()) {
			return new ArrayList<ItemValidation>();
		}
		return (List<ItemValidation>) xstream.fromXML(xml);
	}

	@Override
	public boolean hasForms(IObject item, LinkType type) throws Exception {
		XmlAction op = new XmlAction(createArguments(item, type), IVdmManager.ActionType.HAS_FORMS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public boolean checkIfFormsAreCompleted(IObject item, LinkType type) throws Exception {
		XmlAction op = new XmlAction(createArguments(item, type), IVdmManager.ActionType.CHECK_IF_FORMS_ARE_COMPLETED);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public PermissionItem managePermissionItem(IObject item, PermissionItem permissionItem, ManagerAction action, ShareType shareType, List<Permission> permissions, boolean savePermission) throws Exception {
		XmlAction op = new XmlAction(createArguments(item, permissionItem, action, shareType, permissions, savePermission), IVdmManager.ActionType.MANAGE_ITEM_PERMISSION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (PermissionItem) xstream.fromXML(xml);
	}

	@Override
	public Tree saveDirectory(Tree folder, List<Integer> hierarchies) throws Exception {
		XmlAction op = new XmlAction(createArguments(folder, hierarchies), IVdmManager.ActionType.SAVE_DIRECTORY_WITH_HIER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Tree) xstream.fromXML(xml);
	}

	@Override
	public List<User> getUserEnterprise(int enterpriseId, TypeUser typeUser) throws Exception {
		XmlAction op = new XmlAction(createArguments(enterpriseId, typeUser), IVdmManager.ActionType.GET_USER_ENTERPRISE_BY_TYPE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		if (xml.isEmpty()) {
			return new ArrayList<User>();
		}
		return (List<User>) xstream.fromXML(xml);
	}

	@Override
	public Tree getDirectory(int folderId, ItemTreeType itemTreeType, Boolean isDeleted) throws Exception {
		XmlAction op = new XmlAction(createArguments(folderId, itemTreeType, isDeleted), IVdmManager.ActionType.GET_DIRECTORY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Tree) xstream.fromXML(xml);
	}

	@Override
	public List<IObject> getItems(Integer userId, Integer parentId, PermissionItem permissionParent, ItemTreeType itemTreeType, IObject.ItemType type, boolean loadChilds) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId, parentId, permissionParent, itemTreeType, type, loadChilds), IVdmManager.ActionType.GET_ITEMS_NOT_DELETED_AND_ACTIVATED);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		if (xml.isEmpty()) {
			return new ArrayList<IObject>();
		}
		return (List<IObject>) xstream.fromXML(xml);
	}

	@Override
	public List<IObject> getItems(Integer userId, Integer parentId, PermissionItem permissionParent, ItemTreeType treeItemType, IObject.ItemType type, Boolean isDeleted, Boolean isActivated, boolean checkPermission, boolean loadChilds) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId, parentId, permissionParent, treeItemType, type, isDeleted, isActivated, checkPermission, loadChilds), IVdmManager.ActionType.GET_ITEMS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		if (xml.isEmpty()) {
			return new ArrayList<IObject>();
		}
		return (List<IObject>) xstream.fromXML(xml);
	}

	@Override
	public Tree getEnterpriseRootFolder(Integer userId, Integer enterpriseId, IObject.ItemType type, Boolean isDeleted, Boolean isActivated, boolean checkPermission, boolean loadChilds) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId, enterpriseId, type, isDeleted, isActivated, checkPermission, loadChilds), IVdmManager.ActionType.GET_ENTERPRISE_ROOT_FOLDER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Tree) xstream.fromXML(xml);
	}

	@Override
	public List<Permission> getPermissions(int permissionItemId, ShareType type, boolean lightWeight) throws Exception {
		XmlAction op = new XmlAction(createArguments(permissionItemId, type, lightWeight), IVdmManager.ActionType.GET_PERMISSIONS_BY_TYPE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Permission>) xstream.fromXML(xml);
	}

	@Override
	public PermissionItem getPermissionItem(String hash) throws Exception {
		XmlAction op = new XmlAction(createArguments(hash), IVdmManager.ActionType.GET_PERMISSIONITEM_BY_HASH);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (PermissionItem) xstream.fromXML(xml);
	}

	@Override
	public SearchResult searchItems(SearchMetadata searchMetadata, IObject.ItemType type) throws Exception {
		XmlAction op = new XmlAction(createArguments(searchMetadata, type), IVdmManager.ActionType.SEARCH_ITEMS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (SearchResult) xstream.fromXML(xml);
	}
	
	@Override
	public IObject getItem(int itemId, IObject.ItemType type, ItemTreeType itemTreeType, Boolean isDeleted, boolean checkPermission, boolean loadParent, boolean lightWeight) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, type, itemTreeType, isDeleted, checkPermission, loadParent, lightWeight), IVdmManager.ActionType.GET_ITEM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (IObject) xstream.fromXML(xml);
	}

	@Override
	public Versions getLastVersion(int docId) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId), IVdmManager.ActionType.GET_LAST_VERSION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Versions) xstream.fromXML(xml);
	}

	@Override
	public void saveMailServers(List<MailServer> mailServers) throws Exception {
		XmlAction op = new XmlAction(createArguments(mailServers), IVdmManager.ActionType.SAVE_MAIL_SERVERS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<MailServer> getMailServers() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_MAIL_SERVERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MailServer>) xstream.fromXML(xml);
	}

	@Override
	public List<Form> getAllFormByType(FormType formType) throws Exception {
		XmlAction op = new XmlAction(createArguments(formType), IVdmManager.ActionType.GET_ALL_FORMS_BY_TYPE2);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Form>) xstream.fromXML(xml);
	}

	@Override
	public List<OrbeonFormSection> getOrbeonFormSections(Form selectedObject) throws Exception {
		XmlAction op = new XmlAction(createArguments(selectedObject), IVdmManager.ActionType.GET_ORBEON_SECTIONS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<OrbeonFormSection>) xstream.fromXML(xml);
	}

	@Override
	public Documents createOrbeonDocument(int parentId, String name) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId, name), IVdmManager.ActionType.CREATE_ORBEON_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public OrbeonFormInstance getOrbeonFormInstance(Form form, Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(form, doc), IVdmManager.ActionType.GET_ORBEON_INSTANCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (OrbeonFormInstance) xstream.fromXML(xml);
	}

	@Override
	public OrganigramElement getOrganigramElement(int id, boolean fill) throws Exception {
		XmlAction op = new XmlAction(createArguments(id, fill), IVdmManager.ActionType.GET_ORGA_ELEM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (OrganigramElement) xstream.fromXML(xml);
	}

	@Override
	public void updateOrbeonInstance(OrbeonFormInstance instance) throws Exception {
		XmlAction op = new XmlAction(createArguments(instance), IVdmManager.ActionType.UPDATE_ORBEON_INSTANCE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public FileType getFileType(int idFileType) throws Exception {
		XmlAction op = new XmlAction(createArguments(idFileType), IVdmManager.ActionType.GET_FILE_TYPE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FileType) xstream.fromXML(xml);
	}

	@Override
	public List<String> loadOrbeonCheckBoxes(int linkedFormId) throws Exception {
		XmlAction op = new XmlAction(createArguments(linkedFormId), IVdmManager.ActionType.LOAD_ORBEON_SERVLET);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<String>) xstream.fromXML(xml);
	}

	@Override
	public void sendOrbeonNotification(Documents doc, User user, int typeMail) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc, user, typeMail), IVdmManager.ActionType.SEND_ORBEON_NOTIF);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Documents getOrbeonDocument(int parentId) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId), IVdmManager.ActionType.GET_ORBEON_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public void deleteElement(OrganigramElement elem) throws Exception {
		XmlAction op = new XmlAction(createArguments(elem), IVdmManager.ActionType.DELETE_ORGA_ELEM);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteElementSecurity(OrganigramElementSecurity elem) throws Exception {
		XmlAction op = new XmlAction(createArguments(elem), IVdmManager.ActionType.DELETE_ORGA_ELEM_SECU);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Versions saveVersion(Versions docVersion) throws Exception {
		XmlAction op = new XmlAction(createArguments(docVersion), IVdmManager.ActionType.SAVE_VERSION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Versions) xstream.fromXML(xml);
	}

	@Override
	public void updateOrbeonStampSection(Form form, List<String>  sections, String stampPath, String instanceId, String dataXml) throws Exception {
		XmlAction op = new XmlAction(createArguments(form, sections, stampPath, instanceId, dataXml), IVdmManager.ActionType.UPDATE_FORM_STAMP_SECTION);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<OrganigramElementSecurity> getDirectSuperiors(User user, int levelSup, Fonction fonctionSup) throws Exception {
		XmlAction op = new XmlAction(createArguments(user, levelSup, fonctionSup), IVdmManager.ActionType.GET_DIRECT_SUPERIORS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<OrganigramElementSecurity>) xstream.fromXML(xml);
	}

	@Override
	public Tree getDirectoryByNameParent(String name, Integer parentId) throws Exception {
		XmlAction op = new XmlAction(createArguments(name, parentId), IVdmManager.ActionType.GET_DIR_NAME_PARENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Tree) xstream.fromXML(xml);
	}

	@Override
	public OrbeonFormInstance getFormInstance(String instanceId) throws Exception {
		XmlAction op = new XmlAction(createArguments(instanceId), IVdmManager.ActionType.GET_FORM_INSTANCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (OrbeonFormInstance) xstream.fromXML(xml);
	}

	@Override
	public List<Documents> getDocumentsByUser(TypeMethodDocument type, User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(type, user), IVdmManager.ActionType.GET_DOCUMENTS_BY_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public Tree getDirectoryByHierarchyItemAndRoot(FolderHierarchyItem hierarchyItem, Tree root) throws Exception {
		XmlAction op = new XmlAction(createArguments(hierarchyItem, root), IVdmManager.ActionType.GET_DIRECTORY_BY_HIERARCHY_ITEM_AND_ROOT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Tree) xstream.fromXML(xml);
	}

	@Override
	public FolderHierarchy getHierarchy(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_HIERARCHY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (FolderHierarchy) xstream.fromXML(xml);
	}

	@Override
	public List<String> getColumnsbyRequest(SourceConnection connection, String request) throws Exception {
		XmlAction op = new XmlAction(createArguments(connection, request), IVdmManager.ActionType.GET_COLUMNS_BY_REQUEST);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<String>) xstream.fromXML(xml);
	}

	@Override
	public List<Map<String, String>> getResultbyRequest(SourceConnection connection, String request) throws Exception {
		XmlAction op = new XmlAction(createArguments(connection, request), IVdmManager.ActionType.GET_RESULT_BY_REQUEST);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Map<String, String>>) xstream.fromXML(xml);
	}

	@Override
	public InputStream createMetadataPDFFile(IObject item, Map<Form, List<FormFieldValue>> values) throws Exception {
		XmlAction op = new XmlAction(createArguments(item, values), IVdmManager.ActionType.CREATE_METADATA_PDF);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (InputStream) xstream.fromXML(xml);
	}

	@Override
	public List<FormFieldValue> getFormValuesFromDataSource(Form form, List<FormFieldValue> filterKeys, Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(form, filterKeys, doc), IVdmManager.ActionType.GET_FORM_VALUES_FROM_SOURCE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<FormFieldValue>) xstream.fromXML(xml);
	}

	@Override
	public User getUserBySession(String sessionId) throws Exception {
		XmlAction op = new XmlAction(createArguments(sessionId), IVdmManager.ActionType.GET_USER_BY_SESSION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (User) xstream.fromXML(xml);
	}
	
	//To delete


//	@Override
//	public List<Tree> getItems(String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_ITEMS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<Tree>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<Documents> getAllDocs() throws Exception {
//		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_DOCS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<Documents>) xstream.fromXML(xml);
//	}

//	@Override
//	public void editThisDoc(Documents doc) throws Exception {
//		XmlAction op = new XmlAction(createArguments(doc), IVdmManager.ActionType.EDIT_DOCS);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public List<Versions> getAllVersions() throws Exception {
//		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_VERSION);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<Versions>) xstream.fromXML(xml);
//	}

//	@Override
//	public IObject getRootParent(int id, String type) throws Exception {
//		XmlAction op = new XmlAction(createArguments(id, type), IVdmManager.ActionType.GET_ROOT_PARENT);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (IObject) xstream.fromXML(xml);
//	}

//	@Override
//	public List<Tree> getAllFiles() throws Exception {
//		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_FILES);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<Tree>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<Documents> getDocsFromParent(int id) throws Exception {
//		XmlAction op = new XmlAction(createArguments(id), IVdmManager.ActionType.GET_DOCS_FROM_PARENT);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<Documents>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<Documents> getScanDocs(User user) throws Exception {
//		XmlAction op = new XmlAction(createArguments(user), IVdmManager.ActionType.GET_SCAN_DOCS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<Documents>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<IObject> getAllFilesAndDocs() throws Exception {
//		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_FOLDERS_DOCS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public void pushToAklaBox(List<Documents> documents, String sourceURL) throws Exception {
//		XmlAction op = new XmlAction(createArguments(documents, sourceURL), IVdmManager.ActionType.PUSH_TO_AKLABOX);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}
	
//	@Override
//	public List<Documents> getAllDocsbyFileType(int typeId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(typeId), IVdmManager.ActionType.GET_ALL_DOC_BY_FILETYPE);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<Documents>) xstream.fromXML(xml);		
//	}

//	@Override
//	public List<Tree> getEnterpriseFolders() throws Exception {
//		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ENTERPRISE_FOLDERS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<Tree>) xstream.fromXML(xml);
//	}

//	@Override
//	public Documents updateDocStatus(Documents doc) throws Exception {
//		XmlAction op = new XmlAction(createArguments(doc), IVdmManager.ActionType.UPDATE_DOC_STATUS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (Documents) xstream.fromXML(xml);
//	}

//	@Override
//	public List<Tree> recursiveDirectory(IObject object) throws Exception {
//		XmlAction op = new XmlAction(createArguments(object), IVdmManager.ActionType.RECURSIVE_FOLDERS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<Tree>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<IObject> getContents(int id, String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(id, email), IVdmManager.ActionType.GET_CONTENTS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public Tree getItem(Tree folder) throws Exception {
//		XmlAction op = new XmlAction(createArguments(folder), IVdmManager.ActionType.GET_CHILD);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (Tree) xstream.fromXML(xml);
//	}

//	@Override
//	public List<IObject> getGroupShare(int groupId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(groupId), IVdmManager.ActionType.GET_GROUP_SHARE);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<IObject> getGroupSharePerUser(String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_GROUP_SHARE_BY_USER);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<IObject> getPublicTree() throws Exception {
//		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_PUBLIC);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<IObject> getSubItems(int folderId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(folderId), IVdmManager.ActionType.GET_SUB_ITEMS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<FolderShare> getExternalShare(int docId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(docId), IVdmManager.ActionType.GET_EXTERNAL_SHARE);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<FolderShare>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<IObject> getSharedByMe(String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.SHARED_ME);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public FolderShare checkIfShared(String email, int id, String type) throws Exception {
//		XmlAction op = new XmlAction(createArguments(email, id, type), IVdmManager.ActionType.CHECK_SHARE);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (FolderShare) xstream.fromXML(xml);
//	}

//	@Override
//	public List<IObject> getSubShare(int id, String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(id, email), IVdmManager.ActionType.USER_SUB_SHARE);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public void saveEnterpriseFolders(Tree folder, List<Enterprise> enterprises) throws Exception {
//		XmlAction op = new XmlAction(createArguments(folder, enterprises), IVdmManager.ActionType.SAVE_ENTERPRISE_FOLDER);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public List<UserEnterprise> getUserEnterprise(Enterprise enterprise) throws Exception {
//		XmlAction op = new XmlAction(createArguments(enterprise), IVdmManager.ActionType.GET_USER_ENTERPRISE);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<UserEnterprise>) xstream.fromXML(xml);
//	}

//	@Override
//	public Tree savePrivateFolders(Tree folder, List<FolderHierarchy> hierarchies) throws Exception {
//		XmlAction op = new XmlAction(createArguments(folder, hierarchies), IVdmManager.ActionType.SAVE_PRIVATE_FOLDERS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (Tree) xstream.fromXML(xml);
//	}
	
	
	
	


//	@Override
//	public void editDirectoryInfo(Tree tree, String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(tree, email), IVdmManager.ActionType.EDIT_DIR);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public void editDocumentInfo(Documents doc, String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(doc, email), IVdmManager.ActionType.EDIT_DOC);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public List<FolderShare> getDocShares(int docId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(docId), IVdmManager.ActionType.GET_DOC_SHARES);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<FolderShare>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<Tree> getFromLast(String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.SELECT_REVERSE_DIR);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<Tree>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<FolderShare> getFolderShares(int folderId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(folderId), IVdmManager.ActionType.GET_FOLDER_SHARE);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<FolderShare>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<FolderShare> getSharedDocs(int docId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(docId), IVdmManager.ActionType.GET_SHARED_DOCS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<FolderShare>) xstream.fromXML(xml);
//	}

//	@Override
//	public GroupSharedName getPin(String pinCode) throws Exception {
//		XmlAction op = new XmlAction(createArguments(pinCode), IVdmManager.ActionType.CHECK_PIN);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (GroupSharedName) xstream.fromXML(xml);
//	}

//	@Override
//	public GroupShare getEmail(GroupSharedName groupSharedName, String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(groupSharedName, email), IVdmManager.ActionType.CHECK_MAIL);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (GroupShare) xstream.fromXML(xml);
//	}

//	@Override
//	public List<GroupShare> saveEmailLog(int gsnId, String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(gsnId, email), IVdmManager.ActionType.SAVE_MAIL);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<GroupShare>) xstream.fromXML(xml);
//	}

//	@Override
//	public void saveGroupWorkspace(String email, String groupName, String groupDescription, String pinCode, List<User> emails, List<Documents> docs, boolean canRead, boolean isAnonymous, boolean allowAds) throws Exception {
//		XmlAction op = new XmlAction(createArguments(email, groupName, groupDescription, pinCode, emails, docs, canRead, isAnonymous, allowAds), IVdmManager.ActionType.SAVE_GROUP_WORKSPACE);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public GroupSharedName getGroupSharedInfo(int gsnId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(gsnId), IVdmManager.ActionType.GET_GSN);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (GroupSharedName) xstream.fromXML(xml);
//	}

//	@Override
//	public List<Documents> getGroupDocs(int gsnId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(gsnId), IVdmManager.ActionType.GET_GSN_DOCS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<Documents>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<GroupShare> getGroupMails(int gsnId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(gsnId), IVdmManager.ActionType.GET_GSN_MAILS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<GroupShare>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<GroupSharedName> getListGroupShared(String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_LIST_GSN);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<GroupSharedName>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<GroupShare> saveLogout(int gsnId, String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(gsnId, email), IVdmManager.ActionType.GO_OFFLINE);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<GroupShare>) xstream.fromXML(xml);
//	}

//	@Override
//	public void updateGroupWorkspace(int gsnId, String email, String groupName, String groupDescription, String pinCode, List<User> emails, List<Documents> docs, boolean canRead, boolean isAnonymous, boolean allowAds) throws Exception {
//		XmlAction op = new XmlAction(createArguments(gsnId, email, groupName, groupDescription, pinCode, emails, docs, canRead, isAnonymous, allowAds), IVdmManager.ActionType.UPDATE_WORKSPACE);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public List<FileShared> getFileShared(int fileId, String fileType) throws Exception {
//		XmlAction op = new XmlAction(createArguments(fileId, fileType), IVdmManager.ActionType.GET_FILE_SHARE);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<FileShared>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<IObject> getFilesEnterprisePerUser(String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_FOLDERS_ENTERPRISE_PER_USER);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<IObject> getFilesEnterprise(int enterpriseId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(enterpriseId), IVdmManager.ActionType.GET_FOLDERS_PER_ENTERPRISE);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public void updateEnterpriseFolder(Tree folder, List<Enterprise> enterprise, String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(folder, enterprise, email), IVdmManager.ActionType.UPDATE_ENTERPRISE_FOLDERS);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public List<IObject> getChildOfShared(int folderId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(folderId), IVdmManager.ActionType.GET_CHILD_SHARED);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<User> getAllUsersSharedPerDocument(int docId, String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(docId, email), IVdmManager.ActionType.GET_ALL_USER_SHARED);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<User>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<Documents> getAllPublicDocuments(String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_PUBLIC_DOCUMENTS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<Documents>) xstream.fromXML(xml);
//	}

	/*
	 * @Override public List<Tree> getAllFilesbyUser(int userId) throws
	 * Exception { XmlAction op = new XmlAction(createArguments(userId),
	 * IVdmManager.ActionType.GET_ALL_FILES_BY_USER); String xml =
	 * httpCommunicator.executeAction(op, xstream.toXML(op)); return
	 * (List<Tree>) xstream.fromXML(xml); }
	 */

//	@Override
//	public void editPreEmption(List<FolderShare> fs) throws Exception {
//		XmlAction op = new XmlAction(createArguments(fs), IVdmManager.ActionType.EDIT_PREEMPTION);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public void createPersonalUser(User user, String url, Boolean isAndroid) throws Exception {
//		XmlAction op = new XmlAction(createArguments(user, url, isAndroid), IVdmManager.ActionType.CREATE_PERSONAL_USER);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public List<FolderShare> getAllExternalShare() throws Exception {
//		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_ALL_EXTERNAL);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<FolderShare>) xstream.fromXML(xml);
//	}

//	@Override
//	public void updateFolderShare(FolderShare fs) throws Exception {
//		XmlAction op = new XmlAction(createArguments(fs), IVdmManager.ActionType.UPDATE_FOLDER_SHARE);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public List<IObject> getDirGroupShare(int groupId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(groupId), IVdmManager.ActionType.GET_DIR_GROUP_SHARE);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<IObject> getPublicDir() throws Exception {
//		XmlAction op = new XmlAction(createArguments(), IVdmManager.ActionType.GET_PUBLIC_DIR);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<IObject> getDirUserShare(String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.GET_DIR_USER_SHARE);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<IObject> getDirsEnterprise(int enterpriseId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(enterpriseId), IVdmManager.ActionType.GET_DIR_ENTREPRISE);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}

//	@Override
//	public List<FolderShare> getFolderExternalShare(int folderId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(folderId), IVdmManager.ActionType.GET_FOLDER_EXTERNAL);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<FolderShare>) xstream.fromXML(xml);
//	}

//	@Override
//	public void updateFolder(Tree tree) throws Exception {
//		XmlAction op = new XmlAction(createArguments(tree), IVdmManager.ActionType.UPDATE_FOLDER);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public FolderShare getDocShareByUserDocId(int userId, int docId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(userId, docId), IVdmManager.ActionType.GET_DOC_SHARE_BY_USER_DOC_ID);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (FolderShare) xstream.fromXML(xml);
//	}

//	@Override
//	public void deleteFolderShare(FolderShare fs) throws Exception {
//		XmlAction op = new XmlAction(createArguments(fs), IVdmManager.ActionType.DELETE_FOLDER_SHARE);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public void saveFolderShare(FolderShare fs) throws Exception {
//		XmlAction op = new XmlAction(createArguments(fs), IVdmManager.ActionType.SAVE_FOLDER_SHARE);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public List<FileShared> getExternalShareByUserId(int userId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_EXTERNAL_SHARE_BY_USERID);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<FileShared>) xstream.fromXML(xml);
//	}

//	@Override
//	public FolderShare getFolderShareByUserDocId(int userId, int folderId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(userId, folderId), IVdmManager.ActionType.GET_FOLDER_SHARE_BY_USER_FOLDER_ID);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (FolderShare) xstream.fromXML(xml);
//	}

//	@Override
//	public void saveSuspendDocument(SuspendDocument suspendDoc) throws Exception {
//		XmlAction op = new XmlAction(createArguments(suspendDoc), IVdmManager.ActionType.SAVE_SUSPEND_DOCUMENT);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}
//
//	@Override
//	public void updateSuspendDocument(SuspendDocument suspendDoc) throws Exception {
//		XmlAction op = new XmlAction(createArguments(suspendDoc), IVdmManager.ActionType.UPDATE_SUSPEND_DOCUMENT);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}
//
//	@Override
//	public void deleteSuspendDocument(SuspendDocument suspendDoc) throws Exception {
//		XmlAction op = new XmlAction(createArguments(suspendDoc), IVdmManager.ActionType.DELETE_SUSPEND_DOCUMENT);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}
//
//	@Override
//	public List<SuspendDocument> getAllSuspendDocuments(int userId) throws Exception {
//		XmlAction op = new XmlAction(createArguments(userId), IVdmManager.ActionType.GET_ALL_SUSPEND_DOCUMENT);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<SuspendDocument>) xstream.fromXML(xml);
//	}
//
//	@Override
//	public SuspendDocument getSuspendDocumentByDocId(int docId, String type) throws Exception {
//		XmlAction op = new XmlAction(createArguments(docId, type), IVdmManager.ActionType.GET_SUSPEND_DOCUMENT_BY_ID);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (SuspendDocument) xstream.fromXML(xml);
//	}

//	@Override
//	public void saveRMFolder(Tree tree, RMFolder folder, List<Enterprise> enterprises, List<RMField> rmFields) throws Exception {
//		XmlAction op = new XmlAction(createArguments(tree, folder, enterprises, rmFields), IVdmManager.ActionType.SAVE_RM_FOLDER);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}

//	@Override
//	public List<IObject> getTrashedItems(String email) throws Exception {
//		XmlAction op = new XmlAction(createArguments(email), IVdmManager.ActionType.TRASHED_ITEMS);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<IObject>) xstream.fromXML(xml);
//	}
}