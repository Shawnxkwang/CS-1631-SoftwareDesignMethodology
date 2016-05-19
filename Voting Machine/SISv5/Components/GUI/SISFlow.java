
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ScrollPane;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;


public class SISFlow extends GridPane {
	@FXML
	FlowPane flow;
	
	@FXML
	ScrollPane scroll;
	
	@FXML
	Button change;
	
	@FXML
	TextField email;

	@FXML
	Button switcha;
	
	@FXML
	TextField userID;
	
	ObservableMap<String, KeyValueList> map;
	
	Socket universal;
	static int port = 53217;
	MsgEncoder encoder;
	
	private Pattern pattern;
	private Matcher matcher;
	
	private static final String EMAIL_PATTERN = 
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	// ListView<CustomControl> listView = new ListView<CustomControl>();

	public SISFlow(ObservableMap<String, KeyValueList> m) {
		// TODO Auto-generated constructor stub
		//super();
		map = m;
		pattern = Pattern.compile(EMAIL_PATTERN);
		//flow.setVgap(5);
		//flow.setHgap(5);

		//flow.setCache(true);
		//flow.setCacheShape(true);
		//flow.setCacheHint(CacheHint.SPEED);
		
		//getChildren()
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"SISFlow.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		
		try{
		fxmlLoader.load();
		
		/*scroll.viewportBoundsProperty().addListener(
					new ChangeListener<Bounds>() {
						@Override
						public void changed(
								ObservableValue<? extends Bounds> bounds,
								Bounds oldBounds, Bounds newBounds) {
							flow.setPrefWidth(newBounds.getWidth());
							//flow.setPrefHeight(newBounds.getHeight());
						}
					});*/

		map.addListener(new MapChangeListener<String, KeyValueList>() {

			@Override
			public void onChanged(
					MapChangeListener.Change<? extends String, ? extends KeyValueList> change) {
				// TODO Auto-generated method stub
				if (change.wasAdded()) {

					Supplier<Stream<Node>> supplier = () -> flow.getChildren()
							.stream()
							.parallel()
							.filter(x -> ((CustomControl) x).getTitle().equals(
									change.getKey()));

					// .collect(Collectors.toList());
					if (supplier.get().count() == 0) {
						Platform.runLater(() -> {
						CustomControl control = new CustomControl();
						control.setTitle(change.getKey());
						KeyValueList list = change.getValueAdded();
						list.removePair("Scope");
						list.removePair("Sender");
						String type = list.getValue("MessageType");
						control.setConsole(list.toString());

						if (type.equals("Alert")) {
							control.setAlert();
						} else if (type.equals("Reading")) {
							control.setActive();
						} else if (type.equals("Connect")) {
							control.setEnable();
							String role = list.getValue("Role");
							if(role.equals("Super")||role.equals("Advertiser")){
								control.setSorAItems();
							}
						}
						flow.getChildren().add(control);
						});

					} else {
						supplier.get().forEach(x -> {
							KeyValueList list = change.getValueAdded();
							list.removePair("Scope");
							list.removePair("Sender");
							String date = list.getValue("Date");
							if(date != null&&!date.equals("")){
								SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss z");
								String realDate = format.format(new Date(Long.parseLong(date)));
								list.putPair("Date",realDate);
							}
							Platform.runLater(()->{
							((CustomControl) x).setConsole(list.toString());
							String type = list.getValue("MessageType");

							if (type.equals("Alert")) {
								((CustomControl) x).setAlert();
							} else if (type.equals("Reading")) {
								((CustomControl) x).setActive();
							} else if (type.equals("Connect")) {
								((CustomControl) x).setEnable();
								String role = list.getValue("Role");
								if(role.equals("Super")||role.equals("Advertiser")){
									((CustomControl) x).setSorAItems();
								}
							}
							});
						});
					}
				}
			}
		});
		
		
		}catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		

		// for (int i = 0; i < 10; i++) {
		// map.put(i + "asdasd", i + "sdfdf");
		// }

	}
	
	public void handlerChange(MouseEvent event){
		try {

			if (universal == null) {
				universal = new Socket("127.0.0.1", port);
			}
			if (encoder == null) {
				encoder = new MsgEncoder(universal.getOutputStream());
			}
			
			String em = getEmail();
			
			ArrayList<String> emlist = new ArrayList<>();
			
			if(em==null||em.equals("")){
				return;
			}
			
			String [] ems = em.replaceAll("\\s+","").split(",",0);
			
			for(String e:ems){
				matcher = pattern.matcher(e);
				if(matcher.matches()){
					emlist.add(e);
				}
			}
			
			StringBuilder builder = new StringBuilder();
			for(String e:emlist){
				builder.append(e);
				builder.append(",");
			}
			
			if(builder.length()>0){
				
				KeyValueList updateEmail = new KeyValueList();
				updateEmail.putPair("Scope", CreateGUI.SCOPE);
				updateEmail.putPair("MessageType", "Setting");
				updateEmail.putPair("Sender", CreateGUI.NAME);
				updateEmail.putPair("Receiver", "Uploader");
				updateEmail.putPair("Purpose", "UpdateRecipients");
				updateEmail.putPair("Recipients", builder.toString());
				encoder.sendMsg(updateEmail);
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void handlerSwitch(MouseEvent event){
		try {

			if (universal == null) {
				universal = new Socket("127.0.0.1", port);
			}
			if (encoder == null) {
				encoder = new MsgEncoder(universal.getOutputStream());
			}
			
			String uid = getUserID();

			uid = uid.replaceAll("\\s+","");
			
			if(uid==null||uid.equals("")){
				return;
			}
			
			KeyValueList switchUser = new KeyValueList();
			switchUser.putPair("Scope", CreateGUI.SCOPE);
			switchUser.putPair("MessageType", "Setting");
			switchUser.putPair("Sender", CreateGUI.NAME);
			switchUser.putPair("Receiver", "Uploader");
			switchUser.putPair("Purpose", "SwitchUser");
			switchUser.putPair("UserID", uid);
			encoder.sendMsg(switchUser);
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getUserID() {
		return userIDProperty().get();
	}

	public void setUserID(String value) {
		userIDProperty().set(value);
	}

	public StringProperty userIDProperty() {
		return userID.textProperty();
	}
	
	public String getEmail() {
		return emailProperty().get();
	}

	public void setEmail(String value) {
		emailProperty().set(value);
	}

	public StringProperty emailProperty() {
		return email.textProperty();
	}
}
