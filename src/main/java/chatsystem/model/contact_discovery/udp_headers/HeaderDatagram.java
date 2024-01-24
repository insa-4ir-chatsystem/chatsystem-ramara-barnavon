package chatsystem.model.contact_discovery.udp_headers;

/** Enum class that contains every header type on 4 characters */
public enum HeaderDatagram {
    INCO, // contact information
    DECO, // contact information request
    DEPS, // pseudo validation request
    REPS, // pseudo rejected
    DEID, // id validation request
    REID, // id rejected
    CHPS, // pseudo change request
    RECH, // pseudo change rejected
    NULL // Useless datagram


}
