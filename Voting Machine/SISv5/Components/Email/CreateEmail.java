import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import java.util.Properties;
import javax.mail.*;

public class CreateEmail
{
    // socket for connection to SISServer
    static Socket universal;
    private static int port = 53217;
    // message writer
    static MsgEncoder encoder;
    // message reader
    static MsgDecoder decoder;
    // scope of this component
    private static final String SCOPE = "SIS.Scope1";
    // name of this component
    private static final String NAME = "Email";

    public static void main(String[] args)
    {
        //Main Program Loop
        while (true)
        {
            //Connect to SIS Server
            try
            {

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
                conn.putPair("Scope", SCOPE);
                conn.putPair("MessageType", "Connect");
                conn.putPair("Role", "Basic");
                conn.putPair("Name", NAME);
                encoder.sendMsg(conn);

                // KeyValueList for inward messages, see KeyValueList for
                // details
                KeyValueList kvList;

                Properties props = new Properties();
                props.setProperty("mail.store.protocol", "imaps");
                Session session = Session.getDefaultInstance(props, null);
                Store store = session.getStore("imaps");
                store.connect("imap.gmail.com", "cs1555group17@gmail.com", "cs1555pitt");
                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);
                Message msg;
                int counter = 0;
                int inbox_count = 0;

                while (true)
                {


                    inbox_count = inbox.getMessageCount();
                    if (inbox_count > counter)
                    {
                        msg = inbox.getMessage(inbox_count);
                        System.out.println(msg.getSubject());
                        counter = inbox_count;
                        processEmail(msg);
                    }
                }

            }
            catch (Exception e)
            {
                // if anything goes wrong, try to re-establish the connection
                e.printStackTrace();
                try
                {
                    // wait for 1 second to retry
                    Thread.sleep(1000);
                }
                catch (InterruptedException e2)
                {
                }
                System.out.println("Try to reconnect");
                try
                {
                    universal = connect();
                }
                catch (IOException e1)
                {
                }
            }
        }
    }

    static Socket connect() throws IOException
    {
        //Used for connect(reconnect) to SIS Server
        Socket socket = new Socket("127.0.0.1", port);
        return socket;
    }

    private static KeyValueList initRecord(KeyValueList record)
    {
        //Constructor for return message.
        record.putPair("Scope", SCOPE);
        record.putPair("Sender", NAME);
        record.putPair("Receiver", "VotingApp");
        record.putPair("Date", System.currentTimeMillis() + "");
        record.putPair("MessageType", "Setting");

        return record;
    }

    private static void processEmail(Message input)
    {
        try
        {
            KeyValueList record = new KeyValueList();
            record = initRecord(record);

            String[] parts = input.getSubject().split(" ");
            String type = parts[0];
            record.putPair("Purpose", type);

            switch(type)
            {
                case "StartPoll":
                    record.putPair("NumberOfCandidates", parts[1]);
                    int num_of_candidates = Integer.parseInt(parts[1]);
                    for (Integer i = 0; i < num_of_candidates; i++)
                    {
                        record.putPair("Candidate" + i.toString(), parts[2+i]);
                    }
                break;
                case "ClosePoll":
                break;
                case "Vote":
                    record.putPair("Vote", parts[1]);
                    Address[] from = input.getFrom();
                    String email = from[0].toString();
                    int start = email.indexOf('<');
                    int end = email.indexOf('>');
                    record.putPair("Email", email.substring(start+1, end));
                break;
            }

            encoder.sendMsg(record);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}