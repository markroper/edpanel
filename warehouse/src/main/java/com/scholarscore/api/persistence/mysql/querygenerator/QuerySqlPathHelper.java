package com.scholarscore.api.persistence.mysql.querygenerator;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializerFactory;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.dimension.IDimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * User: jordan
 * Date: 2/24/16
 * Time: 12:37 PM
 */
public class QuerySqlPathHelper {

    final static Logger LOGGER = LoggerFactory.getLogger(QuerySqlPathHelper.class);
    
    // this holds all Nodes, which wrap around dimensions and manage the graph connections 
    // this graph contains ALL connected tables that exist, not just the ones relevant to this query
    private static final HashMap<Dimension, Node> allDimensionsGraph = buildGraph(Arrays.asList(Dimension.values()));

    // this is a reverse mapping that allows us to quickly figure out what nodes are pointing AT a given node
    private static HashMap<Node, Set<Node>> reverseNeighborMapping = buildReverseNodeMapping();
    
    public static void calculateAndAddAdditionalNeededDimensions(Query q) throws SqlGenerationException {
        if (queryHasCompletePath(q)) { return; }
        LOGGER.debug("Determined that additional dimensions are needed...");

        List<Dimension> orderedTables = buildTablesFromQuery(q);
        List<Dimension> unmatchedDimensions = returnUnmatchedTables(orderedTables);
        LOGGER.debug("UNMATCHED TABLES: " + unmatchedDimensions);
        
        if (orderedTables.size() > 0 && (unmatchedDimensions != null && unmatchedDimensions.size() > 0)) {
            Dimension firstTable = orderedTables.get(0);
            LOGGER.trace("FIRST TABLE: " + firstTable);
            LOGGER.debug("OK, now we have table " + firstTable + " and we're trying to find links to...");
            for (Dimension dim : unmatchedDimensions) {
                LOGGER.debug("... Unmatched dimension " + dim);
    
                Set<Dimension> neededDimensions = breadthFirstSearch(firstTable, unmatchedDimensions);
                // okay, now we matched at least one unmatched dimension using the 'neededDimensions' returned, but there may be more
    
                // these tables will be included anyway, so don't hint them 
                for (Dimension alreadyIncludedDimension : orderedTables) {
                    neededDimensions.remove(alreadyIncludedDimension);
                }

                // add any remaining found tables as hints
                for (Dimension neededDimension: neededDimensions) {
                    q.addJoinTable(neededDimension);
                }
                
                List<Dimension> unmatchedDimensionsAfterAdding = returnUnmatchedTables(buildTablesFromQuery(q));
                if (unmatchedDimensionsAfterAdding == null || unmatchedDimensionsAfterAdding.size() == 0) {
                    // we're done!
                    return;
                } else {
                    // after we added our new hint tables, see if there are fewer unmatched dimensions. if not, give up.
                    if (unmatchedDimensionsAfterAdding.size() >= unmatchedDimensions.size()) {
                        LOGGER.warn("UNABLE TO FIND JOIN PATH! Unmatched Dimensions after adding: " + unmatchedDimensionsAfterAdding
                        + "\n" + "Unmatched Before: " + unmatchedDimensions);
                        throw new SqlGenerationException("Unable to find join path through tables!");
                    } else {
                        // okay, we're making progress. keep going.
                        calculateAndAddAdditionalNeededDimensions(q);
                    }
                }
            }
        } else {
            String exceptionMsg;
            if (unmatchedDimensions == null || unmatchedDimensions.size() <= 0) {
                exceptionMsg = "Bug likely -- returnUnmatchedTables should not return empty/null when queryHasCompletePath returns false";
            } else {
                exceptionMsg = "Unexpected situation: attempting to calculateAndAddAdditionalNeededDimensions with no tables.";
            }
            throw new SqlGenerationException(exceptionMsg);
        }
    }
    
    public static Boolean queryHasCompletePath(Query q) {
        List<Dimension> orderedTables = buildTablesFromQuery(q);
        return hasCompleteJoinPath(orderedTables);
    }

