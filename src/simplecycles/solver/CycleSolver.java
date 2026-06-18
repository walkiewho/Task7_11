package simplecycles.solver;

import simplecycles.graph.SimpleGraph;
import simplecycles.model.CycleResult;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CycleSolver {
    public List<CycleResult> solve(SimpleGraph graph, Set<Integer> forbiddenVertices) {
        int n = graph.vertexCount();
        boolean[] forbidden = new boolean[n];
        for (int v : forbiddenVertices) {
            if (v >= 0 && v < n) {
                forbidden[v] = true;
            }
        }

        Set<HashSet> seen = new HashSet<>();
        List<CycleResult> cycles = new ArrayList<>();
        boolean[] used = new boolean[n];
        ArrayDeque<Integer> path = new ArrayDeque<>();

        for (int start = 0; start < n; start++) {
            if (forbidden[start]) {
                continue;
            }
            Arrays.fill(used, false);
            path.clear();
            dfs(graph, start, start, forbidden, used, path, seen, cycles);
        }

        cycles.sort((a, b) -> {
            int c = Integer.compare(a.size(), b.size());
            if (c != 0) return c;
            String sa = a.toString();
            String sb = b.toString();
            return sa.compareTo(sb);
        });
        return cycles;
    }

    private void dfs(SimpleGraph graph, int start, int current, boolean[] forbidden, boolean[] used, ArrayDeque<Integer> path, Set<HashSet> seen, List<CycleResult> cycles) {
        used[current] = true;
        path.addLast(current);

        for (int next : graph.neighbors(current)) {
            if (forbidden[next]) {
                continue;
            }
            if (next == start) {
                if (path.size() >= 3) {
                    List<Integer> cycle = new ArrayList<>(path);
                    if (seen.add(new HashSet(cycle))) {
                        cycles.add(new CycleResult(cycle));
                    }
                }
                continue;
            }
            if (!used[next] && next > start) {
                dfs(graph, start, next, forbidden, used, path, seen, cycles);
            }
        }

        path.removeLast();
        used[current] = false;
    }
}
