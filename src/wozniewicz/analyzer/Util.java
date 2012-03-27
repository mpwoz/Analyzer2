package wozniewicz.analyzer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Util {
	/**
	 * Loads the properties file with all the other configuration options
	 * @return Properties object from the analyzer.properties file
	 */
	public Properties loadProps() 
	{
		Properties prop = new Properties();
		
		InputStream in = this.getClass().getResourceAsStream("analyzer.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
}
