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

public class App extends PApplet {

    public static final int CELLSIZE = 32;
    public static final int SIDEBAR = 120;
    public static final int TOPBAR = 40;
    public static final int BOARD_WIDTH = 20;

    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE+TOPBAR;

    public static final int FPS = 60;

    public String configPath;

    public Random random = new Random();

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

    float monster_frame_count;

    List<Integer[]> baddieEntrance = new ArrayList<>();
    List<Integer[]> corners = new ArrayList<>();
    List<Integer[]> grass_tiles = new ArrayList<>();
    List<Integer[]> tower_tiles = new ArrayList<>();


    int wave_enemy_count = 0;
    boolean isWave1 = true;
    boolean isWave2 = false;
    boolean isWave3 = false;
    boolean youWon = false;
    //config file extractions
    Integer base_tower_range;
    Float base_tower_speed;
    Integer base_tower_damage;
    Integer base_mana;
    Integer mana_max;
    Float mana_max_f;
    Integer mana_regen;
    Integer tower_cost;
    Integer mana_pool_base_cost;
    Integer mana_pool_inc_cost;
    Float mana_pool_spell_mult;
    Float mana_pool_spell_gain_mult;

    //Displayed Info
    Integer Wave_num = 1;
    Integer Countdown = FPS/2;
    Integer Current_mana;
    Float Current_mana_f;
    Float man_w;
    int Mana_width;

    //buttons
    boolean twotimes = false;
    boolean pause = false;
    boolean build_twr = false;
    boolean upgrade_range = false;
    boolean upgrade_speed = false;
    boolean upgrade_damage = false;
    boolean manapool = false;

    String dam = "0";
    String ran = "x";

    int min_x;
    int max_x;
    int min_y;
    int max_y;


    Random rand = new Random();

    PImage wiz_house;
    PImage doorMat;
    Integer[] wizard_HQ = new Integer[2];
    int x_wiz = 0;
    int y_wiz = 0;
    int x_d_wiz = 0;
    int y_d_wiz = 0;

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
    PImage tower_0;
    PImage tower_1;
    PImage tower_2;
    PImage fireball;

	int villain_test_count = 0;
    Baddie villain;
    Tower tower;
    Fireball FB;

    PImage tower_hold;

    Enemies enemies = new Enemies(this);
    Towers towers = new Towers(this);
    Fireballs fireballs = new Fireballs(this);
    int index_of_last_selected_twr = 0;

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
    public void youreALoser() {

    }
    public void youreAWinner() {

    }

    public void firingAtMonsters() {
        for (int tower = 0; tower<towers.allTowers.size(); tower++) {
            Tower Current_tower = towers.allTowers.get(tower);
            for (int enemy = 0; enemy<enemies.allBaddies.size();enemy++) {
                Baddie Current_enemy = enemies.allBaddies.get(enemy);
                double a = (Current_enemy.x+16)-(Current_tower.x+16);
                double b = (Current_enemy.y+16)-(Current_tower.y+16);
                double c = Math.sqrt(a*a+b*b);
                if (c<Current_tower.range) {
                    double end_x = Current_enemy.x+16; // plus something
                    double end_y = Current_enemy.y+16; //plus something
                    if (Current_tower.fireballs.allFireballs.size()==0) {
                        Current_tower.add_FIRE(this, fireball, end_x, end_y);
                    }
                    towers.allTowers.set(tower, Current_tower);
                }
            }
            /*if (Current_tower.fireballs.allFireballs.size()>0) {
                for (int fb = 0; fb<Current_tower.fireballs.allFireballs.size(); fb++) {
                Current_tower.fireballs.allFireballs.get(fb).draw(this);
                }
            }*/
        }
        /*if (fireballs.allFireballs.size()>=1) {
            for (int fire = 0; fire<fireballs.allFireballs.size(); fire++) {
                fireballs.allFireballs.get(fire).draw(this);
            }
        }*/
    }

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


