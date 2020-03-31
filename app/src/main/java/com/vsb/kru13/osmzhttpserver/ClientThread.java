package com.vsb.kru13.osmzhttpserver;

import android.graphics.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ClientThread extends Thread {

    Socket s;
    Handler myHandler;
    Semaphore _semaphore;

    public ClientThread(Socket socket, Handler handler, Semaphore semaphore)
    {
        s = socket;
        myHandler = handler;
        _semaphore = semaphore;
    }

    @Override
    public void run()
    {

        OutputStream o = null;

        try
        {
            o = s.getOutputStream();

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(o));

            String filename = "";

            filename = "/index.html";

            String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            Log.d("CESTA_K_SOUBORU", path);

            sendMessageToMainActivity("AHOJ, toto je string z nejakeho Threadu.");

            try
            {
                File f = new File(path + filename);

                FileInputStream fileIS = new FileInputStream(f);

                out.write("HTTP/1.0 200 OK\n");

                //if ((filename.endsWith(".htm") || filename.endsWith(".html")))
                    out.write("Content-Type: text/html\n");

                out.write("Content-Length: " + String.valueOf(f.length()) + "\n");
                out.write("\n");

                out.flush();

                byte[] buffer = new byte[1024];

                fileIS.read(buffer, 0 , buffer.length);

                o.write(buffer,0, buffer.length);

                if(CameraData.cemeraPictureData != null  && CameraData.cemeraPictureData.length > 0){
                    o.write(CameraData.cemeraPictureData, 0, CameraData.cemeraPictureData.length);
                }


                fileIS.close();
                s.close();

                Log.d("SERVER", "Socket Closed");
            }
            catch (FileNotFoundException e)
            {
                //Log.d("SERVER",e.getMessage());
                //Log.d("SERVER", "HTTP/1.0 404 Not Found");
                out.write("HTTP/1.0 404 Not Found\n\n");
                out.write("Page not found");
                out.flush();

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            _semaphore.release(1);
        }
    }

    private void sendMessageToMainActivity(String message)
    {
        Message msg = myHandler.obtainMessage();

        Bundle bundle = new Bundle();

        bundle.putString("string_key", message);

        msg.setData(bundle);

        myHandler.sendMessage(msg);
    }
}
