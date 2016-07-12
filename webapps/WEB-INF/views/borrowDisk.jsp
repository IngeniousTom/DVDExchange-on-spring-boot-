<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ex" uri="/WEB-INF/first-letter.tld" %>
<%@page session="true" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Позаимствовать диски</title>
    <link href="<c:url value="/resources/css/angular-material.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/resources/css/jquery.mCustomScrollbar.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/resources/css/MainStyleSheet.css"/>" rel="stylesheet"/>
    <c:url var="home" value="/" scope="request"/>
    <sec:csrfMetaTags/>
</head>
<body ng-app="pageApp" ng-cloak data-page-name="borrowDisk" ng-init="numberOfDisks='${AllFreeDisks.size()}'">
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
    <div id="workspace" class="workspace" ng-controller="rowSelectionCtrl">
        <div class="main_space" onmouseover="updateSACheckboxStatus()"
             onclick="updateSACheckboxStatus(); checkForValueButttonConditions($('#borrow-commit-button'));" ng-click="updateDiskInfoField();">
            <div class="h-centeralize table-width page-description">
                <span>Диски напрокат</span>
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
                        <th>Владелец</th>
                    </tr>
                </table>
                <div class="h-centeralize table-width">
                    <div class="bottom_line">
                        <div ng-show="(numberOfDisks < 1)">Нет свободных дисков напрокат</div>
                    </div>
                    <div id="tblock" class="tblock mCustomScrollbar" data-mcs-theme="md">
                        <table id="scrollTable" class="inner_table w2" data-select-mode="none">
                            <c:forEach items="${AllFreeDisks}" var="disk" varStatus="loop">
                                <tr onclick="selectRowsHandler(this, event)" data-selected="false">
                                    <td data-item-id="${disk.id}">
                                        <div>
                                            <md-checkbox role="checkbox" class="md-primary"></md-checkbox>
                                            <span>${loop.index + 1}</span>
                                        </div>
                                    </td>
                                    <td>${disk.name}</td>
                                    <td data-disk-info="${disk.host.lastname} ${disk.host.name} ${disk.host.patronym}
                                                    (ID: ${disk.host.id})">
                                            ${disk.host.lastname}
                                        <ex:first-letter>${disk.host.name}</ex:first-letter>
                                        <ex:first-letter>${disk.host.patronym}</ex:first-letter>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
            </div>
            <div id="buttonSpace" class="button_space">
                <ul>
                    <li>
                        <md-button class="md-primary md-raised md-hue-3" id="borrowDisk" onclick="borrowDisk(this.id)" ng-disabled="(numberOfDisks < 1)">Позаимствовать диск</md-button>
                    </li>
                </ul>
                <div class="counterHolder" ng-init="borrowLimit='${BorrowLimit}'; diskCount='${BorrowCount}'">
                    <b>Всего взято напрокат: </b><span id="diskCounter" class="counter" ng-bind="diskCount"></span><b> из </b>
                    <span class="counter">${BorrowLimit}
                    <md-tooltip md-direction="top">Максимальное число взятых дисков, которыми одновременно можно пользоваться (держать в аренде)</md-tooltip></span>
                    <span id="limitMessage" class="counter" ng-if="diskCount >= borrowLimit"><a href="<c:url value="/takenDisks"/>">(limit!)</a>
                    <md-tooltip md-direction="top">Превышен лимит дисков! Чтобы взять новый диск - верните один из взятых ранее! (стр. "Взятые диски").</md-tooltip>
                    </span>
                </div>
            </div>
            <div id="workspaceDivider" class="workspace_divider" ng-controller="controlPanelCtrl"><%--Область для работы с записями таблицы дисков--%>
                <div id="deleteContextField" class="delete_context_field">
                    <div id="rowsCountField" class="rows_count_field">
                        <span id="diskInfoField"><b>Выберите нужный диск!</b></span>
                        <div id="deleteContextButtonSpace" class="delete-context_button-space">
                            <md-button id="borrow-commit-button" class="md-primary md-raised md-hue-3" onclick="commitButtonPressed()">Взять
                            </md-button>
                            <md-button class="md-primary md-raised md-hue-3" onclick="cancelButtonPressed()">Отмена</md-button>
                        </div>
                    </div>
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