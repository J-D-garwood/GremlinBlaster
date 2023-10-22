//importing packages
package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;
import java.lang.Math;

import javafx.scene.control.Cell;

//class app extends java class PApplet for building gui applications
public class App extends PApplet {

    //defining key display attributes, cell, size, sidebar width, etc.
    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;

    //setting length and width of display
    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;

    //setting frames per second rendered
    public static final int FPS = 60;

    //declaring String for holding config path
    public String configPath;

    //declaring key variables for holding info from the config file 
    JSONObject config;
    String level;
    File layout;
    Scanner scan;
    JSONArray waves;
    JSONObject wave_1_data;
    JSONArray wave_1_monsters;
    JSONObject wave_2_data;
    JSONArray wave_2_monsters;
    JSONObject wave_3_data;
    JSONArray wave_3_monsters;

    //frame counter for calibrating gremlin rendering
    float monster_frame_count = 0;

    //array lists for holding (x,y) coords of different types of tiles and key locations on board.
    List<Integer[]> baddieEntrance = new ArrayList<>();
    List<Integer[]> corners = new ArrayList<>();
    List<Integer[]> grass_tiles = new ArrayList<>();
    List<Integer[]> tower_tiles = new ArrayList<>();


    //declaring key boolean values for classic mode
    int wave_enemy_count = 0;
    boolean isWave1 = true;
    boolean isWave2 = true;
    boolean isWave3 = true;
    boolean youWon = true;
    boolean youLost = false;

    //integer counter, incremented each mana pool spell cast
    int mana_pool_count = 0; 

    //config file extractions
    Integer base_tower_range;
    Float base_tower_speed;
    Integer base_tower_damage;
    Integer base_mana;
    Integer mana_max;
    Integer mana_max_init;
    Float mana_max_f;
    Integer mana_regen;
    Integer tower_cost;
    Integer mana_pool_base_cost;
    Integer mana_pool_current_cost;
    Integer mana_pool_inc_cost;
    Float mana_pool_spell_mult;
    Float mana_pool_spell_gain_mult;


    //Displayed Info variables
    Integer Wave_num = 1;
    Integer Countdown = FPS/2;
    Integer Current_mana;
    Float Current_mana_f;
    Float man_w;
    int Mana_width;

    //boolean values for the 3 game states, menu (wait) screen, endless and classic mode.
    boolean wait_screen = true;
    boolean endless = false;
    boolean classic = false;

    //buttons, active/inactive boolean values
    boolean twotimes = false;
    boolean pause = false;
    boolean build_twr = false;
    boolean upgrade_range = false;
    boolean upgrade_speed = false;
    boolean upgrade_damage = false;
    boolean manapool = false;
    boolean manapool_active = false;

    //mana pool speel calibration and mana multiplier
    int manapool_activation_counter = 0;
    float man_mult = 1;

    //strings to be drawn on towers to indicate range and damage levels
    String dam = "0";
    String ran = "x";

    //coord ints for use in pinpointing mouseclick locations
    int min_x;
    int max_x;
    int min_y;
    int max_y;


    //key wizard house variables
    PImage wiz_house;
    PImage doorMat;
    Integer[] wizard_HQ = new Integer[2];
    int x_wiz = 0;
    int y_wiz = 0;
    int x_d_wiz = 0;
    int y_d_wiz = 0;


    //PImage variables
    PImage grass;
    PImage shrub;
    PImage Wiz_house;
    PImage LR_path;
    PImage UD_path;
    PImage LD_path;
    PImage LU_path;
    PImage RU_path;
    PImage RD_path;
    PImage LDR_path;
    PImage LUR_path;
    PImage ULD_path;
    PImage URD_path;
    PImage all_path;
    PImage gremlin;
    PImage gremlin_death_part1;
    PImage gremlin_death_part2;
    PImage gremlin_death_part3;
    PImage gremlin_death_part4;
    PImage gremlin_death_part5;
    PImage tower_0;
    PImage tower_1;
    PImage tower_2;
    PImage tower_hold;
    PImage fireball;
    PImage menu_background;

    //Key class containers, used throughout
	int villain_test_count = 0;
    Baddie villain;
    Tower tower;
    Fireball FB;
    Baddie Current_enemy;

    //Enemies and Towers class construction -> essentially contains lists of all active gremlins and towers.
    Enemies enemies = new Enemies(this);
    boolean all_dead = true;
    Towers towers = new Towers(this);
    Fireballs fireballs = new Fireballs(this);
    int button_y;
    boolean tower_here = false;

    //endless mode key variables
    Integer endless_durr = 100;
    Integer endless_hp = 1;
    Integer endless_speed = 1;
    Integer endless_mana_gain = 1;
    Float endless_armor = 1f;

    //button (sidebars) y locations, (x's constant)
    Integer[] button_ys = {105, 165, 225, 285, 345, 405, 465};//new Integer[7];

    //deactivate manapool method
    public void deactivate_manaPool() {
        manapool_active = false;
        mana_max_f = mana_max_init*1f;
        man_mult = 1;
        manapool = false;
    }

