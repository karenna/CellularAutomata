package csc212.project4;

public class CA_User_Spec {

	private int CAchoice;
	private int iterationsChoice;
	
	// Constructor--initialize our class variables
	public CA_User_Spec()
	{
		CAchoice = 0;
		iterationsChoice = 0;
	}

	// Getters and setters for our class variables.

	/**
	 * Return the value of CAchoice.
	 */
	public int getCAchoice() {
		return CAchoice;
	}

	/**
	 * Set CAchoice to the value passed into the method.
	 */
	public void setCAchoice(int CAchoice) {
		this.CAchoice = CAchoice;
	}

	/**
	 * Return the value of iterationsChoice.
	 */
	public int getIterationsChoice() {
		return iterationsChoice;
	}

	/**
	 * Set iterationsChoice to the value passed into the method.
	 */
	public void setIterationsChoice(int iterationsChoice) {
		this.iterationsChoice = iterationsChoice;
	}
	
}
