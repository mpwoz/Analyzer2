package wozniewicz.githubtool.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Downloader {
	private Properties props;	
	private int currentPage;
	private int maxPage;
	private static ExecutorService executor;
	
	// Keeps track of projects we've already downloaded.
	private ArrayList<String> completedProjects= new ArrayList<String>();
		
	/**
	 * Default constructor.  Provide the properties file to it
	 * @param props
	 */
	public Downloader(Properties props) {
		this.props = props;
		this.currentPage = 0;
		this.maxPage = getPageRange();
	}
	
	
	/**
	 * getPageRange() parses the github search result page-source to get total number of pages
	 * 	@return the last page number 
	 */
	public int getPageRange()
	{
		try {
			URL url= getURL(1);
			BufferedReader in = new BufferedReader(new InputStreamReader(url
					.openStream()));
			String inputLine;
			String fingerprint="<span class=\"current\">";		// This tag identifies the line of HTML which displays page-selector
			while ((inputLine = in.readLine()) != null) {
				
				if(inputLine.contains(fingerprint))
				{
					int s=inputLine.lastIndexOf("\"");			// The last "-symbol occurs before the last page number
					int e=inputLine.lastIndexOf("<");			// The closing tag, after last page number
					return Integer.parseInt(inputLine.substring(s+2, e));	// Parse out the last page number
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}


	/**
	 * Wrapper to download a given amount of result pages at a time
	 * Automatically increments internal counter
	 * @param n
	 * @return false if no more pages to download, true otherwise
	 */
	public boolean downloadNextPages(int n) 
	{
		boolean retval = true;
		
		int nthreads = Integer.parseInt(props.getProperty("threads"));
		executor = Executors.newFixedThreadPool(nthreads);
		while (n>0) {
			currentPage++;
			n--;
			if (!downloadCurrentPage()) {
				retval = false;
			}
		}
		// Wait until all threads are finished
		executor.shutdown();
		while (!executor.isTerminated()) {}
		return retval;
	}
	
	
	private boolean downloadCurrentPage()
	{
		if (currentPage > maxPage) {
			return false;
		}
		
		try {
			System.out.println("SEARCH PAGE:" + currentPage + " : " + getURL(currentPage));
			processSearchPage(getURL(currentPage));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERROR: Page not found... Skipping.");
		}
		
		return true;
		
	}
	
	
	
	
	
	/**
	 * getURL()
	 * 	URL of the github search results page for parameters specified in 'config' at the top
	 * 	@param pagenum The search results page number 
	 * 	
	 */
	public URL getURL(int pagenum) throws MalformedURLException
	{
		return new URL("https://github.com/search?q=" +
				props.getProperty("searchkeywordquery") + 
				"%20language%3A" +
				props.getProperty("searchlanguage") +
				"&repo=&langOverride=&start_value=" +
				pagenum +
				"&type=Code&language=");
	}
	
	/**
	 * Processes an entire page of search results, extracting URL's of each individual project
	 * @param url
	 * @throws IOException
	 */
	public void processSearchPage(URL url) throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String inputLine, username, projectName;
		URL projectLink;
		CharSequence fingerprint= "<h2 class=\"title\">";	// Marks each entry in the search results page
		
		while ((inputLine = in.readLine()) != null) {
			if(inputLine.contains(fingerprint))				// Found a search result
			{
				inputLine= in.readLine();
				
				String words[] =inputLine.split("/",4);		// separate the project URL into separate words
				username=words[1];							// Second entry is the username (first is blank, since the URL starts with a '/')
				projectName=words[2];						// Third entry is the project name
				if(!completedProjects.contains(projectName))				// Add to list if we don't have this project yet
				{
					completedProjects.add(projectName);
					projectLink= new URL("https://github.com/"+username+"/"+projectName);	// Construct the full URL to the project
					try {
						processProjectPage(projectLink, username, projectName);				
					} catch (MalformedURLException e) {
						System.out.println("MalformedURLException"+projectLink);
					} catch (IOException e) {
						System.out.println("***Couldn't find " + projectLink);
					}
				}
				
				
			}
		}
		in.close();
		
	}

	
	/**
	 * processProjectPage - Processes the github page for a given project
	 * @param url 			the full URL to the project's page
	 * @param username 		the username of the project's creator
	 * @param projectName	the project's title
	 * @throws IOException
	 */
	private void processProjectPage(URL url, String username, String projectName) throws IOException {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String inputLine, tmp[];

		CharSequence zipFlag="</span>ZIP</span>";
		while ((inputLine = in.readLine()) != null) {
			if(inputLine.contains(zipFlag))			// This part downloads the project's .zip source if it has one
			{
				tmp =inputLine.split("\"",3);
				URL zipFile= new URL("https://github.com"+tmp[1]);
				
				String downloadroot = props.getProperty("downloadroot");
				Runnable worker = new DownloadUnzipWorker(zipFile, username, projectName, downloadroot);
				executor.execute(worker);
				
				break;
			}
			
		}
		in.close();
	}
	
	
	
} //class GithubDownloadManager
