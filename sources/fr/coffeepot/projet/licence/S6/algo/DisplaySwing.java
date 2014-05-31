package fr.coffeepot.projet.licence.S6.algo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.Timer;

import fr.upem.pperonne.caterer.Graph;
import fr.upem.pperonne.caterer.GraphFlow;

@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class DisplaySwing extends JFrame implements ActionListener, Display {
	private final HashMap<Graph,ContentSwing> graphs = new HashMap<>();
	private final HashMap<Graph,JRadioButtonMenuItem> menus = new HashMap<>();
	private Timer timer;
	private JPanel home = new JPanel();
	private static String defaultTitle = "CateRer - Home";
	private ArrayList<ChangePageListener> changeListeners = new ArrayList<>();
	private ArrayList<AddGraphListener> addListeners = new ArrayList<>();

	public DisplaySwing() {
		setBackground( Color.BLACK );
		timer = new Timer( 30 , this );
		timer.setInitialDelay( 500 );
		timer.start();
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setPreferredSize( new Dimension( 500, 500 ) );
		setBackground( Color.BLACK );
		setResizable( false );
		initMenu();
		initHome();
		home();
	}
	
	private void initHome() {
		home.setLayout( new BorderLayout() );

		Label label = new Label( "Pour commencer, veuillez ouvrir un nouveau graphe." );
		label.setAlignment( Label.CENTER );
		home.add( label, BorderLayout.CENTER );

		Dimension dimension = getSize();
/*
		JButton exit = new JButton( "Quitter" );
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				System.exit( 0 );
			}
		});
		exit.setSize( dimension.width / 3, 50 );
		exit.set
		home.add( exit, BorderLayout.SOUTH );
*/
		JButton open = new JButton( "Ouvrir un graphe" );
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				openGraph();
			}
		});
		open.setSize( dimension.width / 3, 50 );
		home.add( open, BorderLayout.SOUTH );
		
		addChangePageListener( new ChangePageListener() {
			@Override
			public void apply( Graph graph ) {
				if ( graph == null ) {
					setTitle( defaultTitle );
				} else {
					ContentSwing content = graphs.get( graph );
					setTitle( content.getTitle() );
				}
			}
		} );
	}

	public void setRefresh( int refresh ) {
		timer.setDelay( refresh );
	}

	@Override
	public void add( final Graph graph, String title ) {
		ContentSwing content = new ContentSwing( graph, title );
		content.setBackground( getBackground() );
		graphs.put( graph, content );
		triggerAddGraphListener( graph );
		if ( isHome() ) {
			swap( graph );
		}
	}

	private Graph getCurrentGraph() {
		Graph graphToRemove = null;
		Container content = getContentPane();
		Entry<Graph,ContentSwing> entry;
		Iterator<Entry<Graph,ContentSwing>> it = graphs.entrySet().iterator();
		while ( it.hasNext() ) {
			entry = it.next();
			if ( entry.getValue().equals( content ) ) {
				graphToRemove = entry.getKey();
				break;
			}
		}
		return graphToRemove;
	}

	private void initMenu() {
		JMenu menuFichier = new JMenu( "Fichier" );
		JMenuBar menu = new JMenuBar();
		final JMenuItem openGraph = new JMenuItem( "Ouvrir" ),
			closeGraph = new JMenuItem( "Fermer le graphe" ),
			saveGraph = new JMenuItem( "Enregistrer" );
		closeGraph.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				removeCurrentGraph();
			}
		} );
		openGraph.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				openGraph();
			}
		} );
		saveGraph.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				Runnable thread = new Runnable() {
					@Override
					public void run() {
						saveCurrentGraph();
					}
				};
				thread.run();
			}
		} );
		JMenuItem exit = new JMenuItem( "Quitter" );
		exit.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit( 0 );
			}
		} );
		addChangePageListener( new ChangePageListener() {
			@Override
			public void apply( Graph graph ) {
				if ( graph == null ) {
					if ( saveGraph.isEnabled() ) {
						saveGraph.setEnabled( false );
					}
					if ( closeGraph.isEnabled() ) {
						closeGraph.setEnabled( false );
					}
				} else {
					if ( ! saveGraph.isEnabled() ) {
						saveGraph.setEnabled( true );
					}
					if ( ! closeGraph.isEnabled() ) {
						closeGraph.setEnabled( true );
					}
				}
			}
		});
		menuFichier.add( openGraph );
		menuFichier.add( saveGraph );
		menuFichier.add( closeGraph );
		menuFichier.addSeparator();
		menuFichier.add( exit );

		menu.add( menuFichier );

		final JMenu menuGraph = new JMenu( "Graphes" );
		final ButtonGroup groupGraphMenu = new ButtonGroup();
		menuGraph.setEnabled( false );
		addAddGraphListener( new AddGraphListener() {
			@Override
			public void add( final Graph graph ) {
				ContentSwing content = graphs.get( graph );
				JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem( content.getTitle() );
				menuItem.addActionListener( new ActionListener() {
					@Override
					public void actionPerformed( ActionEvent arg0 ) {
						swap( graph );
					}
				} );
				groupGraphMenu.add( menuItem );
				menus.put( graph, menuItem );
				menuGraph.add( menuItem );
				if ( graphs.size() == 0  ) {
					if ( menuGraph.isEnabled() ) {
						menuGraph.setEnabled( false );
					}
				} else if ( ! menuGraph.isEnabled() ) {
					menuGraph.setEnabled( true );
				}
			}

			@Override
			public void remove( Graph graph ) {
				menuGraph.remove( menus.get( graph ) );
				if ( menuGraph.getItemCount() < 2 ) {
					menuGraph.setEnabled( false );
				}
			}
		} );
		menu.add( menuGraph );

		final JMenu algos = new JMenu( "Algorithmes" );
		final JMenuItem startAlgos = new JMenuItem( "Démarrer" );
		startAlgos.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				Graph current = getCurrentGraph();
				Thread thread = new Thread( (Runnable) current );
				try {
					thread.start();
				} catch ( IllegalStateException ex ) {
					Dialog.error( "Impossible de démarrer l'algorithme", ex );
				}
			}
		} );
		algos.add( startAlgos );

		final JMenu convert = new JMenu( "Préparer" );

		JMenuItem simplexe = new JMenuItem( "Simplexe Réseau" );
		simplexe.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				Runnable run = new Runnable() {
					@Override
					public void run() {
						final Graph current = getCurrentGraph();
						GraphFlow graph = new GraphFlow( current );
						add( graph, getTitle() + " - Simplexe Réseau" );
						swap( graph );
					}
				};
				Thread thread = new Thread( run );
				thread.start();
			}
		});
		convert.add( simplexe );

		algos.add( convert );

		menu.add( algos );
		addChangePageListener( new ChangePageListener() {
			@Override
			public void apply( Graph graph ) {
				if ( graph == null ) {
					algos.setEnabled( false );
				} else {
					algos.setEnabled( true );
					if ( graph instanceof Runnable ) {
						startAlgos.setEnabled( true );
						convert.setEnabled( false );
					} else {
						startAlgos.setEnabled( false );
						convert.setEnabled( true );
					}
				}
			}
		} );

		setJMenuBar( menu );
	}

	private void addAddGraphListener( AddGraphListener listener ) {
		addListeners.add( listener );
	}
	
	public void removeAddGraphListener( ChangePageListener listener ) {
		addListeners.remove( listener );
	}

	public void addChangePageListener( ChangePageListener listener ) {
		changeListeners.add( listener );
	}
	
	public void removeChangePageListener( ChangePageListener listener ) {
		changeListeners.remove( listener );
	}

	private void saveCurrentGraph() {
		Graph graph = getCurrentGraph();
		if ( graph == null ) {
			Dialog.error( "Aucun graphe n'est ouvert" );
			return;
		}
		FileDialog fd = new FileDialog( this, "Test", FileDialog.SAVE );
        fd.setVisible( true );
        String path = fd.getFile();
        if ( path != null ) {
        	File file = new File( path );
        	PrintWriter print;
			try {
				print = new PrintWriter( file );
				print.write( graph.toString() );
	    		print.close();
			} catch ( FileNotFoundException e ) {
				Dialog.error( "Fichier introuvable", e );
			}
        }
	}

	private void openGraph() {
		FileDialog fd = new FileDialog( this, "Test", FileDialog.LOAD );
		String home = System.getenv( "HOME" );
		if ( home == null ) {
			home = System.getenv( "HOMEDRIVE" ) + System.getenv( "HOMEPATH" );
			if ( home != null ) {
				File file = new File( home );
				if ( file.isDirectory() ) {
					fd.setDirectory( file.getAbsolutePath() );
				}
			}
		} else {
			File file = new File( home );
			if ( file.isDirectory() ) {
				fd.setDirectory( file.getAbsolutePath() );
			}
		}
		fd.setVisible( true );
		String filePath = fd.getFile();
		if ( filePath != null ) {
			File file = new File( filePath );
			Scanner stream;
			try {
				stream = new Scanner( new FileReader( file ) );
				try {
					Graph G = new Graph( stream );
					add( G, file.getName() );
					swap( G );
				} catch ( IllegalArgumentException ex ) {
					Dialog.error( "Impossible de charger le graphe", ex );
				} finally {
					stream.close();
				}
			} catch ( FileNotFoundException ex ) {
				Dialog.error( "Impossible d'ouvrir le fichier", ex );
			}
		}
	}

	private void removeCurrentGraph() {
		if ( graphDisplayed() ) {
			remove( getCurrentGraph() );
		}
	}
	
	private boolean isHome() {
		return home.equals( getContentPane() );
	}
	
	private boolean graphDisplayed() {
		return graphs.containsValue( getContentPane() );
	}

	public void remove( Graph graph ) throws IllegalArgumentException {
		if ( ! graphs.containsKey( graph ) ) {
			Dialog.error( "Le graphe n'est pas ouvert." );
		}
		graphs.remove( graph );
		triggerRemoveGraphListener( graph );
		if ( graphs.isEmpty() ) {
			home();
		} else {
			graph = (Graph) graphs.keySet().toArray()[0];
			swap( graph );
		}
	}

	public void home() {
		setContentPane( home );
		triggerChangePageListeners( null );
	}

	public boolean swap( Graph graph ) {
		if ( ! graphs.containsKey( graph ) ) {
			return false;
		}
		menus.get( graph ).setSelected( true );
		ContentSwing content = graphs.get( graph );
		setContentPane( content );
		triggerChangePageListeners( graph );
		validate();
		repaint();
		return true;
	}
	
	private void triggerChangePageListeners( Graph graph ) {
		for ( ChangePageListener listener: changeListeners ) {
			listener.apply( graph );
		}
	}

	private void triggerAddGraphListener( Graph graph ) {
		for ( AddGraphListener listener: addListeners ) {
			listener.add( graph );
		}
	}

	private void triggerRemoveGraphListener( Graph graph ) {
		for ( AddGraphListener listener: addListeners ) {
			listener.remove( graph );
		}
	}

	@Override
	public void actionPerformed( ActionEvent e ) {
		Container content = getContentPane();
		if ( content instanceof ContentSwing ) {
			((ContentSwing) content).actionPerformed( e );
		}
	}
}
