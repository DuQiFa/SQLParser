package sqlparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.forester.phylogeny.PhylogenyNode;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * A program entry for the SQLtable parser for results from Hieranoid 2
 * 
 * @author Mateusz Kaduk <mateusz.kaduk@gmail.com>
 *
 */
public class SQLParser {
	//private static final Logger logger = LogManager.getLogger(SQLParser.class);
	public static PrintWriter openWriter(File outfile) throws FileNotFoundException {
		if(outfile.exists()) {
			outfile.delete();
		}
		return new PrintWriter(new FileOutputStream(outfile),
				true);
	}
	
	public static void main(String[] args) {
		OptionParser parser = new OptionParser();
		OptionSpec<File> sqlDir = parser
				.acceptsAll(Arrays.asList("s", "sql"),
						"A directory with sqltable files named by the branch.")
				.withRequiredArg().ofType(File.class);

		OptionSpec<File> proDir = parser
				.acceptsAll(
						Arrays.asList("p", "prot"),
						"A directory with fasta formated files containing proteomes for each individual organism.")
				.withRequiredArg().ofType(File.class);

		OptionSpec<File> treeFile = parser
				.acceptsAll(Arrays.asList("t", "tree"),
						"A tree to follow when unfoloding different pairs of organisms.")
				.withRequiredArg().ofType(File.class);

		OptionSpec<File> listFile = parser
				.acceptsAll(Arrays.asList("l", "list"),
						"Optional output file for resolved ortholog groups.")
				.withRequiredArg().ofType(File.class);

		OptionSpec<File> pairsFile = parser
				.acceptsAll(Arrays.asList("o", "output"),
						"A file to write list of pairs of orthologs.")
				.withRequiredArg().ofType(File.class);
		try {
			// Parse command line
			OptionSet options = parser.parse(args);

			// Open files for sequential writes
			PrintWriter pairWriter = null;
			PrintWriter listWriter = null;
			if(options.has(pairsFile))
				pairWriter = openWriter(options.valueOf(pairsFile));
			if(options.has(listFile))
				listWriter = openWriter(options.valueOf(listFile));
			
			// Check mandatory arguments
			if(options.has(treeFile) && options.hasArgument(sqlDir)
					&& options.has(proDir)) {
				TreeReader treader = new TreeReader(options.valueOf(treeFile));
				List<PhylogenyNode> leafList = treader.getAllLeafs();
				DirectoryReader sreader = new DirectoryReader(
						options.valueOf(sqlDir), "sqltable.");

				// ProteomesBank pbank = null;
				ProteomesBank pbank = new ProteomesBank(options.valueOf(proDir));
				pbank.loadProteomes();
				
				// For every combination of leafs
				for (int i = 0; i < leafList.size(); i++) {
					for (int j = i + 1; j < leafList.size(); j++) {
					
						// Get pair of organisms
						PhylogenyNode anode = leafList.get(i);
						PhylogenyNode bnode = leafList.get(j);

						// Get last common ancestor (LCA) for a pair
						PhylogenyNode lca = treader.getLastAncestor(anode,
								bnode);

						// Get all nodes prior to LCA
						List<PhylogenyNode> branchList = treader
								.getAllBranches(lca);

						// Combine sqltables to process
						File tmp = File.createTempFile(lca.getName(), ".all");

						ArrayList<File> mergeFiles = new ArrayList<File>();
						for (PhylogenyNode branch : branchList) {
							mergeFiles.add(sreader.getFileMatching("sqltable."
									+ branch.getName() + ".*"));
						}

						// Combine sqltable files for current LCA
						FileMerger fmerger = new FileMerger(tmp);
						fmerger.mergeFiles(mergeFiles);

						// Build ortholog groups
						GroupBuilder gbuilder = new GroupBuilder(tmp);
						ArrayList<ArrayList<String>> pairGroups = gbuilder
								.getOrthologGroups();

						// Clean up temporary file
						tmp.delete();

						// Create a filter for two organisms at the time
						HashSet<String> pset = new HashSet<String>();
						pset.add(anode.getName());
						pset.add(bnode.getName());
						
						// Print groups to file
						if (options.has(listFile)) {
							ListWriter lwriter = new ListWriter(pairGroups, pbank.getSpecies(),pset);
							lwriter.writeLists(listWriter);
						}

						// Print pairs to file
						if (options.has(pairsFile)) {				
							PairWriter pbuilder = new PairWriter(pairGroups,
									pbank.getSpecies(),pset);
					
							pbuilder.writePairs(pairWriter);
						}
					}
				}
				// Close output files
				if(options.has(listFile))
					listWriter.close();
				if(options.has(pairsFile))
					pairWriter.close();
			} else {
				System.out
						.println("SQLTableParser 1.0 - Parser converting concatenaded together sqltable files to list of tab separate pairs of orthologs.");
				System.out
						.println("Corresponding author - Mateusz Kaduk <mateusz.kaduk@scilifelab.se>\n");

				// Display help on missing arguments
				parser.printHelpOn(System.out);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
