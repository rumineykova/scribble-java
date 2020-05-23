/**
 * Copyright 2008 The Scribble Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package betty16.lec1.httpshort;

import static betty16.lec1.httpshort.HttpShort.Http.Http.*;

import org.scribble.runtime.net.Buf;
import org.scribble.runtime.net.session.MPSTEndpoint;
import org.scribble.runtime.net.session.SocketChannelEndpoint;

import betty16.lec1.httpshort.HttpShort.Http.Http;
import betty16.lec1.httpshort.HttpShort.Http.channels.C.EndSocket;
import betty16.lec1.httpshort.HttpShort.Http.channels.C.Http_C_1;
import betty16.lec1.httpshort.HttpShort.Http.roles.C;
import betty16.lec1.httpshort.message.HttpShortMessageFormatter;
import betty16.lec1.httpshort.message.client.Request;
import betty16.lec1.httpshort.message.server.Response;

public class HttpShortC {

	public static void main(String[] args) throws Exception {
		Http http = new Http();
		try (MPSTEndpoint<Http, C> client = new MPSTEndpoint<>(http, C, new HttpShortMessageFormatter())) {
			String host = "www.doc.ic.ac.uk"; int port = 80;
			//String host = "summerschool2016.behavioural-types.eu"; int port = 80;
			//String host = "localhost"; int port = 8080;

			client.connect(S, SocketChannelEndpoint::new, host, port);
			new HttpShortC().run(new Http_C_1(client));
		}
	}

	
	


	

	

	

	
	
	private EndSocket run(Http_C_1 c) throws Exception {
		Buf<Response> buf = new Buf<>();

		EndSocket end = null;
				/*c.send(S, new Response("", ""))
				 .receive(S, Response, buf);*/

		System.out.println("Response:\n" + buf.val);
		
		return end;
	}


	

	

	

	

	


	
	
	
	//c.send(S, new Request("/~rhu/", "1.1", "www.doc.ic.ac.uk")).receive(S, Response, buf);







	
	
	/*
	private EndSocket run2(Http_C_1 c1, String host) throws Exception {
		Buf<Response> buf = new Buf<>();

		EndSocket end =
				c.send(S, new Request("/~rhu/", "1.1", host))
				//.send(S, new Request("/~rhu/", "1.1", host))
				 .receive(S, Response, buf);

		System.out.println("Response:\n" + buf.val);
		
		return end;
	}
	//*/
	//.send(S, new Response("1.1", "..body.."))
	//.send(S, new Request("/~rhu/", "1.1", host))
	//.receive(S, RESPONSE, new Buf<>());
}
