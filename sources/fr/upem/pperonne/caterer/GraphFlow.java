package fr.upem.pperonne.caterer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class GraphFlow extends Graph<NodeFlow,ArcFlow> {
	private final GraphFlow origine;

	public GraphFlow( Graph<NodeInt,Arc<NodeInt>> origine ) {
		HashMap<NodeInt, NodeFlow> link = new HashMap<>();

		NodeFlow nf;
		for ( NodeInt n: origine.nodes ) {
			nf = new NodeFlow( n );
			add( nf );
			link.put( n, nf );
		}

		for ( Arc<NodeInt> arc: origine.arcs ) {
			add( new ArcFlow(
				link.get( arc.origine ),
				link.get( arc.destination ),
				arc.getCout()
			) );
		}

		Collections.sort( nodes, new Comparator<NodeInt>() {
			@Override
			public int compare( NodeInt s1, NodeInt s2 ) {
				return s2.getDegree() - s1.getDegree();
			}
		} );

		this.origine = this.clone();
	}

	private ArcFlow findArcE() {
		for ( ArcFlow e: origine.arcs ) {
			add( new NodeFlow() );
			if ( e.getCout() + e.getOrigine().getDegree() < e.getDestination().getDegree() &&
					isCyclicNoSens() ) {
				return e;
			}
		}
		return null;
	}

	private ArcFlow findArcF( ArcFlow e ) {
		return null;
	}

	public void start() throws IllegalStateException {
		if ( 0 != degreeTotal() ) {
			throw new IllegalStateException( "Imposible de d�marrer l'algorithme du simplexe r�seau, car l'ofre n'est pas �gale � la demande." );
		}
		//TODO: firstSolution();
		ArcFlow e, f;
		while ( null != ( e = findArcE() ) ) {
			f = findArcF( e );
			add( e );
			remove( f );
			//TODO: updatePrice();
		}
	}

	public List<NodeInt> needs() {
		List<NodeInt> nodes = new ArrayList<>();

		for ( NodeFlow S: this.nodes ) {
			if ( S.getDegree() > 0 ) {
				nodes.add( S.clone() );
			}
		}
		Collections.sort( nodes, new Comparator<NodeInt>() {
			@Override
			public int compare( NodeInt s1, NodeInt s2 ) {
				return s1.getDegree() - s2.getDegree();
			}
		} );

		return nodes;
	}

	public List<NodeInt> offres() {
		List<NodeInt> nodes = new ArrayList<>();

		for ( NodeInt S: this.nodes ) {
			if ( S.getDegree() < 0 ) {
				nodes.add( S.clone() );
			}
		}

		return nodes;
	}

	public List<NodeFlow> needsLocal() {
		List<NodeFlow> nodes = new ArrayList<>();

		for ( NodeFlow S: this.nodes ) {
			if ( S.getDegree() > 0 ) {
				nodes.add( S );
			}
		}
		Collections.sort( nodes, new Comparator<NodeFlow>() {
			@Override
			public int compare( NodeFlow s1, NodeFlow s2 ) {
				return s1.getDegree() - s2.getDegree();
			}
		} );

		return nodes;
	}

	public List<NodeFlow> offresLocal() {
		List<NodeFlow> nodes = new ArrayList<>();

		for ( NodeFlow S: this.nodes ) {
			if ( S.getDegree() < 0 ) {
				nodes.add( S );
			}
		}

		return nodes;
	}

	public int offreTotal() {
		int total = 0, degree;
		for ( NodeFlow S: nodes ) {
			degree = S.getDegree();
			if ( degree > 0 ) {
				total += degree;
			}
		}
		return total;
	}

	public int needsTotal() {
		int total = 0, degree;
		for ( NodeFlow S: nodes ) {
			degree = S.getDegree();
			if ( degree < 0 ) {
				total += degree;
			}
		}
		return -total;
	}
	
	public int degreeTotal() {
		int total = 0;
		for ( NodeFlow S: nodes ) {
			total += S.getDegree();
		}
		return total;
	}
	
	@Override
	protected GraphFlow clone() {
		Graph<NodeInt, Arc<NodeInt>> origine = new Graph<>();
		origine.add( origine.arcs );
		origine.add( origine.nodes );
		GraphFlow g = new GraphFlow( origine );
		return g;
	}
}
