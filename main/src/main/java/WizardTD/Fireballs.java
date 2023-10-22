//importing packages
package WizardTD;

import java.io.*;
import java.util.*;
//Fireballs class serves as an array list of all active fireballs for one tower

public class Fireballs {
    ArrayList<Fireball> allFireballs = new ArrayList<>();

    public Fireballs(App app) {
    }

    public void AddFireball(Fireball fireball) {
        allFireballs.add(fireball);
    }
}
