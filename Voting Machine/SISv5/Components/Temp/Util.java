import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.net.SocketException;

class NetTool {
	/**
	 * If an address can be used for public access
	 * 
	 * @param inetAddress
	 *            an address
	 * @return public or not
	 */
	private static boolean isPublic(InetAddress inetAddress) {
		if (!inetAddress.isAnyLocalAddress()
				&& !inetAddress.isLinkLocalAddress()
				&& !inetAddress.isLoopbackAddress()
				&& !inetAddress.isMCGlobal() && !inetAddress.isMCLinkLocal()
				&& !inetAddress.isMCNodeLocal() && !inetAddress.isMCOrgLocal()
				&& !inetAddress.isMCSiteLocal()
				&& !inetAddress.isMulticastAddress()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get the public address of this machine
	 * 
	 * @return public address
	 */
	public static String getPublicAddress() {
		Enumeration<NetworkInterface> e;
		try {
			e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				Enumeration<InetAddress> addrs = e.nextElement()
						.getInetAddresses();
				while (addrs.hasMoreElements()) {
					InetAddress inetAddress = (InetAddress) addrs.nextElement();
					if (isPublic(inetAddress)
							&& inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			// System.out.println(e1.getMessage());
		}

		return null;
	}
}

class KeyValueList {
	// interal map for the message <property name, property value>, key and
	// value are both in String format
	private Map<String, String> map;

	// delimiter for encoding the message
	static final String delim = "$$$";

	// regex pattern for decoding the message
	static final String pattern = "\\$+";

	/*
	 * Constructor
	 */
	public KeyValueList() {
		map = new HashMap<>();
	}

	/*
	 * Add one property to the map
	 */
	public boolean putPair(String key, String value) {
		key = key.trim();
		value = value.trim();
		if (key == null || key.length() == 0 || value == null
				|| value.length() == 0) {
			return false;
		}
		map.put(key, value);
		return true;
	}

	public String removePair(String key) {
		return map.remove(key);
	}

	// /*
	// * extract a List containing all the input message IDs in Integer format
	// * (specifically designed for message 20)
	// */
	// public List<Integer> InputMessages() {
	// int i = 1;
	// List<Integer> list = new ArrayList<>();
	// String m = map.get("InputMsgID" + i);
	// while (m != null) {
	// list.add(Integer.parseInt(m));
	// ++i;
	// m = map.get("InputMsgID" + i);
	// }
	// return list;
	// }
	//
	// /*
	// * extract a List containing all the output message IDs in Integer format
	// * (specifically designed for message 20)
	// */
	// public List<Integer> OutputMessages() {
	// int i = 1;
	// List<Integer> list = new ArrayList<>();
	// String m = map.get("OutputMsgID" + i);
	// while (m != null) {
	// list.add(Integer.parseInt(m));
	// ++i;
	// m = map.get("OutputMsgID" + i);
	// }
	// return list;
	// }

	/*
	 * encode the KeyValueList into a String
	 */
	/*
	 * encode the KeyValueList into a String
	 */
	public String encodedString() {

		StringBuilder builder = new StringBuilder();
		builder.append("(");
		for (Entry<String, String> entry : map.entrySet()) {
			builder.append(entry.getKey() + delim + entry.getValue() + delim);
		}
		// X$$$Y$$$, minimum
		builder.append(")");
		return builder.toString();
	}

	/*
	 * decode a message in String format into a corresponding KeyValueList
	 */
	public static KeyValueList decodedKV(String message) {
		KeyValueList kvList = new KeyValueList();

		String[] parts = message.split(pattern);
		int validLen = parts.length;
		if (validLen % 2 != 0) {
			--validLen;
		}
		if (validLen < 1) {
			return kvList;
		}

		for (int i = 0; i < validLen; i += 2) {
			kvList.putPair(parts[i], parts[i + 1]);
		}
		return kvList;
	}

	/*
	 * get the property value based on property name
	 */
	public String getValue(String key) {
		String value = map.get(key);
		if (value != null) {
			return value;
		}else{
			return "";
		}
	}

	/*
	 * get the number of properties
	 */
	public int size() {
		return map.size();
	}

	/*
	 * toString for printing
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n");
		for (Entry<String, String> entry : map.entrySet()) {
			builder.append(entry.getKey() + " : " + entry.getValue() + "\n");
		}
		return builder.toString();
	}
}

/**************************************************
 * Class MsgEncoder:
 * Serialize the KeyValue List and Send it out to a Stream.
 ***************************************************/
class MsgEncoder {

	// used for writing Strings
	private PrintStream writer;

	/*
	 * Constructor
	 */
	public MsgEncoder(OutputStream out) throws IOException {
		writer = new PrintStream(out);
	}

	/*
	 * encode the KeyValueList that represents a message into a String and send
	 */
	public void sendMsg(KeyValueList kvList) throws IOException {
		if (kvList == null || kvList.size() < 1) {
			return;
		}

		writer.println(kvList.encodedString());
		writer.flush();
	}
}

/**************************************
 * Class MsgDecoder:
 * Get String from input Stream and reconstruct it to
 * a Key Value List.
 ***************************************/

class MsgDecoder {
	// used for reading Strings
	private BufferedReader reader;

	/*
	 * Constructor
	 */
	public MsgDecoder(InputStream in) throws IOException {
		reader = new BufferedReader(new InputStreamReader(in));
	}

	/*
	 * read and decode the message into KeyValueList
	 */
	public KeyValueList getMsg() throws Exception {
		KeyValueList kvList = new KeyValueList();
		StringBuilder builder = new StringBuilder();

		String message = reader.readLine();

		if (message != null && message.length() > 2) {

			builder.append(message);

			while (message != null && !message.endsWith(")")) {
				message = reader.readLine();
				builder.append("\n" + message);
			}

			kvList = KeyValueList
					.decodedKV(builder.substring(1, builder.length() - 1));
		}
		return kvList;
	}
}
