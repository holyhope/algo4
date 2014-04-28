package fr.upem.pperonne.caterer;

@SuppressWarnings("rawtypes")
public abstract class FunctionNode<N extends Node> {
	public abstract void accept( N S );
}
