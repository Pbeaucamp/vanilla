package bpm.vanilla.cmis;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.commons.BasicPermissions;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderContainer;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.data.ObjectParentData;
import org.apache.chemistry.opencmis.commons.data.PermissionMapping;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.PermissionDefinition;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.CapabilityAcl;
import org.apache.chemistry.opencmis.commons.enums.CapabilityChanges;
import org.apache.chemistry.opencmis.commons.enums.CapabilityContentStreamUpdates;
import org.apache.chemistry.opencmis.commons.enums.CapabilityJoin;
import org.apache.chemistry.opencmis.commons.enums.CapabilityOrderBy;
import org.apache.chemistry.opencmis.commons.enums.CapabilityQuery;
import org.apache.chemistry.opencmis.commons.enums.CapabilityRenditions;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.enums.SupportedPermissions;
import org.apache.chemistry.opencmis.commons.enums.Updatability;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNameConstraintViolationException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisStorageException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisStreamNotSupportedException;
import org.apache.chemistry.opencmis.commons.impl.MimeTypes;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AclCapabilitiesDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AllowableActionsImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.CreatablePropertyTypesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.NewTypeSettableAttributesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderContainerImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectInFolderListImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectParentDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PartialContentStreamImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PermissionDefinitionDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PermissionMappingDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyBooleanImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDateTimeImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIdImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIntegerImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyStringImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.RepositoryCapabilitiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.RepositoryInfoImpl;
import org.apache.chemistry.opencmis.commons.impl.server.ObjectInfoImpl;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.ObjectInfoHandler;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;

public class VanillaBridgeRepository {

	private static final String ROOT_ID = "bpm.vanilla.cmis.root.id";

	private static final String FOLDER_PREFIX = "GedDocumentId_";
	private static final String DOCUMENT_PREFIX = "DocumentVersionId_";
	private static final String USER_UNKNOWN = "<unknown>";

	/** Repository id. */
	private final String repositoryId;
	/** Types. */
	private final FileBridgeTypeManager typeManager;

	/** CMIS 1.0 repository info. */
	private final RepositoryInfo repositoryInfo10;
	/** CMIS 1.1 repository info. */
	private final RepositoryInfo repositoryInfo11;

//	private IVanillaAPI vanillaApi;
	private IGedComponent gedComponent;
	private User currentUser;
	private Group currentGroup;
	private Repository currentRepository;

	public VanillaBridgeRepository(final String repositoryId, final FileBridgeTypeManager typeManager) {
		// check repository id
		if (repositoryId == null || repositoryId.trim().length() == 0) {
			throw new IllegalArgumentException("Invalid repository id!");
		}

		this.repositoryId = repositoryId;

		// set type manager objects
		this.typeManager = typeManager;

		// set up read-write user map
		// readWriteUserMap = new HashMap<String, Boolean>();

		// set up repository infos
		repositoryInfo10 = createRepositoryInfo(CmisVersion.CMIS_1_0);
		repositoryInfo11 = createRepositoryInfo(CmisVersion.CMIS_1_1);
	}

	public void setVanillaApi(IVanillaAPI vanillaApi, IGedComponent gedComponent, User user, Group group, Repository repository) {
//		this.vanillaApi = vanillaApi;
		this.gedComponent = gedComponent;
		this.currentUser = user;
		this.currentGroup = group;
		this.currentRepository = repository;
	}

