package com.atomatus.util;

/**
 * <h1>ASCII Table and Description</h1>
 * <p>
 *     ASCII stands for American Standard Code for Information Interchange.
 *     Computers can only understand numbers, so an ASCII code is the numerical representation of
 *     a character such as 'a' or '@' or an action of some sort. ASCII was developed a long time ago
 *     and now the non-printing characters are rarely used for their original purpose.
 *     Below is the ASCII character as enum entry and this includes descriptions of the
 *     first 32 non-printing characters.
 * </p>
 * <p>
 *     ASCII was actually designed for use with teletypes and so the descriptions are somewhat obscure.
 *     If someone says they want your CV however in ASCII format, all this means is they want
 *     'plain' text with no formatting such as tabs, bold or
 *     underscoring - the raw format that any computer can understand.
 * </p>
 * <p>
 *     This is usually so they can easily import the file into their own applications without issues.
 *     Notepad.exe creates ASCII text, or in MS Word you can save a file as 'text only'
 * </p>
 * <a href="http://www.asciitable.com/">see more in www.asciitable.com</a>
 * @author Carlos Matos
 */
@SuppressWarnings("unused")
public enum AsciiTable {

	//region non-printing chars
	/**
	 * Null
	 */
	NUL,

	/**
	 * Start of heading
	 */
	SOH,

	/**
	 * Start of text
	 */
	STX,

	/**
	 * End of text
	 */
	ETX,

	/**
	 * End of transmission
	 */
	EOT,

	/**
	 * Enquiry
	 */
	ENQ,

	/**
	 * Acknowledge
	 */
	ACK,

	/**
	 * Bell
	 */
    BEL,

	/**
	 * Backspace
	 */
	BS,

	/**
	 * Horizontal tab
	 */
	HT,

	/**
	 * New line feed
	 */
	LF,

	/**
	 * Vertical tab
	 */
	VT,

	/**
	 * New page form feed
	 */
	FF,

	/**
	 * Carriage return
	 */
	CR,

	/**
	 * Shift out
	 */
	SO,

	/**
	 * Shift in
	 */
	SI,

	/**
	 * Data link escape
	 */
    DLE,

	/**
	 * Device control 1
	 */
	DC1,

	/**
	 * Device control 2
	 */
	DC2,

	/**
	 * Device control 3
	 */
	DC3,

	/**
	 * Device control 4
	 */
	DC4,

	/**
	 * Negative acknowledge
	 */
	NAK,

	/**
	 * Syncrhonous idle
	 */
	SYN,

	/**
	 * End of transmission block
	 */
	ETB,

	/**
	 * Cancel
	 */
	CAN,

	/**
	 * End of medium
	 */
	EM,

	/**
	 * Substitute
	 */
	SUB,

	/**
	 * Escape
	 */
	ESC,

	/**
	 * File separator
	 */
	FS,

	/**
	 * Group separator
	 */
	GS,

	/**
	 * Record separator
	 */
	RS,

	/**
	 * Unit separator
	 */
	US;
	//endregion

	public byte getCode() {
		return (byte) ordinal();
	}
}
