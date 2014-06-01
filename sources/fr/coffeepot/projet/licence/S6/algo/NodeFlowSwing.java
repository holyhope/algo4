package fr.coffeepot.projet.licence.S6.algo;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

import fr.upem.pperonne.caterer.NodeFlow;

@SuppressWarnings({ "serial" })
public class NodeFlowSwing extends NodeSwing<NodeFlow> {
	private boolean over = false;
	
	public NodeFlowSwing( NodeFlow node, Dimension range )
			throws IllegalArgumentException {
		super(node, range);
	}

	@Override
	public void update( NodeFlow node ) {
		super.update(node);
		if ( over ) {
			String name = node.getPrice() + "";
			this.name = name.toCharArray();
		}
	}
	
	@Override
	public void mouseEntered( MouseEvent e ) {
		super.mouseEntered( e );
		over = true;
	}

	@Override
	public void mouseExited( MouseEvent e ) {
		super.mouseExited( e );
		over = false;
	}
}
