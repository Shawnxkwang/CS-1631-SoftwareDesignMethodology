import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SISServer is used for re-routing messages from some components
 * (must be allowed in their definition) to other components
 * that need those messages (according to their definition)
 */
public class SISServer {

	// internal routing table for each component
	// key is the name of a component, see ComponentInfo for details

	static int port = 53217;

	static Map<ComponentInfo, ComponentConnection> mapping = new ConcurrentHashMap<>();

	// private static Scope scope;

	// static boolean addScope(String newScope) {
	// String[] parts = newScope.split(".");
	// if (!parts[0].equals(scope.current)) {
	// return false;
	// }
	// return addS(scope, parts, 1, parts.length);
	// }

	// private static boolean addS(Scope s, String[] parts, int index, int len)
	// {
	// if (index == len) {
	// return true;
	// }
	// String candidate = parts[index];
	// Scope sub = s.children.get(candidate);
	// if (sub != null) {
	// return addS(sub, parts, index + 1, len);
	// } else if (index == parts.length - 1) {
	// s.children.put(candidate, new Scope(candidate));
	// return true;
	// } else {
	// return false;
	// }
	// }

	static String getTopScope() {
		return "SIS";
		// return scope.current;
	}

	public static void main(String[] args) {

		// thread pool for handling connections to components
		ExecutorService service = Executors.newCachedThreadPool();
		// server socket that accepts new connections
		ServerSocket serverSocket = null;

		try {
			// scope.current = "Top";
			serverSocket = new ServerSocket(port);
			System.out.println("SISServer starts, waiting for new components");
			service.execute(new SISPullTask());
			while (true) {
				// initialize a secondary task for each
				// new connection in the thread pool
				service.execute(new SISTask(serverSocket.accept()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

/**
 * ComponentInfo represents a unique composite key
 * that can identify a component
 * 
 * @author dexterchen
 *
 */
class ComponentInfo {
	String scope;
	ComponentType componentType;
	String name;
	String incomingMessages;
	String outgoingMessages;

	public ComponentInfo(String s, ComponentType t, String n) {
		// TODO Auto-generated constructor stub
		scope = s;
		componentType = t;
		name = n;
	}

	public String getIncomingMessages() {
		return incomingMessages;
	}

	public void setIncomingMessages(String incomingMessages) {
		this.incomingMessages = incomingMessages;
	}

	public String getOutgoingMessages() {
		return outgoingMessages;
	}

	public void setOutgoingMessages(String outgoingMessages) {
		this.outgoingMessages = outgoingMessages;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		ComponentInfo info = (ComponentInfo) obj;
		return info.name.equals(name) && info.scope.equals(scope);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		int result = HashCodeUtil.hash(HashCodeUtil.SEED, scope);
		result = HashCodeUtil.hash(result, name);
		return result;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		builder.append("\nScope: " + scope + "\n");
		builder.append("Component Type: " + componentType + "\n");
		builder.append("Name: " + name + "\n");
		return builder.toString();
	}
}

/**
 * ComponentType represents a possible type of a component
 * 
 * @author dexterchen
 *
 */
enum ComponentType {
	Basic, Controller, Monitor, Advertiser, Debugger
}

/**
 * 
 * ComponentConnection represents connections
 * that are related to a component
 * see MsgEncoder, MsgDecoder, KeyValueList for details
 * 
 * @author dexterchen
 *
 */
class ComponentConnection {
	// message writer for a component
	MsgEncoder encoder;
	// message reader for a component
	MsgDecoder decoder;

	public ComponentConnection() {
		// TODO Auto-generated constructor stub
	}

	public ComponentConnection(MsgEncoder e, MsgDecoder d) {
		// TODO Auto-generated constructor stub
		encoder = e;
		decoder = d;
	}
}

// class Scope {
// String current;
// Map<String, Scope> children;
//
// public Scope(String c) {
// // TODO Auto-generated constructor stub
// current = c;
// children = new ConcurrentHashMap<>();
// }
// }
