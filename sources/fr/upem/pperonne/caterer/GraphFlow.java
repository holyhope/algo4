package fr.upem.pperonne.caterer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class GraphFlow extends Graph<NodeFlow,ArcFlow<NodeFlow>> {
	private final Graph<NodeInt,Arc<NodeInt>> origine;
	private ArrayList<ArcFlow<NodeFlow>> inBase, outBase;

	public GraphFlow( Graph<NodeInt,Arc<NodeInt>> origine ) {
		this.origine = origine;
		reset();
	}

	private void reset() {
		HashMap<NodeInt, NodeFlow> hashmap = new HashMap<>();

		NodeFlow nf;
		for ( NodeInt n: origine.nodes ) {
			nf = new NodeFlow( n );
			add( nf );
			hashmap.put( n, nf );
		}

		for ( Arc<NodeInt> arc: origine.arcs ) {
			add( new ArcFlow<NodeFlow>(
				hashmap.get( arc.origine ),
				hashmap.get( arc.destination ),
				arc.getCout()
			) );
		}

		Collections.sort( nodes, new Comparator<NodeInt>() {
			@Override
			public int compare( NodeInt s1, NodeInt s2 ) {
				return s2.getDegree() - s1.getDegree();
			}
		} );
	}

	@SuppressWarnings("unused")
	private Graph<NodeFlow,ArcFlow<NodeFlow>> firstSolutionOld() {
		Graph<NodeFlow,ArcFlow<NodeFlow>> origine = new Graph<>();
		int totalOffre = offreTotal(),
			totalNeeds = needsTotal(),
			totalOffreTmp, totalNeedsTmp;

		origine.add( nodes );
		origine.add( arcs );

		ArrayList<ArcFlow<NodeFlow>> arcs = new ArrayList<>();
		for ( ArcFlow<NodeFlow> arc: this.arcs ) {
			arcs.add( new ArcFlow<NodeFlow>( arc ) );
		}
		this.arcs = arcs;

		NodeFlow voisin;
		List<NodeFlow> offre, demande;
		int envoye, demandeRestante, offreRestante;

		do {
			offre = offresLocal();
			demande = needsLocal();
			totalOffreTmp = totalOffre;
			totalNeedsTmp = totalNeeds;

			for ( NodeFlow S: offre ) {
				for ( ArcFlow<NodeFlow> A: outArcsLocal( S ) ) {
					voisin = get( A.getDestination() );
					if ( demande.contains( voisin ) ) {
						offreRestante = -S.getDegree();
						demandeRestante = voisin.getDegree();
						envoye = Math.min(
							demandeRestante,
							offreRestante
						);
						offreRestante -= envoye;
						S.setDegree( -offreRestante );

						demandeRestante -= envoye;
						voisin.setDegree( demandeRestante );
						if ( demandeRestante == 0 ) {
							demande.remove( voisin );
						}
						A.setCout( envoye );
						totalOffreTmp -= envoye;
						totalNeedsTmp -= envoye;
					}
				}
			}
		} while ( totalOffreTmp != 0 || totalNeedsTmp != 0 );

		return origine;
	}

	private boolean isOptimal() {
		for ( ArcFlow<NodeFlow> arc: outBase ) {
			//TODO Parcourir les arcs hors base et vérifier les contraintes
		}
		return true;
	}


	private void updateBases() {
		inBase = new ArrayList<>();
		outBase = new ArrayList<>();
		for ( ArcFlow<NodeFlow> arc: arcs ) {
			if ( arc.getCout() == 0 ) {
				outBase.add( arc );
			} else {
				inBase.add( arc );
			}
		}
	}

	public void start() {
		//TODO: firstSolution();
		while ( ! isOptimal() ) {
			//TODO Améliorer le graphe
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
}
