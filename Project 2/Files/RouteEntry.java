import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This class represents the route entry representing the route from 1
 * router to another router
 * 
 * @author Bhavin Oza (bo2115@rit.edu)
 * 
 */
public class RouteEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -716387686271527614L;
	InetAddress ipAddress; //ip address
	InetAddress subnetMask; //subnet mask
	InetAddress nextHop; //next hop address
	int metric; //cost
	long time; //last time entry

	// constructor
	public RouteEntry(String ip, String snMask, String nextHop, int metric) {
		try {
			this.ipAddress = InetAddress.getByName(ip);
			this.subnetMask = InetAddress.getByName(snMask);
			this.nextHop = InetAddress.getByName(nextHop);
			this.metric = metric;
			this.time = System.currentTimeMillis();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	//overriden toString method to print the route entry
	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append(this.ipAddress.getHostAddress() + "/");
		res.append(Utils.cidrString(this.subnetMask) + "\t\t");
		res.append(this.nextHop.getHostAddress() + "\t");
		res.append(this.metric);
		return res.toString();
	}
}
