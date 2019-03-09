package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.util.*;
import java.io.IOException;
import com.google.protobuf.ByteString;

public class Chat{
  
  public static String userAtual = "";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri("amqp://rivanildojr:rivas7892@ec2-34-220-36-234.us-west-2.compute.amazonaws.com");
    
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    //Channel channel_arquivos = connection.createChannel();
    
    Scanner sc = new Scanner(System.in);
    Grupo grupo = new Grupo(channel);
    
    Mensagem msg = new Mensagem();
    
    System.out.print("User: ");
    String user = sc.nextLine();
    
    String QUEUE_NAME = user;
    
                      //(queue-name, durable, exclusive, auto-delete, params); 
    channel.queueDeclare(QUEUE_NAME, false,   false,     false,       null);
    //channel_arquivos.queueDeclare(QUEUE_NAME, false,   false,     false,       null);
    
    
// ------------------------------------ RECEPTOR ------------------------------------
    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("");
       try{
         System.out.println(msg.recebeMessagem(body, user));
       } catch (Exception ex) {
         System.out.println (ex.toString());
       }
       System.out.print(Chat.userAtual + ">>");
    }
    };
  

    
// ------------------------------------- CHAT ----------------------------------------- 
    String current = "";
    String prompt = ">> ";
    String line;
  
    channel.basicConsume(QUEUE_NAME, true,    consumer);
    while(true){
      System.out.print(current+prompt);
      line = sc.nextLine();
      grupo.verificaMensagem(line, user);
      while(line.charAt(0) == '@' || line.charAt(0) == '#'){
        Chat.userAtual = line;
        do{
          System.out.print(current+Chat.userAtual+prompt);
          line = sc.nextLine();
          grupo.verificaMensagem(line,Chat.userAtual.substring(1));
          if(Chat.userAtual.charAt(0) == '#'){
            System.out.println("grupo: "+ Chat.userAtual.substring(1));
            String gr = Chat.userAtual.substring(1);
            do{
              msg.enviarMessagem(user, line, "", channel, gr);
              System.out.print(current+'#'+gr+prompt);
              line = sc.nextLine();
              System.out.println("line: " + line.charAt(0));
              grupo.verificaMensagem(line,Chat.userAtual.substring(1));
            } while(line.charAt(0) != '@' && line.charAt(0) != '#');
            System.out.println("saiu...");
          }
          System.out.println("line: " + line.charAt(0));
          if(line.charAt(0) == '@'){
            Chat.userAtual = line;
            System.out.println("user: " + Chat.userAtual.substring(1));
            while(true){
              System.out.print(current+Chat.userAtual+prompt);
              line = sc.nextLine();
              if(line.charAt(0) == '@' && line.charAt(0) == '#') break;
              grupo.verificaMensagem(line,Chat.userAtual.substring(1));
              msg.enviarMessagem(user, line, Chat.userAtual.substring(1), channel, "");
            }
          }
          if(Chat.userAtual.charAt(0) == '@'){
            System.out.println("user: " + Chat.userAtual.substring(1));
            do {
              msg.enviarMessagem(user, line, Chat.userAtual.substring(1), channel, "");
              System.out.print(current+Chat.userAtual+prompt);
              line = sc.nextLine();
              grupo.verificaMensagem(line,Chat.userAtual.substring(1));
            } while(line.charAt(0) != '@' && line.charAt(0) != '#');
          }
        } while(line.charAt(0) != '@' || line.charAt(0) != '#');
      }
    }
  }
}