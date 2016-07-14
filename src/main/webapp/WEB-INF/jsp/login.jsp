<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<html>
<head>
    <title>Страница авторизации</title>
    <link href="<c:url value="/resources/css/angular-material.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/resources/css/jquery.mCustomScrollbar.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/resources/css/LoginStyleSheet.css"/>" rel="stylesheet"/>
    <c:url var="home" value="/" scope="request"/>
</head>
<body ng-app="pageApp" ng-cloak>

<div class="h-centeralize">
    <div class="description-box"><h1>DVDExchange</h1>
        <h2>Сервис для быстрого и удобного обмена DVD-дисками.</h2>
    </div>

    <div id="login-box" class="login-box h-text-centeralize">
        <div class="textBlock h-text-centeralize">
            <h3 class="primary_color">Вход в систему</h3>
            Введите Логин и Пароль, чтобы войти
        </div>

        <c:if test="${not empty error}">
            <div class="sys-msg-box error">${error}</div>
        </c:if>
        <c:if test="${not empty msg}">
            <div class="sys-msg-box msg">${msg}</div>
        </c:if>

        <form name='loginForm' action="<c:url value='/j_spring_security_check'/>" method='POST' id='loginForm' onkeypress="submitOnEnter(event)" ng-cloak>
            <md-input-container md-no-float>
                <label for="username">Логин</label>
                <input type="text" name="username" id="username" maxlength="120" autofocus size="35">
            </md-input-container>

            <md-input-container md-no-float>
                <label for="password">Пароль</label>
                <input type="password" name="password" maxlength="45" autofocus size="35" id="password">
            </md-input-container>

            <div class="h-text-centeralize">
                <md-button class="md-primary md-raised md-hue-3" onclick="formSubmit()">Вход</md-button>
            </div>

            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>
</div>
<!-- Angular.js библиотеки для использования Angular Material -->
<script src="<c:url value="/resources/js/angular.min.js"/>"></script>
<script src="<c:url value="/resources/js/angular-animate.min.js"/>"></script>
<script src="<c:url value="/resources/js/angular-route.min.js"/>"></script>
<script src="<c:url value="/resources/js/angular-aria.min.js"/>"></script>
<script src="<c:url value="/resources/js/angular-messages.min.js"/>"></script>
<script src="<c:url value="/resources/js/svg-assets-cache.js"/>"></script>

<!-- Библиотеки фреймфорка Angular Material  -->
<script src="<c:url value="/resources/js/angular-material.js"/>"></script>

<%--Подключение JQuey--%>
<script src="<c:url value="/resources/js/jquery-1.12.3.min.js"/>"></script>

<%--Пдключения файла скрипта страницы--%>
<script src="<c:url value="/resources/js/LoginScript.js"/>"></script>

</body>
</html>