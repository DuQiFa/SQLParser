package sqlparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Class that merges intermediate sqltable results, to unfold orthologs for last common ancestor (LCA) of a given pair of species
 * Files that belong to everything below LCA are merged, and unfolded. This is repeated for every pair of organisms.
 * @author Mateusz Kaduk <mateusz.kaduk@gmail.com>
 *
 */
public class FileMerger {
	private File output;

	/**
	 * Constructor
	 * @param output
	 * @throws IOException
	 */
	public FileMerger(File output) throws IOException {
		this.output = output;
	}

	/**
	 * List of sqltable files to be merged
	 * @param fileList
	 * @throws IOException
	 */
	public void mergeFiles(List<File> fileList) throws IOException {
		//System.out.println("Merging :" + fileList);
		BufferedWriter bw = new BufferedWriter(new FileWriter(output, true));
		for (File file : fileList) {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			while((line = in.readLine()) != null) {
				bw.write(line);
				bw.newLine();
			}
			in.close();
		}
		bw.flush();
		bw.close();
	}
}
