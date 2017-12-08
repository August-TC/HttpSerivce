package ssd8.socket.server;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Project : HttpService
 * Package : ssd8.socket.server
 * Created by august
 */
public class ReqOther extends Req
{
    // The method of the request
    private String method;

    // The response code to the request
    private int resp_code = -1;

    /**
     * Construct a ReqOther Object with a request line
     * @param requestLine
     */
    public ReqOther(String requestLine)
    {
        super(requestLine);
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
        method = segments[0];
        setHttp(segments[2]);

        //Check the method
        resp_code = checkMethod(method);

    }

    /**
     * To check if the method of the request valid and if the http version is supported
     * @param method the method provided in request
     * @return the status code will be respond
     */
    private int checkMethod(String method)
    {
        if (method.equals("GET") || method.equals("PUT"))
        {
            if (!getHttp().equals("HTTP/1.0") && !getHttp().equals("HTTP/1.1"))
            {
                return 505;
            }
        }
        else
        {
            if (method.equals("OPTIONS") ||
                    method.equals("HEAD") ||
                    method.equals("POST") ||
                    method.equals("DELETE") ||
                    method.equals("TRACE"))
            {
                return 403;
            }
            else
            {
                throw new IllegalArgumentException("Wrong Request method!");
            }
        }
        return -1;
    }

    /**
     * Response to the client
     * @param os
     */
    @Override
    public void doResponse(OutputStream os) throws IOException
    {
        Resp response = getResponse();
        String message;
        switch (resp_code)
        {
            case 403:
                message = "the method <u>" + method + "</u> is not allow in this service";
                break;
            case 505:
                message = "The HTTP version is wrong or not supported in this service";
                break;
                default:
                    message = "";
        }
        //Construct the page that contains the result
        String resultPage =
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Forbidden</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h1>"+response.getCodeContent(resp_code)+"</h1>\n" +
                        "<h3><i>"+message+"</i></h3>\n" +
                        "</body>\n" +
                        "</html>";

        /**
         * Construct the header of this response
         */
        response.setStatusLine(getHttp() + " " + response.getCodeContent(resp_code));
        response.addHeaderLine("Server:HttpServer");
        response.addHeaderLine("Content-Type:text/html");

        /**
         * Set the response content
         */
        response.setContentLine(resultPage);

        os.write(response.toString().getBytes(),0,response.toString().length());
    }
}
