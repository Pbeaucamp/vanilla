package bpm.united.olap.runtime.projection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;

public class CrossMembersValues implements Serializable {
	private transient List<Date> dates;
	private transient List<Double> values;
	
	private List<String> memberUnames;
	
	//coefficients for the equation : a + bx
	private transient double coeffa;
	private transient double coeffb;
	
	private Double oneVal;
	
	private transient double[] dateArray;
	private transient double[] valueArray;
	
	private transient PolynomialFunctionLagrangeForm lagrangePolynom;
	
	public CrossMembersValues(List<String> members) {
		this.memberUnames = members;
	}
	
	public List<String> getMemberUnames() {
		return memberUnames;
	}
	
	public void setHasOnlyOneValue() {
		oneVal = values.get(0);
	}
	
	public Double getOnlyOneValue() {
		return oneVal;
	}

	public String dump() {
		String unames = "";
		for(String un : memberUnames) {
			unames += un + " ";
		}
		for(int i = 0 ; i < dates.size() ; i++) {
			unames += "\n" + dates.get(i) + " --- " + values.get(i);
		}
		return unames;
	}

	public double getCoeffa() {
		return coeffa;
	}
	
	public double getCoeffb() {
		return coeffb;
	}
	
	public boolean isEquals(List<String> members) {
		return memberUnames.containsAll(members);
	}
	
	public void addValue(Date date, Double value) {
		//FIXME : Check if the date already exists
		if(dates == null) {
			dates = new ArrayList<Date>();
			values = new ArrayList<Double>();
		}
		int index = dates.indexOf(date);
		if(index > -1) {
			double val = values.get(index);
			val+=value;
			values.set(index, val);
			return;
		}
		dates.add(date);
		values.add(value);
	}
	
	public List<Date> getDates() {
		return dates;
	}
	
	public void setCoefficients(double[] coeffs) {
		coeffa = coeffs[0];
		coeffb = coeffs[1];
		dates.clear();
		values.clear();
	}
	
	public double[] getDatesAsArray() {
//		double[] dateAsArray = new double[(dates.size())];
//		for(int i = 0 ; i < dates.size() ; i++) {
//			dateAsArray[i] = dates.get(i).getTime();
//		}
//		return dateAsArray;
		return dateArray;
	} 
	
	public double[] getValuesAsArray() {
//		double[] valuesAsArray = new double[(values.size())];
//		for(int i = 0 ; i < values.size() ; i++) {
//			valuesAsArray[i] = values.get(i);
//		}
//		return valuesAsArray;
		return valueArray;
	}

	public void setArrays(double[] dateArray, double[] valueArray) {
		this.dateArray = dateArray;
		this.valueArray = valueArray;
	}
	
	public void clearArrays() {
		dateArray = null;
		valueArray = null;
	}
	
	public List<Double> getValues() {
		return values;
	}

	public void setLagrangePolynom(PolynomialFunctionLagrangeForm lagrangePolynom) {
		this.lagrangePolynom = lagrangePolynom;
	}

	public PolynomialFunctionLagrangeForm getLagrangePolynom() {
		return lagrangePolynom;
	}
	
}
