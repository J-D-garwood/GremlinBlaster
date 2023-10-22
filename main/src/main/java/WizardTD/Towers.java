package WizardTD;

import java.io.*;
import java.util.*;
//Towers class serves as an array list of all active enemies on the board
public class Towers {
    ArrayList<Tower> allTowers = new ArrayList<>();

    public Towers(App app) {
    }

    public void AddTower(Tower tower) {
        allTowers.add(tower);
    }
}
