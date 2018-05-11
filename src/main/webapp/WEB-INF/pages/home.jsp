<!DOCTYPE html>
<html lang="en">
<head>
    <title>File Upload</title>
    <meta http-equiv="Content-Type" content="multipart/form-data">
</head>

<style>
    form {
        margin-top: 10%;
    }
</style>

<body>
    <form align="center" method="POST" action="/image-forensics/mmapi/media/verificationreport/uploadImage"
           enctype="multipart/form-data" >
        File:
        <input type="file" name="file" id="file" />  <br/>
        </br>
        <input type="submit" value="Upload" name="upload" id="upload" />
    </form>
</body>
</html>

<script>
    uploadImage = function(data) {
        console.log(data);
//        document.getElementById("upload").addEventListener("click", function () {
            console.log("qw");

//            var request = {
//                url: 'http://www.greycloaktech.com/wp-content/uploads/2015/07/url-small.jpg'
//            };
//
//            console.log(JSON.stringify(request));
//
//            var xhttp = new XMLHttpRequest();
//            xhttp.onreadystatechange = function () {
//                console.log(this.readyState);
//                console.log("always");
//            };
//            xhttp.open("GET",
//                "/image-forensics/mmapi/media/verificationreport/addurl?url=http://www.greycloaktech.com/wp-content/uploads/2015/07/url-small.jpg",
//                true);
//            xhttp.setRequestHeader("Content-type", "text/html; charset=utf-8");
//            xhttp.send();
//        }, false);
    }
</script>