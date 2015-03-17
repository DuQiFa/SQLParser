package sqlparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;

/**
 * Object for parsing Newick guide tree from Hieranoid
 * @author Mateusz Kaduk <mateusz.kaduk@gmail.com>
 *
 */
public class TreeReader {
	private static final Logger logger = LogManager.getLogger(TreeReader.class);
	private Phylogeny singleTree;
	private List<PhylogenyNode> externalNodes;
	
	public TreeReader(File treeFile) throws FileNotFoundException, IOException {
		PhylogenyParser treeParser = ParserUtils
				.createParserDependingOnFileType(treeFile, false);
		Phylogeny[] phylo = PhylogenyMethods.readPhylogenies(treeParser,
				treeFile);

		// Use first tree only
		singleTree = phylo[0];
		logger.debug("Loaded guide tree with "
				+ singleTree.getExternalNodes().size() + " nodes.");

		// Get all external node with respect to root
		externalNodes = singleTree.getExternalNodes();
	}

	/**
	 * Method returns all external nodes in the tree
	 * @return
	 */
	public List<PhylogenyNode> getAllLeafs(){
		return externalNodes;
	}
	
	/**
	 * Get path to root from a node
	 * 
	 * @param node
	 * @return
	 */
	private List<PhylogenyNode> getRootPath(PhylogenyNode node) {
		ArrayList<PhylogenyNode> path = new ArrayList<PhylogenyNode>();
		PhylogenyNode root = singleTree.getRoot();

		// If not a branch, get a branch
		if (node.isExternal()) {
			node = node.getParent();
		}

		// Recurrence
		if (node != root)
			path.addAll(getRootPath(node.getParent()));
		path.add(node);

		return path;
	}

	/**
	 * Method to extract all branches with one external node, one internal and with two internal
	 * @param node
	 * @return
	 */
	private List<PhylogenyNode> getAllLeafs(PhylogenyNode node) {
		ArrayList<PhylogenyNode> list = new ArrayList<PhylogenyNode>();
		PhylogenyNode cn1 = node.getChildNode1();
		PhylogenyNode cn2 = node.getChildNode2();
		if(cn1.isExternal()) {
			list.add(cn1);
		} else {
			list.addAll(getAllLeafs(cn1));
		}
		if(cn2.isExternal()) {
			list.add(cn2);
		} else {
			list.addAll(getAllLeafs(cn2));
		}
		// This is required to include branches for which both nodes are internal
		if(cn2.isInternal() && cn1.isInternal()) {
			list.add(cn2);
			list.add(cn1);
		}
		return list;
	}
	
	/**
	 * Get a list of branch nodes given the node
	 * @param node
	 * @return
	 */
	public List<PhylogenyNode> getAllBranches(PhylogenyNode node) {
		//List<PhylogenyNode> elist = node.getAllExternalDescendants(); // This does not work, as it excludes double internal branches
		List<PhylogenyNode> elist = getAllLeafs(node);

		HashSet<PhylogenyNode> descHash = new HashSet<PhylogenyNode>();
		for(PhylogenyNode enode : elist) {
			PhylogenyNode parent = enode.getParent();
			descHash.add(parent);
		}
		ArrayList<PhylogenyNode> list = new ArrayList<PhylogenyNode> (descHash);
		return list;
	}
	
	/**
	 * Get last common ancestor for two nodes
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public PhylogenyNode getLastAncestor(PhylogenyNode a, PhylogenyNode b) {
		List<PhylogenyNode> path = getRootPath(a);
		PhylogenyNode ancestor = path.get(0);

		for (PhylogenyNode step : path) {
			// Update last ancestor
			ancestor = step;
			// Not root, so get parent as last ancestor
			if(step != path.get(0))
				ancestor = step.getParent();
			
			// Create a hash with all descendants for search
			HashSet<PhylogenyNode> eset = new HashSet<PhylogenyNode>(
					step.getAllExternalDescendants());
			if (!(eset.contains(a) && eset.contains(b))) {
				break;
			}
		}
		return ancestor;
	}
}
