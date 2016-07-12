<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ex" uri="/WEB-INF/first-letter.tld" %>
<%@page session="true" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Отданные напрокат диски</title>
    <link href="<c:url value="/resources/css/angular-material.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/resources/css/jquery.mCustomScrollbar.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/resources/css/MainStyleSheet.css"/>" rel="stylesheet"/>
    <c:url var="home" value="/" scope="request"/>
    <sec:csrfMetaTags/>
</head>
<body ng-app="pageApp" ng-cloak data-page-name="givenDisks" ng-init="numberOfDisks='${GivenDisks.size()}'">
<div class="main_container">
    <div class="header">
        <div class="menu">
            <ul>
                <li>
                    <md-button class="md-raised" href="<c:url value="/diskLib"/>">Мои диски</md-button>
                </li>
                <li>
                    <md-button class="md-raised" href="<c:url value="/borrowDisk"/>">Позаимствовать диск</md-button>
                </li>
                <li>
                    <md-button class="md-raised" href="<c:url value="/takenDisks"/>">Взятые диски</md-button>
                </li>
                <li>
                    <md-button class="md-raised" href="<c:url value="/givenDisks"/>">Отданные диски</md-button>
                </li>
            </ul>
        </div>
        <div id="userInfo" class="user_info" ng-controller="userInfo as ctrl">
            <md-menu md-offset="84 48">
                <md-button class="md-button" md-no-ink ng-click="ctrl.openMenu($mdOpenMenu, $event)">
                    ${CurrentUserEmail}
                    <md-tooltip md-direction="top">${CurrentUserEmail}</md-tooltip>
                </md-button>

                <md-menu-content width="2">
                    <md-menu-item>
                        <md-button md-no-ink onclick="formSubmit()">
                            <md-icon md-svg-src="<c:url value="/resources/icons/logout-icon.svg"/>"></md-icon>
                            LogOut
                        </md-button>
                    </md-menu-item>
                </md-menu-content>
            </md-menu>
        </div>
    </div>
    <div id="workspace" class="workspace wh2">
        <div class="main_space" onmouseover="updateSACheckboxStatus()"
             onclick="updateSACheckboxStatus(); checkForEditingContext();">
            <div class="h-centeralize table-width page-description">
                <span>Отданные напрокат диски</span>
            </div>
            <div id="tableSpaceCtrl" ng-controller="tableSpaceCtrl">
                <table class="outer_table w2">
                    <tr>
                        <th>
                            <div class="wrapper">
                                <md-tooltip md-direction="top" id="SATooltip">{{tooltipText}}</md-tooltip>
                                <md-checkbox role="checkbox" on-class-change onclick="triggerSelectAll()" id="SACheckbox" class="md-primary">
                                </md-checkbox>
                            </div>
                            <span id="numberSymbol">№</span>
                        </th>
                        <th>Описание</th>
                        <th>Арендатор</th>
                    </tr>
                </table>
                <div class="h-centeralize table-width">
                    <div class="bottom_line">
                        <div ng-show="(numberOfDisks < 1)">У вас нет отданных напрокат дисков</div>
                    </div>
                    <div id="tblock" class="tblock mCustomScrollbar" data-mcs-theme="md">
                        <table id="scrollTable" class="inner_table w2" data-select-mode="none">
                            <c:forEach items="${GivenDisks}" var="givenDisk" varStatus="loop">
                                <tr onclick="selectRowsHandler(this, event)" data-selected="false">
                                    <td data-item-id="${givenDisk.iddisk}">
                                        <div>
                                            <md-checkbox role="checkbox" class="md-primary"></md-checkbox>
                                            <span>${loop.index + 1}</span>
                                        </div>
                                    </td>
                                    <td>${givenDisk.disk.name}</td>
                                    <td data-disk-info="${givenDisk.tempUser.lastname} ${givenDisk.tempUser.name} ${givenDisk.tempUser.patronym}
                                                    (ID: ${givenDisk.tempUser.id})">
                                            ${givenDisk.tempUser.lastname}
                                        <ex:first-letter>${givenDisk.tempUser.name}</ex:first-letter>
                                        <ex:first-letter>${givenDisk.tempUser.patronym}</ex:first-letter>
                                        <md-tooltip md-direction="top" ng-cloak>${givenDisk.tempUser.lastname} ${givenDisk.tempUser.name} ${givenDisk.tempUser.patronym}
                                            (ID: ${givenDisk.tempUser.id})
                                        </md-tooltip>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
                <div id="buttonSpace" class="button_space">
                    <div class="counterHolder">
                        <b>Всего отдано напрокат: </b><span id="borrowedDisksCounter" class="counter">${GivenCount}</span><b> из </b><span
                            class="counter"><a href="<c:url value="/diskLib"/>">${TotalCount}</a><md-tooltip md-direction="top" ng-cloak>Общее число взятых дисков, предоставленных вами для проката.
                    Левый клик - подробный просмотр всех дисков (на стр. "Мои диски")</md-tooltip></span>
                    </div>
                </div>
            </div>
        </div>
    </div>


    <!-- Все необходимые теги и данные для выхода из текущего аккаунта-->
    <c:url value="/j_spring_security_logout" var="logoutUrl"/>
    <form action="${logoutUrl}" method="post" id="logoutForm">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
    <script>
        function formSubmit() {
            document.getElementById("logoutForm").submit();
        }
    </script>

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

    <!--Библиотеки mCustomScrollbar()-->
    <script src="<c:url value="/resources/js/jquery.mCustomScrollbar.concat.min.js"/>"></script>

    <%--Главный скрипт для работы элементов управления страницы--%>
    <script src="<c:url value="/resources/js/MainScript.js"/>"></script>

</body>
</html>
