

/**
 * This is just a convenient "container class" to hold a pair of
 * objects. The first object contains all the important information
 * about a node (its id number, hostname, port number, etc.). The
 * second object is an edge cost.
 * <p/>
 * This class is used to create "neighbor information" for nodes in
 * the router graph.
 */

public class NbrCostPair {
    private Neighbor nbr;
    private int cost;

    public NbrCostPair(Neighbor nbr, int cost) {
        this.nbr = nbr;
        this.cost = cost;
    }

    public Neighbor getNbr() {
        return nbr;
    }

    public int getCost() {
        return cost;
    }

}
