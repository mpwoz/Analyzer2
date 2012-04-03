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

import wozniewicz.githubtool.analyzer.ProjectData;

/**
 * Class for presentation of project data
 * @author Martin Wozniewicz
 *
 */
public class Presenter {
	
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
	}
	
	/** Adds a project's data to the table in the file given by 'filename'
	 * That table should have first been initialized using startProjectFile()
	 * @param pd
	 * @param filename
	 */
	 void writeProjectData(ProjectData pd, String filename)
	{		
		
		List<String> data = new ArrayList<String>();
		data.add(PresenterUtil.makeHTMLLink(pd.url, pd.projectFolder.getName()));
		data.add(String.valueOf(pd.linecount));
		data.add(pd.description);
		
		PresenterUtil.appendToFile(filename, PresenterUtil.makeHTMLRow(data));
	}
	
	
	public void endProjectFile(String filename)
	{
		String s = "</table>";
		PresenterUtil.appendToFile(filename, s);
	}
	

	
	
	
	
	
}











