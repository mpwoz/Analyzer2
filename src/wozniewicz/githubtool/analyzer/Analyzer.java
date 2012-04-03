package wozniewicz.githubtool.analyzer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import wozniewicz.githubtool.Stopwatch;
	
/**
 * Main application - analyzes downloaded github projects
 * @author Martin Wozniewicz
 *
 */
public class Analyzer {

	/* CONFIG */
	// projectRoot: the root of all the downloaded project folders
	String projectRoot;
	
	String rejectRoot;
	
	String finishedRoot;
	
	String[] keywords;
	
	int thresholdLoc;
		
	List<String> done = new ArrayList<String>();
	
	Stopwatch t = new Stopwatch();
	
	
	public Analyzer(Properties props) 
	{
		projectRoot = props.getProperty("downloadroot");
		rejectRoot = props.getProperty("rejectroot");
		finishedRoot = props.getProperty("finishedroot");
		keywords = (props.getProperty("searchkeyword")).split(" ");
		thresholdLoc = Integer.parseInt(props.getProperty("minlines"));
	
	}
	
	
	
	public List<ProjectData> analyzeNewProjects()
	{

		/**
		 * Get all projects in the project folder
		 */
		
		System.out.print("Finding all new projects...");
		List<File> projects = AnalyzerUtil.getDirectoryContents(projectRoot);
		
		
		/**
		 * Fill out the ProjectData objects for each project
		 */
		
		int count = 1;
		
		t.Start();
		
		List<ProjectData> projectDataList = new ArrayList<ProjectData>();
		for(File project : projects) {
			if (done.contains(project.getName())) {
				continue;
			}
			ProjectData pd = new ProjectData();	// Object for storing all the data about a project
			pd.setKeywords(keywords);			// Set the keywords of the ProjecData object
			pd.projectFolder = project;			// Set the root project folder
			
			pd.setFiles(AnalyzerUtil.getAllFiles(project));	// Set the file list in the ProjectData object
			if (pd.initializeMatrix() < 0 ) {
				// There weren't enough files in this project, so remove
				continue;
			}
			
			System.out.println("Collecting data for " + project.getName() + "..." + 
						" (" + count + "/" + projects.size() + ")");
			count++;
			
			if (AnalyzerUtil.checkLOC(pd, thresholdLoc, rejectRoot)) {
				AnalyzerUtil.fillAllData(pd, finishedRoot);
				if (pd.getFilesByKeyword(0).size() > 0)
				{
					projectDataList.add(pd);
				}
				else moveToDone(pd);
			}
			
		}
		t.Stop();
		
		
		
		/**
		 * Create list of the valid results
		 */
		List<ProjectData> res = new ArrayList<ProjectData>();
		for (ProjectData pd : projectDataList) {
			if (!done.contains(pd.projectFolder.getName()))
				res.add(pd);
			moveToDone(pd);
		}
		return res;
			
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




