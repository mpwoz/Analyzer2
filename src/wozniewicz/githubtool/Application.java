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
		
		// Specify correct properties file here
		Application application = new Application("ews.properties");
		application.runAll(0);
	} 
	



	Properties properties;
	Downloader downloader;
	Presenter presenter;
	Analyzer analyzer;
	
	/** Constructor */
	public Application(String propertiesFile) {
		properties = loadProps(propertiesFile);
		//downloader = new Downloader(properties);
		presenter = new Presenter();
		analyzer = new Analyzer(properties);
	}
	
	
	private void runAll(int limit) {
		List<ProjectData> projects = new ArrayList<ProjectData>();
		projects = analyzer.analyzeNewProjects(limit);
		
		
		String outDir = properties.getProperty("outputroot");
		String timestamp = "" + System.currentTimeMillis();
		
		String projectList = outDir + "projects-" + timestamp + ".html";
		String fileList = outDir + "keywords-" + timestamp + ".html";
		String summaryFile = outDir + "summary-" + timestamp + ".html";
		
		// Make the directory if it doesn't yet exist
		File f = new File(outDir);
		f.mkdirs();
		
		// Write out all the data to separate html files
		System.out.println("Writing data to file....");
		presenter.startKeywordFile(fileList);
		presenter.addKeywords(projects, fileList);

		presenter.startProjectFile(null, projectList);
		presenter.addProjects(projects, projectList);
		presenter.endProjectFile(projectList);
		
		presenter.summarizeKeywords(projects, summaryFile);
		
		System.out.println("Done.");
	}
	
	
	/**
	 * Runs the application
	 */
	private void findLOC() {
		String outDir = properties.getProperty("outputroot");
		String timestamp = "" + System.currentTimeMillis();
		
		String projectList = outDir + "project-" + timestamp + ".html";
		
		File f = new File(outDir);
		f.mkdirs();
		
		presenter.startProjectFile(null, projectList);
		
		List<ProjectData> projects = new ArrayList<ProjectData>();
		
		/*
		// Download as long as there are more pages
		while (downloader.downloadNextPages(2)) {
			projects = analyzer.analyzeNewProjects();
			presenter.addProjects(projects, outFile);
		}
		*/
		
		projects = analyzer.analyzeNewProjects(0);

		System.out.println("Writing data to file....");
		presenter.addProjects(projects, projectList);
		
		
		presenter.endProjectFile(projectList);
		
		System.out.println("Done.");
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
