package fr.upem.pperonne.caterer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class GraphFlow extends Graph<NodeFlow,ArcFlow> implements Runnable {
	private final Graph<NodeInt, Arc<NodeInt>> origine;
	private HashMap<Node<?>, NodeFlow> nodeMap = new HashMap<>();
	private HashMap<Arc<?>, ArcFlow> arcMap = new HashMap<>();

	public GraphFlow( Graph<NodeInt,Arc<NodeInt>> origine ) {
		super();

		this.origine = origine.clone();
		NodeFlow nf;
		for ( NodeInt n: this.origine.nodes ) {
			nf = new NodeFlow( n );
			add( nf );
			nodeMap.put( n, nf );
		}

		ArcFlow af;
		for ( Arc<NodeInt> a: this.origine.arcs ) {
			af = new ArcFlow(
				nodeMap.get( a.origine ),
				nodeMap.get( a.destination ),
				a.getCout(),
				0
			);
			add( af );
			arcMap.put( a, af );
		}
	}

	private GraphFlow() { origine = null; }

	private ArcFlow findArcE() {
		System.out.println( "On trouve l'arc entrant." );
		ArcFlow e;
		Arc<NodeInt> tmp;
		Iterator<Arc<NodeInt>> it = origine.arcs.iterator();
		while ( it.hasNext() ) {
			tmp = it.next();
			e = new ArcFlow(
				nodeMap.get( tmp.origine ),
				nodeMap.get( tmp.destination ),
				tmp.getCout(),
				0
			);
			if ( ! contains( e ) ) {
				System.out.print( e + "\t: " );
				add( e );
				System.out.print( e.getOrigine().getPrice() + " + " + e.getCout() + " < " + e.getDestination().getPrice() + " " );
				if ( e.getOrigine().getPrice() + e.getCout() < e.getDestination().getPrice() ) {
					if ( isCyclicNoSens() ) {
						System.out.println( "arc entrant : " + e );
						remove( e );
						return e;
					} else {
						System.out.println( "non cyclique" );
					}
				} else {
					System.out.println( "pas rentable" );
				}
				remove( e );
			}
		}
		System.out.println( "Pas d'arc entrant." );
		return null;
	}

	@Override
	public boolean remove( final ArcFlow arc ) throws IllegalArgumentException {
		final LinkedList<Arc<?>> keys = new LinkedList<>();
		arcMap.forEach(new BiConsumer<Arc<?>, ArcFlow>() {

			@Override
			public void accept(Arc<?> arg0, ArcFlow arg1) {
				if ( arg1.equals( arc ) ) {
					keys.add( arg0 );
				}
			}
		});
		for ( Arc<?> key: keys ) {
			arcMap.remove( key );
		}
		return super.remove( arc );
	}
	
	@Override
	public boolean add( NodeFlow S ) throws IllegalArgumentException {
		nodeMap.put( null, S );
		return super.add(S);
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
		for ( NodeFlow node : nodes ) {
			node.setPrice( 0 );
		}
		System.out.println( "mise à jour des prix." );
		final LinkedList<ArcFlow> visited = new LinkedList<>();
		for ( ArcFlow arc: arcs ) {
			if ( ! visited.contains( arc ) ) {
				runPrefix( arc, new FunctionArc<ArcFlow>() {
					@Override
					public void accept( ArcFlow arc ) {
						System.out.print( "mise à jour de \"" + arc.destination.getPrice() + "\" -> " + arc.getOrigine().getPrice() + " + " + arc.getCout() + " = " );
						arc.destination.setPrice( arc.getOrigine().getPrice() + arc.getCout() );
						System.out.println( arc.destination.getPrice() );
						visited.add( arc );
					}

					@Override
					public ArcFlow next( ArcFlow arc, Set<ArcFlow> v ) {
						Set<ArcFlow> neighbours;
						System.out.println( "next arc \"" + arc + "\"" );
						
						for ( ArcFlow a: visited ) {
							neighbours = inOutArcsLocal( a.destination );
							neighbours.addAll( inOutArcsLocal( a.origine ) );
							neighbours.removeAll( visited );
							for ( ArcFlow af: neighbours ) {
								System.out.println( af );
								return af;
							}
						}
						System.out.println( "aucun" );
						return null;
					}
				} );
			}
		}
		for ( ArcFlow arc: arcs ) {
			System.out.println( arc.getDestination() + "\t" + arc.getDestination().getPrice() );
			System.out.println( arc.getOrigine() + "\t" + arc.getOrigine().getPrice() );
		}
		for ( NodeFlow node: nodes ) {
			System.out.println( node + "\t" + node.getPrice() );
		}
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

	public void firstSolution() {
		// weight --> modifierPoidsArcs
		int result = 0;
		NodeFlow bestNode = null;
		int nbNeighbours;
		// ---------- Meilleur Noeud ------------
		for ( NodeFlow node: nodes ) {
			nbNeighbours = neighbours( node ).size();
			if ( nbNeighbours > result ) {
				bestNode = node;
				result = nbNeighbours;
			}
		}
		// ----------- Arcs réels ---------------
		Iterator<ArcFlow> it = arcs.iterator();
		ArcFlow arc;
		NodeFlow linkedNode;
		while ( it.hasNext() ) {
			arc = it.next();
			linkedNode = null;
			if ( bestNode.equals( arc.getOrigine() ) ){
				linkedNode = arc.getDestination();
			} else if ( bestNode.equals( arc.getDestination() ) ) {
				linkedNode = arc.getOrigine();
			}
			if ( linkedNode == null ) {
				it.remove();
			} else {
				arc.setCout( Math.abs( linkedNode.getDegree() ) );
			}
		}
		// ---------- Arcs Artificiels ---------
		boolean linked;
		int degree, weight;
		for ( NodeFlow node: nodes ) {
			linked = false;
			for ( NodeFlow neighbour: neighbours( node ) ) {
				if ( neighbour.equals( bestNode ) ) {
					linked = true;
				}
			}
			if ( ! linked ) {
				degree = node.getDegree();
				weight = Math.abs( degree );
				if ( degree > 0 ) {
					add( new ArcFlow( bestNode, node, weight, 1 ) );
				} else {
					add( new ArcFlow( node, bestNode, weight, 1 ) );
				}
			}
		}
		updatePrice();
	}
	
	public boolean nextIteration() {
		ArcFlow e = findArcE(), f;
		if ( e == null ) {
			return false;
		}
		System.out.println( e );
		f = findArcF( e );
		add( e );
		remove( f );
		updatePrice();
		updateFlow( e );
		return true;
	}

	@Override
	public synchronized void run() throws IllegalStateException {
		if ( 0 != degreeTotal() ) {
			throw new IllegalStateException( "L'offre n'est pas égale à la demande." );
		}
		firstSolution();
		while ( nextIteration() );
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
		Graph<NodeFlow, ArcFlow> tmp = super.clone();
		GraphFlow g = new GraphFlow();
		g.add( tmp.nodes );
		g.add( tmp.arcs );
		return g;
	}
}
