package exercise;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;
import java.util.StringJoiner;

/**
 * Simple Monte Carlo simulation at finite temperature 簡単な有限温度Monte Carlo
 *
 * @author tadaki
 */
public class SimpleMC {

    private final int count[];//the number of visits for each state
    private final double energy[];//the energy for each state
    private double temperature;//temperature
    private final Random myRandom;
    private int current;//current state
    private int numState;//the number of states

    /**
     * Initialize by specifying temperature
     *
     * @param energy energy state
     * @param myRandom
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


    public void oneStep() {
        int s = current;
        while (s == current) {//candidate for destination
            s = myRandom.nextInt(numState);
        }
        if (energy[current] <= energy[s]) {//destination has higher energy
            double de = energy[s] - energy[current];
            if (myRandom.nextDouble() < Math.exp(-de / temperature)) {
                current = s;
            }
        } else {
            current = s;
        }
        count[current]++;
    }

    public int[] getCount() {
        return count;
    }

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
     * theoretical values for frequencies
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
     * Converting array to string
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
     * Converting array to string
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
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        double temperature = 1.;
        int tmax = 50000;
        int dt = 100;
        Random myRandom = new Random(48L);
        //エネルギー順位
        double energy[] = {0., 1., 2., 4.};

        SimpleMC simpleMC = new SimpleMC(energy, myRandom);
        simpleMC.setTemperature(temperature);
        String filename = "SimpleMC-" + String.valueOf((int) temperature) + ".txt";
        try ( PrintStream out = new PrintStream(filename)) {
            for (int t = 0; t < tmax; t++) {
                simpleMC.oneStep();
                if (t % dt == 0) {//output at every dt step
                    out.println(t + " " + SimpleMC.a2ss(simpleMC.evalFreq()));
                }
            }
        }
        System.out.println("Simulation:" + SimpleMC.a2s(simpleMC.evalFreq()));
        System.out.println("Theory    :" + SimpleMC.a2s(simpleMC.expectation()));
    }

}
