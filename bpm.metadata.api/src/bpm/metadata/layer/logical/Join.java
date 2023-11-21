package bpm.metadata.layer.logical;

/**
 * This class symbolize a join between 2 columns
 * @author LCA
 *
 */
public class Join {
	public static final int FULL_OUTER = 3;

	public static final int LEFT_OUTER = 2;

	public static final int RIGHT_OUTER = 1;

	public static final int INNER = 0;
	
	
	private int outer = 0;
	private IDataStreamElement left, right;
	private String leftName, rightName;
	
	private String onStatement;
	
	public Join(){
	}
	
	public void setLeftName(String leftName) {
		this.leftName = leftName;
	}
	
	public String getLeftName() {
		return leftName;
	}

	public String getRightName() {
		return rightName;
	}
	
	public void setRightName(String rightName) {
		this.rightName = rightName;
	}

	public void setOuter(String outer) {
		this.outer = Integer.parseInt(outer);
	}
	
	public void setOuter(int outer) {
		this.outer = outer;
	}
	
	public int getOuter(){
		return outer;
	}
	
	public Join(IDataStreamElement left, IDataStreamElement right, int outer){
		this.left = left;
		this.right = right;
		this.outer = outer;
	}
	
	/**
	 * 
	 * @return true if the join is between 2 columns from the same Table
	 */
	public boolean isParentChild(){
		return left.getDataStream() == right.getDataStream();
	}
	
	/**
	 * dont use
	 * @return
	 */
	public IDataStreamElement getLeftElement(){
		return left;
	}
	
	/**
	 * dont use
	 * @return
	 */
	public IDataStreamElement getRightElement(){
		return right;
	}
	
	public String getDefaultOnStatement() {
		StringBuilder buf = new StringBuilder();
		if(this.getLeftElement() instanceof ICalculatedElement) {
			buf.append(((ICalculatedElement)this.getLeftElement()).getFormula());
		}
		else {
			buf.append( "`" + this.getLeftElement().getDataStream().getName() + "`." + this.getLeftElement().getOrigin().getShortName());
		}
		buf.append("=");
		if(this.getRightElement() instanceof ICalculatedElement) {
			buf.append(((ICalculatedElement)this.getRightElement()).getFormula());
		}
		else {
			buf.append("`" + this.getRightElement().getDataStream().getName() + "`." + this.getRightElement().getOrigin().getShortName());
		}
		return buf.toString();
	}
	
	public String toString(){
		if (left != null && right != null){
			StringBuffer buf = new StringBuffer();
			if (getOuter() == Join.INNER){
				if (getLeftElement() instanceof ICalculatedElement){
					buf.append(getLeftElement().getName());
				}
				else{
					buf.append(getLeftElement().getOrigin().getName());
				}
				buf.append("=");
				if (getRightElement() instanceof ICalculatedElement){
					buf.append(getRightElement().getName());
				}
				else{
					buf.append(getRightElement().getOrigin().getName());
					
				}
				
			}
			else if (getOuter() == Join.LEFT_OUTER){
				if (getLeftElement() instanceof ICalculatedElement){
					buf.append(getLeftElement().getName());
				}
				else{
					buf.append(getLeftElement().getOrigin().getName());

				}
				buf.append("=(+)");
				if (getRightElement() instanceof ICalculatedElement){
					buf.append(getRightElement().getName());
				}
				else{
					buf.append(getRightElement().getOrigin().getName());

				}
			}
			else if (getOuter() == Join.RIGHT_OUTER){
				if (getLeftElement() instanceof ICalculatedElement){
					buf.append(getLeftElement().getName());
				}
				else{
					buf.append(getLeftElement().getOrigin().getName());
					
				}
				buf.append("(+)=");
				if (getRightElement() instanceof ICalculatedElement){
					buf.append(getRightElement().getName());
				}
				else{
					buf.append(getRightElement().getOrigin().getName());
					
				}
//				buf.append("(+)");
			}
			else if (getOuter() == Join.FULL_OUTER){
				if (getLeftElement() instanceof ICalculatedElement){
					buf.append(getLeftElement().getName());
				}
				else{
					buf.append(getLeftElement().getOrigin().getName());
					
				}
				buf.append("(+)");
				if (getRightElement() instanceof ICalculatedElement){
					buf.append(getRightElement().getName());
				}
				else{
					buf.append(getRightElement().getOrigin().getName());
					
				}
//				buf.append("(+)");
			}
			return buf.toString();
		}
		else{
			return super.toString();
		}
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("                <join>\n");
		buf.append("                    <left>" + left.getName() + "</left>\n");
		buf.append("                    <right>" + right.getName() + "</right>\n");
		buf.append("                    <outer>" + outer + "</outer>\n");
		buf.append("                    <onStatement>" + onStatement + "</onStatement>\n");
		buf.append("                </join>\n");
		
		
		return buf.toString();
	}

	/**
	 * do not use 
	 * this method establish references to IdataStreamElement from the datasource 
	 * @param ds
	 */
	public void setElements(IDataStream leftTable, IDataStream rightTable) throws Exception{
		left = leftTable.getElementNamed(leftName);;
		right = rightTable.getElementNamed(rightName);;
		
		if (left == null){
			throw new Exception("Unable to find the left column " + leftName + " from " + leftTable.getName());
		}
		if (right == null){
			throw new Exception("Unable to find the right column " + rightName + " from " + rightTable.getName());
		}
		
	}
	
	public Join copy(){
		Join j = new Join();
		j.left = left;
		j.right = right;
		j.outer = outer;
		j.onStatement = onStatement;
		return j;
	}

	public String getOnStatement() {
		return onStatement;
	}

	public void setOnStatement(String onStatement) {
		this.onStatement = onStatement;
	}

}
