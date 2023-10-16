package WizardTD;

import java.util.Random;

import jogamp.graph.geom.plane.AffineTransform;
import processing.core.PImage;

public class Tower {
    
    public int x;
    public int y;

    public  int range;
    public int damage;
    public float speed;

    public int lvl = 0;
    public int range_lvl = 0;
    public int damage_lvl = 0;
    public int speed_lvl = 0;

    public boolean selected = true;
    /*private int width = 20;
    private int height = 20;*/

    private boolean firingNow;

    public Tower(App app, int x, int y, int range, int damage, float speed) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.range = range;
        this.speed = speed;
    }

    // may need to change "void" to "int" in below method...
    public void draw(App app,  PImage towerSprite) {
        app.image(towerSprite, x, y);
    }

    public void FIRE() {
    }

    public void upgrade_range() {
        this.range += 32;
        this.range_lvl += 1;
    }

    public void upgrade_damage(int damage_inc) {
        this.damage += damage_inc;
        this.damage_lvl += 1;
    }

    public void upgrade_speed() {
        this.speed += 0.5;
        this.speed_lvl += 1;
    }

    public void spawn(App app, int x, int y) {
    }

    public void setLevel(int lvl) {
        this.lvl = lvl;
    }
}