    // return an ordered list of all tables involved in the ultimate SQL query
    private static List<Dimension> buildDimensionsFromQuery(Query q) {
        //Get the dimensions in the correct order for joining:
        HashSet<Dimension> selectedDims = new HashSet<>();
        if (null != q.getFields()) {
            for (DimensionField f : q.getFields()) {
                selectedDims.add(f.getDimension());
            }
        }
        // if any hints are included, use them 
        if (null != q.getJoinTables()) {
            for (Dimension d : q.getJoinTables()) {
                selectedDims.add(d);
            }
        }
        //Add any dimensions to join that may be referenced only in the WHERE clause
        Set<Dimension> filterDims = q.resolveFilterDimensions();
        if (null != filterDims) {
            selectedDims.addAll(filterDims);
        }

        if (q.getAggregateMeasures() != null && q.getAggregateMeasures().size() > 0) {
            AggregateMeasure aggregateMeasure = q.getAggregateMeasures().get(0);
            MeasureSqlSerializer serializer = MeasureSqlSerializerFactory.get(aggregateMeasure.getMeasure());
            Dimension table = serializer.toTableDimension();
            Dimension optionalTable = serializer.toSecondTableDimension();
            if (!selectedDims.contains(table)) {
                selectedDims.add(table);
            }
            if (optionalTable != null && !selectedDims.contains(optionalTable)) {
                selectedDims.add(optionalTable);
            }
        }

        return Dimension.resolveOrderedDimensions(selectedDims);
    }
    
    // invoke buildDimensionsFromQuery() with an a pseudo-dimension conversion step
    private static List<Dimension> buildTablesFromQuery(Query q) {

        List<Dimension> orderedTables = buildDimensionsFromQuery(q);
        
        // replace any pseudo dimensions with their corresponding actual dimensions
        // (a bit hacky -- we can achieve this by converting from dimensions to tablenames and then back to dimensions again)
        for (int i = 0 ; i < orderedTables.size() ; i++) {
            Dimension originalDimension = orderedTables.get(i);
            Dimension resolvedDimension = DbMappings.TABLE_NAME_TO_DIMENSION.get(DbMappings.DIMENSION_TO_TABLE_NAME.get(originalDimension));
            if (resolvedDimension != originalDimension) {
                // oops, we're using a pseudo dimension and need to convert it
                orderedTables.remove(i);
                orderedTables.add(i, resolvedDimension);
            }
        }
        return orderedTables;
    }

    private static class Node {
        Dimension dimension;
        
        // this node has connections to the following nodes
        Node[] neighbors;
        
        @Override
        public String toString() {
            return "Node (d:" + dimension +") (neighbor count: [" + neighbors.length + "])";
        }
    }

    private static HashMap<Dimension, Node> buildGraph(List<Dimension> dimensions) {
        HashMap<Dimension, Node> nodesSoFar = new HashMap<>();
        // first just build the nodes
        for (Dimension dimension : dimensions) {
            Node node = new Node();
            node.dimension = dimension;
            nodesSoFar.put(dimension, node);
        }
        // then wire up the edges
        for (Dimension dimension : dimensions) {
            Node node = nodesSoFar.get(dimension);
            IDimension dimensionClass = dimension.buildDimension();
            Set<Dimension> parentDimensions = dimensionClass.getParentDimensions();
            if (parentDimensions != null && parentDimensions.size() > 0) {
                Node[] neighbors = new Node[parentDimensions.size()];
                int arrayPos = 0;
                for (Dimension parentDimension : parentDimensions) {
                    neighbors[arrayPos++] = nodesSoFar.get(parentDimension);
                }
                node.neighbors = neighbors;
            } else {
                node.neighbors = new Node[0];
            }
        }
        return nodesSoFar;
    }

    // return all direct (1-hop) neighbor nodes to a provided node. Includes ALL neighbors - pointed at, and pointed from
    private static Set<Node> findImmediateNeighbors(Node dimensionNode) {
        return findImmediateNeighbors(dimensionNode, true);
    }

    // return all direct (1-hop) neighbor nodes to a provided node
    // pass in bidirectional=false to receive only the nodes pointed AT from this node
    private static Set<Node> findImmediateNeighbors(Node dimensionNode, boolean bidirectional) {
        if (dimensionNode == null) { return new HashSet<>(); }
        Set<Node> allNodes = new HashSet<>(Arrays.asList(dimensionNode.neighbors));
        if (bidirectional) {
            // all nodes that are pointing at this node
            allNodes.addAll(reverseNeighborMapping.get(dimensionNode));
        }
        return allNodes;
    }

