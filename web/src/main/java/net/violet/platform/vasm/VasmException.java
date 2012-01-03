package net.violet.platform.vasm;

import java.io.IOException;

/**
 * Classe pour une exception décrivant une erreur de compilation.
 */
public class VasmException extends Exception {

	VasmException(int inLine, String inMessage, Throwable inThrowable) {
		super("line " + inLine + ": " + inMessage, inThrowable);
	}

	VasmException(int inLine, String inMessage) {
		super("line " + inLine + ": " + inMessage);
	}

	/**
	 * Construction à partir d'une erreur I/O.
	 * 
	 * @param anException
	 */
	public VasmException(IOException anException) {
		super(anException);
	}
}
