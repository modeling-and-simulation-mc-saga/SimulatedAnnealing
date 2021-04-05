package model;

import java.awt.Point;
import java.util.List;
import java.util.Random;
import myLib.utils.Utils;

/**
 * 一つの巡回路を表現するクラス
 *
 * @author tadaki
 */
public class Route {

    private final List<Point> path;//頂点列
    private double pathLength;//経路長
    //点の存在する範囲
    private Point min;
    private Point max;
    private final Random random;

    /**
     * コンストラクタ
     *
     * @param path 初期の経路
     */
    public Route(List<Point> path, Random random) {
        this.path = path;
        calcArea();
        this.random = random;
    }

    /**
     * 点の存在する範囲を調べる
     */
    private void calcArea() {
        min = new Point(path.get(0));
        max = new Point(path.get(0));
        for (Point p : path) {
            min.x = Math.min(min.x, p.x);
            min.y = Math.min(min.y, p.y);
            max.x = Math.max(max.x, p.x);
            max.y = Math.max(max.y, p.y);
        }
    }

    /**
     * 経路長を計算する
     *
     * @return 経路長
     */
    public double calcPathLength() {
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
     * MC法に基づき提案する新しい経路
     *
     * @return 新経路
     */
    public Route nextRoute() {
        int n = path.size();
        int pp = 0;
        int qq = 0;
        while (pp == qq) {
            pp = random.nextInt(n-1);
            qq = random.nextInt(n-1);
        }
        int p = 1 + Math.min(pp, qq);
        int q = 1 + Math.max(pp, qq);
        return nextRouteSub(p, q);
    }

    /**
     * 経由点(p,q)を指定して、新経路を生成
     *
     * @param p
     * @param q
     * @return
     */
    private Route nextRouteSub(int p, int q) {
        List<Point> newPath = Utils.createList();
        for (int i = 0; i < p; i++) {
            newPath.add(path.get(i));
        }
        for (int i = q; i >= p; i--) {
            newPath.add(path.get(i));
        }
        for (int i = q + 1; i < path.size(); i++) {
            newPath.add(path.get(i));
        }

        Route np = new Route(newPath,random);
        int qq = (q + 1) % path.size();
        np.pathLength = this.pathLength
                - (path.get(p - 1).distance(path.get(p))
                + path.get(q).distance(path.get(qq)))
                + (newPath.get(p - 1).distance(newPath.get(p))
                + newPath.get(q).distance(newPath.get(qq)));
        return np;
    }

    /**
     * 経路長を返す
     *
     * @return
     */
    public double getPathLength() {
        return pathLength;
    }

    /**
     * 頂点数を返す
     *
     * @return
     */
    public int numCity() {
        return path.size();
    }

    /**
     * 経路を文字列表現する
     *
     * @return
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(pathLength).append(" : [");
        path.stream().forEachOrdered(p
                -> sb.append("(").append(p.x).append(",").append(p.y).append(")")
        );
        sb.append("]");
        return sb.toString();
    }

    /**
     * 頂点リストを返す
     *
     * @return
     */
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
