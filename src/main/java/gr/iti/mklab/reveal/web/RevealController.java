package gr.iti.mklab.reveal.web;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.mongodb.MongoClientURI;
import gr.iti.mklab.reveal.forensics.api.ForensicReport;
import gr.iti.mklab.reveal.forensics.api.ForensicReportBase64;
import gr.iti.mklab.reveal.forensics.api.ReportManagement;

import gr.iti.mklab.reveal.util.Configuration;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.annotation.PreDestroy;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@Controller
@RequestMapping("/mmapi")
public class RevealController {

    public RevealController() throws Exception {
        Configuration.load(getClass().getResourceAsStream("/remote.properties"));
        // MorphiaManager.setup(Configuration.MONGO_HOST);
    }

    @PreDestroy
    public void cleanUp() throws Exception {
        System.out.println("Spring Container destroy");
        //  MorphiaManager.tearDown();
    }

    @RequestMapping("home")
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView("home");
        return mav;
    }

    ////////////////////////////////////////////////////////
    ///////// MANIPULATION DETECTION     ///////////////////////////
    ///////////////////////////////////////////////////////


//    @RequestMapping(value = "/media/verificationreport/uploadImage")
//    public void uploadImage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        System.out.println("uploadingImage...");
//
//        try {
//            Part filePart = request.getPart("file");
//            System.out.println(filePart.getInputStream());
//
//            MongoClientURI mongoURI = new MongoClientURI(Configuration.MONGO_URI);
//
//            System.out.println(mongoURI);
//
//            String hash=ReportManagement.downloadURL(filePart.getInputStream(), Configuration.MANIPULATION_REPORT_PATH, mongoURI);
//
//            PrintWriter out  = response.getWriter();
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title> A very simple servlet example</title>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<h1>welcomeMessage"+ hash +"</h1>");
//            out.println("</body>");
//            out.println("</html>");
//            out.close();
//        } catch (Exception ex) {
//            System.out.println("Exception occured while uploading image."+ ex.getMessage() + " " + ex.getStackTrace().toString());
//        }
//    }

    @RequestMapping(value = "/media/verificationreport/addurl", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String addverification(@RequestParam(value = "url", required = true) String url) throws RevealException {
        try {
            System.out.println("Received new URL. Downloading...");
            MongoClientURI mongoURI = new MongoClientURI(Configuration.MONGO_URI);
            String URL=ReportManagement.downloadURL(url, Configuration.MANIPULATION_REPORT_PATH, mongoURI );
            return URL;
        } catch (Exception ex) {
            throw new RevealException((ex.getMessage()), ex);
        }
    }

    @RequestMapping(value = "/media/verificationreport/generatereport", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String generateReport(@RequestParam(value = "hash", required = true) String hash) throws RevealException {
        try {
            System.out.println("Received new hash for analysis. Beginning...");
            MongoClientURI mongoURI = new MongoClientURI(Configuration.MONGO_URI);
            String ReportResult=ReportManagement.createReport(hash, mongoURI, Configuration.MANIPULATION_REPORT_PATH,Configuration.MAX_GHOST_IMAGE_SMALL_DIM,Configuration.NUM_GHOST_THREADS,Configuration.NUM_TOTAL_THREADS,Configuration.FORENSIC_PROCESS_TIMEOUT);
            System.out.println("Analysis complete with message: " + ReportResult);
            return ReportResult;
        } catch (Exception ex) {
            throw new RevealException((ex.getMessage()), ex);
        }
    }

    @RequestMapping(value = "/media/verificationreport/getreport", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ForensicReport returnReport(@RequestParam(value = "hash", required = true) String hash) throws RevealException {
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
            throw new RevealException((ex.getMessage()), ex);
        }
    }


    @RequestMapping(value = "/media/verificationreport/getreportbase64", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ForensicReportBase64 returnReportBase64(@RequestParam(value = "hash", required = true) String hash) throws RevealException {
        try {
            System.out.println("Request for base64 forensic report received, hash=" + hash + ".");
            MongoClientURI mongoURI = new MongoClientURI(Configuration.MONGO_URI);
            ForensicReportBase64 Report=ReportManagement.getBase64(hash, mongoURI);

            return Report;
        } catch (Exception ex) {
            throw new RevealException((ex.getMessage()), ex);
        }
    }



    ////////////////////////////////////////////////////////
    ///////// EXCEPTION HANDLING ///////////////////////////
    ///////////////////////////////////////////////////////

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RevealException.class)
    @ResponseBody
    public RevealException handleCustomException(RevealException ex) {
        return ex;
    }


    public static void main(String[] args) throws Exception {

        /*ForensicAnalysis fa = ToolboxAPI.analyzeImage("http://nyulocal.com/wp-content/uploads/2015/02/oscars.1.jpg", "/tmp/reveal/images/");
        if (fa.DQ_Lin_Output != null)
            fa.DQ_Lin_Output = "http://localhost:8080/images/" + fa.DQ_Lin_Output.substring(fa.DQ_Lin_Output.lastIndexOf('/') + 1);
        if (fa.Noise_Mahdian_Output != null)
            fa.Noise_Mahdian_Output = "http://localhost:8080/images/" + fa.Noise_Mahdian_Output.substring(fa.Noise_Mahdian_Output.lastIndexOf('/') + 1);

        final List<String> newGhostOutput = new ArrayList<>();
        if (fa.GhostOutput != null) {
            fa.GhostOutput.stream().forEach(s -> newGhostOutput.add("http://localhost:8080/images/" + s.substring(s.lastIndexOf('/') + 1)));
        }
        fa.GhostOutput = newGhostOutput;
        int m = 5;
        //ForensicAnalysis fa = ToolboxAPI.analyzeImage("http://eices.columbia.edu/files/2012/04/SEE-U_Main_Photo-540x359.jpg");*/


     /*   //Configuration.load("remote.properties");
        MorphiaManager.setup("160.40.51.20");
        AssociationDAO associationDAO = new AssociationDAO("syria_migrants");
        List<Association> assList = associationDAO.getDatastore().find(Association.class).disableValidation().filter("className", TextualRelation.class.getName()).
                limit(300).asList();
        List<TextualRelation> trlist = new ArrayList<>(assList.size());
        assList.stream().forEach(association ->
                        trlist.add(((TextualRelation) association))
        );*/

        //MediaDAO<Image> imageDAO = new MediaDAO<>(Image.class, "eurogroup");
        //List<String> s = new ArrayList<>();
        //s.add("Twitter");
        //List<Image> imgs = imageDAO.search("crawlDate", null, 100, 100, 50, 0, null, null, s);
        //DAO<NamedEntity, String> rankedEntities = new BasicDAO<>(NamedEntity.class, MorphiaManager.getMongoClient(), MorphiaManager.getMorphia(), MorphiaManager.getDB("eurogroup").getName());
        //List<NamedEntity> list = rankedEntities.find().asList();
        //int m = 5;

        /*Pattern p = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        Query<Image> q = imageDAO.createQuery();
        q.and(
                q.criteria("lastModifiedDate").greaterThanOrEq(new Date(date)),
                q.criteria("width").greaterThanOrEq(w),
                q.criteria("height").greaterThanOrEq(h),
                q.or(
                        q.criteria("title").equal(p),
                        q.criteria("description").equal(p)
                )*/

        /*VisualIndexer.init();
        ExecutorService clusteringExecutor = Executors.newSingleThreadExecutor();
        clusteringExecutor.submit(new ClusteringCallable("camerona", 60, 1.3, 2));
        MorphiaManager.tearDown();*/
    }
}