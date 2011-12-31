package net.violet.platform.vasm;

/**
 * Classe pour une paire immutable d'éléments non nul.
 *
 * @param <E1> type du premier élément.
 * @param <E2> type du second élément.
 */
public class Pair<E1, E2> {

    private E1 firstElement;
    private E2 secondElement;

    public Pair(E1 inFirstElement, E2 inSecondElement) {
        this(inFirstElement, inSecondElement, true);
    }

    public Pair(E1 inFirstElement, E2 inSecondElement, boolean throwNull) {
        if ((inFirstElement == null) && throwNull) {
            throw new NullPointerException("first element is null");
        }

        this.firstElement = inFirstElement;
        this.secondElement = inSecondElement;
    }

    public E1 getFirst() {
        return this.firstElement;
    }

    public E2 getSecond() {
        return this.secondElement;
    }

    @Override
    public String toString() {
        return "{" + this.firstElement + ", " + this.secondElement + "}";
    }

    @Override
    public int hashCode() {
        return this.firstElement.hashCode() * 17 + this.secondElement.hashCode();
    }

    @Override
    public boolean equals(Object inAlter) {
        if (!(inAlter instanceof Pair)) {
            return false;
        }
        final Pair alterAsPair = (Pair) inAlter;

        return alterAsPair.firstElement.equals(this.firstElement) && (((alterAsPair.secondElement == null) && (this.secondElement == null)) || ((alterAsPair.secondElement != null) && alterAsPair.secondElement.equals(this.secondElement)));
    }

    public void setFirst(E1 firstElement) {
        this.firstElement = firstElement;
    }

    public void setSecond(E2 secondElement) {
        this.secondElement = secondElement;
    }
}

