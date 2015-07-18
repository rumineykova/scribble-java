package demo.http.message.server;

import demo.http.Http;
import demo.http.message.HeaderField;

public class ContentType extends HeaderField
{
	private static final long serialVersionUID = 1L;

	public ContentType(String type)
	{
		super(Http.CONTENTT, type);
	}
}