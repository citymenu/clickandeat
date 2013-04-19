package com.ezar.clickandeat.util;

public class Triple<X,Y,Z> {

    public final X first;

    public final Y second;
    
    public final Z third;

    public Triple(X first, Y second, Z third) {
        this.first = first;
        this.second = second;
        this.third = third;
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
        if(obj == null || !(obj instanceof Triple)) return false;
        final Triple<X,Y,Z> triple = (Triple<X,Y,Z>)obj;
        return first.equals(triple.first) && second.equals(triple.second) && third.equals(triple.third);
    }

    @Override
    public int hashCode() {
        return first.hashCode() * second.hashCode() * third.hashCode();
    }
}
