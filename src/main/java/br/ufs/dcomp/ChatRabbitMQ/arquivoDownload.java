package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.*;
import java.text.*;
import java.io.FileOutputStream;
import com.google.protobuf.ByteString;


public class arquivoDownload extends Thread{
  private final String destino;
  private final String emissor;
  private final MensagemProto.Conteudo conteudoMensagem;
  private final String data;
  private final String hora;
  private final String grupo;
  
  public arquivoDownload(String destino, String emissor, MensagemProto.Conteudo conteudoMensagem, String data, String hora, String grupo){
    this.destino = destino;
    this.emissor = emissor;
    this.conteudoMensagem = conteudoMensagem;
    this.data = data;
    this.hora = hora;
    this.grupo = grupo;
      start();
  }
  
  public void run(){
    try{
      ByteString corpo = (this.conteudoMensagem).getCorpo();
      byte[] buffer = corpo.toByteArray();
      String nome = (this.conteudoMensagem).getNome();
        
      //ESCREVENDO NO ARQUIVO
      FileOutputStream outputStream = new FileOutputStream(this.destino + "_downloads" + "/" + nome); 
      outputStream.write(buffer);
      outputStream.close();
        
      String emi = "";
      String dest = "";
      if((this.grupo).equals("") == false) {//se o envio for para um grupo
        emi = this.emissor + "#" + this.grupo;
        dest = "#" + this.grupo;
      } else {
        emi = "@" + this.emissor;
        dest = "@" + this.emissor;
      }
      System.out.println("\n(" + this.data + " às " + this.hora + ") Arquivo " + "\"" + nome + "\"" + " recebido de " + emi + "  !");
      System.out.print(dest + ">> ");
        
    } catch(Exception ex){
      System.out.println (ex.toString());
    }
 }
 
}