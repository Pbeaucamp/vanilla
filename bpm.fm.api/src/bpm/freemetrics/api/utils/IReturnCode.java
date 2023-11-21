package bpm.freemetrics.api.utils;

public interface IReturnCode {

	public static final int OPERATION_DONE_SUCCESFULLY = 0;
	public static final int NO_GROUP_FOR_USER = -4;
	public static final int NO_GROUP_FOR_APPLICATION = -1;

	public static final int ASSOCIATION_USER_GROUP_ALREADY_EXIST = -2;
	public static final int ASSOCIATION_APPLICATION_GROUP_ALREADY_EXIST = -3;

	public static final int USER_CREATION_FAILED = -5;
	public static final int USER_REPOSITORY_SYNCHRONIZATION_FAILED = -6;
	public static final int USER_NOT_EXIST = -7;
	public static final int REPOSITORY_SYNCHRONIZATION_FAILED = -8;
	public static final int GROUP_REPOSITORY_SYNCHRONIZATION_FAILED = -9;
	public static final int GROUP_CREATION_FAILED = -10;
}
