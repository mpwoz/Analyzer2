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
	}
	
	public void writeProjectTable(ProjectData pd, String filename)
	{
		String out = "<div class=\"project\">";
		for(String keyword : pd.keywords) {
			out += "\t<h3>" + keyword + "</h3>\n"
				+ "\t<ul>\n"; 
			
			for(File file : pd.getFilesByKeyword(keyword)) {
				String name = file.getPath(); 
				String parent = pd.projectFolder.getName();
				
				int offset = parent.length();
				int i = name.lastIndexOf(parent);
				if (i >= 0) 
					name = name.substring(i + offset);
				
				out += "\t\t<li>" + name + "</li>\n";
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

	public void startKeywordFile(String filename) {
		makeEmptyFile(filename);
	}
	
	/**
	 * Writes all the files by keyword on a page (in all projects)
	 * @param projects
	 * @param filename
	 */
	public void addKeywords(List<ProjectData> projects, String filename) {
		if (projects.size() < 1) return;
		
		List<String> keywords = projects.get(0).keywords; 
		int numkeys = keywords.size();
		
		for (int i=0; i<numkeys; i++) {
			PresenterUtil.appendToFile(filename, makeKeywordTable(projects, keywords.get(i)));
		}
	}
	
	public String makeKeywordTable(List<ProjectData> projects, String keyword)
	{
		String out = "<table>\n";
		out += "\t<tr>\n"
				+ "\t\t<th>Project Name</th>\n"
				+ "\t\t<th># files</th>\n"
				+ "\t\t<th>Files containing " + keyword + "</th>\n";
		for(ProjectData project : projects) 
		{
			List<File> files = project.getFilesByKeyword(keyword);
			int numFiles = files.size();
			
			if (numFiles == 0) continue;
			
			out += "\t<tr>\n"
					+ "\t\t<td>" + project.projectFolder.getName() + "</td>\n"
					+ "\t\t<td>" + numFiles + "</td>\n"
					+ "\t\t<td>\n";
			for (File f : files)
			{
				String name = f.getPath(); 
				String parent = project.projectFolder.getName();
				
				int offset = parent.length();
				
				int i = name.lastIndexOf(parent);
				int j = name.indexOf("src");
				
				int index = Math.min(i, j);
				
				if (index >= 0) 
					name = name.substring(index);
				
				index = name.indexOf('\\');
				if (index >= 0 && index < name.length() - 1)
					name = name.substring(index);
				
				out += "\t\t\t" + name + "<br>\n";
			}
			out += "\t\t</td>\n"
				+ "\t</tr>\n";
		}
		out += "</table>\n";
		return out;		
	}

	
	
	
	
	
}











