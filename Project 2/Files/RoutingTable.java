import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents the routing table, storing different routes
 * 
 * @author Bhavin Oza (bo2115@rit.edu)
 * 
 */
public class RoutingTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2123249403479876730L;
	ArrayList<RouteEntry> routes = new ArrayList<>(); // list of route objects

	// add a route to the list
	void addRoute(RouteEntry e) {
		this.routes.add(e);
	}

	// ovverirdden toString, printing the whole routing table
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Address" + "\t\t" + "Next Hop" + "\t" + "Cost" + "\n");
		result.append("=================================================================\n");
		for (int i = 0; i < routes.size(); i++) {
			result.append(routes.get(i) + "\n");
		}
		return result.toString();
	}

}
