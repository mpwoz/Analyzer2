package wozniewicz.analyzer.oldanalyzer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
	
/**
 * Main application - analyzes downloaded github projects
 * @author Martin Wozniewicz
 *
 */
public class Main {

	/* CONFIG */
	// projectRoot: the root of all the downloaded project folders
	static String projectRoot = 
			"C:\\PROJECTS\\PURE\\akkaappsAll";
	
	static String rejectRoot =
			"C:\\PROJECTS\\PURE\\rejectApps";
	
	// Keywords : all keywords to search for in projects
	static String[] keywords = { 
		"akka.actor",
		"scala.actor"
	};
	
	// Minimum lines of code to take a project under consideration
	static int THRESHOLD_LOC = 500;
	
	// Path to the HTML file with the table of project descriptions
	public static String PATH_TO_DESCRIPTION_FILE = 
			"C:\\PROJECTS\\PURE\\AkkaProjectsInfo\\webpage\\akkaprojects.html";
	public static Document descriptionDoc;
	
	public static void main(String[] args)
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
		File[] projects = Analyzer.getDirectoryContents(projectRoot);
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
			
			pd.setFiles(Analyzer.getAllFiles(project));	// Set the file list in the ProjectData object
			if (pd.initializeMatrix() < 0 ) {
				// There weren't enough files in this project, so remove
				continue;
			}
			
			System.out.println("Collecting data for " + project.getName() + "..." + 
						" (" + count + "/" + validProjects.size() + ")");
			count++;
			
			if (Analyzer.checkLOC(pd)) {
				Analyzer.fillAllData(pd);
				projectDataList.add(pd);
			}
			
			// TODO DEBUG ONLY
//			if (count > 10) {
//				break;
//			}
		}
		
		System.out.println("Done collecting data.");
		System.out.println(System.currentTimeMillis()-starttime + "ms");
		
		
		/**
		 * Print out the results
		 */
		System.out.println("RESULTS: \n\n");
		for (ProjectData pd : projectDataList) {
			if (pd.linecount > THRESHOLD_LOC) {
				Presenter.printProjectData(pd);
			}
			
		}
		
		Presenter.printProjectDataHTML(projectDataList, THRESHOLD_LOC, false);
		Presenter.printProjectDataHTML(projectDataList, THRESHOLD_LOC, true);
		
		
		
		
	}
	
	
	


	
	
	
	
	
	



}





