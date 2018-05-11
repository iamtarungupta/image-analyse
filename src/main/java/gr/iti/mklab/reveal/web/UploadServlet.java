package gr.iti.mklab.reveal.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.gson.Gson;
import com.mongodb.MongoClientURI;
import gr.iti.mklab.reveal.forensics.api.ForensicReport;
import gr.iti.mklab.reveal.forensics.api.ReportManagement;
import gr.iti.mklab.reveal.util.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

            Map<String, String> map = ReportManagement.downloadURL(filePart.getInputStream(), Configuration
                            .MANIPULATION_REPORT_PATH, mongoURI);

            String generatedReportStatus = generateReport(map.get("hash"));
            String report = new Gson().toJson(returnReport(map.get("hash")));

//            PrintWriter out  = response.getWriter();
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title> A very simple servlet example</title>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("Hash : "+ map.get("hash") +"</br>");
//            out.println("File Name : "+ filePart.getSubmittedFileName() +"</br>");
//            out.println("Is Duplicate : "+ map.get("exist") +"</br>");
//            out.println("ReportGenerationStatus : "+ generatedReport +"</br>");
//            out.println("Report : "+ report +"</br>");
//            out.println("</body>");
//            out.println("</html>");
//            out.close();

            request.setAttribute("fileName", filePart.getSubmittedFileName());
            request.setAttribute("hash", map.get("hash"));
            request.setAttribute("isDuplicate", map.get("exist"));
            request.setAttribute("generatedReportStatus", generatedReportStatus);
            request.setAttribute("report", report);

            getServletContext().getRequestDispatcher("/WEB-INF/pages/success.jsp").forward(request, response);

        } catch (Exception ex) {
            System.out.println("Exception occured while uploading image."+ ex.getMessage());
        }
    }

    public String generateReport(String hash) throws RevealException {
        try {
            System.out.println("Received new hash for analysis. Beginning...");
            MongoClientURI mongoURI = new MongoClientURI(Configuration.MONGO_URI);
            String ReportResult=ReportManagement.createReport(hash, mongoURI, Configuration.MANIPULATION_REPORT_PATH,Configuration.MAX_GHOST_IMAGE_SMALL_DIM,Configuration.NUM_GHOST_THREADS,Configuration.NUM_TOTAL_THREADS,Configuration.FORENSIC_PROCESS_TIMEOUT);
            System.out.println("Analysis complete with message: " + ReportResult);
            return ReportResult;
        } catch (Exception ex) {
            System.out.println("Exception occured while generating report."+ ex.getMessage());
            return null;
        }
    }


    public ForensicReport returnReport(String hash) throws RevealException {
        try {
            System.out.println("Request for forensic report received, hash=" + hash + ".");
            MongoClientURI mongoURI = new MongoClientURI(Configuration.MONGO_URI);
            ForensicReport Report=ReportManagement.getReport(hash, mongoURI);
            if (Report!=null) {
                if (Report.elaReport.completed)
                    Report.elaReport.map=Report.elaReport.map.replace(Configuration.MANIPULATION_REPORT_PATH, Configuration.HTTP_HOST + "images/");
                if (Report.dqReport.completed)
                    Report.dqReport.map=Report.dqReport.map.replace(Configuration.MANIPULATION_REPORT_PATH,Configuration.HTTP_HOST + "images/");
                if (Report.displayImage!=null)
                    Report.displayImage=Report.displayImage.replace(Configuration.MANIPULATION_REPORT_PATH,Configuration.HTTP_HOST + "images/");
                if (Report.dwNoiseReport.completed)
                    Report.dwNoiseReport.map=Report.dwNoiseReport.map.replace(Configuration.MANIPULATION_REPORT_PATH,Configuration.HTTP_HOST + "images/");
                if (Report.gridsReport.completed){
                    Report.gridsReport.map=Report.gridsReport.map.replace(Configuration.MANIPULATION_REPORT_PATH,Configuration.HTTP_HOST + "images/");
                }
                if (Report.gridsInversedReport.completed){
                    Report.gridsInversedReport.map=Report.gridsInversedReport.map.replace(Configuration.MANIPULATION_REPORT_PATH,Configuration.HTTP_HOST + "images/");
                }
                if (Report.ghostReport.completed) {
                    for (int GhostInd = 0; GhostInd < Report.ghostReport.maps.size(); GhostInd++) {
                        Report.ghostReport.maps.set(GhostInd, Report.ghostReport.maps.get(GhostInd).replace(Configuration.MANIPULATION_REPORT_PATH, Configuration.HTTP_HOST + "images/"));
                    }
                }
                if (Report.thumbnailReport.numberOfThumbnails>0) {
                    for (int ThumbInd = 0; ThumbInd < Report.thumbnailReport.thumbnailList.size(); ThumbInd++) {
                        Report.thumbnailReport.thumbnailList.set(ThumbInd, Report.thumbnailReport.thumbnailList.get(ThumbInd).replace(Configuration.MANIPULATION_REPORT_PATH, Configuration.HTTP_HOST + "images/"));
                    }
                }
                if (Report.blockingReport.completed)
                    Report.blockingReport.map=Report.blockingReport.map.replace(Configuration.MANIPULATION_REPORT_PATH,Configuration.HTTP_HOST + "images/");
                if (Report.medianNoiseReport.completed)
                    Report.medianNoiseReport.map=Report.medianNoiseReport.map.replace(Configuration.MANIPULATION_REPORT_PATH,Configuration.HTTP_HOST + "images/");

            }
            return Report;

        } catch (Exception ex) {
            System.out.println("Exception occured while getting report."+ ex.getMessage());
            return null;
        }
    }
}