    //activate manapool method
    public void activate_manaPool() {
        if (mana_pool_current_cost>Current_mana) {
            return;
        }
        Current_mana -= mana_pool_current_cost;
        manapool_active = true;
        manapool = false;
        this.rect(650, 465, 50, 50);
        mana_pool_current_cost = mana_pool_base_cost + mana_pool_count*mana_pool_inc_cost;
        if ((mana_max_f * (mana_pool_spell_gain_mult + mana_pool_count*0.1f))>=mana_max_f*mana_pool_spell_mult) {
            mana_max_f = mana_max_f*mana_pool_spell_mult;
            man_mult = mana_pool_spell_mult;
        } else {
            mana_max_f = mana_max_f * (mana_pool_spell_gain_mult + mana_pool_count*0.1f);
            man_mult = (mana_pool_spell_gain_mult + mana_pool_count*0.1f);
        }
        mana_pool_count +=1;
    }


    //restart function, returns all key vars to base values
    public void restart() {
        endless_durr = 100;
        endless_hp = 1;
        endless_speed = 1;
        endless_mana_gain = 1;
        endless_armor = 1f;


        mana_pool_count = 0;
        Current_mana = base_mana;
        monster_frame_count = 0;
        wave_enemy_count = 0;
        isWave1 = true;
        isWave2 = true;
        isWave3 = true;
        youWon = true;
        Wave_num = 1;
        Countdown = FPS/2;
        man_mult = 1;

        twotimes = false;
        pause = false;
        build_twr = false;
        upgrade_range = false;
        upgrade_speed = false;
        upgrade_damage = false;
        manapool = false;
        manapool_active = false;

        enemies = new Enemies(this);
        all_dead = true;
        towers = new Towers(this);
        fireballs = new Fireballs(this);

    }

    //determines if user has paused game, ends draw loop if so (METHOD)
    public void pause_check() {
        if (pause) {
            build_twr = false;
            this.fill(255,255,0);
            this.rect(650, 165, 50, 50);
            this.fill(0);
            this.textSize(30);
            this.text("P", 655,200);
            noLoop();
        } else {
            loop();
        }
    }

    //hover effects on sidebar buttons
    public void update_buttons_hover() {
        for (int b = 0; b<7; b++) {
            button_y = button_ys[b];
            if (mouseX>650&&mouseX<700&&mouseY>button_y&&mouseY<(button_y+50)) {
                this.fill(128,128,128);
                this.rect(650,button_y, 50, 50);
            }
        }
    }

    //wizard tower rotation method
    public PImage rotateWizTower(PImage orig, String direct) {
        if (direct.equals("N")) {
            return rotateImageByDegrees(orig, 270);
        } else if (direct.equals("E")) {
            return orig;
        } else if (direct.equals("S")) {
            return rotateImageByDegrees(orig, 90);
        } else  {
            return rotateImageByDegrees(orig, 180);
        }
    }

    //losing method - called when mana = 0
    public void youreALoser() {
        for (int tower_un=0; tower_un<towers.allTowers.size(); tower_un++) {
            towers.allTowers.get(tower_un).selected = false;
        }
        this.fill(255,0,0);        
        this.textSize(100);
        this.text("YOU LOST!", 100,300);
        this.textSize(30);
        this.text("Press 'r' to restart..", 150,370);
        this.text("Press 'h' to return home",150,420);

    }

    //winning method - called when user has survived all 3 waves
    public void youreAWinner() {
        for (int tower_un=0; tower_un<towers.allTowers.size(); tower_un++) {
            towers.allTowers.get(tower_un).selected = false;
        }
        this.fill(255,255,0);        
        this.textSize(100);
        this.text("YOU WON!", 100,300);
        this.textSize(30);
        this.text("Press 'r' to restart..", 150,370);
        this.text("Press 'h' to return home",150,420);
    }

    //method checks all enemies in wave dead
    public boolean excessEnemyClearing() {
        for (int x = 0; x<enemies.allBaddies.size();x++) {
            if (!enemies.allBaddies.get(x).dead) {
                return false;
            }
        }
        return true;
    }

    //this method renders tower - gremlin interaction.
    public void firingAtMonsters() {
        for (int tower = 0; tower<towers.allTowers.size(); tower++) {
            Tower Current_tower = towers.allTowers.get(tower);
            for (int enemy = 0; enemy<enemies.allBaddies.size();enemy++) {
                Current_enemy = enemies.allBaddies.get(enemy);
                double a = (Current_enemy.x+16)-(Current_tower.x+16);
                double b = (Current_enemy.y+16)-(Current_tower.y+16);
                double c = Math.sqrt(a*a+b*b);
                if (c<Current_tower.range) {
                    double end_x = Current_enemy.x+16; 
                    double end_y = Current_enemy.y+16; 
                    if (Current_tower.fireballs.allFireballs.size()==0) {
                        Current_tower.add_FIRE(this, fireball, end_x, end_y);
                        if (Current_tower.fireballs.allFireballs.size()==1) {
                            Current_enemy.health -= Current_tower.damage*Current_enemy.Armor;
                            enemies.allBaddies.set(enemy, Current_enemy);
                        } 
                    }
                    towers.allTowers.set(tower, Current_tower);
                }
            }
        }
    }

    //simple method to concantenate a string to itself a given number of times
    public String repeater(String str, int times) {
        String multiplied = str;
        for (int i = 1; i<times; i++) {
            multiplied = multiplied + str;
        }
        if (times==0) {
            return "";
        } else {
            return multiplied;
        }
    }