    // doesn't actually tell us a path, but rather if a given list of dimensions can be joined together w/o additional tables
    private static boolean hasCompleteJoinPath(List<Dimension> orderedTables) {
        Collection<Dimension> unmatchedTables = returnUnmatchedTables(orderedTables);
        return unmatchedTables == null;
    }
    
    // given a set of tables, return any tables that do not form a connected graph (starting from the first table)
    private static List<Dimension> returnUnmatchedTables(List<Dimension> orderedTables) {
        if (orderedTables.size() <= 1) { return null; }

        List<Node> unmatchedTables = new ArrayList<>();
        for (Dimension dimension : orderedTables) {
            unmatchedTables.add(allDimensionsGraph.get(dimension));
        }

        // take the first table and scan its neighbors, which will then lead to all connected tables being scanned.
        Node firstNode = unmatchedTables.iterator().next();
        Set<Node> neighborNodes = findImmediateNeighbors(firstNode);
        unmatchedTables.remove(firstNode);

        // at the start, only the initial table (the first one in the provided list) is considered 'connected'.
        // go through the list of UNconnected tables and see if any of them connect to any of the 
        boolean tableMatchedThisRound = true;
        while (tableMatchedThisRound) {
            tableMatchedThisRound = false;
            // check all unmatched tables against these neighbor nodes. 
            for (Iterator<Node> unmatchedTableIterator = unmatchedTables.iterator(); unmatchedTableIterator.hasNext(); ) {
                Node unmatchedTable = unmatchedTableIterator.next();
                // one of the joined tables has this node as a neighbor - consider it linked
                if (neighborNodes.contains(unmatchedTable)) {
                    // remove the node from the list of unmatched tables
                    unmatchedTableIterator.remove();
                    // add the new table's immediate neighbor nodes to the list of one-hop neighbors
                    neighborNodes.addAll(findImmediateNeighbors(unmatchedTable));
                    // since we've added new neighbors, loop again in case any previous tables missed a connection through this new table
                    tableMatchedThisRound = true;
                }
            }
        }

        // okay, now all the tables are matched or they *will never* be matched.
        // if there's any unmatched tables, the join path is incomplete.
        List<Dimension> unmatchedDimensions = new ArrayList<>();
        if (unmatchedTables.size() > 0) {
            for (Node unmatchedTable : unmatchedTables) {
                unmatchedDimensions.add(unmatchedTable.dimension);
            }
            if (unmatchedDimensions.size() == 0) { return null; }
            return unmatchedDimensions;
        }
        return null;
    }

    private static Set<Node> getAllNodesPointingAt(Node root) {
        HashSet<Node> allNodesPointingAtRoot = new HashSet<>();
        for (Dimension dimension : allDimensionsGraph.keySet()) {
            Node currentNode = allDimensionsGraph.get(dimension);
            if (currentNode.neighbors != null) { 
                for (Node neighbor : currentNode.neighbors) { 
                    if (neighbor != null && neighbor.equals(root)) { 
                        allNodesPointingAtRoot.add(currentNode);
                    }
                }
            }
        }
        return allNodesPointingAtRoot;
    }
    
    private static HashMap<Node, Set<Node>> buildReverseNodeMapping() { 
        HashMap<Node, Set<Node>> toReturn = new HashMap<>();
        for (Node node : allDimensionsGraph.values()) {
            Set<Node> pointingAtThisNode = getAllNodesPointingAt(node);
            toReturn.put(node, pointingAtThisNode);
        }
        return toReturn;
    }
    
    // find the shortest path from specified rootDimension to ANY of the specified target dimensions
    private static Set<Dimension> breadthFirstSearch(Dimension rootDimension, List<Dimension> targetDimensions) {
        // it's okay to 'lose' the order of the list here -- the order of target dimensions is meaningless at this point
        BreadthFirstSearcher bfs = new BreadthFirstSearcher(rootDimension, new HashSet<>(targetDimensions));
        return bfs.search();
    }