	private RepositoryInfo createRepositoryInfo(CmisVersion cmisVersion) {
		assert cmisVersion != null;

		RepositoryInfoImpl repositoryInfo = new RepositoryInfoImpl();

		repositoryInfo.setId(repositoryId);
		repositoryInfo.setName(repositoryId);
		repositoryInfo.setDescription(repositoryId);
		repositoryInfo.setCmisVersionSupported(cmisVersion.value());
		repositoryInfo.setProductName("Vanilla CMIS Server");
		repositoryInfo.setProductVersion("1.0");
		repositoryInfo.setVendorName("BPM-Conseil");
		repositoryInfo.setRootFolder(ROOT_ID);

		repositoryInfo.setThinClientUri("");
		repositoryInfo.setChangesIncomplete(true);

		RepositoryCapabilitiesImpl capabilities = new RepositoryCapabilitiesImpl();
		capabilities.setCapabilityAcl(CapabilityAcl.DISCOVER);
		capabilities.setAllVersionsSearchable(false);
		capabilities.setCapabilityJoin(CapabilityJoin.NONE);
		capabilities.setSupportsMultifiling(false);
		capabilities.setSupportsUnfiling(false);
		capabilities.setSupportsVersionSpecificFiling(false);
		capabilities.setIsPwcSearchable(false);
		capabilities.setIsPwcUpdatable(false);
		capabilities.setCapabilityQuery(CapabilityQuery.METADATAONLY);
		capabilities.setCapabilityChanges(CapabilityChanges.NONE);
		capabilities.setCapabilityContentStreamUpdates(CapabilityContentStreamUpdates.ANYTIME);
		capabilities.setSupportsGetDescendants(true);
		capabilities.setSupportsGetFolderTree(true);
		capabilities.setCapabilityRendition(CapabilityRenditions.NONE);

		if (cmisVersion != CmisVersion.CMIS_1_0) {
			capabilities.setCapabilityOrderBy(CapabilityOrderBy.NONE);

			NewTypeSettableAttributesImpl typeSetAttributes = new NewTypeSettableAttributesImpl();
			typeSetAttributes.setCanSetControllableAcl(false);
			typeSetAttributes.setCanSetControllablePolicy(false);
			typeSetAttributes.setCanSetCreatable(false);
			typeSetAttributes.setCanSetDescription(false);
			typeSetAttributes.setCanSetDisplayName(false);
			typeSetAttributes.setCanSetFileable(false);
			typeSetAttributes.setCanSetFulltextIndexed(false);
			typeSetAttributes.setCanSetId(false);
			typeSetAttributes.setCanSetIncludedInSupertypeQuery(false);
			typeSetAttributes.setCanSetLocalName(false);
			typeSetAttributes.setCanSetLocalNamespace(false);
			typeSetAttributes.setCanSetQueryable(false);
			typeSetAttributes.setCanSetQueryName(false);

			capabilities.setNewTypeSettableAttributes(typeSetAttributes);

			CreatablePropertyTypesImpl creatablePropertyTypes = new CreatablePropertyTypesImpl();
			capabilities.setCreatablePropertyTypes(creatablePropertyTypes);
		}

		repositoryInfo.setCapabilities(capabilities);

		AclCapabilitiesDataImpl aclCapability = new AclCapabilitiesDataImpl();
		aclCapability.setSupportedPermissions(SupportedPermissions.BASIC);
		aclCapability.setAclPropagation(AclPropagation.OBJECTONLY);

		// permissions
		List<PermissionDefinition> permissions = new ArrayList<PermissionDefinition>();
		permissions.add(createPermission(BasicPermissions.READ, "Read"));
		permissions.add(createPermission(BasicPermissions.WRITE, "Write"));
		permissions.add(createPermission(BasicPermissions.ALL, "All"));
		aclCapability.setPermissionDefinitionData(permissions);

		// mapping
		List<PermissionMapping> list = new ArrayList<PermissionMapping>();
		list.add(createMapping(PermissionMapping.CAN_CREATE_DOCUMENT_FOLDER, BasicPermissions.READ));
		list.add(createMapping(PermissionMapping.CAN_CREATE_FOLDER_FOLDER, BasicPermissions.READ));
		list.add(createMapping(PermissionMapping.CAN_DELETE_CONTENT_DOCUMENT, BasicPermissions.WRITE));
		list.add(createMapping(PermissionMapping.CAN_DELETE_OBJECT, BasicPermissions.ALL));
		list.add(createMapping(PermissionMapping.CAN_DELETE_TREE_FOLDER, BasicPermissions.ALL));
		list.add(createMapping(PermissionMapping.CAN_GET_ACL_OBJECT, BasicPermissions.READ));
		list.add(createMapping(PermissionMapping.CAN_GET_ALL_VERSIONS_VERSION_SERIES, BasicPermissions.READ));
		list.add(createMapping(PermissionMapping.CAN_GET_CHILDREN_FOLDER, BasicPermissions.READ));
		list.add(createMapping(PermissionMapping.CAN_GET_DESCENDENTS_FOLDER, BasicPermissions.READ));
		list.add(createMapping(PermissionMapping.CAN_GET_FOLDER_PARENT_OBJECT, BasicPermissions.READ));
		list.add(createMapping(PermissionMapping.CAN_GET_PARENTS_FOLDER, BasicPermissions.READ));
		list.add(createMapping(PermissionMapping.CAN_GET_PROPERTIES_OBJECT, BasicPermissions.READ));
		list.add(createMapping(PermissionMapping.CAN_MOVE_OBJECT, BasicPermissions.WRITE));
		list.add(createMapping(PermissionMapping.CAN_MOVE_SOURCE, BasicPermissions.READ));
		list.add(createMapping(PermissionMapping.CAN_MOVE_TARGET, BasicPermissions.WRITE));
		list.add(createMapping(PermissionMapping.CAN_SET_CONTENT_DOCUMENT, BasicPermissions.WRITE));
		list.add(createMapping(PermissionMapping.CAN_UPDATE_PROPERTIES_OBJECT, BasicPermissions.WRITE));
		list.add(createMapping(PermissionMapping.CAN_VIEW_CONTENT_OBJECT, BasicPermissions.READ));
		Map<String, PermissionMapping> map = new LinkedHashMap<String, PermissionMapping>();
		for (PermissionMapping pm : list) {
			map.put(pm.getKey(), pm);
		}
		aclCapability.setPermissionMappingData(map);

		repositoryInfo.setAclCapabilities(aclCapability);

		return repositoryInfo;
	}

	private PermissionDefinition createPermission(String permission, String description) {
		PermissionDefinitionDataImpl pd = new PermissionDefinitionDataImpl();
		pd.setId(permission);
		pd.setDescription(description);

		return pd;
	}

	private PermissionMapping createMapping(String key, String permission) {
		PermissionMappingDataImpl pm = new PermissionMappingDataImpl();
		pm.setKey(key);
		pm.setPermissions(Collections.singletonList(permission));

		return pm;
	}

	/**
	 * Returns the id of this repository.
	 */
	public String getRepositoryId() {
		return repositoryId;
	}

	// --- CMIS operations ---

	/**
	 * CMIS getRepositoryInfo.
	 */
	public RepositoryInfo getRepositoryInfo(CallContext context) {
		checkUser(context, false);

		if (context.getCmisVersion() == CmisVersion.CMIS_1_0) {
			return repositoryInfo10;
		}
		else {
			return repositoryInfo11;
		}
	}

	/**
	 * CMIS getTypesChildren.
	 */
	public TypeDefinitionList getTypeChildren(CallContext context, String typeId, Boolean includePropertyDefinitions, BigInteger maxItems, BigInteger skipCount) {
		checkUser(context, false);

		return typeManager.getTypeChildren(context, typeId, includePropertyDefinitions, maxItems, skipCount);
	}

	/**
	 * CMIS getTypesDescendants.
	 */
	public List<TypeDefinitionContainer> getTypeDescendants(CallContext context, String typeId, BigInteger depth, Boolean includePropertyDefinitions) {
		checkUser(context, false);

		return typeManager.getTypeDescendants(context, typeId, depth, includePropertyDefinitions);
	}

	/**
	 * CMIS getTypeDefinition.
	 */
	public TypeDefinition getTypeDefinition(CallContext context, String typeId) {
		checkUser(context, false);

		return typeManager.getTypeDefinition(context, typeId);
	}

	/**
	 * Create* dispatch for AtomPub.
	 */
	public ObjectData create(CallContext context, Properties properties, String folderId, ContentStream contentStream, VersioningState versioningState, ObjectInfoHandler objectInfos) {
		boolean userReadOnly = checkUser(context, true);

		String typeId = FileBridgeUtils.getObjectTypeId(properties);
		TypeDefinition type = typeManager.getInternalTypeDefinition(typeId);
		if (type == null) {
			throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
		}

		String objectId = null;
		if (type.getBaseTypeId() == BaseTypeId.CMIS_DOCUMENT) {
			objectId = createDocument(context, properties, folderId, contentStream, versioningState);
		}
		else if (type.getBaseTypeId() == BaseTypeId.CMIS_FOLDER) {
			objectId = createFolder(context, properties, folderId);
		}
		else {
			throw new CmisObjectNotFoundException("Cannot create object of type '" + typeId + "'!");
		}

		return compileObjectData(context, getFile(objectId), null, false, false, userReadOnly, objectInfos);
	}

