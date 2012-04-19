package wozniewicz.githubtool.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for analyzing .scala source files
 * @author Martin Wozniewicz
 *
 */
public class AnalyzerUtil {
	
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
	 * Searches all the files in the project for the keyword.
	 * @param project the full path to the project
	 * @param keyword the keyword to search for
	 */
	public List<File> checkProjectForKeyword(File project, String keyword) {
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
	public static List<File> getDirectoryContents(String dir) 
	{
		File folder = new File(dir);
		File[] files = folder.listFiles();
		return files != null ? Arrays.asList(files) : null;
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
	 * Gets all .scala files in a directory
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
	 * Fills out the matrix, and linecount fields of a ProjectData
	 * @param pd
	 */
	public static void fillAllData(ProjectData pd, String finishedRoot)
	{
		fillMatrix(pd);
		File gitdir = getGitSubdirectory(pd);
		pd.url = Parser.getURLFromFile(gitdir);
		pd.description = pd.comments = ""; // empty for new projects
	}
	
	/**
	 * Gets the subdirectory which contains the author and project info, one level below project_folder
	 * @param pd
	 * @return
	 */
	private static File getGitSubdirectory(ProjectData pd) {
		File projfolder = pd.projectFolder;
		List<File> contents = getDirectoryContents(projfolder.getAbsolutePath());
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
		for (int f=0; f<pd.files.size(); f++ ) {
			int k = 0;
			boolean first = fileHasKeyword(pd.getFile(f), pd.getKeyword(k));
			pd.setMatrix(k, f, first);
			
			/* Only proceed to the other keywords if the file has the first (filter) keyword */
			if (first) {
				for (k = 1; k<pd.keywords.size(); k++) {
					pd.setMatrix(k, f, fileHasKeyword(pd.getFile(f), pd.getKeyword(k)));
				}
			}
		}
	}


	/**
	 * Searches the list of files for the ones which contain a given keyword.  Returns a list of them
	 * @param projectFiles the files to search
	 * @param keyword 
	 * @return 
	 */
	private List<File> getFilesContainingKeyword(List<File> projectFiles, String keyword) {
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
					input.close();
					return true;
				}
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}


	/**
	 * Counts lines and rejects the short ones
	 * @param windows 
	 * 
	 */
	public static boolean checkLOC(ProjectData pd, int threshold, String rejectRoot, boolean windows) {
		pd.linecount = ClocProject(pd.projectFolder, windows);
		if (pd.linecount < threshold) {
			return false;
		}
		return true;
	}


	/**
	 * Runs the CLOC tool on a project folder, and returns the lines of code. 
	 * @param project_path
	 * @return
	 */
	private static int ClocProject(String project_path, boolean windows) 
	{
		//System.out.print("Counting lines of scala code in " + project_path + "...");
		try {
	        Runtime rt = Runtime.getRuntime();
	        
	        String cmd;
	        if (windows) cmd = "lib/cloc.exe ";
	        else cmd = "lib/cloc.pl ";
	        cmd += "--quiet --progress-rate=0 --skip-uniqueness " +
	        		"--match-f=\\.scala$ " + project_path;
	        Process pr = rt.exec(cmd);
	        
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


	private static int ClocProject(File project, boolean windows)
	{
		String path = project.getAbsolutePath();
		return ClocProject(path, windows);
	}

	
	
	
} // class Analyzer
