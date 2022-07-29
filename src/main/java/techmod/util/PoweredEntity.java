package techmod.util;

public interface PoweredEntity {
    int needed();
    boolean generator();
    int producing();
    int using();

}
