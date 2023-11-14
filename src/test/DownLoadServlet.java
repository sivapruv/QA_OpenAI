package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/download")
public class DownLoadServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	
    	
        // Get the file path from the request parameter
        String filePath = request.getParameter("filePath");

        // Get the file from the server
        File file = new File(filePath);

        // Set the content type and content length of the response
        response.setContentType("application/octet-stream");
        response.setContentLength((int) file.length());
        
        System.out.println("filename========="+file.getName() );

        // Set the content disposition header to prompt the user to download the file
        String headerValue = "attachment; filename=\"" + file.getName() + "\"";
        response.setHeader("Content-Disposition", headerValue);

        // Copy the file to the output stream of the response
        try (FileInputStream inputStream = new FileInputStream(file);
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}