    //sub method to towerAdditions
    public void towerAdditions_sub(Tower tower, int lvl) {
        this.fill(128,0,128);
        this.textSize(12);
        this.text(repeater(dam, tower.damage_lvl-lvl), tower.x, tower.y+32);
        this.text(repeater(ran, tower.range_lvl-lvl), tower.x, tower.y+7);
        if (tower.speed_lvl-lvl>0) {
            this.stroke(0,255,255);
            this.strokeWeight(((tower.speed_lvl-lvl)*1)+1);
            this.line(tower.x+6, tower.y+6, tower.x+26, tower.y+6);
            this.line(tower.x+6, tower.y+26, tower.x+26, tower.y+26);
            this.line(tower.x+6, tower.y+6, tower.x+6, tower.y+26);
            this.line(tower.x+26, tower.y+6, tower.x+26, tower.y+26);
        }
    }

//method to draw any tower additions (range circle when selected, level indicators, etc.)
    public void towerAdditions(Tower tower, int tow) {
        if (tower.selected) {
            noFill();
            this.strokeWeight(3);
            this.stroke(255, 255, 0);
            this.arc(tower.x*1f+16, tower.y*1f+16, tower.range*2f, tower.range*2f, 0, TWO_PI); 
        }
        if (tower.lvl==0) {
            towerAdditions_sub(tower, 0);
        } else if (tower.lvl==1) {
            towerAdditions_sub(tower, 1);
        } else {
            towerAdditions_sub(tower, 2);
        }
        this.strokeWeight(1);
        this.stroke(0);
        fill(250,250,250);
    }

    //method to draw all towers
    public void drawTowers() {
        if (towers.allTowers.size()>0) {
            for (int tow = 0; tow<towers.allTowers.size(); tow++) {
                tower = towers.allTowers.get(tow);
                if (tower.damage_lvl>=2&&tower.speed_lvl>=2&&tower.range_lvl>=2) {
                    FB = tower.draw(this, tower_2);
                    tower.setLevel(2);
                } else if (tower.damage_lvl>=1&&tower.speed_lvl>=1&&tower.range_lvl>=1) {
                    FB = tower.draw(this, tower_1);
                    tower.setLevel(1);
                } else {
                    FB = tower.draw(this, tower_0);
                    tower.setLevel(0);
                }
                towerAdditions(tower, tow); 
                if (FB!=null) {
                    if (FB.destination_reached) {
                        for (int M = 0; M<enemies.allBaddies.size(); M++) {
                            // here is where we add damage to enemies.
                        }
                    }
                }
            }

        }
    }

    //endless mode method
    public void endless() {
        Countdown=0;
        if (Current_mana<=0) {
                youLost = true;
                youreALoser();
                return;
        } 
        monster_frame_count+=1;
        if ((monster_frame_count%endless_durr)==0.0) {
                    villain = new Baddie(this, gremlin, baddieEntrance, corners, wizard_HQ, endless_hp, endless_speed, endless_armor, endless_mana_gain, gremlin_death_part1, gremlin_death_part2, gremlin_death_part3, gremlin_death_part4, gremlin_death_part5);
                    enemies.AddEnemy(villain); 
                    endless_hp+=5;
                    if (endless_mana_gain<25) {
                        endless_mana_gain+=1;
                    }
                    if (endless_durr>30) {
                         endless_durr-=1;
                    }
        }
        for (int vil_c=enemies.allBaddies.size()-1; vil_c>-1; vil_c--) {
            int mana_gain = enemies.allBaddies.get(vil_c).draw(this);
            if (mana_gain<(mana_max_f.intValue()-Current_mana)) {
                Current_mana = (int) (Current_mana + mana_gain*man_mult);
            } else {
                Current_mana = (mana_max_f.intValue());
            }
            if (enemies.allBaddies.get(vil_c).isAtWiz) {
                int HP = enemies.allBaddies.get(vil_c).health;
                Current_mana -= HP;
                enemies.RemoveEnemy(vil_c);
            }
            if (enemies.allBaddies.get(vil_c).x==-1000) {
                enemies.RemoveEnemy(vil_c);
            }
        }
        }


    //classic mode method
    public boolean movingStuff(boolean thisWave, JSONArray wave_monsters, JSONObject furtherWaveData, JSONObject nextWaveData) {
        if (Current_mana<=0) {
                youLost = true;
                youreALoser();
        }
        if (Countdown>0) {
            this.fill(0);
            this.textSize(CELLSIZE);
            this.text("starts: "+Countdown/FPS,140, 35);
            Countdown -=1;
        } else {
            monster_frame_count+=1;
            if (wave_enemy_count==wave_monsters.getJSONObject(0).getInt("quantity")) {
                all_dead = excessEnemyClearing();
                if (all_dead) {
                    enemies.allBaddies.clear();
                }
            }
            if (wave_enemy_count==wave_monsters.getJSONObject(0).getInt("quantity")/**/&&enemies.allBaddies.size()==0) {
                if (nextWaveData!=null) {
                    Countdown = FPS*nextWaveData.getInt("pre_wave_pause");
                    Wave_num+=1;
                    wave_enemy_count = 0;
                    monster_frame_count = 0;
                    all_dead = true;
                }
                return false;
            }
            // I have altered the spawn rate so it is not exactly "correct" in how frequently it spawns gremlins, but it makes for nicer gameplay
            if ((monster_frame_count%((FPS*2)/furtherWaveData.getInt("duration")))==0.0) {
                if (wizard_HQ!=null) {
                    if (wave_enemy_count<wave_monsters.getJSONObject(0).getInt("quantity")) {
                        villain = new Baddie(this, gremlin, baddieEntrance, corners, wizard_HQ, wave_monsters.getJSONObject(0).getInt("hp"),wave_monsters.getJSONObject(0).getInt("speed"), wave_monsters.getJSONObject(0).getFloat("armour"), wave_monsters.getJSONObject(0).getInt("mana_gained_on_kill"), gremlin_death_part1, gremlin_death_part2, gremlin_death_part3, gremlin_death_part4, gremlin_death_part5);
                        enemies.AddEnemy(villain); 
                        wave_enemy_count+=1;
                    }    
                }
            }
            for (int vil_c=enemies.allBaddies.size()-1; vil_c>-1; vil_c--) {
                int mana_gain = enemies.allBaddies.get(vil_c).draw(this);
                if (mana_gain<(mana_max_f.intValue()-Current_mana)) {
                    Current_mana = (int) (Current_mana + mana_gain*man_mult);
                } else {
                    Current_mana = (mana_max_f.intValue());
                }
                if (enemies.allBaddies.get(vil_c).isAtWiz) {
                    int HP = enemies.allBaddies.get(vil_c).health;
                    Current_mana -= HP;
                    enemies.RemoveEnemy(vil_c);
                }
            }
        }
        return true;
    }

