package xyz.destillegast.dsgutils.helpers;

/**
 * Created by DeStilleGast 23-5-2021
 */
public final class Tuple<A, B> {
    private final A partA;
    private final B partB;

    public Tuple(A partA, B partB) {
        this.partA = partA;
        this.partB = partB;
    }

    public A getPartA() {
        return partA;
    }

    public B getPartB() {
        return partB;
    }
}
