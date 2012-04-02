package wozniewicz.analyzer.downloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GithubDownloadManager {
	private Properties props;	
	private int page;
	private int maxpages;
	private int nthreads;
	
	private ArrayList<String> data= new ArrayList<String>();
	private static ExecutorService executor;

	
	/**
	 * Default constructor.  Provide the properties file to it
	 * @param props
	 */
	public GithubDownloadManager(Properties props) {
		this.props = props;
		this.page = 0;
		
		this.maxpages = getPageRange();
		
		nthreads = Integer.parseInt(props.getProperty("threads"));   
	}
	
	
	/**
	 * Wrapper to download a given amount of result pages at a time
	 * Automatically increments internal counter
	 * @param n
	 * @return false if no more pages to download, true otherwise
	 */
	public boolean downloadNextPages(int n) 
	{
		boolean retval = true;	// Keeps track of whether there are more pages to download
		executor = Executors.newFixedThreadPool(nthreads);
		while (n>0) {
			page++;
			if (!downloadCurrentPage()) {
				retval = false;
			}
			n--;
		}
		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated()) {}
		return retval;
	}
	
	
	private boolean downloadCurrentPage()
	{
		if (page < maxpages) {
			try {
				System.out.println(
						"++++++++++++++++++++++++++++++++++++++++++++++++++++++++SEARCH PAGE:"
						+ page);
				System.out.println(getURL(page));
				processSearchPage(getURL(page));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Page not found Error, Automatically Skip this page");
			}
		}
		else {
			System.out.println("Done downloading all pages");
			return false;
		}
		return true;
		
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
				if(!data.contains(projectName))				// Add to list if we don't have this project yet
				{
					data.add(projectName);
					projectLink= new URL("https://github.com/"+username+"/"+projectName);	// Construct the full URL to the project
					try {
						processProjectPage(projectLink, username, projectName);				
					} catch (MalformedURLException e) {
						System.out.println("MalformedURLException"+projectLink);
					} catch (IOException e) {
						System.out.println("***Error! Project page is not found on the Github. Automatically Skip this project:"+username+"/"+projectName);
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
		URL projectLink,zipFile;
		CharSequence forkFlag= "<span class=\"fork-flag\">";
		CharSequence zipFlag="</span>ZIP</span>";
		while ((inputLine = in.readLine()) != null) {
			
			if(inputLine.contains(forkFlag))		// If there is a fork, process that too
			{
				inputLine= in.readLine();
				tmp=inputLine.split("\"")[3].split("/");
				
				username= tmp[1];
				projectName= tmp[2];
				projectLink= new URL("https://github.com/"+username+"/"+projectName);
				processProjectPage(projectLink, username, projectName);
				break;
			}
			if(inputLine.contains(zipFlag))			// This part downloads the project's .zip source if it has one
			{
				tmp =inputLine.split("\"",3);
				zipFile= new URL("https://github.com"+tmp[1]);
				
				//System.out.println(zipFile);
				
				String downloadroot = props.getProperty("downloadroot");
				Runnable worker = new DownloadUnzip(zipFile, username, projectName, downloadroot);
				executor.execute(worker);
				
				break;
			}
			
		}
		in.close();
	}
	
	
	
} //class GithubDownloadManager
