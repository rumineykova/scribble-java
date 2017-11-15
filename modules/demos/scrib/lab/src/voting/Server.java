package voting;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.management.relation.Role;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.net.Buf;
import org.scribble.net.ObjectStreamFormatter;
import org.scribble.net.scribsock.ScribServerSocket;
import org.scribble.net.scribsock.SocketChannelServer;
import org.scribble.net.session.MPSTEndpoint;
import org.scribble.net.session.SocketChannelEndpoint;

import voting.EProtocol.EVoting.EVoting;
import voting.EProtocol.EVoting.channels.S.EVoting_S_1;
import voting.EProtocol.EVoting.channels.S.EVoting_S_2;
import voting.EProtocol.EVoting.channels.S.EVoting_S_3;
import voting.EProtocol.EVoting.channels.S.EVoting_S_3_Cases;
import voting.EProtocol.EVoting.channels.S.EVoting_S_4;
import voting.EProtocol.EVoting.channels.S.ioifaces.Branch_S_V_No_String__V_Yes_String.Branch_S_V_No_String__V_Yes_String_Enum;
import voting.EProtocol.EVoting.roles.S;

public class Server
{	
	
	public static void main(String[] args) throws IOException, ScribbleRuntimeException, ExecutionException, InterruptedException, ClassNotFoundException
	{
		try (ScribServerSocket ss_v = new SocketChannelServer(7777))
		{
			EVoting vp = new EVoting();
			try (MPSTEndpoint<EVoting, S> se = new MPSTEndpoint<>(vp, EVoting.S, new ObjectStreamFormatter()))
			{
				System.out.println("before accept");
				
				se.accept(ss_v, EVoting.V);
				System.out.println("Accept");
				
				Map<Branch_S_V_No_String__V_Yes_String_Enum, Integer> result = new HashMap<Branch_S_V_No_String__V_Yes_String_Enum, Integer>(); 
				result.putIfAbsent(Branch_S_V_No_String__V_Yes_String_Enum.No,0); 
				result.putIfAbsent(Branch_S_V_No_String__V_Yes_String_Enum.Yes,0);
				
				EVoting_S_1 s1 = new EVoting_S_1(se);
				Buf<String> cid = new Buf<String>(); 
				
				EVoting_S_3_Cases cases = s1.receive(EVoting.V, EVoting.Authenticate, cid) 
								   			  .send(EVoting.V, EVoting.Ok, "new token")
								              .branch(EVoting.V); 
				
				
				EVoting_S_4 s5;
				Buf<String> voting = new Buf<String>();
				switch (cases.op){
				case No: 
						s5 = cases.receive(EVoting.No, voting); 
							 result.put(fromString("No"), 
								result.get(fromString("No")) + 1); 
						System.out.println("sending results " + result.get(fromString("No")));
						
						s5.send(EVoting.V, EVoting.Result, result.get(fromString("No"))); 
					break;
				case Yes:
						s5 = cases.receive(EVoting.Yes, voting);
							 result.put(fromString("Yes"), 
								result.get(fromString("Yes")) + 1);
					    s5.send(EVoting.V, EVoting.Result, result.get(fromString("Yes")));
					break;
				default:
					break;
				
				}
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	public static Branch_S_V_No_String__V_Yes_String_Enum fromString(String stringValue) throws Exception {
        switch (stringValue) {
            case "Yes": return Branch_S_V_No_String__V_Yes_String_Enum.Yes;
            case "No": return Branch_S_V_No_String__V_Yes_String_Enum.No;
            default: throw new Exception("Only Yes and No are allows as map keys");
        }
	}
}
