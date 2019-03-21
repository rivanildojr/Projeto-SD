package br.ufs.dcomp.ChatRabbitMQ;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RESTClient {
    
    public void obterPedido(String caminho, String opcao){
        try {
            String username = "rivanildojr";
            String password = "rivas7892";
         
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
                    JSONParser jsonParser = new JSONParser();
                    Object objeto = jsonParser.parse(json);
                    JSONArray jsonArray = (JSONArray) objeto;
                    int i = jsonArray.size();
                    for(Object item : jsonArray){
                        String nome = pegaNome((JSONObject) item, opcao);
                        i=i-1;
                        if(!nome.equals("")){
                            System.out.print(nome);
                            if(i != 0){
                                System.out.print(", ");
                            }
                        }
                    }
                    System.out.println();
                }    
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    }
    
    public static String pegaNome(JSONObject objeto, String opcao){
        String nome = (String) objeto.get(opcao);
        return nome;
    }
}
