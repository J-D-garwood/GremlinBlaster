package WizardTD;

import java.io.*;
import java.util.*;

public class Towers {
    ArrayList<Tower> allTowers = new ArrayList<>();

    public Towers(App app) {
    }

    public void AddTower(Tower tower) {
        allTowers.add(tower);
    }
}
