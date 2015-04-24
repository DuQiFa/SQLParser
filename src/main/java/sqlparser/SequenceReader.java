package sqlparser;

import java.io.File;
import java.util.HashMap;

import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;

public class SequenceReader {
	private File fasta;

	/**
	 * Sequence mapping reader
	 * 
	 * @param fasta
	 */
	public SequenceReader(File fasta) {
		this.fasta = fasta;
	}

	/**
	 * Get map of proteins to files
	 * 
	 * @return
	 */
	public HashMap<String, String> getMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			HashMap<String, ProteinSequence> pmap = FastaReaderHelper
					.readFastaProteinSequence(fasta);
			for (String key : pmap.keySet()) {
				
				// Strip file extension
				String fname = fasta.getName();
				String name = fname.replaceFirst("[.][^.]+$", "");
				
				// Put into map
				map.put(key,name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