    //method to print map layout from textfile
    public void printmap() {
        
        String level = config.getString("layout");
        File layout = new File("C:\\Users\\garwo\\Semester 2, 2023\\OOP\\Assignment\\scaffold\\"+level);
        Scanner scan = null;
        try {
            scan = new Scanner(layout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String currentLine = "";
        String[] currentLineSplit;
        int line = 0;
        final String[][] map = new String[20][20];
        while (scan.hasNextLine()) {
            currentLine = scan.nextLine();
            currentLineSplit = currentLine.split("");
            for (int i=0; i<currentLine.length(); i++) {
                map[line][i] = currentLineSplit[i];
            }
            line+=1;
        }
    
        String[] previousLine = new String[20];
        String[] thisLine = new String[20];
        String[] nextLine = new String[20];

        PImage Tile = grass;
        String txt;

        String up = "";;
        String down = "";;
        String left = "";
        String right = "";

        int x_tile;
        int y_tile;

        //insert code to render map
        for (int j=0;j<20;j++) {
            if (j>0) {
                previousLine = thisLine;
            }
            if (j<19) {
                nextLine = map[j+1];
            }
            thisLine = map[j];
            for (int k=0; k<20; k++) { 
                txt = thisLine[k];
                x_tile = k*32;
                y_tile = 40+j*32;
                if (txt==null) {
                    if (grass_tiles.size()<400) {
                        Integer[] thisIsGrass = new Integer[2];
                        thisIsGrass[0] = x_tile;
                        thisIsGrass[1] = y_tile;
                        grass_tiles.add(thisIsGrass);
                    }
                    image(grass, x_tile, y_tile);
                    continue;
                }
                if(txt.equals("X")) {

                    up = previousLine[k];
                    down = nextLine[k];
                    if (k>0) {
                        left = thisLine[k-1];
                    }
                    if (k<19) {
                        right = thisLine[k+1];
                    }
                    if (k>0 && k<19 && j>0 && j<19) {
                        if (!up.equals("X")&&left.equals("X")&&!down.equals("X")&&right.equals("X")) { //two 
                            Tile = LR_path;// left to right
                        } else if (up.equals("X")&&left.equals("X")&&!down.equals("X")&&!right.equals("X")) {
                            Tile = LU_path;// left to up
                            if (corners.size()<10) {
                                Integer[] head_left = new Integer[3];
                                head_left[0] = x_tile;
                                head_left[1] = y_tile;
                                head_left[2] = 4;
                                corners.add(head_left);
                            }
                        } else if (!up.equals("X")&&left.equals("X")&&down.equals("X")&&!right.equals("X")){
                            Tile = LD_path;//; left to down
                            if (corners.size()<10) {
                                Integer[] head_down = new Integer[3];
                                head_down[0] = x_tile;
                                head_down[1] = y_tile;
                                head_down[2] = 3;
                                corners.add(head_down);
                            }
                        } else if (up.equals("X")&&!left.equals("X")&&!down.equals("X")&&right.equals("X")) {
                            Tile = RU_path;//Tile =; right to up
                            if (corners.size()<10) {
                                Integer[] turn_right = new Integer[3];
                                turn_right[0] = x_tile;
                                turn_right[1] = y_tile;
                                turn_right[2] = 3;
                                corners.add(turn_right);
                            }
                        } else if (!up.equals("X")&&!left.equals("X")&&down.equals("X")&&right.equals("X")) {
                            Tile = RD_path;// right to down
                            if (corners.size()<10) {
                                Integer[] head_down = new Integer[3];
                                head_down[0] = x_tile;
                                head_down[1] = y_tile;
                                head_down[2] = 3;
                                corners.add(head_down);
                            }
                        } else if (up.equals("X")&&!left.equals("X")&&down.equals("X")&&!right.equals("X")) {
                            Tile = UD_path;//up to down
                        } else if (!up.equals("X")&&left.equals("X")&&down.equals("X")&&right.equals("X")) { //three
                            Tile = LDR_path; //all except up
                        }  else if (up.equals("X")&&left.equals("X")&&!down.equals("X")&&right.equals("X")) { //three
                            Tile = LUR_path;//all except down
                            Integer[] turn_right = new Integer[3];
                            turn_right[0] = x_tile;
                            turn_right[1] = y_tile;
                            turn_right[2] = 2;
                            corners.add(turn_right);
                        } else if (up.equals("X")&&!left.equals("X")&&down.equals("X")&&right.equals("X")) { //three
                            Tile = URD_path;//all except left
                        } else if (up.equals("X")&&left.equals("X")&&down.equals("X")&&!right.equals("X")) { //three
                            Tile = ULD_path;//all except right
                        } else if (up.equals("X")&&left.equals("X")&&down.equals("X")&&right.equals("X")) { //three
                            Tile = all_path;//all except right
                        } else if((!up.equals("X")&&!left.equals("X")&&!down.equals("X")&&right.equals("X"))||(!up.equals("X")&&left.equals("X")&&!down.equals("X")&&!right.equals("X"))) { //one
                            Tile = LR_path;
                        } else if((up.equals("X")&&!left.equals("X")&&!down.equals("X")&&!right.equals("X"))||(!up.equals("X")&&!left.equals("X")&&down.equals("X")&&!right.equals("X"))) {
                            Tile = UD_path;
                        }
                    } else {
                        Integer[] start_xy = new Integer[3];
                        if ((k==0||k==19)) {
                            Tile = LR_path;
                            start_xy[2] = 1;
                        } else {
                            Tile = UD_path;
                            start_xy[2] = 0;
                        }
                        start_xy[0] = x_tile;
                        start_xy[1] = y_tile;
                        baddieEntrance.add(start_xy);

                    }
                } else if (txt.equals("S")) {
                    Tile = shrub;
                } else if (txt.equals(" ")) {
                    if (grass_tiles.size()<400) {
                        Integer[] thisIsGrass = new Integer[2];
                        thisIsGrass[0] = x_tile;
                        thisIsGrass[1] = y_tile;
                        grass_tiles.add(thisIsGrass);
                    }
                    Tile = grass;
                } else if (txt.equals("W")) {
                     up = previousLine[k];
                     down = nextLine[k];
                     left = thisLine[k-1];
                     right = thisLine[k+1];
                    if (up.equals("X")) {
                        Wiz_house = rotateWizTower(Wiz_house, "N");
                        doorMat = UD_path;
                        x_d_wiz = x_tile;
                        y_d_wiz = y_tile - 40;
                    } else if (right.equals("X")) {
                        Wiz_house = rotateWizTower(Wiz_house, "E");
                        doorMat = LR_path;
                        x_d_wiz = x_tile + 40;
                        y_d_wiz = y_tile;
                    } else if (right.equals("X")) {
                        Wiz_house = rotateWizTower(Wiz_house, "S");
                        doorMat = UD_path;
                        x_d_wiz = x_tile;
                        y_d_wiz = y_tile + 40;
                    } else {
                        Wiz_house = rotateWizTower(Wiz_house, "W");
                        doorMat = LR_path;
                        x_d_wiz = x_tile - 40;
                        y_d_wiz = y_tile;
                    }
                    y_wiz = y_tile - 4;
                    x_wiz = x_tile - 4;
                    wizard_HQ[0] = x_tile;
                    wizard_HQ[1] = y_tile;
            } else {
                    continue;
                }
                image(Tile, x_tile, y_tile);
            }
        }

    }

    //main app method
    public App() {
        this.configPath = "config.json";
    }

    //setting size of papplet window
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    //Loading all resources and initialising key variables from config file
	@Override
    public void setup() {
        frameRate(FPS);
        grass = loadImage("src/main/resources/WizardTD/grass.png");
        shrub = loadImage("src/main/resources/WizardTD/shrub.png");
        Wiz_house = loadImage("src/main/resources/WizardTD/wizard_house.png");
        LR_path = loadImage("src/main/resources/WizardTD/path0.png");
        UD_path = rotateImageByDegrees(LR_path, 90);
        LD_path = loadImage("src/main/resources/WizardTD/path1.png");
        LU_path = rotateImageByDegrees(LD_path, 90);
        RU_path = rotateImageByDegrees(LU_path, 90);
        RD_path = rotateImageByDegrees(RU_path, 90);
        LDR_path = loadImage("src/main/resources/WizardTD/path2.png");
        LUR_path = rotateImageByDegrees(LDR_path, 180);
        ULD_path = rotateImageByDegrees(LDR_path, 90);
        URD_path = rotateImageByDegrees(LDR_path, 270);
        all_path = loadImage("src/main/resources/WizardTD/path3.png");
        gremlin = loadImage("src/main/resources/WizardTD/gremlin.png");
        gremlin_death_part1 = loadImage("src/main/resources/WizardTD/gremlin1.png");
        gremlin_death_part2 = loadImage("src/main/resources/WizardTD/gremlin2.png");
        gremlin_death_part3 = loadImage("src/main/resources/WizardTD/gremlin3.png");
        gremlin_death_part4 = loadImage("src/main/resources/WizardTD/gremlin4.png");
        gremlin_death_part5 = loadImage("src/main/resources/WizardTD/gremlin5.png");
        tower_0 = loadImage("src/main/resources/WizardTD/tower0.png");
        tower_1 = loadImage("src/main/resources/WizardTD/tower1.png");
        tower_2 = loadImage("src/main/resources/WizardTD/tower2.png");
        fireball = loadImage("src/main/resources/WizardTD/fireball.png");
        menu_background = loadImage("src/main/resources/WizardTD/menu_background.jpg");


        //loading config.json
        config = loadJSONObject(this.configPath);
        level = config.getString("layout");
        layout = new File("C:\\Users\\garwo\\Semester 2, 2023\\OOP\\Assignment\\scaffold\\"+level);
        scan = null;
        waves = config.getJSONArray("waves");

        //Wave information
        wave_1_data = waves.getJSONObject(0);
        wave_1_monsters = wave_1_data.getJSONArray("monsters");
        wave_2_data = waves.getJSONObject(1);
        wave_2_monsters = wave_2_data.getJSONArray("monsters");
        wave_3_data = waves.getJSONObject(2);
        wave_3_monsters = wave_3_data.getJSONArray("monsters");

        base_tower_range = config.getInt("initial_tower_range");
        base_tower_speed = config.getFloat("initial_tower_firing_speed");
        base_tower_damage = config.getInt("initial_tower_damage");
        base_mana = config.getInt("initial_mana");
        mana_max = config.getInt("initial_mana_cap");
        mana_max_init = mana_max;
        mana_max_f = (float) mana_max;
        Current_mana = base_mana;
        mana_regen = config.getInt("initial_mana_gained_per_second");
        tower_cost = config.getInt("tower_cost");
        mana_pool_base_cost = config.getInt("mana_pool_spell_initial_cost");
        mana_pool_current_cost = mana_pool_base_cost;
        mana_pool_inc_cost = config.getInt("mana_pool_spell_cost_increase_per_use");
        mana_pool_spell_mult = config.getFloat("mana_pool_spell_cap_multiplier");
        mana_pool_spell_gain_mult = config.getFloat("mana_pool_spell_mana_gained_multiplier");
    }

    //key pressed method, handles fast forward, pause, build tower, upgrades, manapool, restart and home user commands via keyboard
	@Override
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        if (key == 70) {
            twotimes = !twotimes;
            return;
        }
        if (key == 80) {
            pause = !pause;
            pause_check();
            return;
        }
        if (key == 84) {
            build_twr = !build_twr;
            return;
        }
        if (key == 49) {
            upgrade_range = !upgrade_range;
            return;
        }
        if (key == 50) {
            upgrade_speed = !upgrade_speed;
            return;
        }
        if (key == 51) {
            upgrade_damage = !upgrade_damage;
            return;
        }
        if (key == 77) {
            if (!manapool_active&&Countdown==0) {
                manapool = true;
            }
            return;
        }
        if (key == 82 && ((!isWave1 && !isWave2 && !isWave3)||(youLost))) {
            restart();
            youLost = false;
            return;
        }
        if (key == 72 && ((!isWave1 && !isWave2 && !isWave3)||(youLost))) {
            wait_screen = true;
            classic = false;
            endless = false;
            restart();
        }
    }

    //mousePressed method, handles all mouse pressed events
    @Override
    public void mousePressed(MouseEvent e) {
        //menu mouse click handing
        if (wait_screen) {
            if (mouseX>300&&mouseX<460&&mouseY>220&&mouseY<260) {
                wait_screen = false;
                classic = true;
            }
            if (mouseX>300&&mouseX<460&&mouseY>270&&mouseY<310) {
                wait_screen = false;
                endless = true;
            }
            return;
        }
        //gameplay mouse click handling
        for (int b = 0; b<7; b++) {
            button_y = button_ys[b];
            if (mouseX>650&&mouseX<700&&mouseY>button_y&&mouseY<(button_y+50)) {
                switch (b) {
                    case 0:
                        twotimes = !twotimes;
                        break;
                    case 1:
                        pause = !pause;
                        pause_check();
                        break;
                        
                    case 2:
                        build_twr = !build_twr;
                        break;
                    case 3:
                        upgrade_range = !upgrade_range;
                        break;
                    case 4:
                        upgrade_speed = !upgrade_speed;
                        break;
                    case 5:
                        upgrade_damage = !upgrade_damage;
                        break;
                    case 6:
                        if (!manapool_active&&Countdown==0) {
                            manapool = true;
                        }
                        break;

                }
            }
        }
        //mouseclick builds a tower
        if (build_twr==true) {
            for (int tile=0; tile<grass_tiles.size();tile++) {
                min_x = grass_tiles.get(tile)[0];
                max_x = grass_tiles.get(tile)[0]+32;
                min_y = grass_tiles.get(tile)[1];
                max_y = grass_tiles.get(tile)[1]+32;
                if ((mouseX>min_x)&&(mouseX<max_x)&&(mouseY<max_y)&&(mouseY>min_y)) {
                    grass_tiles.remove(tile);
                    Integer[] towerHere = new Integer[2];
                    towerHere[0] = min_x;
                    towerHere[1] = min_y;
                    tower_tiles.add(towerHere);
                    if (!upgrade_damage&&!upgrade_range&&!upgrade_speed) {
                        if ((Current_mana - tower_cost)>0) {
                            Current_mana -= tower_cost;
                        } else {
                            return;
                        }
                    }
                    if (upgrade_damage&&upgrade_range&&upgrade_speed) {
                        if ((Current_mana - (tower_cost+60))>0) {
                            Current_mana -= tower_cost + 60;
                        } else {
                            return;
                        }
                    }
                    if ((upgrade_damage&&upgrade_range&&!upgrade_speed)||(upgrade_damage&&!upgrade_range&&upgrade_speed)||(!upgrade_damage&&upgrade_range&&upgrade_speed)) {
                        if ((Current_mana - (tower_cost+40))>0) {
                            Current_mana -= tower_cost + 40;
                        } else {
                            return;
                        }
                    }
                    if ((!upgrade_damage&&upgrade_range&&!upgrade_speed)||(upgrade_damage&&!upgrade_range&&!upgrade_speed)||(!upgrade_damage&&!upgrade_range&&upgrade_speed)) {
                        if ((Current_mana - (tower_cost+20))>0) {
                            Current_mana -= tower_cost + 20;
                        } else {
                            return;
                        }
                    }
                    tower = new Tower(this, min_x, min_y, base_tower_range, base_tower_damage, base_tower_speed, FPS);
                    if (upgrade_damage) {
                        tower.upgrade_damage(base_tower_damage/2);
                    }
                    if (upgrade_range) {
                        tower.upgrade_range();
                    }
                    if (upgrade_speed) {
                        tower.upgrade_speed(FPS);
                    }
                    if (towers.allTowers.size()>0) {
                        for (int t = 0; t<towers.allTowers.size();t++) {
                            towers.allTowers.get(t).selected = false;
                        }
                    }
                    towers.AddTower(tower);
                    build_twr = false;
                    upgrade_damage = false;
                    upgrade_range = false;
                    upgrade_speed = false;
                    break;              
                };
            }
        } else { //mouseclick selects and unselects towers
            for (int twr_tile=0;twr_tile<tower_tiles.size();twr_tile++) {
                min_x = tower_tiles.get(twr_tile)[0];
                max_x = tower_tiles.get(twr_tile)[0]+32;
                min_y = tower_tiles.get(twr_tile)[1];
                max_y = tower_tiles.get(twr_tile)[1]+32;
                if ((mouseX>min_x)&&(mouseX<max_x)&&(mouseY<max_y)&&(mouseY>min_y)) {
                    for (int twr=0; twr<towers.allTowers.size();twr++) { 
                        if (towers.allTowers.get(twr).x==min_x&&towers.allTowers.get(twr).y==min_y) {
                            towers.allTowers.get(twr).selected = true;
                            tower_here = true;
                            upgrade_damage = false;
                            upgrade_range = false;
                            upgrade_speed = false;
                        } else {
                             towers.allTowers.get(twr).selected = false;
                        }
                    }
                }
            }
            if (!tower_here&&mouseX<650&&mouseY>40) {
                for (int T=0; T<towers.allTowers.size();T++) { 
                        towers.allTowers.get(T).selected = false;
                }
            }
            tower_here = false;
        }
    }


    //overall drawing method, draws hold user interface
	@Override
    public void draw() {
        //draws menu initially
        if (wait_screen) {
            background(50,200,50);
            image(menu_background, 0, -200);
            this.fill(0,0,255);
            this.textSize(50);
            this.text("GREMLINBLASTER", 170, 200);
            this.rect(300, 220, 160, 40);
            this.rect(300, 270, 160, 40, 5, 5, 5, 5);
            this.fill(255,255,0);
            this.textSize(25);
            this.text("Classic", 335, 250);
            this.text("Endless", 332, 300);
            this.image(gremlin, 345, 135);
            this.image(gremlin,367, 135);
            this.image(gremlin,389, 135);
            return;
        }
        //draws gameplay, with assistance from previous methods
        background(165,135,16);
        textSize(CELLSIZE);
        printmap();
        image(doorMat, x_d_wiz, y_d_wiz);
        drawTowers();
        fill(165,135,16);
        stroke(165,135,16);
        this.rect(0, 0, 760, 40);
        this.rect(640, 0, 120, 680);
        stroke(0);
        //sets gameplay to 2x speed if button activated
        if (twotimes) {
            frameRate(FPS*2);
        } else {
            frameRate(FPS);
        }
        this.fill(0);
        this.textSize(CELLSIZE);
        if (classic) {
            // wave label
            this.text("wave: "+Wave_num,10, 35);
        } else {
            this.text("endless mode",10, 35);
        }
        this.fill(0);
        this.textSize(19);
        this.text("MANA: ", 410, 30);
        this.fill(0);
        //mana bar
        this.fill(255);
        this.rect( 480, 10, 270, 25);
        Current_mana_f = (float) Current_mana;
        man_w = (Current_mana / mana_max_f*270);
        Mana_width = man_w.intValue(); //Width of mana bar
        mana_max = mana_max_f.intValue();
        if (Current_mana>0) {
            this.fill(176,224,230);
            this.rect(480, 10, Mana_width, 25);
        }
        if (Current_mana>0) {
            this.fill(0);
            this.text(Current_mana + " / "+ mana_max, 550, 30);
        } else {
            this.fill(0);
            this.text("0 / "+ mana_max, 550, 30);
        }
        //rendering sidebar buttons
        this.fill(0);
        this.textSize(13);
        this.text("2 x", 703, 115);
        this.text("Speed", 703, 130);
        this.text("pause", 703, 180);
        this.text("build", 703, 240);
        this.text("tower", 703, 255);
        this.text("upgrade", 703, 300);
        this.text("range", 703, 315);
        this.text("upgrade", 703, 360);
        this.text("speed", 703, 375);
        this.text("upgrade", 703, 420);
        this.text("damage", 703, 435);
        this.text("Mana", 703, 480);
        this.text("Pool", 703, 495);
        this.text("Cost:"+mana_pool_current_cost, 703, 510);
        this.fill(165,135,16);
        this.rect(650, 105, 50, 50);
        this.rect(650, 165, 50, 50);
        this.rect(650, 225, 50, 50);
        this.rect(650, 285, 50, 50);
        this.rect(650, 345, 50, 50);
        this.rect(650, 405, 50, 50);
        this.rect(650, 465, 50, 50);
        this.fill(255,255,0);
        //rendering yellow rectanges within buttons if active
        if (twotimes) {
            this.rect(650,105, 50, 50);
        }
        if (pause) {
            this.rect(650, 165, 50, 50);
        }
        if (build_twr) {
            this.rect(650, 225, 50, 50);
        }
        if (upgrade_range) {
            this.rect(650, 285, 50, 50);
        }
        if (upgrade_speed) {
            this.rect(650, 345, 50, 50);
        }
        if (upgrade_damage) {
            this.rect(650, 405, 50, 50);
        }
        if (manapool) {
            activate_manaPool();
        }

        //mana pool lasts for 8 seconds
        if (manapool_active) {
             this.rect(650, 465, 50, 50);
            manapool_activation_counter += 1;
            if (manapool_activation_counter%(FPS*10)==0) {
                deactivate_manaPool();
            }
        }

        //adding hover effects to buttons
        update_buttons_hover();
        this.fill(0);
        this.textSize(30);
        this.text("FF", 655,140);
        this.text("P", 655,200);
        this.text("T", 655,260);
        this.text("U1", 655,320);
        this.text("U2", 655,380);
        this.text("U3", 655,440);
        this.text("M", 655,500);

        //displaying selected tower upgrade costs in bottom right corner + tooltips
        for (int t = 0; t<towers.allTowers.size(); t++) {
            if (towers.allTowers.get(t).selected) {
                int ran_cost = (20 + (towers.allTowers.get(t).range_lvl-1)*10);
                int spe_cost = (20 + (towers.allTowers.get(t).speed_lvl-1)*10);
                int dam_cost = (20 + (towers.allTowers.get(t).damage_lvl-1)*10);
                this.fill(240, 240, 240);
                this.rect(650, 575, 100, 25);
                this.rect(650, 600, 100, 25);
                this.rect(650, 625, 100, 25);
                this.rect(650, 650, 100, 25);
                fill(0);
                this.textSize(10);
                this.text("Upgrade Costs", 655, 590);
                this.text("Range: " + ran_cost, 655, 615);
                this.text("Speed: " + spe_cost, 655, 640);
                this.text("Damage: " + dam_cost, 655, 665);
                fill(240, 240, 240);
                this.textSize(13);
                if (mouseX>650&&mouseX<700&&mouseY>285&&mouseY<335) {
                    this.rect(620, 290, 40, 20);
                    this.fill(0);
                    this.text(ran_cost, 625, 305);
                }
                if (mouseX>650&&mouseX<700&&mouseY>345&&mouseY<395) {
                    this.rect(620, 350, 40, 20);
                    this.fill(0);
                    this.text(spe_cost, 625, 365);
                }
                if (mouseX>650&&mouseX<700&&mouseY>405&&mouseY<455) {
                    this.rect(620, 410, 40, 20);
                    this.fill(0);
                    this.text(dam_cost, 625, 425);
                }
                if (upgrade_damage) {
                    if (Current_mana - (20 + (towers.allTowers.get(t).damage_lvl-1)*10)>0) {
                        Current_mana -= (20 + (towers.allTowers.get(t).damage_lvl-1)*10);
                        towers.allTowers.get(t).upgrade_damage(base_tower_damage/2);
                        upgrade_damage = false;
                    }
                }
                if (upgrade_range) {
                    if (Current_mana - (20 + (towers.allTowers.get(t).range_lvl-1)*10)>0) {
                        Current_mana -= (20 + (towers.allTowers.get(t).range_lvl-1)*10);
                        towers.allTowers.get(t).upgrade_range();
                        upgrade_range = false;
                    }
                }
                if (upgrade_speed) {
                    if (Current_mana - (20 + (towers.allTowers.get(t).speed_lvl-1)*10)>0) {
                        Current_mana -= (20 + (towers.allTowers.get(t).speed_lvl-1)*10);
                        towers.allTowers.get(t).upgrade_speed(FPS);;
                        upgrade_speed = false;
                    }
                }
            }
        }
        //regenerating mana/frame
        if (monster_frame_count%FPS==0&&Countdown==0) {
            if (mana_max_f.intValue()-(Current_mana+mana_regen)>0) {
                if (manapool_active) {
                    Current_mana = (int) (Current_mana + mana_regen*man_mult);
                } else {
                    Current_mana += mana_regen;
                }
            } else {
                Current_mana = mana_max_f.intValue();
            }
        }
        //playing endless mode if activated
        if (endless) {
            endless();
        }
        if (classic) {
        //playing classic mode if activated
            if (isWave1) {
            isWave1 = movingStuff(isWave1, wave_1_monsters, wave_1_data, wave_2_data);
            } else if (isWave2) {
                isWave2 = movingStuff(isWave2, wave_2_monsters, wave_2_data, wave_3_data);
            } else if (isWave3) {
                isWave3 = movingStuff(isWave3, wave_3_monsters, wave_3_data, null);
            } else {
                youreAWinner();
            }

        }
        //fireing at monsters rendering gremlin tower interaction second last
        firingAtMonsters();
        //rendering wizard house last in one frame
        image(Wiz_house, x_wiz, y_wiz);
    }

    //running game
    public static void main(String[] args) {
        PApplet.main("WizardTD.App");
    }

    /**
     * Source: https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
     * @param pimg The image to be rotated
     * @param angle between 0 and 360 degrees
     * @return the new rotated image
     */
    public PImage rotateImageByDegrees(PImage pimg, double angle) {
        BufferedImage img = (BufferedImage) pimg.getNative();
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        PImage result = this.createImage(newWidth, newHeight, RGB);
        //BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        BufferedImage rotated = (BufferedImage) result.getNative();
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.set(i, j, rotated.getRGB(i, j));
            }
        }
        return result;
    }
}
