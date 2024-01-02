package connect.Component.Chart.CurveChart;

import javax.swing.*;
import java.awt.*;

public class LabelColor2 extends JLabel {
    
    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height) - 4;
        int x = (width - size) / 2;
        int y = (height - size) / 2;
        g2.setPaint(new GradientPaint(0, 0, getBackground(), width, 0, getForeground()));
        g2.fillOval(x, y, size, size);
    }
}