package chatsystem.ContactDiscoveryLib;

/** Enum class that contains every header type */
public enum HeaderDatagram {
    INCO, //info contact
    DECO, //demande contact
    DEPS,  //demande pseudo
    REPS, //refus pseudo
    DEID, //demande id
    REID,  //refus id
    NULL //Paquet mort ou insignifiant
    ;

}
