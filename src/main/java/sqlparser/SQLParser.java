package sqlparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.forester.phylogeny.PhylogenyNode;

import sbc.orthoxml.io.OrthoXMLWriter;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * A program entry for the SQLtable parser for results from Hieranoid 2
 * 
 * @author Mateusz Kaduk <mateusz.kaduk@gmail.com>
 *
 */
@SuppressWarnings("restriction")
public class SQLParser {
	private static final Logger logger = LogManager.getLogger(SQLParser.class);
	
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
		
		OptionSpec<File> xmlFile = parser
				.acceptsAll(Arrays.asList("x", "orthoxml"),
						"A file to write orthologs in orthoXML format.")
				.withRequiredArg().ofType(File.class);
		
		try {
			// Parse command line
			OptionSet options = parser.parse(args);

			// Open files for sequential writes
			PrintWriter pairWriter = null;
			PrintWriter listWriter = null;
			OrthoXMLWriter orthoXMLWriter = null;
			
			if(options.has(pairsFile))
				pairWriter = openWriter(options.valueOf(pairsFile));
			if(options.has(listFile))
				listWriter = openWriter(options.valueOf(listFile));
			if(options.has(xmlFile)) {
				String timestamp = new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss").format(new Date());
				orthoXMLWriter = new OrthoXMLWriter(options.valueOf(xmlFile), "Hieranoid2", timestamp);
			
			}
			// Check mandatory arguments
			if(options.has(treeFile) && options.hasArgument(sqlDir)
					&& options.has(proDir)) {
				TreeReader treader = new TreeReader(options.valueOf(treeFile));
				List<PhylogenyNode> leafList = treader.getAllLeafs();
				DirectoryReader sreader = new DirectoryReader(
						options.valueOf(sqlDir), "sqltable.");
				
				ProteomesBank pbank = new ProteomesBank(options.valueOf(proDir));
				pbank.loadProteomes();
				
				// For every combination of leafs
				Integer current = 0;
				for (int i = 0; i < leafList.size(); i++) {
					for (int j = i + 1; j < leafList.size(); j++) {
					
						// Count pairs
						current++;
						
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
									+ branch.getName()));
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
						
						// Log organism pair
						logger.info("Parsing for " + pset + " "+current+"/"+(leafList.size()*(leafList.size()-1))/2);
						
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
						
						// Save orthologs to XML
						if (options.has(xmlFile)) {
							XMLWriter xmlwr = new XMLWriter(pairGroups, pbank.getSpecies(), pset);
							
							xmlwr.writeXML(orthoXMLWriter);
						}
					}
				}
				// Close output files
				if(options.has(listFile))
					listWriter.close();
				if(options.has(pairsFile))
					pairWriter.close();
				if(options.has(xmlFile))
					orthoXMLWriter.close();
				
			} else {
				System.out
						.println("SQLTableParser 0.1 - Parser converting sqltable files to orthologs.");
				System.out
						.println("Corresponding author - Mateusz Kaduk <mateusz.kaduk@scilifelab.se>\n");

				// Display help on missing arguments
				parser.printHelpOn(System.out);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (@SuppressWarnings("restriction") XMLStreamException e) {
			e.printStackTrace();
		}
	}
}
