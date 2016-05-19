import java.io.File;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class RemoteContent extends GridPane {

	@FXML
	TextField sIP;

	@FXML
	TextField sPort;

	@FXML
	TextField rRate;

	@FXML
	TextField scope;

	@FXML
	ToggleButton connect;

	@FXML
	ListView<Message> messages;

	@FXML
	Button reset;

	@FXML
	Button send;

	@FXML
	Button load;

	@FXML
	Button clear;

	@FXML
	TableView<Field> toSend;

	@FXML
	TableColumn<Field, String> toSendKey;

	@FXML
	TableColumn<Field, String> toSendValue;

	@FXML
	TableView<Field> received;

	@FXML
	TableColumn<Field, String> receivedKey;

	@FXML
	TableColumn<Field, String> receivedValue;

	@FXML
	TitledPane toSendTitle;

	@FXML
	TitledPane receivedTitle;

	@FXML
	TitledPane messagesTitle;

	// socket for connection to SISServer
	private static Socket universal;
	// message writer
	private static MsgEncoder encoder;
	// message reader
	private static MsgDecoder decoder;
	// scope of this component
	// private static final String SCOPE = "SIS";
	// name of this component
	private static final String NAME = "Remote";
	
	private static final int PORT = 53217;

	private Dialog<String> dialog = new Dialog<>();

	// messages types that can be handled by this component

	// private static final List<String> TYPES = new ArrayList<String>(
	// Arrays.asList(new String[] { "Setting", "Confirm", "Reading",
	// "Alert", "Connect", "Register", "Emergency" }));

	// private KeyValueList recKV = new KeyValueList();

	private Stage primaryStage;

	private static ObservableList<Message> data = FXCollections
			.observableArrayList();

	private static ObservableList<Field> toSendData = FXCollections
			.observableArrayList();

	private static ObservableList<Field> receivedData = FXCollections
			.observableArrayList();

	private static DebuggerProc proc;

	public RemoteContent(Stage primaryStage) {
		this.primaryStage = primaryStage;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"Remote.fxml"));

		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
			setServerIP("127.0.0.1");
			setServerPort(PORT + "");
			setRefreshRate("3000");
			setScope("SIS");

			dialog.getDialogPane().getButtonTypes()
					.add(new ButtonType("OK", ButtonData.OK_DONE));

			toSend.setItems(toSendData);
			received.setItems(receivedData);

			messages.setItems(data);
			toSend.placeholderProperty().set(
					new Label("No message loaded / recognized"));
			received.placeholderProperty().set(
					new Label("No message loaded / recognized"));

			messages.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {

				public ListCell<Message> call(ListView<Message> param) {
					final ListCell<Message> cell = new ListCell<Message>() {
						@Override
						public void updateItem(Message item, boolean empty) {
							super.updateItem(item, empty);
							if (item != null) {
								setText(item.getName());
								setTooltip(new Tooltip(item.getPath()));
							}
						}
					}; // ListCell
					return cell;
				}
			}); // setCellFactory

			toSendKey
					.setCellValueFactory(new PropertyValueFactory<Field, String>(
							"key"));
			toSendValue
					.setCellValueFactory(new PropertyValueFactory<Field, String>(
							"value"));

			receivedKey
					.setCellValueFactory(new PropertyValueFactory<Field, String>(
							"key"));
			receivedValue
					.setCellValueFactory(new PropertyValueFactory<Field, String>(
							"value"));

			// messages.setOnMouseClicked(new EventHandler<Event>() {
			//
			// @Override
			// public void handle(Event event) {
			// ObservableList<Message> selectedItems = messages
			// .getSelectionModel().getSelectedItems();
			//
			// if (selectedItems.size() == 1) {
			// // TABLEVIEW
			//
			// try {
			// String path = selectedItems.get(0).getPath();
			// KeyValueList kvList = XMLUtil.extractToKV(new File(
			// path));
			// List<String> table = kvList.tableOutput();
			//
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
			//
			// }
			//
			// });

			messages.getSelectionModel().selectedItemProperty()
					.addListener(new ChangeListener<Message>() {
						public void changed(
								ObservableValue<? extends Message> observable,
								Message oldValue, Message newValue) {
							toSendData.clear();

							try {
								if (observable != null
										&& observable.getValue() != null) {
									String path = observable.getValue()
											.getPath();
									KeyValueList kvList = XMLUtil
											.extractToKV(new File(path));

									List<String> table = kvList.tableOutput();
									for (String string : table) {
										String[] parts = string.split(" ");
										toSendData.add(new Field(parts[0],
												parts[1]));
									}
								}

							} catch (Exception e) {
								// TODO Auto-generated catch block
								// data.remove(newValue);
								// messages.setItems(null);
								// messages.setItems(data);
							}
						}
					});

			messages.getSelectionModel().setSelectionMode(
					SelectionMode.MULTIPLE);

			// toSend.focusedProperty().addListener(new
			// ChangeListener<Boolean>() {
			//
			// @Override
			// public void changed(
			// ObservableValue<? extends Boolean> observable,
			// Boolean oldValue, Boolean newValue) {
			// // TODO Auto-generated method stub
			// if (newValue) {
			// toSendTitle
			// .setText("Message To Send (Hit 'S' To Save)");
			// } else {
			// toSendTitle.setText("Message To Send");
			// }
			// }
			// });
			//
			// received.focusedProperty().addListener(
			// new ChangeListener<Boolean>() {
			//
			// @Override
			// public void changed(
			// ObservableValue<? extends Boolean> observable,
			// Boolean oldValue, Boolean newValue) {
			// // TODO Auto-generated method stub
			// if (newValue) {
			// receivedTitle
			// .setText("Message Received (Hit 'S' To Save)");
			// } else {
			// receivedTitle.setText("Message Received");
			// }
			// }
			// });

			// messages.setCellFactory(Message::getName);

		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}

	}

	/*
	 * used for connect(reconnect) to SISServer
	 */
	private Socket connect() throws Exception {
		Socket socket = new Socket(getServerIP(),
				Integer.parseInt(getServerPort()));
		return socket;
	}

	public void handlerConnect() {
		boolean selected = connect.selectedProperty().get();
		if (selected) {
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

				proc = new DebuggerProc();

				KeyValueList reg = new KeyValueList();

				reg.putPair("Scope", getScope());
				reg.putPair("MessageType", "Register");
				reg.putPair("Role", "Debugger");
				reg.putPair("Name", NAME);
				encoder.sendMsg(reg);

				KeyValueList conn = new KeyValueList();
				conn.putPair("Scope", getScope());
				conn.putPair("MessageType", "Connect");
				conn.putPair("Role", "Debugger");
				conn.putPair("Name", NAME);
				encoder.sendMsg(conn);

				// timer.schedule(new TimerTask() {
				//
				// @Override
				// public void run() {
				//
				// }
				// }, 0, Long.parseLong(getRefreshRate()));
				send.setDisable(false);
				connect.setText("Connected");
				connect.setTextFill(Color.GREEN);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				connect.setSelected(false);
				proc.close();

				send.setDisable(true);
			}
		} else {
			try {
				send.setDisable(true);
				proc.close();
				// universal.close();
				connect.setText("Connect");
				connect.setTextFill(Color.BLACK);
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
			connect.setSelected(false);
		}
	}

	public void handlerReset() {
		setServerIP("127.0.0.1");
		setServerPort(PORT + "");
		setScope("SIS");
		setRefreshRate("3000");
	}

	public void handlerClear() {
		data.clear();
	}

	public void handlerSend() {
		ObservableList<Message> selectedItems = messages.getSelectionModel()
				.getSelectedItems();
		// System.out.println(selectedItems.size());
		for (Message message : selectedItems) {
			String path = message.getPath();

			try {
				KeyValueList kvList = XMLUtil.extractToKV(new File(path));
				encoder.sendMsg(kvList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				// data.remove(message);
				dialog.setTitle("Error");
				dialog.setHeaderText("Fail to send message");
				dialog.setContentText(message.getName() + "\n\n"
						+ message.getPath());
				dialog.showAndWait();
			}
		}
	}

	public void handlerLoad() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load XML messages / Script");
		List<String> exts = new ArrayList<String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 5932481899696458622L;

			{
				add("*.xml");
				add("*.txt");
			}
		};
		fileChooser
				.setInitialDirectory(new File(System.getProperty("user.dir")));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("XML, TXT", exts));
		List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);

		if (files != null) {
			for (File file : files) {
				String extension = file.getName().toLowerCase();
				if (extension.endsWith(".xml")) {
					loadOneXML(file, null);
				} else if (extension.endsWith(".txt")) {
					try {
						List<String> paths = Files.readAllLines(FileSystems
								.getDefault().getPath(file.getAbsolutePath()));
						for (String p : paths) {
							File temp = new File(p);
							String ext = temp.getName().toLowerCase();
							if (ext.endsWith(".xml")) {
								loadOneXML(temp, null);
							}
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}
			}
		}
	}

	private void loadOneXML(File file, File base) {
		if (!file.exists()) {
			String prefix = base.getParent();
			prefix += "\\" + file.getName();
			file = new File(prefix);
			if (!file.exists()) {
				return;
			}
		}
		Message aMessage = new Message(file.getName(), file.getAbsolutePath());
		if (!data.contains(aMessage)) {
			data.add(aMessage);
		}
	}

	public void handlerToSendSave() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save XML message to be sent");
		fileChooser
				.setInitialDirectory(new File(System.getProperty("user.dir")));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("XML", "*.xml"));
		File loc = fileChooser.showSaveDialog(primaryStage);
		if (loc != null) {
			KeyValueList kvList = new KeyValueList();
			for (Field field : toSendData) {
				kvList.putPair(field.getKey(), field.getValue());
			}
			try {
				if (kvList.size() > 0) {
					XMLUtil.saveKVToFile(kvList, loc);
				} else {
					dialog.setTitle("Warning");
					dialog.setHeaderText("Invalid message");
					dialog.setContentText("No message recognized");
					dialog.showAndWait();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block

			}
		}
	}

	// public void handlerToSendKeyPressed(final KeyEvent keyEvent) {
	// if (toSend.focusedProperty().get() && keyEvent.getCode() == KeyCode.S) {
	// keyEvent.consume();
	// FileChooser fileChooser = new FileChooser();
	// fileChooser.setTitle("Save XML message to be sent");
	// fileChooser.setInitialDirectory(new File(System
	// .getProperty("user.dir")));
	// fileChooser.getExtensionFilters().addAll(
	// new FileChooser.ExtensionFilter("XML", "*.xml"));
	// File loc = fileChooser.showSaveDialog(primaryStage);
	// if (loc != null) {
	// KeyValueList kvList = new KeyValueList();
	// for (Field field : toSendData) {
	// kvList.putPair(field.getKey(), field.getValue());
	// }
	// XMLUtil.saveKVToFile(kvList, loc);
	// }
	// }
	// }

	public void handlerReceivedSave() {
		proc.pause();
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save XML message received");
		fileChooser
				.setInitialDirectory(new File(System.getProperty("user.dir")));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("XML", "*.xml"));
		File loc = fileChooser.showSaveDialog(primaryStage);
		if (loc != null) {
			KeyValueList kvList = new KeyValueList();
			for (Field field : receivedData) {
				kvList.putPair(field.getKey(), field.getValue());
			}
			try {
				if (kvList.size() > 0) {
					XMLUtil.saveKVToFile(kvList, loc);
				} else {
					dialog.setTitle("Warning");
					dialog.setContentText("No message recognized");
					dialog.showAndWait();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		}
		proc.resume();
	}

	// public void handlerReceivedKeyPressed(final KeyEvent keyEvent) {
	// if (received.focusedProperty().get() && keyEvent.getCode() == KeyCode.S)
	// {
	// keyEvent.consume();
	// FileChooser fileChooser = new FileChooser();
	// fileChooser.setTitle("Save XML message received");
	// fileChooser.setInitialDirectory(new File(System
	// .getProperty("user.dir")));
	// fileChooser.getExtensionFilters().addAll(
	// new FileChooser.ExtensionFilter("XML", "*.xml"));
	// File loc = fileChooser.showSaveDialog(primaryStage);
	// if (loc != null) {
	// KeyValueList kvList = new KeyValueList();
	// for (Field field : receivedData) {
	// kvList.putPair(field.getKey(), field.getValue());
	// }
	// XMLUtil.saveKVToFile(kvList, loc);
	// }
	// }
	// }

	public void handlerMessagesHint() {
		messagesTitle.setText("Hold 'Ctrl' To Select");
	}

	public void handlerMessagesHintExit() {
		messagesTitle.setText("Message(s)");
	}

	public String getServerIP() {
		return serverIPProperty().get();
	}

	public void setServerIP(String value) {
		serverIPProperty().set(value);
	}

	public StringProperty serverIPProperty() {
		return sIP.textProperty();
	}

	public String getServerPort() {
		return serverPortProperty().get();
	}

	public void setServerPort(String value) {
		serverPortProperty().set(value);
	}

	public StringProperty serverPortProperty() {
		return sPort.textProperty();
	}

	public String getRefreshRate() {
		return refreshRateProperty().get();
	}

	public void setRefreshRate(String value) {
		refreshRateProperty().set(value);
	}

	public StringProperty refreshRateProperty() {
		return rRate.textProperty();
	}

	public String getScope() {
		return scopeProperty().get();
	}

	public void setScope(String value) {
		scopeProperty().set(value);
	}

	public StringProperty scopeProperty() {
		return scope.textProperty();
	}

	class DebuggerProc {
		private Thread thread;
		private DebuggerTask debuggerTask;

		public DebuggerProc() {
			debuggerTask = new DebuggerTask();
			thread = new Thread(debuggerTask);
			thread.start();
		}

		public void pause() {
			debuggerTask.pause();
		}

		public void resume() {
			debuggerTask.resume();
		}

		public void close() {
			try {
				debuggerTask.terminate();
				thread.join();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	class DebuggerTask extends Task<Void> {

		private volatile boolean running = true;
		private volatile boolean pause = false;

		public void terminate() throws Exception {
			// TODO Auto-generated method stub
			universal.shutdownInput();
			universal.shutdownOutput();
			universal.close();
			running = false;
		}

		public void pause() {
			pause = true;
		}

		public void resume() {
			pause = false;
		}

		@Override
		protected Void call() throws Exception {
			// TODO Auto-generated method stub
			while (running) {
				if (!pause) {
					try {
						KeyValueList msg = decoder.getMsg();
						if (msg != null) {
							receivedData.clear();
							List<String> table = msg.tableOutput();
							for (String string : table) {
								String[] parts = string.split(" ");
								receivedData.add(new Field(parts[0], parts[1]));
							}
						}
						Thread.sleep(Long.parseLong(getRefreshRate()));
						// TABLEVIEW
					} catch (Exception e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
						break;
					}
				}
			}
			return null;
		}
	}

}
