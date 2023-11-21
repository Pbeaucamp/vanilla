package bpm.data.viz.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.DataPreparationResult;
import bpm.data.viz.core.preparation.ExportPreparationInfo;
import bpm.vanilla.platform.core.IPreferencesManager;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteDataVizComponent implements IDataVizComponent {
	
	private static XStream xstream;

	static {
		xstream = new XStream();
	}

	private HttpCommunicator httpCommunicator;

	public RemoteDataVizComponent(IRepositoryApi repositoryConnection) {
		this.httpCommunicator = new HttpCommunicator();
		httpCommunicator.init(repositoryConnection);
	}

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	@Override
	public DataPreparation saveDataPreparation(DataPreparation dataprep) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataprep), IDataVizComponent.ActionType.SAVE_DATA_PREP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (DataPreparation)xstream.fromXML(xml);
	}

	@Override
	public void deleteDataPreparation(DataPreparation dataprep) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataprep), IDataVizComponent.ActionType.DELETE_DATA_PREP);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<DataPreparation> getDataPreparations() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IDataVizComponent.ActionType.GET_ALL_DATA_PREP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DataPreparation>)xstream.fromXML(xml);
	}

	@Override
	public DataPreparationResult executeDataPreparation(DataPreparation dataPrep) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataPrep), IDataVizComponent.ActionType.EXECUTE_DATA_PREP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (DataPreparationResult)xstream.fromXML(xml);
	}
	
	@Override
	public Integer countDataPreparation(DataPreparation dataPrep) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataPrep), IDataVizComponent.ActionType.COUNT_DATA_PREP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public InputStream exportDataPreparation(ExportPreparationInfo info) throws Exception {
		XmlAction op = new XmlAction(createArguments(info), IDataVizComponent.ActionType.EXPORT_DATA_PREP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		ByteArrayInputStream bis =  (ByteArrayInputStream) xstream.fromXML(xml);
		return bis;
	}

	@Override
	public void createDatabase(String tableName, DataPreparation dataPrep, boolean insert) throws Exception {
		XmlAction op = new XmlAction(createArguments(tableName, dataPrep, insert), IDataVizComponent.ActionType.CREATE_DATABASE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public String publicationETL(DataPreparation dataPrep) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataPrep), IDataVizComponent.ActionType.ETL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String)xstream.fromXML(xml);
	}
	
}
