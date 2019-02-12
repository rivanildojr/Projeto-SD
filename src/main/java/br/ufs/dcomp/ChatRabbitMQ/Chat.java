package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.IOException;

public class Chat {
  
  public static String userAtual= ""; 

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUri("amqp://rivanildojr:rivas7892@ec2-34-220-36-234.us-west-2.compute.amazonaws.com");
    
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    
    Scanner sc = new Scanner(System.in);
    
    System.out.print("User: ");
    String user = sc.nextLine();
    
    String QUEUE_NAME = user;
                      //(queue-name, durable, exclusive, auto-delete, params); 
    channel.queueDeclare(QUEUE_NAME, false,   false,     false,       null);
    
    Consumer consumer = new DefaultConsumer(channel) {
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        System.out.println("");
        System.out.println(message);
        System.out.print(Chat.userAtual + ">> ");
      }
    };
    
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat horaFormat = new SimpleDateFormat("HH:mm");
    Date date = new Date();
    String data = dateFormat.format(date);
    String hora = horaFormat.format(date);
    
    String current = "";
    String prompt = ">> ";
    String line;
    while(true){
      channel.basicConsume(QUEUE_NAME, true, consumer);
      System.out.print(current+prompt);
      line = sc.nextLine();
      while(line.charAt(0) == '@'){
        Chat.userAtual = line;
        do{
          System.out.print(current+Chat.userAtual+prompt);
          line = sc.nextLine();
          if(line.charAt(0) != '@' && line.charAt(0) != '!'){
            String message = "(" + data + " às " + hora + ") " + Chat.userAtual.substring(1, Chat.userAtual.length()) + " diz: " + line;
            channel.basicPublish("",Chat.userAtual.substring(1, Chat.userAtual.length()), null,  message.getBytes("UTF-8"));
          }
        }
        while(line.charAt(0) != '@');
      }
    }
  }
}