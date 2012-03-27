package wozniewicz.analyzer.oldanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for analyzing .scala source files
 * @author Martin Wozniewicz
 *
 */
public class Analyzer {
	
	/**
	 *  Custom filter to look for .scala files
	 */
	private static class ExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			String ext = ".scala";
			return name.endsWith(ext);
		}
	}	
	
	/**
	 * Given a list of 
	 * @param projects array of ALL projects, to be checked for eligibility
	 * @param minLoc minimum lines of code to be accepted
	 * @return List<File> of all the qualifying projects
	 */
	public static List<File> getProjectsForAnalysis(File[] projects, int minLoc) {
		List<File> validProjects = new ArrayList<File>();
		for (int i=0; i<projects.length; i++) {
			
			int lines = ClocProject(projects[i]);
			if (lines > minLoc) {
				validProjects.add(projects[i]);
			}
			
		}
		return validProjects;
	}
	
	
	/**
	 * Runs the CLOC tool on a project folder, and returns the lines of code. 
	 * @param project_path
	 * @return
	 */
	private static int ClocProject(String project_path) 
	{
		//System.out.print("Counting lines of scala code in " + project_path + "...");
		try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec("cloc.exe --quiet --progress-rate=0 --match-f=\\.scala$ " + project_path);
            
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

            String line=null;

            while((line=input.readLine()) != null) {
            	if (line.indexOf("SUM") != -1) {
            		String[] tokens = line.split("[\\s]+");		// Split on whitespace
            		return Integer.parseInt(tokens[4]);			// Fourth column corresponds to LOC
            	}
            }

            pr.waitFor();
        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
		return 0; 
	}
	
	private static int ClocProject(File project)
	{
		String path = project.getAbsolutePath();
		return ClocProject(path);
	}
	
	/**
	 * Searches all the files in the project for the keyword.
	 * @param project the full path to the project
	 * @param keyword the keyword to search for
	 */
	public static List<File> checkProjectForKeyword(File project, String keyword) {
		List<File> projectFiles = new ArrayList<File>();
		getAllFiles(projectFiles, project, new ExtensionFilter() );
		
		List<File> keywordFiles = getFilesContainingKeyword(projectFiles, keyword);
		
		return keywordFiles;
		
	}

	
	/**
	 * Finds all files at the top-level of a directory
	 * @param dir the root directory which should be searched 
	 * @return Files/folders inside a directory
	 */
	public static File[] getDirectoryContents(String dir) 
	{
		File folder = new File(dir);
		return folder.listFiles();
	}
	
	
	/**
	 * Recursively gets all the files under a given directory and add them to a list if they pass the filter
	 * @param files list of files to add to
	 * @param dir root directory to start search in
	 * @param filter a FilenameFilter that can narrow down the files returned
	 */
	private static void getAllFiles(List<File> files, File dir, FilenameFilter filter)
	{
		File[] contents = dir.listFiles();
		if (contents == null) 
			return;
		
		for (int i=0; i<contents.length; i++) {
			if (contents[i].isDirectory()) {						// If directory, recurse into it
				getAllFiles(files, contents[i], filter );
			}
			else if (filter.accept(dir, contents[i].getName())) {	// add files that pass the filter to the list
				files.add(contents[i]);
			}
		}
	}
	
	/**
	 * Interface method.  Gets all .scala files in a directory
	 * @param dir The directory to search
	 * @return List of all the .scala files within the project
	 */
	public static List<File> getAllFiles(File dir)
	{
		List<File> result = new ArrayList<File>();
		getAllFiles(result, dir, new ExtensionFilter() );
		return result;
	}

	/**
	 * Searches the list of files for the ones which contain a given keyword.  Returns a list of them
	 * @param projectFiles the files to search
	 * @param keyword 
	 * @return 
	 */
	private static List<File> getFilesContainingKeyword(List<File> projectFiles, String keyword) {
		List<File> filesWithKeyword = new ArrayList<File>();
		for (File currentFile: projectFiles) {
			if (fileHasKeyword(currentFile, keyword)) {
				filesWithKeyword.add(currentFile);
			}
			
		}
		return filesWithKeyword;
	}



	/**
	 * Line-by-line search of file, to check if it contains the keyword. Returns true as soon as it's found. 
	 * @param currentFile
	 * @param keyword
	 * @return
	 */
	private static boolean fileHasKeyword(File currentFile, String keyword) {
		
		BufferedReader input = null;
		
		try {
			input = new BufferedReader (new FileReader( currentFile ));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		String line = null;
		
		try {
			while (( line = input.readLine()) != null ) {
				if (line.indexOf(keyword) != -1) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/** 
	 * Fills out the matrix, and linecount fields of a ProjectData
	 * @param pd
	 */
	public static void fillAllData(ProjectData pd)
	{
		fillMatrix(pd);
		
		File gitdir = getGitSubdirectory(pd);
				
		pd.urlGit = Parser.getURLFromFile(gitdir);
		
		List<String> data = Parser.findProjectData(pd.projectFolder);
		if (data != null) {
			pd.description = data.get(0);
			pd.comments = data.get(1);
		} else {
			pd.description = pd.comments = "Unavailable";
		}
	}
	
	/**
	 * Gets the subdirectory which contains the author and project, one level below project_folder
	 * @param pd
	 * @return
	 */
	private static File getGitSubdirectory(ProjectData pd) {
		File projfolder = pd.projectFolder;
		File[] contents = getDirectoryContents(projfolder.getAbsolutePath());
		for (File f : contents)
		{
			if ( f.getName().indexOf(pd.projectFolder.getName()) >= 0 ) {
				return f;
			}
		}
		return null;
	}


	/**
	 * Calls Analyzer methods to fill out the matrix of the given ProjectData
	 * @param pd
	 */
	public static void fillMatrix(ProjectData pd)
	{
		for (int k = 0; k<pd.keywords.size(); k++) {
			for (int f=0; f<pd.files.size(); f++ ) {
				pd.setMatrix(k, f, fileHasKeyword(pd.getFile(f), pd.getKeyword(k)));
			}
		}
	}


	/**
	 * Counts lines and rejects the short ones
	 * 
	 */
	public static boolean checkLOC(ProjectData pd) {
		pd.linecount = ClocProject(pd.projectFolder);
		if (pd.linecount < Main.THRESHOLD_LOC) {
			File rejdir = new File(Main.rejectRoot);
			File curr = pd.projectFolder;
			if (!curr.renameTo(new File(rejdir, curr.getName()))) {
				System.out.println("Failed to reject file "+ curr.getName() + " to " + rejdir.getAbsolutePath());
			}
			return false;
		}
		return true;
	}

	
	
	
} // class Analyzer