	/**
	 * CMIS createDocument.
	 */
	public String createDocument(CallContext context, Properties properties, String folderId, ContentStream contentStream, VersioningState versioningState) {
		checkUser(context, true);

		// check versioning state
		if (VersioningState.NONE != versioningState) {
			throw new CmisConstraintException("Versioning not supported!");
		}

		// get parent File
		Object parent = getFile(folderId);
		if (!isGedDocument(parent)) {
			throw new CmisObjectNotFoundException("Parent is not a ged document!");
		}

		// check properties
		checkNewProperties(properties, BaseTypeId.CMIS_DOCUMENT);

		// check the file
		String name = FileBridgeUtils.getStringProperty(properties, PropertyIds.NAME);

		// create the file
		try {
			if (contentStream != null && contentStream.getStream() != null) {
				DocumentVersion newVersion = gedComponent.addVersionToDocumentThroughServlet(((GedDocument) parent).getId(), getFormatFromName(name), contentStream.getStream());
				return getId(newVersion);
			}
			else {
				throw new CmisNameConstraintViolationException("Stream is empty!");
			}
		} catch (Exception e) {
			throw new CmisStorageException("Could not create file: " + e.getMessage(), e);
		}
	}

	/**
	 * CMIS createDocumentFromSource.
	 */
	public String createDocumentFromSource(CallContext context, String sourceId, Properties properties, String folderId, VersioningState versioningState) {
		checkUser(context, true);

		// check versioning state
		if (VersioningState.NONE != versioningState) {
			throw new CmisConstraintException("Versioning not supported!");
		}

//		// get parent File
//		IObject parent = getFile(folderId);
//		if (!isDirectory(parent)) {
//			throw new CmisObjectNotFoundException("Parent is not a folder!");
//		}
//
//		// get source File
//		IObject source = getFile(sourceId);
//		if (isDirectory(source)) {
//			throw new CmisObjectNotFoundException("Source is not a document!");
//		}
//
//		// check properties
//		// checkCopyProperties(properties, BaseTypeId.CMIS_DOCUMENT.value());
//
//		// check the name
//		String name = null;
//		if (properties != null && properties.getProperties() != null) {
//			name = FileBridgeUtils.getStringProperty(properties, PropertyIds.NAME);
//		}
//		if (name == null) {
//			name = source.getName();
//		}
//
//		// create the file
//		try {
//			String sourcePath = ((Documents) source).getFilePath();
//
//			Documents newDocuments = uploadFile(currentUser, name, "", parent.getId(), new FileInputStream(sourcePath), "Private", new Date(), new ArrayList<FileShared>(), new ArrayList<FolderShare>());
//
//			return getId(newDocuments);
//		} catch (IOException e) {
//			throw new CmisStorageException("Could not create file: " + e.getMessage(), e);
//		}
		//TODO
		return null;
	}

//	private Documents uploadFile(User user, String name, String path, int parentId, InputStream inputStream, String status, Date lastModifiedDate, List<FileShared> statusShared, List<FolderShare> documentShared) {
//		try {
//			String fileName = Utils.deAccent(name.replace("." + FilenameUtils.getExtension(name), ""));
//			fileName = fileName + System.currentTimeMillis() + "." + FilenameUtils.getExtension(name);
//
//			Documents doc = new Documents();
//			doc.setAnnotation("");
//			doc.setAuthorName(user.getEmail());
//			doc.setCheckoutStatus(false);
//			doc.setCreationDate(new Date());
//			doc.setDeleted(false);
//			doc.setEncrypt(false);
//			doc.setEncryptPassword(null);
//			doc.setFileExtension(user.getEncryptPassword());
//			doc.setFileName(fileName);
//			doc.setFileSize((int) inputStream.available());
//			doc.setFileExtension(FilenameUtils.getExtension(name));
//			doc.setFinished(true);
//			doc.setItemTypeId(0);
//			if (lastModifiedDate != null)
//				doc.setLastModified(lastModifiedDate);
//			else
//				doc.setLastModified(new Date());
//			doc.setLastModifiedBy(user.getEmail());
//			doc.setName(name);
//			doc.setOriginalPath(path);
//			doc.setSecurityStatus(status);
//			doc.setTreeType("Documents");
//			doc.setUploadDate(new Date());
//			doc.setUserId(user.getUserId());
//			doc.setParentId(parentId);
//			doc.setValidationStatus("Waiting");
//
//			long l = 0;
//			for (int i = 0; i < doc.getFileName().length(); i++)
//				l = l * 127 + doc.getFileName().charAt(i) + doc.getId();
//			doc.setUniqueCode(String.valueOf(Math.abs(l)));
//
//			HashMap<String, InputStream> map = new HashMap<String, InputStream>();
//
//			map.put(fileName, inputStream);
//			doc = aklaboxService.uploadFiles(map, doc);
//
//			return doc;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new CmisStorageException("Could not create file: " + e.getMessage(), e);
//		}
//	}

	/**
	 * CMIS createFolder.
	 */
	public String createFolder(CallContext context, Properties properties, String folderId) {
		checkUser(context, true);

		// check properties
		checkNewProperties(properties, BaseTypeId.CMIS_FOLDER);

//		// get parent File
//		IObject parent = getFile(folderId);
//		if (!isDirectory(parent)) {
//			throw new CmisObjectNotFoundException("Parent is not a folder!");
//		}
//
//		Tree parentFolder = (Tree) parent;
//
//		// create the folder
//		String name = FileBridgeUtils.getStringProperty(properties, PropertyIds.NAME);
//		try {
//			// Example, see
//			// bpm.document.management.web.client.view.upload.CreateFolder
//			Tree dir = new Tree();
//			dir.setTreeType(AklaboxConstant.FOLDER);
//			dir.setParentId(parentFolder.getId());
//			dir.setValidatedBy(parentFolder.getValidatedBy());
//			dir.setValidationStatus(parentFolder.getValidationStatus());
//			dir.setName(name);
//			dir.setDocumentIndexed(parentFolder.getDocumentIndexed());
//			dir.setThumbnailCreated(parentFolder.getThumbnailCreated());
//			dir.setFolderStatus("Private");
//			dir.setIsDeleted(false);
//			dir.setFileTypeSelected(null);
//			dir.setAssociatedWorkflow(0);
//
//			Tree newFolder = aklaboxService.savePrivateFolders(dir, "Private", null);
//			return getId(newFolder);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new CmisStorageException("Could not create ged document!");
//		}
		return null;
	}

