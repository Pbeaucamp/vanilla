package bpm.vanilla.api.core.exception;

import org.springframework.http.HttpStatus;

public enum VanillaApiError {
	USER_NOT_FOUND(0, "User not found. Check user's login.",HttpStatus.NOT_FOUND ),
	DUPLICATE_USER(1, "This user already exists.", HttpStatus.CONFLICT),
	USERS_NOT_FOUND(2, "Unable to retrieve users.",HttpStatus.INTERNAL_SERVER_ERROR),
	USER_GROUPS_NOT_FOUND(3,"Unable to retrieve user's groups.",HttpStatus.INTERNAL_SERVER_ERROR),
	USER_REPOSITORIES_NOT_FOUND(4,"Unable to retrieve user's repositoires.",HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_UPDATE_USER(22,"Unable to update user.",HttpStatus.INTERNAL_SERVER_ERROR),
	
	GROUPS_NOT_FOUND(5,"Unable to retrieve groups.", HttpStatus.INTERNAL_SERVER_ERROR),
	GROUP_NOT_FOUND(6,"Group not found. Check group's name.", HttpStatus.NOT_FOUND),
	UNABLE_ADD_USERGROUP(7,"Unable to add user to group.", HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_REMOVE_USERGROUP(8,"Unable to remove user from group.",HttpStatus.INTERNAL_SERVER_ERROR),
	USER_NOT_IN_GROUP(9,"The user does not belong to this group.",HttpStatus.NOT_FOUND),
	USER_ALREADY_IN_GROUP(10,"The user is already in this group.", HttpStatus.CONFLICT),
	UNABLE_ADD_GROUP(11,"Unable to create group.", HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_REMOVE_GROUP(12,"Unable to delete group.", HttpStatus.INTERNAL_SERVER_ERROR),
	DUPLICATE_GROUP(13,"This group already exists.",HttpStatus.CONFLICT),
	
	REPOSITORIES_NOT_FOUND(14,"Unable to retrieve repositories.",HttpStatus.INTERNAL_SERVER_ERROR),
	REPOSITORY_NOT_FOUND(15,"Repository not found. Check repository's name.", HttpStatus.NOT_FOUND),
	USER_ALREADY_IN_REPOSITORY(16,"The user is already in this repository.", HttpStatus.CONFLICT),
	UNABLE_ADD_USERREP(17,"Unable to add user to repository.",HttpStatus.INTERNAL_SERVER_ERROR),
	MISSING_PARAMETERS(18,"Missing or empty parameter(s).", HttpStatus.BAD_REQUEST),
	UNABLE_DELETE_USER(19,"Unable to delete user.",HttpStatus.INTERNAL_SERVER_ERROR),
	UNALBE_DELETE_ADMIN_USER(20,"Unable to delete user. User is in the admin group.",HttpStatus.BAD_REQUEST),
	
	// Error from KPI API 
	OBSERVATORIES_NOT_FOUND(21,"Unable to retrieve observatories.",HttpStatus.INTERNAL_SERVER_ERROR),
	THEMES_NOT_FOUND(23,"Unable to retrieve themes.",HttpStatus.INTERNAL_SERVER_ERROR),
	KPI_NOT_FOUND(24,"KPI Not found. Check KPI's ID.",HttpStatus.NOT_FOUND),
	UNABLE_GET_KPI_VALUE(25,"Unable to retrieve KPI values.",HttpStatus.INTERNAL_SERVER_ERROR),
	AXIS_NOT_FOUND(26,"Unable to retrieve axis.",HttpStatus.INTERNAL_SERVER_ERROR),
	THEME_NOT_FOUND(27,"Theme not found. Check theme's id.",HttpStatus.NOT_FOUND),
	OBSERVATORY_NOT_FOUND(28,"Observatory not found. Check observatory's id.",HttpStatus.NOT_FOUND),
	AXIS_NOT_LINKED(29,"Axis is not linked with this KPI.",HttpStatus.CONFLICT),
	UNABLE_GET_VALUE(30, "Unable to get value",HttpStatus.INTERNAL_SERVER_ERROR),
	
	// Error from Metadata API 
	UNABLE_GET_METADATAS(31,"Unable to retrieve metadatas", HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_LOAD_METADATA(32,"Unable to load metadata",HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_GET_MODELS(33, "Unable to retrieve business models.",HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_GET_PACKAGES(34,"Unable to retrieve business packages",HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_GET_TABLES(35,"Unable to retrieve business tables.",HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_GET_COLUMN(36,"Unable to retrieve column, check column's id.", HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_GET_COLUMN_VALUES(37,"Unable to retrieve table's values.",HttpStatus.INTERNAL_SERVER_ERROR),
	METHOD_DOES_NOT_EXIST(38,"The method does not exist.",HttpStatus.INTERNAL_SERVER_ERROR),
	METADATA_NOT_FOUND(39,"Metadata not found. Check metadata's name.", HttpStatus.NOT_FOUND),
	BUSINESS_MODEL_NOT_FOUND(40, "Business model not found. Check business model's name",HttpStatus.NOT_FOUND),
	BUSINESS_PACKAGE_NOT_FOUND(41, "Business package not found. Check business package's name",HttpStatus.NOT_FOUND),
	BUSINESS_TABLE_NOT_FOUND(43, "Business table not found. Check business table's name",HttpStatus.NOT_FOUND),
	TABLE_COLUMN_NOT_FOUND(44, "Column not found. Check column's name",HttpStatus.NOT_FOUND),
	UNABLE_GET_SAVED_QUERIES(45, "Unable to retrieve saved queries.",HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_GET_QUERY(47, "Unable to retrieve query.",HttpStatus.INTERNAL_SERVER_ERROR),
	
	//Error from OLAP API
	UNABLE_GET_CUBES(48, "Unable to retrieve cubes.",HttpStatus.INTERNAL_SERVER_ERROR),
	CUBE_NOT_FOUND(49, "Cube not found. Check cube's name",HttpStatus.NOT_FOUND),
	CUBE_VIEW_NOT_FOUND(50,"Cube view not found. Check view's name or cube's name",HttpStatus.NOT_FOUND),
	UNABLE_SAVE_VIEW(51,"Unable to save the view",HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_GET_DETAILS(52,"Unable to get cell details",HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_SEARCH_DIMENSION(53,"Unable to search on dimensions",HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_GET_PARAMETERS(54,"Unable to get parameters",HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_SET_PARAMETERS(55,"Unable to set parameters",HttpStatus.INTERNAL_SERVER_ERROR),
	
	//Error from FWR API
	UNABLE_PREVIEW_REPORT(56,"Unable to preview the report",HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_GET_FWR_REPORTS(57,"Unable to retrieve fwr reports",HttpStatus.NOT_FOUND),
	UNABLE_SAVE_REPORT(58,"Unable to save the report",HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_LOAD_REPORT(59,"Unable to load the report",HttpStatus.NOT_FOUND),
	
	ROLE_NOT_FOUND(60, "The role has not been found.", HttpStatus.NOT_FOUND),
	UNABLE_TO_ADD_ROLE(61, "Unable to add role for the selected group.", HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_TO_ADD_GROUP_TO_DIRECTORY(62, "Unable to add group to the directory.", HttpStatus.INTERNAL_SERVER_ERROR),
	UNABLE_ADD_DIRECTORY(63, "Unable to add directory.", HttpStatus.INTERNAL_SERVER_ERROR),
	DIRECTORY_NOT_FOUND(64, "Directory not found.", HttpStatus.NOT_FOUND),
	
	// Error from Treeview API 
	FILE_NOT_FOUND(46,"Unable to retrieve file.", HttpStatus.INTERNAL_SERVER_ERROR),
	ERROT_NOT_FOUND(42,"Unknown error happened.",HttpStatus.INTERNAL_SERVER_ERROR);
	
	private final int code;
	private final String description;
	private final HttpStatus status;

	 public static VanillaApiError getVanillaApiErrorByCode(int code)
     {
         for(VanillaApiError error : values())
         {
             if(error.getCode() == code)
                 return error;
         }
         return null;
     }
	 
	private VanillaApiError(int code, String description, HttpStatus status) {
		this.code = code;
		this.description = description;
		this.status = status;
		
	}

	public String getDescription() {
		return description;
	}

	public int getCode() {
		return code;
	}
	
	public HttpStatus getStatus() {
		return status;
	}
	
	@Override
	public String toString() {
		return code + ": " + description;
	}
}
