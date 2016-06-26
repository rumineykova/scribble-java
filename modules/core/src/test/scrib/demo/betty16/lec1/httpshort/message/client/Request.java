package demo.betty16.lec1.httpshort.message.client;

import demo.betty16.lec1.httpshort.HttpShort.Http.Http;
import demo.betty16.lec1.httpshort.message.HttpShortMessage;

public class Request extends HttpShortMessage {

	private static final long serialVersionUID = 1L;

	public static final String HOST = "Host";
	public static final String USER_AGENT = "User-Agent";
	public static final String ACCEPT = "Accept";
	public static final String ACCEPT_LANGUAGE = "Accept-Language";
	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	public static final String DO_NOT_TRACK = "DNT";     
	public static final String CONNECTION = "Connection";

	public Request(String get, String http, String host, String userA, String accept, String acceptL, String acceptE, String dnt, String connection) {
		super(Http.Request, getHeadersAndBody(get, http, host, userA, accept, acceptL, acceptE, dnt, connection));
	}
	
	public Request(String get, String http, String host) {
		this(get, http, host, null, null, null, null, null, null);
	}

	protected static String getHeadersAndBody(String get, String http, String host, String userA, String accept, String acceptL, String acceptE, String dnt, String connection) {
		return " "
				+ get + " " + HttpShortMessage.HTTP + "/" + http + HttpShortMessage.CRLF
				+ Request.HOST + ": " + host + HttpShortMessage.CRLF
				+ ((userA == null) ? "" : Request.USER_AGENT + ": " + userA + HttpShortMessage.CRLF)
				+ ((accept == null) ? "" : Request.ACCEPT + ": " + accept + HttpShortMessage.CRLF)
				+ ((acceptL == null) ? "" : Request.ACCEPT_LANGUAGE + ": " + acceptL + HttpShortMessage.CRLF)
				+ ((acceptE == null) ? "" : Request.ACCEPT_ENCODING + ": " + acceptE + HttpShortMessage.CRLF)
				+ ((dnt == null) ? "" : Request.DO_NOT_TRACK + ": " + dnt + HttpShortMessage.CRLF)
				+ ((connection == null) ? "" : Request.CONNECTION + ": " + connection + HttpShortMessage.CRLF)
				+ "" + HttpShortMessage.CRLF;  // Empty body
	}
}
