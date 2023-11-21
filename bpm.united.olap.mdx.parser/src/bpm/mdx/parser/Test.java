package bpm.mdx.parser;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

//import bpm.mdx.parser.MdxParser.mdx_statement_return;
import bpm.mdx.parser.result.RootItem;

public class Test {

	/**
	 * @param args
	 * @throws RecognitionException 
	 */
	public static void main(String[] args) throws RecognitionException {
//		CharStream input = new ANTLRStringStream("SELECT UNION([time dimension].[all time], [Measures].[Sales]) ON ROW, [product dimension].[all product] ON COLUMN FROM Sales");
//		CharStream input = new ANTLRStringStream("SELECT CROSSJOIN(UNION([Time Dimension].[All Member].[Year].MEMBERS,[Time Dimension].[All Member].[Year].[Month].MEMBERS),[Customer Dimension].[All Member].[Country].MEMBERS) ON ROWS, CROSSJOIN([Product Dimension].[All Member].[Vendor].MEMBERS,[Measures].[Sales]) ON COLUMNS FROM Sales");
//		Aggregate({[Customer.CustomerHierarchy].[All Customer].[Austria], [Customer.CustomerHierarchy].[All Customer].[Australia], [Customer.CustomerHierarchy].[All Customer].[Belgium]})
		
//		String func = "([Measures].[Quantity] + (25 + [Measure].[aaaaa]) + 45)";
//		CharStream input = new ANTLRStringStreamCaseInsensitive(func);
//		CalculatedMeasureLexer lexer = new CalculatedMeasureLexer(input);
//		TokenStream tok = new CommonTokenStream(lexer);
//		CalculatedMeasureParser parser = new CalculatedMeasureParser(tok);
//		calculatedmeasure_return ret = parser.calculatedmeasure();
//		CommonTree tree = (CommonTree) ret.getTree();
//		System.out.println(tree.toStringTree());
//		
//		CommonTreeNodeStream nodeStream = new CommonTreeNodeStream(tree);
//		CalculatedMeasureTree walker = new CalculatedMeasureTree(nodeStream);
//		CalculatedItem item = walker.result();
//		item.setFormula(func);
////		
////		
////		
//		System.out.println(item.toStringTree());
		
//		new ANTLRInputStream()
		
//		ScriptEngineManager mgr = new ScriptEngineManager();
//		try {
//			System.out.println(mgr.getEngineByName("JavaScript").eval("(12+(45-34)*4)"));
//		} catch (ScriptException e) {
//			e.printStackTrace();
//		}
		
		String querydef = 
"SELECT {Hierarchize({[proéuctDim.prodduçHiera].[All produc-----------------im]})} on columns, {Hierarchize(Crossjoin (Union (Union (Union ({[t23meDime.timeDime].[All timeDime]}, [timeDime.timeDime].[All timeDime].[2009].[2nd semester].children), [timeDime.timeDime].[All timeDime].[2009].children), Union ([timeDime.timeDime].[All timeDime].children, [timeDime.timeDime].[All timeDime].[2009].[2nd semester].children)), {[Measures].[orderfact_quantityordered]}))} on rows FROM [testDateCube]";
		
		querydef = querydef.replace("\'", "");
		
//		CharStream input = new ANTLRStringStreamCaseInsensitive(querydef);
//		MdxLexer lexer = new MdxLexer(input);
//		
//		TokenStream tok = new CommonTokenStream(lexer);
//		
//		MdxParser parser = new MdxParser(tok);
//		
//		mdx_statement_return ret = parser.mdx_statement();
//		
//		CommonTree tree = (CommonTree) ret.tree;
//		
//		CommonTreeNodeStream nodeStream = new CommonTreeNodeStream(tree);
//		MdxTree walker = new MdxTree(nodeStream);
//		RootItem item = walker.resultTree();
//		System.out.println(tree.toStringTree());
	}

}
