package demo.smtp.message.client;

import org.scribble.sesstype.name.Op;

import demo.smtp.message.SmtpMessage;
import demo.smtp.message.SmtpMessageFormatter;

// FIXME: rename
public class DataLine extends SmtpMessage
{
	private static final long serialVersionUID = 1L;

	public DataLine()
	{
		super(Op.EMPTY_OPERATOR);
	}

	public DataLine(String body)
	{
		super(Op.EMPTY_OPERATOR, body);
		if (body.equals("."))
		{
			throw new RuntimeException("Illegal body: " + body);
		}
	}

	@Override
	public byte[] toBytes()
	{
		// No space after op and no implicit CRLF
		return (SmtpMessage.getOpString(this.op) + getBody()).getBytes(SmtpMessageFormatter.cs);
	}
}
