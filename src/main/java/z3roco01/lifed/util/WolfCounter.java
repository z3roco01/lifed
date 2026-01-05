package z3roco01.lifed.util;

/**
 * Interface used in ServerPlayerEntityMixin, for implementing wolf counting
 */
public interface WolfCounter {
    int getWolfCount();
    void incrementWolfCount();
    void decrementWolfCount();
}
