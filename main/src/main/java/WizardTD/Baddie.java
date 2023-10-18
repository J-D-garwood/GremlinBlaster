package WizardTD;

import java.util.List;
import java.util.Random;

import processing.core.PImage;

public class Baddie {
    public int x;
    public int y;

    public int health;
    public int max_health;
    public int Speed;
    public float Armor;
    public int Mana_gain;
    private Random rand = new Random();
    private int start = rand.nextInt(2);

    public int xVel = 0;
    public int yVel = 0;

    public boolean isAtWiz = false;
    private int velMult;

    public int die_count = 8;

    private Integer[] wizard_HQ;

    private PImage BaddieSprite;

    private PImage D1;
    private PImage D2;
    private PImage D3;
    private PImage D4;
    private PImage D5;


    private List<Integer[]> corners;


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

    public void down() {
        this.xVel = 0;
        this.yVel = 1;
    }

    public void up() {
        this.xVel = 0;
        this.yVel = -1;
    }

    public void right() {
        if (this.xVel==-1) {
            return;
        }
        this.xVel = 1;
        this.yVel = 0;
    }

    public void left() {
        if (this.xVel==1) {
            return;
        }
        this.xVel = -1;
        this.yVel = 0;
    }


    public void draw(App app) {
        if (this.health>0){
            app.image(BaddieSprite, this.x, this.y);
            if (this.health<this.max_health) {
                /*Current_mana_f = (float) Current_mana;
                man_w = (Current_mana / mana_max_f*270);
                Mana_width = man_w.intValue();*/
                app.fill(255, 0, 0);
                app.rect(this.x, this.y-8,20, 5);
                app.fill(124, 252, 0);
                /*app.fill(255,0,0);
                app.rect(this.x+(this.health/this.max_health)*20, this.y-8, 20-(this.health/this.max_health)*20, 5);*/
            }
            this.x += xVel*Speed;
            this.y += yVel*Speed;
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
            this.DIE(app);
        }

    }

    public void ouch(int damage) {
        this.health -= damage;
    }
    ///I have intensionally slowed down the death of the gremlin for artistic reasons (I think it looks better slowed down)
    public void DIE(App app) {
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
            this.x = -100;
            this.y = -100;
            return;
        }
    }

}
