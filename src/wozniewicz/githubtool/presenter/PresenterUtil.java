package wozniewicz.githubtool.presenter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class PresenterUtil {
	
	/**
	 * Gets only the path relative to 'root' to the files
	 * @param files the files to cut
	 * @param root the File describing root folder
	 * @return List of strings with the paths, for printing
	 */
	public List<String> getPathsRelativeToRoot(List<File> files, File root)
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
	
	
	
	/** Construct an HTML row of all the data in the list */
	public static String makeHTMLRow(List<String> data) {
		String s;
		s = "<tr>";
		for (String td : data) {
			s += makeHTMLData(td);
		}
		s += "</tr>";
		return s;
	}
	
	public static String makeHTMLLink(String url, String content) {
		return "<a href=\"" + url + "\">" + content + "</a>";
	}
		
	public static String makeHTMLData(String data) {
		return makeHTMLData(data, "");
	}
	
	public static String makeHTMLData(String data, String td_class)
	{
		return "<td class=\"" + td_class + "\">" + data + "</td>";
	}
	
	public static String makeHTMLHeader(String header) {
		return "<th>" + header + "</th>";
	}
	
	
	
	
	/** Write content to the given file. */
	static void writeFile(String filename, String contents) {
	  try {
		  Writer out = new OutputStreamWriter(new FileOutputStream(filename));
		  out.write(contents);
		  out.close();
	  } catch( Exception e) {
		  e.printStackTrace();
	  }
	  	
	}
	 
	/** Append to the end of a file */
	static void appendToFile(String filename, String contents) 
	{
		try {
		    BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));
		    out.write(contents);
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