    public void towerAdditions(Tower tower, int tow) {
        if (tower.selected) {
            /*for (int t = 0; t<towers.allTowers.size(); t++) {
                if (t == tow) {
                    continue;
                } else {
                    towers.allTowers.get(t).selected = false;
                }
            }*/
            noFill();
            this.strokeWeight(3);
            this.stroke(255, 255, 0);
            this.arc(tower.x*1f+16, tower.y*1f+16, tower.range*2f, tower.range*2f, 0, TWO_PI); //THIS NEEEEEEEEEEEEDSSS TO BEEEE FIXXXXEDD (ADDING CIRCLE WHEN selected)
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
        fill(0);
    }

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


    public boolean movingStuff(boolean thisWave, JSONArray wave_monsters, JSONObject furtherWaveData, JSONObject nextWaveData) {
        monster_frame_count+=1;
        if (Countdown>0) {
            //countdown conditionally rendered before wave starts
            this.fill(0);
            this.textSize(CELLSIZE);
            this.text("starts: "+Countdown/FPS,140, 35);
            Countdown -=1;
        } else {
            if (Current_mana==0) {
                youreALoser();
                //stop();
            }
            if (wave_enemy_count==wave_monsters.getJSONObject(0).getInt("quantity")&&enemies.allBaddies.size()==0) {
                if (!youWon) {
                    Countdown = FPS*nextWaveData.getInt("pre_wave_pause");
                }
                Wave_num+=1;
                wave_enemy_count = 0;
                monster_frame_count = 0;
                thisWave = false;
                return true;
            }
            // I have altered this so it is not exactly "correct" but it makes for nicer gameplay
            if ((monster_frame_count%((FPS*2)/furtherWaveData.getInt("duration")))==0.0/*&&(monster_frame_count%((FPS)/furtherWaveData.getInt("duration")))<=1*/) {
                if (wizard_HQ!=null) {
                    if (wave_enemy_count<wave_monsters.getJSONObject(0).getInt("quantity")) {
                        villain = new Baddie(this, gremlin, baddieEntrance, corners, wizard_HQ, wave_monsters.getJSONObject(0).getInt("hp"),wave_monsters.getJSONObject(0).getInt("speed"), wave_monsters.getJSONObject(0).getFloat("armour"), wave_monsters.getJSONObject(0).getInt("mana_gained_on_kill"));
                        enemies.AddEnemy(villain); 
                        wave_enemy_count+=1;
                    }    
                }
            }
            for (int vil_c=enemies.allBaddies.size()-1; vil_c>-1; vil_c--) {
                enemies.allBaddies.get(vil_c).draw(this);
                if (enemies.allBaddies.get(vil_c).isAtWiz) {
                    int HP = enemies.allBaddies.get(vil_c).health;
                    Current_mana -= HP;
                    enemies.RemoveEnemy(vil_c);
                }
            }
        }
            //System.out.println(villain);
            //Wizard rendered last
        //Take section from
        return false;
    }
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.
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
                        //insert code for sides of boards (road)
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
                    if (villain_test_count==0) {
                        /*villain = new Baddie(this, gremlin, baddieEntrance, corners, wizard_HQ);
                        enemies.AddEnemy(villain);
                        villain_test_count+=1;*/
                    }
            } else {
                    continue;
                }
                image(Tile, x_tile, y_tile);
                //image(, x_tile, y_tile);
            }
        }

    }

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player, enemies and map elements.
     */
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
        tower_0 = loadImage("src/main/resources/WizardTD/tower0.png");
        tower_1 = loadImage("src/main/resources/WizardTD/tower1.png");
        tower_2 = loadImage("src/main/resources/WizardTD/tower2.png");
        fireball = loadImage("src/main/resources/WizardTD/fireball.png");


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
        mana_max_f = (float) mana_max;
        Current_mana = mana_max;
        mana_regen = config.getInt("initial_mana_gained_per_second");
        tower_cost = config.getInt("tower_cost");
        mana_pool_base_cost = config.getInt("mana_pool_spell_initial_cost");
        mana_pool_inc_cost = config.getInt("mana_pool_spell_cost_increase_per_use");
        mana_pool_spell_mult = config.getFloat("mana_pool_spell_cap_multiplier");
        mana_pool_spell_gain_mult = config.getFloat("mana_pool_spell_mana_gained_multiplier");
    }

    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        if (key == 70) {
            twotimes = !twotimes;
        }
        if (key == 80) {
            pause = !pause;
            if (pause) {
                noLoop();
                this.fill(255,255,0);
                this.rect(650, 165, 50, 50);
                this.fill(0);
                this.text("P", 655,200);
            } else {
                loop();
            }
        }
        if (key == 84) {
            build_twr = !build_twr;
        }
        if (key == 49) {
            upgrade_range = !upgrade_range;
        }
        if (key == 50) {
            upgrade_speed = !upgrade_speed;
        }
        if (key == 51) {
            upgrade_damage = !upgrade_damage;
        }
        if (key == 77) {
            manapool = !manapool;
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){

    }

    @Override
    public void mousePressed(MouseEvent e) {
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
                        Current_mana -= tower_cost;
                    }
                    if (upgrade_damage&&upgrade_range&&upgrade_speed) {
                        Current_mana -= tower_cost + 60;
                    }
                    if ((upgrade_damage&&upgrade_range&&!upgrade_speed)||(upgrade_damage&&!upgrade_range&&upgrade_speed)||(!upgrade_damage&&upgrade_range&&upgrade_speed)) {
                        Current_mana -= tower_cost + 40;
                    }
                    if ((!upgrade_damage&&upgrade_range&&!upgrade_speed)||(upgrade_damage&&!upgrade_range&&!upgrade_speed)||(!upgrade_damage&&!upgrade_range&&upgrade_speed)) {
                        Current_mana -= tower_cost + 20;
                    }
                    //below image input needs to change
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
                    index_of_last_selected_twr = towers.allTowers.size();
                    towers.AddTower(tower);
                    break;              
                };
            }
        } else {
            for (int twr_tile=0;twr_tile<tower_tiles.size();twr_tile++) {
                min_x = tower_tiles.get(twr_tile)[0];
                max_x = tower_tiles.get(twr_tile)[0]+32;
                min_y = tower_tiles.get(twr_tile)[1];
                max_y = tower_tiles.get(twr_tile)[1]+32;
                if ((mouseX>min_x)&&(mouseX<max_x)&&(mouseY<max_y)&&(mouseY>min_y)) {
                    for (int twr=0; twr<towers.allTowers.size();twr++) { 
                        if (towers.allTowers.get(twr).x==min_x&&towers.allTowers.get(twr).y==min_y) {
                            towers.allTowers.get(twr).selected = true;
                        } else {
                             towers.allTowers.get(twr).selected = false;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    /*@Override
    public void mouseDragged(MouseEvent e) {

    }*/

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        background(165,135,16);
        textSize(CELLSIZE);
        printmap();
        image(doorMat, x_d_wiz, y_d_wiz);
        drawTowers();
        if (twotimes) {
            frameRate(FPS*2);
        } else {
            frameRate(FPS);
        }
        // wave label
        this.fill(0);
        this.textSize(CELLSIZE);
        this.text("wave: "+Wave_num,10, 35);
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
        this.fill(165,135,16);
        this.rect(650, 105, 50, 50);
        this.rect(650, 165, 50, 50);
        this.rect(650, 225, 50, 50);
        this.rect(650, 285, 50, 50);
        this.rect(650, 345, 50, 50);
        this.rect(650, 405, 50, 50);
        this.rect(650, 465, 50, 50);
        this.fill(255,255,0);
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
            this.rect(650, 465, 50, 50);
        }
        this.fill(0);
        this.textSize(30);
        this.text("FF", 655,140);
        this.text("P", 655,200);
        this.text("T", 655,260);
        this.text("U1", 655,320);
        this.text("U2", 655,380);
        this.text("U3", 655,440);
        this.text("M", 655,500);

        if (isWave1) {
            isWave2 = movingStuff(isWave1, wave_1_monsters, wave_1_data, wave_2_data);
        } else if (isWave2) {
            isWave3 = movingStuff(isWave2, wave_2_monsters, wave_2_data, wave_3_data);
        } else if (isWave3) {
            youWon = movingStuff(isWave3, wave_3_monsters, wave_3_data, null);
        } else {
            youreAWinner();
        }

        //drawTowers();
        firingAtMonsters();
        image(Wiz_house, x_wiz, y_wiz);
    }

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
