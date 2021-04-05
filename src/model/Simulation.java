package model;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import myLib.utils.FileIO;
import myLib.utils.Utils;

/**
 * TSPのsimulated annealing
 *
 * @author tadaki
 */
public class Simulation {

    private Route path;//現在の経路
    private final Route initialPath;
    private double temperature;//温度
    private final Random random;

    /**
     * 頂点一覧のファイルを指定して初期化
     *
     * @param filename
     * @throws IOException
     */
    public Simulation(String filename, Random random) throws IOException {
        List<Point> list = Utils.createList();
        this.random = random;
        try (BufferedReader in = FileIO.openReader(filename)) {
            String line;
            while ((line = in.readLine()) != null) {
                String s[] = line.split("\\s+");
                if (s.length > 1) {
                    try {
                        int x = Integer.valueOf(s[0]);
                        int y = Integer.valueOf(s[1]);
                        list.add(new Point(x, y));
                    } catch (NumberFormatException e) {
                    }
                }
            }
        }
        path = new Route(list,random);
        initialPath = new Route(list,random);
        path.calcPathLength();
        //十分高温に設定
        temperature = list.size() * Math.max(path.getMax().x - path.getMin().x,
                path.getMax().y - path.getMin().y);
    }

    public Simulation(String lines[],Random random) throws NumberFormatException {
        List<Point> list = Utils.createList();
        this.random = random;
        for (String line : lines) {
            String s[] = line.split("\\s+");
            if (s.length > 1) {
                try {
                    int x = Integer.valueOf(s[0]);
                    int y = Integer.valueOf(s[1]);
                    list.add(new Point(x, y));
                } catch (NumberFormatException e) {
                    throw e;
                }
            }
        }
        path = new Route(list,random);
        initialPath = new Route(list,random);
        path.calcPathLength();
        //十分高温に設定
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
        //頂点数回、経路変更を試行する
        for (int i = 0; i < n; i++) {
            oneFlip();
        }
        return path.getPathLength();
    }

    /**
     * 一回の経路変更試行
     *
     * @return 経路が変更されたらtrue
     */
    protected boolean oneFlip() {
        Route cand = path.nextRoute();//新しい経路の候補
        double d = cand.getPathLength() - path.getPathLength();
        if (d < 0.) {//新経路の方が短い
            path = cand;
            path.calcPathLength();
            return true;
        }
        //新経路の方が長い
        if (random.nextDouble() < Math.exp(-d / temperature)) {
            path = cand;
            path.calcPathLength();
            return true;
        }
        return false;
    }

    /**
     * 温度をT->d*Tへ変更する
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
        path = new Route(initialPath.getPath(),random);
        path.calcPathLength();
        temperature = Math.max(path.getMax().x, path.getMax().y);
    }

    /**
     * @param args
     * @
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Simulation sim = new Simulation("points.txt",new Random(48L));
        List<Result> plist = Utils.createList();
        for (int i = 0; i < 200; i++) {
            for (int t = 0; t < 1000; t++) {
                sim.oneMonteCarloStep();
            }
            plist.add(new Result(i, sim.getPathLength(), sim.getTemperature()));
            sim.cooling(0.9);
        }
        String filename = Simulation.class.getSimpleName() + ".txt";
        try (BufferedWriter out = FileIO.openWriter(filename)) {
            for (Result r : plist) {
                FileIO.writeSSV(out, r.t, r.d, r.temp);
            }
        }
    }

}
