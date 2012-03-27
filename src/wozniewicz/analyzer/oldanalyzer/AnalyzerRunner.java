package wozniewicz.analyzer.oldanalyzer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
	
/**
 * Main application - analyzes downloaded github projects
 * @author Martin Wozniewicz
 *
 */
public class AnalyzerRunner {

	/* CONFIG */
	// projectRoot: the root of all the downloaded project folders
	String projectRoot;
	
	String rejectRoot;
	
	String[] keywords;
	
	int thresholdLoc;
	
	// Path to the HTML file with the table of project descriptions
	public String PATH_TO_DESCRIPTION_FILE = 
			"C:\\PROJECTS\\PURE\\AkkaProjectsInfo\\webpage\\akkaprojects.html";
	public Document descriptionDoc;
	
	
	Analyzer analyzer = new Analyzer();
	
	
	public AnalyzerRunner(Properties props) 
	{
		projectRoot = props.getProperty("downloadroot");
		rejectRoot = props.getProperty("rejectroot");
		keywords = (props.getProperty("searchkeyword")).split(" ");
		thresholdLoc = Integer.parseInt(props.getProperty("minlines"));
	}
	
	
	
	public void analyzeAll()
	{
		File projectDescriptionFile = new File(PATH_TO_DESCRIPTION_FILE);
		try {
			descriptionDoc = Jsoup.parse(projectDescriptionFile, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		/**
		 * Get all projects in the project folder
		 */
		
		long starttime = System.currentTimeMillis();
		System.out.print("Finding all projects...");
		File[] projects = analyzer.getDirectoryContents(projectRoot);
		System.out.println(System.currentTimeMillis()-starttime + "ms");
		
		
		
		
		/**
		 * Fill out the ProjectData objects for each project
		 */
		
		List<File> validProjects = Arrays.asList(projects);
		int count = 1;
		
		starttime = System.currentTimeMillis();
		
		List<ProjectData> projectDataList = new ArrayList<ProjectData>();
		for(File project : validProjects) {
			ProjectData pd = new ProjectData();	// Object for storing all the data about a project
			pd.setKeywords(keywords);			// Set the keywords of the ProjecData object
			pd.projectFolder = project;			// Set the root project folder
			
			pd.setFiles(analyzer.getAllFiles(project));	// Set the file list in the ProjectData object
			if (pd.initializeMatrix() < 0 ) {
				// There weren't enough files in this project, so remove
				continue;
			}
			
			System.out.println("Collecting data for " + project.getName() + "..." + 
						" (" + count + "/" + validProjects.size() + ")");
			count++;
			
			if (analyzer.checkLOC(pd, thresholdLoc, rejectRoot)) {
				analyzer.fillAllData(pd, descriptionDoc);
				projectDataList.add(pd);
			}
			
		}
		
		System.out.println("Done collecting data.");
		System.out.println(System.currentTimeMillis()-starttime + "ms");
		
		
		/**
		 * Print out the results
		 */
		System.out.println("RESULTS: \n\n");
		for (ProjectData pd : projectDataList) {
			if (pd.linecount > thresholdLoc) {
				Presenter.printProjectData(pd);
			}
			
		}
		
		Presenter.printProjectDataHTML(projectDataList, thresholdLoc, false);
		Presenter.printProjectDataHTML(projectDataList, thresholdLoc, true);
		
		
		
		
	}
	
	
	


	
	
	
	
	
	



}





