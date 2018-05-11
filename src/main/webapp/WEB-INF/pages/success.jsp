<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<html>
<head>
    <title>Analysis</title>
</head>
<body>

<style>
    table {
        margin-top: 10%;
    }
    table, th, td {
        border: 1px solid black;
    }
    th, td {
        padding: 15px;
    }
    th {
        text-align: left;
    }
    .color-red {
        color: #ff0000;
    }
    .color-green {
        color: #2d8655;
    }
</style>

<table align="center">
    <tr>
        <th>File Name</th>
        <th>${fileName}</th>
    </tr>
    <tr>
        <td>Hash</td>
        <td>${hash}</td>
    </tr>
    <tr>
        <td>Is Duplicate</td>
        <td class="${isDuplicate ? "color-red" : "color-green"}">Yes</td>
    </tr>
    <%--<tr>--%>
        <%--<td>Generated Report Status</td>--%>
        <%--<td>${generatedReport}</td>--%>
    <%--</tr>--%>
    <%--<tr>--%>
        <%--<td>Report</td>--%>
        <%--<td>${report}</td>--%>
    <%--</tr>--%>
</table>

</body>
</html>