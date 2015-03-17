package sqlparser;

import java.io.File;
import java.util.LinkedHashMap;

import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;

/**
 * Fasta files reader
 * @author Mateusz Kaduk <mateusz.kaduk@gmail.com>
 */
public class FastaReader {
	private LinkedHashMap<String, ProteinSequence> fastaMap;
	private File input;

	/**
	 * Constructor loading up fasta file
	 * @param input
	 * @throws Exception
	 */
	public FastaReader(File input) throws Exception {
		fastaMap = FastaReaderHelper.readFastaProteinSequence(input);
		this.input = input;
	}

	/**
	 * Method returns a map of protein sequences
	 * @return
	 */
	public LinkedHashMap<String, ProteinSequence> getSequences() {
		return fastaMap;
	}

	/**
	 * Method returns the number of sequences in the fasta file
	 * @return
	 */
	public int getSequenceNumber() {
		return fastaMap.size();
	}

	/**
	 * Method returns the reference to the loaded file
	 * @return
	 */
	public File getInputFile() {
		return input;
	}
}
