
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class Initializer {

    static Socket universal;

    static MsgEncoder encoder;

    static int port = 53217;

    // public static void example() {
    // try {
    // String path = "xml/CreateBloodPressure.XML";
    //
    // Msg msg = new Msg();
    // Head head = new Head();
    // Body body = new Body();
    // Item item1 = new Item();
    // item1.setKey("InputMessage1");
    // item1.setValue("43");
    // Item item2 = new Item();
    // item2.setKey("OutputMessage1");
    // item2.setValue("44");
    // List<Item> items = new ArrayList<Item>();
    // items.add(item1);
    // items.add(item2);
    // body.setItems(items);
    // head.setMsgId("20");
    // head.setDescription("REG");
    // msg.setBody(body);
    // msg.setHead(head);
    //
    // JAXBContext context = JAXBContext.newInstance(Msg.class);
    // Marshaller marshaller = context.createMarshaller();
    // marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    // marshaller.marshal(msg, System.out);
    //
    // Unmarshaller unmarshaller = context.createUnmarshaller();
    //
    // Msg msg2 = (Msg) unmarshaller.unmarshal(new File(path));
    //
    // marshaller.marshal(msg2, System.out);
    //
    // } catch (JAXBException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }

//    static KeyValueList extractToKV(Msg msg) {
//        KeyValueList kvList = new KeyValueList();
//        //Head head = msg.getHead();
//        //Body body = msg.getBody();
//        //kvList.addPair("MsgID", head.getMsgId());
//        //kvList.addPair("Description", head.getDescription());
//        List<Item> items = body.getItems();
//        for (Item i : items) {
//            kvList.addPair(i.getKey(), i.getValue());
//        }
//        return kvList;
//    }

    static void processFile(File file) {
        KeyValueList kvList = new KeyValueList();
        try {
            JAXBContext context = JAXBContext.newInstance(Msg.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            Msg msg = (Msg) unmarshaller.unmarshal(file);

            kvList = XMLUtil.generateKV(msg);

            System.out.println("Registration Attempt: "
                               + kvList.getValue("Name"));

            encoder.sendMsg(kvList);

            System.out.println("Registration Success: "
                               + kvList.getValue("Name"));

        } catch (JAXBException | IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Registration Fail: " + kvList.getValue("Name"));
        }
    }

    static Socket connect() throws IOException {
        Socket socket = new Socket("127.0.0.1", port);
        return socket;
    }

    public static void main(String[] args) {

        Path path = FileSystems.getDefault().getPath("../xml/InitXML");

        while (true) {

            try {

                universal = connect();
                encoder = new MsgEncoder(universal.getOutputStream());

                Files.list(path).forEach(x -> {
                    processFile(x.toFile());
                });
                universal.close();
                break;

            } catch (IOException e) {
                // TODO Auto-generated catch block
                 e.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e2) {
                    // TODO Auto-generated catch block
                    // e2.printStackTrace();
                }
                System.out.println("Try to reconnect");
                try {
                    universal = connect();

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    // e1.printStackTrace();
                }
            }
        }
    }
}
