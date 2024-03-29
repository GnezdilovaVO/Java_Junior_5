package org.example.client;

import org.example.server.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

public class Client {

    public static void main(String[] args) {
//        Scanner console = new Scanner(System.in);
        try {
//            Thread.sleep(Duration.ofSeconds(1));
            Socket serverSocket = new Socket("localhost", Server.PORT);
            System.out.println("Подключились к сереверу: tcp://localhost:" + Server.PORT);

            Scanner serverIn = new Scanner(serverSocket.getInputStream());
            String input = serverIn.nextLine();
            System.out.println("Сообщение от сервера: " + input);

            new PrintWriter(serverSocket.getOutputStream(), true).println(UUID.randomUUID());


            new Thread(new ServerReader(serverSocket)).start();
            new Thread(new ServerWriter(serverSocket)).start();
        } catch (IOException e) {
            throw new RuntimeException("Не удалось подключиться к серверу: " + e.getMessage(), e);
        }
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }
}

class ServerWriter implements Runnable{
    private final Socket serverSocket;
//    private final PrintWriter out;
//    private final Scanner consoleReader;

    public ServerWriter(Socket serverSocket) {
//        this.out = new PrintWriter(outputStream, true);
//        this.consoleReader = new Scanner(System.in);
        this.serverSocket =serverSocket;
    }

    @Override
    public void run() {
        Scanner consoleReader = new Scanner(System.in);
        try(PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true)) {
            while (true) {
                String msgFromConsole = consoleReader.nextLine();
                out.println(msgFromConsole);
                if (Objects.equals("exit", msgFromConsole)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при записи на сервер: " + e.getMessage());
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Ошибка при отключении от червера: " + e.getMessage());
        }
    }
}
class ServerReader implements Runnable {
    private final Socket serverSocket;


    public ServerReader(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try (Scanner in = new Scanner(serverSocket.getInputStream())) {
            while (in.hasNext()) {
                String input = in.nextLine();
                System.out.println("Сообщение от сервера: " + input);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении с сервера: " + e.getMessage());
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Ошибка при отключении от сервера: " + e.getMessage());
        }

    }
}
