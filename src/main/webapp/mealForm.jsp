<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Books Store Application</title>
</head>
<body>
<center>
    <h1>Books Management</h1>
    <h2>
        <a href="/topjava/meals/new">Add New Meal</a>
        &nbsp;&nbsp;&nbsp;
        <a href="/topjava/meals/list">List All Meals</a>

    </h2>
</center>
<div align="center">
    <c:if test="${meal != null}">
    <form action="/topjava/meals/update" method="post">
        </c:if>
        <c:if test="${meal == null}">
        <form action="insert" method="post">
            </c:if>
            <table border="1" cellpadding="5">
                <caption>
                    <h2>
                        <c:if test="${meal != null}">
                            Edit Meal
                        </c:if>
                        <c:if test="${meal == null}">
                            Add New Meal
                        </c:if>
                    </h2>
                </caption>
                <c:if test="${meal != null}">
                    <input type="hidden" name="id" value="<c:out value='${meal.id}' />" />
                </c:if>
                <tr>
                    <th>Date: </th>
                    <td>
                        <input type="datetime-local" name="dateTime" value="<c:out value='${meal.dateTime}'/>"
                        />
                    </td>
                </tr>
                <tr>
                    <th>Description: </th>
                    <td>
                        <input type="text" name="description"
                               value="<c:out value='${meal.description}' />"
                        />
                    </td>
                </tr>
                <tr>
                    <th>Calories: </th>
                    <td>
                        <input type="text" name="calories"
                               value="<c:out value='${meal.calories}' />"
                        />
                    </td>
                </tr>
                <tr>
                    <td colspan="2" align="center">
                        <input type="submit" value="Save" />
                    </td>
                </tr>
            </table>
        </form>
</div>
</body>
</html>
