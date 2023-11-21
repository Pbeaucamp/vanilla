package bpm.mdx.parser;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;

public class ANTLRStringStreamCaseInsensitive extends ANTLRStringStream {

	public ANTLRStringStreamCaseInsensitive(String string) {
		super(string);
	}

	@Override
	public int LA(int i) {
		if ( i==0 ) {
			return 0; // undefined
		}
		if ( i<0 ) {
			i++; // e.g., translate LA(-1) to use offset i=0; then data[p+0-1]
			if ( (p+i-1) < 0 ) {
				return CharStream.EOF; // invalid; no char before first char
			}
		}

		if ( (p+i-1) >= n ) {
            //System.out.println("char LA("+i+")=EOF; p="+p);
            return CharStream.EOF;
        }
        //System.out.println("char LA("+i+")="+(char)data[p+i-1]+"; p="+p);
		//System.out.println("LA("+i+"); p="+p+" n="+n+" data.length="+data.length);
		return Character.toUpperCase(data[p+i-1]);
	}

	
	
}
