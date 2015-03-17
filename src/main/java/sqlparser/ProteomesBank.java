package sqlparser;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class holding mappings between proteins and organisms names derived from file names
 * @author Mateusz Kaduk <mateusz.kaduk@gmail.com>
 *
 */
public class ProteomesBank {
	private static final Logger logger = LogManager.getLogger(ProteomesBank.class);
	private HashMap<String, String> map;
	private List<File> fastaList;
	/**
	 * Given the directory load protein names from all fasta files
	 * @param dir
	 */
	public ProteomesBank(File dir) {
		DirectoryReader rdir = new DirectoryReader(dir, ".*.fa");
		fastaList = rdir.getFileList();
		map = new HashMap<String, String>();
	}

	/**
	 * Method loads protein names and corresponding organism name (file base name) into memory
	 */
	public void loadProteomes() {
		for(File fFile : fastaList) {
			SequenceReader seqRead = new SequenceReader(fFile);
			HashMap<String, String> tmap = seqRead.getMap();
			logger.info("Loaded " + fFile + " with " + tmap.size() + " sequences.");
			map.putAll(tmap);
		}
	}
	
	/**
	 * Method returns a hashmap of protein names and species
	 * @return
	 */
	public HashMap<String, String> getSpecies() {
		return map;
	}
}
