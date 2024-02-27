import java.net.InetAddress;

/**
 * This class represents utility file to provide suppport methods for the
 * program
 * 
 * @author Bhavin Oza (bo2115@rit.edu)
 * 
 */
public class Utils {

	// compare two address and return boolean of they are equal or not
	static boolean isSameAddressString(InetAddress a, InetAddress b) {
		return a.getHostAddress().equals(b.getHostAddress());
	}

	// get a route entry matching the address from given table
	static RouteEntry getEntryMatching(InetAddress packetAdd, RoutingTable table) {
		RouteEntry entry = null;
		for (int i = 0; i < table.routes.size(); i++) {
			if (isSameAddressString(packetAdd, table.routes.get(i).ipAddress)) {
				entry = table.routes.get(i);
				break;
			}
		}
		return entry;
	}

	// cidr for given subnet mask
	static int cidrString(InetAddress s) {
		byte[] maskBytesArr = s.getAddress(); // get address as bytes
		int resultCidr = 0;
		for (byte b : maskBytesArr) {// loop through each byte
			int a = 0x80; // decimal 128
			for (int i = 0; i < 8; i++) { // loop for each bit
				if ((b & a) != 0) { // performing
					resultCidr++; // increment resultCidr
				}
				a = a >>> 1; // decrement a by 2^n-1
			}
		}
		return resultCidr; // return computed cidr
	}

	// print the routing table
	static void printRoutingTable(RoutingTable table) {
		System.out.println("===================================Routing Table===================================");
		System.out.println(table);
		System.out.println("===================================================================================");
	}
}
