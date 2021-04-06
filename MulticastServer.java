package actividad6;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastServer extends Observable implements Runnable {

    private long FIVE_SECONDS = 5000;
    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreQuotes = true;

    public MulticastServer() {
        try {
            socket = new DatagramSocket(50000);
        } catch (SocketException ex) {
            Logger.getLogger(MulticastServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            in = new BufferedReader(new FileReader("banners.txt"));
        } catch (FileNotFoundException e) {
            System.err.println("Could not open quote file. Serving time instead.");
        }
    }

    public void run() {
        while (moreQuotes) {
            try {
                byte[] buf = new byte[256];

                // construct quote
                String dString = null;
                if (in == null) {
                    dString = new Date().toString();
                } else {
                    dString = getNextQuote();
                }
                buf = dString.getBytes();                

                // send it
                InetAddress group = InetAddress.getByName("224.0.0.1");
                DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 50089);
                socket.send(packet);
                System.out.println("Quote sent: "+ dString);
                //Mostrar recibido junto la fecha/hora de recepción
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                System.out.println(" Fecha/hora de envío: " + dateFormat.format(date));
                
                setChanged();
                this.notifyObservers(dString);
                clearChanged();
                
                // sleep for a while
                try {
                    Thread.sleep((long) (Math.random() * FIVE_SECONDS));
                } catch (InterruptedException e) {
                }
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            }
        }
        socket.close();
    }

    protected String getNextQuote() {
        String returnValue = null;
        try {
            if ((returnValue = in.readLine()) == null) {
                in.close();
                moreQuotes = false;
                returnValue = "No more quotes. Goodbye.";
            }
        } catch (IOException e) {
            returnValue = "IOException occurred in server.";
        }
        return returnValue;
    }
}

