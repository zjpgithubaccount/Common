package org.simple.base.nutz.model

class Pair<A, B> {

    A first

    B second

    Pair() {}

    Pair(A a, B b) {
        first = a
        second = b
    }

    static Pair<String, String> from(String expr) {
        if (!expr) {
            return null
        }

        def exprList = expr.split('=')

        if (exprList.size() == 1) {
            return new Pair<String, String>(expr, null)
        }

        //noinspection GroovyAssignabilityCheck
        return new Pair<String, String>(*exprList)
    }
}

class Triple<A, B, C> extends Pair<A, B> {

    C third

    Triple() {}

    Triple(A a, B b, C c) {
        super(a, b)
        third = c
    }
}

class Quadruple<A, B, C, D> extends Triple<A, B, C> {

    D fourth

    Quadruple() {}

    Quadruple(A a, B b, C c, D d) {
        super(a, b, c)
        fourth = d
    }
}