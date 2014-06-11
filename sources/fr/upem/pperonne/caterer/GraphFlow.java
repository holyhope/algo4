package fr.upem.pperonne.caterer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
/**
 * classe pour les graphe de flot
 * @author pierre
 * @author jeremy
 * 
 */
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
	/**
	 * fonction portant sur la recherche de l'arc entrant: 
	 * c'est a dire un arc qui ameliore la solution finale,elle renvoi l'arc trouver sans l'ajouter dans le graphe
	 * on utilise un iterator pour parcourir l'ensemble des arcs
	 * @return
	 */
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
				System.out.print( e.getOrigine().getPrice() + " + " + e.getCout() + " < " + e.getDestination().getPrice() + " " );
				if ( e.getOrigine().getPrice() + e.getCout() < e.getDestination().getPrice() ) {
					if ( isCyclicNoSens() ) {
						System.out.println( "arc entrant : " + e );
						return e;
					} else {
						System.out.println( "non cyclique" );
					}
				} else {
					System.out.println( "pas rentable" );
				}
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
	/**
	 * fonction de recherche de l'arc sortant
	 * on parcourt le chemin entre les deux noeud de l'arc entrant afin de determiner l'arc a supprimer
	 * @param e
	 * @return
	 */
	private ArcFlow findArcF( ArcFlow e ) {
		ArcFlow f = null;
		System.out.println("noeud origine  dest "+e.origine+" "+e.destination);
		PathArc<ArcFlow> path = smallestPathNoSensLocal( e.destination, e.origine );

		int min = Integer.MAX_VALUE, flow;

		System.out.println( "On trouve l'arc sortant dans le chemin " + path );
		NodeFlow dest = e.destination;
		for ( ArcFlow arc: path ) {
			flow = arc.getFlow();
			if ( arc.destination.equals( dest ) && flow < min ) {
				min = flow;
				f = arc;
				dest = arc.origine;
			} else {
				dest = arc.destination;
			}
		}
		System.out.println( "arc sortant : " + f );
		/*

		do {
			for ( ArcFlow a: arcs ) {
				// Sens opposé à e
				if ( last.equals( a.getDestination() ) ) {
					last = a.getOrigine();
					cout = a.getCout();
					if ( cout > max ) {
						max = cout;
						arc = f = a;
					}
				} else {
					// Sens identique à e
					if ( ! arc.equals( a ) && last.equals( a.getOrigine() ) ) {
						arc = a;
						last = a.getDestination();
					}
				}
			}
		} while ( ! last.equals( e.getDestination() ) );

		add( useless );
*/
		return f;
	}
	/**
	 * fonction de mise a jour des prix
	 * on part d'un noued au hasard et on met son prix a zero et on met a jour les autre noueds
	 */
	private void updatePrice() {
		for ( NodeFlow node : nodes ) {
			node.setPrice( 0 );
		}
		System.out.println( "mise à jour des prix." );
		for ( NodeFlow node: nodes ) {
			node.setPrice( 0 );
			runPrefix( node, new FunctionNode<NodeFlow>() {
				private ArcFlow arc;

				@Override
				public void accept( NodeFlow S ) {
					if ( arc != null ) {
						int price;
						if ( S.equals( arc.destination ) ) {
							price = arc.origine.getPrice() + arc.getCout();
							S.setPrice( price );
						} else {
							price = arc.destination.getPrice() - arc.getCout();
							S.setPrice( price );
						}
					}
				}

				@Override
				public NodeFlow next( NodeFlow S, Set<NodeFlow> visited ) {
					Set<ArcFlow> arcs;
					for ( NodeFlow node: visited ) {
						arcs = outArcsLocal( node );
						for ( ArcFlow arc: arcs ) {
							if ( ! visited.contains( arc.destination ) ) {
								this.arc = arc;
								return arc.destination;
							}
						}
						arcs = inArcsLocal( node );
						for ( ArcFlow arc: arcs ) {
							if ( ! visited.contains( arc.origine ) ) {
								this.arc = arc;
								return arc.origine;
							}
						}
					}
					return null;
				}
			} );
		}
	}

	/**
	 * mise a jour des flow du graphe apres avoir trouver une solution
	 * @param e
	 */
	private void updateFlow( ArcFlow e ) {
		PathArc<ArcFlow> path = smallestPathNoSensLocal( e.destination, e.origine );
		NodeFlow dest = e.destination;
		e.setFlow( e.destination.getDegree() );
		for ( ArcFlow arc: path ) {
			if ( arc.origine.equals( dest ) ) {
				arc.setFlow( arc.getFlow() + e.getFlow() );
				dest = arc.destination;
			} else {
				arc.setFlow( arc.getFlow() - e.getFlow() );
				dest = arc.origine;
			}
		}
	}

	/**
	 * fonction qui determine l'etape 0 de l'algorithme et nous donnée une solution initiale
	 */
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
		System.out.println( "Meilleur noeud = " + bestNode );
		// ----------- Arcs réels ---------------
		ArcFlow arc;
		NodeFlow linkedNode;
		Iterator<ArcFlow> it = arcs.iterator();
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
				arc.setFlow( Math.abs( linkedNode.getDegree() ) );
			}
		}
		// ---------- Arcs Artificiels ---------
		boolean linked;
		int degree, flow;
		for ( NodeFlow node: nodes ) {
			linked = false;
			for ( NodeFlow neighbour: neighbours( node ) ) {
				if ( neighbour.equals( bestNode ) ) {
					linked = true;
				}
			}
			if ( ! linked ) {
				degree = node.getDegree();
				flow = Math.abs( degree );
				if ( degree > 0 ) {
					add( new ArcFlow( bestNode, node, 1, flow ) );
				} else {
					add( new ArcFlow( node, bestNode, 1, flow ) );
				}
			}
		}
		updatePrice();
	}
	/**
	 * fonction de boucle qui renvoi faux si la solution et optimal sinon renvoi vrai et met a jour le graphe
	 * @return
	 */
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
