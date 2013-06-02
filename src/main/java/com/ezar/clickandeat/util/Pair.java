package com.ezar.clickandeat.util;

public class Pair<X,Y> {
    
    public final X first;
    
    public final Y second;

    public Pair(X first, Y second) {
        this.first = first;
        this.second = second;
    }
    
    public X getFirst() {
        return first;
    }
    
    public Y getSecond() {
        return second;
    }


    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || !(obj instanceof Pair)) return false;
        final Pair<X,Y> pair = (Pair<X,Y>)obj;
        return first.equals(pair.first) && second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return first.hashCode() * second.hashCode();
    }

}
