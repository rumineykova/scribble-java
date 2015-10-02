package org.scribble.net.session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.scribble.main.RuntimeScribbleException;
import org.scribble.net.ScribInterrupt;
import org.scribble.net.ScribMessage;

public abstract class BinaryChannelEndpoint
{
	protected SessionEndpoint se;
	
	//protected BinaryChannelEndpoint parent;
	private AbstractSelectableChannel c;
	private ByteBuffer bb;

	//private final SocketChannel;
	protected final List<ScribMessage> msgs = new LinkedList<>();
	
	private boolean isClosed = false;

	private int count = 0;  // How many ScribMessages read so far
	private int ticket = 0;  // Index of the next expected ScribMessage

	// Server side
	protected BinaryChannelEndpoint(SessionEndpoint se, AbstractSelectableChannel c) throws IOException
	{
		this.bb = ByteBuffer.allocate(16921);  // FIXME: size  // Use put mode as default
		init(se, c);
	}

	// Client side
	protected BinaryChannelEndpoint()
	{
		this.bb = ByteBuffer.allocate(16921);  // FIXME: size  // Use put mode as default
	}
	
	public abstract void initClient(SessionEndpoint se, String host, int port) throws IOException;
	
	protected void init(SessionEndpoint se, AbstractSelectableChannel c) throws IOException
	{
		this.se = se;
		this.c = c;
		this.c.configureBlocking(false);
	}

	//protected BinaryChannelEndpoint(BinaryChannelEndpoint c)
	public void wrapChannel(BinaryChannelEndpoint c)
	{
		this.se = c.se;
		//this.msgs.addAll(c.msgs);  // Guaranteed to be empty/0 for reconnect?
		//this.count = c.count;
		//this.ticket = c.ticket;
		//this.parent = c;
		this.c = c.c;
		this.bb = c.bb;
		
		//FIXME: complete all pending futures on parent chan -- no: not enough by itself, that is just reading the already-deserialized cache
		//FIXME: pull all pending data out of parent chan (due to selector not handling it yet -- in send states, we just need to clear all expected messages up to this point)
		//  -- so that wrapper handshake is starting clean
	}
	
	public AbstractSelectableChannel getSelectableChannel()  // For asynchrony (via nio Selector) -- maybe implement/extend instead
	{
		return this.c;
	}
	
	public void write(ScribMessage m) throws IOException
	{
		writeBytes(this.se.smf.toBytes(m));
	}

	// Default CompletableFuture executed by common forkjoin pool -- so all messages that are received/async'd will eventually be pulled from the queue (no manual GC necessary)
	public synchronized CompletableFuture<ScribMessage> getFuture()
	{
		// FIXME: better exception handling (integrate with Future interface?)
		return CompletableFuture.supplyAsync(() ->
				{
					try
					{
						ScribMessage m = read(getTicket());
						if (m instanceof ScribInterrupt)  // FIXME: hacked in
						{
							throw new RuntimeScribbleException((Throwable) ((ScribInterrupt) m).payload[0]);
						}
						return m;
					}
					catch(IOException e)
					{
						throw new RuntimeScribbleException(e);
					}
				});
	}
	
	private synchronized ScribMessage read(int ticket) throws IOException
	{
		try
		{
			while (this.count < ticket && !this.isClosed)
			{
				wait();
			}
			while (this.msgs.isEmpty() && !this.isClosed)
			{
				wait();
			}
			if (this.isClosed)
			{
				throw new IOException("Channel closed");
			}
			this.count++;
			return this.msgs.remove(0);
		}
		catch (InterruptedException e)
		{
			throw new IOException(e);
		}
	}
	
	protected synchronized void enqueue(ScribMessage m)
	{
		this.msgs.add(m);
		this.count++;
		notify();
	}

	public abstract void writeBytes(byte[] bs) throws IOException;
	protected abstract void readBytesIntoBuffer() throws IOException;  // synchronized (against read)  // bytes ready for reading: try to deserialize and then enqueue, or else cache for later
	
	public synchronized void readAndEnqueueMessages() throws ClassNotFoundException, IOException  // Here for synchronisation
	{
		readBytesIntoBuffer();
		ScribMessage m;
		while ((m = this.se.smf.fromBytes(this.bb)) != null)
		{
			enqueue(m);
		}
	}
	
	public synchronized void close() throws IOException
	{
		this.isClosed = true;
		notify();
	}
	
	public synchronized int getTicket()
	{
		return ++this.ticket;
	}
	
	// post: bb:put
	public ByteBuffer getBuffer()
	{
		return this.bb;
	}
}