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

    public float fireball_time;
    public int frame_count;

    public int lvl = 0;
    public int range_lvl = 0;
    public int damage_lvl = 0;
    public int speed_lvl = 0;

    public boolean selected = true;

    public Fireballs fireballs;
    public Fireball FB;
    /*private int width = 20;
    private int height = 20;*/

    //private boolean firingNow;

    public Tower(App app, int x, int y, int range, int damage, float speed, int FPS) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.range = range;
        this.speed = speed;
        this.fireballs = new Fireballs(app);
        this.fireball_time = FPS / speed;
    }

    // may need to change "void" to "int" in below method...
    public Fireball draw(App app,  PImage towerSprite) {
        app.image(towerSprite, x, y);
        this.frame_count += 1;
        if (this.fireballs.allFireballs.size()>0) {
            for (int fireb=0; fireb<this.fireballs.allFireballs.size(); fireb++) {
                this.fireballs.allFireballs.get(fireb).draw(app);
                if (this.fireballs.allFireballs.get(fireb).destination_reached) {
                    this.FB = this.fireballs.allFireballs.get(fireb);
                    this.fireballs.allFireballs.remove(fireb);
                    return this.FB;
                }
            }
        }
        return null;
    }


    public void add_FIRE(App app, PImage sprite, double end_x, double end_y) {
        if (frame_count%fireball_time==0) {
            FB = new Fireball(app, sprite, this.x+16, this.y+16, end_x, end_y);
            this.fireballs.AddFireball(FB);
        }
    }

    public void upgrade_range() {
        this.range += 32;
        this.range_lvl += 1;
    }

    public void upgrade_damage(int damage_inc) {
        this.damage += damage_inc;
        this.damage_lvl += 1;
    }

    public void upgrade_speed(int FPS) {
        this.speed += 0.5;
        this.speed_lvl += 1;
        this.fireball_time = FPS / speed;
    }

    public void spawn(App app, int x, int y) {
    }

    public void setLevel(int lvl) {
        this.lvl = lvl;
    }
}
