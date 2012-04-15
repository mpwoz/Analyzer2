package wozniewicz.githubtool.presenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import wozniewicz.githubtool.analyzer.ProjectData;

/**
 * Class for presentation of project data
 * @author Martin Wozniewicz
 *
 */
public class Presenter {
	
	public void makeEmptyFile(String filename)
	{
		PresenterUtil.writeFile(filename, "");
	}
	
	/** Creates and writes the start of an HTML table in 'filename' */
	public void startProjectFile(List<String> headers, String filename)
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
		
		s += PresenterUtil.makeHTMLRow(headers);
		
		PresenterUtil.writeFile(filename, s);
		
		String logfile = filename + ".txt";
		makeEmptyFile(logfile);
	}
	
	
	/**
	 * Adds all the projectData objects in a list, to the given file
	 * @param projects
	 * @param filename
	 */
	public void addProjects(List<ProjectData> projects, String filename) {
		for (ProjectData pd : projects) {
			writeProjectData(pd, filename);
		}
	}
	
	/** Adds a project's data to the table in the file given by 'filename'
	 * That table should have first been initialized using startProjectFile()
	 * @param pd
	 * @param filename
	 */
	public void writeProjectData(ProjectData pd, String filename)
	{		
		
		List<String> data = new ArrayList<String>();
		data.add(PresenterUtil.makeHTMLLink(pd.url, pd.projectFolder.getName()));
		data.add(String.valueOf(pd.linecount));
		data.add(pd.description);
		
		PresenterUtil.appendToFile(filename, PresenterUtil.makeHTMLRow(data));
		
		String logstr = pd.projectFolder.getName();
		logstr += "(" + pd.getFilesByKeyword(0).size() + ")";
		PresenterUtil.appendToFile(filename+".txt", logstr + '\n');
	}
	
	public void writeProjectTable(ProjectData pd, String filename)
	{
		String out = "<div class=\"project\">";
		for(String keyword : pd.keywords) {
			out += "\t<h3>" + keyword + "</h3>\n"
				+ "\t<ul>\n"; 
			
			for(File file : pd.getFilesByKeyword(keyword)) {
				out += "\t\t<li>" + file.getName() + "</li>\n";
			}
			
			out += "\t</ul>\n";
		}
		out += "</div>";
		
		PresenterUtil.appendToFile(filename, out);
	}
	
	
	public void endProjectFile(String filename)
	{
		String s = "</table>";
		PresenterUtil.appendToFile(filename, s);
	}

	public void addProjectTables(List<ProjectData> projects,
			String filename) {
		for (ProjectData pd : projects) {
			writeProjectTable(pd, filename);
		}
		
	}
	

	
	
	
	
	
}











