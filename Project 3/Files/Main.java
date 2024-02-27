import java.util.Arrays;

/**
 * This class represents the main entry point tot the program
 * 
 * @author Bhavin Oza (bo2115@rit.edu)
 * 
 */
class Main {

	public static void main(String args[]) {
		System.out.println("Main file parameters==>\t" + Arrays.toString(args));
		if (args.length == 0) { // receiver has no input param
			try {
				Receiver receiver = new Receiver(); // receiver instance
				receiver.start(); // start the receiver
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (args.length == 2) { // sender has filename and receiving ip
			try {
				Sender sender = new Sender(args[0], args[1]); // sender instance
				sender.start(); // start the sender
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			System.out.println("Invalid input args!");
		}
	}
}
