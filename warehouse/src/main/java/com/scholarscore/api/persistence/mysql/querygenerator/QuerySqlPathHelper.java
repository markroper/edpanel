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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: jordan
 * Date: 2/24/16
 * Time: 12:37 PM
 */
public class QuerySqlPathHelper {

    final static Logger LOGGER = LoggerFactory.getLogger(QuerySqlPathHelper.class);
    
    // -- TODO ideas:
    // -- relationship between two tables: tell the FK direction (and thus column names?) (this could simplify SQL serializers)
    // 
    
    // This layer of abstraction is required as long as we have "user-visible dimensions" 
    // (of which *more* than one can be mapped to a specific table) that are different 
    // from our actual tables (e.g. teacher and admin are dimensions, but don't have corresponding tables)
    private static final Map<Dimension, Dimension> PSEUDO_DIMENSION_CONVERSION_TABLE =
            new HashMap<Dimension, Dimension>() {
                {
                    put(Dimension.TEACHER, Dimension.STAFF);
                    put(Dimension.ADMINISTRATOR, Dimension.STAFF);
                }
            };

    public static void calculateAndAddAdditionalNeededDimensions(Query q) {
        if (queryHasCompletePath(q)) { return; /*new HashSet<>();*/ }
        LOGGER.debug("Determined that additional dimensions are needed...");
        
        //Get the dimensions in the correct order for joining:
        HashSet<Dimension> selectedDims = new HashSet<>();
        if(null != q.getFields()) {
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
        if(null != filterDims) {
            selectedDims.addAll(filterDims);
        }
        List<Dimension> orderedTables = Dimension.resolveOrderedDimensions(selectedDims);

        Set<Dimension> unmatchedDimensions = returnUnmatchedTables(buildExtendedTablesFromQuery(q));
        if (unmatchedDimensions == null || unmatchedDimensions.size() <= 0) {
            LOGGER.warn("Bug likely -- returnUnmatchedTables should not return empty/null when queryHasCompletePath returns false");
            /*return new HashSet<>();*/
        }
        
        if (orderedTables.size() > 0) {
            Dimension firstTable = orderedTables.get(0);
            LOGGER.debug("OK, now we have table " + firstTable + " and we're trying to find links to...");
            for (Dimension dim : unmatchedDimensions) {
                LOGGER.debug("... Unmatched dimension " + dim);
                // okay, now we matched at least one, but there may be more
                Set<Dimension> neededDimensions = breadthFirstSearch(firstTable, unmatchedDimensions);

                // not sure about this, but seems reasonable as these tables are already included
                neededDimensions.remove(firstTable);
                for (Dimension unmatchedDimension : unmatchedDimensions) {
                    neededDimensions.remove(unmatchedDimension);
                }

                // TODO Jordan: Below here begins modifying the query which may not be expected. hrm. change before final version?

                // add any remaining found tables as hints
                for (Dimension neededDimension: neededDimensions) {
                    q.addJoinTable(neededDimension);
                }
                
                Set<Dimension> unmatchedDimensionsAfterAdding = returnUnmatchedTables(buildExtendedTablesFromQuery(q));
                if (unmatchedDimensionsAfterAdding == null || unmatchedDimensionsAfterAdding.size() == 0) {
                    // we're done!
                    return;
                } else {
                    // after we added our new hint tables, see if there are fewer unmatched dimensions. if not, give up.
                    if (unmatchedDimensionsAfterAdding.size() >= unmatchedDimensions.size()) {
                        throw new RuntimeException("Unable to find join path through tables!");
                    } else {
                        // okay, we're making progress. keep going.
                        calculateAndAddAdditionalNeededDimensions(q);
                    }
                }
            }
        } else {
            LOGGER.warn("Unexpected situation: attempting to calculateAndAddAdditionalNeededDimensions but no tables.");
        }
    }
    
    public static Boolean queryHasCompletePath(Query q) {
        List<Dimension> orderedTables = buildExtendedTablesFromQuery(q);
        return hasCompleteJoinPath(orderedTables);
    }
    
    // this list of ordered tables includes aggregate measures and a pseudo-dimension conversion step
    private static List<Dimension> buildExtendedTablesFromQuery(Query q) {
        //Get the dimensions in the correct order for joining:
        HashSet<Dimension> selectedDims = new HashSet<>();
        if(null != q.getFields()) {
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
        if(null != filterDims) {
            selectedDims.addAll(filterDims);
        }
        List<Dimension> orderedTables = Dimension.resolveOrderedDimensions(selectedDims);

        if (q.getAggregateMeasures() != null && q.getAggregateMeasures().size() > 0) {
            AggregateMeasure aggregateMeasure = q.getAggregateMeasures().get(0);
            MeasureSqlSerializer serializer = MeasureSqlSerializerFactory.get(aggregateMeasure.getMeasure());
            Dimension table = DbMappings.TABLE_NAME_TO_DIMENSION.get(serializer.toTableName());
            Dimension optionalTable = DbMappings.TABLE_NAME_TO_DIMENSION.get(serializer.optionalJoinedTable());

            orderedTables.add(table);
            if (optionalTable != null) {
                orderedTables.add(optionalTable);
            }
        }

        // replace any pseudo dimensions with their corresponding actual dimensions
        for (int i = 0 ; i < orderedTables.size() ; i++) {
            Dimension currentDimension = orderedTables.get(i);
            Dimension actualDimension = PSEUDO_DIMENSION_CONVERSION_TABLE.get(currentDimension);
            if (actualDimension != null) {
                // oops, we're using a pseudo dimension and need to convert it
                orderedTables.remove(i);
                orderedTables.add(i, actualDimension);
            }
        }
        return orderedTables;
    }

    private static class Node {
        Edge[] edges;
        Dimension dimension;

        @Override
        public String toString() {
            return "Node (d:" + dimension +") (edges: [" + edges.length + "])";
        }
    }

    private static class Edge {
        Node pointedFrom;
        Node pointedAt;
    }

    // should probably take all dimensions and build the full graph once?
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
            IDimension dimensionClass = Dimension.buildDimension(dimension);
            Set<Dimension> parentDimensions = dimensionClass.getParentDimensions();
            if (parentDimensions != null && parentDimensions.size() > 0) {
                Edge[] edges = new Edge[parentDimensions.size()];
                int arrayPos = 0;
                for (Dimension parentDimension : parentDimensions) {
                    Edge edge = new Edge();
                    edge.pointedAt = nodesSoFar.get(parentDimension);
                    edge.pointedFrom = nodesSoFar.get(dimension);
                    edges[arrayPos++] = edge;
                }
                node.edges = edges;
            } else {
                node.edges = new Edge[0];
            }
        }
        return nodesSoFar;
    }

