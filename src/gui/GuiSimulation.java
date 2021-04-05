package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import model.Simulation;

/**
 *
 * @author tadaki
 */
public class GuiSimulation implements Runnable {

    private volatile boolean running = false;
    private final int sleepTime = 100;
    private int tInterval = 100;
    private final Simulation sys;
    private final double r = 10;
    private final double fx = 2.;
    private final double fy = 2;
    private final int tMax = 500;
    private final double coolingRate = 0.98;
    private int t = 0;
    private DrawPanel drawPanel;

    public GuiSimulation() throws IOException {
        sys = new Simulation("points.txt",new Random(48L));
        t = 0;
    }

    public void createImage(BufferedImage image) {
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.translate(50, 850);
        g.scale(1., -1.);
        for (int i = 0; i < tInterval; i++) {
            sys.oneMonteCarloStep();
        }
        List<Point> path = sys.currentPath();
        Path2D.Double polygon = new Path2D.Double();
        polygon.moveTo(fx * path.get(0).x, fy * path.get(0).y);
        for (int i = 1; i < path.size(); i++) {
            Point p = path.get(i);
            polygon.lineTo(fx * p.x, fy * p.y);
        }
        polygon.closePath();
        g.setColor(Color.CYAN);
        g.setStroke(new BasicStroke(3));
        g.draw(polygon);
        g.setColor(Color.red);
        path.forEach(p
                -> g.fill(new Ellipse2D.Double(fx * p.x - r, fy * p.y - r, 
                        2 * r, 2 * r)));
        sys.cooling(coolingRate);
        t++;
        tInterval = (int) (tInterval / coolingRate);
    }

    public void setDrawPanel(DrawPanel drawPanel) {
        this.drawPanel = drawPanel;
    }

    @Override
    public void run() {
        while (running) {
            drawPanel.initImage();
            BufferedImage image = drawPanel.getImage();
            createImage(image);
            drawPanel.setImgage(image);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
