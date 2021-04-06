package exercise;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;
import java.util.StringJoiner;
import myLib.utils.FileIO;

/**
 * 簡単な有限温度Monte Carlo
 *
 * @author tadaki
 */
public class SimpleMC {

    private final int count[];//各状態の訪問数
    private final double energy[];//各状態のエネルギー
    private double temperature;//温度
    private final Random myRandom;
    private int current;//現在の状態
    private int numState;//状態総数

    /**
     * 温度を与えて初期化
     *
     * @param energy エネルギー順位
     * @param myRandom 乱数生成機
     */
    public SimpleMC(double[] energy, Random myRandom) {
        this.myRandom = myRandom;
        numState = energy.length;
        current = myRandom.nextInt(numState);
        this.energy = energy;
        count = new int[numState];
        count[current]++;
        temperature = Double.MAX_VALUE;
    }

    /**
     * 一時間ステップ
     */
    public void oneStep() {
        int s = current;
        while (s == current) {//遷移先の候補
            s = myRandom.nextInt(numState);
        }
        if (energy[current] <= energy[s]) {//遷移先のエネルギーが高い
            double de = energy[s] - energy[current];
            if (myRandom.nextDouble() < Math.exp(-de / temperature)) {
                current = s;
            }
        } else {
            current = s;
        }
        count[current]++;
    }

    /**
     * 各状態の訪問数
     *
     * @return
     */
    public int[] getCount() {
        return count;
    }

    /**
     * 各状態の相対頻度
     *
     * @return
     */
    public double[] evalFreq() {
        int sum = 0;
        for (int c : count) {
            sum += c;
        }
        double f[] = new double[numState];
        for (int i = 0; i < numState; i++) {
            f[i] = (double) count[i] / sum;
        }
        return f;
    }

    /**
     * 各状態の出現確率の理論値
     *
     * @return
     */
    public double[] expectation() {
        double sum = 0.;
        double f[] = new double[numState];
        for (int i = 0; i < numState; i++) {
            f[i] = Math.exp(-energy[i] / temperature);
            sum += f[i];
        }
        for (int i = 0; i < numState; i++) {
            f[i] /= sum;
        }
        return f;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
        current = 0;
    }

    /**
     * 配列を文字列化
     *
     * @param d
     * @return
     */
    public static String a2s(double d[]) {
        StringJoiner sj = new StringJoiner(",", "[", "]");
        for (double x : d) {
            sj.add(String.valueOf(x));
        }
        return sj.toString();
    }

    /**
     * 配列を文字列化
     *
     * @param d
     * @return
     */
    public static String a2ss(double d[]) {
        StringJoiner sj = new StringJoiner(" ");
        for (double x : d) {
            sj.add(String.valueOf(x));
        }
        return sj.toString();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        double temperature = 10.;
        int tmax = 50000;
        int dt = 100;
        Random myRandom = new Random(48L);
        //エネルギー順位
        double energy[] = {0., 1., 2., 4.};
        
        SimpleMC simpleMC = new SimpleMC(energy, myRandom);
        simpleMC.setTemperature(temperature);
        
        try ( BufferedWriter out = FileIO.openWriter("SimpleMC.txt")) {
            for (int t = 0; t < tmax; t++) {
                simpleMC.oneStep();
                if (t % dt == 0) {//100ステップ毎に出力
                    FileIO.writeSSV(out, t, SimpleMC.a2ss(simpleMC.evalFreq()));
                }
            }
        }
        System.out.println("シミュレーション結果：" + SimpleMC.a2s(simpleMC.evalFreq()));
        System.out.println("理論値　　　　　　　：" + SimpleMC.a2s(simpleMC.expectation()));
    }

}
