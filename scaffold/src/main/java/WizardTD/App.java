package WizardTD;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.Graphics2D;
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
    Integer Countdown = FPS*10;
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

	int villain_test_count = 0;
    Baddie villain;
    Tower tower;

    PImage tower_hold;

    Enemies enemies = new Enemies(this);
    Towers towers = new Towers(this);

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
            }
            if (wave_enemy_count==wave_monsters.getJSONObject(0).getInt("quantity")&&enemies.allBaddies.size()==0) {
                if (!youWon) {
                    Countdown = FPS*nextWaveData.getInt("pre_wave_pause");
                }
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
            for (int tow=0; tow<towers.allTowers.size(); tow++) {
                towers.allTowers.get(tow).draw(this);
            }
            //System.out.println(villain);
            //Wizard rendered last
        }
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
                    tower = new Tower(this, tower_0, min_x, min_y);
                    towers.AddTower(tower);
                    break;              
                };
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
        if (twotimes) {
            frameRate(FPS*2);
        } else {
            frameRate(FPS);
        }
        // wave label
        this.fill(0);
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
