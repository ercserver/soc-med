import DatabaseModule.src.model.DbComm_V1;

import java.util.HashMap;

/**
 * Created by ohad on 16/4/2015.
 */
public class ohadTest {
    public static void main(String[] args) {
        DbComm_V1 db = new DbComm_V1();
        HashMap<String, String> det = new HashMap<String, String>();
        det.put("user_type", "0");

        db.addNewCommunityMember(det);

    }
}
