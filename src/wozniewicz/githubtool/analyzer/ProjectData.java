package wozniewicz.githubtool.analyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to store data related to a project (LOC, .scala files, keyword data, etc.)
 * Also makes extraction easy, by giving the ability to search files by keyword and vice versa
 * @author Martin Wozniewicz
 *
 */
public class ProjectData {
	
	// The project's main folder
	public File projectFolder;
	
	// Total lines of code in the project
	public int linecount; 			
	
	// All the files in the project
	public List<File> files; 	
	
	// List of the keywords
	public List<String> keywords;
	
	// A matrix storing keyword and file booleans (1 = file contains it, 0=it doesn't)
	public boolean [][] matrix;
	
	// String representation of URL to project's github
	public String url;
	
	public String description;
	public String comments;
	
	
	/**
	 * Given a list of files, sets the 'files' array to its contents (as one of the axes on the matrix)
	 * @param fileList
	 */
	public void setFiles(List<File> fileList)
	{
		this.files = fileList;
	}
	
	/**
	 * Sets the keyword array to the array passed in (used to keep track of one axis of the matrix)
	 * @param keywordArr
	 */
	public void setKeywords(String[] keywordArr)
	{
		this.keywords = Arrays.asList(keywordArr);
	}
	
	
	public int initializeMatrix(int numKeywords, int numFiles)
	{
		if (numKeywords <= 0 || numFiles <= 0) {
			//System.out.println("ERROR: Attempting to initialize 0-dim. matrix!");
			return -1;
		}
		matrix = new boolean[numKeywords][numFiles];
		for (int k=0; k<numKeywords; k++) {
			for (int f=0; f<numFiles; f++) {
				// Set initial values to 'false'
				matrix[k][f] = false;
			}
		}
		return 0;
	}
	
	public int initializeMatrix()
	{
		return initializeMatrix(keywords.size(), files.size());
	}
	
	/**
	 * Set the 'contains' matrix at [k,f] to a given value 
	 * @param k The 'keyword' coordinate
	 * @param f The 'file' coordinate
	 * @param val The value to set that square to 
	 */
	public void setMatrix(int k, int f, boolean val) {
		if (k >= keywords.size() || f >= files.size()) {
			System.out.println("ERROR: Attempted out-of-bounds matrix access");
			return;
		}
		matrix[k][f] = val;
	}
	
	/**
	 * Fetch the keyword at an index
	 * @param index
	 * @return
	 */
	public String getKeyword(int index)
	{
		if (index >= keywords.size()) {
			return null;
		}
		else return keywords.get(index);
	}
	
	/**
	 * Fetch the file at an index
	 * @param index
	 * @return
	 */
	public File getFile(int index)
	{
		if (index >= files.size()) {
			return null;
		}
		else return files.get(index);
	}
	
	/**
	 * Gets the files containing a given keyword, from the matrix
	 * @param k
	 * @return
	 */
	public List<File> getFilesByKeyword(String k)
	{
		int index = keywords.indexOf(k);
		if (index < 0) {
			System.out.println("Keyword " + k + " not found!");
			return null;
		}
		
		return getFilesByKeyword(index);
	}
	
	public List<File> getFilesByKeyword(int indexOfKeyword)
	{
		List<File> result = new ArrayList<File>();
		
		for (int i=0; i<files.size(); i++)
		{
			if (matrix[indexOfKeyword][i]) { 
				result.add(files.get(i));
			}
		}
		
		return result; 
	}
	
	/**
	 * Gets the keywords in a given files, from the  matrix
	 * @param f
	 * @return
	 */
	public List<String> getKeywordsByFile(File f)
	{
		int index = files.indexOf(f);
		if (index < 0) {
			System.out.println("File " + f + " not found!");
			return null;
		}
		
		return getKeywordsByFile(index);
	}
	
	public List<String> getKeywordsByFile(int indexOfFile)
	{
		List<String> result = new ArrayList<String>();
		
		for (int i=0; i<keywords.size(); i++)
		{
			if (matrix[i][indexOfFile]) { 
				result.add(keywords.get(i));
			}
		}
		
		return result; 
	}
}
