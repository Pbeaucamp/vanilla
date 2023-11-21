package bpm.gwt.commons.client.services.exception;

import java.util.HashMap;
import java.util.Map;

public class ServiceException extends Exception {

	private static final long serialVersionUID = 3832176451298815197L;	
	
	public enum CodeException {
		//TODO: 00 - Support all error code in client
		CODE_UNKNOWN(0),
		CODE_UPLOAD_DOCUMENT(1),
		CODE_UPLOAD_BAD_FORMAT(2);
		
		private int code;

		private static Map<Integer, CodeException> map = new HashMap<Integer, CodeException>();
		static {
			for (CodeException code : CodeException.values()) {
				map.put(code.getCode(), code);
			}
		}

		private CodeException(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static CodeException valueOf(int type) {
			return map.get(type);
		}
	}

	private CodeException code;
	
	//Old way, don't use it anymore
	public static final int CODE_BAD_USERNAME = 100;
	public static final int CODE_BAD_PASSWORD = 101;
	public static final int CODE_SESSION_EXPIRED = 102;
	
	private Integer typeException;
	
	public ServiceException() { }
	
	public ServiceException(String msg) {
		super(msg);
	}
	
	public ServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
	
	public ServiceException(int typeException, String msg) {
		super(msg);
		this.typeException = typeException;
	}
	
	public ServiceException(int typeException, String arg0, Throwable arg1) {
		super(arg0, arg1);
		this.typeException = typeException;
	}
	
	public ServiceException(CodeException code) {
		this.code = code;
	}
	
	public ServiceException(CodeException code, String msg) {
		super(msg);
		this.code = code;
	}
	
	public ServiceException(CodeException code, Throwable arg1) {
		super(arg1);
		this.code = code;
	}
	
	public boolean isSpecificException() {
		return typeException != null;
	}
	
	public Integer getTypeException() {
		return typeException;
	}
	
	public boolean hasCode() {
		return code != null;
	}
	
	public CodeException getCode() {
		return code;
	}
}
