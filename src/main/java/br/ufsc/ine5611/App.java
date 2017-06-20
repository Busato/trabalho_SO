package br.ufsc.ine5611;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.Base64;


public class App {
    public static void main(String[] args) throws Exception {
        
        if(args.length==0){   
            System.out.print("Especifique um Path");
            return;
        }
             
        
        //Escrever arquivo passado pela cmd line para file
        
        File file = new File("file");      
        
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(args[1].getBytes());
        
        FileInputStream fileInputStream = new FileInputStream(file);
 
        System.out.println(file.length());
        
        
        //Criação file temporário e do mappedByteBuffer nele
        
        File tempFile = File.createTempFile("tempFile", ".txt");
        
        FileChannel ch = FileChannel.open(tempFile.toPath(), READ, WRITE);
        MappedByteBuffer mb = ch.map(READ_WRITE, 0, file.length()+4+32);
        
        
        System.out.println(file.length());
        
        
        // Popular MappedByteBuffer
        
        for(int i = 0; i<4; i++){
            mb.put((byte)file.length());       
    }     
        for(int i = 0; i<file.length(); i++){
            mb.put((byte)fileInputStream.read());
    }
        for(int i = 0; i<32; i++){
            mb.put((byte)0);       
    }
        
        System.out.println(file.length());
        
        
        // Criação do processo signer via pipes
        
        ProcessBuilder builder = new ProcessBuilder()
        .command(args[0]);
        Process process = builder.start();
        int exitCode = process.waitFor();
               
        
        System.out.println(file.length());
       
        SignerClient c = new SignerClient(process.getOutputStream(), process.getInputStream());
        c.sign(tempFile);
        
        
        System.out.println(file.length());
        
        // Pegar a assinatura
        
        byte[] assinatura = new byte[32];
        int x = 0;
        for(int i = ((int)file.length()+4); i<=((int)file.length()+4)+32; i++){
            assinatura[x]=mb.get(i);
            x++;
            
    }
        
              
        System.out.println(Base64.getEncoder().encodeToString(assinatura));
        
        
        System.out.print(c.getExpectedSignature(tempFile).equals(file));
        
    }
}
