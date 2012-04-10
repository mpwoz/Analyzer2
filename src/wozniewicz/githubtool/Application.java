package wozniewicz.githubtool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import wozniewicz.githubtool.analyzer.Analyzer;
import wozniewicz.githubtool.analyzer.ProjectData;
import wozniewicz.githubtool.downloader.Downloader;
import wozniewicz.githubtool.presenter.Presenter;

public class Application {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Application application = new Application("app.properties");	// New instance of the application from the app.properties file
		application.Run();
	} 
	
	
	Properties properties;
	Downloader downloader;
	Presenter presenter;
	Analyzer analyzer;
	
	/** Constructor */
	public Application(String propertiesFile) {
		properties = loadProps(propertiesFile);
		downloader = new Downloader(properties);
		presenter = new Presenter();
		analyzer = new Analyzer(properties);
	}
	

	/**
	 * Runs the application
	 */
	private void Run() {
		String outDir = properties.getProperty("outputroot");
		String outFile = "output-" + System.currentTimeMillis();
		outFile = outDir + outFile + ".html";
		
		System.out.println("Printing to " + outFile);
		
		File f = new File(outDir);
		f.mkdirs();
		
		presenter.startProjectFile(null, outFile);
		
		List<ProjectData> projects = new ArrayList<ProjectData>();
		
		/*
		// Download as long as there are more pages
		while (downloader.downloadNextPages(2)) {
			projects = analyzer.analyzeNewProjects();
			presenter.addProjects(projects, outFile);
		}
		*/
		
		projects = analyzer.analyzeNewProjects();
		presenter.addProjects(projects, outFile);
		
		presenter.endProjectFile(outFile);
		
	}

	/**
	 * Helper to load the given .properties file
	 * @param propertiesFile pathname to the .properties file to load
	 * @return the Properties object
	 */
	private Properties loadProps(String propertiesFile) 
	{
		Properties prop = new Properties();
		InputStream in = this.getClass().getResourceAsStream(propertiesFile);
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
	
	
	
	
	
	

}
