package fr.upem.pperonne.caterer;

/**
 * classe sur la structure des noeud de flots
 * @author pierre 
 * @author jeremy
 *
 */
public class NodeFlow extends NodeInt {
	private int price = 0;

	public NodeFlow( int poids ) {
		super( poids );
	}

	public NodeFlow( int poids, int price ) {
		this( poids );
		this.price = price;
	}

	public NodeFlow() {
		super();
	}
	
	public NodeFlow( NodeInt n ) throws NullPointerException {
		super( n );
	}

	@Override
	public NodeFlow clone() {
		NodeFlow nf = new NodeFlow( getDegree(), getPrice() );
		clone( nf );
		return nf;
	}

	public void setPrice( int price ) {
		this.price = price;
	}

	public int getPrice() {
		return price;
	}

	public boolean isNeeding() {
		return getDegree() > 0;
	}
}