    // this holds all Nodes, which wrap around dimensions and manage the graph connections 
    // this graph contains ALL connected tables that exist, not just the ones relevant to this query
    private static final HashMap<Dimension, Node> allDimensionsGraph = buildGraph(Arrays.asList(Dimension.values()));
    
    // this is a reverse mapping that allows us to quickly figure out what nodes are pointing AT a given node
    private static HashMap<Node, Set<Node>> reverseNeighborMapping = buildReverseNodeMapping();

    // right now this only checks the neighbors a node points to and all nodes pointing at a node
    private static Set<Node> findImmediateNeighbors(Node dimensionNode) {
        if (dimensionNode == null) { return new HashSet<>(); }
        Set<Node> allNodes = new HashSet<>();
        // all nodes this node points at
        for (Edge edge : dimensionNode.edges) {
            allNodes.add(edge.pointedAt);
        }
        // all nodes that are pointing at this node
        allNodes.addAll(reverseNeighborMapping.get(dimensionNode));
        return allNodes;
    }

    // doesn't actually tell us a path, but rather if a given list of dimensions can be joined together w/o additional tables
    private static boolean hasCompleteJoinPath(List<Dimension> orderedTables) {
        Set<Dimension> unmatchedTables = returnUnmatchedTables(orderedTables);
        return unmatchedTables == null;
    }
    
