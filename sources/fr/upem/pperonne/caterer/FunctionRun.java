package fr.upem.pperonne.caterer;

import java.util.Set;

public interface FunctionRun<T> {
	public abstract void accept( T S );
	public abstract T next( T S, Set<T> visited );
}
