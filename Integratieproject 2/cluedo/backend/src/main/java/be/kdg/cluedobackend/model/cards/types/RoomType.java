package be.kdg.cluedobackend.model.cards.types;

public enum RoomType {
    KITCHEN,
    BALLROOM,
    CONSERVATORY,
    BILLIARDROOM,
    LIBRARY,
    STUDY,
    HALL,
    LOUNGE,
    DININGROOM,
    CELLAR;


    public String getName() {
        return this.name().substring(0, 1).toUpperCase() + this.name().substring(1).toLowerCase();
    }
}
