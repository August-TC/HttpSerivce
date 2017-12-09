package ssd8.socket.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

/**
 * Project : HttpService
 * Package : ssd8.socket.server
 * Created by august
 */
public abstract class Req
{
    //The request line of the request
    private String requestLine;

    //A header line of the request
    private String headerLine;

    //A bodyLine line of the request
    private String bodyLine;

    //The http version contained in the request
    private String http;

    //The response responded to the client
    private Resp response;

    /**
     * Construct a Req Object with a request line
     * @param requestLine
     */
    public Req(String requestLine)
    {
        this.requestLine = requestLine;
        response = new Resp();
    }

    /**
     * Get the request line
     * @return
     */
    public String getRequestLine()
    {
        return requestLine;
    }

    /**
     * Get a header line
     * @return a header line
     */
    public String getHeaderLine()
    {
        return headerLine;
    }

    /**
     * Set the header line
     * @param headerLine the header line read from BufferReader
     */
    public void setHeaderLine(String headerLine)
    {
        this.headerLine = headerLine;
    }

    /**
     * Get a body line
     * @return a body line
     */
    public String getBodyLine()
    {
        return bodyLine;
    }

    /**
     * Set the body line
     * @param bodyLine the body line read from BufferReader
     */
    public void setBodyLine(String bodyLine)
    {
        this.bodyLine = bodyLine;
    }

    /**
     * Get the http version
     * @return a body line
     */
    public String getHttp()
    {
        return http;
    }

    /**
     * Set the http version
     * @param http the http version contained in request line
     */
    public void setHttp(String http)
    {
        this.http = http;
    }

    /**
     * Get the response matched with this request
     * @return
     */
    public Resp getResponse()
    {
        return response;
    }

    // Analyse the request message
    public abstract void doRequest() throws IOException, URISyntaxException;

    // Respond to the client
    public abstract void doResponse(OutputStream os) throws IOException;

    /**
     * Respond the error to the client
     * @param os the output stream used for respond
     * @param status_code the code of the error message
     * @throws IOException
     */
    public void doErrorResponse(OutputStream os,int status_code) throws IOException
    {
        Resp response = getResponse();

        //Construct the page that contains the error
        String errorPage =
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Error</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h1>"+response.getCodeContent(status_code)+"</h1>\n" +
                        "</body>\n" +
                        "</html>";

        /**
         * Construct the header of this response
         */
        response.setStatusLine(getHttp() + " " + response.getCodeContent(status_code));
        response.addHeaderLine("Server:HttpServer");
        response.addHeaderLine("Content-Type:text/html");
        response.addHeaderLine("Content-Length:" + errorPage.length());

        /**
         * Set the response content
         */
        response.setContentLine(errorPage);

        os.write(response.toString().getBytes(),0,response.toString().length());
    }
}
