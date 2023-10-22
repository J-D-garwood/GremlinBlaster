//importing packages
package WizardTD;
import java.io.*;
import java.util.*;
//Enemies class serves as an array list of all active enemies on the board
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
