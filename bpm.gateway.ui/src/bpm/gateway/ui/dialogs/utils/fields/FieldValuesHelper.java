package bpm.gateway.ui.dialogs.utils.fields;

import java.util.Collection;
import java.util.List;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseHelper;
import bpm.gateway.core.server.database.OracleXmlViewHelper;
import bpm.gateway.core.server.file.FileCSV;
import bpm.gateway.core.server.file.FileCSVHelper;
import bpm.gateway.core.server.file.FileVCL;
import bpm.gateway.core.server.file.FileVCLHelper;
import bpm.gateway.core.server.file.FileWekaHelper;
import bpm.gateway.core.server.file.FileXLS;
import bpm.gateway.core.server.file.FileXLSHelper;
import bpm.gateway.core.server.file.FileXML;
import bpm.gateway.core.server.file.FileXMLHelper;
import bpm.gateway.core.server.vanilla.DataPreparationHelper;
import bpm.gateway.core.transformations.SqlLookup;
import bpm.gateway.core.transformations.inputs.D4CInput;
import bpm.gateway.core.transformations.inputs.DataBaseInputStream;
import bpm.gateway.core.transformations.inputs.DataPreparationInput;
import bpm.gateway.core.transformations.inputs.FMDTInput;
import bpm.gateway.core.transformations.inputs.OdaInput;
import bpm.gateway.core.transformations.inputs.OracleXmlView;
import bpm.gateway.core.transformations.inputs.odaconsumer.OdaHelper;
import bpm.gateway.core.transformations.nosql.SqoopTransformation;
import bpm.gateway.core.transformations.olap.OlapDimensionExtractor;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.core.transformations.outputs.FileOutputWeka;
import bpm.gateway.ui.i18n.Messages;
import bpm.metadata.layer.business.IBusinessPackage;

public class FieldValuesHelper {

	public static List<List<Object>> getRows(Transformation stream, StreamElement field) throws Throwable {

		if (stream instanceof DataBaseInputStream || stream instanceof DataBaseOutputStream || stream instanceof SqlLookup || stream instanceof SqoopTransformation) {
			return DataBaseHelper.getCountDistinctFieldValues(null, field, (DataStream) stream);
		}
		else if (stream instanceof FileOutputWeka) {
			return FileWekaHelper.getCountDistinctFieldValues(field, (FileOutputWeka) stream);
		}
		else if (stream instanceof FileXLS) {
			return FileXLSHelper.getCountDistinctFieldValues(field, (FileXLS) stream);
		}
		else if (stream instanceof FileCSV) {
			return FileCSVHelper.getCountDistinctFieldValues(field, (FileCSV) stream);
		}
		else if (stream instanceof D4CInput) {
			if (((D4CInput) stream).getFileTransfo() instanceof FileCSV) {
				return FileCSVHelper.getCountDistinctFieldValues(field, (D4CInput) stream);
			}
			else if (((D4CInput) stream).getFileTransfo() instanceof FileXLS) {
				return FileXLSHelper.getCountDistinctFieldValues(field, (D4CInput) stream);
			}
		}
		else if (stream instanceof FileXML) {
			return FileXMLHelper.getCountDistinctFieldValues(field, (FileXML) stream);
		}
		else if (stream instanceof FileVCL) {
			return FileVCLHelper.getCountDistinctFieldValues(field, (FileVCL) stream);
		}
		else if (stream instanceof OdaInput) {
			return OdaHelper.getCountDistinctFieldValues(field, (OdaInput) stream);
		}
		else if (stream instanceof OracleXmlView) {
			return OracleXmlViewHelper.getCountDistinctFieldValues(field, (OracleXmlView) stream);
		}
		else if (stream instanceof OlapDimensionExtractor) {
			return stream.getDocument().getOlapHelper().getCountDistinctFieldValues(field, (OlapDimensionExtractor) stream);
		}
		throw new Exception(Messages.FieldValuesHelper_0 + stream.getClass().getSimpleName() + " steps");//$NON-NLS-1$
	}

	public static List<List<Object>> getRowsForFmdt(FMDTInput stream, Collection<IBusinessPackage> fmdtPackages, List<StreamElement> cols, StreamElement field) throws Exception {
		if (stream instanceof FMDTInput) {
			return stream.getDocument().getFMDTHelper().getCountDistinctFieldValues(fmdtPackages, cols, field, stream);
		}
		throw new Exception(Messages.FieldValuesHelper_0 + stream.getClass().getSimpleName() + " steps");//$NON-NLS-1$
	}

	public static List<List<Object>> getRowsForDataPreparation(DataPreparationInput stream, StreamElement field) throws Exception {
		if (stream instanceof DataPreparationInput) {
			return DataPreparationHelper.getCountDistinctFieldValues(field, (DataPreparationInput) stream);
		}
		throw new Exception(Messages.FieldValuesHelper_0 + stream.getClass().getSimpleName() + " steps");//$NON-NLS-1$
	}
}
