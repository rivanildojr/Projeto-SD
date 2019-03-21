package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.util.*;
import java.io.IOException;
import com.google.protobuf.ByteString;

public class Chat{
  
  public static String userAtual = "";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri("amqp://rivanildojr:rivas7892@amqp-lb2-fe31f4738626da01.elb.us-west-2.amazonaws.com");
    
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();// fila normal
    Channel channel_arquivos = connection.createChannel();// fila de arquivos
    
    Scanner sc = new Scanner(System.in);
    Grupo grupo = new Grupo(channel, channel_arquivos);
    
    Mensagem msg = new Mensagem();
    
    System.out.print("User: ");
    String user = sc.nextLine();
    String nomeGrupo = "";
    msg.criarDiretorio(user);
    String QUEUE_NAME = user;
    String QUEUE_NAME_FILE = QUEUE_NAME + "F";
    
                      //(queue-name, durable, exclusive, auto-delete, params); 
    channel.queueDeclare(QUEUE_NAME, false,   false,     false,       null);
    channel_arquivos.queueDeclare(QUEUE_NAME_FILE, false,   false,     false,       null);
    
    
// ------------------------------------ RECEPTOR ------------------------------------
    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try{
          msg.recebeMessagem(body, user, Chat.userAtual);
        } catch (Exception ex) {
           System.out.println (ex.toString());
        }
      }
    };
    
// ------------------------------------- CHAT ----------------------------------------- 
    String current = "";
    String prompt = ">> ";
    String line = "";
  
    channel.basicConsume(QUEUE_NAME, true, consumer);
    channel_arquivos.basicConsume(QUEUE_NAME_FILE, true, consumer);
    
    // While principal
    while(line.equals(".") == false){
      System.out.print(Chat.userAtual + prompt + current);
      line = sc.nextLine();
        
      if(line.equals(".") == true) 
        break; //saida do chat
      if(line.charAt(0) == '@'){//Muda usuario no prompt
        Chat.userAtual = line;
        nomeGrupo = "";
      }
      else if(line.charAt(0) == '#'){//mensagem para grupo
        Chat.userAtual = line;
        nomeGrupo = line.substring(1);
      }
      else if(line.charAt(0) == '!'){//verifica comando
        grupo.verificaMensagem(line, user, Chat.userAtual, nomeGrupo);
      }
      else if(Chat.userAtual.equals("") == false){  
        if(Chat.userAtual.charAt(0) == '#') //caso o envio seja para um grupo
          msg.enviarMessagem(user, line, "", channel, nomeGrupo);
        else 
          msg.enviarMessagem(user, line, Chat.userAtual.substring(1), channel, ""); //caso o envio seja para outro usuario
      }
    }
    channel.close();
    connection.close();
  }
}