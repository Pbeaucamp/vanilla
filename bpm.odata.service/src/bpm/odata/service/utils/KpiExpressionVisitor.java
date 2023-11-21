package bpm.odata.service.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.edm.primitivetype.EdmString;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;

import bpm.odata.service.data.FilterResult;

public class KpiExpressionVisitor implements ExpressionVisitor<Object> {

	private static final String METHOD_DATE = "date";

	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

	@Override
	public Object visitBinaryOperator(BinaryOperatorKind operator, Object left, Object right) throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Binary Operator are not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
	}

	@Override
	public Object visitUnaryOperator(UnaryOperatorKind operator, Object operand) throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Unary Operator are not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
	}

	@Override
	public Object visitMethodCall(MethodKind methodCall, List<Object> parameters) throws ExpressionVisitException, ODataApplicationException {
		String methodName = methodCall.toString();
		if (methodName.equalsIgnoreCase(METHOD_DATE) && parameters != null && parameters.size() == 1) {
			try {
				String dates = parameters.get(0).toString();
				String[] datesArray = dates.split(",");

				Date startDate = df.parse(datesArray[0]);
				Date endDate = datesArray.length > 1 ? df.parse(datesArray[1]) : null;

				return new FilterResult(startDate, endDate);
			} catch (Exception e) {
				throw new ODataApplicationException("This function should be respecting the following example format ex: date('2012-10-10T12:12,2015-10-10T12:12') or date('2015-10-10T12:12')", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
			}
		}
		throw new ODataApplicationException("Only '" + METHOD_DATE + "' method is supported with one or two date paremeters separated by a coma (ex: date('2012-10-10T12:12,2015-10-10T12:12') )", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
	}

	@Override
	public Object visitLambdaExpression(String lambdaFunction, String lambdaVariable, Expression expression) throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Lambda Expression are not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
	}

	@Override
	public Object visitLiteral(Literal literal) throws ExpressionVisitException, ODataApplicationException {
		String literalAsString = literal.getText();
		if (literal.getType() instanceof EdmString) {
			String stringLiteral = "";
			if (literal.getText().length() > 2) {
				stringLiteral = literalAsString.substring(1, literalAsString.length() - 1);
			}

			return stringLiteral;
		}

		throw new ODataApplicationException("Only Edm.String literals are implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
	}

	@Override
	public Object visitMember(UriInfoResource member) throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Member are not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
	}

	@Override
	public Object visitAlias(String aliasName) throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Alias are not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
	}

	@Override
	public Object visitTypeLiteral(EdmType type) throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Type Literal are not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
	}

	@Override
	public Object visitLambdaReference(String variableName) throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Lambda Reference are not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
	}

	@Override
	public Object visitEnum(EdmEnumType type, List<String> enumValues) throws ExpressionVisitException, ODataApplicationException {
		throw new ODataApplicationException("Enum are not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
	}

}
