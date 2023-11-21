package bpm.vanillahub.core.beans.activities.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.Constants;

public class EbayFinding implements DataServiceAttribute {

//	public static final String HELP_URL = "https://www.quandl.com/help/api";
	private static final String EBAY_API_URL = "http://svcs.ebay.com/services/search/FindingService/v1";

	public enum FormatOutput {
		XML(0, "XML"), JSON(1, "JSON");

		private int type;
		private String format;

		private static Map<Integer, FormatOutput> map = new HashMap<Integer, FormatOutput>();
		static {
			for (FormatOutput formatOutput : FormatOutput.values()) {
				map.put(formatOutput.getType(), formatOutput);
			}
		}

		private FormatOutput(int type, String format) {
			this.type = type;
			this.format = format;
		}

		public int getType() {
			return type;
		}

		public String getFormat() {
			return format;
		}

		public static FormatOutput valueOf(int formatOutput) {
			return map.get(formatOutput);
		}
	}
	
	public enum TypeProduct {
		ISBN(0, "ISBN"), 
		UPC(1, "UPC"), 
		EAN(1, "EAN"),
		REFERENCE_ID(3, "ReferenceID");

		private int type;
		private String name;

		private static Map<Integer, TypeProduct> map = new HashMap<Integer, TypeProduct>();
		static {
			for (TypeProduct typeProduct : TypeProduct.values()) {
				map.put(typeProduct.getType(), typeProduct);
			}
		}

		private TypeProduct(int type, String name) {
			this.type = type;
			this.name = name;
		}

		public int getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		public static TypeProduct valueOf(int typeProduct) {
			return map.get(typeProduct);
		}
	}

	private VariableString apiKey = new VariableString();
	private VariableString productId = new VariableString();
//	private VariableString sortOrderType = new VariableString();
	private VariableString outputName = new VariableString();
	private VariableString resultPerPage = new VariableString();
	private VariableString pageNumber = new VariableString();

	private FormatOutput formatOutput;
	private TypeProduct typeProduct;

	public EbayFinding() {
	}

	public VariableString getApiKeyVS() {
		return apiKey;
	}

	public String getApiKeyDisplay() {
		return apiKey.getStringForTextbox();
	}

	public void setApiKey(VariableString apiKey) {
		this.apiKey = apiKey;
	}

	public VariableString getProductIdVS() {
		return productId;
	}

	public String getProductIdDisplay() {
		return productId.getStringForTextbox();
	}

	public void setProductId(VariableString productId) {
		this.productId = productId;
	}

//	public VariableString getSortOrderTypeVS() {
//		return sortOrderType;
//	}
//
//	public String getSortOrderTypeDisplay() {
//		return sortOrderType.getStringForTextbox();
//	}
//
//	public void setSortOrderType(VariableString sortOrderType) {
//		this.sortOrderType = sortOrderType;
//	}

	public VariableString getOutputNameVS() {
		return outputName;
	}

	public String getOutputNameDisplay() {
		return outputName.getStringForTextbox();
	}

	public void setOutputName(VariableString outputName) {
		this.outputName = outputName;
	}

	@Override
	public String getOutputName(List<Parameter> parameters, List<Variable> variables) {
		return outputName.getString(parameters, variables) + "." + getFormat();
	}

	public VariableString getResultPerPageVS() {
		return resultPerPage;
	}

	public String getResultPerPageDisplay() {
		return resultPerPage.getStringForTextbox();
	}

	public void setResultPerPage(VariableString resultPerPage) {
		this.resultPerPage = resultPerPage;
	}

	public VariableString getPageNumberVS() {
		return pageNumber;
	}

	public String getPageNumberDisplay() {
		return pageNumber.getStringForTextbox();
	}

	public void setPageNumber(VariableString pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void setFormatOutput(FormatOutput formatOutput) {
		this.formatOutput = formatOutput;
	}

	public FormatOutput getFormatOutput() {
		return formatOutput;
	}
	
	public TypeProduct getTypeProduct() {
		return typeProduct;
	}
	
	public void setTypeProduct(TypeProduct typeProduct) {
		this.typeProduct = typeProduct;
	}

	@Override
	public String buildDataUrl(List<Parameter> parameters, List<Variable> variables) {
		String apiKeyStr = apiKey.getString(parameters, variables);
		String productIdStr = productId.getString(parameters, variables);
//		String sortOrderTypeStr = sortOrderType.getString(parameters, variables);
		String resultPerPageStr = resultPerPage.getString(parameters, variables);
		String pageNumberStr = resultPerPage.getString(parameters, variables);
		String productTypeStr = typeProduct.getName();
		String formatStr = getFormat();
		
		StringBuilder builder = new StringBuilder(EBAY_API_URL);
		builder.append("?OPERATION-NAME=findItemsByProduct");
		builder.append("&SERVICE-VERSION=1.0.0");
		builder.append("&SECURITY-APPNAME=" + apiKeyStr);
		builder.append("&RESPONSE-DATA-FORMAT=" + formatStr);
		builder.append("&REST-PAYLOAD");
		builder.append("&paginationInput.entriesPerPage=" + resultPerPageStr);
		builder.append("&paginationInput.pageNumber=" + pageNumberStr);
		builder.append("&productId.@type=" + productTypeStr);
		builder.append("&productId=" + productIdStr);
//		builder.append("&sortOrder=" + sortOrderTypeStr);
		return builder.toString();
	}

	private String getFormat() {
		if (formatOutput != null) {
			switch (formatOutput) {
			case JSON:
				return Constants.JSON;
			case XML:
				return Constants.XML;
			default:
				break;
			}
		}

		return Constants.XML;
	}

	@Override
	public boolean isValid() {
		return outputName != null && !outputName.getStringForTextbox().isEmpty() && productId != null && !productId.getStringForTextbox().isEmpty() 
				/*&& sortOrderType != null && !sortOrderType.getStringForTextbox().isEmpty()*/
				&& resultPerPage != null && !resultPerPage.getStringForTextbox().isEmpty()
				&& pageNumber != null && !pageNumber.getStringForTextbox().isEmpty();
	}

	@Override
	public List<Variable> getVariables(List<?> resources) {
		List<Variable> variables = new ArrayList<>();
		variables.addAll(apiKey.getVariables());
		variables.addAll(productId.getVariables());
//		variables.addAll(sortOrderType.getVariables());
		variables.addAll(outputName.getVariables());
		variables.addAll(resultPerPage.getVariables());
		variables.addAll(pageNumber.getVariables());
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<?> resources) {
		List<Parameter> parameters = new ArrayList<>();
		parameters.addAll(apiKey.getParameters());
		parameters.addAll(productId.getParameters());
//		parameters.addAll(sortOrderType.getParameters());
		parameters.addAll(outputName.getParameters());
		parameters.addAll(resultPerPage.getParameters());
		parameters.addAll(pageNumber.getParameters());
		return parameters;
	}

}
