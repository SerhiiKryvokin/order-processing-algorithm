package base.indexpq;

public class IndexMaxPQ<Key extends Comparable<Key>> extends IndexMinPQ<Key> {

    /**
     * Initializes an empty indexed priority queue with indices between {@code 0}
     * and {@code maxN - 1}.
     *
     * @param maxN the keys on this priority queue are index from {@code 0}
     *             {@code maxN - 1}
     * @throws IllegalArgumentException if {@code maxN < 0}
     */
    public IndexMaxPQ(int maxN) {
        super(maxN);
    }

    /**
     * Inverse min PQ greater method to get max PQ
     */
    @Override
    protected boolean greater(int i, int j) {
        return !super.greater(i, j);
    }
}
