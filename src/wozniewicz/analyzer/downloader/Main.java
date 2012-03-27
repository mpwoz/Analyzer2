package wozniewicz.analyzer.downloader;
 import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
	
	/*CONFIG*/
	static String keyword="actor";  					// keyword which at least one class of projects has
	static String language="Scala";  					// "Java" for java, "C%23" for C#
	static ExecutorService executor = Executors.newFixedThreadPool(4);  // number of threads for executing downloading and uncompressing projects. 
																		// Go through search result pages is not multithreaded
	
	
	
	/*
	 *  Main
	 */
	static ArrayList<String> data= new ArrayList<String>();
	public static void main(String[] args)
	{
		for (int i=0; i<10; i++)					// Do only the first page (testing)
		//for(int i=1; i<getPageRange(); i++)	// Loop through all the pages of search results
		{
			
			try {
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++SEARCH PAGE:"+ i);
				processSearchPage(getURL(i));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Page not found Error, Automatically Skip this page");
			}
		}
		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated()) {

		}
	}
	
	/**
	 * getPageRange() parses the github search result page-source to get total number of pages
	 * 	@return the last page number 
	 */
	public static int getPageRange()
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
	 * 	@param i The search results page number 
	 * 	
	 */
	public static URL getURL(int i) throws MalformedURLException
	{
		
		return new URL("https://github.com/search?langOverride=&language="+language+"&q="+keyword+"&repo=&start_value="+i+"&type=Code&x=32&y=24");
		
	}
	
	/**
	 * Processes an entire page of search results, extracting URL's of each individual project
	 * @param url
	 * @throws IOException
	 */
	public static void processSearchPage(URL url) throws IOException
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
	private static void processProjectPage(URL url, String username, String projectName) throws IOException {
		
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
				
				System.out.println(zipFile);
				
				Runnable worker = new DownloadUnzip(zipFile, username, projectName);
				executor.execute(worker);
				
				break;
			}
			
		}
		in.close();
	}
	

	


}
