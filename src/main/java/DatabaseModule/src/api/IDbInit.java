package DatabaseModule.src.api;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IDbInit {
    //TODO - Perhaps split to connect and initialize separately? we will need to connect without
    // initilalizing as well - joining the two will force us to write double code (for the connection part)....
    void initializeAndConnect();
}
