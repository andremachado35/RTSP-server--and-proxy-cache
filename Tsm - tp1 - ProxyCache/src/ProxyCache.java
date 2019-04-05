

import java.net.*;
import java.io.*;
import java.util.*;


public class ProxyCache{
    /** Porta para o proxy */
    private static int port;
   
    private static ServerSocket socket;
   
    public static void init(int p) {
	port = p;
	try {
	    socket = new ServerSocket(port);
	} catch (IOException e) {
	    System.out.println("Error creating socket: " + e);
	    System.exit(-1);
	}
    }

    public static void handle(Socket client) {
	Socket server = null;
	HttpRequest request = null;
	HttpResponse response = null;

	/*  Requisicao do processo. Se existir excessoes , retorna e finaliza a thread.
	 * Isso significa que o cliente espera pelo while ate da timeout*/

	/* Ler requisicao */
	try {
	    BufferedReader fromClient = 
		new BufferedReader(new InputStreamReader(client.getInputStream()));
	    System.out.println("\n"+"\n"+"Reading request...");
	    request = new HttpRequest(fromClient);
	    System.out.println("Got request...");


	} catch (IOException e) {
	    System.out.println("Error reading request from client: " + e);
	    return;
	}
	try {
		server = new Socket(request.getHost(), request.getPort());
		DataOutputStream toServer = new DataOutputStream(server.getOutputStream());
		Singleton_http single_http = Singleton_http.getInstance();
		HttpResponse respostax = single_http.get_Response_by_httprequest(request.getHost());
		System.out.println("A resposta é" + "\t" + String.valueOf(respostax) + "\n");
		if (respostax != null) {
			toServer.writeBytes(request.toString());
			System.out.println("Tem este URI na CACHE, não precisa carregar de novo:" + "\t" + request.getHost() + "\n" + "\n");
			DataInputStream fromServer = new DataInputStream(server.getInputStream());
			response = new HttpResponse(fromServer);
			DataOutputStream toClient = new DataOutputStream(client.getOutputStream());
			toClient.writeBytes(response.toString());
			toClient.write(response.body);
			toClient.close();
			server.close();
		} else {
			single_http.put_httprequest(request.getHost());
			toServer.writeBytes(request.toString());

		}
	}
		catch (UnknownHostException e) {
			System.out.println("Unknown host: " + request.getHost());
			System.out.println(e);
			return;
		} catch (IOException e) {
			System.out.println("Error writing request to server: " + e);
			return;
		}

		try {
			DataInputStream fromServer = new DataInputStream(server.getInputStream());
			response = new HttpResponse(fromServer);
			Singleton_http single_http = Singleton_http.getInstance();
			String requestx = single_http.get_httprequest();
			single_http.add_Request_to_Response(requestx,response);
			single_http.add_Response_to_Request(response,requestx);
			DataOutputStream toClient =
					new DataOutputStream(client.getOutputStream());
			single_http.add_Pedido_to_Data(requestx,toClient);
			toClient.writeBytes(response.toString());
			toClient.write(response.body);
			toClient.close();
			server.close();


		} catch (IOException e) {
			System.out.println("Error writing response to client: " + e);
		}

	/* Ler e colocar a resposta para o cliente */

}

    /** Ler argumentos da linha de comando e inicia o proxy */
    public static void main(String args[]) {
	int myPort = 8787;

	init(myPort);
	
	Socket client = null;
	
	while (true) {
	    try {
		client = socket.accept();
		System.out.println("Got connection " + client);
		ProxyCacheThread request = new ProxyCacheThread(client);
		// Criar uma  nova thread para processar a requisição.
		Thread thread = new Thread(request);
		//Iniciar a thread.
		thread.start();
		
	    } catch (IOException e) {
		System.out.println("Error reading request from client: " + e);
		continue;
	    }
	}

    }


}

class ProxyCacheThread implements Runnable
{
	private Socket client = null;
	
	public ProxyCacheThread(Socket socket)
	{
		this.client = socket;
	}
	@Override
	public void run() {
		ProxyCache proxy = new ProxyCache(); 
		proxy.handle(client);
	}
}
