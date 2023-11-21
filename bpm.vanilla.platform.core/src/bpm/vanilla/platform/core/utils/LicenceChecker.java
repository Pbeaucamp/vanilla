package bpm.vanilla.platform.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

import bpm.vanilla.platform.core.beans.Licence;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class LicenceChecker {

	public static boolean checkLicence(String application, String password, boolean licenceMandatory) {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String licenceApplicationPath = config.getProperty(VanillaConfiguration.P_LICENCE_PATH + "." + application);
		if (licenceMandatory || (licenceApplicationPath != null && !licenceApplicationPath.isEmpty())) {
			return checkLicenceFile(password, licenceApplicationPath);
		}

		String licenceGlobalPath = config.getProperty(VanillaConfiguration.P_LICENCE_PATH);
		if (licenceMandatory || (licenceGlobalPath != null && !licenceGlobalPath.isEmpty())) {
			return checkLicenceFile(password, licenceApplicationPath);
		}
		
		return false;
	}

	private static boolean checkLicenceFile(String password, String licenceApplicationPath) {
		if (licenceApplicationPath == null || licenceApplicationPath.isEmpty()) {
			return true;
		} 
		
//		Path path = Paths.get(licenceApplicationPath);
//		 
//		 try (InputStream in = Files.newInputStream(path)) {
//            ByteArrayOutputStream byteIs = FileEncryptor.decrypt(password, in);
//            byte[] licenceByte = byteIs.toByteArray();
//            ObjectMapper mapper = new ObjectMapper();
//
//            Licence licence = mapper.readValue(licenceByte, Licence.class);
//            return licence.isDateLimitReach();
//        } catch (Exception e) {
//        	e.printStackTrace();
//			return true;
//        }
		//TODO: Deactivate
		return false;
	}
}
