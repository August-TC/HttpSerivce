package ssd8.socket.server;


import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Project : HttpService
 * Package : ssd8.socket.server
 * Created by august
 */
public class ReqGet extends Req
{
    //The uri contained in request
    private URI uri;

    //The name of the file required
    private String filename;

    //The FileInputStream used for File transition
    private FileInputStream fis;

    /**
     * Construct a ReqGet Object with a request line
     * @param requestLine
     */
    public ReqGet(String requestLine)
    {
        super(requestLine);
        filename = "";
    }

    /**
     * Analyse the request message
     */
    @Override
    public void doRequest() throws FileNotFoundException, URISyntaxException
    {
        /**
         * Analyse the request line and get the method and version used in request
         */
        String[] segments = getRequestLine().split(" ");
        if (segments.length != 3)
        {
            throw new IllegalArgumentException("Wrong Request message!");
        }
        uri = new URI(segments[1]);
        filename = uri.getPath();
        setHttp(segments[2]);

        //Check if the root path is viewed
        if (filename.equals("/"))
        {
            filename += HttpServer.getWelcomePage();
        }
        fis = new FileInputStream(HttpServer.getRoot() + filename);
    }

    /**
     * Get the content type for the response
     * @param filename the name of the file
     * @return the content type for the response
     */
    private String getContentType(String filename)
    {
        /**
         * check the type of file
         */
        int type_pos = filename.lastIndexOf('.');
        String type = filename.substring(type_pos + 1).toLowerCase();

        // to set the content type for the response according to the typr of the file
        String contentType = "";
        switch (type.charAt(0))
        {
            case 'h':                       //response of html or htm
                contentType = "text/html";
                break;
            case 'j':                       //response of jpg
                contentType = "image/jpg";
                break;
            case 'g':                       //response of gif
                contentType = "image/gif";
                break;
            default:                        //response of other types
                contentType = "*/" + type;
        }

        return contentType;
    }

    /**
     * Response to the client
     * @param os the output stream used for respond
     */
    @Override
    public void doResponse(OutputStream os) throws IOException
    {
        Resp response = getResponse();

        /**
         * Construct the header of this response
         */
        response.setStatusLine(getHttp() + " " + response.getCodeContent(200));
        String contentType = getContentType(filename);
        response.addHeaderLine("Server:HttpServer");
        response.addHeaderLine("Content-Type:" + contentType);
        response.addHeaderLine("Content-Length:" + fis.available());

        os.write(response.getResponseHeader().getBytes(),0,response.getResponseHeader().length());

        /**
         * Set the response content
         */
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //The Byte array to read file
        byte[] readBuf = new byte[8192];

        //The length of the buffer array for each turn
        int len = 0;
        while ((len = fis.read(readBuf)) != -1)
        {
            baos.write(readBuf,0,len);
        }
        baos.flush();
        baos.writeTo(os);
        fis.close();
        baos.close();
    }
}
