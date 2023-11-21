package bpm.workflow.runtime.resources.servers;


/**
 * This class is used to create the Server Resources.
 * You have to specify which type of Server you want to create
 * @author LCA
 *
 */
public class FactoryServer {
//	public static final int VANILLA_REPOSITORY = 0;
	public static final int DATABASE_SERVER = 1;
	public static final int MAIL_SERVER = 2;
//	public static final int GATEWAY_SERVER = 3;
//	public static final int SECURITY_SERVER = 4;
	public static final int FREEMETRICS_SERVER = 5;
	public static final int FILE_SERVER = 6;
//	public static final int RUNTIME_SERVER = 7;
		
	/**
	 * create a server instance
	 * @param type : one of the provided constants 
	 * @return a new Server of the speficied type
	 * @throws FactoryServerException
	 */
	public static Server getInstance(int type, java.util.Properties prop) throws FactoryServerException{
		switch(type){
//		case VANILLA_REPOSITORY:
//			try {
//					return new BiRepository(prop);
//				} catch (Exception e) {
//					e.printStackTrace();
//					throw new FactoryServerException("Unable to create Vanilla Repository object : " + e.getMessage());
//				}
		
		case DATABASE_SERVER:
			return new DataBaseServer(prop);
		
		case FREEMETRICS_SERVER:
			return new FreemetricServer(prop);
			
		case MAIL_SERVER:
			return new ServerMail(prop);
			
		case FILE_SERVER:
			return new FileServer(prop);
			
//		case SECURITY_SERVER:
//			try {
//				return new SecurityServer(prop);
//			} catch (Exception e1) {
//				throw new FactoryServerException("Unable to create Security Server : " + e1.getMessage());
//			}
//			
//		case GATEWAY_SERVER:
//			try {
//				return new ServerGateway(prop);
//			} catch (Exception e) {
//				e.printStackTrace();
//				throw new FactoryServerException("Unable to create Gateway object : " + e.getMessage());
//			}
//			
//		case RUNTIME_SERVER:
//			try {
//				return new VanillaRuntimeServer(prop);
//			} catch (Exception e1) {
//				throw new FactoryServerException("Unable to create Vanilla Runtime Server : " + e1.getMessage());
//			}
		}
		throw new FactoryServerException("Bad constant for Server instantiation");
		
	}
}
