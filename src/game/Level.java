package game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

class Level {

	public static final double MONITOR_SPEED = 130;
	public static final double PLAYER_SPEED = 125;
	public static final double GUEST_SPEED = 150;
	public static final double BOWL_RADIUS = 20;
	public static final double EXIT_RADIUS = 5;
	public static final int WIN_TARGET = 24;

	private Group levelNode;
	private PartyGuest player;
    private PartyGuest monitor;
    private HashSet<String> input;
	private Circle punchBowl;
	private Circle exit;
	private int[][] clusterMatrix;
	private ArrayList<GuestCluster> clusterList;
	private HashSet<GuestCluster> degenClusters;
	private HashSet<GuestCluster> regenClusters;
	private HashSet<GuestCluster> safeClusters;
	private HashSet<PartyGuest> movingAwayGuests;
	private HashSet<PartyGuest> movingBackGuests;
	private int monitorTimer;
	private int thirstTimer;
	private boolean punchBowlPoisoned;
	private int guestPoisoned;
	private Text score;
	private boolean punchResetArmed;
	private boolean godMode;

	/**
	 * a level has contains gameplay mechanics and logic which
	 * leads to either a win or a loss depending on user input
	 * @param node the root of the level
	 */
	public Level(Group node) {
		levelNode = node;
		input = new HashSet<String>();
		movingAwayGuests = new HashSet<PartyGuest>();
		movingBackGuests = new HashSet<PartyGuest>();
		setMonitorTimer();
		setThirstTimer();
		guestPoisoned = 0;
		punchResetArmed = true;
		score = new Text(500, 25, "Score: " + guestPoisoned + "/" + WIN_TARGET);
		levelNode.getChildren().add(score);

	}

