package com.scholarscore.api.persistence.mysql.querygenerator;

import com.scholarscore.api.persistence.DbMappings;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializer;
import com.scholarscore.api.persistence.mysql.querygenerator.serializer.MeasureSqlSerializerFactory;
import com.scholarscore.models.query.AggregateMeasure;
import com.scholarscore.models.query.Dimension;
import com.scholarscore.models.query.DimensionField;
import com.scholarscore.models.query.Query;
import com.scholarscore.models.query.dimension.IDimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * User: jordan
 * Date: 2/24/16
 * Time: 12:37 PM
 */
public class QuerySqlPathHelper {

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

    
    public static Boolean queryHasCompletePath(Query q) {

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
        
        boolean hasCompletePath = hasCompleteJoinPath(orderedTables);
        if (hasCompletePath) {
            System.out.println("Query has COMPLETE join path!");
        } else {
            System.out.println("Query has -incomplete- join path!");
        }
        return hasCompletePath;
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

    private static final HashMap<Dimension, Node> allDimensionsGraph = buildGraph(Arrays.asList(Dimension.values()));

    // right now this only checks the neighbors a node points to and all nodes pointing at a node
    private static Set<Node> findImmediateNeighbors(Node dimensionNode) {
        if (dimensionNode == null) { return new HashSet<>(); }
        Set<Node> allNodes = new HashSet<>();
        // all nodes this node points at
        for (Edge edge : dimensionNode.edges) {
            allNodes.add(edge.pointedAt);
        }
        // all nodes that are pointing at this node
        allNodes.addAll(getAllNodesPointingAt(dimensionNode));
        return allNodes;
    }

    // doesn't actually tell us a path, but rather if a given list of dimensions can be joined together w/o additional tables
    private static boolean hasCompleteJoinPath(List<Dimension> orderedTables) {
        if (orderedTables.size() <= 1) { return true; }

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
//            }
            }
        }

        // okay, now all the tables are matched or they *will never* be matched.
        // if there's any unmatched tables, the join path is incomplete.
        if (unmatchedTables.size() > 0) {
            System.out.println("Unmatched Table! Could not match on table(s)...");
            System.out.print("ALL TABLES: ( ");
            for (Dimension table : orderedTables) {
                if (table != null) {
                    System.out.print(table.name() + " ");
                }
            }
            System.out.print(")");
            System.out.print(" and started from " + firstNode);
            System.out.println();
            for (Node unmatchedTable : unmatchedTables) {
                System.out.println("NO MATCH FOR TABLE " + unmatchedTable);
            }
            return false;
        }
        return true;
        
        /*
        for (int i = 1; i < orderedTables.size(); i++) {
            Dimension currentTable = orderedTables.get(i);
            Node tableNode = allDimensionsGraph.get(currentTable);
            
            // is node already scanned? if so, we don't need to do anything else
            if (tableGraph.keySet().contains(tableNode)) { continue; }
            
            // okay, node isn't scanned. This could be because our graph is directional, 
            // so maybe *this* table points to one of the ones we've already scanned. 
            // Additionally, it's possible that this table points to ANOTHER table that's not scanned yet, but 
            // is found LATER in the orderedTables list (and thus, will be scanned) 
            if (tableNode.edges != null && tableNode.edges.length > 0) {
                for (Edge edge : tableNode.edges) {
                    // if the node has edges, check 'em. Maybe this table points to either
                    // (a) a table that we've already scanned 
                    if (tableGraph.keySet().contains(edge.pointedAt.dimension)) {
                        // ayup, already scanned a table this one is pointing at. might as well scan this one 
                    }
                    // or 
                    // (b) a table that we will be scanning soon
                }
            }
        }
        */


        /*
        // ...
        Set<Node> nodesPointingAt = getAllNodesPointingAt(firstDimensionNode);
        for (Node nodePointingAt : nodesPointingAt) {
            // if we have unscanned nodes that are directly pointing to our initial node,
        }
        

        // now, check to see if all tables are in the set (i.e. connected) 
        // any dimension not in the set indicates a query without a complete join path
        for (Dimension table : orderedTables) {
            Node tableNode = allDimensionsGraph.get(table);
            if (!tableGraph.containsKey(tableNode)) {
                return false;
            }
        }
        return true;
        */
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

    private static List<Dimension> breadthFirstSearch(Dimension rootDimension, Dimension targetDimension) {
        PriorityQueue<Dimension> dimensionsToSearch = new PriorityQueue<>();

        return null;
    }

}
