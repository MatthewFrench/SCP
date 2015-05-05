/**
 * Authors:
 *   Venkatesh-Prasad Ranganath
 *
 * Copyright (c) 2014, Kansas State University
 * Licensed under Eclipse Public License v1.0
 * http://www.eclipse.org/legal/epl-v10.html
 */

package scp.util;

import java.io.Serializable;

/**
 * A pair of values.
 * @param <F>  Type of the first element.
 * @param <S>  Type of the second element.
 */
public class Pair<F, S> implements Serializable {

    /**
     * First element.
     */
    public final F first;
    /**
     * Second element.
     */
    public final S second;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Tuple{");
        sb.append("first=").append(first);
        sb.append(", second=").append(second);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;

        Pair pair = (Pair) o;

        if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
        if (second != null ? !second.equals(pair.second) : pair.second != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    /**
     * Instantiates a new Pair.
     *
     * @param first the first element.
     * @param second the second element.
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;

    }
}
