package WizardTD;
//an array list of all enemies on the board??
import java.io.*;
import java.util.*;

public class Enemies {
    ArrayList<Baddie> allBaddies = new ArrayList<>();
    List<Integer[]> entries;

    public Enemies(App app) {
    }

    public void AddEnemy(Baddie baddie) {
        allBaddies.add(baddie);
    }

    public void RemoveEnemy(int index) {
        allBaddies.remove(index);
    }
}
