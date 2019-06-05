package model;

/**
 * シミュレーションの途中結果を保存するクラス
 * @author tadaki
 */
public class Result {
    public final int t;//Monte Carlo step
    public final double d;//経路長
    public final double temp;//温度

    public Result(int t, double d, double temp) {
        this.t = t;
        this.d = d;
        this.temp = temp;
    }
    
}
