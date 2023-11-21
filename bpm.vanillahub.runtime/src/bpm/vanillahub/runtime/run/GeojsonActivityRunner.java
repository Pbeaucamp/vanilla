package bpm.vanillahub.runtime.run;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.output.ByteArrayOutputStream;

import bpm.geojson.creator.GeoJsonCreator;
import bpm.geoloc.creator.CSVHelper;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.vanillahub.core.beans.activities.GeojsonActivity;
import bpm.vanillahub.core.beans.activities.GeojsonActivity.TypeGeoloc;
import bpm.vanillahub.core.beans.activities.GeojsonActivity.TypeOption;
import bpm.vanillahub.runtime.WorkflowProgress;
import bpm.vanillahub.runtime.i18N.Labels;
import bpm.workflow.commons.beans.Result;

public class GeojsonActivityRunner extends ActivityRunner<GeojsonActivity> {

	private String vanillaFilesPath;

	private String tempFilePath;
	private String newFilePath;

	public GeojsonActivityRunner(WorkflowProgress workflowProgress, IVanillaLogger logger, String workflowName, String launcher, GeojsonActivity activity, List<Parameter> parameters, List<Variable> variables, boolean isLoop) {
		super(workflowProgress, logger, workflowName, launcher, activity, parameters, variables, isLoop);
		VanillaConfiguration vanillaConfig = ConfigurationManager.getInstance().getVanillaConfiguration();
		this.vanillaFilesPath = vanillaConfig.getProperty(VanillaConfiguration.P_VANILLA_FILES);
		this.tempFilePath = vanillaFilesPath + "temp/";
	}

	@Override
	protected void run(Locale locale) {
		TypeOption option = activity.getTypeOption();
		if (option == TypeOption.GEOJSON) {
			String columnId = activity.getColumnId(parameters, variables);
			String columnCoordonate = activity.getColumnGeojsonCoordinate(parameters, variables);
			String limit = activity.getLimit(parameters, variables);

			ByteArrayInputStream input = result.getInputStream();

			addInfo(Labels.getLabel(locale, Labels.StartConvertGeojson));

			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				GeoJsonCreator.createGeoJson(input, outputStream, columnId, columnCoordonate, limit);

				result.setInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
			} catch (Exception e) {
				e.printStackTrace();
				
				addError(e.getMessage());
				setResult(Result.ERROR);

				return;
			}
			String name = result.getFileName();
			if (name.contains(".")) {
				name = name.substring(0, name.lastIndexOf("."));
			}
			result.setFileName(name + ".geojson");
			addInfo(Labels.getLabel(locale, Labels.EndConvertGeojson));
		}
		else if (option == TypeOption.GEOLOC) {

			ByteArrayInputStream input = result.getInputStream();

			addInfo(Labels.getLabel(locale, Labels.StartGeoloc));

			String separator = activity.getSeparator();
			String encoding = activity.getEncoding();

			TypeGeoloc typeGeoloc = activity.getTypeGeoloc();
			switch (typeGeoloc) {
			case COLUMN_LAT_AND_LONG:
				String columnLat = activity.getColumnLat(parameters, variables);
				String columnLong = activity.getColumnLong(parameters, variables);

				// We build the CSV with Geoloc
				try {
					newFilePath = CSVHelper.buildCSVWithOneCoordinateColumn(input, separator.charAt(0), encoding, columnLat, columnLong, tempFilePath);
				} catch (Exception e) {
					e.printStackTrace();

					addError(e.getMessage());
					setResult(Result.ERROR);

					return;
				}
				break;
			case COLUMNS_ADDRESS:
				String columnNum = activity.getColumnNum(parameters, variables);
				String columnStreet = activity.getColumnStreet(parameters, variables);
				String columnCity = activity.getColumnCity(parameters, variables);
				String columnAddress = activity.getColumnAddress(parameters, variables);
				String columnPostalCode = activity.getColumnPostalCode(parameters, variables);
				int score = activity.getScore();

				// We build the CSV with Geoloc
				try {
					newFilePath = CSVHelper.buildCSVWithGeoloc(input, separator.charAt(0), encoding, false, columnNum, columnStreet, columnAddress, columnPostalCode, columnCity, new Double(score / 100), tempFilePath);
				} catch (Exception e) {
					e.printStackTrace();

					addError(e.getMessage());
					setResult(Result.ERROR);

					return;
				}
				break;
			default:
				break;
			}

			String name = result.getFileName();
			if (name.contains(".")) {
				name = name.substring(0, name.lastIndexOf("."));
			}
			result.setFileName(name + ".csv");
			
			try {
				result.setInputStream(getResourceAsStream(new FileInputStream(newFilePath)));
			} catch (Exception e) {
				e.printStackTrace();

				addError(e.getMessage());
				setResult(Result.ERROR);

				return;
			}
			addInfo(Labels.getLabel(locale, Labels.EndGeoloc));
		}
		setResult(Result.SUCCESS);
	}

	public ByteArrayInputStream getResourceAsStream(InputStream inputStream) throws Exception {
		byte[] buff = new byte[8000];
		int bytesRead = 0;

		byte[] data = null;
		try (ByteArrayOutputStream bao = new ByteArrayOutputStream()) {
			while ((bytesRead = inputStream.read(buff)) != -1) {
				bao.write(buff, 0, bytesRead);
			}
	
			data = bao.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}
		return new ByteArrayInputStream(data);
	}

	private void deleteFile(String filePath) {
		File file = new File(filePath);
		file.delete();
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
	protected void clearResources() {
		try {
			if (tempFilePath != null && !tempFilePath.isEmpty()) {
				deleteFile(tempFilePath);
			}

			if (vanillaFilesPath != null && !vanillaFilesPath.isEmpty()) {
				deleteFile(vanillaFilesPath);
			}
		} catch (Exception e) {
		}
	}
}
