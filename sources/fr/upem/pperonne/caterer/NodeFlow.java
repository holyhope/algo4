package fr.upem.pperonne.caterer;


public class NodeFlow extends NodeInt {
	public NodeFlow( int poids ) {
		super( poids );
	}

	public NodeFlow() {
		super();
	}
	
	public NodeFlow( NodeInt n ) throws NullPointerException {
		super( n );
	}

	@Override
	public NodeFlow clone() {
		NodeFlow nf = new NodeFlow( getDegree() );
		clone( nf );
		return nf;
	}
}
