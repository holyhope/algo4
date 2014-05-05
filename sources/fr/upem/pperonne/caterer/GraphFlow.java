package fr.upem.pperonne.caterer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphFlow extends Graph<NodeFlow,ArcFlow> implements Runnable {
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

		this.origine = this.clone();
	}

	private GraphFlow() { origine = null; }

	private ArcFlow findArcE() {
		//TODO: A tester: Parait renvoyer toujours null (la première solution est déjà la meilleur ?).
		for ( ArcFlow e: origine.arcs ) {
			add( new NodeFlow() );
			if ( e.getCout() + e.getOrigine().getDegree() < e.getDestination().getDegree() &&
					isCyclicNoSens() ) {
				return e;
			}
		}
		return null;
	}
	
	private Set<ArcFlow> isolateCycle() {
		Set<ArcFlow> useless = new HashSet<>();
		for ( ArcFlow arc: arcs ) {
			remove( arc );
			if ( ! isCyclicNoSens() ) {
				add( arc );
			} else {
				useless.add( arc );
			}
		}
		remove( useless );
		return useless;
	}

	private ArcFlow findArcF( ArcFlow e ) {
		ArcFlow f = null, arc;
		arc = e;
		Set<ArcFlow> useless = isolateCycle();
		int max = e.getCout(), cout;
		NodeFlow last = e.getDestination();

		do {
			for ( ArcFlow a: arcs ) {
				/* Sens opposé à e */
				if ( last.equals( a.getDestination() ) ) {
					last = a.getOrigine();
					cout = a.getCout();
					if ( cout > max ) {
						max = cout;
						arc = f = a;
					}
				} else {
					/* Sens identique à e */
					if ( ! arc.equals( a ) && last.equals( a.getOrigine() ) ) {
						arc = a;
						last = a.getDestination();
					}
				}
			}
		} while ( ! last.equals( e.getDestination() ) );

		add( useless );

		return f;
	}
	
	private void updatePrice() {
		NodeFlow node = (NodeFlow) nodes.toArray()[0];
		node.setPrice( 0 );
		runPrefix( node, new FunctionNode<NodeFlow>() {
			@Override
			public void accept( NodeFlow S ) {
				for ( ArcFlow arc: outArcs( S ) ) {
					arc.destination.setPrice( S.getPrice() + arc.getCout() );
				}
			}

			@Override
			public NodeFlow next( NodeFlow node, Set<NodeFlow> visited ) {
				Set<NodeFlow> neighbours;
				for ( NodeFlow n: visited ) {
					neighbours = neighboursLocal( n );
					neighbours.removeAll( visited );
					for ( NodeFlow nf: neighbours ) {
						return nf;
					}
				}
				return null;
			}
		} );
	}
	
	private void updateFlow( ArcFlow e ) {
		runPrefix( e, new FunctionArc<ArcFlow>() {
			@Override
			public void accept( ArcFlow A ) {
				NodeFlow dest = A.getDestination();
				int flow = A.getFlow() - dest.getDegree(), transported, needed;
				for ( ArcFlow a: outArcsLocal( dest ) ) {
					needed = origine.get( a.destination ).getDegree();
					transported = Math.min( needed, flow );
					flow -= transported;
					a.setFlow( transported );
				}
			}

			@Override
			public ArcFlow next( ArcFlow arc, Set<ArcFlow> visited ) {
				Set<ArcFlow> nexts;
				for ( ArcFlow A: visited ) {
					nexts = outArcsLocal( A.getDestination() );
					nexts.removeAll( visited );
					for ( ArcFlow a: nexts ) {
						return a;
					}
				}
				return null;
			}
		} );
	}

	private void firstSolution() {
		//TODO: A tester
		runPrefix( new FunctionNode<NodeFlow>() {
			@Override
			public void accept(NodeFlow S) {
				NodeFlow dest;
				int degree;
				for ( ArcFlow arc: outArcsLocal( S ) ) {
					dest = arc.destination;
					if ( dest.isNeeding() ) {
						degree = dest.getDegree();
						S.addDegree( degree );
						arc.setFlow( degree );
						dest.setDegree( 0 );
					}
				}
			}

			@Override
			public NodeFlow next( NodeFlow S, Set<NodeFlow> visited ) {
				if ( S == null ) {
					for ( NodeFlow node: nodes ) {
						return node;
					}
				}
				Set<NodeFlow> nexts = children( S );
				nexts.removeAll( visited );
				for ( NodeFlow node: nexts ) {
					return next( node, visited );
				}
				if ( S.isNeeding() ) {
					for ( NodeFlow node: parents( S ) ) {
						return node;
					}
				}
				nexts = getNodes();
				nexts.removeAll( visited );
				for ( NodeFlow node: nexts ) {
					return node;
				}
				return null;
			}
		} );
		runPrefix( new FunctionArc<ArcFlow>() {
			@Override
			public void accept( ArcFlow S ) {
				if ( S.getFlow() == 0 ) {
					remove( S );
				}
			}
	
			@Override
			public ArcFlow next( ArcFlow S, Set<ArcFlow> visited ) {
				Set<ArcFlow> nexts = arcs;
				arcs.removeAll( visited );
				for ( ArcFlow arc: nexts ) {
					return arc;
				}
				return null;
			}
		} );
	}

	@Override
	public synchronized void run() throws IllegalStateException {
		if ( 0 != degreeTotal() ) {
			throw new IllegalStateException( "L'offre n'est pas égale à la demande." );
		}
		firstSolution();
		updatePrice();
		ArcFlow e, f;
		int i = 0;
		while ( null != ( e = findArcE() ) ) {
			f = findArcF( e );
			add( e );
			remove( f );
			updatePrice();
			updateFlow( e );
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
		GraphFlow g = new GraphFlow();
		return g;
	}
}
