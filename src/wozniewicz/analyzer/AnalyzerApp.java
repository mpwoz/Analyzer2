package wozniewicz.analyzer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AnalyzerApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Util util = new Util();
		Properties p = util.loadProps();
		System.out.println(p.getProperty("searchlanguage"));
		
		
	} // main
	
	
	
	
	
	

}
