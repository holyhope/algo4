package fr.upem.pperonne.caterer;

@SuppressWarnings("rawtypes")
public abstract class FunctionArc<A extends Arc> {
	public abstract void accept( A a );
}