	/**
	 * @param clusterMatrix a 2d array storing the x and y
	 * coordinates of the clusters in the level
	 */
	public void setClusterMatrix(int[][] clusterMatrix) {
		this.clusterMatrix = clusterMatrix;
	}
	/**
	 * places the clusters according to the cluster matrix
	 * and populates them with guests
	 */
	public void initClusters() {
		clusterList = new ArrayList<GuestCluster>();
		for(int i = 0; i < clusterMatrix.length; i++) {
			GuestCluster cluster = new GuestCluster(clusterMatrix[i][0],clusterMatrix[i][1], levelNode);
			clusterList.add(cluster);
			cluster.populate();
		}
	}
	/**
	 * removes the cluster that belongs to a guest
	 * from the list of clusters the level will
	 * acknowledge
	 * @param guest the guest belonging to the cluster
	 * to be removed
	 */
	public void removeCluster(PartyGuest guest) {
		clusterList.remove(guest.getCluster());
	}
	/**
	 * @return the list of clusters the level acknowledges
	 */
	public ArrayList<GuestCluster> getClusterList() {
		return clusterList;
	}
	/**
	 * places an unpoisoned punch bowl in its location
	 */
	public void initPunchBowl(double x, double y) {
		punchBowl = new Circle(x, y, BOWL_RADIUS, Color.RED);
		setPunchBowlPoisoned(false);
		levelNode.getChildren().add(punchBowl);
	}
	/**
	 * places an exit point at the bottom of the level
	 */
	public void initExit(double x, double y) {
		exit = new Circle(x, y, EXIT_RADIUS);
	}
	/**
	 * @return x location of the exit
	 */
	public double getExitX() {
		return exit.getCenterX();
	}
	/**
	 * @return y location of the exit
	 */
	public double getExitY() {
		return exit.getCenterY();
	}
	/**
	 * determines if a guest has collided with the exit
	 * @param guest the guest that may or may not have hit
	 * the exit
	 * @return whether or not the guest collided with the
	 * exit
	 */
	public boolean exitCollision(PartyGuest guest){
		return exit.contains(guest.getX(), guest.getY());
	}
	/**
	 * initializes the player at its location
	 * @param x x location of the player
	 * @param y y location of the player
	 * @param node the root of the player
	 */
	public void initPlayer(double x, double y, Group node) {
		player = new PartyGuest("player");
		player.setX(x);
		player.setY(y);
		node.getChildren().add(player);
		player.setSafe(false);
	}
	/**
	 * @return the player
	 */
	public PartyGuest getPlayer() {
		return player;
	}
	/**
	 * initializes the monitor at its location
	 * @param x starting x location of the monitor
	 * @param y starting y location of the monitor
	 * @param node the root of the monitor
	 */
	public void initMonitor(double x, double y, Group node) {
		monitor = new PartyGuest("monitor");
		monitor.init(x, y, node);
		monitor.setRandomVol(MONITOR_SPEED);
	}
	/**
	 * update the location of the monitor
	 * @param dt the time differential from one frame
	 * to the next
	 */
	public void updateMonitor(double dt) {
		monitor.updateGuest(dt);
	}
	/**
	 * makes a random guest thirsty enough to get a drink
	 * from the the punch bowl
	 * @return
	 */
	public PartyGuest thirstSelector() {
		Random r = new Random();
		GuestCluster cluster = clusterList.get(r.nextInt(clusterList.size()));
		ArrayList<PartyGuest> guestList = cluster.getClusterGuests();
		return guestList.get(r.nextInt(guestList.size()));
	}
	/**
	 * @return a set of the guests moving away from their
	 * clusters toward the punch
	 */
	public HashSet<PartyGuest> getMovingAwayGuests() {
		return movingAwayGuests;
	}
	/**
	 * adds a guest to the set of guests who are moving
	 * away from their clusters toward the punch bowl
	 * @param guest the guest to be added
	 */
	public void addMovingAwayGuest(PartyGuest guest) {
		movingAwayGuests.add(guest);
	}
	/**
	 * removes a guest to the set of guests who are moving
	 * away from their clusters toward the punch bowl
	 * @param guest the guest to be removed
	 */
	public void removeMovingAwayGuest(PartyGuest guest) {
		movingAwayGuests.remove(guest);
	}
	/**
	 * @return a set of the guests moving away from the punch bowl
	 */
	public HashSet<PartyGuest> getMovingBackGuests() {
		return movingBackGuests;
	}
	/**
	 * adds a guest to the set of guests who are moving
	 * away from the punch bowl
	 * @param guest the guest to be added
	 */
	public void addMovingBackGuest(PartyGuest guest) {
		movingBackGuests.add(guest);
	}
	/**
	 * removes a guest to the set of guests who are moving
	 * away from the punch bowl
	 * @param guest the guest to be removed
	 */
	public void removeMovingBackGuest(PartyGuest guest) {
		movingBackGuests.remove(guest);
	}
	/**
	 * determines whether a guest is colliding with any
	 * cluster
	 * @param guest the guest who may or may not be
	 * colliding with any cluster
	 * @return if the guest collides with any cluster
	 */
	public boolean clusterCollision(PartyGuest guest) {
		for(GuestCluster cluster: clusterList) {
			if(cluster.isSafe() && guest.getCharacter().getBoundsInParent().intersects(cluster.getBoundsInParent())) {
				return true;
			}
		}
		return false;
	}
	/**
	 * determines whether a guest has collided with the border
	 * @param guest the guest who may or may not have collided
	 * with the border
	 * @param width the width of the level
	 * @param height the height of the level
	 * @return if the guest collides with the border
	 */
	public boolean borderCollision(PartyGuest guest, int width, int height) {
		return (!(between(guest.getX(), 0, width) && between(guest.getY(), 0, height)));
	}

