package com.tinkerpop.blueprints.pgm.impls.neo4j;

import com.tinkerpop.blueprints.pgm.AutomaticIndexTestSuite;
import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.EdgeTestSuite;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.GraphTestSuite;
import com.tinkerpop.blueprints.pgm.Index;
import com.tinkerpop.blueprints.pgm.IndexTestSuite;
import com.tinkerpop.blueprints.pgm.IndexableGraph;
import com.tinkerpop.blueprints.pgm.IndexableGraphTestSuite;
import com.tinkerpop.blueprints.pgm.TestSuite;
import com.tinkerpop.blueprints.pgm.TransactionalGraphTestSuite;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.VertexTestSuite;
import com.tinkerpop.blueprints.pgm.impls.GraphTest;
import com.tinkerpop.blueprints.pgm.impls.Parameter;
import com.tinkerpop.blueprints.pgm.util.io.graphml.GraphMLReaderTestSuite;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSetting;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.index.impl.lucene.LowerCaseKeywordAnalyzer;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Neo4jGraphTest extends GraphTest {


    public Neo4jGraphTest() {
        this.allowsDuplicateEdges = true;
        this.allowsSelfLoops = true;
        this.isPersistent = true;
        this.isRDFModel = false;
        this.supportsVertexIteration = true;
        this.supportsEdgeIteration = true;
        this.supportsVertexIndex = true;
        this.supportsEdgeIndex = true;
        this.ignoresSuppliedIds = true;
        this.supportsTransactions = true;

        this.allowSerializableObjectProperty = false;
        this.allowBooleanProperty = true;
        this.allowDoubleProperty = true;
        this.allowFloatProperty = true;
        this.allowIntegerProperty = true;
        this.allowPrimitiveArrayProperty = true;
        this.allowUniformListProperty = true;
        this.allowMixedListProperty = false;
        this.allowLongProperty = true;
        this.allowMapProperty = false;
        this.allowStringProperty = true;

    }

    /*public void testNeo4jBenchmarkTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new Neo4jBenchmarkTestSuite(this));
        printTestPerformance("Neo4jBenchmarkTestSuite", this.stopWatch());
    }*/

    public void testVertexTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new VertexTestSuite(this));
        printTestPerformance("VertexTestSuite", this.stopWatch());
    }

    public void testEdgeTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new EdgeTestSuite(this));
        printTestPerformance("EdgeTestSuite", this.stopWatch());
    }

    public void testGraphTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphTestSuite(this));
        printTestPerformance("GraphTestSuite", this.stopWatch());
    }

    public void testIndexableGraphTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new IndexableGraphTestSuite(this));
        printTestPerformance("IndexableGraphTestSuite", this.stopWatch());
    }

    public void testIndexTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new IndexTestSuite(this));
        printTestPerformance("IndexTestSuite", this.stopWatch());
    }

    public void testAutomaticIndexTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new AutomaticIndexTestSuite(this));
        printTestPerformance("AutomaticIndexTestSuite", this.stopWatch());
    }

    public void testTransactionalGraphTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new TransactionalGraphTestSuite(this));
        printTestPerformance("TransactionalGraphTestSuite", this.stopWatch());
    }

    public void testGraphMLReaderTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphMLReaderTestSuite(this));
        printTestPerformance("GraphMLReaderTestSuite", this.stopWatch());
    }

    public Graph getGraphInstance() {
        String directory = System.getProperty("neo4jGraphDirectory");
        if (directory == null)
            directory = this.getWorkingDirectory();
        return new Neo4jGraph(directory);
    }

    public void doTestSuite(final TestSuite testSuite) throws Exception {
        String doTest = System.getProperty("testNeo4jGraph");
        if (doTest == null || doTest.equals("true")) {
            String directory = System.getProperty("neo4jGraphDirectory");
            if (directory == null)
                directory = this.getWorkingDirectory();
            deleteDirectory(new File(directory));
            for (Method method : testSuite.getClass().getDeclaredMethods()) {
                if (method.getName().startsWith("test")) {
                    System.out.println("Testing " + method.getName() + "...");
                    method.invoke(testSuite);
                    deleteDirectory(new File(directory));
                }
            }
        }
    }

    private String getWorkingDirectory() {
        String directory = System.getProperty("neo4jGraphDirectory");
        if (directory == null) {
            if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
                directory = "C:/temp/blueprints_test";
            else
                directory = "/tmp/blueprints_test";
        }
        return directory;
    }

    public void testLongIdConversions() {
        String id1 = "100";  // good  100
        String id2 = "100.0"; // good 100
        String id3 = "100.1"; // good 100
        String id4 = "one"; // bad

        try {
            Double.valueOf(id1).longValue();
            assertTrue(true);
        } catch (NumberFormatException e) {
            assertFalse(true);
        }
        try {
            Double.valueOf(id2).longValue();
            assertTrue(true);
        } catch (NumberFormatException e) {
            assertFalse(true);
        }
        try {
            Double.valueOf(id3).longValue();
            assertTrue(true);
        } catch (NumberFormatException e) {
            assertFalse(true);
        }
        try {
            Double.valueOf(id4).longValue();
            assertTrue(false);
        } catch (NumberFormatException e) {
            assertFalse(false);
        }
    }

    public void testQueryIndex() throws Exception {
        String directory = System.getProperty("neo4jGraphDirectory");
        if (directory == null)
            directory = this.getWorkingDirectory();
        deleteDirectory(new File(directory));

        IndexableGraph graph = new Neo4jGraph(directory);
        Vertex a = graph.addVertex(null);
        a.setProperty("name", "marko");
        Iterator itty = graph.getIndex(Index.VERTICES, Vertex.class).get("name", Neo4jTokens.QUERY_HEADER + "*rko").iterator();
        int counter = 0;
        while (itty.hasNext()) {
            counter++;
            assertEquals(itty.next(), a);
        }
        assertEquals(counter, 1);

        Vertex b = graph.addVertex(null);
        Edge edge = graph.addEdge(null, a, b, "knows");
        edge.setProperty("weight", 0.75);
        itty = graph.getIndex(Index.EDGES, Edge.class).get("label", Neo4jTokens.QUERY_HEADER + "k?ows").iterator();
        counter = 0;
        while (itty.hasNext()) {
            counter++;
            assertEquals(itty.next(), edge);
        }
        assertEquals(counter, 1);
        itty = graph.getIndex(Index.EDGES, Edge.class).get("weight", Neo4jTokens.QUERY_HEADER + "[0.5 TO 1.0]").iterator();
        counter = 0;
        while (itty.hasNext()) {
            counter++;
            assertEquals(itty.next(), edge);
        }
        assertEquals(counter, 1);
        assertEquals(count(graph.getIndex(Index.EDGES, Edge.class).get("weight", Neo4jTokens.QUERY_HEADER + "[0.1 TO 0.5]")), 0);


        graph.shutdown();
        deleteDirectory(new File(directory));
    }

    public void testIndexParameters() throws Exception {
        String directory = System.getProperty("neo4jGraphDirectory");
        if (directory == null)
            directory = this.getWorkingDirectory();
        deleteDirectory(new File(directory));

        IndexableGraph graph = new Neo4jGraph(directory);
        graph.dropIndex(Index.VERTICES);
        graph.createAutomaticIndex(Index.VERTICES, Vertex.class, null, new Parameter("analyzer", LowerCaseKeywordAnalyzer.class.getName()));
        Vertex a = graph.addVertex(null);
        a.setProperty("name", "marko");
        Iterator itty = graph.getIndex(Index.VERTICES, Vertex.class).get("name", Neo4jTokens.QUERY_HEADER + "*rko").iterator();
        int counter = 0;
        while (itty.hasNext()) {
            counter++;
            assertEquals(itty.next(), a);
        }
        assertEquals(counter, 1);

        itty = graph.getIndex(Index.VERTICES, Vertex.class).get("name", Neo4jTokens.QUERY_HEADER + "MaRkO").iterator();
        counter = 0;
        while (itty.hasNext()) {
            counter++;
            assertEquals(itty.next(), a);
        }
        assertEquals(counter, 1);

        graph.shutdown();
        deleteDirectory(new File(directory));
    }

    public void testShouldNotDeleteAutomaticNeo4jIndexes() {
        String directory = System.getProperty("neo4jGraphDirectory");
        if (directory == null)
            directory = this.getWorkingDirectory();
        deleteDirectory(new File(directory));

        Neo4jGraph graph = new Neo4jGraph(new GraphDatabaseFactory().
                newEmbeddedDatabaseBuilder(directory).
                setConfig(GraphDatabaseSettings.node_keys_indexable, "name").
                setConfig(GraphDatabaseSettings.relationship_keys_indexable, "rel1").
                setConfig(GraphDatabaseSettings.node_auto_indexing, GraphDatabaseSetting.TRUE).
                setConfig(GraphDatabaseSettings.relationship_auto_indexing, GraphDatabaseSetting.TRUE).
                newGraphDatabase());
        Vertex a = graph.addVertex(null);
        a.setProperty("name", "foo");
        graph.shutdown();

        graph = new Neo4jGraph(directory);
        graph.clear();
        assertTrue(null != graph.getRawGraph().index().getNodeAutoIndexer().getAutoIndex());
        graph.shutdown();
        deleteDirectory(new File(directory));

    }

    public void testShouldNotDeleteAutomaticBlueprintsIndexes() {
        String directory = System.getProperty("neo4jGraphDirectory");
        if (directory == null)
            directory = this.getWorkingDirectory();
        deleteDirectory(new File(directory));

        Neo4jGraph graph = new Neo4jGraph(directory);
        Vertex a = graph.addVertex(null);
        a.setProperty("name", "foo");
        graph.shutdown();

        graph = new Neo4jGraph(directory);
        graph.clear();
        a = graph.addVertex(null);
        a.setProperty("name", "foo");
        assertNotNull(graph.getRawGraph().index().getNodeAutoIndexer().getAutoIndex());

        graph.shutdown();
        deleteDirectory(new File(directory));

    }

    public void testAutomaticIndexKernalPropertyValue() {
        String directory = System.getProperty("neo4jGraphDirectory");
        if (directory == null)
            directory = this.getWorkingDirectory();
        deleteDirectory(new File(directory));

        Neo4jGraph graph = new Neo4jGraph(directory);
        assertEquals(count(graph.getIndices()), 2);
        assertTrue((Boolean) graph.getKernalProperty(Neo4jTokens.BLUEPRINTS_HASAUTOINDEX));
        graph.dropIndex(Index.VERTICES);
        assertEquals(count(graph.getIndices()), 1);
        assertTrue((Boolean) graph.getKernalProperty(Neo4jTokens.BLUEPRINTS_HASAUTOINDEX));
        graph.dropIndex(Index.EDGES);
        assertEquals(count(graph.getIndices()), 0);
        assertFalse((Boolean) graph.getKernalProperty(Neo4jTokens.BLUEPRINTS_HASAUTOINDEX));
        graph.shutdown();

        graph = new Neo4jGraph(directory);
        assertEquals(count(graph.getIndices()), 0);
        assertFalse((Boolean) graph.getKernalProperty(Neo4jTokens.BLUEPRINTS_HASAUTOINDEX));
        graph.shutdown();

        graph = new Neo4jGraph(directory);
        assertEquals(count(graph.getIndices()), 0);
        assertFalse((Boolean) graph.getKernalProperty(Neo4jTokens.BLUEPRINTS_HASAUTOINDEX));
        graph.createAutomaticIndex("anIndex", Edge.class, null);
        assertEquals(count(graph.getIndices()), 1);
        assertTrue((Boolean) graph.getKernalProperty(Neo4jTokens.BLUEPRINTS_HASAUTOINDEX));
        graph.dropIndex("anIndex");
        assertEquals(count(graph.getIndices()), 0);
        assertFalse((Boolean) graph.getKernalProperty(Neo4jTokens.BLUEPRINTS_HASAUTOINDEX));
        graph.shutdown();

        graph = new Neo4jGraph(directory);
        assertEquals(count(graph.getIndices()), 0);
        assertFalse((Boolean) graph.getKernalProperty(Neo4jTokens.BLUEPRINTS_HASAUTOINDEX));
        graph.createAutomaticIndex("anIndex", Vertex.class, null);
        assertEquals(count(graph.getIndices()), 1);
        assertTrue((Boolean) graph.getKernalProperty(Neo4jTokens.BLUEPRINTS_HASAUTOINDEX));
        graph.dropIndex("anIndex");
        assertEquals(count(graph.getIndices()), 0);
        assertFalse((Boolean) graph.getKernalProperty(Neo4jTokens.BLUEPRINTS_HASAUTOINDEX));
        graph.shutdown();

        deleteDirectory(new File(directory));


    }
}
