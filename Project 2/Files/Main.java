import java.net.InetAddress;

class Main {

	public static void main(String args[]) {
		if (args.length > 0) {
			int nodeNum = Integer.parseInt(args[0]); //node number passed as parameter
			try {
				InetAddress broadCastIP = InetAddress.getByName("230.230.230.230"); // broadCast on IP, by joining
																					// socket group
				RoutingTable rt = new RoutingTable(); // a route table to store all routes
				rt.addRoute(new RouteEntry("10.0." + nodeNum + ".0", "255.255.255.0",
						InetAddress.getLocalHost().getHostAddress(), 0)); // add default route of the route
				Thread client = new Thread(new UdpMulticastClient(63001, broadCastIP, rt)); // client instance
				client.start(); // start the client
				Thread sender = new Thread(new UdpMulticastSender(63001, broadCastIP, nodeNum, rt)); // sender instance
				sender.start(); // start the sender
				while (true) {
					Thread.sleep(1000); // sleep for 1 second
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			System.out.println("No input args! Must specify Node Number!");
		}
	}
}
