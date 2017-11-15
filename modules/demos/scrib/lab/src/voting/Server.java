package voting;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.net.Buf;
import org.scribble.net.ObjectStreamFormatter;
import org.scribble.net.session.MPSTEndpoint;
import org.scribble.net.session.SocketChannelEndpoint;

import voting.EProtocol.EVoting.EVoting;
import voting.EProtocol.EVoting.channels.S.EVoting_S_1;
import voting.EProtocol.EVoting.channels.S.EVoting_S_3_Cases;
import voting.EProtocol.EVoting.channels.S.EVoting_S_4;
import voting.EProtocol.EVoting.roles.S;

public class Server
{
	public static void main(String[] args) throws IOException, ScribbleRuntimeException, ExecutionException, InterruptedException, ClassNotFoundException
	{
		EVoting vp = new EVoting();
		try (MPSTEndpoint<EVoting, S> se = new MPSTEndpoint<>(vp, EVoting.S, new ObjectStreamFormatter()))
		{
			se.connect(EVoting.S, SocketChannelEndpoint::new, "localhost", 8888);
			EVoting_S_1 s1 = new EVoting_S_1(se);
			
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
