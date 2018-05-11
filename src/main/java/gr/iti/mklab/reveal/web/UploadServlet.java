package gr.iti.mklab.reveal.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.mongodb.MongoClientURI;
import gr.iti.mklab.reveal.forensics.api.ReportManagement;
import gr.iti.mklab.reveal.util.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@WebServlet(name = "FileUploadServlet", urlPatterns = {"/mmapi/media/verificationreport/uploadImage"})
@MultipartConfig(fileSizeThreshold = 6291456, // 6 MB
        maxFileSize = 10485760L, // 10 MB
        maxRequestSize = 20971520L // 20 MB
)
public class UploadServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        System.out.println("uploadingImage...");

//        System.out.println(ServletFileUpload.isMultipartContent(request));

        try {
            Part filePart = request.getPart("file");
            System.out.println(filePart.getInputStream());

            MongoClientURI mongoURI = new MongoClientURI(Configuration.MONGO_URI);

            System.out.println(mongoURI);

            String hash= ReportManagement.downloadURL(filePart.getInputStream(), Configuration.MANIPULATION_REPORT_PATH, mongoURI);

            PrintWriter out  = response.getWriter();
            out.println("<html>");
            out.println("<head>");
            out.println("<title> A very simple servlet example</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>welcomeMessage"+ hash +"</h1>");
            out.println("</body>");
            out.println("</html>");
            out.close();
        } catch (Exception ex) {
            System.out.println("Exception occured while uploading image."+ ex.getMessage() + " " + ex.getStackTrace().toString());
        }
    }
}
