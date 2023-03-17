package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * One circuit
 *
 * @author tadaki
 */
public class Route {

    private final List<Point> path;//Sequence of nodes
    private double pathLength;//the length of the path
    //area of nodes
    private Point min;
    private Point max;
    private final Random random;

    /**
     *
     *
     * @param path initial path
     * @param random
     */
    public Route(List<Point> path, Random random) {
        this.path = path;
        evalArea();
        this.random = random;
    }

    /**
     * Evaluate area
     */
    private void evalArea() {
        min = new Point(path.get(0));
        max = new Point(path.get(0));
        path.forEach(p -> {
            min.x = Math.min(min.x, p.x);
            min.y = Math.min(min.y, p.y);
            max.x = Math.max(max.x, p.x);
            max.y = Math.max(max.y, p.y);
        });
    }

    /**
     * Evaluate path length
     *
     * @return 経路長
     */
    public double evalPathLength() {
        int n = path.size();
        pathLength = 0.;
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            Point p = path.get(i);
            pathLength += p.distance(path.get(j));
        }
        return pathLength;
    }

    /**
     * Generate new path
     *
     *
     * @return new path
     */
    public Route nextRoute() {
        int n = path.size();
        int pp = 0;
        int qq = 0;
        while (pp == qq) {
            pp = random.nextInt(n - 1);
            qq = random.nextInt(n - 1);
        }
        int p = 1 + Math.min(pp, qq);
        int q = 1 + Math.max(pp, qq);
        return nextRouteSub(p, q);
    }

    /**
     * Generate new path by fixing (p,q)
     *
     * @param p
     * @param q
     * @return
     */
    private Route nextRouteSub(int p, int q) {
        List<Point> newPath = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < p; i++) {
            newPath.add(path.get(i));
        }
        for (int i = q; i >= p; i--) {
            newPath.add(path.get(i));
        }
        for (int i = q + 1; i < path.size(); i++) {
            newPath.add(path.get(i));
        }

        Route np = new Route(newPath, random);
        int qq = (q + 1) % path.size();
        np.pathLength = this.pathLength
                - (path.get(p - 1).distance(path.get(p))
                + path.get(q).distance(path.get(qq)))
                + (newPath.get(p - 1).distance(newPath.get(p))
                + newPath.get(q).distance(newPath.get(qq)));
        return np;
    }


    public double getPathLength() {
        return pathLength;
    }

    public int numCity() {
        return path.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(pathLength).append(" : [");
        path.stream().forEachOrdered(p
                -> sb.append("(").append(p.x).append(",").append(p.y).append(")")
        );
        sb.append("]");
        return sb.toString();
    }

    public List<Point> getPath() {
        return path;
    }

    public Point getMin() {
        return min;
    }

    public Point getMax() {
        return max;
    }

}
