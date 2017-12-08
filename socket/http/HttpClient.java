package ssd8.socket.http;

import java.io.*;
import java.net.Socket;

/**
 * Class <em>HttpClient</em> is a class representing a simple HTTP client.
 *
 * @author wben
 */

public class HttpClient {

	/**
	 * default HTTP port is port 80
	 */
	private static int port = 80;

	/**
	 * Allow a maximum buffer size of 8192 bytes
	 */
	private static int buffer_size = 8192;

	/**
	 * Response is stored in a byte array.
	 */
	private byte[] buffer;

	/**
	 * My socket to the world.
	 */
	Socket socket = null;

	/**
	 * Default port is 80.
	 */
	private static final int PORT = 80;

	/**
	 * Output stream to the socket.
	 */
	BufferedOutputStream ostream = null;

	/**
	 * Input stream from the socket.
	 */
	BufferedInputStream istream = null;

	/**
	 * StringBuffer storing the header
	 */
	private StringBuffer header = null;

	/**
	 * StringBuffer storing the response.
	 */
	private StringBuffer response = null;
	
	/**
	 * String to represent the Carriage Return and Line Feed character sequence.
	 */
	static private String CRLF = "\r\n";

	/**
	 * HttpClient constructor;
	 */
	public HttpClient() {
		buffer = new byte[buffer_size];
		header = new StringBuffer();
		response = new StringBuffer();
	}

	/**
	 * <em>connect</em> connects to the input host on the default http port --
	 * port 80. This function opens the socket and creates the input and output
	 * streams used for communication.
	 */
	public void connect(String host) throws Exception {

		/**
		 * Open my socket to the specified host at the default port.
		 */
		socket = new Socket(host, PORT);

		/**
		 * Create the output stream.
		 */
		ostream = new BufferedOutputStream(socket.getOutputStream());

		/**
		 * Create the input stream.
		 */
		istream = new BufferedInputStream(socket.getInputStream());
	}

	/**
	 * <em>processGetRequest</em> process the input GET request.
	 */
	public void processGetRequest(String request) throws Exception {
		/**
		 * Send the request to the server.
		 */
//		request += CRLF + "Host:" + socket.getInetAddress() + CRLF;
//		request += "Connection:close"+ CRLF + CRLF;
		request += CRLF + CRLF;
		buffer = request.getBytes();
		ostream.write(buffer, 0, request.length());
		ostream.flush();
		/**
		 * waiting for the response.
		 */
		processResponse();
	}
	
	/**
	 * <em>processPutRequest</em> process the input PUT request.
	 */
	public void processPutRequest(String request) throws Exception {
		//=======start your job here============//
		/**
		 * Send the request to the server.
		 */
		File file = getPutFile(request);
        /**
         * Check if the file is valid
         */
		if (file != null)
        {   //The file is valid

            /**
             * Construct the header of this request
             */
            FileInputStream fis = new FileInputStream(file);
            request += CRLF + "Content-Length:" + fis.available() + CRLF + CRLF;

            ostream.write(request.getBytes(),0,request.length());

            /**
             * Set the request content
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
            baos.writeTo(ostream);
            fis.close();
            baos.close();
			ostream.flush();
            /**
             * waiting for the response.
             */
            processResponse();
        }
		else
        {   //The file is invalid
            System.err.println("The file you PUT is invalid");
        }
		//=======end of your job============//
	}

	/**
	 * Get the file which is PUT to server from request
	 * @param request
	 * @return the wanted file
	 */
	private File getPutFile(String request)
	{
		String[] info = request.split(" ");
        String[] res = info[1].split("/");
        File file = new File(res[res.length-1]);
		return file.isFile() ? file : null;
	}

	/**
	 * <em>processResponse</em> process the server response.
	 * 
	 */
	public void processResponse() throws Exception {
		int last = 0, c = 0;
		/**
		 * Process the header and add it to the header StringBuffer.
		 */
		boolean inHeader = true; // loop control
		while (inHeader && ((c = istream.read()) != -1)) {
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

		/**
		 * Read the contents and add it to the response StringBuffer.
		 */
		int length;
		while ((length = istream.read(buffer)) != -1) {
			response.append(new String(buffer,0,length,"iso-8859-1"));
		}
	}

	/**
	 * Get the response header.
	 */
	public String getHeader() {
		return header.toString();
	}

	/**
	 * Get the server's response.
	 */
	public String getResponse() {
		return response.toString();
	}

	/**
	 * Close all open connections -- sockets and streams.
	 */
	public void close() throws Exception {
		socket.close();
		istream.close();
		ostream.close();
	}
}