package wozniewicz.analyzer.oldanalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for presentation of Analyzer results
 * @author Martin Wozniewicz
 *
 */
public class Presenter {
	
	/**
	 * Gets only the path relative to 'root' to the files
	 * @param files the files to cut
	 * @param root the File describing root folder
	 * @return List of strings with the paths, for printing
	 */
	public static List<String> getPathsRelativeToRoot(List<File> files, File root)
	{
		List<String> relativePaths = new ArrayList<String>();
		for(File current : files) {
			String r = root.getName();
			String f = current.getAbsolutePath();
			int index = f.indexOf(r);
			String path = f.substring(index + r.length());
			relativePaths.add(path);
		}
		return relativePaths;
		
	}

	/**
	 * Plain text output for the data in a ProjectData object
	 * @param pd
	 */
	public static void printProjectData(ProjectData pd) {
		String divider = "-----------------------------------";
		String testfilter = "\\test\\";	// Used to find 'test' folder and print separately
		
		String output = "\n" + divider + "\n";
		output += pd.projectFolder.getAbsolutePath() + "\n" + 
				"LINES =" + pd.linecount + "\n";
		
		List<File> files;
		List<String> paths;
		
		for (String keyword : pd.keywords) {
			files = pd.getFilesByKeyword(keyword);
			if (files.size() <= 0) {
				output += "No files contain " + keyword + "\n";
				continue;
			} else {
				output += "Files containing \"" + keyword + "\":\n";
			}
			
			paths = getPathsRelativeToRoot(files, pd.projectFolder);
			
			List<String> temp = new ArrayList<String>();
			for(String path : paths) {
				if (path.indexOf(testfilter) >= 0) {
					temp.add(path);
				} else 
					output += "\t\t" + path + "\n";
			}
			if (temp.size() > 0) {
				output += "\tTest: \n";
				for (String testfile : temp) {
					output += "\t\t" + testfile + "\n";
				}
			}	
		}
		
		output += divider + "\n\n";
		
		
		System.out.println(output);
	}
	

	/**
	 * Outputs an HTML File of all the project data's above THRESHOLD_LOC lines
	 * @param projectDataList
	 * @param tHRESHOLD_LOC
	 */
	public static void printProjectDataHTML(List<ProjectData> projectDataList,
			int THRESHOLD_LOC, boolean verbose) {
		
		String out = "<table class=\"project-data\">" + "\n";
		
		out = appendColumnHeaders(out, verbose);
		
		for (ProjectData pd : projectDataList) {
			if (pd.linecount > THRESHOLD_LOC) {
				out = appendProjectRow(out, pd, verbose);
			}
		}
		
		out += "</table>";
		
		long timestamp = System.currentTimeMillis();
		
		String id; 
		if (verbose) id = "presentation";
		else id = "internal";
		
		String filename = "../HTML_out/" + id + "_" + timestamp + ".html";
		try {
			writeFile(filename, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	 private static String appendColumnHeaders(String out, boolean verbose) {
		if (!verbose)
			out +=	"\t<tr>\n" +
					"\t\t<th>Name</th>\n" +
					"\t\t<th>LOC</th>\n" +
					"\t\t<th>Description\n" +
					"</th>\n\t</tr>" + "\n";
		else 
			out +=	"\t<tr>\n" +
					"\t\t<th>Name</th>\n" +
					"\t\t<th>LOC</th>\n" +
					"\t\t<th>Description\n" +
					"\t\t<th>Comments\n" +
					"</th>\n\t</tr>" + "\n";
		return out;
					
		
	}

	private static String appendProjectRow(String out, ProjectData pd,
			boolean verbose) {
		
		 out += "\t<tr>\n";
		
		// Title of project with link to github
		out += "\t\t<td>"
			+ "<a href=\"" + pd.urlGit + "\">"
			+ pd.projectFolder.getName() 
			+ "</a>"
			+ "</td>\n";
		
		// Lines of Code
		out += "\t\t<td>" + pd.linecount + "</td>\n";
		
		out += "\t\t<td>" + pd.description + "</td>\n";
		
		// Only print comments if verbose
		if (verbose)
			out += "\t\t<td>" + pd.comments + "</td>\n";
				
		out += "\t</tr>\n";
		 
		return out;
	}

	/**
	 * Nice text output for the stats of each project/keyword
	 * @param paths
	 * @param project
	 * @param keyword
	 */
	public static void printProjectStats(List<String> paths, File project,
			String keyword) {
		
		// Don't print if there are no files with the keyword
		if (paths.size() < 1) {
			return;
		}
		List<String> temp = new ArrayList<String>();
		String divider = "-----------------------------------\n";
		String testfilter = "\\test\\";	// Used to find 'test' folder and print separately
		
		String output = "\n" + divider;
		output += project.getAbsolutePath() + "\n";
		
		output += "Files containing \"" + keyword + "\" : \n";
		for(String path : paths) {
			if (path.indexOf(testfilter) >= 0) {
				temp.add(path);
			} else 
				output += "	" + path + "\n";
		}
		
		if (temp.size() > 0) {
			output += "Tests:\n";
			for (String s : temp) {
				output += "	" + s + "\n";
			}
		}
		output += divider;
		
		System.out.print(output);
	}

	/** Write content to the given file. */
	static void writeFile(String filename, String contents) throws IOException  {
	
	  Writer out = new OutputStreamWriter(new FileOutputStream(filename));
	  try {
		  out.write(contents);
	  }
	  finally {
		  out.close();
	  }
	}
	
	
	/** Creates and writes the start of an HTML table in 'filename' */
	public static void startProjectFile(List<String> headers, String filename)
	{
		// Default headers
		if (headers == null) {
			headers = new ArrayList<String>();
			headers.add("Project");
			headers.add("LOC");
			headers.add("Description");
			startProjectFile(headers, filename);
		}
		
		
		String s = "<table>";
		s += "<tr>";
		for (String header : headers) {
			s += makeHTMLHeader(header);
		}
		s += "</tr>";
		
		try {
			writeFile(filename, s);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/** Adds a project's data to the table in the file given by 'filename'
	 * That table should have first been initialized using startProjectFile()
	 * @param pd
	 * @param filename
	 */
	static void writeProjectData(ProjectData pd, String filename)
	{		
		
		List<String> data = new ArrayList<String>();
		data.add(makeHTMLLink(pd.urlGit, pd.projectFolder.getName()));
		data.add(String.valueOf(pd.linecount));
		data.add(pd.description);
		
		appendToFile(filename, makeHTMLRow(data));
	}
	
	
	public static void endProjectFile(String filename)
	{
		String s = "</table>";
		appendToFile(filename, s);
	}
	
	/** Construct an HTML row of all the data in the list */
	static String makeHTMLRow(List<String> data) {
		String s;
		s = "<tr>";
		for (String td : data) {
			s += makeHTMLData(td);
		}
		s += "</tr>";
		return s;
	}
	
	static String makeHTMLLink(String url, String content) {
		return "<a href=\"" + url + "\">" + content + "</a>";
	}
	
	static String makeHTMLData(String data)
	{
		return "<td>" + data + "</td>";
	}
	
	static String makeHTMLHeader(String header) {
		return "<th>" + header + "</th>";
	}
	
	
	
	/** Append to the end of a file */
	static void appendToFile(String filename, String contents) 
	{
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
		    out.write(contents);
		    out.close();
		} catch (IOException e) {
		}
		
	}
	
}











