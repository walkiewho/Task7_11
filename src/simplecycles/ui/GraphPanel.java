package simplecycles.ui;

import simplecycles.graph.SimpleGraph;
import simplecycles.model.CycleResult;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {
    private static final int RADIUS = 18;
    private SimpleGraph graph;
    private List<CycleResult> cycles = List.of();
    private int selectedCycle = -1;

    public GraphPanel() {
        setPreferredSize(new Dimension(900, 700));
        setBackground(Color.WHITE);
    }

    public void setGraph(SimpleGraph graph) {
        this.graph = graph;
        repaint();
    }

    public void setCycles(List<CycleResult> cycles) {
        this.cycles = cycles == null ? List.of() : List.copyOf(cycles);
        repaint();
    }

    public void setSelectedCycle(int index) {
        this.selectedCycle = index;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int n = graph.vertexCount();
            List<int[]> pos = layout(n);

            drawEdges(g2, pos);
            drawSelectedCycle(g2, pos);
            drawVertices(g2, pos);
        } finally {
            g2.dispose();
        }
    }

    private List<int[]> layout(int n) {
        List<int[]> pos = new ArrayList<>(n);
        int w = Math.max(1, getWidth());
        int h = Math.max(1, getHeight());
        int cx = w / 2;
        int cy = h / 2;
        int r = Math.max(120, Math.min(w, h) / 2 - 80);
        for (int i = 0; i < n; i++) {
            double angle = -Math.PI / 2 + 2 * Math.PI * i / Math.max(1, n);
            int x = (int) Math.round(cx + r * Math.cos(angle));
            int y = (int) Math.round(cy + r * Math.sin(angle));
            pos.add(new int[]{x, y});
        }
        return pos;
    }

    private void drawEdges(Graphics2D g2, List<int[]> pos) {
        g2.setColor(new Color(180, 180, 180));
        g2.setStroke(new BasicStroke(1.8f));
        int n = graph.vertexCount();
        for (int u = 0; u < n; u++) {
            for (int v : graph.neighbors(u)) {
                if (u < v) {
                    int[] a = pos.get(u);
                    int[] b = pos.get(v);
                    g2.draw(new Line2D.Double(a[0], a[1], b[0], b[1]));
                }
            }
        }
    }

    private void drawSelectedCycle(Graphics2D g2, List<int[]> pos) {
        if (selectedCycle < 0 || selectedCycle >= cycles.size()) {
            return;
        }
        List<Integer> cycle = cycles.get(selectedCycle).vertices();
        if (cycle.isEmpty()) {
            return;
        }
        int n = cycle.size();
        int step = Math.max(1, 256 / n);

        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < n; i++) {
            int u = cycle.get(i);
            int v = cycle.get((i + 1) % n);
            int[] a = pos.get(u);
            int[] b = pos.get(v);
            g2.setColor(colorForIndex(i, step));
            g2.draw(new Line2D.Double(a[0], a[1], b[0], b[1]));
        }
    }

    private void drawVertices(Graphics2D g2, List<int[]> pos) {
        int n = graph.vertexCount();
        for (int i = 0; i < n; i++) {
            Color fill = new Color(70, 130, 180);
            int labelIndex = -1;
            int cycleSize = -1;
            if (selectedCycle >= 0 && selectedCycle < cycles.size()) {
                List<Integer> cycle = cycles.get(selectedCycle).vertices();
                cycleSize = cycle.size();
                for (int j = 0; j < cycle.size(); j++) {
                    if (cycle.get(j) == i) {
                        labelIndex = j;
                        break;
                    }
                }
            }
            if (labelIndex >= 0 && cycleSize > 0) {
                int step = Math.max(1, 256 / cycleSize);
                fill = colorForIndex(labelIndex, step);
            }

            int[] p = pos.get(i);
            g2.setColor(fill);
            g2.fill(new Ellipse2D.Double(p[0] - RADIUS, p[1] - RADIUS, RADIUS * 2.0, RADIUS * 2.0));
            g2.setColor(Color.BLACK);
            g2.draw(new Ellipse2D.Double(p[0] - RADIUS, p[1] - RADIUS, RADIUS * 2.0, RADIUS * 2.0));

            String label = String.valueOf(i);
            FontMetrics fm = g2.getFontMetrics();
            int tx = p[0] - fm.stringWidth(label) / 2;
            int ty = p[1] + fm.getAscent() / 2 - 2;
            g2.drawString(label, tx, ty);
        }
    }

    private Color colorForIndex(int index, int step) {
        int value = Math.min(255, index * step);
        return new Color(value, 255 - value, 0);
    }
}
