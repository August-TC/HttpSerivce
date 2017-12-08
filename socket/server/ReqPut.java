package ssd8.socket.server;

import java.io.*;

/**
 * Project : HttpService
 * Package : ssd8.socket.server
 * Created by august
 */
public class ReqPut extends Req
{
    //A byte[] used as Buffer to receive the file put from client
    private byte[] bufferPut;

    //The BufferedReader used in reading content in the request
    private BufferedInputStream bis;

    /**
     * Construct a ReqPut Object with a request line
     * @param requestLine
     * @param bis
     */
    public ReqPut(String requestLine, BufferedInputStream bis)
    {
        super(requestLine);
        this.bis = bis;
    }

    /**
     * Analyse the request message
     */
    @Override
    public void doRequest() throws IOException
    {
        /**
         * Analyse the request line and get the method and version used in request
         */
        String[] segments = getRequestLine().split(" ");
        if (segments.length != 3)
        {
            throw new IllegalArgumentException("Wrong Request message!");
        }
        String filename = segments[1];
        setHttp(segments[2]);

        /**
         * receive the content of the PUT request
         */
        bufferPut = getContent();

        //Check the path of the file
        String path = filename.substring(0,filename.lastIndexOf("/"));

        /**
         * Check if the path specify in the request is exist
         */
        File newFile = new File(HttpServer.getRoot()+path);
        if(!newFile.exists())
        {
            //if the path is not exist, create the path
            newFile.mkdirs();
        }

        /**
         * Output the file contained in request
         */
        FileOutputStream fos = new FileOutputStream(HttpServer.getRoot() + filename);
        fos.write(bufferPut,0,bufferPut.length);
        fos.flush();
        fos.close();
    }

    /**
     * Get the content contained in the response
     * @return the byte array of the content
     * @throws IOException
     */
    private byte[] getContent() throws IOException
    {
        /**
         * Get the content in the content line of the response
         */
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream contentArray = new ByteArrayOutputStream();
        int len;
        while ((len = bis.read(buffer)) != -1) {
            contentArray.write(buffer, 0, len);
            if (len < 8192) {
                break;
            }
        }
        return contentArray.toByteArray();
    }

    /**
     * Response to the client
     * @param os
     */
    @Override
    public void doResponse(OutputStream os) throws IOException
    {
        Resp response = getResponse();

        //Construct the page that contains the result
        String resultPage =
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Create</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h1>"+response.getCodeContent(201)+"</h1>\n" +
                        "</body>\n" +
                        "</html>";

        /**
         * Construct the header of this response
         */
        response.setStatusLine(getHttp() + " " + response.getCodeContent(201));
        response.addHeaderLine("Server:HttpServer");
        response.addHeaderLine("Content-Type:text/html");
        response.addHeaderLine("Content-Length:" + bufferPut.length);

        /**
         * Set the response content
         */
        response.setContentLine(resultPage);

        os.write(response.toString().getBytes(),0,response.toString().length());

    }
}
