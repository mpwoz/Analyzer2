package wozniewicz.analyzer.oldanalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class parses all kinds of data for Analyzer
 * @author Martin Wozniewicz
 *
 */
public class Parser {
	
	
	/**
	 * Gets the URL to the github repository of a project, given the folder it is in
	 * 	This works due to the naming conventions of projects downloaded from github
	 * @param projectFile
	 * @return
	 */
	public static String getURLFromFile(File projectFile)
	{
		// The entire name.  Format   <author>-<projectname>-<revision>
		String folderName;
		folderName = projectFile.getName();
		
		// The first dash, and the last dash
		int dash1 = folderName.indexOf('-');
		int dash2 = folderName.lastIndexOf('-');
		
		String author = folderName.substring(0, dash1);
		String project = folderName.substring(dash1 + 1, dash2);
		
		// Format of URL https://github.com/<author>/<project>
		String URL = "https://github.com/" + author + "/" + project;
		return URL;
	}
	
	
	public static List<String> findProjectData(File projectFile) 
	{
		Document descFile = Main.descriptionDoc;
		
		// Finds the table row which contains the project's name
		String selector = "tr:contains(" + projectFile.getName() + ")";
		
		Element row = descFile.select(selector).first();
		if (row == null) return null;
		
		Elements children = row.children();
		
		Element desc = children.get(1);
		Element comm = children.get(2);

		String description = desc.text();
		String comments = comm.text();
		
		List<String> result = new ArrayList<String>();
		result.add(description);
		result.add(comments);
		
		return result;
	}
}
