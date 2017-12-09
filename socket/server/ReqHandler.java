package ssd8.socket.server;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;

/**
 * Project : HttpService
 * Package : ssd8.socket.server
 * Created by august
 */
public class ReqHandler implements Runnable
{
    //The socket for transform data
    private Socket socket;

    /**
     * The method for input and output
     */
    private OutputStream os;
    private InputStream is;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;

    //The request the client send
    private Req req;

    /**
     * Initiate the ReqHandler
     * @param socket the socket used in the communication with client
     */
    public ReqHandler(Socket socket)
    {
        this.socket = socket;
    }

    /**
     * Initiate the method for input and output
     * @throws IOException
     */
    public void initStream() throws IOException
    {
        os = socket.getOutputStream();
        is = socket.getInputStream();
        bis = new BufferedInputStream(is);
        bos = new BufferedOutputStream(os);
    }

    /**
     * Run a thread of server
     */
    @Override
    public void run()
    {
        try
        {
            System.out.println("=== New Request ===");

            //Initiate the method for input and output
            initStream();

            //Get the request line contained in the request
            String requestLine = processRequest().split("\n")[0];

            //Check the request line
            CheckRequest(requestLine);

            //Send response to the client
            req.doResponse(os);
        }
        catch (URISyntaxException ue)
        {
            try
            {
                //Send 400 error message if the request message is not correct as it is specify in the document
                req.doErrorResponse(os,400);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException fnfe){
            try
            {
                //Send 404 error message if the file is not exist
                req.doErrorResponse(os,404);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        catch (IllegalArgumentException iae)
        {
            try
            {
                //Send 400 error message if the request message is not correct as it is specify in the document
                req.doErrorResponse(os,400);
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        catch (IOException e)
        {
            try
            {
                //Send 500 error message if something goes wrong in server
                req.doErrorResponse(os,500);
            } catch (IOException es)
            {
                es.printStackTrace();
            }
        }finally
        {
            try
            {
                os.flush();
                os.close();
                bos.close();
                if (socket != null)
                {
                    socket.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("=== Close Request ===");
    }

    /**
     * Check the type of request
     * @param requestLine the request line contained in request
     */
    private void CheckRequest(String requestLine) throws IOException, URISyntaxException
    {
        if (requestLine.startsWith("GET") && (requestLine.endsWith("HTTP/1.0") || requestLine.endsWith("HTTP/1.1")) )
        {
            //Deal with the GET request
            req = new ReqGet(requestLine);
            req.doRequest();
        }
        else if (requestLine.startsWith("PUT") && (requestLine.endsWith("HTTP/1.0") || requestLine.endsWith("HTTP/1.1")))
        {
            //Deal with the PUT request
            req = new ReqPut(requestLine,bis);
            req.doRequest();
        }
        else
        {
            //Deal with the other request
            req = new ReqOther(requestLine);
            req.doRequest();
        }
    }

    /**
     * To get the header of the request
     * @return the header of the request
     * @throws IOException
     */
    public String processRequest() throws IOException {
        int last = 0, c = 0;
        StringBuffer header = new StringBuffer();
        boolean inHeader = true;
        while (inHeader && ((c = bis.read()) != -1)) {
            switch (c) {
                case '\r':
                    break;
                case '\n':
                    if (c == last) {
                        inHeader = false;
                        break;
                    }
                    last = c;
                    header.append("\n");
                    break;
                default:
                    last = c;
                    header.append((char) c);
            }
        }
        return header.toString();
    }
}
