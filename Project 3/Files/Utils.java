/**
 * This class is a helper class to be used in other classes
 * 
 * @author Bhavin Oza (bo2115@rit.edu)
 * 
 */
public class Utils {
	static final int receivingPort = 1100; //receiving port
	static final int headerSize = 2; // header size
	static final int maxPacketSize = 2048; // max bytes handled in a packet, including the header size
	static final byte fin = (byte) -1; //FIN bit

	static byte[] toBytes(int i) { //convert seq to bytes array of length 2
		byte[] b = new byte[2];
		b[0] = (byte) (i & 0xFF);
		b[1] = (byte) ((i >> 8) & 0xFF);
		return b;
	}

	public static void main(String args[]) {

	}
}
