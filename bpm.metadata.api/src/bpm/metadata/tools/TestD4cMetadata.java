package bpm.metadata.tools;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;

public class TestD4cMetadata {

	private static final String ARG_HELP = "h";
	private static final String ARG_URL_CKAN = "u";
	private static final String ARG_API_KEY = "k";
	private static final String ARG_ORG = "o";
	private static final String ARG_JDBC_URL = "j";
	private static final String ARG_JDBC_LOGIN = "l";
	private static final String ARG_JDBC_PASS = "p";
	private static final String ARG_DIR = "d";
	private static final String ARG_DATASETS = "ds";
	private static final String ARG_UPDATE = "up";
	private static final String ARG_NAME = "n";
	
	
	public static void main(String[] args) throws Exception {
		
		try {
			Options options = new Options();
			options.addOption(ARG_HELP, false, "Help");
			options.addOption(ARG_URL_CKAN, true, "URL to CKAN");
			options.addOption(ARG_API_KEY, true, "CKAN api key");
			options.addOption(ARG_ORG, true, "CKAN organisation");
			options.addOption(ARG_JDBC_URL, true, "JDBC Url");
			options.addOption(ARG_JDBC_LOGIN, true, "JDBC Login");
			options.addOption(ARG_JDBC_PASS, true, "JDBC Password");
			options.addOption(ARG_DIR, true, "Vanilla directory or item id");
			options.addOption(ARG_DATASETS, true, "Dataset list (separated by ;)");
			options.addOption(ARG_UPDATE, true, "Update (1 for update 0 for create)");
			options.addOption(ARG_NAME, true, "Metadata name");
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);		
			
			BaseVanillaContext vanillaCtx = new BaseVanillaContext("http://localhost:7070/VanillaRuntime", "system", "system");
			
			RemoteVanillaPlatform vanillaApi = new RemoteVanillaPlatform(vanillaCtx);
			
			BaseRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, vanillaApi.getVanillaSecurityManager().getGroupById(1), vanillaApi.getVanillaRepositoryManager().getRepositoryById(1));
			IRepositoryApi api = new RemoteRepositoryApi(ctx);
			
			String xml = D4CMetadataHelper.createMetadata(
					cmd.getOptionValue(ARG_URL_CKAN), 
					cmd.getOptionValue(ARG_API_KEY), 
					cmd.getOptionValue(ARG_ORG), 
					cmd.getOptionValue(ARG_JDBC_URL), 
					cmd.getOptionValue(ARG_JDBC_LOGIN), 
					cmd.getOptionValue(ARG_JDBC_PASS), 
					cmd.getOptionValue(ARG_DATASETS), 
					Integer.parseInt(cmd.getOptionValue(ARG_UPDATE)) == 1, 
					Integer.parseInt(cmd.getOptionValue(ARG_DIR)),
					api);
			
//			System.out.println("Xml generated");
//			System.out.println(xml);
			
			Files.write(Paths.get("./fmdt.txt"), xml.getBytes());
			
			
			if(Integer.parseInt(cmd.getOptionValue(ARG_UPDATE)) == 1) {
				api.getRepositoryService().updateModel(api.getRepositoryService().getDirectoryItem(Integer.parseInt(cmd.getOptionValue(ARG_DIR))), xml);
			}
			else {
				api.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FMDT_TYPE, -1, api.getRepositoryService().getDirectory(Integer.parseInt(cmd.getOptionValue(ARG_DIR))), cmd.getOptionValue(ARG_NAME), "", "", "", xml, true);
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
