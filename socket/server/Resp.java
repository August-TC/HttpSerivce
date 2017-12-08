package ssd8.socket.server;

/**
 * Project : HttpService
 * Package : ssd8.socket.server
 * Created by august
 */
public class Resp
{
    //String to represent the Carriage Return and Line Feed character sequence.
    private static final String CRLF = "\r\n";

    //The status line of the response
    private String statusLine;

    //The header line of the response
    private String headerLine;

    //The content Line line of the response
    private String contentLine;

    /**
     * Initiate the response
     */
    public Resp()
    {
        statusLine = "";
        headerLine = "";
        contentLine = CRLF;
    }

    /**
     * Get the status line of the response
     * @return the status line
     */
    public String getStatusLine()
    {
        return statusLine;
    }

    /**
     * Set the status line of the response
     * @param statusLine the status line set for response
     */
    public void setStatusLine(String statusLine)
    {
        this.statusLine = statusLine + CRLF;
    }

    /**
     * Get the header line of the response
     * @return the header line
     */
    public String getHeaderLine()
    {
        return headerLine;
    }

    /**
     * Set the header line of the response
     * @param headerLine the header line set for response
     */
    public void setHeaderLine(String headerLine)
    {
        this.headerLine = headerLine;
    }

    /**
     * Get the content line of the response
     * @return the content line
     */
    public String getContentLine()
    {
        return contentLine;
    }

    /**
     * Set the content line of the response
     * @param contentLine the content line set for response
     */
    public void setContentLine(String contentLine)
    {
        this.contentLine = contentLine + CRLF;
    }

    /**
     * Get the Reponse Header
     * @return the Reponse Header
     */
    public String getResponseHeader()
    {
        return getStatusLine() + getHeaderLine() + CRLF;
    }

    /**
     * Get the entire response
     * @return the entire response
     */
    @Override
    public String toString()
    {
        return getStatusLine() + getHeaderLine() + CRLF + getContentLine();
    }

    /**
     * To add a new header line of the response
     * @param newLine the new line to be added
     */
    public void addHeaderLine(String newLine)
    {
        setHeaderLine(getHeaderLine() + newLine + CRLF);
    }

    /**
     * Get the content of the status code
     * @param status_code the status code of the response
     * @return
     */
    public String getCodeContent(int status_code)
    {
        String content;
        switch (status_code)
        {
            case 200:
                content = "200 OK";
                break;
            case 201:
                content = "201 Created";
                break;
            case 404:
                content = "404 Not Found";
                break;
            case 403:
                content = "403 Forbidden";
                break;
            case 400:
                content = "400 Bad Request";
                break;
            case 505:
                content = "505 HTTP Version Not Supported";
                break;
                default:
                    content = "500 Internal Server Error";
        }
        return content;
    }
}
