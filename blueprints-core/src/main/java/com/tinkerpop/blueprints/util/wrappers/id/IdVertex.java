package com.tinkerpop.blueprints.util.wrappers.id;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Query;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.WrapperQuery;

/**
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class IdVertex extends IdElement implements Vertex {

    public IdVertex(final Vertex baseVertex) {
        super(baseVertex);
    }

    public Vertex getBaseVertex() {
        return (Vertex) this.baseElement;
    }

    public Iterable<Edge> getEdges(final Direction direction, final String... labels) {
        return new IdEdgeIterable(((Vertex) this.baseElement).getEdges(direction, labels));
    }

    public Iterable<Vertex> getVertices(final Direction direction, final String... labels) {
        return new IdVertexIterable(((Vertex) this.baseElement).getVertices(direction, labels));
    }

    public Query query() {
        return new WrapperQuery(((Vertex) this.baseElement).query()) {
            @Override
            public Iterable<Vertex> vertices() {
                return new IdVertexIterable(this.query.vertices());
            }

            @Override
            public Iterable<Edge> edges() {
                return new IdEdgeIterable(this.query.edges());
            }
        };
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof IdVertex && ((Vertex) other).getId().equals(getId());
    }
}