package wozniewicz.analyzer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import wozniewicz.analyzer.downloader.GithubDownloadManager;

public class AnalyzerApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Util util = new Util();
		Properties p = util.loadProps();
		
		GithubDownloadManager dlm = new GithubDownloadManager(p);
		dlm.downloadNextPages(2);
		
	} // main
	
	
	
	
	
	

}
