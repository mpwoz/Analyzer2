package wozniewicz.analyzer.downloader;
import static org.apache.commons.io.FileUtils.copyURLToFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class DownloadUnzip implements Runnable {

	private final URL zipFile;
	private final String directory;
	private final String fileName = "src.zip";
	final int BUFFER = 2048;

	public DownloadUnzip(URL zipFile, String username, String projectName) {
		this.zipFile = zipFile;
		System.out.println("unzipping "+projectName+"...");
		directory = "projects2/" + projectName + "/";
		File file = new File(directory);
		file.mkdir();

	}

	@Override
	public void run() {
		download();
	}

	public void download() {
		try {

			File file = new File(directory + fileName);
			
			copyURLToFile(zipFile, file);
			uncompress();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out
					.println("***Error! Zip file is not found on the project page. Automatically Skip this zip file:"
							+ zipFile);
		}

	}

	public void uncompress() {
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			ZipFile zipFile = new ZipFile(directory + fileName);

			// Extracts all files to the path specified
			zipFile.extractAll(directory);

		} catch (ZipException e) {
			System.out.println(zipFile);
			e.printStackTrace();
		}

	}
}
