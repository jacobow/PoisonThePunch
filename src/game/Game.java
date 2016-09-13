package game;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

class Game {

	public static final String TITLE = "Poison the Punch";
	public static final double PUNCH_BOWL_X = 300;
	public static final double PUNCH_BOWL_Y = 300;
	public static final double EXIT_X = 300;
	public static final double EXIT_Y = 600;
	public static final double PLAYER_X = 300;
	public static final double PLAYER_Y = 500;
	public static final double MONITOR_X = 50;
	public static final double MONITOR_Y = 50;
	public static final int[][] clusterMatrix = {//a pentagon of clusters
										{300, 100},
										{110, 238},
										{182, 462},
										{418, 462},
										{490, 238}
										};

	private Scene scene;
	private Group root;
    private Level level;
    private int width;
    private int height;
    private int currentLevel;
    private boolean godMode;

    /**
     * Returns name of the game.
     */
    public String getTitle () {
        return TITLE;
    }
    /**
     * Builds the scene of the game, and places the main menu on it.
     * @param width the width of the scene
     * @param height the height of the scene
     * @return the initial scene to be set on the stage
     */
    public Scene init(int width, int height) {
    	root = new Group();
    	this.width = width;
    	this.height = height;
    	scene = new Scene(root, width, height, Color.rgb(164, 195, 230));
    	initMenu();
    	return scene;
    }

    private void initLevel1() {
    	currentLevel = 1;
    	root.getChildren().clear();
    	level = new Level(root);
    	level.setClusterMatrix(clusterMatrix);
    	level.initClusters();
    	level.initPunchBowl(PUNCH_BOWL_X, PUNCH_BOWL_X);
    	level.initExit(EXIT_X, EXIT_Y);
    	level.initPlayer(PLAYER_X, PLAYER_Y, root);
    	level.initMonitor(MONITOR_X, MONITOR_Y, root);
    	if(godMode) level.setGodMode(true);
    	scene.setOnKeyPressed(e -> handleKeyPress(e.getCode()));
    	scene.setOnKeyReleased(e -> handleKeyRelease(e.getCode()));
    }

    private void initLevel2() {
    	initLevel1();
    	currentLevel = 2;
    	level.initDegenRegen();
    }

    private void initLevel3() {
    	initLevel2();
    	currentLevel = 3;
    }
    /**
     * Step method passed as a frame to the TimeLine. Handles level animation and logic.
     * @param elaspedTime
     */
    public void step(double elaspedTime) {
    	switch(currentLevel) {
    		case 1:
    			stepLevel1(elaspedTime);
    			if(level.lose()) initLevel1();
    	    	if(level.win()) nextLevelButton();
    			break;
    		case 2:
    			stepLevel2(elaspedTime);
    			if(level.lose()) initLevel2();
    	    	if(level.win()) nextLevelButton();
    			break;
    		case 3:
    			stepLevel3(elaspedTime);
    			if(level.lose()) initLevel3();
    	    	if(level.win()) nextLevelButton();
    			break;
    		default:
    			break;
    	}
    }

    private void stepLevel1(double elaspedTime) {
    	level.showScore();
    	level.controlMonitorCollisions(width, height);
    	level.setMonitorVelocityAndPlayerSafety();
    	if(level.getThirstTimer() == 0) level.sendGuestsToPunch();
    	level.sendGuestsFromPunch(elaspedTime);
    	if(level.punchBowlCollision(level.getPlayer())) level.setPunchBowlPoisoned(true);
    	level.setClusterSafety();
    	level.tickTimers();
    	level.updatePlayer(elaspedTime);
    	level.updateMonitor(elaspedTime);
    }

    private void stepLevel2(double elaspedTime) {
       	stepLevel1(elaspedTime);
    	level.clusterDegenControl();
    }

    private void stepLevel3(double elaspedTime) {
    	stepLevel2(elaspedTime);
    	level.punchBowlReset();
    }