    private static class BreadthFirstSearcher {

        // This HashMap is insert-only and is used to store the shortest path between the root node and other nodes
        // For key <NodeKey>, <NodeValue> will be returned where NodeValue is one step closer to the Root Node (the parent node)
        // if <NodeValue> is null, <NodeKey> refers to the root node
        private HashMap<Node, Node> nodeParents = new HashMap<>();

        private Set<Node> allSeenNodes = new HashSet<>();
        
        private boolean bidirectionalSearch = false; //true;
        
        Node rootNode;
        Set<Dimension> targetDimensions;
        
        public BreadthFirstSearcher(Dimension rootDimension, Set<Dimension> targetDimensions) {
            rootNode = allDimensionsGraph.get(rootDimension);
            this.targetDimensions = targetDimensions;
        }

        private Set<Node> currentRoundUnvisitedNodes;
        private Set<Node> nextHopUnvisitedNodes = new HashSet<>();
        
        public Set<Dimension> search() {
            
            addNodeIfUnseen(rootNode, null);
            int step = 1;
            currentRoundUnvisitedNodes = nextHopUnvisitedNodes;
            
            while (currentRoundUnvisitedNodes.size() > 0) { // as long as the current round has any candidate node to search, search them!
                nextHopUnvisitedNodes = new HashSet<>();
                // visit all the notes in this step
                LOGGER.debug("Now " + step++ + " steps away from root node.");
                for (Node nodeBeingVisited : currentRoundUnvisitedNodes) {
                    LOGGER.trace("Visiting formerly-unvisited node " + nodeBeingVisited.dimension);
                    // "visit" the node by checking all its pointed-to neighbors
                    // (AND the nodes that point at it, if bidirectional is enabled)

                    // This is either the target dimension, or one of many
                    if (targetDimensions.contains(nodeBeingVisited.dimension)) {
                        LOGGER.debug("Found targetDimension " + nodeBeingVisited.dimension);
                        // we're "done" - we only need one match
                        
                        // (this path will be from the last table to the first... reverse?) 
                        Set<Dimension> neededTables = new HashSet<>();
                        
                        // this is the target
                        neededTables.add(nodeBeingVisited.dimension);
                        Node parentNode = nodeParents.get(nodeBeingVisited);
                        while (parentNode != null) {
                            neededTables.add(parentNode.dimension);
                            parentNode = nodeParents.get(parentNode);
                        }

                        return neededTables;
                    }

                    // any pointed-to neighbors? add them to next round's 'visit-list' if unvisited
                    if (nodeBeingVisited.neighbors != null && nodeBeingVisited.neighbors.length > 0) {
                        for (Node neighbor : nodeBeingVisited.neighbors) {
                            // cache this neighbor (IF it hasn't been seen before) so that we can visit it next round
                            addNodeIfUnseen(neighbor, nodeBeingVisited);
                        }
                    }

                    // any pointed-from neighbors? add them to next round's 'visit-list' if unvisited
                    if (bidirectionalSearch) {
                        Set<Node> pointingFrom = getAllNodesPointingAt(nodeBeingVisited);
                        if (pointingFrom != null && pointingFrom.size() > 0) {
                            for (Node UnvisitedNeighborNode : pointingFrom) {
                                addNodeIfUnseen(UnvisitedNeighborNode, nodeBeingVisited);
                            }
                        }
                    }
                }
                // done this round. now consider the NEXT HOP neighbors (if there are any)
                currentRoundUnvisitedNodes = nextHopUnvisitedNodes;
            }

            // no path found!
            return null;
        }

        private void addNodeIfUnseen(Node nodeToAdd, Node parentNode) {
            if (!allSeenNodes.contains(nodeToAdd)) {
                LOGGER.trace("Found node " + nodeToAdd + " that hasn't been seen before -- adding to unvisited node list");
                allSeenNodes.add(nodeToAdd);
                nextHopUnvisitedNodes.add(nodeToAdd);
                // record a breadcrumb path so (if this path turns out to be fruitful, ) we can reconstruct the path we took
                nodeParents.put(nodeToAdd, parentNode);
            }
        }

    }
    
}
