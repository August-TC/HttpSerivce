package ssd8.socket.server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Project : HttpService
 * Package : ssd8.socket.server
 * Created by august
 */
public class HttpServer
{
    /**
     * The root work path of the server
     */
    private static String root;

    /**
     * Default port is 80.
     */
    private static final int PORT = 80;

    /**
     * The ServerSocket use to accept request
     */
    private ServerSocket serverSocket;

    /**
     * The thread pool and its size of the server
     */
    ExecutorService executorService;
    private int POOL_SIZE = 4;

    // The welcome page of the server
    private static String welcomePage;

    /**
     * Initiate the server
     * @throws IOException
     */
    public HttpServer() throws IOException
    {
        serverSocket = new ServerSocket(PORT);

        //Create a thread pool
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*POOL_SIZE);
        System.out.println("=== Server start ===");
    }

    /**
     * Get the root path
     * @return root path
     */
    public static String getRoot()
    {
        return root;
    }

    /**
     * Set the root path
     * @param root the root path provided as an argument
     */
    public void setRoot(String root)
    {
        HttpServer.root = root;
    }

    public static void main(String[] args)
    {
        try
        {
            HttpServer server = new HttpServer();
            //Check if the path valid
            if (checkRootPath(server,args))
            {
                server.service();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Provide Http WEB service
     */
    private void service()
    {
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();

                // Use thread pool to support multithreading
                executorService.execute(new ReqHandler(socket));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if the root path is valid and return the result. if valid, set the root path.
     * @param server the aimed server
     * @param args the input path
     * @return the result
     */
    private static boolean checkRootPath(HttpServer server, String[] args)
    {
        //Check the input argument
        if(args.length >= 1 && args.length <= 2)
        {
            String root_input = args[0];
            if (root_input.endsWith("/"))
            {
                root_input = root_input.substring(0,root_input.length()-1);
            }
            File file = new File(root_input);

            // Check if the path is valid
            if (file.exists())
            {
                server.setRoot(root_input);

                //Check if specify the welcome page of the service
                if (args.length == 2)
                {   //The specify welcome page is provided as the second argument

                    server.setWelcomePage(args[1]);
                }
                else
                {   //The specify welcome page is not provided so set the welcome page as default page <em>index.html</em>

                    System.out.println("Warning: The Welcome Page is set to the default page \"index.html\"");
                    server.setWelcomePage("index.html");
                }
                return true;
            }
            else
            {
                System.err.println("The directory is NOT exist!");
                return false;
            }
        }
        else if (args.length == 0)
        {   //lack of arguments
            System.err.println("Usage: java HTTPServer <dir> [optional: welcome page]");
            return false;
        }
        else
        {
            //too match arguments
            System.err.println("== Only ONE path can be set as root path. ==");
            System.err.println("Usage: java FileServer <dir> [optional: welcome page]");
            return false;
        }
    }

    /**
     * Get the welcome page of the server
     * @return the welcome page
     */
    public static String getWelcomePage()
    {
        return welcomePage;
    }

    /**
     * Set the welcome page of the server
     * @param welcomePage
     */
    public void setWelcomePage(String welcomePage)
    {
        this.welcomePage = welcomePage;
    }
}
