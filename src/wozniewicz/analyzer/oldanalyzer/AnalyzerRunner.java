package wozniewicz.analyzer.oldanalyzer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
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
	
	String finishedRoot;
	
	String[] keywords;
	
	int thresholdLoc;
	
	// Path to the HTML file with the table of project descriptions
	public String PATH_TO_DESCRIPTION_FILE = 
			"C:\\PROJECTS\\PURE\\AkkaProjectsInfo\\webpage\\akkaprojects.html";
	public Document descriptionDoc;
	
	
	Analyzer analyzer = new Analyzer();
	
	String filename;
	
	public AnalyzerRunner(Properties props, String filename) 
	{
		projectRoot = props.getProperty("downloadroot");
		rejectRoot = props.getProperty("rejectroot");
		finishedRoot = props.getProperty("finishedroot");
		keywords = (props.getProperty("searchkeyword")).split(" ");
		thresholdLoc = Integer.parseInt(props.getProperty("minlines"));
		
		this.filename = filename;
	}
	
	
	
	public void analyzeAll()
	{
	/*
	 	File projectDescriptionFile = new File(PATH_TO_DESCRIPTION_FILE);
	 	try {
			descriptionDoc = Jsoup.parse(projectDescriptionFile, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	*/	
		
		
		
		/**
		 * Get all projects in the project folder
		 */
		
		System.out.print("Finding all projects...");
		File[] projects = analyzer.getDirectoryContents(projectRoot);
		
		
		/**
		 * Fill out the ProjectData objects for each project
		 */
		
		List<File> validProjects = Arrays.asList(projects);
		int count = 1;
		
		long starttime = System.currentTimeMillis();
		
		List<ProjectData> projectDataList = new ArrayList<ProjectData>();
		for(File project : validProjects) {
			if (done.contains(project.getName())) {
				continue;
			}
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
				analyzer.fillAllData(pd, finishedRoot);
				if (pd.getFilesByKeyword(0).size() > 0)
				{
					projectDataList.add(pd);
				}
				else moveToDone(pd);
			}
			
		}
		
		System.out.println("Done collecting data.");
		System.out.println(System.currentTimeMillis()-starttime + "ms");
		
		
		/**
		 * Print out the results
		 */
		System.out.println("Printing results...\n");
		for (ProjectData pd : projectDataList) {
			if (!done.contains(pd.projectFolder.getName()))
			Presenter.writeProjectData(pd, filename);
			moveToDone(pd);
		}
			
	}
	
	List<String> done = new ArrayList<String>();
	
	void endProjectFile() {
		Presenter.endProjectFile(filename);
	}
	
	
	void moveToDone(ProjectData pd) 
	{
		done.add(pd.projectFolder.getName());
		
		File curr = pd.projectFolder;
		File rej = new File(finishedRoot, pd.projectFolder.getName());
		
		System.out.println("Moving " + curr.getPath() + " to " + rej.getPath());
		if (!curr.renameTo(rej)) {
			System.out.println("Failed.");
		}
	}

	
	
	
	
	
	



}





