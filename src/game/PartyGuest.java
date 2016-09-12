package game;

import java.util.Random;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

class PartyGuest extends Group {

	private ImageView character;
	private Circle fov;
	private double x;
	private double y;
	private double xVol;
	private double yVol;
	private Circle originalZone;
	private GuestCluster cluster;
	private boolean safe;
	private boolean inPlay;

	/**
	 * Makes a type of party guest.  Can make the player,
	 * the monitor, or just a standard guest.
	 * @param code Code determines what type of guest is made.
	 */
	public PartyGuest(String code) {
		//field of vision for the monitor
		fov = new Circle();
		switch(code) {
			case "guest":
				Image guestImage = new Image(getClass().getClassLoader().getResourceAsStream("guest.png"));
				character = new ImageView(guestImage);
				sizeAndScale(character);
				this.getChildren().add(character);
				//guest is still in the game and hasn't exited
				inPlay = true;
				break;
			case "player":
				Image playerImage = new Image(getClass().getClassLoader().getResourceAsStream("player.png"));
				character = new ImageView(playerImage);
				sizeAndScale(character);
				this.getChildren().add(character);
				break;
			case "monitor":
				Image monitorImage = new Image(getClass().getClassLoader().getResourceAsStream("monitor.png"));
				character = new ImageView(monitorImage);
				sizeAndScale(character);
				this.getChildren().add(character);
				fov.setRadius(80);
				fov.setFill(Color.YELLOW);
				fov.setOpacity(0.2);
				this.getChildren().add(fov);
				break;
		}
	}

	private void sizeAndScale(ImageView i) {
		i.setPreserveRatio(true);
		i.setFitHeight(25);
	}
	/**
	 * @return the x location of the guest
	 */
	public double getX() {
		return x;
	}
	/**
	 * @return the y location of the guest
	 */
	public double getY() {
		return y;
	}
	/**
	 * @param the x location of the guest
	 */
	public void setX(double x) {
		this.x = x;
		character.setX(x - character.getBoundsInLocal().getWidth()/2);
		fov.setCenterX(x);
	}
	/**
	 * @param y the y location of the guest
	 */
	public void setY(double y) {
		this.y = y;
		character.setY(y - character.getBoundsInLocal().getHeight()/2);
		fov.setCenterY(y - character.getBoundsInLocal().getHeight()/2);
	}
	/**
	 * sets the initial location of the guest and the root node
	 * @param x the x location of the guest
	 * @param y the y location of the guest
	 * @param node the root node
	 */
	public void init(double x, double y, Group node) {
		this.setX(x);
		this.setY(y);
		node.getChildren().add(this);
	}
	/**
	 * @return the x velocity of the guest
	 */
	public double getxVol() {
		return xVol;
	}
	/**
	 * @param xVol the x velocity of the guest
	 */
	public void setxVol(double xVol) {
		this.xVol = xVol;
	}
	/**
	 * @return the y velocity of the guest
	 */
	public double getyVol() {
		return yVol;
	}
	/**
	 *
	 * @param yVol the y velocity of the guest
	 */
	public void setyVol(double yVol) {
		this.yVol = yVol;
	}
	/**
	 * gets the guest's original placement area
	 * @return guest's original placement area
	 */
	public Circle getOriginalZone() {
		return originalZone;
	}
	/**
	 * set a circle where the guest is
	 * @param x x location of guest
	 * @param y y location of guest
	 */
	public void setOriginalZone(double x, double y) {
		originalZone = new Circle(x, y, 1);
	}
	/**
	 *gets the ImageView of the guest
	 * @return the ImageView of the guest
	 */
	public ImageView getCharacter() {
		return character;
	}

	private double randomDouble(double range) {
		Random r = new Random();
		return 2*range*r.nextDouble() - range;
	}
	/**
	 * Sets the guest's velocity vector toward a certain point
	 * with a certain speed
	 * @param x x location of point to go to
	 * @param y y location of point to go to
	 * @param speed how fast the guest should move
	 */
	public void goToward(double x, double y, double speed) {
		double distance = distance(x, y, this.getX(), this.getY());
		setxVol(speed*(x-this.getX())/distance);
		setyVol(speed*(y-this.getY())/distance);
	}

	private static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}
	/**
	 * Updates the guest's location
	 * @param dt the time differential from one frame to the next
	 */
	public void updateGuest(double dt) {
		this.setX(this.getX() + this.getxVol()*dt);
		this.setY(this.getY() + this.getyVol()*dt);
	}
	/**
	 * Sets a random velocity for the guest
	 * @param speed the magnitude of the random velocity
	 */
	public void setRandomVol(double speed) {
		setxVol(randomDouble(speed));
		setyVol(Math.sqrt(speed*speed - this.getxVol()*this.getxVol()));
	}
	/**
	 * Reverses the guest's velocity
	 */
	public void reverseVol() {
		xVol = -xVol;
		yVol = -yVol;
	}
	/**
	 * @return the cluster the guest belongs to
	 */
	public GuestCluster getCluster() {
		return cluster;
	}
	/**
	 * @param cluster the cluster the guest belongs to
	 */
	public void setCluster(GuestCluster cluster) {
		this.cluster = cluster;
	}
	/**
	 * @return if the guest is safe from the monitor
	 */
	public boolean isSafe() {
		return safe;
	}
	/**
	 * @param safe is the guest safe from the monitor
	 */
	public void setSafe(boolean safe) {
		this.safe = safe;
	}
	/**
	 * @return the circular field of vision of the monitor
	 */
	public Circle getFov() {
		return fov;
	}
	/**
	 * @return if the guest is still in play
	 */
	public boolean isInPlay() {
		return inPlay;
	}
	/**
	 * @param inPlay is the guest still in play
	 */
	public void setInPlay(boolean inPlay) {
		this.inPlay = inPlay;
	}
}
