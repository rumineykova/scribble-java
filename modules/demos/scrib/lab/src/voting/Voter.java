package voting;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.net.Buf;
import org.scribble.net.ObjectStreamFormatter;
import org.scribble.net.session.MPSTEndpoint;
import org.scribble.net.session.SocketChannelEndpoint;

import voting.EProtocol.EVoting.EVoting;
import voting.EProtocol.EVoting.channels.V.EVoting_V_1;
import voting.EProtocol.EVoting.channels.V.EVoting_V_2_Cases;
import voting.EProtocol.EVoting.channels.V.EVoting_V_4;
import voting.EProtocol.EVoting.channels.V.EndSocket;
import voting.EProtocol.EVoting.roles.V;


public class Voter
{
	public static void main(String[] args) throws IOException, ScribbleRuntimeException, ExecutionException, InterruptedException, ClassNotFoundException
	{
		EVoting vp = new EVoting();
		try (MPSTEndpoint<EVoting, V> se = new MPSTEndpoint<>(vp, EVoting.V, new ObjectStreamFormatter()))
		{			
			se.connect(EVoting.S, SocketChannelEndpoint::new, "localhost", 7777);
			String name = "my name"; 
			System.out.println("Connected");

			EVoting_V_1 s1 = new EVoting_V_1(se);
			
			EVoting_V_2_Cases cases = s1.receive(EVoting.S, EVoting.Authenticate, name)
										 .branch(EVoting.S);;
			 
		    System.out.println("Authenticated");

			EVoting_V_4 s3 = null; 
			
			switch(cases.op){
				case Ok: 
						Buf<String> token = new Buf<>(); 
						s3 = cases.receive(EVoting.Ok, token)
						  	       .send(EVoting.S, EVoting.Maybe, token.val);
						 
					break;
				case Reject:
						Buf<String> reason = new Buf<>();  
						s3 = cases.receive(EVoting.S, EVoting.Reject,reason)
								   .send(EVoting.S, EVoting.Yes, "fake vote");
					break;
			
			}
			
			Buf<Integer> results = new Buf<>();
			s3.receive(EVoting.S, EVoting.Result, results);
			
			System.out.println("Done");
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
