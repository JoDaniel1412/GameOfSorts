package client;

import game.draw.Drawer;
import game.entities.Dragon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import util.Math;

import java.util.List;

public class WaveGenerator {

    private static Logger logger = LoggerFactory.getLogger(WaveGenerator.class);
    private static final Marker SYS = MarkerFactory.getMarker("SYS");
    private static String[] waveSortMethod = {"selection", "insertion", "quick", "binary-tree", "avl-tree"};

    /**
     * Method that ask to the client for a new Wave
     * @param size of the wave required
     * @return the new Wave
     */
    public static Wave getNewWave(int size) {
        Wave wave = new Wave(0, size);
        unSortedWave(wave);
        return wave;
    }


    /**
     * Sends a Wave to the client to received sorted
     * @param wave the wave to sort
     * @param type last formation
     * @return the wave sorted and added to the GUI
     */
    public static Wave getWaveSorted(Wave wave, int type) {
        int i = type % waveSortMethod.length;
        String sort = waveSortMethod[i];
        wave.formation = sort;

        // Here the client ask for a sorted wave

        // Reposition the Dragons in formation
        if (i < 3) sort = "list";
        if (i == 3) sort = "avl-tree";
        if (i == 4) sort = "binary-tree";

        // Determines based in "sort" the formation of the wave
        listWave(wave);
        return wave;
    }

    /**
     * Gets a new Wave in disorder pattern
     * @param wave the wave to disorder
     */
    private static void unSortedWave(Wave wave){
        List<Dragon> dragons = wave.getDragonsList();
        int waveSize = wave.getSize();
        int columns = 8;
        int rows = waveSize / columns;
        double width = Drawer.width / 2;
        double height = Drawer.height;
        double xOffset = width / columns;
        double yOffset = height / (rows + 1);
        double xPoss = Drawer.width;
        double yMax = yOffset * rows;

        int n = 0;
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                double x = (i + 1) * xOffset;
                double y = Math.getRandomNumberInRange(0, (int) yMax);
                x += xPoss;
                generateDragon(x, y, dragons, n);
                n++;
            }
        }
    }

    /**
     * Makes an cubic like formation
     * @param wave the wave to order
     */
    private static void listWave(Wave wave) {
        List<Dragon> dragons = wave.getDragonsList();
        int waveSize = wave.getSize();
        int columns = (int) java.lang.Math.sqrt(waveSize);
        int rows = waveSize / columns;
        double width = Drawer.width / 2;
        double height = Drawer.height;
        double xOffset = width / columns;
        double yOffset = height / (rows + 1);
        double xPoss = getMinXPoss(dragons) - xOffset;
        int xi = 0;

        int n = 0;
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                double x = (i + 1) * xOffset;
                double y = (j + 1) * yOffset;
                Dragon dragon = dragons.get(n);
                x += xPoss;
                // Sets the position of the Dragon to animate
                dragon.setPoss(x, y);
                n++;
            }
            xi = i + 1;
        }

        // Adds the extra Dragons to the formation
        while(n < dragons.size()){
            columns += 1;
            for (int j = 0; j < rows; j++) {
                double y = (j + 1) * yOffset;
                double x = (xi + 1) * xOffset;
                Dragon dragon = dragons.get(n);
                dragon.setPoss(x, y);
            }
            n++;
        }

    }

    /**
     * Makes an tree like formation
     * @param wave the wave to order
     */
    private static void treeWave(Wave wave){

    }

    /**
     * Translates the Dragons received from the Server
     * @param x xPoss
     * @param y yPoss
     * @param list list of dragons
     * @param i position of the Dragon in the Wave
     */
    private static void generateDragon(double x, double y, List<Dragon> list, int i) {
        Dragon dragon = list.get(i);
        int parentAge = dragon.getParentAge();
        int age = dragon.getAge();
        String rank = dragon.getRank();
        String name = dragon.getName();
        int lives = dragon.getLives();
        int fire_rate = dragon.getFire_rate();
        list.set(i, new Dragon(x, y, parentAge, age, rank, name, lives, fire_rate));
    }

    /**
     * Looks for the minimum xPosition to place the Dragons
     * @param dragons list of the wave
     * @return the minimum xPoss
     */
    private static double getMinXPoss(List<Dragon> dragons){
        double result = dragons.get(0).getxPoss();
        for (Dragon dragon : dragons){
            if (dragon.getxPoss() < result){
                result = dragon.getxPoss();
            }
        }
        return result;
    }
}