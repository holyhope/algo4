package fr.upem.pperonne.caterer.simplex;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fr.upem.pperonne.caterer.Arc;
import fr.upem.pperonne.caterer.Graph;
import fr.upem.pperonne.caterer.NodeFlow;

/**
 * 
 * @author jeremy
 *
 * premiere etape de l'algorithme du simplex
 * on cherche un noued tel que 
 * 
 */
public class StepZero {
	
	public Graph<NodeFlow,Arc<NodeFlow>> testStepZero(Graph<NodeFlow,Arc<NodeFlow>> G){
			return trivialCase(G);
	}
	
	/*cas trivial*/
	@SuppressWarnings("rawtypes")
	private Graph<NodeFlow,Arc<NodeFlow>> trivialCase(Graph<NodeFlow,Arc<NodeFlow>> G){
		Set<Arc> listArc = G.getArcs();/*on recupere une liste des arcs pour essayer de trouver un noeud possedant un lien vers tout les autre noeuds*/
		Set<NodeFlow> listNode = G.getNodes();
		NodeFlow tmp;
		Iterator<NodeFlow> it = listNode.iterator();
		while (it.hasNext()) {
			tmp = it.next();
			if(isIdenticalHasSet(G.neighbours(tmp),listNode)){
				return solutionInitiale(listArc, tmp);
			}
		}
		return problemeAuxilliere(G);
	}
	
	@SuppressWarnings("rawtypes")
	private boolean isIdenticalHasSet (Set<NodeFlow> h1, Set<NodeFlow> h2){
	    if ( h1.size() != h2.size() ) {
	        return false;
	    }
	    HashSet<NodeFlow> clone = new HashSet<NodeFlow>(h2); // just use h2 if you don't need to save the original h2
	    Iterator it = h1.iterator();
	    while (it.hasNext() ){
	        NodeFlow A = (NodeFlow) it.next();
	        if (clone.contains(A)){ // replace clone with h2 if not concerned with saving data from h2
	            clone.remove(A);
	        } else {
	            return false;
	        }
	    }
	    return true; // will only return true if sets are equal
	}

	/*fonction qui construit l'arbre pour la solution initiale*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Graph<NodeFlow,Arc<NodeFlow>> solutionInitiale(Set<Arc> listArc, NodeFlow node){
		Iterator<Arc> itArc= listArc.iterator();
		Arc<NodeFlow> tmp;
		Graph<NodeFlow,Arc<NodeFlow>> G = new Graph<NodeFlow,Arc<NodeFlow>>();
		while(itArc.hasNext()){
			tmp=itArc.next();
			if(tmp.getOrigine().equals(node)){
				G.add(tmp);
				G.add(tmp.getOrigine());
			}else if(tmp.getDestination().equals(node)){
				G.add(tmp);
				G.add(tmp.getDestination());
			}
		}
		return G;
	}
	
	/*cas ou on rajoute des lien
	 * lien naturel = 0
	 * lien crée =1 
	*/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Graph<NodeFlow,Arc<NodeFlow>> problemeAuxilliere(Graph<NodeFlow,Arc<NodeFlow>> G){
		Graph<NodeFlow,Arc<NodeFlow>> tmp = new Graph<NodeFlow, Arc<NodeFlow>>();
		Set<NodeFlow> listNode = G.getNodes();
		Set<Arc> listArc = G.getArcs() ; 
		Iterator<NodeFlow> itNode = listNode.iterator();
		Iterator<Arc> itArc = listArc.iterator();
		NodeFlow centre = itNode.next();
		Arc<NodeFlow> tmpArc; 
		/*on crée a présent les lien manquant*/
		while(itArc.hasNext()){
			tmpArc = itArc.next();
			if(tmpArc.getDestination().equals(centre)){
				/*mettre le lien a 0*/
			} else if(tmpArc.getOrigine().equals(centre)){
				/*mettre le lien a 0*/
			}else{
				/*mettre le lien a 1*/
			}
		}
		return tmp;
	}
}
