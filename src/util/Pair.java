package util;

import java.util.Objects;

/**
 * Simple class for holding 2 values together.
 * @param <X> First of the two values. Key.
 * @param <Y> Second of the two values. Value.
 * @author Tereza Miklóšová
 */
public class Pair<X, Y> {
    /**
     * The first value.
     */
    private X x;
    /**
     * The second value.
     */
    private Y y;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return x.equals(pair.x) &&
                y.equals(pair.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Gets the first of the two values in pair.
     * @return {@link Pair#x}
     */
    public X getKey(){
        return x;
    }

    /**
     * Gets the second of the two values in pair.
     * @return {@link Pair#y}
     */
    public Y getValue(){
        return y;
    }

    /**
     * Constructor for this class.
     * @param x The first value.
     * @param y The second value.
     */
    public Pair(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}
