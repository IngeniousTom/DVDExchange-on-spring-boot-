<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="true" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ваши диски</title>
    <link href="<c:url value="/resources/css/angular-material.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/resources/css/jquery.mCustomScrollbar.min.css"/>" rel="stylesheet"/>
    <link href="<c:url value="/resources/css/MainStyleSheet.css"/>" rel="stylesheet"/>
    <c:url var="home" value="/" scope="request"/>
    <sec:csrfMetaTags/>
</head>
<body ng-app="pageApp" ng-cloak data-page-name="diskLib" ng-init="numberOfDisks=${AllDisksOfTheUser.size()}; numberOfBlockedDisks=${GivenDisksCount}">
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
             onclick="updateSACheckboxStatus(); updateSelectedRowCounter(); checkForValueButttonConditions($('#delete-commit-button'));
              checkForValueButttonConditions($('#edit-create-commit-button'));" ng-click="showDiskDescription();">
            <div class="h-centeralize table-width page-description">
                <span>Каталог ваших дисков</span>
            </div>
            <div id="tableSpaceCtrl" ng-controller="tableSpaceCtrl">
                <table class="outer_table">
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
                        <th>Статус</th>
                    </tr>
                </table>
                <div class="h-centeralize table-width">
                    <div class="bottom_line">
                        <div ng-show="(numberOfDisks < 1)">У вас нет дисков в каталоге</div>
                    </div>
                    <div id="tblock" class="tblock mCustomScrollbar" data-mcs-theme="md">
                        <table id="scrollTable" class="inner_table" data-select-mode="none">
                            <c:forEach items="${AllDisksOfTheUser}" var="disk" varStatus="loop">
                                <tr onclick="selectRowsHandler(this, event)" data-selected="false" data-unavailable-to-delete="${disk.given == true}">
                                    <td data-item-id="${disk.id}">
                                        <div>
                                            <md-checkbox role="checkbox" class="md-primary"></md-checkbox>
                                            <span>${loop.index + 1}</span>
                                        </div>
                                    </td>
                                    <td>${disk.name}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${disk.given == true}">
                                                Занят
                                            </c:when>
                                            <c:otherwise>
                                                Свободен
                                            </c:otherwise>
                                        </c:choose>
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
                        <md-button class="md-primary md-raised md-hue-3" id="newDisk" onclick="NewDisk(this.id)" ng-disabled="(numberOfDisks < 1)">Добавить диск</md-button>
                    </li>
                    <li>
                        <md-button class="md-primary md-raised md-hue-3" id="editDisk" onclick="EditDisk(this.id)" ng-disabled="(numberOfDisks < 1)">Изменить описание</md-button>
                    </li>
                    <li>
                        <md-button class="md-primary md-raised md-hue-3" id="deleteDisk" onclick=" DeleteDisk(this.id)" ng-disabled="(numberOfDisks < 1)||(numberOfDisks == numberOfBlockedDisks)"
                                   ng-attr-data-blocked="{{(numberOfDisks == numberOfBlockedDisks)}}">
                            Удалить диск
                        </md-button>
                    </li>
                </ul>
                <div class="counterHolder" ng-init="diskCount='${AllDisksOfTheUser.size()}'">
                    <b>Всего дисков: </b><span id="diskCounter" class="counter" ng-bind="diskCount"></span>
                </div>
            </div>
            <div id="workspaceDivider" class="workspace_divider" ng-controller="controlPanelCtrl" ng-variable="TAValue" ng-init="Context=''">
                <div id="contentEditField" class="content_edit_field">
                    <md-input-container md-no-float>
                        <textarea placeholder="Введите описание диска (не более 100 символов)" maxlength="100" cols="55" data-selected-item="" onkeypress="preventEnterKey(event);" ng-model="TAValue">
                        </textarea>
                    </md-input-container>
                    <div class="edit_button-field_container">
                        <md-button class="md-primary md-raised md-hue-3" id="edit-create-commit-button" onclick="commitButtonPressed()" ng-disabled="((Context == 'add')&&(TAValue == ''))">Сохранить
                        </md-button>
                        <md-button class="md-primary md-raised md-hue-3" onclick="cancelButtonPressed();resetTextArea();" ng-click="Context=''">Отмена</md-button>
                    </div>
                </div>
                <div id="deleteContextField" class="delete_context_field">
                    <div id="rowsCountField" class="rows_count_field">
                        <b>Всего выбрано: </b><span id="selectedRowsCounter" class="counter"></span>
                        <div id="deleteContextButtonSpace" class="delete-context_button-space">
                            <md-button class="md-primary md-raised md-hue-3" id="delete-commit-button" onclick="commitButtonPressed()">Удалить
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
        $("#logoutForm").submit();
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