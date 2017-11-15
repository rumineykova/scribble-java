package calculator;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.net.ObjectStreamFormatter;
import org.scribble.net.scribsock.ScribServerSocket;
import org.scribble.net.scribsock.SocketChannelServer;
import org.scribble.net.session.MPSTEndpoint;
import org.scribble.net.session.SocketChannelEndpoint;

import voting.EProtocol.EVoting.EVoting;
import calculator.EProtocol.Calc.Calc;
import calculator.EProtocol.Calc.channels.S.Calc_S_1;
import calculator.EProtocol.Calc.roles.S;


public class Server
{
	public static void main(String[] args) throws IOException, ScribbleRuntimeException, ExecutionException, InterruptedException, ClassNotFoundException
	{
		try (ScribServerSocket ss_C = new SocketChannelServer(7777))
		{
			Calc calculator = new Calc();
			try (MPSTEndpoint<Calc, S> se = new MPSTEndpoint<>(calculator, Calc.S, new ObjectStreamFormatter()))
			{
				se.accept(ss_C, Calc.C);
				Calc_S_1 s1 = new Calc_S_1(se);
				
				// todo: Implement the rest ...
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
