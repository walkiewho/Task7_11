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

        Set<String> seen = new HashSet<>();
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

    private void dfs(SimpleGraph graph,
                     int start,
                     int current,
                     boolean[] forbidden,
                     boolean[] used,
                     ArrayDeque<Integer> path,
                     Set<String> seen,
                     List<CycleResult> cycles) {
        used[current] = true;
        path.addLast(current);

        for (int next : graph.neighbors(current)) {
            if (forbidden[next]) {
                continue;
            }
            if (next == start) {
                if (path.size() >= 3) {
                    List<Integer> cycle = new ArrayList<>(path);
                    String key = canonicalKey(cycle);
                    if (seen.add(key)) {
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

    private String canonicalKey(List<Integer> cycle) {
        int n = cycle.size();
        int[] a = cycle.stream().mapToInt(Integer::intValue).toArray();
        int[] best = null;

        for (int dir = 0; dir < 2; dir++) {
            int[] b = new int[n];
            for (int i = 0; i < n; i++) {
                b[i] = dir == 0 ? a[i] : a[n - 1 - i];
            }
            for (int shift = 0; shift < n; shift++) {
                int[] rot = new int[n];
                for (int i = 0; i < n; i++) {
                    rot[i] = b[(i + shift) % n];
                }
                if (best == null || lexLess(rot, best)) {
                    best = rot;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < best.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(best[i]);
        }
        return sb.toString();
    }

    private boolean lexLess(int[] a, int[] b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return a[i] < b[i];
            }
        }
        return false;
    }
}
