package actividad6;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

public class MulticastClient extends Observable implements Runnable {

    @Override
    public void run() {
        try {
            MulticastSocket socket = new MulticastSocket(50089);
            InetAddress address = InetAddress.getByName("224.0.0.1");
            socket.joinGroup(address);

            DatagramPacket packet;

            // get a few quotes
            for (int i = 0; i < 5; i++) {
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Quote received: " + received);
                //Mostrar recibido junto la fecha/hora de recepción
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                System.out.println(" Fecha/hora de recepción: " + dateFormat.format(date));
                
                setChanged();
                this.notifyObservers(received);
                clearChanged();
            }

            socket.leaveGroup(address);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

