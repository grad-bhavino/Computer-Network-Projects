import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpMulticastSender implements Runnable {

	public int port = 63001; // port number to listen on
	public InetAddress broadcastAddress; // multicast address to send on
	public int node = 0; // the arbitrary node number of this executable
	public RoutingTable table; // instance of routing table for this client

	// standard constructor
	public UdpMulticastSender(int portNo, InetAddress bdIP, int nodeNo, RoutingTable rTable) {
		port = portNo;
		broadcastAddress = bdIP;
		node = nodeNo;
		table = rTable;
	}

	// sends a udp message, by broadcasting routing table in the packet
	public void sendUdpMessage() {
		try {
			DatagramSocket socket = new DatagramSocket(); // socket instance
			ByteArrayOutputStream bos = new ByteArrayOutputStream(); // bytestream to ouput data bytes
			ObjectOutputStream oos = new ObjectOutputStream(bos); // objectstream to output data from byteoutputstream
			oos.writeObject(table); // write the routing table object to the objectstream
			oos.flush();
			byte[] msg = bos.toByteArray(); // read the routing table as byte array
			// creates a packet instance, to send data in packets
			DatagramPacket packet = new DatagramPacket(msg, msg.length, broadcastAddress, port);
			socket.send(packet); // send packet
			socket.close(); // close socket
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// called after Thread.start()
	@Override
	public void run() {
		while (true) {
			try {
				sendUdpMessage();
				Thread.sleep(5000); // sleep for 5 seconds
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
