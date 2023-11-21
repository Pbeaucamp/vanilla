package bpm.freemetrics.api.organisation.metrics;

import java.util.Calendar;
import java.util.Date;

import bpm.freemetrics.api.utils.Tools;

public class MetricValues {

	private int id = 0;
	private Integer mvGlAssoc_ID;
	private Date    mvValueDate;
	private Float   mvValue;
	private String  mvTrendDate ;
	private String  mvGlObjLocalNat;
	private String  mvGlObjAnMens;
	private String  mvPeriodType;
	private Integer mvPreviousMetricsValueId;
	private Float   mvMinValue;
	private Float   mvMaxValue;
	private Date    mvCreationDate;
	private Date    mvPeriodDate;

	private Float mvGlValeurSeuilMini,mvGlValeurSeuilMaxi
	, mvGlMaxiPoint, mvGlMiniPoint,mvGlObjectif;

	private Integer mvTolerance;
	private String mvStatus,mvTrendOrder,mvTrendDay,mvTrendMonth,mvTrendQuarter
	,mvTrendWeek,mvTrendYear,mvTrendSemiYear;
	
	private String axes = "";

	public MetricValues() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the mvGlAssoc_ID
	 */
	public Integer getMvGlAssoc_ID() {
		if(mvGlAssoc_ID == null) {
			return 0;
		}
		return mvGlAssoc_ID;
	}

	/**
	 * @param mvGlAssoc_ID the mvGlAssoc_ID to set
	 */
	public void setMvGlAssoc_ID(Integer mvGlAssoc_ID) {
		this.mvGlAssoc_ID = mvGlAssoc_ID;
	}

	/**
	 * @return the mvValueDate
	 */
	public Date getMvValueDate() {
		return mvValueDate;
	}

	/**
	 * @param mvValueDate the mvValueDate to set
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void setMvValueDate(Date mvValueDate) {

		if(mvValueDate!= null && mvValueDate.getSeconds() == 0){
			mvValueDate.setHours(Calendar.getInstance().HOUR);
			mvValueDate.setMinutes(Calendar.getInstance().MINUTE);
			mvValueDate.setSeconds(Calendar.getInstance().SECOND);
		}

		this.mvValueDate = mvValueDate;
	}

	/**
	 * @return the mvValue
	 */
	public Float getMvValue() {
		return mvValue;
	}

	/**
	 * @param mvValue the mvValue to set
	 */
	public void setMvValue(Float mvValue) {
		if(mvValue != null)
			this.mvValue = mvValue;
	}

	/**
	 * @return the mvTrendDate
	 */
	public String getMvTrendDate() {
		return mvTrendDate;
	}

	/**
	 * @param mvTrendDate the mvTrendDate to set
	 */
	public void setMvTrendDate(String mvTrendDate) {
		if(Tools.isValid(mvTrendDate))
			this.mvTrendDate = mvTrendDate;
	}

	/**
	 * @return the mvGlObjLocalNat
	 */
	public String getMvGlObjLocalNat() {
		return mvGlObjLocalNat;
	}

	/**
	 * @param mvGlObjLocalNat the mvGlObjLocalNat to set
	 */
	public void setMvGlObjLocalNat(String mvGlObjLocalNat) {
		if(Tools.isValid(mvGlObjLocalNat))
			this.mvGlObjLocalNat = mvGlObjLocalNat;

	}

	/**
	 * @return the mvGlObjAnMens
	 */
	public String getMvGlObjAnMens() {
		return mvGlObjAnMens;
	}

	/**
	 * @param mvGlObjAnMens the mvGlObjAnMens to set
	 */
	public void setMvGlObjAnMens(String mvGlObjAnMens) {
		if(Tools.isValid(mvGlObjAnMens))
			this.mvGlObjAnMens = mvGlObjAnMens;
	}

	/**
	 * @return the mvPeriodType
	 */
	public String getMvPeriodType() {
		return mvPeriodType;
	}

	/**
	 * @param mvPeriodType the mvPeriodType to set
	 */
	public void setMvPeriodType(String mvPeriodType) {
		if(Tools.isValid(mvPeriodType))
			this.mvPeriodType = mvPeriodType;
	}

	/**
	 * @return the mvPreviousMetricsValueId
	 */
	public Integer getMvPreviousMetricsValueId() {
		return mvPreviousMetricsValueId;
	}

	/**
	 * @param mvPreviousMetricsValueId the mvPreviousMetricsValueId to set
	 */
	public void setMvPreviousMetricsValueId(Integer mvPreviousMetricsValueId) {
		this.mvPreviousMetricsValueId = mvPreviousMetricsValueId;
	}

	/**
	 * @return the mvMinValue
	 */
	public Float getMvMinValue() {
		return mvMinValue;
	}

