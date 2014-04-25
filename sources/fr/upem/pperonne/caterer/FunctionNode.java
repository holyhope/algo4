package fr.upem.pperonne.caterer;

public abstract class FunctionNode<N extends Node> {
	public abstract void accept( N S );
}
