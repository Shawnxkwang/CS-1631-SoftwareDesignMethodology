
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class CreateGUI extends Application {

	private ObservableMap<String, KeyValueList> map = FXCollections
			.observableHashMap();

	private Proc pro = new Proc(map);
	
	public static final String SCOPE = "SIS.Scope1";
	// name of this component
    public static final String NAME = "GUI";

	@Override
	public void start(Stage primaryStage) {
		try {
			SISFlow root = new SISFlow(map);
			//ScrollPane root = new ScrollPane();
			Scene scene = new Scene(root);

			// scene.getStylesheets().add(
			// getClass().getResource("application.css").toExternalForm());

			// CustomControl customControl = new CustomControl();

			// timer.schedule(new TimerTask() {
			//
			// @Override
			// public void run() {
			// // TODO Auto-generated method stub
			// customs.add(new CustomControl());
			// }
			// }, new Date(), 500);

			

			/*root.viewportBoundsProperty().addListener(
					new ChangeListener<Bounds>() {
						@Override
						public void changed(
								ObservableValue<? extends Bounds> bounds,
								Bounds oldBounds, Bounds newBounds) {
							flow.setPrefWidth(newBounds.getWidth());
							flow.setPrefHeight(newBounds.getHeight());
						}
					});

			root.setContent(flow);*/

			primaryStage.setScene(scene);
			primaryStage.setMinWidth(515);
			primaryStage.setMinHeight(500);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
		pro.close();
	}

	public static void main(String[] args) {

		launch(args);
	}
}

class MonitorTask implements Runnable {
	private volatile boolean running = true;

	// socket for connection to SISServer
	private Socket universal;
	private static int port = 53217;
	// message writer
	private MsgEncoder encoder;
	// message reader
	private MsgDecoder decoder;
	// scope of this component
	//private final String SCOPE = "SIS.Scope1";
	// messages types that can be handled by this component
	private final List<String> TYPES = new ArrayList<String>(
			Arrays.asList(new String[] { "Reading", "Alert", "Confirm",
					"Connect" }));

	private ObservableMap<String, KeyValueList> map;

	public MonitorTask(ObservableMap<String, KeyValueList> m) {
		map = m;
	}

	public void terminate() {
		running = false;
		try {
			universal.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Close the connection");
			// e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		runTask();
	}

	public void runTask() {
		while (running) {
			try {
				// try to establish a connection to SISServer
				universal = connect();

				// bind the message reader to inputstream of the socket
				decoder = new MsgDecoder(universal.getInputStream());
				// bind the message writer to outputstream of the socket
				encoder = new MsgEncoder(universal.getOutputStream());
		
				/*
				 * construct a Connect message to establish the connection
				 */
				KeyValueList conn = new KeyValueList();
				conn.putPair("Scope", CreateGUI.SCOPE);
				conn.putPair("MessageType", "Connect");
				conn.putPair("Role", "Monitor");
				conn.putPair("Name", CreateGUI.NAME);
				encoder.sendMsg(conn);

				// KeyValueList for inward messages, see KeyValueList for
				// details

				while (running) {
					ProcessMsg(decoder.getMsg());
				}

			} catch (Exception e) {
				// if anything goes wrong, try to re-establish the connection
				// e.printStackTrace();
				if (running) {
					try {
						// wait for 1 second to retry
						Thread.sleep(1000);
					} catch (InterruptedException e2) {
					}
					System.out.println("Try to reconnect");
					try {
						universal = connect();
					} catch (IOException e1) {
					}
				}
			}
		}
	}

	/*
	 * used for connect(reconnect) to SISServer
	 */
	private Socket connect() throws IOException {
		Socket socket = new Socket("127.0.0.1", port);
		return socket;
	}

	private void ProcessMsg(KeyValueList kvList) throws Exception {

		String scope = kvList.getValue("Scope");
		if (!CreateGUI.SCOPE.startsWith(scope)) {
			return;
		}

		String messageType = kvList.getValue("MessageType");
		if (!TYPES.contains(messageType)) {
			return;
		}

		String sender = kvList.getValue("Sender");

		String receiver = kvList.getValue("Receiver");

		String purpose = kvList.getValue("Purpose");

		switch (messageType) {
		case "Connect":
			String name = kvList.getValue("Name");
			String role = kvList.getValue("Role");
			if (!name.equals(CreateGUI.NAME)&&!role.equals("Monitor")) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						// if you change the UI, do it here !
						kvList.removePair("Scope");
						map.put(name, kvList);
					}
				});
			}
			break;
		case "Alert":
		case "Reading":

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					// if you change the UI, do it here !
					kvList.removePair("Scope");
					map.put(sender, kvList);
				}
			});

			break;
		case "Confirm":
			System.out.println("Connect to SISServer successful.");
			break;
		}
	}
}

class Proc {

	private Thread thread;

	private MonitorTask monitorTask;

	public Proc(ObservableMap<String, KeyValueList> m) {
		monitorTask = new MonitorTask(m);
		thread = new Thread(monitorTask);
		thread.start();
	}

	public void close() {

		try {
			monitorTask.terminate();
			thread.join();
			// universal.shutdownInput();
			// universal.shutdownOutput();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