	private boolean between(double i, double min, double max) {
		return (min < i && i < max);
	}
	/**
	 * determines if a guest has collided with the punch bowl
	 * @param guest the guest who may or may not have collided
	 * with the punch bowl
	 * @return if the guest collided with the punch bowl
	 */
	public boolean punchBowlCollision(PartyGuest guest) {
		return (punchBowl.contains(guest.getX(), guest.getY()));
	}
	/**
	 * @return if the punch bowl is poisoned
	 */
	public boolean isPunchBowlPoisoned() {
		return punchBowlPoisoned;
	}
	/**
	 * sets the punch bowl to be poisoned or unpoisoned.
	 * punch bowl is red when unpoisoned and green when
	 * poisoned
	 * @param punchBowlPoisoned whether or not the punch
	 * bowl is to be poisoned
	 */
	public void setPunchBowlPoisoned(boolean punchBowlPoisoned) {
		this.punchBowlPoisoned = punchBowlPoisoned;
		if(punchBowlPoisoned) {
			punchBowl.setFill(Color.GREEN);
		}
		else {
			punchBowl.setFill(Color.RED);
		}
	}
	/**
	 * sets the monitor velocity toward the player if
	 * the player is out in the open and in a random
	 * direction if the player is hidden.  also determines
	 * if the player is hidden
	 */
    public void setMonitorVelocityAndPlayerSafety() {
    	if(!this.clusterCollision(player)) {
    		player.setSafe(false);
    		monitor.goToward(player.getX(), player.getY(), MONITOR_SPEED);
    	}
    	else {
    		player.setSafe(true);
    		if(this.getMonitorTimer() <= 0) {
        		monitor.setRandomVol(MONITOR_SPEED);
        		setMonitorTimer();
        	}
    	}
    }
    /**
     * reverses the monitors velocity if the monitor has
     * collided with the border
     * @param width width of the level
     * @param height height of the level
     */
    public void controlMonitorCollisions(int width, int height) {
    	if(borderCollision(monitor, width, height)) monitor.reverseVol();
    }
    /**
     * sends a random guest to the punch bowl
     */
    public void sendGuestsToPunch() {
    	PartyGuest thirstyGuest = this.thirstSelector();
    	thirstyGuest.goToward(punchBowl.getCenterX(), punchBowl.getCenterX(), GUEST_SPEED);
    	addMovingAwayGuest(thirstyGuest);
    	setThirstTimer();
    }
    /**
     * determines where to send guests from the punch bowl
     * and sends them there
     * @param dt
     */
    public void sendGuestsFromPunch(double dt) {
    	for(PartyGuest guest: getMovingAwayGuests()) {
    		if(punchBowlCollision(guest)) {
    			if(isPunchBowlPoisoned()) {
    				guest.goToward(getExitX(), getExitY(), GUEST_SPEED);
    			}
    			else {
    				guest.reverseVol();
        			addMovingBackGuest(guest);
    			}
    		}
    		if(exitCollision(guest) && guest.isInPlay()) {
    			guest.setInPlay(false);
    			guest.getCluster().removeGuest(guest);
    			guestPoisoned++;
    		}
    		guest.updateGuest(dt);
    	}
    	for(PartyGuest g: getMovingBackGuests()) {
    		if(g.getOriginalZone().contains(g.getX(), g.getY())) {
    			g.setxVol(0);
    			g.setyVol(0);
    			removeMovingBackGuest(g);
    			if(g.getCluster().guestCount() == 0) removeCluster(g);
    		}
    	}
    }
    /**
     * sets the safety of the clusters based on their
     * population count.  clusters less than 6 are removed
     */
    public void setClusterSafety() {
       	for(GuestCluster cluster: getClusterList()) {
    		if(cluster.guestCount() < 6) {
    			cluster.setSafe(false);
    		}
    	}
    }
    /**
     * ticks the timers for the monitor to change routes
     * and the guests to get thirsty
     */
    public void tickTimers() {
    	tickMonitorTimer();
    	tickThirstTimer();
    }
    /**
     * add input to the list of input to be processed in
     * updatePlayer
     * @param code user input
     */
    public void addInput(String code) {
    	input.add(code);
    }
    /**
     * remove input from the list of input to be processed in
     * updatePlayer
     * @param code user input
     */
    public void removeInput(String code) {
    	input.remove(code);
    }
    /**
     * updates the player based on user input
     * @param dt the time differential from one frame to
     * the next
     */
	public void updatePlayer(double dt) {
		double c = 1;
		if(input.size() == 2) {
			c = Math.sqrt(2);
		}
		if(input.contains("RIGHT")|input.contains("D")) {
			player.setX(player.getX() + PLAYER_SPEED*dt/c);
		}
		if(input.contains("LEFT")|input.contains("A")) {
			player.setX(player.getX() - PLAYER_SPEED*dt/c);
		}
		if(input.contains("UP")|input.contains("W")) {
			player.setY(player.getY() - PLAYER_SPEED*dt/c);
		}
		if(input.contains("DOWN")|input.contains("S")) {
			player.setY(player.getY() + PLAYER_SPEED*dt/c);
		}
    }
	/**
	 * @return gets the current time of the timer controlling
	 * when the monitor changes routes
	 */
	public int getMonitorTimer() {
		return monitorTimer;
	}
	/**
	 * sets a 300 frame timer for when the monitor should
	 * changes routes
	 */
	public void setMonitorTimer() {
		monitorTimer = 300;
	}
	/**
	 * ticks the monitor timer down one frame
	 */
	public void tickMonitorTimer() {
		monitorTimer--;
	}
	/**
	 * @return gets the current time of the timer controlling
	 * when the a guest will be thirsty
	 */
	public int getThirstTimer() {
		return thirstTimer;
	}
	/**
	 * sets a 160 frame timer for when the a guest will
	 * get thirsty
	 */
	public void setThirstTimer() {
		this.thirstTimer = 180;
	}
	/**
	 * ticks the thirst timer down one frame
	 */
	public void tickThirstTimer() {
		thirstTimer--;
	}
	/**
	 * @return the number of guests who have been poisoned
	 */
	public int getGuestPoisoned() {
		return guestPoisoned;
	}
	/**
	 * @return whether or not the player has made enough
	 * guests exit to win
	 */
	public boolean win() {
		return guestPoisoned == WIN_TARGET;
	}
	/**
	 * @return whether or not the player has been caught
	 * by the monitor
	 */
	public boolean lose() {
		return !godMode && !player.isSafe() && monitor.getFov().contains(player.getX(), player.getY());
	}
	/**
	 * sets up the sets that will control the degeneration
	 * and regeneration of the clusters
	 */
	public void initDegenRegen() {
		degenClusters = new HashSet<GuestCluster>();
		regenClusters = new HashSet<GuestCluster>();
		safeClusters = new HashSet<GuestCluster>();
	}
	/**
	 * makes clusters degenerate when the player enters it
	 * and regenerate after a certain amount of time
	 */
	public void clusterDegenControl() {
		for(GuestCluster c : clusterList) {
			if(c.contains(player.getX(), player.getY()) && !c.isDegen()) {
				c.setDegenState(true);
				c.setClusterTimer();
				degenClusters.add(c);
			}
		}
		for(GuestCluster c : degenClusters) {
			c.tickTimer();
			c.lowerOpacity(0.3/180);
			if(c.getClusterTimer() == 0) {
				c.setClusterTimer();
				c.setSafe(false);
				regenClusters.add(c);
			}
		}
		for(GuestCluster c: regenClusters) {
			c.tickTimer();
			if(degenClusters.contains(c)) degenClusters.remove(c);
			if(c.getClusterTimer() == 0) {
				c.setSafe(true);
				safeClusters.add(c);
			}
		}
		for(GuestCluster c: safeClusters) {
			regenClusters.remove(c);
			c.setDegenState(false);
		}
		safeClusters.clear();
	}
	/**
	 * resets the punch bowl to not be poisoned each time 6
	 * guests get sick
	 */
	public void punchBowlReset() {
		if(punchResetArmed && guestPoisoned%6 == 0 && guestPoisoned > 0) {
			setPunchBowlPoisoned(false);
			punchResetArmed = false;
		}
		if(guestPoisoned%6 != 0) {
			punchResetArmed = true;
		}
	}
	/**
	 * displays the amount of guests who have exited
	 * and the number of guests it takes to win
	 */
	public void showScore() {
		score.setText("Score: " + guestPoisoned + "/" + WIN_TARGET);
	}
	/**
	 * @param godMode in god mode the player can't be caught
	 * by the monitor
	 */
	public void setGodMode(boolean godMode) {
		this.godMode = godMode;
	}
}
