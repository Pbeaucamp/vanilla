package bpm.vanilla.platform.core.commands;

/**
 * This Manager is meant to provide a way to perform some specific commands
 * on the VanillaPlatform. 
 * 
 * Typically it will be used for long time consuming operation that depends 
 * on environment(exemple, historize a folder ca e launched, but the result
 * depends on the folder content, it make sure that it will be done at some point)
 * 
 * 
 * @author ludo
 *
 */
public interface IVanillaCommandManager {
	public static final String SERVLET_MASS_HISTORIZATION ="/vanilla41/massReportHistorization";

	//public List<IVanillaCommand> getRunningCommands() throws Exception;
	
	/**
	 * This command will browse the content of the folder [VANILLA_FILES]/vanillaRootRelativeFolderPath.
	 * The command is looking for files with a name matching the following pattern :
	 * name$repositoryId_reportDirectoryItemId_userId1.reportFormat
	 * 
	 * All files matching this pattern will sent to the Vanilla ReportHistoricComponent and
	 * create an entry for the file. If everything works fine, the file will be deleted.
	 * The created Entry will be an private entry for the given User.
	 * 
	 * Only one of this command can run at the same time, if the command is running, a warn message will
	 * be dumped in the Vanilla console.
	 * 
	 * 
	 * @throws Exception
	 */
	public void historizeFolderContent(String vanillaRootRelativeFolderPath) throws Exception;
}
