package WizardTD;

import java.util.Random;

import processing.core.PImage;

public class Tower {
    
    private int x;
    private int y;

    private int width = 20;
    private int height = 20;

    private boolean firingNow;

    private PImage towerSprite;

    public Tower(App app, PImage towerSprite, int x, int y) {
        this.towerSprite = towerSprite;
        this.x = x;
        this.y = y;
        //this.spawn(app, x, y);
    }

    // may need to change "void" to "int" in below method...
    public void draw(App app) {
        app.image(towerSprite, x, y);
    }

    public void FIRE() {
    }

    public void upgrade(int range, int power, int speed) {
    }

    public void spawn(App app, int x, int y) {
    }
}
