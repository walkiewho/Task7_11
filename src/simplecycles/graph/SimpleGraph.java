package simplecycles.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleGraph {
    private final List<List<Integer>> adj;

    public SimpleGraph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount < 0");
        }
        this.adj = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public int vertexCount() {
        return adj.size();
    }

    public void addEdge(int u, int v) {
        checkVertex(u);
        checkVertex(v);
        if (u == v) {
            return;
        }
        if (!adj.get(u).contains(v)) {
            adj.get(u).add(v);
        }
        if (!adj.get(v).contains(u)) {
            adj.get(v).add(u);
        }
    }

    public List<Integer> neighbors(int v) {
        checkVertex(v);
        return Collections.unmodifiableList(adj.get(v));
    }

    public boolean hasEdge(int u, int v) {
        checkVertex(u);
        checkVertex(v);
        return adj.get(u).contains(v);
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= vertexCount()) {
            throw new IllegalArgumentException("Invalid vertex: " + v);
        }
    }
}
