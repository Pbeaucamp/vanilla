package bpm.vanillahub.runtime.run;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.MergeFilesActivity;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.workflow.commons.beans.Result;

public class MergeFilesActivityRunner extends ActivityRunner<MergeFilesActivity> {

	private boolean loopBegin;

	private String tempFilePath;

	private String outputFolderPath;
	private String outputFilePath;
	private String shapefileName;

	public MergeFilesActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, MergeFilesActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		this.loopBegin = true;

		VanillaConfiguration vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		this.tempFilePath = vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_FILES) + "temp/";
	}

	@Override
	public List<Variable> getVariables() {
		return activity.getVariables(null);
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}

	@Override
	protected void run(Locale locale) {
		File folder = new File(tempFilePath);
		try {
			if (!folder.exists()) {
				logger.info("No folder at " + tempFilePath + ". Creating it.");
				if (!folder.mkdirs()) {
					throw new Exception(Labels.getLabel(locale, Labels.UnableToCreateFolder));
				}
			}
		} catch (Exception e) {
			addError(Labels.getLabel(locale, Labels.UnableToAccessFolder));
			addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

			setResult(Result.ERROR);

			return;
		}

		if (outputFilePath == null) {
			String outputFile = activity.getOutputFile(parameters, variables);
			this.shapefileName = outputFile.replace(".shp", "");

			this.outputFolderPath = tempFilePath + "Merge_" + new Object().hashCode() + "/";
			try {
				File folderOutput = new File(outputFolderPath);
				if (!folderOutput.exists()) {
					logger.info("No folder at " + outputFolderPath + ". Creating it.");
					if (!folderOutput.mkdirs()) {
						throw new Exception(Labels.getLabel(locale, Labels.UnableToCreateFolder));
					}
				}
			} catch (Exception e) {
				addError(Labels.getLabel(locale, Labels.UnableToAccessFolder));
				addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

				setResult(Result.ERROR);

				return;
			}
			this.outputFilePath = outputFolderPath + outputFile;
		}

		String filePath = null;
		if (result.isBigFile()) {
			int n = result.getInputStream().available();
			byte[] bytes = new byte[n];
			result.getInputStream().read(bytes, 0, n);
			filePath = new String(bytes, StandardCharsets.UTF_8);
		}
		else {
			InputStream parentStream = result.getInputStream();
			// String filename = result.getFileName() + new Object().hashCode();
			String filename = result.getFileName();

			// We need to stock the file in a temp folder
			filePath = tempFilePath + filename;
			try {
				Files.copy(parentStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();

				addError(e.getMessage());

				setResult(Result.ERROR);

				return;
			}
		}

		// We need to unzip the incoming shapefile
		File inputShape = null;
		try {
			String fileDirectory = unzipFile(filePath);
			inputShape = findShape(fileDirectory);
		} catch (Exception e) {
			e.printStackTrace();

			addError(e.getMessage());

			setResult(Result.ERROR);

			return;
		}

		addInfo(Labels.getLabel(locale, Labels.MergeShapefiles));

		// ogr2ogr -f ‘ESRI Shapefile’ merge.shp ..\Sh1\Shape1.shp
		// ogr2ogr -f ‘ESRI Shapefile’ -update -append merge.shp
		// ..\Sh2\Shape2.shp -nln merge

		try {
			boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

			List<String> parameters = new ArrayList<String>();
			parameters.add("ogr2ogr");
			parameters.add("-f");
			parameters.add(isWindows ? "ESRI Shapefile" : "'ESRI Shapefile'");

			if (!loopBegin) {
				// runCommand("C:/BPM/test/0722/Shape/Sh1/Shape1.shp");
				parameters.add("-update");
				parameters.add("-append");
			}

			parameters.add(outputFilePath);
			parameters.add(inputShape.getAbsolutePath());

			if (!loopBegin) {
				parameters.add("-nln");
				parameters.add(shapefileName);
			}

			addInfo(Labels.getLabel(locale, Labels.MergingFile) + " " + filePath);

			runCommand(isWindows, parameters);
		} catch (Exception e) {
			e.printStackTrace();

			addError(e.getMessage());

			setResult(Result.ERROR);

			return;
		}

		if (isLoopEnd()) {
			// We need to package the file as zip and put it in the hub process
			try {
				String zipFilePath = zipDirectory(outputFolderPath, shapefileName);
				File zipFile = new File(zipFilePath);

				try {
					long size = FileUtils.sizeOf(zipFile);
					if (size > 1024 * 1024 * 500) {
						String path = zipFile.getAbsolutePath();
						result.setBigFile(true);
						ByteArrayInputStream bis = new ByteArrayInputStream(path.getBytes());
						result.setInputStream(bis);
					}
					else {
						ByteArrayInputStream bis = new ByteArrayInputStream(FileUtils.readFileToByteArray(zipFile));
						result.setInputStream(bis);
					}
				} catch (Exception e) {
					e.printStackTrace();

					addError(Labels.getLabel(locale, Labels.UnableToGetFileContent));
					addError(Labels.getLabel(locale, Labels.Reason) + " : " + e.getLocalizedMessage());

					setResult(Result.ERROR);
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();

				addError(e.getMessage());

				setResult(Result.ERROR);

				return;
			}
			
//			TODO: Deletes resources after
//			TODO: Integration process

			// byte[] fileCompress = baos.toByteArray();
			//
			addInfo(Labels.getLabel(locale, Labels.EndMergeShapefiles));

			clearResources();
			setResult(Result.SUCCESS);
		}

		this.loopBegin = false;
	}

	private File findShape(String fileDirectory) {
		File dir = new File(fileDirectory);
		File[] rootDirectories = dir.listFiles();
		if (rootDirectories == null || rootDirectories.length <= 0) {
			return null;
		}
		File rootDirectory = rootDirectories[0];
		File[] files = rootDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".shp");
			}
		});
		return files != null && files.length > 0 ? files[0] : null;
	}

	private void runCommand(boolean isWindows, List<String> parameters) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder();
		if (isWindows) {
			parameters.add(0, "cmd.exe");
			parameters.add(1, "/c");

			logger.info("Merging files with command in directory '" + tempFilePath + "'");
			logger.info(parameters.toString());
			
			builder.command(parameters.toArray(new String[parameters.size()]));
		}
		else {
			//For linux we need to make one big argument
			String commandArg = StringUtils.join(parameters, ' '); 
			
			parameters = new ArrayList<String>();
			parameters.add(0, "sh");
			parameters.add(1, "-c");
			parameters.add(2, commandArg);

			logger.info("Merging files with command in directory '" + tempFilePath + "'");
			logger.info(parameters.toString());
			
			builder.command(parameters.toArray(new String[parameters.size()]));
		}
		builder.directory(new File(tempFilePath));
		Process process = builder.start();
		// Read and print the standard output stream of the process
		try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = input.readLine()) != null) {
				addInfo(line);
			}
		}

		// StreamGobbler streamGobbler = new
		// StreamGobbler(process.getInputStream(), System.out::println);
		// Executors.newSingleThreadExecutor().submit(streamGobbler);
		// int exitCode = process.waitFor();
		// assert exitCode == 0;
	}

	private String unzipFile(String fileZip) throws Exception {
		String unzipDirectory = tempFilePath + "Input_" + new Object().hashCode() + "/";

		File destDir = new File(unzipDirectory);
		byte[] buffer = new byte[1024];

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip))) {
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				while (zipEntry != null) {
					File newFile = newFile(destDir, zipEntry);
					if (zipEntry.isDirectory()) {
						if (!newFile.isDirectory() && !newFile.mkdirs()) {
							throw new IOException("Failed to create directory " + newFile);
						}
					}
					else {
						// fix for Windows-created archives
						File parent = newFile.getParentFile();
						if (!parent.isDirectory() && !parent.mkdirs()) {
							throw new IOException("Failed to create directory " + parent);
						}

						// write file content
						FileOutputStream fos = new FileOutputStream(newFile);
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
						fos.close();
					}
					zipEntry = zis.getNextEntry();
				}
			}
			zis.closeEntry();
			return unzipDirectory;
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	private String zipDirectory(String directoryPath, String shapefileName) throws IOException {
		String zipOutputFile = tempFilePath + shapefileName + ".zip";

		FileOutputStream fos = new FileOutputStream(zipOutputFile);
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		File fileToZip = new File(directoryPath);

		zipFile(fileToZip, fileToZip.getName(), zipOut);
		zipOut.close();
		fos.close();

		return zipOutputFile;
	}

	private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			}
			else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}

	@Override
	protected void clearResources() {
		try {
			if (tempFilePath != null && !tempFilePath.isEmpty()) {
				deleteFile(tempFilePath);
			}
		} catch (Exception e) {
		}
	}

	private void deleteFile(String filePath) {
		File file = new File(filePath);
		file.delete();
	}
}
