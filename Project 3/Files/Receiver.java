import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents the receiver, in the program
 * 
 * @author Bhavin Oza (bo2115@rit.edu)
 * 
 */
public class Receiver extends Thread {

	DatagramSocket soc; // spcket object
	ArrayList<Byte> fileData; //store data of file received in packets

	Receiver() {
		try {
			this.soc = new DatagramSocket(Utils.receivingPort); // instantiate socket with port
		} catch (SocketException e) {
			e.printStackTrace();
		}
		System.out.println("Receiver now active");
	}

	@Override
	public void run() {
		while (true) {
			DatagramPacket receiveDgPckt = new DatagramPacket(new byte[Utils.maxPacketSize], Utils.maxPacketSize);// datagrampacket
																													// obj
																													// to
																													// receive
																													// data
																													// through
																													// socket

			try {
				System.out.println("Receiving data packet.........");
				this.soc.receive(receiveDgPckt); // wait until data received
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Received data packet.........");

			CustomPacket customReceivedPckt = new CustomPacket(receiveDgPckt.getData()); // custompacket obj for storing
																							// received data
			System.out.println("Message Received: \t" + new String(customReceivedPckt.data));
			for (Byte b : customReceivedPckt.data) {
				fileData.add(b); //add data to be stored in the file
			}
			CustomPacket ackPckt = new CustomPacket(customReceivedPckt.seqAck, null); // custom ack packet to be sent
			DatagramPacket ackDgPckt = new DatagramPacket(ackPckt.allData.clone(), ackPckt.allData.length,
					receiveDgPckt.getAddress(), receiveDgPckt.getPort()); // datagram packet to be sent for ack
			try {
				System.out.println("Sending ack packet.........");
				this.soc.send(ackDgPckt); // send ACK datagram packet
				System.out.println("Sent ack packet........." + ackPckt.seqAck);
				if (Arrays.equals(customReceivedPckt.seqAck, Utils.toBytes(Utils.fin))) { // if it was FIN packet, full
																							// data is receieved
																							// now close socket
					this.soc.close();
					System.out.println("File transfer complete.");
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
	}
}
