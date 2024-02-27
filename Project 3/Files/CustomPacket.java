import java.util.Arrays;

/**
 * This class represents the custom packet used to store the header and data information from file
 * 
 * @author Bhavin Oza (bo2115@rit.edu)
 * 
 */
public class CustomPacket { // custom packet class

	// header field
	byte[] seqAck = new byte[2]; // Sequence and Acknowledgement num
	byte[] data; // data of packet
	byte[] allData; // data + header

	CustomPacket(byte[] seqack, byte[] sendData) {
		this.seqAck = seqack;
		this.data = sendData == null ? new byte[0] : sendData; // if no data make it null, else store data
		this.allData = new byte[Utils.headerSize + this.data.length];
		for (int i = 0; i < Utils.headerSize; i++) {
			this.allData[i] = seqAck[i];
		}
		for (int j = Utils.headerSize; j < (Utils.headerSize + data.length); j++) {
			this.allData[j] = data[j - Utils.headerSize];
		}
	}

	CustomPacket(byte[] packetData) { // store bytes to actual data fields of the packet
		this.seqAck = Arrays.copyOfRange(packetData, 0, Utils.headerSize);
		this.data = Arrays.copyOfRange(packetData, Utils.headerSize, packetData.length);
		this.allData = packetData;
	}

	public static void main(String[] args) {
	}
}
