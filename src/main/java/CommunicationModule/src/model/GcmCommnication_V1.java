package CommunicationModule.src.model;


import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 05/04/2015.
 */
public class GcmCommnication_V1 extends CommToUsers_V1 {

    // The SENDER_ID here is the "Browser Key" that was generated when I
    // created the API keys for my Google APIs project.
    private static final String SENDER_ID = "AIzaSyBJK4AOF4swqz5zE_5mNnVDm9CCxUJ1apQ";

    public GcmCommnication_V1(HashMap<Integer,HashMap<String,String>> data,
                              ArrayList<String> target) {
        super(data,target);
    }

    public JSONArray sendResponse () {
        Sender sender = new Sender(SENDER_ID);
        Message message = new Message.Builder()

                // If multiple messages are sent using the same .collapseKey()
                // the android target device, if it was offline during earlier message
                // transmissions, will only receive the latest message for that key when
                // it goes back on-line.
                .collapseKey("GCM_Message")
                .timeToLive(30)
                .delayWhileIdle(true)
                .addData("message", String.valueOf(objToSend))
                .build();
        try {
            // use this for multicast messages.  The second parameter
            // of sender.send() will need to be an array of register ids.
            MulticastResult result = sender.send(message, targets, 1);

            if (result.getResults() != null) {
                int canonicalRegId = result.getCanonicalIds();
                if (canonicalRegId != 0) {

                }
            } else {
                int error = result.getFailure();
                System.out.println("Broadcast failure: " + error);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
