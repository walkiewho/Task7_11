package simplecycles.ui;

import simplecycles.graph.SimpleGraph;
import simplecycles.model.CycleResult;
import simplecycles.solver.CycleSolver;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CycleFrame extends JFrame {
    private final JTextArea edgesArea = new JTextArea(12, 24);
    private final JTextArea forbiddenArea = new JTextArea(6, 24);
    private final DefaultListModel<String> cycleListModel = new DefaultListModel<>();
    private final JList<String> cycleList = new JList<>(cycleListModel);
    private final GraphPanel graphPanel = new GraphPanel();
    private final JLabel statusLabel = new JLabel("Введите рёбра и нажмите кнопку поиска.");

    private final CycleSolver solver = new CycleSolver();

    public CycleFrame() {
        super("Поиск циклов в графе");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        edgesArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        forbiddenArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        edgesArea.setText("0 1\n1 2\n2 0\n2 3\n3 4\n4 2");
        forbiddenArea.setText("");

        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        inputPanel.add(wrapWithLabel("Рёбра (по одной связи в строке: u v)", new JScrollPane(edgesArea)));
        inputPanel.add(wrapWithLabel("Запрещённые вершины (список чисел)", new JScrollPane(forbiddenArea)));

        JButton solveButton = new JButton("Построить граф и найти циклы");
        solveButton.addActionListener(e -> solveFromInput());

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(solveButton, BorderLayout.SOUTH);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        cycleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cycleList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                graphPanel.setSelectedCycle(cycleList.getSelectedIndex());
            }
        });

        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JPanel(),
                graphPanel
        );
        mainSplit.setResizeWeight(0.0);
        mainSplit.setLeftComponent(makeSidePanel());

        add(topPanel, BorderLayout.NORTH);
        add(mainSplit, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setSize(1300, 850);
        setLocationRelativeTo(null);
    }

    private JPanel makeSidePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(300, 600));
        panel.add(new JScrollPane(cycleList), BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        return panel;
    }

    private JPanel wrapWithLabel(String label, JScrollPane scrollPane) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel(label), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void solveFromInput() {
        try {
            Set<Integer> forbidden = parseForbidden(forbiddenArea.getText());
            List<int[]> edges = parseEdges(edgesArea.getText());
            int vertexCount = inferVertexCount(edges, forbidden);

            SimpleGraph graph = new SimpleGraph(vertexCount);
            for (int[] edge : edges) {
                graph.addEdge(edge[0], edge[1]);
            }

            List<CycleResult> cycles = solver.solve(graph, forbidden);

            cycleListModel.clear();
            for (int i = 0; i < cycles.size(); i++) {
                cycleListModel.addElement((i + 1) + ": " + cycles.get(i));
            }

            graphPanel.setGraph(graph);
            graphPanel.setCycles(cycles);
            graphPanel.setSelectedCycle(cycles.isEmpty() ? -1 : 0);
            cycleList.setSelectedIndex(cycles.isEmpty() ? -1 : 0);
            statusLabel.setText("Вершин: " + vertexCount + ", рёбер: " + edges.size() + ", циклов: " + cycles.size());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "input error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Set<Integer> parseForbidden(String text) {
        Set<Integer> result = new LinkedHashSet<>();
        String cleaned = text == null ? "" : text.trim();
        if (cleaned.isEmpty()) {
            return result;
        }
        String[] tokens = cleaned.split("\\s+");
        for (String token : tokens) {
            if (token.isBlank()) {
                continue;
            }
            result.add(Integer.parseInt(token));
        }
        return result;
    }

    private List<int[]> parseEdges(String text) {
        List<int[]> edges = new ArrayList<>();
        String[] lines = (text == null ? "" : text).split("\\R");
        for (int lineNo = 0; lineNo < lines.length; lineNo++) {
            String line = lines[lineNo].trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+");
            if (parts.length != 2) {
                throw new IllegalArgumentException("line format error");
            }
            int u = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);
            if (u < 0 || v < 0) {
                throw new IllegalArgumentException("negative vertex error");
            }
            edges.add(new int[]{u, v});
        }
        return edges;
    }

    private int inferVertexCount(List<int[]> edges, Set<Integer> forbidden) {
        int max = -1;
        for (int[] edge : edges) {
            max = Math.max(max, Math.max(edge[0], edge[1]));
        }
        for (int v : forbidden) {
            if (v < 0) {
                throw new IllegalArgumentException("negative forbidden vertex ");
            }
            max = Math.max(max, v);
        }
        return max + 1;
    }
}
