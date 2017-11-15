package voting;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.management.relation.Role;

import org.scribble.main.ScribbleRuntimeException;
import org.scribble.net.Buf;
import org.scribble.net.ObjectStreamFormatter;
import org.scribble.net.session.MPSTEndpoint;
import org.scribble.net.session.SocketChannelEndpoint;

import voting.EProtocol.EVoting.EVoting;
import voting.EProtocol.EVoting.channels.S.EVoting_S_1;
import voting.EProtocol.EVoting.channels.S.EVoting_S_2;
import voting.EProtocol.EVoting.channels.S.EVoting_S_3;
import voting.EProtocol.EVoting.channels.S.EVoting_S_3_Cases;
import voting.EProtocol.EVoting.channels.S.EVoting_S_4;
import voting.EProtocol.EVoting.channels.S.ioifaces.Branch_S_V_No_String__V_Yes_String.Branch_S_V_No_String__V_Yes_String_Enum;
import voting.EProtocol.EVoting.channels.S.ioifaces.Branch_S_V_No_String__V_Yes_String.Branch_S_V_No_String__V_Yes_String_Enum.*;
import voting.EProtocol.EVoting.roles.S;

public class Server
{	
	
	public static void main(String[] args) throws IOException, ScribbleRuntimeException, ExecutionException, InterruptedException, ClassNotFoundException
	{
		EVoting vp = new EVoting();
		try (MPSTEndpoint<EVoting, S> se = new MPSTEndpoint<>(vp, EVoting.S, new ObjectStreamFormatter()))
		{
			se.connect(EVoting.S, SocketChannelEndpoint::new, "localhost", 8888);
			
			Map<Branch_S_V_No_String__V_Yes_String_Enum, Integer> result = new HashMap<Branch_S_V_No_String__V_Yes_String_Enum, Integer>(); 
			result.putIfAbsent(Branch_S_V_No_String__V_Yes_String_Enum.No,0); 
			result.putIfAbsent(Branch_S_V_No_String__V_Yes_String_Enum.Yes,0);
			
			EVoting_S_1 s1 = new EVoting_S_1(se);
			Buf<String> cid = new Buf<String>(); 
			
			EVoting_S_2	s3 = s1.receive(EVoting.V, EVoting.Authenticate, cid); 
			EVoting_S_3 s4 = s3.send(EVoting.V, EVoting.Ok, "authnetication is successfull");
			
			EVoting_S_3_Cases cases = s4.branch(EVoting.V); 
			
			EVoting_S_4 s5;
			switch (cases.op){
			case No: 
					s5 = cases.receive(EVoting.No); 
						 result.put(fromString("No"), 
							result.get(fromString("No")) + 1); 
					s5.send(EVoting.V, EVoting.Result, result.get(fromString("No"))); 
				break;
			case Yes:
					s5 = cases.receive(EVoting.Yes);
						 result.put(fromString("Yes"), 
							result.get(fromString("Yes")) + 1);
				    s5.send(EVoting.V, EVoting.Result, result.get(fromString("Yes")));
				break;
			default:
				break;
			
			}
			
			//s5.send(EVoting.V, EVoting.Result, printResult(result));
			
		} catch (Exception e)
		{
			e.printStackTrace();
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