    private static Set<Dimension> returnUnmatchedTables(List<Dimension> orderedTables) {
        if (orderedTables.size() <= 1) { return null; }

        Set<Node> unmatchedTables = new HashSet<>();
        for (Dimension dimension : orderedTables) {
            unmatchedTables.add(allDimensionsGraph.get(dimension));
        }

        // take the first table and scan its neighbors, which will then lead to all connected tables being scanned.
//        HashMap<Node, Integer> tableGraph = new HashMap<>();
        Node firstNode = unmatchedTables.iterator().next();
        Set<Node> neighborNodes = findImmediateNeighbors(firstNode);
        unmatchedTables.remove(firstNode);

        // yeah, okay, a table hasn't been connected to the connected-table-graph yet...
        // we may need to loop through this a number of times depending on the order of the tables.
        // as long as any new table is matched, loop again.
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
        Set<Dimension> unmatchedDimensions = new HashSet<>();
        if (unmatchedTables.size() > 0) {
            StringBuilder unmatchedTableWarning = new StringBuilder();
            unmatchedTableWarning.append("Unmatched Table found! ");
            unmatchedTableWarning.append("ALL TABLES: ( ");
            for (Dimension table : orderedTables) {
                if (table != null) {
                    unmatchedTableWarning.append(table.name() + " ");
                }
            }
            unmatchedTableWarning.append(") started from " + firstNode.dimension + ", ");
            for (Node unmatchedTable : unmatchedTables) {
                unmatchedTableWarning.append("(UNMATCHED TABLE: " + unmatchedTable.dimension + ") ");
                unmatchedDimensions.add(unmatchedTable.dimension);
            }
            LOGGER.warn(unmatchedTableWarning.toString());
            if (unmatchedDimensions.size() == 0) { return null; }
            return unmatchedDimensions;
        }
        return null;
    }

    private static Set<Node> getAllNodesPointingAt(Node root) {
        HashSet<Node> allNodesPointingAtRoot = new HashSet<>();
        for (Dimension dimension : allDimensionsGraph.keySet()) {
            Node currentNode = allDimensionsGraph.get(dimension);
            if (currentNode.edges != null) {
                for (Edge edge : currentNode.edges) {
                    if (edge.pointedAt != null && edge.pointedAt.equals(root)) {
                        allNodesPointingAtRoot.add(currentNode);
                    }
                }
            }
        }
        return allNodesPointingAtRoot;
    }
    
    private static HashMap<Node, Set<Node>> buildReverseNodeMapping() { 
        HashMap<Node, Set<Node>> toReturn = new HashMap<Node, Set<Node>>();
        for (Node node : allDimensionsGraph.values()) {
            Set<Node> pointingAtThisNode = getAllNodesPointingAt(node);
            toReturn.put(node, pointingAtThisNode);
        }
        return toReturn;
    }
    
    // find the shortest path from specified rootDimension to ANY of the specified target dimensions
    private static Set<Dimension> breadthFirstSearch(Dimension rootDimension, Set<Dimension> targetDimensions) {

        
        BreadthFirstSearcher bfs = new BreadthFirstSearcher(rootDimension, targetDimensions);
        Set<Dimension> results = bfs.search();
        return results;
        
//        HashMap<Node, Integer> nodeDistances = new HashMap<>();
//        HashMap<Node, Node> visitedNodes = new HashMap<>();
//        Set<Node> willVisitNextHop = new HashSet<>();

        
        
        
//        allSeenNodes.add(rootNode);
//        nextHopUnvisitedNodes.add(rootNode);
        
        /*
        nodeParents.put(rootNode, null);
        nodeDistances.put(rootNode, 0);
//        visitedNodes.add(rootNode);
                
        Node currentNode = rootNode;
        int currentDistance = 0;
        
        // loop through all nodes at this distance 
        // loop through all neighbors pointed to by this node
        for (Edge edge : rootNode.edges) {
            Node neighbor = edge.pointedAt;
            if (targetDimensions.contains(neighbor.dimension)) {
                // yes! we found a connection.
                // return this chain of parents, it's the dimensions to return.
                if (nodeDistances.get(currentNode) > 0) {
                    Node parentNode = nodeParents.get(currentNode);
                }
            } else {
                
            }
//            if (!visitedNodes.contains(neighbor)) {
//                willVisitNextHop.add(neighbor);
//            }
        }   
        */
        
        // also (if setting enabled) loop through all neighbors one direction away, POINTING at this node
//        if (!bidirectionalSearch) { }
    }

    private static class BreadthFirstSearcher {

        // This HashMap is insert-only and is used to store the shortest path between the root node and other nodes
        // For key <NodeKey>, <NodeValue> will be returned where NodeValue is one step closer to the Root Node (the parent node)
        // if <NodeValue> is null, <NodeKey> refers to the root node
        private HashMap<Node, Node> nodeParents = new HashMap<>();

        private Set<Node> allSeenNodes = new HashSet<>();
        
        private boolean bidirectionalSearch = true;
        
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
                    if (nodeBeingVisited.edges != null && nodeBeingVisited.edges.length > 0) {
                        for (Edge edge : nodeBeingVisited.edges) {
                            // cache this neighbor (IF it hasn't been seen before) so that we can visit it next round
                            addNodeIfUnseen(edge.pointedAt, nodeBeingVisited);
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
