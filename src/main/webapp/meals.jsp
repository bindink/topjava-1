<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Meals</title>
</head>
<body>
<h2> <a href="index.html">HOME</a></h2>
<hr>
<h2>Meals list</h2>
<a href="/topjava/meals/new">Add New Meal</a>
<table border="1" cellspacing="0" cellpadding="2">
    <tr>
        <td>Date</td>
        <td>Description</td>
        <td>Calories</td>
        <td></td>
        <td></td>
    </tr>

    <c:forEach var="meal" items="${mealsTo}">
        <tr style="color:${meal.excess ? 'red' : '#00cc00'}">
            <td>
                <fmt:parseDate value="${meal.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${parsedDateTime}" />
            </td>
            <td><c:out value="${meal.description}"/></td>
            <td><c:out value="${meal.calories}"/></td>
            <td> <a href="/topjava/meals/edit?id=<c:out value='${meal.id}'/>">Update</a></td>
            <td><a href="/topjava/meals/delete?id=<c:out value='${meal.id}'/>">Delete</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
