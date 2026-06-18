package simplecycles.model;

import java.util.List;

public class CycleResult {
    private final List<Integer> vertices;

    public CycleResult(List<Integer> vertices) {
        this.vertices = List.copyOf(vertices);
    }

    public List<Integer> vertices() {
        return vertices;
    }

    public int size() {
        return vertices.size();
    }

    @Override
    public String toString() {
        return vertices.toString();
    }
}
