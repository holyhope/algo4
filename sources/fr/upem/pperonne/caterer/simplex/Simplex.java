package fr.upem.pperonne.caterer.simplex;

import java.util.Iterator;
import java.util.Set;

import fr.upem.pperonne.caterer.Arc;
import fr.upem.pperonne.caterer.Graph;
import fr.upem.pperonne.caterer.NodeFlow;
import fr.upem.pperonne.caterer.PathArc;

public class Simplex  {

	/*fonction qui met en place le systeme de prix sur lequelle ont va appliquer le simplex
	 * prix du sommet j = prix du sommet i + capacité de l'arc 
	 * on commence a mettre un noued a zero et on determine les prix des autres noued de cette facon 
	 * */
	@SuppressWarnings({ "unchecked", "null" })
	private Graph<NodeFlow, Arc<NodeFlow>> initialise(Graph<NodeFlow,Arc<NodeFlow>> G){
		Set<NodeFlow> listNode =G.getNodes();
		@SuppressWarnings("rawtypes")
		Set<Arc> listArc = G.getArcs();
		Arc<NodeFlow> arc;
		@SuppressWarnings("rawtypes")
		Iterator<Arc> it;
		Set<NodeFlow> visited = null; 
		Iterator<NodeFlow> itNode = listNode.iterator();
		NodeFlow tmp = itNode.next();
		NodeFlow first =(NodeFlow) G.get(tmp);
		NodeFlow dest;
		first.setPrice(0);
		visited.add(first.clone());
		while((dest=G.next(first,visited))!=null ){
			visited.add(dest.clone());
			it = listArc.iterator();
			while(it.hasNext()){
				arc = it.next();
				if((arc.getDestination().equals(first)&& arc.getOrigine().equals(dest) || (arc.getDestination().equals(first)&& arc.getOrigine().equals(dest) ))){
					dest.setPrice(first.getPrice()+arc.getCout());
				}
			}
			first=dest;
		}
		return G;
	}
	
	/*fonction de base qui fait tourner l'algorithme du simplex */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Graph<NodeFlow, Arc<NodeFlow>> algoSimplex(Graph<NodeFlow,Arc<NodeFlow>> arbre,Graph<NodeFlow,Arc<NodeFlow>> solution){
		Set<Arc> listArc = arbre.getArcs();/*on recupere une liste des arcs pour essayer de trouver un noeud possedant un lien vers tout les autre noeuds*/
		Iterator<Arc> it = listArc.iterator();
		Arc<NodeFlow> arc;
		initialise(solution);
		while(it.hasNext()){
			/*on parcourt la liste des arcs pour voir si capacité plus noud est plus faible*/
			arc = it.next();
			if(arc.getOrigine().getPrice()+ arc.getCout() < arc.getDestination().getPrice()){
				/*on applique l'algo du simplx dessus*/
				nouvelleSolution(solution, arc.getOrigine(), arc.getDestination(),arc);
			}
		}
		return solution;
	}

	/*on vient d'ajouter un arc dans le graphe et faut determiner lequelle il faut virer
	 * mais avant il faut il faut mettre a jour les arc avec la variable t
	 * on utilise une variable suppelmentaire dans Node appeller t 
	 * qui a trois valeur possible -1 0 et 1 
	 * idee : pour trouver le reste du chemin ou mdettrz la variable on enleve tmp le nouvel arc pour trouver le chemin et apres on le remet 
	 * une fois cela fait on remet les prix a jour
	 * on cherche le chemin pour aller de depart a arrive
	 * */
	private Graph<NodeFlow, Arc<NodeFlow>> nouvelleSolution(Graph<NodeFlow,Arc<NodeFlow>> G, NodeFlow depart , NodeFlow arrive, Arc<NodeFlow> arcEntrant){
		PathArc<Arc<NodeFlow>> listArc = G.smallestPathNoSens(depart,arrive);
		int min=Integer.MAX_VALUE;
		Arc<NodeFlow> arcSortant = null;/*arc qu'on devrat supprimer*/
		Arc<NodeFlow> arcActuelle;
		Iterator<Arc<NodeFlow>> itArc = listArc.iterator();
		NodeFlow actuelle=depart;
		/*on parcourt le chemin
		 *  en mettant a jour la variable, pas bessoin de mettre la variable on cherche juste le minimum car l'alho en garanti un */
		while(itArc.hasNext()){
			arcActuelle = itArc.next();
			/*gros du programme a mettre en place*/
			if(arcActuelle.getOrigine().equals(actuelle)){
				/*on a un arc en sens normale*/
				if(arcActuelle.getCout() < min){
					min = arcActuelle.getCout();
					arcSortant = arcActuelle;
				}
				actuelle = arcActuelle.getDestination();
			}else {
				/*on a un arc en sens inverse on ne fait rien */
				actuelle = arcActuelle.getDestination();
			}
		}
		G.remove(arcSortant);
		G.add(arcEntrant);
		initialise(G);
		return G;
	}
	
}