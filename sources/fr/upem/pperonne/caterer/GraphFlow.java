package fr.upem.pperonne.caterer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class GraphFlow extends Graph<NodeFlow,ArcFlow<NodeFlow>> {
	private final Graph<Node,Arc<Node>> origine;
	private ArrayList<ArcFlow<NodeFlow>> inBase, outBase;

	public GraphFlow( Graph<Node,Arc<Node>> origine ) {
		this.origine = origine;
		reset();
	}

	private void reset() {
		HashMap<Node, NodeFlow> hashmap = new HashMap<>();

		NodeFlow nf;
		for ( Node n: origine.nodes ) {
			nf = new NodeFlow( n );
			add( nf );
			hashmap.put( n, nf );
		}

		for ( Arc<Node> arc: origine.arcs ) {
			add( new ArcFlow<NodeFlow>(
				hashmap.get( arc.origine ),
				hashmap.get( arc.destination ),
				arc.getCout()
			) );
		}

		Collections.sort( nodes, new Comparator<Node>() {
			@Override
			public int compare( Node s1, Node s2 ) {
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

	private void run() {
		while ( ! isOptimal() ) {
			//TODO Améliorer le graphe
		}
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
		run();
	}
}
