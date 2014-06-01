package fr.upem.pperonne.caterer;


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
