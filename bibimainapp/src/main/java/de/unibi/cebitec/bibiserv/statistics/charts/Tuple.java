package de.unibi.cebitec.bibiserv.statistics.charts;

import java.util.Objects;

/**
 * Simple tuple class.
 *
 * @author jschmolke
 * @param <T>
 * @param <S>
 */
public class Tuple<T, S >{

    private T first;
    private S second;

    /**
     * Standardconstructor.
     *
     * @param first First element.
     * @param second Second element.
     */
    public Tuple(T first, S second) {
        
        this.first = first;
        this.second = second;
    }

    /**
     * Standardgetter.
     *
     * @return First element.
     */
    public T getFirst() {
        return first;
    }

    /**
     * Standardsetter.
     *
     * @param first First element.
     */
    public void setFirst(T first) {
        this.first = first;
    }

    /**
     * Standardgetter.
     *
     * @return Second element.
     */
    public S getSecond() {
        return second;
    }

    /**
     * Standardsetter.
     *
     * @param second 2nd element.
     */
    public void setSecond(S second) {
        this.second = second;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (first != null) {
            hash = hash * 37 + first.hashCode();
        }
        if (second != null) {
            hash = hash * 37 + second.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tuple<?, ?> other = (Tuple<?, ?>) obj;
        if (!Objects.equals(this.first, other.first)) {
            return false;
        }
        if (!Objects.equals(this.second, other.second)) {
            return false;
        }
        return true;
    }

}
