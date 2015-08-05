package demo.smtp.message;

import java.nio.charset.Charset;

import org.scribble.net.ScribMessage;
import org.scribble.sesstype.name.Op;
import org.scribble.util.Caller;

import demo.smtp.SMTP;

public abstract class SmtpMessage extends ScribMessage
{
	public static final String _220 = "220";
	public static final String _250 = "250";
	public static final String _235 = "235";
	public static final String _535 = "535";
	public static final String _501 = "501";
	public static final String _354 = "354";

	public static final String EHLO = "ehlo";
	public static final String STARTTLS = "starttls";
	public static final String AUTH = "auth";
	public static final String MAIL = "mail";
	public static final String RCPT = "rcpt";
	public static final String SUBJECT = "subject";
	public static final String DATA = "data";
	public static final String QUIT = "quit";

	public static final String DATA_LINE = "";
	public static final String END_OF_DATA = "";

	private static final long serialVersionUID = 1L;
	
	protected static final String CRLF = "\r\n";
	
	public SmtpMessage(Op op)
	{
		super(op);
	}

	public SmtpMessage(Op op, String body)
	{
		super(op, body);
	}
	
	public String getBody()
	{
		return (this.payload.length == 0) ? "" : (String) this.payload[0];
	}

	public byte[] toBytes()
	{
		byte[] bs = (getOpString(this.op) + " " + getBody() + SmtpMessage.CRLF).getBytes(SmtpMessageFormatter.cs);  // Can give "utf-8" as arg directly
		return bs;
	}
	
	@Override
	public String toString()
	{
		return new String(toBytes());
	}
	
	protected static String getOpString(Op op)
	{
		return
					(op.equals(SMTP._220)) ? SmtpMessage._220
				: (op.equals(SMTP._250)) ? SmtpMessage._250
				: (op.equals(SMTP._250_)) ? SmtpMessage._250
				: (op.equals(SMTP._235)) ? SmtpMessage._235
				: (op.equals(SMTP._535)) ? SmtpMessage._535
				: (op.equals(SMTP._501)) ? SmtpMessage._501
				: (op.equals(SMTP._354)) ? SmtpMessage._354
				: (op.equals(SMTP.EHLO)) ? SmtpMessage.EHLO
				: (op.equals(SMTP.STARTTLS)) ? SmtpMessage.STARTTLS
				: (op.equals(SMTP.AUTH)) ? SmtpMessage.AUTH
				: (op.equals(SMTP.MAIL)) ? SmtpMessage.MAIL
				: (op.equals(SMTP.RCPT)) ? SmtpMessage.RCPT
				: (op.equals(SMTP.SUBJECT)) ? SmtpMessage.SUBJECT
				: (op.equals(SMTP.DATA)) ? SmtpMessage.DATA
				: (op.equals(SMTP.QUIT)) ? SmtpMessage.QUIT
				: (op.equals(SMTP.DATALINE)) ? SmtpMessage.DATA_LINE
				: (op.equals(SMTP.ATAD)) ? SmtpMessage.END_OF_DATA
				: new Caller().call(() -> { throw new RuntimeException("TODO: " + op); });
	}
}
