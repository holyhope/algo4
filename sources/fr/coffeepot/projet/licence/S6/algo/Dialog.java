package fr.coffeepot.projet.licence.S6.algo;

import javax.swing.JOptionPane;

public class Dialog {
	public static void error( String text ) {
		error( text, new Exception() );
	}

	public static void error( String text, Exception e ) {
		String msg = e.getMessage();
		if ( msg == null ) {
			JOptionPane.showMessageDialog( null, text + ".", "error", JOptionPane.ERROR_MESSAGE );
		} else {
			JOptionPane.showMessageDialog( null, text + " :\n" + e.getMessage(), "error", JOptionPane.ERROR_MESSAGE );
		}
		System.err.println( e );
	}

	public static void alert( String text ) {
		alert( text, new Exception() );
	}

	public static void alert( String text, Exception e ) {
		String msg = e.getMessage();
		if ( msg == null ) {
			JOptionPane.showMessageDialog( null, text + ".", "Attention", JOptionPane.WARNING_MESSAGE );
		} else {
			JOptionPane.showMessageDialog( null, text + " :\n" + e.getMessage(), "Attention", JOptionPane.WARNING_MESSAGE );
		}
		e.printStackTrace();
	}

	public static boolean confirm( String text ) {
		return JOptionPane.showConfirmDialog( null, text, "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE ) == JOptionPane.YES_OPTION;
	}

	public static void success( String text ) {
		JOptionPane.showMessageDialog( null, text, "Executé", JOptionPane.INFORMATION_MESSAGE );
	}
}