	/**
	 * CMIS deleteObject.
	 */
	public void deleteObject(CallContext context, String objectId) {
		checkUser(context, true);

		// get the file or folder
		Object file = getFile(objectId);

		try {
			if (isGedDocument(file)) {
				gedComponent.deleteGedDocument(((GedDocument) file).getId());
			}
			else {
				gedComponent.deleteVersion((DocumentVersion) file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CmisStorageException("Deletion failed!");
		}
	}

	/**
	 * CMIS getObject.
	 */
	public ObjectData getObject(CallContext context, String objectId, String versionServicesId, String filter, Boolean includeAllowableActions, Boolean includeAcl, ObjectInfoHandler objectInfos) {
		boolean userReadOnly = checkUser(context, false);

		// check id
		if (objectId == null && versionServicesId == null) {
			throw new CmisInvalidArgumentException("Object Id must be set.");
		}

		if (objectId == null) {
			// this works only because there are no versions in a file system
			// and the object id and version series id are the same
			objectId = versionServicesId;
		}

		// get the file or folder
		Object file = getFile(objectId);

		// set defaults if values not set
		boolean iaa = FileBridgeUtils.getBooleanParameter(includeAllowableActions, false);
		boolean iacl = FileBridgeUtils.getBooleanParameter(includeAcl, false);

		// split filter
		Set<String> filterCollection = FileBridgeUtils.splitFilter(filter);

		// gather properties
		return compileObjectData(context, file, filterCollection, iaa, iacl, userReadOnly, objectInfos);
	}

	/**
	 * CMIS getAllowableActions.
	 */
	public AllowableActions getAllowableActions(CallContext context, String objectId) {
		boolean userReadOnly = checkUser(context, false);

		// get the file or folder
		Object file = getFile(objectId);

		return compileAllowableActions(file, userReadOnly);
	}

	/**
	 * CMIS getACL.
	 */
	// public Acl getAcl(CallContext context, String objectId) {
	// checkUser(context, false);
	//
	// // get the file or folder
	// IObject file = getFile(objectId);
	// return compileAcl(file);
	// }

	/**
	 * CMIS getContentStream.
	 */
	public ContentStream getContentStream(CallContext context, String objectId, BigInteger offset, BigInteger length) {
		checkUser(context, false);

		// get the file
		final Object file = getFile(objectId);
		if (isGedDocument(file)) {
			throw new CmisStreamNotSupportedException("Not a file!");
		}

//		if (file.getFileSize() <= 0) {
//			throw new CmisConstraintException("Document has no content!");
//		}

		DocumentVersion doc = (DocumentVersion) file;

		InputStream stream = null;
		try {
			GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(doc.getParent(), currentUser.getId(), doc.getVersion());
			
			InputStream docStream = gedComponent.loadGedDocument(config);
			stream = new BufferedInputStream(docStream, 64 * 1024);
			if (offset != null || length != null) {
				stream = new ContentRangeInputStream(stream, offset, length);
			}
		} catch (Exception e) {
			throw new CmisObjectNotFoundException(e.getMessage(), e);
		}

		// compile data
		ContentStreamImpl result;
		if ((offset != null && offset.longValue() > 0) || length != null) {
			result = new PartialContentStreamImpl();
		}
		else {
			result = new ContentStreamImpl();
		}

		result.setFileName(getName(file));
		//TODO: Set file size
//		result.setLength(BigInteger.valueOf(file.getFileSize()));
		result.setLength(null);
		result.setMimeType(MimeTypes.getMIMEType(getFormat(file)));
		result.setStream(stream);

		return result;
	}

	/**
	 * CMIS getChildren.
	 */
	public ObjectInFolderList getChildren(CallContext context, String folderId, String filter, Boolean includeAllowableActions, Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount, ObjectInfoHandler objectInfos) {
		boolean userReadOnly = checkUser(context, false);

		// split filter
		Set<String> filterCollection = FileBridgeUtils.splitFilter(filter);

		// set defaults if values not set
		boolean iaa = FileBridgeUtils.getBooleanParameter(includeAllowableActions, false);
		boolean ips = FileBridgeUtils.getBooleanParameter(includePathSegment, false);

		// skip and max
		int skip = (skipCount == null ? 0 : skipCount.intValue());
		if (skip < 0) {
			skip = 0;
		}

		int max = (maxItems == null ? Integer.MAX_VALUE : maxItems.intValue());
		if (max < 0) {
			max = Integer.MAX_VALUE;
		}

		// get the folder
		Object folder = getFile(folderId);
		if (!isGedDocument(folder)) {
			throw new CmisObjectNotFoundException("Not a folder!");
		}

		// set object info of the the folder
		if (context.isObjectInfoRequired()) {
			compileObjectData(context, folder, null, false, false, userReadOnly, objectInfos);
		}

		// prepare result
		ObjectInFolderListImpl result = new ObjectInFolderListImpl();
		result.setObjects(new ArrayList<ObjectInFolderData>());
		result.setHasMoreItems(false);
		int count = 0;

		// iterate through children
		List<Object> childs = new ArrayList<Object>();
		if (isRoot(folder)) {
			try {
				List<Integer> groupIds = new ArrayList<Integer>();
				groupIds.add(currentGroup.getId());
				
				List<GedDocument> documents = gedComponent.getDocuments(groupIds, currentRepository.getId());
				if (documents != null) {
					for (GedDocument document : documents) {
						childs.add(document);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new CmisRuntimeException("Unable to get version for GedDocument with id '" + ((GedDocument) folder).getId() + "!");
			}
		}
		else {
			try {
				List<DocumentVersion> versions = ((GedDocument) folder).getDocumentVersions();
				if (versions != null) {
					for (DocumentVersion version : versions) {
						childs.add(version);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new CmisRuntimeException("Unable to get version for GedDocument with id '" + ((GedDocument) folder).getId() + "!");
			}
		}

		if (childs != null) {
			for (Object child : childs) {
				count++;

				if (skip > 0) {
					skip--;
					continue;
				}

				if (result.getObjects().size() >= max) {
					result.setHasMoreItems(true);
					continue;
				}

				// build and add child object
				ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
				objectInFolder.setObject(compileObjectData(context, child, filterCollection, iaa, false, userReadOnly, objectInfos));
				if (ips) {
					objectInFolder.setPathSegment(getName(child));
				}

				result.getObjects().add(objectInFolder);
			}
		}

		result.setNumItems(BigInteger.valueOf(count));

		return result;
	}

	/**
	 * CMIS getDescendants.
	 */
	public List<ObjectInFolderContainer> getDescendants(CallContext context, String folderId, BigInteger depth, String filter, Boolean includeAllowableActions, Boolean includePathSegment, ObjectInfoHandler objectInfos, boolean foldersOnly) {
		boolean userReadOnly = checkUser(context, false);

		// check depth
		int d = (depth == null ? 2 : depth.intValue());
		if (d == 0) {
			throw new CmisInvalidArgumentException("Depth must not be 0!");
		}
		if (d < -1) {
			d = -1;
		}

		// split filter
		Set<String> filterCollection = FileBridgeUtils.splitFilter(filter);

		// set defaults if values not set
		boolean iaa = FileBridgeUtils.getBooleanParameter(includeAllowableActions, false);
		boolean ips = FileBridgeUtils.getBooleanParameter(includePathSegment, false);

		// get the folder
		Object folder = getFile(folderId);
		if (!isGedDocument(folder)) {
			throw new CmisObjectNotFoundException("Not a folder!");
		}

		// set object info of the the folder
		if (context.isObjectInfoRequired()) {
			compileObjectData(context, folder, null, false, false, userReadOnly, objectInfos);
		}

		// get the tree
		List<ObjectInFolderContainer> result = new ArrayList<ObjectInFolderContainer>();
		gatherDescendants(context, folder, result, foldersOnly, d, filterCollection, iaa, ips, userReadOnly, objectInfos);

		return result;
	}

	/**
	 * Gather the children of a folder.
	 */
	private void gatherDescendants(CallContext context, Object folder, List<ObjectInFolderContainer> list, boolean foldersOnly, int depth, Set<String> filter, boolean includeAllowableActions, boolean includePathSegments, boolean userReadOnly, ObjectInfoHandler objectInfos) {
		assert folder != null;
		assert list != null;

		// iterate through children
		List<DocumentVersion> childs = ((GedDocument) folder).getDocumentVersions();
		for (DocumentVersion child : childs) {

			// folders only?
			if (foldersOnly) {
				continue;
			}

			// add to list
			ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
			objectInFolder.setObject(compileObjectData(context, child, filter, includeAllowableActions, false, userReadOnly, objectInfos));
			if (includePathSegments) {
				objectInFolder.setPathSegment(getName(child));
			}

			ObjectInFolderContainerImpl container = new ObjectInFolderContainerImpl();
			container.setObject(objectInFolder);

			list.add(container);

			// move to next level (only one level)
//			if (depth != 1 && child instanceof GedDocument) {
//				container.setChildren(new ArrayList<ObjectInFolderContainer>());
//				gatherDescendants(context, child, container.getChildren(), foldersOnly, depth - 1, filter, includeAllowableActions, includePathSegments, userReadOnly, objectInfos);
//			}
		}
	}

	/**
	 * CMIS getFolderParent.
	 */
	public ObjectData getFolderParent(CallContext context, String folderId, String filter, ObjectInfoHandler objectInfos) {
		List<ObjectParentData> parents = getObjectParents(context, folderId, filter, false, false, objectInfos);

		if (parents.isEmpty()) {
			throw new CmisInvalidArgumentException("The root folder has no parent!");
		}

		return parents.get(0).getObject();
	}

	/**
	 * CMIS getObjectParents.
	 */
	public List<ObjectParentData> getObjectParents(CallContext context, String objectId, String filter, Boolean includeAllowableActions, Boolean includeRelativePathSegment, ObjectInfoHandler objectInfos) {
		boolean userReadOnly = checkUser(context, false);

		// split filter
		Set<String> filterCollection = FileBridgeUtils.splitFilter(filter);

		// set defaults if values not set
		boolean iaa = FileBridgeUtils.getBooleanParameter(includeAllowableActions, false);
		boolean irps = FileBridgeUtils.getBooleanParameter(includeRelativePathSegment, false);

		// get the file or folder
		Object file = getFile(objectId);

		// don't climb above the root folder
		if (isRoot(file)) {
			return Collections.emptyList();
		}

		// set object info of the the object
		if (context.isObjectInfoRequired()) {
			compileObjectData(context, file, null, false, false, userReadOnly, objectInfos);
		}

		// get parent folder
		GedDocument parent = getParent(file);
		ObjectData object = compileObjectData(context, parent, filterCollection, iaa, false, userReadOnly, objectInfos);

		ObjectParentDataImpl result = new ObjectParentDataImpl();
		result.setObject(object);
		if (irps) {
			result.setRelativePathSegment(getName(file));
		}

		return Collections.<ObjectParentData> singletonList(result);
	}

	// --- helpers ---

	/**
	 * Compiles an object type object from a documents or folder.
	 */
	private ObjectData compileObjectData(CallContext context, Object file, Set<String> filter, boolean includeAllowableActions, boolean includeAcl, boolean userReadOnly, ObjectInfoHandler objectInfos) {
		ObjectDataImpl result = new ObjectDataImpl();
		ObjectInfoImpl objectInfo = new ObjectInfoImpl();

		result.setProperties(compileProperties(context, file, filter, objectInfo));

		if (includeAllowableActions) {
			result.setAllowableActions(compileAllowableActions(file, userReadOnly));
		}

		// TODO: ACL ?
		// if (includeAcl) {
		// result.setAcl(compileAcl(file));
		// result.setIsExactAcl(true);
		// }

		if (context.isObjectInfoRequired()) {
			objectInfo.setObject(result);
			objectInfos.addObjectInfo(objectInfo);
		}

		return result;
	}

	/**
	 * Gathers all base properties of a file or folder.
	 */
	private Properties compileProperties(CallContext context, Object file, Set<String> orgfilter, ObjectInfoImpl objectInfo) {
		if (file == null) {
			throw new IllegalArgumentException("File must not be null!");
		}

		// copy filter
		Set<String> filter = (orgfilter == null ? null : new HashSet<String>(orgfilter));

		// find base type
		String typeId = null;

		// identify if the file is a doc or a folder/directory
		if (isGedDocument(file)) {
			typeId = BaseTypeId.CMIS_FOLDER.value();
			objectInfo.setBaseType(BaseTypeId.CMIS_FOLDER);
			objectInfo.setTypeId(typeId);
			objectInfo.setContentType(null);
			objectInfo.setFileName(null);
			objectInfo.setHasAcl(true);
			objectInfo.setHasContent(false);
			objectInfo.setVersionSeriesId(null);
			objectInfo.setIsCurrentVersion(true);
			objectInfo.setRelationshipSourceIds(null);
			objectInfo.setRelationshipTargetIds(null);
			objectInfo.setRenditionInfos(null);
			objectInfo.setSupportsDescendants(true);
			objectInfo.setSupportsFolderTree(true);
			objectInfo.setSupportsPolicies(false);
			objectInfo.setSupportsRelationships(false);
			objectInfo.setWorkingCopyId(null);
			objectInfo.setWorkingCopyOriginalId(null);
		}
		else {
			typeId = BaseTypeId.CMIS_DOCUMENT.value();
			objectInfo.setBaseType(BaseTypeId.CMIS_DOCUMENT);
			objectInfo.setTypeId(typeId);
			objectInfo.setHasAcl(true);
			objectInfo.setHasContent(true);
			objectInfo.setHasParent(true);
			objectInfo.setVersionSeriesId(null);
			objectInfo.setIsCurrentVersion(true);
			objectInfo.setRelationshipSourceIds(null);
			objectInfo.setRelationshipTargetIds(null);
			objectInfo.setRenditionInfos(null);
			objectInfo.setSupportsDescendants(false);
			objectInfo.setSupportsFolderTree(false);
			objectInfo.setSupportsPolicies(false);
			objectInfo.setSupportsRelationships(false);
			objectInfo.setWorkingCopyId(null);
			objectInfo.setWorkingCopyOriginalId(null);
		}

		// exercise 3.3
		try {
			PropertiesImpl result = new PropertiesImpl();

			// id
			String id = fileToId(file);
			addPropertyId(result, typeId, filter, PropertyIds.OBJECT_ID, id);
			objectInfo.setId(id);

			// name
			String name = getName(file);
			addPropertyString(result, typeId, filter, PropertyIds.NAME, name);
			objectInfo.setName(name);

			// created and modified by
			addPropertyString(result, typeId, filter, PropertyIds.CREATED_BY, USER_UNKNOWN);
			addPropertyString(result, typeId, filter, PropertyIds.LAST_MODIFIED_BY, USER_UNKNOWN);
			objectInfo.setCreatedBy(USER_UNKNOWN);

			// creation and modification date
			GregorianCalendar lastModified = FileBridgeUtils.millisToCalendar(getModificationDate(file) != null ? getModificationDate(file).getTime() : new Date().getTime());
			addPropertyDateTime(result, typeId, filter, PropertyIds.CREATION_DATE, lastModified);
			addPropertyDateTime(result, typeId, filter, PropertyIds.LAST_MODIFICATION_DATE, lastModified);
			objectInfo.setCreationDate(lastModified);
			objectInfo.setLastModificationDate(lastModified);

			// change token - always null
			addPropertyString(result, typeId, filter, PropertyIds.CHANGE_TOKEN, null);

			// CMIS 1.1 properties
			if (context.getCmisVersion() != CmisVersion.CMIS_1_0) {
				addPropertyString(result, typeId, filter, PropertyIds.DESCRIPTION, null);
				addPropertyIdList(result, typeId, filter, PropertyIds.SECONDARY_OBJECT_TYPE_IDS, null);
			}

			// directory or file
			if (file instanceof GedDocument) {
				// base type and type name
				addPropertyId(result, typeId, filter, PropertyIds.BASE_TYPE_ID, BaseTypeId.CMIS_FOLDER.value());
				addPropertyId(result, typeId, filter, PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_FOLDER.value());
				// String path = getRepositoryPath(file);
				// TODO: To Change
				String path = "/";
				addPropertyString(result, typeId, filter, PropertyIds.PATH, path);

				// folder properties
				boolean isRoot = isRoot(file);
				if (!isRoot) {
					addPropertyId(result, typeId, filter, PropertyIds.PARENT_ID, (isRoot ? ROOT_ID : getParentId(file) + ""));
					objectInfo.setHasParent(true);
				}
				else {
					addPropertyId(result, typeId, filter, PropertyIds.PARENT_ID, null);
					objectInfo.setHasParent(false);
				}

				addPropertyIdList(result, typeId, filter, PropertyIds.ALLOWED_CHILD_OBJECT_TYPE_IDS, null);
			}
			else {
				// base type and type name
				addPropertyId(result, typeId, filter, PropertyIds.BASE_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());
				addPropertyId(result, typeId, filter, PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());

				// file properties
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_IMMUTABLE, false);
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_LATEST_VERSION, true);
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_MAJOR_VERSION, true);
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_LATEST_MAJOR_VERSION, true);
				addPropertyString(result, typeId, filter, PropertyIds.VERSION_LABEL, getName(file));
				addPropertyId(result, typeId, filter, PropertyIds.VERSION_SERIES_ID, fileToId(file));
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_VERSION_SERIES_CHECKED_OUT, false);
				addPropertyString(result, typeId, filter, PropertyIds.VERSION_SERIES_CHECKED_OUT_BY, null);
				addPropertyString(result, typeId, filter, PropertyIds.VERSION_SERIES_CHECKED_OUT_ID, null);
				addPropertyString(result, typeId, filter, PropertyIds.CHECKIN_COMMENT, "");
				if (context.getCmisVersion() != CmisVersion.CMIS_1_0) {
					addPropertyBoolean(result, typeId, filter, PropertyIds.IS_PRIVATE_WORKING_COPY, false);
				}

//				if (file.getFileSize() <= 0) {
//					addPropertyBigInteger(result, typeId, filter, PropertyIds.CONTENT_STREAM_LENGTH, null);
//					addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_MIME_TYPE, null);
//					addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_FILE_NAME, null);
//
//					objectInfo.setHasContent(false);
//					objectInfo.setContentType(null);
//					objectInfo.setFileName(null);
//				}
//				else {
					addPropertyBigInteger(result, typeId, filter, PropertyIds.CONTENT_STREAM_LENGTH, null);
					addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_MIME_TYPE, MimeTypes.getMIMEType(getFormat(file)));
					addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_FILE_NAME, name);

					objectInfo.setHasContent(true);
					objectInfo.setContentType(MimeTypes.getMIMEType(getFormat(file)));
					objectInfo.setFileName(name);
//				}

				addPropertyId(result, typeId, filter, PropertyIds.CONTENT_STREAM_ID, null);
			}

			return result;
		} catch (CmisBaseException cbe) {
			throw cbe;
		} catch (Exception e) {
			throw new CmisRuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Checks a property set for a new object.
	 */
	private void checkNewProperties(Properties properties, BaseTypeId baseTypeId) {
		// check properties
		if (properties == null || properties.getProperties() == null) {
			throw new CmisInvalidArgumentException("Properties must be set!");
		}

		// check the name
		String name = FileBridgeUtils.getStringProperty(properties, PropertyIds.NAME);
		if (!isValidName(name)) {
			throw new CmisNameConstraintViolationException("Name is not valid!");
		}

		// check the type
		String typeId = FileBridgeUtils.getObjectTypeId(properties);
		if (typeId == null) {
			throw new CmisInvalidArgumentException("Type Id is not set!");
		}

		TypeDefinition type = typeManager.getInternalTypeDefinition(typeId);
		if (type == null) {
			throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
		}

		if (type.getBaseTypeId() != baseTypeId) {
			if (baseTypeId == BaseTypeId.CMIS_DOCUMENT) {
				throw new CmisInvalidArgumentException("Type is not a document type!");
			}
			else if (baseTypeId == BaseTypeId.CMIS_DOCUMENT) {
				throw new CmisInvalidArgumentException("Type is not a folder type!");
			}
			else {
				throw new CmisRuntimeException("A file system does not support a " + baseTypeId.value() + " type!");
			}
		}

		// check type properties
		checkTypeProperties(properties, typeId, true);

		// check if required properties are missing
		for (PropertyDefinition<?> propDef : type.getPropertyDefinitions().values()) {
			if (propDef.isRequired() && !properties.getProperties().containsKey(propDef.getId()) && propDef.getUpdatability() != Updatability.READONLY) {
				throw new CmisConstraintException("Property '" + propDef.getId() + "' is required!");
			}
		}
	}

	/**
	 * Checks if the property belong to the type and are settable.
	 */
	private void checkTypeProperties(Properties properties, String typeId, boolean isCreate) {
		// check type
		TypeDefinition type = typeManager.getInternalTypeDefinition(typeId);
		if (type == null) {
			throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
		}

		// check if all required properties are there
		for (PropertyData<?> prop : properties.getProperties().values()) {
			PropertyDefinition<?> propType = type.getPropertyDefinitions().get(prop.getId());

			// do we know that property?
			if (propType == null) {
				throw new CmisConstraintException("Property '" + prop.getId() + "' is unknown!");
			}

			// can it be set?
			if (propType.getUpdatability() == Updatability.READONLY) {
				throw new CmisConstraintException("Property '" + prop.getId() + "' is readonly!");
			}

			if (!isCreate) {
				// can it be set?
				if (propType.getUpdatability() == Updatability.ONCREATE) {
					throw new CmisConstraintException("Property '" + prop.getId() + "' cannot be updated!");
				}
			}
		}
	}

	private void addPropertyId(PropertiesImpl props, String typeId, Set<String> filter, String id, String value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyIdImpl(id, value));
	}

