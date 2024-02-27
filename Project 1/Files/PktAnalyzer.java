
/**
 * Version:
 *     $1.0$
 *
 */
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 * This class analyzes the binary file of different packets, and prints the info
 * of fields in the packet
 * 
 * @author Bhavin Oza (bo2115@rit.edu)
 * 
 */
public class PktAnalyzer {
	static String type; // type of packet: TCP, UDP, ICMP
	static String[] dataStr; // data converted to string and stored in array

	public static void main(String args[]) {
		String fileName = args[0]; // name of file, which contains packet
		try (InputStream ps = new FileInputStream(fileName)) { // try with resources, closes resources as the block ends
			byte[] data = new byte[ps.available()]; // create array of size of data in file
			ps.read(data); // read data from file and store as byte in array

			//array with hex char, used to convert byte to hex
			char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
			dataStr = new String[data.length]; // initialize string array of size data array
			// loop through each hex byte in data array, and convert it to equivalent string
			// form
			for (int i = 0; i < data.length; i++) {
				byte aByte = data[i];
				StringBuilder s = new StringBuilder();
				s.append(HEX[(0xf0 & aByte) >>> 4]); // read first 4 bits and convert to hex
				s.append(HEX[(0x0f & aByte)]); // read other half bits and convert to hex
				dataStr[i] = s.toString(); // append the hex of byte to array
			}

			int t = Integer.parseInt(dataStr[23], 16); // type of the packet
			type = t == 1 ? "ICMP" : t == 6 ? "TCP" : t == 17 ? "UDP" : dataStr[23];
			switch (type) { // switch types of packet and handle accordingly
			case "TCP":
				handleTCPPacket();
				break;
			case "UDP":
				handleUDPPacket();
				break;
			case "ICMP":
				handleICMPPacket();
				break;
			default:
				System.out.println("NO SUCH TYPE. TYPE: " + type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts an integer to 8 bit string, by padding extra 0's if needed at front
	 * 
	 * @param n integer whose 8 bit is to calculated
	 * @return 8 bit string representation of a number
	 */
	static String eightBitString(int n) {
		String bitString = Integer.toBinaryString(n);
		int size = bitString.length();
		StringBuilder s = new StringBuilder(8);
		for (int i = 0; i < 8 - size; i++)
			s.append('0');
		s.append(bitString);
		return s.toString();
	}

	/**
	 * Appends ETHER: at start of each string and prints its
	 * 
	 * @param s string to be printed with ETHER:
	 */
	static void printEtherString(String s) {
		System.out.println("ETHER:  " + s);
	}

	/**
	 * Appends IP: at start of each string and prints its
	 * 
	 * @param s string to be printed with IP:
	 */
	static void printIPString(String s) {
		System.out.println("IP:  " + s);
	}

	/**
	 * Appends TCP: at start of each string and prints its
	 * 
	 * @param s string to be printed with TCP:
	 */
	static void printTCPString(String s) {
		System.out.println("TCP:  " + s);
	}

	/**
	 * Appends UDP: at start of each string and prints its
	 * 
	 * @param s string to be printed with UDP:
	 */
	static void printUDPString(String s) {
		System.out.println("UDP:  " + s);
	}

	/**
	 * Appends ICMP: at start of each string and prints its
	 * 
	 * @param s string to be printed with ICMP:
	 */
	static void printICMPString(String s) {
		System.out.println("ICMP:  " + s);
	}

	/**
	 * Invoked when packet type is TCP
	 */
	static void handleTCPPacket() {
		printEtherHeaderDetails();
		printIPHeaderDetails();
		printTCPDetails();
		printDataDetails();
	}

	/**
	 * Invoked when packet type is UDP
	 */
	static void handleUDPPacket() {
		printEtherHeaderDetails();
		printIPHeaderDetails();
		printUDPDetails();
		printDataDetails();
	}

	/**
	 * Invoked when packet type is ICMP
	 */
	static void handleICMPPacket() {
		printEtherHeaderDetails();
		printIPHeaderDetails();
		printICMPDetails();
	}

	/**
	 * ETHER header is analyzed and printed from the data Data is fetched and
	 * processed from 0 to 13 index of dataStr array
	 */
	static void printEtherHeaderDetails() {
		printEtherString("----- Ether Header -----");
		printEtherString("");
		printEtherString("Packet size = " + dataStr.length + " bytes");

		String[] dest = Arrays.copyOfRange(dataStr, 0, 6);
		String destination = "Destination = ";
		for (String i : dest) {
			destination += i + ":";
		}
		printEtherString(destination.substring(0, destination.length() - 1));

		String[] src = Arrays.copyOfRange(dataStr, 6, 12);
		String source = "Source = ";
		for (String i : src) {
			source += i + ":";
		}
		printEtherString(source.substring(0, source.length() - 1));

		String[] type = Arrays.copyOfRange(dataStr, 12, 14);
		String typeStr = "Ethertype =  ";
		for (String i : type) {
			typeStr += i;
		}
		typeStr += " (IP)";
		printEtherString(typeStr);
		printEtherString("");
	}

	/**
	 * IP header is analyzed and printed from the data Data is fetched and processed
	 * from 14 to 33 index of dataStr array
	 */
	public static void printIPHeaderDetails() {
		printIPString("----- IP Header -----");
		printIPString("");
		printIPString("Version =  " + dataStr[14].substring(0, 1));
		int hLength = 4 * Integer.valueOf(dataStr[14].substring(1, 2));
		printIPString("Header length = " + hLength + " bytes");
		printIPString("Type of service = 0x" + dataStr[15]);
		printIPString("      xxx. .... = 0 (precedence)");
		String services = eightBitString(Integer.parseInt(dataStr[15], 16));
		if (services.charAt(3) == '0') {
			printIPString("      ...0 .... = normal delay");
		}
		if (services.charAt(4) == '0') {
			printIPString("      .... 0... = normal throughput");
		}
		if (services.charAt(5) == '0') {
			printIPString("      .... .0.. = normal reliability");
		}
		printIPString("Total length = " + Integer.valueOf(dataStr[17], 16) + " bytes");
		printIPString("Identification = " + Integer.valueOf(dataStr[18] + dataStr[19], 16));
		printIPString("Flags = 0x" + dataStr[20]);
		String flag = eightBitString(Integer.parseInt(dataStr[20], 16));
		if (flag.charAt(1) == '0') {
			printIPString("      .0.. .... = OK to fragment");
		}
		if (flag.charAt(1) == '1') {
			printIPString("      .1.. .... = do not fragment");
		}
		if (flag.charAt(2) == '0') {
			printIPString("      ..0. .... = last fragment");
		}
		printIPString("Fragment offset = " + Integer.parseInt(dataStr[21], 16) + " bytes");
		printIPString("Time to live = " + Integer.parseInt(dataStr[22], 16) + " seconds/hops");
		printIPString("Protocol = " + Integer.parseInt(dataStr[23], 16) + " (" + type + ")");
		printIPString("Header checksum = 0x" + dataStr[24] + dataStr[25]);
		String source = Integer.parseInt(dataStr[26], 16) + "." + Integer.parseInt(dataStr[27], 16) + "."
				+ Integer.parseInt(dataStr[28], 16) + "." + Integer.parseInt(dataStr[29], 16);
		printIPString("Source address = " + source);
		String destination = Integer.parseInt(dataStr[30], 16) + "." + Integer.parseInt(dataStr[31], 16) + "."
				+ Integer.parseInt(dataStr[32], 16) + "." + Integer.parseInt(dataStr[33], 16);
		printIPString("Destination address = " + destination);
		if (Integer.valueOf(dataStr[14].substring(1, 2)) > 5) {
			printIPString("Options (" + hLength + " bytes)");
		} else {
			printIPString("No options");
		}
		printIPString("");
	}

	/**
	 * TCP header is analyzed and printed from the data Data is fetched and
	 * processed from 34 to 53 index of dataStr array
	 */
	public static void printTCPDetails() {
		printTCPString("----- TCP Header -----");
		printTCPString("");
		printTCPString("Source port = " + Integer.valueOf(dataStr[34] + dataStr[35], 16));
		printTCPString("Destination port = " + Integer.valueOf(dataStr[36] + dataStr[37], 16));
		printTCPString("Sequence number = " + Long.valueOf(dataStr[38] + dataStr[39] + dataStr[40] + dataStr[41], 16));
		printTCPString(
				"Acknowledgement number = " + Long.valueOf(dataStr[42] + dataStr[43] + dataStr[44] + dataStr[45], 16));
		printTCPString("Data offset = " + Integer.parseInt((dataStr[46]).substring(0, 1), 16) + " bytes");
		printTCPString("Flags = 0x" + dataStr[47]);
		String flags = eightBitString(Integer.parseInt(dataStr[47], 16));
		if (flags.charAt(2) == '1') {
			printTCPString("      ..1. .... = Urgent pointer");
		} else {
			printTCPString("      ..0. .... = No Urgent pointer");
		}
		if (flags.charAt(3) == '1') {
			printTCPString("      ...1 .... = Acknowledgement");
		} else {
			printTCPString("      ...0 .... = No Acknowledgement");
		}
		if (flags.charAt(4) == '1') {
			printTCPString("      .... 1... = Push");
		} else {
			printTCPString("      .... 0... = No Push");
		}
		if (flags.charAt(5) == '1') {
			printTCPString("      .... .1.. = Reset");
		} else {
			printTCPString("      .... .0.. = No Reset");
		}
		if (flags.charAt(6) == '1') {
			printTCPString("      .... ..1. = Syn");
		} else {
			printTCPString("      .... ..0. = No Syn");
		}
		if (flags.charAt(7) == '1') {
			printTCPString("      .... ...1 = Fin");
		} else {
			printTCPString("      .... ...0 = No Fin");
		}
		printTCPString("Window = " + Integer.valueOf(dataStr[48] + dataStr[49], 16));
		printTCPString("Checksum = 0x" + dataStr[50] + dataStr[51]);
		printTCPString("Urgent pointer = " + Integer.valueOf(dataStr[52] + dataStr[53], 16));
		int hLength = 4 * Integer.valueOf(dataStr[14].substring(1, 2));
		if (Integer.valueOf(dataStr[14].substring(1, 2)) > 5) {
			printTCPString("Options (" + hLength + " bytes)");
		} else {
			printTCPString("No options");
		}
		printTCPString("");
	}

	/**
	 * UDP header is analyzed and printed from the data Data is fetched and
	 * processed from 34 to 41 index of dataStr array
	 */
	public static void printUDPDetails() {
		printUDPString("----- UDP Header -----");
		printUDPString("");
		printUDPString("Source port = " + Integer.valueOf(dataStr[34] + dataStr[35], 16));
		printUDPString("Destination port = " + Integer.valueOf(dataStr[36] + dataStr[37], 16));
		printUDPString("Length = " + Integer.valueOf(dataStr[38] + dataStr[39], 16));
		printUDPString("Checksum = 0x" + dataStr[40] + dataStr[41]);
		printUDPString("");
	}

	/**
	 * ICMP header is analyzed and printed from the data Data is fetched and
	 * processed from 34 to 37 index of dataStr array
	 */
	public static void printICMPDetails() {
		printICMPString("----- ICMP Header -----");
		printICMPString("");
		printICMPString("Type = " + Integer.parseInt(dataStr[34], 16) + " (Echo request)");
		printICMPString("Code = " + Integer.valueOf(dataStr[35], 16));
		printICMPString("Checksum =  0x" + dataStr[36] + dataStr[37]);
		printICMPString("");
	}

	/**
	 * DATA field header is analyzed and printed from the packet It is fetched and
	 * processed from 34 to x index of dataStr array Here, x is last index of the
	 * packet data
	 */
	public static void printDataDetails() {
		String typeStr = type + ":  ";
		System.out.println(typeStr + "Data: (first 64 bytes)");
		// a temp array containing just data
		String[] dataTemp = Arrays.copyOfRange(dataStr, 34, dataStr.length);
		// since each 16 bytes of data, stop after 64 bytes or if the bytes are over
		for (int a = 0; a < 4 && (a * 16) < dataTemp.length; a++) {
			StringBuilder hex = new StringBuilder(typeStr); // hex data from the file
			StringBuilder str = new StringBuilder(); // actual data
			// loop through until data is there to read
			for (int b = 0; b < 16 && (b + (a * 16)) < dataTemp.length; b++) {
				int actualIndex = b + (a * 16); // actual index for array
				hex.append(dataTemp[actualIndex]); // append hex data
				if (b % 2 != 0) { // append space after every 2 bytes
					hex.append(" ");
				}
				int cr = Integer.parseInt(dataTemp[actualIndex], 16); // decimal representation of hex
				if (cr >= 36 && cr <= 126) { // valid ascii to print
					str.append((char) cr); // append ascii char for the int
				} else {
					str.append('.'); // else append .
				}
			}
			// if there are any spaces left to fill for hex data, append spaces
			hex.append(" ".repeat((40 + typeStr.length()) - hex.length()));
			System.out.print(hex); // print the hex data
			System.out.print("\t");
			// if there are any spaces left to fill for ascii chars, append spaces
			str.append(".".repeat(16 - str.length()));
			System.out.println("'" + str + "'"); // print the ascii data
		}
	}
}