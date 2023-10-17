package WizardTD;

import java.util.List;
import java.util.Random;

import processing.core.PImage;

public class Baddie {
    public int x;
    public int y;

    public int health;
    public int Speed;
    public float Armor;
    public int Mana_gain;
    private Random rand = new Random();
    private int start = rand.nextInt(2);

    public int xVel = 0;
    public int yVel = 0;

    public boolean isAtWiz = false;
    private int velMult;

    private Integer[] wizard_HQ;

    private PImage BaddieSprite;


    private List<Integer[]> corners;


    public Baddie(App app, PImage BaddieSprite, List<Integer[]> baddieEntrance, List<Integer[]> corners, Integer[] wizard_HQ, Integer HP, Integer Speed, Float Armor, Integer Mana_gain) {
        Integer[] beginning_coords = baddieEntrance.get(start);
        this.wizard_HQ = wizard_HQ;
        this.x = beginning_coords[0];
        this.y = beginning_coords[1];
        this.health = HP;
        this.Speed = Speed;
        this.Mana_gain = Mana_gain;
        this.Armor = Armor;
        if (beginning_coords[2]==0) {
            this.down();
        } else {
            this.right();
        }
        /*this.x = x;
        this.y = y;
        this.xVel = xVel;
        this.yVel = yVel;*/
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
        app.image(BaddieSprite, this.x, this.y);
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
    }

    public void ouch(int damage) {
        this.health -= damage;
    }

    public void DIE() {

    }

}
