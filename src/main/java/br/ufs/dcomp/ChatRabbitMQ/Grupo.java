package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.util.*;
import java.io.IOException;

public class Grupo{

// -------------------------------- GERENCIANDO GRUPO --------------------------------------- 
    public Channel channel;
    public Channel channelArquivo;
    Mensagem msg = new Mensagem();
    
    public Grupo(Channel channel, Channel channelArquivo){
        this.channel = channel;
        this.channelArquivo = channelArquivo;
    }
    
    //Criando grupos
    public void criarGrupo(String nomeGrupo, String usuario){
        try{
            channel.exchangeDeclare(nomeGrupo, "fanout");
            channel.queueBind(usuario,nomeGrupo,"");
            
            channel.exchangeDeclare(nomeGrupo + "F", "fanout");
            channel.queueBind(usuario + "F",nomeGrupo + "F","");
        }catch(IOException ex){
            System.out.println (ex.toString());
        }
    }
    
    //Criando grupos
    public void excluirGrupo(String nomeGrupo){
        try{
            channel.exchangeDelete(nomeGrupo, true);
            channel.exchangeDelete(nomeGrupo + "F", true);
        }catch(IOException ex){
            System.out.println (ex.toString());
        }
    }
    
    //Criando grupos
    public void inserirUsuarioGrupo(String usuario, String nomeGrupo){
        try{
            channel.queueBind(usuario,nomeGrupo,"");
            channel.queueBind(usuario + "F",nomeGrupo + "F","");
        }catch(IOException ex){
            System.out.println (ex.toString());
        }
    }
    
    //Criando grupos
    public void removerUsuarioGrupo(String nomeGrupo, String usuario){
        try{
            channel.queueUnbind(usuario, nomeGrupo, "");
            channel.queueUnbind(usuario + "F", nomeGrupo + "F", "");
        }catch(IOException ex){
            System.out.println (ex.toString());
        }
    }
    
    //Criando grupos
    public void enviarMensagemGrupo(String nomeGrupo, String message){
        try{
            channel.basicPublish(nomeGrupo, "", null, message.getBytes("UTF-8"));
        }catch(IOException ex){
            System.out.println (ex.toString());
        }
    }
    
    //Enviando arquivo
    public void enviarArquivo(String caminho, String destino, String usuario, String grupo)throws Exception{
        try{
            msg.upload(caminho, destino, usuario, channelArquivo, grupo);
        }catch(IOException ex){
            System.out.println (ex.toString());
        }
    }
    
    //Verificando comandos (!)
    public void verificaMensagem(String line, String usuario, String destino, String grupo) throws Exception{
        String[] mensagem = line.split(" ");
        switch(mensagem[0]){
          // Criando grupo
          case "!addGroup":
            criarGrupo(mensagem[1],usuario);
            System.out.println("Grupo criado com sucesso!");
            break;
          // Excluindo grupo
          case "!removeGroup":
            excluirGrupo(mensagem[1]);
            System.out.println("Grupo removido com sucesso!");
            break;
          // Inserindo usuário em um grupo
          case "!addUser":
            inserirUsuarioGrupo(mensagem[1],mensagem[2]);
            System.out.println("Usuário inserido com sucesso!");
            break;
          // Removendo usuário de um grupo
          case "!delFromGroup":
            removerUsuarioGrupo(mensagem[1],mensagem[2]);
            System.out.println("Usuário removido com sucesso!");
            break;
          case "!upload":
            enviarArquivo(mensagem[1], destino, usuario, grupo);
            System.out.println("arquivo enviado com sucesso!");
            break;  
        }
    }
}