	/**
	 * @param mvMinValue the mvMinValue to set
	 */
	public void setMvMinValue(Float mvMinValue) {
		this.mvMinValue = mvMinValue;
	}

	/**
	 * @return the mvMaxValue
	 */
	public Float getMvMaxValue() {
		return mvMaxValue;
	}

	/**
	 * @param mvMaxValue the mvMaxValue to set
	 */
	public void setMvMaxValue(Float mvMaxValue) {
//		if(mvMaxValue != null)
			this.mvMaxValue = mvMaxValue;
//		else
//			this.mvMaxValue = new Float(0);
	}

	/**
	 * @return the mvCreationDate
	 */
	public Date getMvCreationDate() {
		return mvCreationDate;
	}

	/**
	 * @param mvCreationDate the mvCreationDate to set
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	public void setMvCreationDate(Date mvCreationDate) {

		if(mvCreationDate!= null && mvCreationDate.getSeconds() == 0){
			mvCreationDate.setHours(Calendar.getInstance().HOUR);
			mvCreationDate.setMinutes(Calendar.getInstance().MINUTE);
			mvCreationDate.setSeconds(Calendar.getInstance().SECOND);
		}

		this.mvCreationDate = mvCreationDate;
	}

	/**
	 * @return the mvPeriodDate
	 */
	public Date getMvPeriodDate() {
		return mvPeriodDate;
	}

	/**
	 * @param mvPeriodDate the mvPeriodDate to set
	 */
	public void setMvPeriodDate(Date mvPeriodDate) {

//		if(mvPeriodDate!= null && mvPeriodDate.getSeconds() == 0){
//		mvPeriodDate.setHours(Calendar.getInstance().HOUR);
//		mvPeriodDate.setMinutes(Calendar.getInstance().MINUTE);
//		mvPeriodDate.setSeconds(Calendar.getInstance().SECOND);
//		}

		this.mvPeriodDate = mvPeriodDate;
	}

	/**
	 * @return the mvGlValeurSeuilMini
	 */
	public Float getMvGlValeurSeuilMini() {
		return mvGlValeurSeuilMini;
	}

	/**
	 * @param mvGlValeurSeuilMini the mvGlValeurSeuilMini to set
	 */
	public void setMvGlValeurSeuilMini(Float mvGlValeurSeuilMini) {
//		if(mvGlValeurSeuilMini != null)
			this.mvGlValeurSeuilMini = mvGlValeurSeuilMini;
//		else
//			this.mvGlValeurSeuilMini = new Float(0);
	}

	/**
	 * @return the mvGlValeurSeuilMaxi
	 */
	public Float getMvGlValeurSeuilMaxi() {
		return mvGlValeurSeuilMaxi;
	}

	/**
	 * @param mvGlValeurSeuilMaxi the mvGlValeurSeuilMaxi to set
	 */
	public void setMvGlValeurSeuilMaxi(Float mvGlValeurSeuilMaxi) {
//		if(mvGlValeurSeuilMaxi != null)
			this.mvGlValeurSeuilMaxi = mvGlValeurSeuilMaxi;
//		else
//			this.mvGlValeurSeuilMaxi = new Float(0);
	}

	/**
	 * @return the mvGlMaxiPoint
	 */
	public Float getMvGlMaxiPoint() {
		return mvGlMaxiPoint;
	}

	/**
	 * @param mvGlMaxiPoint the mvGlMaxiPoint to set
	 */
	public void setMvGlMaxiPoint(Float mvGlMaxiPoint) {
//		if(mvGlMaxiPoint != null)
			this.mvGlMaxiPoint = mvGlMaxiPoint;
//		else
//			this.mvGlMaxiPoint = new Float(0);
	}

	/**
	 * @return the mvGlMiniPoint
	 */
	public Float getMvGlMiniPoint() {
		return mvGlMiniPoint;
	}

	/**
	 * @param mvGlMiniPoint the mvGlMiniPoint to set
	 */
	public void setMvGlMiniPoint(Float mvGlMiniPoint) {
//		if(mvGlMiniPoint != null)
			this.mvGlMiniPoint = mvGlMiniPoint;
//		else
//			this.mvGlMiniPoint = new Float(0);
	}

	/**The target for a given period
	 * @return the mvGlObjectif
	 */
	public Float getMvGlObjectif() {
		return mvGlObjectif;
	}

	/**
	 * @param mvGlObjectif the mvGlObjectif to set
	 */
	public void setMvGlObjectif(Float mvGlObjectif) {
//		if(mvGlObjectif != null)
//			this.mvGlObjectif = mvGlObjectif;
//		else
//			this.mvGlObjectif = new Float(0);
		this.mvGlObjectif = mvGlObjectif;
	}

