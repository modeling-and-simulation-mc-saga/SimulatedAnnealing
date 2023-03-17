package model;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * TSPのsimulated annealing
 *
 * @author tadaki
 */
public class Simulation {

    private Route path;//current path
    private final Route initialPath;
    private double temperature;//temperature
    private final Random random;

    /**
     * Reading file for initializing path
     *
     * @param filename
     * @param random
     * @throws IOException
     */
    public Simulation(String filename, Random random) throws IOException {
        List<Point> list = Collections.synchronizedList(new ArrayList<>());
        this.random = random;
        try ( BufferedReader in = openReader(filename)) {
            String line;
            while ((line = in.readLine()) != null) {
                String s[] = line.split("\\s+");
                if (s.length > 1) {
                    try {
                        int x = Integer.parseInt(s[0]);
                        int y = Integer.parseInt(s[1]);
                        list.add(new Point(x, y));
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        path = new Route(list, random);
        initialPath = new Route(list, random);
        path.evalPathLength();
        //Setting high temperature
        temperature = list.size() * Math.max(path.getMax().x - path.getMin().x,
                path.getMax().y - path.getMin().y);
    }

    public Simulation(String lines[], Random random) throws NumberFormatException {
        List<Point> list = Collections.synchronizedList(new ArrayList<>());
        this.random = random;
        for (String line : lines) {
            String s[] = line.split("\\s+");
            if (s.length > 1) {
                try {
                    int x = Integer.parseInt(s[0]);
                    int y = Integer.parseInt(s[1]);
                    list.add(new Point(x, y));
                } catch (NumberFormatException e) {
                    throw e;
                }
            }
        }
        path = new Route(list, random);
        initialPath = new Route(list, random);
        path.evalPathLength();
        //Setting high temperature
        temperature = list.size() * Math.max(path.getMax().x - path.getMin().x,
                path.getMax().y - path.getMin().y);
    }

    /**
     * one Mc step
     *
     * @return
     */
    public double oneMonteCarloStep() {
        int n = path.numCity();
        //trials for changing path
        for (int i = 0; i < n; i++) {
            oneFlip();
        }
        return path.getPathLength();
    }

    /**
     * One trial for changing path
     *
     * @return true if changed
     */
    protected boolean oneFlip() {
        Route cand = path.nextRoute();//Condidate for new path
        double d = cand.getPathLength() - path.getPathLength();
        if (d < 0.) {//the new path is shorter than the current
            path = cand;
            path.evalPathLength();
            return true;
        }
        //the new path is longer than the current
        if (random.nextDouble() < Math.exp(-d / temperature)) {
            path = cand;
            path.evalPathLength();
            return true;
        }
        return false;
    }

    /**
     * Decreasing temperature T->d*T
     *
     * @param d
     * @return
     */
    public double cooling(double d) {
        temperature *= d;
        return temperature;
    }

    public List<Point> currentPath() {
        return path.getPath();
    }

    public double getPathLength() {
        return path.getPathLength();
    }

    public String currentPathStr() {
        return path.toString();
    }

    public Point getMin() {
        return path.getMin();
    }

    public Point getMax() {
        return path.getMax();
    }

    public double getTemperature() {
        return temperature;
    }

    public void reInitialize() {
        path = new Route(initialPath.getPath(), random);
        path.evalPathLength();
        temperature = Math.max(path.getMax().x, path.getMax().y);
    }

    public static BufferedReader openReader(String filename) throws IOException {
        FileInputStream fStream = new FileInputStream(new File(filename));
        return new BufferedReader(new InputStreamReader(fStream));
    }

    /**
     * @param args
     * @
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Simulation sim = new Simulation("points.txt", new Random(48L));
        List<Result> plist = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < 200; i++) {
            for (int t = 0; t < 1000; t++) {
                sim.oneMonteCarloStep();
            }
            plist.add(new Result(i, sim.getPathLength(), sim.getTemperature()));
            sim.cooling(0.9);
        }
        String filename = Simulation.class.getSimpleName() + ".txt";
        try ( PrintStream out = new PrintStream(filename)) {
            plist.forEach(r -> out.println(r.t() + " " + r.d() + " " + r.temp()));
        }
    }

}
