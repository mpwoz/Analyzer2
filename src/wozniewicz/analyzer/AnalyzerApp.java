package wozniewicz.analyzer;

import java.util.Properties;

import wozniewicz.analyzer.downloader.GithubDownloadManager;
import wozniewicz.analyzer.oldanalyzer.AnalyzerRunner;
import wozniewicz.analyzer.oldanalyzer.Presenter;

public class AnalyzerApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Util util = new Util();
		Properties p = util.loadProps();
		
		GithubDownloadManager dlm = new GithubDownloadManager(p);
		String outDir = p.getProperty("outputroot");
		String outFile = "output-" + System.currentTimeMillis();
		outFile = outDir + outFile + ".html";
		AnalyzerRunner ar = new AnalyzerRunner(p, outFile);
		
		Presenter.startProjectFile(null, outFile);
		
		int count = 0;
		// Download as long as there are more pages
		while (dlm.downloadNextPages(2)) {
			ar.analyzeAll();
			count++;
			if (count > 100) break;
		}
		
		Presenter.endProjectFile(outFile);
	} // main
	
	
	
	
	
	

}