	private void addPropertyIdList(PropertiesImpl props, String typeId, Set<String> filter, String id, List<String> value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyIdImpl(id, value));
	}

	private void addPropertyString(PropertiesImpl props, String typeId, Set<String> filter, String id, String value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyStringImpl(id, value));
	}

//	private void addPropertyInteger(PropertiesImpl props, String typeId, Set<String> filter, String id, long value) {
//		addPropertyBigInteger(props, typeId, filter, id, BigInteger.valueOf(value));
//	}

	private void addPropertyBigInteger(PropertiesImpl props, String typeId, Set<String> filter, String id, BigInteger value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyIntegerImpl(id, value));
	}

	private void addPropertyBoolean(PropertiesImpl props, String typeId, Set<String> filter, String id, boolean value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyBooleanImpl(id, value));
	}

	private void addPropertyDateTime(PropertiesImpl props, String typeId, Set<String> filter, String id, GregorianCalendar value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyDateTimeImpl(id, value));
	}

	private boolean checkAddProperty(Properties properties, String typeId, Set<String> filter, String id) {
		if ((properties == null) || (properties.getProperties() == null)) {
			throw new IllegalArgumentException("Properties must not be null!");
		}

		if (id == null) {
			throw new IllegalArgumentException("Id must not be null!");
		}

		TypeDefinition type = typeManager.getInternalTypeDefinition(typeId);
		if (type == null) {
			throw new IllegalArgumentException("Unknown type: " + typeId);
		}
		if (!type.getPropertyDefinitions().containsKey(id)) {
			throw new IllegalArgumentException("Unknown property: " + id);
		}

		String queryName = type.getPropertyDefinitions().get(id).getQueryName();

		if ((queryName != null) && (filter != null)) {
			if (!filter.contains(queryName)) {
				return false;
			}
			else {
				filter.remove(queryName);
			}
		}

		return true;
	}

	/**
	 * Compiles the allowable actions for a file or folder.
	 */
	private AllowableActions compileAllowableActions(Object file, boolean userReadOnly) {
		if (file == null) {
			throw new IllegalArgumentException("File must not be null!");
		}

		boolean isReadOnly = true;
		boolean isFolder = isGedDocument(file);
		boolean isRoot = isRoot(file);

		Set<Action> aas = EnumSet.noneOf(Action.class);

		addAction(aas, Action.CAN_GET_OBJECT_PARENTS, !isRoot);
		addAction(aas, Action.CAN_GET_PROPERTIES, true);
		addAction(aas, Action.CAN_UPDATE_PROPERTIES, !userReadOnly && !isReadOnly);
		addAction(aas, Action.CAN_MOVE_OBJECT, !userReadOnly && !isRoot);
		addAction(aas, Action.CAN_DELETE_OBJECT, !userReadOnly && !isReadOnly && !isRoot);
		addAction(aas, Action.CAN_GET_ACL, true);

		if (isFolder) {
			addAction(aas, Action.CAN_GET_DESCENDANTS, true);
			addAction(aas, Action.CAN_GET_CHILDREN, true);
			addAction(aas, Action.CAN_GET_FOLDER_PARENT, !isRoot);
			addAction(aas, Action.CAN_GET_FOLDER_TREE, true);
			addAction(aas, Action.CAN_CREATE_DOCUMENT, !userReadOnly);
			addAction(aas, Action.CAN_CREATE_FOLDER, !userReadOnly);
			addAction(aas, Action.CAN_DELETE_TREE, !userReadOnly && !isReadOnly);
		}
		else {
			addAction(aas, Action.CAN_GET_CONTENT_STREAM, isDocumentVersion(file));
			addAction(aas, Action.CAN_SET_CONTENT_STREAM, !userReadOnly && !isReadOnly);
			addAction(aas, Action.CAN_DELETE_CONTENT_STREAM, !userReadOnly && !isReadOnly);
			addAction(aas, Action.CAN_GET_ALL_VERSIONS, true);
		}

		AllowableActionsImpl result = new AllowableActionsImpl();
		result.setAllowableActions(aas);

		return result;
	}

	private boolean isRoot(Object file) {
		return isGedDocument(file) && ((GedDocument) file).getId() <= 0;
	}

	private void addAction(Set<Action> aas, Action action, boolean condition) {
		if (condition) {
			aas.add(action);
		}
	}

	/**
	 * Checks if the given name is valid for a file system.
	 * 
	 * @param name
	 *            the name to check
	 * 
	 * @return <code>true</code> if the name is valid, <code>false</code>
	 *         otherwise
	 */
	private boolean isValidName(String name) {
		if (name == null || name.length() == 0 || name.indexOf(File.separatorChar) != -1 || name.indexOf(File.pathSeparatorChar) != -1) {
			return false;
		}

		return true;
	}

	/**
	 * Checks if the user in the given context is valid for this repository and
	 * if the user has the required permissions.
	 */
	private boolean checkUser(CallContext context, boolean writeRequired) {
		if (context == null) {
			throw new CmisPermissionDeniedException("No user context!");
		}

		return true;
	}

	private boolean isGedDocument(Object file) {
		return file instanceof GedDocument;
	}
	
	private boolean isDocumentVersion(Object file) {
		return file instanceof DocumentVersion;
	}

	private String getName(Object file) {
		return isGedDocument(file) ? ((GedDocument) file).getName() : ((DocumentVersion) file).getParent().getName() + " - V" + ((DocumentVersion) file).getVersion();
	}

	private Date getModificationDate(Object file) {
		return isGedDocument(file) ? (((GedDocument) file).getLastVersion() != null ? ((GedDocument) file).getLastVersion().getModificationDate() : null) : ((DocumentVersion) file).getModificationDate();
	}

	private Integer getParentId(Object file) {
		return isGedDocument(file) ? null : ((DocumentVersion) file).getParent().getId();
	}

	private String getFormat(Object file) {
		return isGedDocument(file) ? null : ((DocumentVersion) file).getFormat();
	}    
	
	public static String getFormatFromName(String fileName) {
        if (fileName == null) {
            return null;
        }

        int x = fileName.lastIndexOf('.');
        if (x > -1) {
        	fileName = fileName.substring(x + 1);
        }
        return fileName.toLowerCase();
    }

	private GedDocument getParent(Object file) {
		return isDocumentVersion(file) ? ((DocumentVersion) file).getParent() : new GedDocument();
	}

	/**
	 * Returns the File object by id or throws an appropriate exception.
	 */
	private Object getFile(String id) {
		try {
			if (id.equals(ROOT_ID)) {
				return new GedDocument();
			}
			return idToFile(id);
		} catch (Exception e) {
			throw new CmisObjectNotFoundException(e.getMessage(), e);
		}
	}

	/**
	 * Converts an id to a File object. A simple and insecure implementation,
	 * but good enough for now.
	 */
	private Object idToFile(String id) throws IOException {
		if (id == null || id.length() == 0) {
			throw new CmisInvalidArgumentException("Id is not valid!");
		}

		if (id.contains(FOLDER_PREFIX)) {
			id = id.substring(FOLDER_PREFIX.length());

			try {
				if (id.equals("0")) {
					return new GedDocument();
				}
				return gedComponent.getDocumentDefinitionById(Integer.parseInt(id));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		else if (id.contains(DOCUMENT_PREFIX)) {
			id = id.substring(DOCUMENT_PREFIX.length());

			try {
				return gedComponent.getDocumentVersionById(Integer.parseInt(id));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * Returns the id of a File object or throws an appropriate exception.
	 */
	private String getId(Object file) {
		try {
			return fileToId(file);
		} catch (Exception e) {
			throw new CmisRuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Creates a File object from an id. A simple and insecure implementation,
	 * but good enough for now.
	 */
	private String fileToId(Object file) throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("File is not valid!");
		}

		return file instanceof GedDocument ? FOLDER_PREFIX + ((GedDocument) file).getId() : DOCUMENT_PREFIX + ((DocumentVersion) file).getId();
	}
}
