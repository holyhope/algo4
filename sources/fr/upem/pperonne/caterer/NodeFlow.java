package fr.upem.pperonne.caterer;


public class NodeFlow extends Node {
	public NodeFlow( int poids ) {
		super( poids );
	}

	public NodeFlow() {
		super();
	}
	
	public NodeFlow( Node n ) throws NullPointerException {
		super( n );
	}

	@Override
	public NodeFlow clone() {
		NodeFlow nf = new NodeFlow( getDegree() );
		clone( nf );
		return nf;
	}
}
