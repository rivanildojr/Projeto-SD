package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.util.*;
import java.text.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ArquivoUpload extends Thread{
  private final String caminho;
  private final String destino;
  private final String usuario;
  private final String grupo;
  private final Channel channel;
  private final String data;
  private final String hora;
  
  public ArquivoUpload(String caminho, String destino, String usuario, Channel channel, String data, String hora, String grupo){
      this.caminho = caminho;
      this.destino = destino;
      this.usuario = usuario;
      this.grupo = grupo;
      this.channel = channel;
      this.data = data;
      this.hora = hora;
        start();
    }
  
  public void run(){
      try{
      //Obtendo arquivo
      Path arquivoPath = Paths.get(this.caminho);
      byte[] arquivo = Files.readAllBytes(arquivoPath);
      String tipoMime = Files.probeContentType(arquivoPath);
      
      
      //Criando Mensagem
     MensagemProto.Mensagem.Builder pacoteMensagem = MensagemProto.Mensagem.newBuilder();
     pacoteMensagem.setEmissor(this.usuario);
     pacoteMensagem.setData(this.data);
     pacoteMensagem.setHora(this.hora);
     pacoteMensagem.setGrupo(this.grupo);
     
     //Criando Conteudo
     MensagemProto.Conteudo.Builder conteudoMensagem = MensagemProto.Conteudo.newBuilder();
     ByteString corpo = ByteString.copyFrom(arquivo);
     
     conteudoMensagem.setCorpo(corpo);
     conteudoMensagem.setTipo(tipoMime);
     conteudoMensagem.setNome((arquivoPath.getFileName()).toString());
     
     pacoteMensagem.setConteudo(conteudoMensagem);
     
     //Obtendo Mensagem
     MensagemProto.Mensagem msg = pacoteMensagem.build();
     byte[] buffer = msg.toByteArray();
     String dest = ""; //serve para armazenar o destinatario
     
     if((this.grupo).equals("") == true){//se for para um outro usuario
       (this.channel).basicPublish("", this.destino + "F", null, buffer);
       dest = "@" + this.destino;
     } else {
        (this.channel).basicPublish(this.grupo+"F", "", null, buffer);
        dest = "#" + this.destino;
     }
      
     System.out.println("\nArquivo \"" + this.caminho + "\" foi enviado para " + dest + " !");
     System.out.print(dest + ">>");
      
     } catch(Exception ex){
        System.out.println (ex.toString());
     }
  }
}