package WizardTD;

import java.util.List;
import java.util.Random;

import processing.core.PImage;

public class Fireball {
    public float x;
    public float y;

    public double x_vel;
    public double y_vel;

    public double end_x;
    public double end_y;

    public boolean destination_reached;
    public int damage;

    public PImage sprite;

    public Fireball(App app, PImage sprite, float x, float y, double end_x, double end_y) {
        this.sprite = sprite;
        this.x = x;
        this.y = y;
        this.end_x = end_x;
        this.end_y = end_y;
        this.calc_vels();
    }

    public void draw(App app) {
        if (Math.abs(x-end_x)>2&&Math.abs(y-end_y)>2) {
            this.x += this.x_vel;
            this.y += this.y_vel;
            app.image(sprite, this.x, this.y);
        } else {
            destination_reached = true;
        }
    }

    public void calc_vels() {
        double a = this.end_x - this.x;
        double b = this.end_y - this.y;
        double c = Math.sqrt(25/(a*a+b*b));
        this.x_vel = a*c;
        this.y_vel = b*c;
    }
}