    private void initMenuButtons() {
    	Button level1Button = new Button("Start Level 1");
        level1Button.setLayoutX(width*0.05);
        level1Button.setLayoutY(height*0.8);
        showInstruction(width*0.25, height*0.8 + 20, "This is a very easy tutorial level to show how the basic game mechanic works without difficulty.");
        level1Button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                initLevel1();
            }
        });
        Button level2Button = new Button("Start Level 2");
        level2Button.setLayoutX(width*0.05);
        level2Button.setLayoutY(height*0.8 + 40);
        showInstruction(width*0.25, height*0.8 + 60, "Now the guests recognize you, so they won't hide you forever.");
        level2Button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                initLevel2();
            }
        });
        Button level3Button = new Button("Start Level 3");
        level3Button.setLayoutX(width*0.05);
        level3Button.setLayoutY(height*0.8 + 80);
        showInstruction(width*0.25, height*0.8 + 100, "Now the guests have learned to refill the punch bowl");
        level3Button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                initLevel3();
            }
        });
        godMode = false;
        Glow glow = new Glow();
        DropShadow shadow = new DropShadow();
        Button godModeButton = new Button("god mode");
        godModeButton.setLayoutX(width*0.8);
        godModeButton.setLayoutY(height*0.8 + 80);
        godModeButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
            	if(godMode) {
            		godModeButton.setEffect(glow);
                	godMode = false;
            	}
            	else {
            		godModeButton.setEffect(shadow);
                	godMode = true;
            	}
            }
        });
        root.getChildren().add(level1Button);
        root.getChildren().add(level2Button);
        root.getChildren().add(level3Button);
        root.getChildren().add(godModeButton);
    }

    private void showTitle() {
    	Text title = new Text(25, 50, getTitle());
    	title.setFont(Font.font ("Bodoni 72 Oldstyle", 36));
    	root.getChildren().add(title);
    }
    //puts a game mechanic instruction on main menu
    private void showInstruction(double x, double y, String inst) {
    	Text t = new Text(x, y, inst);
    	t.setFont(Font.font ("Bodoni 72 Oldstyle", 12));
    	root.getChildren().add(t);
    }
    //puts instructions and icons on main menu
    private void initInstructions() {
    	showInstruction(400, 50, "Press E to return to this menu");
    	showInstruction(200, 100, "Control player with WASD or arrow keys.");
    	PartyGuest p = new PartyGuest("player");
    	p.setX(100);
    	p.setY(100);
    	root.getChildren().add(p);
    	showInstruction(200, 150, "Poison the punch by walking into it.  The goal of the game is to make people sick. \nThe bowl can be refilled in level three.");
    	Circle pb = new Circle(100, 150, 20, Color.RED);
    	root.getChildren().add(pb);
    	showInstruction(200, 270, "Don't let the monitor catch you.");
    	PartyGuest m = new PartyGuest("monitor");
    	m.setX(100);
    	m.setY(270);
    	root.getChildren().add(m);
    	showInstruction(200, 400, "Hide from the monitor in guest clusters.  Only clusters with more than 5 people will\nhide you.");
    	GuestCluster gc = new GuestCluster(100, 400, root);
    	gc.populate();
    	gc.setSafe(true);
    }

    private void initMenu() {
    	root.getChildren().clear();
    	currentLevel = 0;
    	showTitle();
    	initInstructions();
    	initMenuButtons();
    }
    private void nextLevelButton() {
    	level.setGodMode(true);
    	Button b = new Button("Level passed.  Click to continue.");
        b.setLayoutX(200);
        b.setLayoutY(300);
        b.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
            	switch(currentLevel) {
        		case 1:
        			initLevel2();
        			level.setGodMode(godMode);
        			break;
        		case 2:
        			initLevel3();
        			level.setGodMode(godMode);
        			break;
        		case 3:
        			initMenu();
        			level.setGodMode(godMode);
        			break;
        		default:
        			break;
        	}
            }
        });
        root.getChildren().add(b);
    }

    private void handleKeyPress(KeyCode code) {
        level.addInput(code.toString());
        if(code.toString().equals("E")) {
        	initMenu();
        }
    }

    private void handleKeyRelease(KeyCode code) {
        level.removeInput(code.toString());
    }
}
