import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * This class represents the sender, in the program
 * 
 * @author Bhavin Oza (bo2115@rit.edu)
 * 
 */
public class Sender extends Thread {

	DatagramSocket soc; // socket object
	InetAddress receiverIP; // receiver IP
	byte[] fileData; // data of file to be sent

	Sender(String fileName, String receiverIP) {
		try {
			this.soc = new DatagramSocket(); // instatiate socket
			this.fileData = Files.readAllBytes(Paths.get(fileName)); // read data of file
			this.receiverIP = InetAddress.getByName(receiverIP);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Sender now active");
	}

	@Override
	public void run() {
		int maxFileData = Utils.maxPacketSize - Utils.headerSize;
		for (int seq = 0; seq < this.fileData.length; seq += maxFileData) { // send data of file in packets of
																			// maxPacketSize
			if (seq + maxFileData >= fileData.length) { // if there less data than maxPacketSize
				maxFileData = this.fileData.length - seq;
			}
			this.sendData(Utils.toBytes(seq), Arrays.copyOfRange(fileData, seq, seq + maxFileData)); //send tge data
		}

		this.sendData(Utils.toBytes(Utils.fin), null); //send the fin 
		this.soc.close();
		System.out.println("File data sent completely.");
	}

	void sendData(byte[] seq, byte[] dataToSend) {//sequence no of packet and data to be sent in packet
		CustomPacket customSendPckt = new CustomPacket(seq, dataToSend); //creaet custom packet
		try {
			System.out.println("Sending data packet.........");
			DatagramPacket dgPckt = new DatagramPacket(customSendPckt.allData.clone(), customSendPckt.allData.length,
					receiverIP, Utils.receivingPort); //datagrampacket obj to send through socket
			this.soc.send(dgPckt);
			System.out.println("Sent data packet........." + customSendPckt.seqAck);

			boolean ackReceived = false; //track if the ACK is received for recent sent packet
			while (ackReceived == false) {
				byte[] ack = new byte[Utils.headerSize];
				DatagramPacket ackPckt = new DatagramPacket(ack, ack.length); //datagrampacket obj to store ACK packet
				System.out.println("Waiting for ack packet.........");
				this.soc.receive(ackPckt); //receive ack packet
				System.out.println("Ack packet received........." + seq);
				CustomPacket receivedAckPckt = new CustomPacket(ackPckt.getData());
				if (Arrays.equals(receivedAckPckt.seqAck, customSendPckt.seqAck)) { //ACK received
					ackReceived = true;
					System.out.println("Ack and Seq matches........." + seq);
					break;
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
	}
}