	/**The tolerance for a given periode 0 as default
	 * @return the mvTolerance
	 */
	public Integer getMvTolerance() {
		return mvTolerance;
	}

	/**
	 * @param mvTolerance the mvTolerance to set
	 */
	public void setMvTolerance(Integer mvTolerance) {
		this.mvTolerance = mvTolerance;
	}
	
	/**The trend order according the target, 
	 * May have 3 values : 
	 * <LI> IConstants.TREND_ORDER_DOWN, means that value is over the target and must regressed</LI>
	 * <LI> IConstants.TREND_ORDER_UP, means that value is under the target and must progressed</LI>
	 * <LI> IConstants.TREND_ORDER_STABLE, means that value is close to the target</LI>
	 * 
	 * </BR></BR>
	 * 
	 * @return the mvTrendOrder
	 */
	public String getMvTrendOrder() {
		return mvTrendOrder;
	}

	/**The trend order according the target, 
	 * May have 3 values : 
	 * <LI> IConstants.TREND_ORDER_DOWN, means that value is over the target and must regressed</LI>
	 * <LI> IConstants.TREND_ORDER_UP, means that value is under the target and must progressed</LI>
	 * <LI> IConstants.TREND_ORDER_STABLE, means that value is close to the target</LI>
	 * 
	 * </BR></BR>
	 * @param mvTrendOrder the mvTrendOrder to set
	 */
	public void setMvTrendOrder(String mvTrendOrder) {
		this.mvTrendOrder = mvTrendOrder;
	}

	/**
	 * @return the mvTrendDay
	 */
	public String getMvTrendDay() {
		return mvTrendDay;
	}

	/**
	 * @param mvTrendDay the mvTrendDay to set
	 */
	public void setMvTrendDay(String mvTrendDay) {
		this.mvTrendDay = mvTrendDay;
	}

	/**
	 * @return the mvTrendMonth
	 */
	public String getMvTrendMonth() {
		return mvTrendMonth;
	}

	/**
	 * @param mvTrendMonth the mvTrendMonth to set
	 */
	public void setMvTrendMonth(String mvTrendMonth) {
		this.mvTrendMonth = mvTrendMonth;
	}

	/**
	 * @return the mvTrendQuarter
	 */
	public String getMvTrendQuarter() {
		return mvTrendQuarter;
	}

	/**
	 * @param mvTrendQuarter the mvTrendQuarter to set
	 */
	public void setMvTrendQuarter(String mvTrendQuarter) {
		this.mvTrendQuarter = mvTrendQuarter;
	}

	/**
	 * @return the mvTrendWeek
	 */
	public String getMvTrendWeek() {
		return mvTrendWeek;
	}

	/**
	 * @param mvTrendWeek the mvTrendWeek to set
	 */
	public void setMvTrendWeek(String mvTrendWeek) {
		this.mvTrendWeek = mvTrendWeek;
	}

	/**
	 * @return the mvTrendYear
	 */
	public String getMvTrendYear() {
		return mvTrendYear;
	}

	/**
	 * @param mvTrendYear the mvTrendYear to set
	 */
	public void setMvTrendYear(String mvTrendYear) {
		this.mvTrendYear = mvTrendYear;
	}

	/**
	 * @return the mvTrendSemiYear
	 */
	public String getMvTrendSemiYear() {
		return mvTrendSemiYear;
	}

	/**
	 * @param mvTrendSemiYear the mvTrendSemiYear to set
	 */
	public void setMvTrendSemiYear(String mvTrendSemiYear) {
		this.mvTrendSemiYear = mvTrendSemiYear;
	}

	/**The value's status according to the target, 
	 * May have 3 values : 
	 * <LI> IConstants.STATUS_OVER, means that value is over the target (With Tolerance range)</LI>
	 * <LI> IConstants.STATUS_UNDER, means that value is under the target (With Tolerance range)</LI>
	 * <LI> IConstants.STATUS_ACHIEVED, means that value is close to the target (With Tolerance range)</LI>
	 * 
	 * </BR></BR>
	 * @return the mvStatus
	 */
	public String getMvStatus() {
		return mvStatus;
	}

	/**The value's status according to the target, 
	 * May have 3 values : 
	 * <LI> IConstants.STATUS_OVER, means that value is over the target (With Tolerance range)</LI>
	 * <LI> IConstants.STATUS_UNDER, means that value is under the target (With Tolerance range)</LI>
	 * <LI> IConstants.STATUS_ACHIEVED, means that value is close to the target (With Tolerance range)</LI>
	 * 
	 * </BR></BR>
	 * @param mvStatus the mvStatus to set
	 */
	public void setMvStatus(String mvStatus) {
		this.mvStatus = mvStatus;
	}

	public void setAxes(String axes) {
		this.axes = axes;
	}

	public String getAxes() {
		return axes;
	}


}
