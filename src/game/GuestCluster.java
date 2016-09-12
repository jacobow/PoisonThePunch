package game;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

class GuestCluster extends Circle{
	public final static double GUEST_COUNT = 10;
	public final static double RADIUS = 40;
	public final static double SAFE_OPACITY = 0.3;
	public final static double UNSAFE_OPACITY = 0;

	private boolean safe;
	private Group levelNode;
	private ArrayList<PartyGuest> clusterGuests;
	private int clusterTimer;
	private boolean degenState;
	private double opacity;

	/**
	 * set up a crowd cluster for the player to hide in
	 * @param x the x position of the GuestCluster
	 * @param y the y position of the GuestCluster
	 * @param levelNode the parent node of the GuestCluster
	 */
	public GuestCluster(int x, int y, Group parent) {
		super(x, y, RADIUS, Color.BLUE);
		levelNode = parent;
		levelNode.getChildren().add(this);
		setSafe(true);
		degenState = false;
		clusterGuests = new ArrayList<PartyGuest>();
	}
	/**
	 * gets a list of the guests in the cluster
	 * @return list of the guests in the cluster
	 */
	public ArrayList<PartyGuest> getClusterGuests() {
		return clusterGuests;
	}
	/**
	 * adds a new PartyGuest to the cluster,
	 * enough of which makes the cluster a
	 * good place for hiding
	 * @param guest a new member of the cluster
	 */
	public void addGuest(PartyGuest guest) {
		levelNode.getChildren().add(guest);
		clusterGuests.add(guest);
		guest.setCluster(this);
	}
	/**
	 * remove a guest from the cluster, eventually
	 * making the cluster a bad place to hide
	 * @param guest a member to remove from the cluster
	 */
	public void removeGuest(PartyGuest guest) {
		levelNode.getChildren().remove(guest);
		clusterGuests.remove(guest);
	}
	/**
	 *randomly places guests inside the cluster
	 * @param guestCount the number of guests to be added to the cluster
	 */
	public void populate() {
		double x, y;
		for(int i = 0; i < GUEST_COUNT; i++) {
			x = randomDouble(RADIUS-15);
			y = randomDouble(Math.sqrt((RADIUS-15)*(RADIUS-15)-x*x));
			PartyGuest guest  = new PartyGuest("guest");
			guest.setX(this.getCenterX() + x - guest.getCharacter().getBoundsInLocal().getWidth() / 2);
			guest.setY(this.getCenterY() + y - guest.getCharacter().getBoundsInLocal().getHeight() / 2);
			guest.setOriginalZone(guest.getX(), guest.getY());
			this.addGuest(guest);
		}
	}
	private double randomDouble(double range) {
		Random r = new Random();
		return 2*range*r.nextDouble() - range;
	}
	/**
	 * determines whether a guest is inside the cluster
	 * @param guest the guest that may or may not be
	 * in the cluster
	 * @return whether the guest is in the cluster
	 */
	public boolean containsGuest(PartyGuest guest) {
		return this.contains(guest.getX(), guest.getY());
	}
	/**
	 * counts the guests currently in the cluster
	 * @return the number of guests in the cluster
	 */
	public int guestCount() {
		int count = 0;
		for(PartyGuest guest: clusterGuests) {
			if(this.containsGuest(guest)) count++;
		}
		return count;
	}
	/**
	 * @return if the cluster is safe
	 */
	public boolean isSafe() {
		return safe;
	}
	/**
	 * sets whether or not the cluster is safe, and
	 * sets its capacity to indicate this
	 * @param safe whether or not the cluster is safe
	 */
	public void setSafe(boolean safe) {
		this.safe = safe;
		if(safe) opacity = SAFE_OPACITY;
		else opacity = UNSAFE_OPACITY;
		this.setOpacity(opacity);
	}
	/**
	 * lowers the opacity of the cluster
	 * @param amount
	 */
	public void lowerOpacity(double amount) {
		opacity -= amount;
		this.setOpacity(opacity);
	}
	/**
	 * sets a 180 frame timer for the cluster
	 */
	public void setClusterTimer() {
		this.clusterTimer = 180;
	}
	/**
	 * @return returns the time of the cluster timer
	 */
	public int getClusterTimer() {
		return clusterTimer;
	}
	/**
	 * lets the timer tick one frame down
	 */
	public void tickTimer() {
		clusterTimer--;
	}
	/**
	 * @return if the cluster cluster is in the process of
	 * degenerating or regenerating
	 */
	public boolean isDegen() {
		return degenState;
	}
	/**
	 * @param state is the cluster in the process of
	 * degenerating or regenerating
	 */
	public void setDegenState(boolean state) {
		degenState = state;
	}
}
