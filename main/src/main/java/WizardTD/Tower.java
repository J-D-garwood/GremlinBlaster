//importing packages
package WizardTD;

import java.util.ArrayList;
import java.util.Random;

import jogamp.graph.geom.plane.AffineTransform;
import processing.core.PImage;

//class Tower represents towers
public class Tower {
    
    // x and y integers represent tower location
    public int x;
    public int y;

    // key tower attributes
    public  int range;
    public int damage;
    public float speed;

    // primitives for calibrating fireball generation
    public float fireball_time;
    public int frame_count;

    //tower attribute levels and overall level (up to 2, beyond irrelevant)
    public int lvl = 0;
    public int range_lvl = 0;
    public int damage_lvl = 0;
    public int speed_lvl = 0;

    //boolean which indicates whether tower is selected
    public boolean selected = true;

    //Fireballs class holds all this towers currently active fireballs
    public Fireballs fireballs;
    //Fireball class
    public Fireball FB;

    //Tower constructor, writes in key attributes
    public Tower(App app, int x, int y, int range, int damage, float speed, int FPS) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.range = range;
        this.speed = speed;
        this.fireballs = new Fireballs(app);
        this.fireball_time = FPS / speed;
    }

    // drawing tower and its fireballs 
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
        if (this.fireballs.allFireballs.size()>2) {
            this.fireballs.allFireballs = new ArrayList<Fireball>();
        }
        return null;
    }

//adding a new fireball to towers active fireballs, if and only if, enough frames have been rendered sinced the last.
    public void add_FIRE(App app, PImage sprite, double end_x, double end_y) {
        if (frame_count%fireball_time==0) {
            FB = new Fireball(app, sprite, this.x+16, this.y+16, end_x, end_y);
            this.fireballs.AddFireball(FB);
        }
    }

    // upgrading tower range , there is a range cap of 400 pixels
    public void upgrade_range() {
        if (range>400) {
            return;
        }
        this.range += 32;
        this.range_lvl += 1;
    }

    //upgrading tower damage
    public void upgrade_damage(int damage_inc) {
        this.damage += damage_inc;
        this.damage_lvl += 1;
    }
    // upgrading tower speed, there is a speed cap of level 3 (errors and lag develops if allowed to go faster)
    public void upgrade_speed(int FPS) {
        if (speed_lvl==3) {
            return;
        }
        this.speed += 0.5;
        this.speed_lvl += 1;
        this.fireball_time = FPS / speed;
    }

    //method to set overall level of tower
    public void setLevel(int lvl) {
        this.lvl = lvl;
    }
}
