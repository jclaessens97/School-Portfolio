package be.kdg.cluedobackend.model.cards.types;

public enum WeaponType {
    REVOLVER,
    ROPE,
    DAGGER,
    WRENCH,
    CANDLESTICK,
    LEADPIPE;

    public String getName() {
        return this.name().substring(0, 1).toUpperCase() + this.name().substring(1).toLowerCase();
    }
}
