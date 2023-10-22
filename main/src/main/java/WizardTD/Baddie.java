//importing packages
package WizardTD;

import java.util.List;
import java.util.Random;

import processing.core.PImage;

// class baddie represents gremlins
public class Baddie {
    // x and y integers represent gremlin location
    public int x;
    public int y;

    // baddie key attributes, current health, max health, speed, etc.
    public int health;
    public int max_health;
    public int Speed;
    public float Armor;
    public int Mana_gain;
    private Random rand = new Random();
    private int start = rand.nextInt(2);

    //integers that represent baddie x, y movement vectors
    public int xVel = 0;
    public int yVel = 0;


    // boolean values, is baddie "dead", has baddie reached the wizard house
    public boolean isAtWiz = false;
    public boolean dead = false;

    //counter for baddie's death frames
    public int die_count = 8;

    //integer array of wizard HQ
    private Integer[] wizard_HQ;

    //Rendered Gremlin image container
    private PImage BaddieSprite;

    //Gremlin death frames
    private PImage D1;
    private PImage D2;
    private PImage D3;
    private PImage D4;
    private PImage D5;

    //list of map corners
    private List<Integer[]> corners;

    //class constructor, writes in above attributes, determines starting location and movement
    public Baddie(App app, PImage BaddieSprite, List<Integer[]> baddieEntrance, List<Integer[]> corners, Integer[] wizard_HQ, Integer HP, Integer Speed, Float Armor, Integer Mana_gain, PImage D1, PImage D2, PImage D3, PImage D4, PImage D5) {
        Integer[] beginning_coords = baddieEntrance.get(start);
        this.wizard_HQ = wizard_HQ;
        this.x = beginning_coords[0];
        this.y = beginning_coords[1];
        this.health = HP;
        this.max_health = HP;
        this.Speed = Speed;
        this.Mana_gain = Mana_gain;
        this.Armor = Armor;
        this.D1 = D1;
        this.D2 = D2;
        this.D3 = D3;
        this.D4 = D4;
        this.D5 = D5;
        if (beginning_coords[2]==0) {
            this.down();
        } else {
            this.right();
        }
        this.BaddieSprite = BaddieSprite;
        this.corners = corners;
    }

    // method to set gremlin motion south
    public void down() {
        this.xVel = 0;
        this.yVel = 1;
    }

    // method to set gremlin motion north
    public void up() {
        this.xVel = 0;
        this.yVel = -1;
    }

    // method to set gremlin motion east
    public void right() {
        if (this.xVel==-1) {
            return;
        }
        this.xVel = 1;
        this.yVel = 0;
    }

    //method to set gremlin motion west
    public void left() {
        if (this.xVel==1) {
            return;
        }
        this.xVel = -1;
        this.yVel = 0;
    }

    //method to draw the baddie at its current coords (and healthbar if health lower than max)
    public int draw(App app) {
        if (this.health>0){
            app.image(BaddieSprite, this.x, this.y);
            if (this.health<this.max_health) {
                float Current_health_f = (float) this.health;
                Float h_w = (Current_health_f / (float)this.max_health)*20;
                int health_width = h_w.intValue();
                app.fill(255, 0, 0);
                app.rect(this.x, this.y-8,20, 5);
                app.fill(124, 252, 0);
                app.rect(this.x, this.y-8,health_width, 5);
            }
            //gremlin moving based on movement vectors (multiplied by speed scalar)
            this.x += xVel*Speed;
            this.y += yVel*Speed;
            //this next section checks if the gremlin has reached a corner, and adjusts its motion accordingly
            Integer[] current_corn;
            for (int i = 0; i<corners.size(); i++) {
            current_corn = corners.get(i);
            if (this.x==current_corn[0]&&this.y==current_corn[1]) {
                if (current_corn[2]==1) {
                    this.up();
                }
                if (current_corn[2]==2) {
                    this.right();
                }
                if (current_corn[2]==3) {
                    this.down();
                }
                if (current_corn[2]==4) {
                    this.left();
                }
            }
            }
            if (this.x==this.wizard_HQ[0]&&this.y==this.wizard_HQ[1]) {
                this.isAtWiz = true;
            }
        } else {
            // baddie dies if health is equal to or lower than 0
            return this.DIE(app);
        }
        return 0;

    }

    //death of gremlin, happens in total of 10 frames because I think it looks better than 4 frames.
    public int DIE(App app) {
        if (this.die_count>6) {
            app.image(D1, this.x, this.y);
            this.die_count--;
        } else if (this.die_count>4) {
            app.image(D2, this.x, this.y);
            this.die_count--;
        } else if (this.die_count>2) {
            app.image(D3, this.x, this.y);
            this.die_count--;
        } else if (this.die_count>0) {
            app.image(D4, this.x, this.y);
            this.die_count--;
        } else if (this.die_count>-2) {
            app.image(D5, this.x, this.y);
            this.die_count--;
        } else if (this.die_count==-2){
            this.x = -1000;
            this.y = -1000;
            this.dead = true;
            this.die_count--;
            return this.Mana_gain;
        }
        return 0;
    }

}
