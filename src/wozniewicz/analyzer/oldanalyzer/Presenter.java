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











