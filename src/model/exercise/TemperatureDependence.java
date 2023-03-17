package exercise;

import java.io.PrintStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author tadaki
 */
public class TemperatureDependence {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        double temperature = 1.;
        int tmax = 100000;
        List<String> outString = Collections.synchronizedList(new ArrayList<>());
        double energy[]={0.,1.,2.};
        SimpleMC simpleMC = new SimpleMC(energy,new Random(48L));
        while (temperature < 1025.) {
            simpleMC.setTemperature(temperature);
            for (int t = 0; t < tmax; t++) {
                simpleMC.oneStep();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(temperature).append(" ");
            sb.append(simpleMC.evalFreq());
            outString.add(sb.toString());
            temperature *= 2.;
        }
        String filename = TemperatureDependence.class.getSimpleName() + ".txt";
        try (PrintStream out = new PrintStream(filename)) {
            outString.forEach(s->out.println(s));
        }
    }

}
