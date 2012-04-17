package wozniewicz.githubtool.analyzer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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

	boolean windows;
	
	List<String> done = new ArrayList<String>();
	
	Stopwatch t = new Stopwatch();

	
	
	public Analyzer(Properties props) 
	{
		projectRoot = props.getProperty("downloadroot");
		rejectRoot = props.getProperty("rejectroot");
		finishedRoot = props.getProperty("finishedroot");
		
		String delim = props.getProperty("searchdelimeter");
		delim = "[" + delim + "]+"; // allow for one or more delimeter characters
		
		String keystring = props.getProperty("searchkeyword");
		keywords = keystring.split(delim);
		
		thresholdLoc = Integer.parseInt(props.getProperty("minlines"));
		windows = Boolean.parseBoolean(props.getProperty("windows"));
	
	}
	
	
	
	public List<ProjectData> analyzeNewProjects(int limit)
	{
		/**
		 * Get all projects in the project folder
		 */
		
		List<File> allProjects = AnalyzerUtil.getDirectoryContents(projectRoot);
		
		
		/**
		 * Fill out the ProjectData objects for each project
		 */
		
		int count = 1;
		
		t.Start();
		
		List<ProjectData> projectStats = new ArrayList<ProjectData>();
		for(File project : allProjects) {
			
			String name = project.getName();
			
			// Make sure we don't analyze the same project twice
			if (done.contains(name))
				continue;
			done.add(name);
			
			// All the files in the project's folder
			List<File> projectFiles = AnalyzerUtil.getAllFiles(project);
			
			// If there aren't any files with the extension we want, don't use this project
			if (projectFiles.size() == 0) 
				continue;
			
			
			System.out.println("Collecting data for " + name + "..." + 
						" (" + count + " of " + allProjects.size() + ")");	// progress indicator
			count++;
			
			
			ProjectData pd = new ProjectData(project, projectFiles, keywords);	
			
			
			if (AnalyzerUtil.checkLOC(pd, thresholdLoc, rejectRoot, windows)) {
				AnalyzerUtil.fillAllData(pd, finishedRoot);
				if (pd.getFilesByKeyword(0).size() > 0)
				{
					projectStats.add(pd);
				}
			}
			
			if (limit > 0 && count > limit) break;
			
		}
		t.Stop();
		
		
		return projectStats;
			
	}
	
	
	void moveToDone(ProjectData pd) 
	{
		done.add(pd.projectFolder.getName());
		
		/*
		File curr = pd.projectFolder;
		File rej = new File(finishedRoot, pd.projectFolder.getName());
		
		System.out.println("Moving " + curr.getPath() + " to " + rej.getPath());
		if (!curr.renameTo(rej)) {
			System.out.println("Failed.");
		}
		*/
	}

	
	
	
	
	
	



}





