import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UdpMulticastClient implements Runnable {

	public int port = 63001; // port number to listen on
	public InetAddress broadcastAddress; // multicast address to listen on
	public RoutingTable table; // instance of routing table for this client

	// constructor
	public UdpMulticastClient(int portNo, InetAddress bdIP, RoutingTable rTable) {
		port = portNo;
		broadcastAddress = bdIP;
		table = rTable;
	}

	// joins a group using socket, and parses and reads routing table sent in the
	// packet, received in UDP message
	public void receiveUDPMessage() throws IOException {
		byte[] buffer = new byte[1024]; // bytes array to store data

		// create and initialize the socket
		MulticastSocket socket = new MulticastSocket(port);
		socket.joinGroup(broadcastAddress);

		while (true) {
			try {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length); // creates a packet instance, to
																					// receive data in packets
				// wait for the next packet
				socket.receive(packet);
				// if the message is not from self
				if (!Utils.isSameAddressString(packet.getAddress(), InetAddress.getLocalHost())) {
					this.processReceivedDataPacket(packet); // process the received packet
					this.checkStatusOfOtherRouters(packet); // check if any routers offline, so update table accordingly
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	// called after Thread.start()
	@Override
	public void run() {
		try {
			receiveUDPMessage();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Processes the packet received in the message and reads the routing table from
	 * the packet and updates its local routing table accordingly
	 * 
	 * @param packetc packet received in message
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	void processReceivedDataPacket(DatagramPacket packet) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData()); // bytestream to read data bytes
		ObjectInputStream ois = new ObjectInputStream(bis); // objectstream to read data from byteinputstream
		RoutingTable receivedPacketTable = (RoutingTable) ois.readObject(); // parse the routing table from the
																			// objectstream
		boolean shouldPrintTable = false; // flag to track if there is any update in the current routing table, and
											// should it be printed
		for (RouteEntry currEntry : this.table.routes) { // loop through each of the routes in the current table
			if (Utils.isSameAddressString(currEntry.nextHop, packet.getAddress())) { // if any packets next hop, it the
																						// current router
				// get that router from the packet received table
				RouteEntry packetEntry = Utils.getEntryMatching(packet.getAddress(), receivedPacketTable);
				if (packetEntry != null) {
					if (packetEntry.metric == 16 && currEntry.metric != 16) {
						// check if the router is not reachable and update accordingly
						currEntry.metric = 16;
						shouldPrintTable = true;
					} else if (currEntry.metric != packetEntry.metric + 1) { // update the hop count
						currEntry.metric = packetEntry.metric + 1;
						shouldPrintTable = true;
					}
				}
			}
		}

		for (RouteEntry packetEntry : receivedPacketTable.routes) { // loop through each of the routes in the packet
																	// received table
			if (!Utils.isSameAddressString(packetEntry.nextHop, packet.getAddress())) { // if the routes next hop is not
																						// the current router (Simple
																						// Split Horizon)
				RouteEntry currEntry = Utils.getEntryMatching(packetEntry.ipAddress, this.table); // get same route from
																									// the
																									// current table
				if (currEntry == null) { // if there is no such route in current table
					// add the new route
					this.table.addRoute(
							new RouteEntry(packetEntry.ipAddress.getHostName(), packetEntry.subnetMask.getHostName(),
									packetEntry.nextHop.getHostName(), packetEntry.metric + 1));
					shouldPrintTable = true;
				} else if (currEntry.metric > packetEntry.metric + 1) { // compare the cost and update it accourdingly
																		// along
					// with the nextHop
					currEntry.metric = packetEntry.metric + 1;
					currEntry.nextHop = InetAddress.getByName(packet.getAddress().getHostName());
					shouldPrintTable = true;
				}
			}
		}
		if (shouldPrintTable) { // there are changes in the current routing table, so print it
			Utils.printRoutingTable(this.table);
		}
	}

	void checkStatusOfOtherRouters(DatagramPacket packet) {
		boolean isRouteDeleted = false;
		for (RouteEntry currEntry : this.table.routes) { // loop through each of the routes in the current table
			if ((System.currentTimeMillis() - currEntry.time) > 10000) { // check if anyroute is not updated in last 10
																			// seconds, then set as not reachable
				currEntry.metric = 16;
				currEntry.time = System.currentTimeMillis();
				isRouteDeleted = true;
				for (RouteEntry tempCurrEntry : this.table.routes) { // loop through each of the routes in the current
																		// table
					if (Utils.isSameAddressString(currEntry.ipAddress, tempCurrEntry.nextHop)) { // also update the
																									// routes,
						// where currEntry is nextHop
						tempCurrEntry.metric = 16;
						tempCurrEntry.time = System.currentTimeMillis();
					}
				}
			}
		}
		if (isRouteDeleted) { // some routers have been marked offline, so print the routing table
			Utils.printRoutingTable(this.table);
		}
	}
}
