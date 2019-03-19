package br.ufs.dcomp.ChatRabbitMQ;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RESTClient {
    
    public void obterPedido(String caminho){
        try {
            String username = "gexxalce";
            String password = "vSmzq8vnE5k9gw3zrvP2QvzF1GmKb5Gn";
         
            String usernameAndPassword = username + ":" + password;
            String authorizationHeaderName = "Authorization";
            String authorizationHeaderValue = "Basic " + java.util.Base64.getEncoder().encodeToString( usernameAndPassword.getBytes() );
         
            // Perform a request
            String restResource = "http://http-lb2-79bfab72cf72632d.elb.us-west-2.amazonaws.com"; //dominio do balanceador de carga
            Client client = ClientBuilder.newClient();
            Response resposta = client.target( restResource )
                .path(caminho)
                .request(MediaType.APPLICATION_JSON)
                .header( authorizationHeaderName, authorizationHeaderValue ) // The basic authentication header goes here
                .get();     // Perform a get
               
                if (resposta.getStatus() == 200) {
                	String json = resposta.readEntity(String.class);
                    System.out.println(json);
                }    
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    }
}
