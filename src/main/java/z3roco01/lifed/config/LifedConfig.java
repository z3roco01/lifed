package z3roco01.lifed.config;

public record LifedConfig(
        Integer maxBoogeymen,
        String youAre,
        String notABoogeyman,
        String aBoogeyman,
        String boogeyChatMsg
) {
    public LifedConfig() {
        this(
                10,
                "You are...",
                "...NOT a boogeyman !!!",
                "...A BOOGEYMAN",
                "§7You are a boogeyman ! you must kill a §2dark green§7, §agreen§7 or §eyellow§7 to cure yourself. you lose EVERY alliance as a boogeyman until you are cured. if you do not then you will go to your §clast life§7 at the end of this session.§r"
        );
    }
}
