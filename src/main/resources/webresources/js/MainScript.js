//--------------------------------------------Глобальные переменные (доступны в том числе и в контроллерах и директивах angular)-----------------------------------------------------

var selectedRowsIds = [];           //Множество Id строк(дисков), выбранных пользователем
var blockedRowsCount = 0;           //Множество Id задлокированных строк(дисков)
var lastSelectedRow = 0;            //Номер/индекс последней выделенной строки, используется для быстрого выбора строк через shift
var textareaNewDiskPlaceholder = "Введите описание диска (не более 100 символов)...";   //Тексты подсказое для тектового поля для контектов добавления и редактирования диска
var textareaEditDiskPlaceholder = "Выберите диск чтобы изменить его описание...";
var textareaEditDiskPlaceholderSelected = "Введите новое описание...";
var diskInfoPlaceholder = "<b>Выберите нужный диск!</b>";                //Текст подсказки при удалении диска
var actionContext = "";             //Контекст работы с дисками (создание, редактирование, удаление, заимствование, возврат, просмотр (по-умолчанию))
var pageName = $('body').attr("data-page-name");              //название страницы

//----Ключевые элементы страницы приложения---------------------------------------

var tableBodyBlock = $("#tblock");                   //Блок в котором содержится динамическая часть таблицы (т.е. записи)
var selectAllCheckbox = $("#SACheckbox");            //Чекбокс, обладающий функциями "Выделить всё!/Снять всё выделение!"
var numberSymbol = $("#numberSymbol");               //Элемент <span> с символом "№" в шапке таблицы.
var createEditField = $("#contentEditField");        //Главная облать с информацией о текущих действиях с записями таблицы
var deleteContextField = $("#deleteContextField");   //Дополнительная область с информацией о действиях с записями таблицы (ипользуется н.р. в контексте удаления, отсюда название)
var textArea = $("#contentEditField textarea");      //Многострочное текстовое поле для редактирования данных о дисках пользователя
var diskCounterHolder = $('div.counterHolder');      //Область, содержащая счетчик дисков
var diskInfoField = $('#diskInfoField');             //Поле под информацию о выбранном диске
var workspaceDivider = $('#workspaceDivider');       //Область редактирования данных таблицы

//--------------------------------------Функции конпок работы с таблицей (Мои Диски)--------------------------------------------------

function NewDisk(currentButtonId) {
    /**Переключение программы в контекст добавления нового диска*/
    if (actionContext != "add") {                 //предотвращение повторного выполнения действий если кнопка уже нажата
        mainButtonsDeadlock(currentButtonId);     //блокировка всех кнопок, кроме той, что вызвала текущий режим работы с таблицей. (подр. в описании функции)
        actionContext = "add";                    //присвоить переменной контекста (режима работы) занчения "добавление нового диска (add)"
        angular.element(workspaceDivider).scope().Context = 'add';    //запись текущего контекста (режима) во вспомогательную переменную для angular.
        // Данный способ выбран потому, что ng-click="Context='add'" на кнопке "Добавить диск" срабатывает лишь один раз
        textArea.prop("placeholder", textareaNewDiskPlaceholder);        //приминение соответствующей подсказки
        createEditField.css("display", "block");                         //сделать видимым то, что нужно для добавления нового диска
        deleteContextField.css("display", "none");                       //скрыть то, что не относится к добавлению диска
        resetTextArea();                                                 //очистка текстового поля
        workspaceDivider.css("max-height", "190px");                     //открыть область работы с контентом таблицы
    }
}

function EditDisk(currentButtonId) {
    /**Переключение программы в контекст изменения описания диска*/
    if (actionContext != "edit") {
        mainButtonsDeadlock(currentButtonId);
        actionContext = "edit";
        disableButton('#edit-create-commit-button', true);
        textArea.prop("placeholder", textareaEditDiskPlaceholder);
        createEditField.css("display", "block");
        deleteContextField.css("display", "none");
        resetTextArea();
        workspaceDivider.css("max-height", "190px");
        enableRowsSelectMode(false);                 //активация одиночного режима выбора строк (дисков)  (только 1 строка)
    }
}

function DeleteDisk(currentButtonId) {
    /**Переключение программы в контекст удаления диска(ов)*/
    if (actionContext != "delete") {
        mainButtonsDeadlock(currentButtonId);
        actionContext = "delete";
        disableButton('#delete-commit-button', true);
        deleteContextField.css("display", "block");
        createEditField.css("display", "none");
        workspaceDivider.css("max-height", "190px");
        enableRowsSelectMode(true);                //активация множественного режима выбора строк (дисков) (1 и более строк)
    }
}

function borrowDisk() {
    /**Переключение программы в контекст займа диска*/
    actionContext = "borrow";
    deleteContextField.css("display", "block");
    workspaceDivider.css("max-height", "190px");
    enableRowsSelectMode(false);    //активация одиночного режима выбора строк (дисков)  (только 1 строка)
}

function giveDiskBack() {
    /**Переключение программы в контекст возврата дисков*/
    actionContext = "giveBack";
    deleteContextField.css("display", "block");
    workspaceDivider.css("max-height", "190px");
    enableRowsSelectMode(true);                  //активация множественного режима выбора строк (дисков) (1 и более строк)
}

//----------------------------------Кнопки формы редактирования---------------------------------------------------------

function commitButtonPressed() {
    /**Функция отправки соответствующего ajax-запроса в зависимости
     **от контекста(режима) работы программы.*/
    switch (actionContext) {
        case "add":
            createDiskViaAjax();
            break;
        case "edit":
            editDiskViaAjax();
            break;
        case "delete":
            deleteDiskViaAjax();
            break;
        case "borrow":
            borrowDiskViaAjax();
            break;
        case "giveBack":
            giveBackDiskViaAjax();
            break;
    }
}

function cancelButtonPressed() {
    /**Отмена всех незафиксированных в БД дествий с дисками пользователя
     * и переход в обычный режим(контекст) просмотра*/
    actionContext = "";                 //контекст просмотра
    disableRowsSelectMode();           //отключения режима выбора строк (дисков)
    selectAllRowsToggle(false);        //убрать выделение со всех строк
    refreshButtons();                  //сделать вновь активными все кнопки дейтсвий с таблицей (добавление, удаление и т.д...)
    workspaceDivider.css("max-height", "0px");   //Закрыть область работы с контентом таблицы
}

//--------------------------------------Вспомогательные функции---------------------------------------------------------

function resetTextArea() {
    /**Сброс параметров текстового поля до изначального состояния*/
    eraseTAText();
    textArea.css("height", "30px");            //если просто удалить весь текст, то высота textarea сама не вернется в начальное состояние
    textArea.attr("data-selected-item", "");   //запись id выбранного диска.....
}

function eraseTAText() {
    /**Стирает текст внутри тектового поля и приводит его ng-model в состояние пустой строки*/
    var TAScope = angular.element(textArea).scope();      //доступ к области видимости, где находятся доп. переменная текстового поля для  angular-функционала
    textArea.val("");          //обнуление текстового поля (очистка в 0 символов)
    TAScope.TAValue = "";           // Это действие нужно, потому что, если обнулить содержимое текстового поля программно, то ng-model не среагирует  (не обновится)
}

function mainButtonsDeadlock(currentButtonId) {
    /**Блокирует все управляющие клавиши формы ("добавить", "редактировать" и "удалить"), кроме вызвавшей текущий контекст работы с таблицей.
     * Текущая кнопка должна быть активной, чтобы пользователь лучше понимал, в каком режиме работы находится программа*/
    var buttons = $("#buttonSpace button");
    buttons.each(function () {
        if ($(this).prop("id") !== currentButtonId) {
            disableButton(this, true);
        }
    });
}

function refreshButtons() {                        //*
    /**Разблокирует все управляющие кнопки формы, кроме специально заблокированных*/
    var buttons = $('#buttonSpace button');
    buttons.each(function () {
        if ($(this).attr("data-blocked") != "true") {      //"data-blocked" - атрибут-признак того, что кнопка заблокирована
            disableButton(this, false);
        }
    });
}

function enableRowsSelectMode(isMultiselect) {
    /**Активирует режим выделения записей таблицы (выбора дисков)
     * single- режим выбора только одной из записей, multi-нескольких*/
    var table = $("table.inner_table");
    var tableRows = table.find("tr");

    tableRows.each(function () {
        var currentRowCheckbox = $(this).find("td:first-child > div > md-checkbox");
        var currentRowNumber = $(this).find("td:first-child > div > span");
        currentRowNumber.css("display", "none");                  //номера строк скрываются,
        currentRowCheckbox.css("display", "inline-block");      //а на их месте появляются чекбоксы
        //"Занятые", т.е. отданные в аренду диски удалить нельзя, значит, если строка отображает информацию о диске, то ее выделение блокируется в режиме удаления
        if ((actionContext == 'delete') && ($(this).attr("data-unavailable-to-delete") == "true")) {
            currentRowCheckbox.attr("disabled", true);           //чекбокс нужно сделать неактивным
            $(this).attr("data-selected", "blocked");            //добавление строке признака блокировки (с помощью css-стилей строка с этим признаком приобретает "неактивный" внешний вид)
        }
    });

    if (isMultiselect) {
        table.attr("data-select-mode", "multi");          //запись в атрибут таблицы информации о режиме выбора строк (дисков)
        selectAllCheckbox.css("display", "inline-block");   //Если режим выбора строк - "множественный", то нужно также сделать видимым чекбокс для выбора/отмены выделения всех строк
        numberSymbol.css("display", "none");                //А символ "№" нужно скрыть
    } else {
        table.attr("data-select-mode", "single");       //запись в атрибут таблицы информации о режиме выбора строк (дисков)
    }
}

function disableRowsSelectMode() {
    /**Отключение режима выбора записей таблицы (дисков):
     * data-select-mode = "none" */
    var table = $("table.inner_table");
    var tableRows = table.find("tr");
    //Все строки таблицы приводятся в изначальный вид, в котором они были в режиме просмотра, т.е. при actionContext=""
    tableRows.each(function () {
        var currentRowCheckbox = $(this).find("td:first-child > div > md-checkbox");
        var currentRowNumber = $(this).find("td:first-child > div > span");
        currentRowCheckbox.css("display", "none");
        currentRowCheckbox.attr("disabled", false);
        currentRowNumber.css("display", "inline");
        if ($(this).attr("data-unavailable-to-delete") == "true") {
            $(this).attr("data-selected", "false");                          //с заблокированных строк снимается блокировка, т.к. в других контекстах она может быть ненужной
        }
    });
    selectAllCheckbox.css("display", "none");
    numberSymbol.css("display", "inline");
    table.attr("data-select-mode", "none");  //признак того что таблица больше не в режиме выбора строк
}

function selectRowsHandler(currentRow, event) {
    /**Обработчик выделения записей таблицы (выбора дисков) в одиночном и мультистрочном режимах.
     * Многострочный режим поддерживает быстрое выделение через зажатие shift*/
    var currentCheckbox = $(currentRow).find("td:first-child > div > md-checkbox");
    var currentItemId = $(currentRow).find("td:first-child").attr("data-item-id");
    var rows = $("table.inner_table tr");
    var selectMode = $("table.inner_table").attr("data-select-mode");

    switch (selectMode) {              //алгоритм зависит от режима выбора строк (мультистрочный(множественный) или одиночный)
        case "single":
            if ($(currentRow).attr("data-selected") == "false") {        //если выбранная строка еще не выделена
                selectAllRowsToggle(false);                              //сперва снять выделение со всех строк перед тем, как выделить новую
                $(currentRow).attr("data-selected", "true");             //отметить что строка выбрана
                currentCheckbox.addClass("md-checked");                  //отметить калочкой чекбокс
                selectedRowsIds.push(currentItemId);                     //занести id выбранной строки в соответсвующий массив
                textArea.attr("data-selected-item", currentItemId);      //
            } else {                                                     //если выбранная строка уже выделена (была выбрана ранее)
                $(currentRow).attr("data-selected", "false");            //отметить что строка не выбрана
                currentCheckbox.removeClass("md-checked");               //убрать метку с чекбокса
                selectedRowsIds.splice(selectedRowsIds.indexOf(currentItemId), 1);      //удалить id выбранной строки из массива выбранных дисков
                textArea.attr("data-selected-item", "");                                //
            }
            break;
        case "multi":
            if ((event.shiftKey)) {
                //С зажатой клавишей shift
                var start = 0;                    //наименьший (ближе к началу таблицы) порядковый номер свреди всех выделенных строк
                var end = 0;                      //наибольший (ближе к концу таблицы) порядковый номер свреди всех выделенных строк
                if (lastSelectedRow < currentRow.rowIndex) {     //если предыдущая выделенная строка ближе к началу таблицы, то цикл выделения строк начнется с нее
                    start = lastSelectedRow;
                    end = currentRow.rowIndex;
                } else {                                         //если предыдущая выделенная строка ближе к концу таблицы, то цикл выделения начнется с текущей выделенной строки
                    end = lastSelectedRow;
                    start = currentRow.rowIndex;
                }
                //выделение/ снятие выделения строк между прошлой и текущей выбранными строками, включая предыдущую и текущую
                if ($(currentRow).attr("data-selected") == "false") {      //если выбранная строка еще не выделена
                    rows.each(function () {
                        if (($(this).index() >= start) && ($(this).index() <= end) && ($(this).attr("data-selected") != "blocked")) {
                            var iCheckbox = $(this).find("td:first-child > div > md-checkbox");
                            var iItemId = $(this).find("td:first-child").attr("data-item-id");
                            $(this).attr("data-selected", "true");
                            iCheckbox.addClass("md-checked");
                            if (selectedRowsIds.indexOf(iItemId) == -1) {   //проверка, чтобы в массиве id выделенных строк элементы не дублировались
                                selectedRowsIds.push(iItemId);
                            }
                        }
                    })
                } else if ($(currentRow).attr("data-selected") == "true") {           //если выбранная строка уже была выделена
                    rows.each(function () {
                        if (($(this).index() >= start) && ($(this).index() <= end) && ($(this).attr("data-selected") != "blocked")) {
                            var iCheckbox = $(this).find("td:first-child > div > md-checkbox");
                            var iItemId = $(this).find("td:first-child").attr("data-item-id");
                            $(this).attr("data-selected", "false");
                            iCheckbox.removeClass("md-checked");
                            if (selectedRowsIds.indexOf(iItemId) != -1) {
                                selectedRowsIds.splice(selectedRowsIds.indexOf(iItemId), 1);
                            }
                        }
                    })
                }
            } else {     //Без зажатой клавиши shift
                if ($(currentRow).attr("data-selected") == "false") {              //если выбранная строка еще не выделена
                    $(currentRow).attr("data-selected", "true");
                    currentCheckbox.addClass("md-checked");
                    selectedRowsIds.push(currentItemId);
                } else if ($(currentRow).attr("data-selected") == "true") {         //если выбранная строка уже была выделена
                    $(currentRow).attr("data-selected", "false");
                    currentCheckbox.removeClass("md-checked");
                    selectedRowsIds.splice(selectedRowsIds.indexOf(currentItemId), 1);
                }
            }
            break;
    }
    lastSelectedRow = currentRow.rowIndex;            //запоминание номера текущей строки для использование в качестве предыдущей при следующем выделении
}

function selectAllRowsToggle(flag) {
    /**Выделяет все строки/снимает все выделение в зависимости от того, выделены все строки или нет*/
    selectedRowsIds = [];                     //не важно, что требуется, в начале все равно разумно обнулить массив id выбранных строк(дисков)
    var rows = $("table.inner_table tr");

    rows.each(function () {
        if (($(this).attr("data-selected") != "blocked")) {    //действия над строкой производятся только если она не заблокированна
            $(this).attr("data-selected", String(flag));        //выделить строки если true и снять выделение если false
            var checkbox = $(this).find("td:first-child > div > md-checkbox");
            var currentItemId = $(this).find("td:first-child").attr("data-item-id");

            if (flag) {
                checkbox.addClass("md-checked");              //поставить или снять галочку чекбокса строки
                selectedRowsIds.push(currentItemId);
            } else {
                checkbox.removeClass("md-checked");
            }
        }
    });
}

function updateSelectedRowCounter() {
    /*Обновление счетчика выделенных строк для angular-функционала и отображения на странице*/
    var counterScope = angular.element($('#workspaceDivider')).scope();
    counterScope.SelectedCount = selectedRowsIds.length;
    $("#selectedRowsCounter").html(selectedRowsIds.length);
}

function triggerSelectAll() {
    /**Выделяет все строки или снимает выделение в зависисмости от того, выделены все строки или нет*/
    if (!selectAllCheckbox.hasClass("md-checked")) {     //чекбокс "Выделить все" устроен так, что он отмечен (.hasClass("md-checked")), только если выделены все строки
        selectAllRowsToggle(true);
    } else {
        selectAllRowsToggle(false);
    }
}

function preventEnterKey(event) {
    /**Запрещает нажатие клавиши Enter, т.е. запрещает перенос строки*/
    if (event.keyCode == 13) {
        event.preventDefault();
    }
}

function updateSACheckboxStatus() {
    /**Используется для решения, в какое состояние переключить чекбокс с функцией "Выделить все!"*/
    var numberOfTableRows = $("#scrollTable tr").length;
    //Проверяет количество выделенных строк из общей массы строк за исключением блокированных(они не учитываются).
    //В зависимости от этого чекбокс "Выделить все" либо становится отмеченным, либо неотмеченным (без галочки)
    toggleSACheckbox(selectedRowsIds.length === numberOfTableRows - blockedRowsCount);
}

function toggleSACheckbox(flag) {
    /**Переключение состояния чекбокса "Выделить все!" */
    if (flag) {
        if (!selectAllCheckbox.hasClass("md-checked")) {
            selectAllCheckbox.addClass("md-checked");
        }
    } else {
        if (selectAllCheckbox.hasClass("md-checked")) {
            selectAllCheckbox.removeClass("md-checked");
        }
    }
}

function disableButton(buttonId, flag) {
    /**Функция делает код более читабельным, хотя функционал простой
     * В качестве первого аргумента можно передавать как строку id или любой jquery-серектор, так и сам элемент в виде объекта (DOM)*/
    $(buttonId).prop("disabled", flag);
}

function checkForValueButttonConditions(targetButton) {
    /*Функция служит для блокировки клавиши отправки запроса, если не выделено ни одного диска и разблокировки, если выделен хотя бы один*/
    if (actionContext != "add") {                                  //при добавлении нового диска эта функция не нужна
        if (selectedRowsIds.length > 0) {
            if ($(targetButton).prop("disabled") == true) {
                $(targetButton).prop("disabled", false);
            }
        } else {
            if ($(targetButton).prop("disabled") == false) {
                $(targetButton).prop("disabled", true);
            }
        }
    }
}

function updateCounterValue(newValue) {
    /*Обновляет общее количество дисков в счетчиках дисков для angular* и счетчик общего кол-ва дисков на странице*/
    var counterScope = angular.element(diskCounterHolder).scope();
    var bodyScope = angular.element($('body')).scope();

    bodyScope.numberOfDisks = parseInt(newValue);
    counterScope.diskCount = parseInt(newValue);
    $("#diskCounter").html(newValue);
}

function updateBlockedRowsCounter() {
    /*Обновляет количество заблокированных дисков в счетчиках дисков для иcпользования в angular*/
    var blockedTableRows = $('table.inner_table tr[data-unavailable-to-delete="true"]');
    var numberOfBlockedDisks = angular.element($('body')).scope().numberOfBlockedDisks;

    numberOfBlockedDisks = blockedTableRows.length;
}

function firstLetter(sourceString) {
    /*Оставляет от исходной строки первую букву, возводя её в верхний регистр и добавляя после неё точку*/
    return sourceString.substr(0, 1).toUpperCase() + ".";
}

function idComparator(a, b) {
    /**Сравнивает 2 числа по возрастанию*/
    return parseInt(a["id"]) - parseInt(b["id"]);
}

//------------------------------------------Инициализация Angular Material----------------------------------------------

var app = angular.module('pageApp', ['ngMaterial']);         //внедрение ресурсов фрэймворка ngMaterial в текущее angular-приложение

app.config(function ($mdThemingProvider) {
    // Расширение темы "индиго" новым цветом через создание новой темы
    var newIndigoMap = $mdThemingProvider.extendPalette('indigo', {
        '500': '#5C6BC0'
    });
    // Регистрация новой цифровой палитры c названием newIndigo
    $mdThemingProvider.definePalette('newIndigo', newIndigoMap);
    // Присвоение новой палитры в роли основной
    $mdThemingProvider.theme('default')
        .primaryPalette('newIndigo', {
            'hue-3': '500'            //новый цвет ставится на место третьего оттенка
        })
        .accentPalette('orange');     //акцентная палитра - оранжевая
});

//------------------------------------------Настройка тонкостей работы плагина jquery.mCustomScrollbar----------------------------------------------

$(".content").mCustomScrollbar({
    scrollInertia: 250,                     //плавность прокуртки контента
    scrollbarPosition: "outside",           //отображать полосу прокрутки снаружи блока
    mouseWheel: {preventDefault: true},     //доп. настройка для предотвращения прокуртки тела документа вместо содержимого блока
    mouseWheel: {scrollAmount: 70}          //скорость прокурутки колесиком мыши в пикселях
});

//---------------------------------------------Angularjs логика---------------------------------------------------------

app.controller('userInfo', function menuCtrl() {
    /**Контроллер, отвечающий за открытие меню выхода из текущего аккаунта*/
    var originatorEv;       //ссылка на событие, которое привело к открытию меню

    this.openMenu = function ($mdOpenMenu, ev) {
        originatorEv = ev;   //получение ссылки на событие
        $mdOpenMenu(ev);     //открытие меню с помощью специального сервиса
    };
});

app.controller('tableSpaceCtrl', function ($scope, $compile, $mdToast) {
    /**Главный контроллер, содержащий несколько полезных функций (вывод уведомлений, компиляция angular элементов и т.д.)*/
    $scope.tooltipText = "Выделить все!";              //Текст подсказки для чекбокса "Выделить все!"

    var last = {                                       //Шаблон конфигурации направления движения всплывающих уведомлений в исходном виде (направление задано как "снизу-справа")
        bottom: true,
        top: false,
        left: false,
        right: true
    };

    $scope.toastPosition = angular.extend({}, last);   //Конфигурация направления движения всплывающих уведомлений

    function sanitizePosition() {
        /**Исправляет конфигурацию направления движения (всплывания) уведомления,
         убирая взаимоисключающие варианты*/
        var current = $scope.toastPosition;

        if (current.bottom && last.top) current.top = false;        //верх и низ несовместимы
        if (current.top && last.bottom) current.bottom = false;
        if (current.right && last.left) current.left = false;       //право и лево тоже
        if (current.left && last.right) current.right = false;

        last = angular.extend({}, current);         //запоминание текущей конфигурации для следующей проверки при её изменении
    };

    $scope.getToastPosition = function () {
        /**Получение исправленной конфигурации движения для уведомлений*/
        sanitizePosition();
        return Object.keys($scope.toastPosition)
            .filter(function (pos) {
                return $scope.toastPosition[pos];
            })
            .join(' ');
    };

    $scope.showToast = function (msg) {
        /**Выводит всплывающие уведомления с заданным текстом*/
        $mdToast.show(
            $mdToast.simple()
                .textContent(msg)
                .position($scope.getToastPosition())         //уведомление всплывает в позиции, описанной в конфигурации
                .hideDelay(3000)
                .parent($('#workspace'))
        );
    };

    $scope.compileFreshElements = function () {
        /**Компилирует angular директивы и элементы в записях таблицы, полученных из ajax-ответа (конкретно чекбоксы md-checkbox)*/
        var checkboxes = $("table.inner_table tr > td:first-child > div");
        //каждый чекбокс таблицы поочередно компилируется средствами angularjs
        checkboxes.each(function () {
            var aCB = angular.element($(this));
            var scope = aCB.scope();
            $compile(aCB.contents())(scope);
            $(this).replaceWith(aCB);                   //чекбокс текущей строки таблицы заменяется на свой скомпилированный вариант
        });
    };

});

app.controller('controlPanelCtrl', function ($scope, $mdDialog) {
    /**Контроллер, содержащий функции связанные с панелью кнопок для работы с каталогом дисков пользователя (т.е. записями таблицы)*/
    $scope.showAlert = function (event, msg, title) {
        // Добавление всплывающего окна к document.body для того, чтобы перекрыть sidenav в docs app,
        // т.к. модальные окна должны польностью покрывать всё окно приложения,
        // чтобы предотвратить взаимодествие с остальным интерфейсом
        $mdDialog.show(
            $mdDialog.alert()
                .parent(angular.element($('body')))
                .clickOutsideToClose(true)
                .title((title) ? title : 'Контроль ввода')    //если третий аргумент 'title' не передан, то импользуется заголовок по-умолчанию
                .textContent(msg)
                .ariaLabel('Alert Dialog')
                .ok('ОК')
                .targetEvent(event)
        );
    };
});

app.controller('rowSelectionCtrl', function ($scope) {
    /**Контроллер отвечает за дейтсвия, которые могут быть полезны, когда пользователь выделяет строки в режиме выбора*/
    $scope.getSelectedRow = function () {
        /*Вспомогательная функция для получения текущей выбранной строки(диска)*/
        var selector = 'table.inner_table tr td:first-child[data-item-id="' + selectedRowsIds[0] + '"]';
        var selectedRow = $(selector).parent('tr');
        return selectedRow;
    };

    $scope.updateDiskInfoField = function () {
        //Отображает подсказку, если ни одного диска не выбрано или выводит информацию о выбранном диске, если таковой есть при заимствоании диска (actionContext="borrow")
        if (selectedRowsIds.length > 0) {
            var sourceRow = $scope.getSelectedRow();
            var diskDescripion = sourceRow.find("td:nth-child(2)").html();
            var hostInfo = sourceRow.find("td:nth-child(3)").attr("data-disk-info");
            var diskInfo = '<b>Диск: </b>' + diskDescripion + '<br><b>Владелец: </b>' + hostInfo;

            diskInfoField.html(diskInfo);
        } else {
            diskInfoField.html(diskInfoPlaceholder);
        }
    };

    $scope.showDiskDescription = function () {
        /*Отображает описание диска и применяет соответствующую подсказку(placeholder) в текстовом поле если диск выбран.
         * Если выбранных дисков нет, то убирает всю инфорацию из текстового поля и возвращает ему исходную подсказку для этого режима работы (actionContext="edit")*/
        if (selectedRowsIds.length > 0) {
            var sourceRow = $scope.getSelectedRow();
            var currentDiskDescription = sourceRow.find("td:nth-child(2)").html();

            textArea.attr("placeholder", textareaEditDiskPlaceholderSelected);
            textArea.val(currentDiskDescription);
            textArea.focus();
        } else {
            textArea.attr("placeholder", textareaEditDiskPlaceholder);
            textArea.val("");
        }
    };

});

app.directive('onClassChange', function () {
    /**Директива для переключения текста подсказки к чекбоксу с функцией "Выбрать всё/Снять все выделение" **/
    function link(scope, element) {
        scope.$watch(function () {         //сервис $watch помогает отслеживать изменения в элементах документа
            return element.attr('class');       //отслеживаем изменения в классе элемента (в данном случае чекбокса "Выдеить все")
        }, function (newValue, oldValue) {
            if (newValue !== oldValue) {                   //Значения равны при инициализации
                if ($(element).hasClass("md-checked")) {
                    scope.tooltipText = "Снять все выделение!"
                } else {
                    scope.tooltipText = "Выделить все!"
                }
            }
        });
    }

    return {
        link: link  //привязка контроллирующей функции
    };
});

//--------------------------------------------------AJAX----------------------------------------------------------------

jQuery(document).ready(function ($) {
    /**После полной загрузки веб-страницы нужно подготавить некоторые данные для правильной работы приложения*/

    //Токены безопасности csrf передаются в заголовках ajax-запроса (далее идет их подготовка)
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    //закрепление токена в заголовке ajax-запроса
    $(document).ajaxSend(function (event, xhr, settings) {
        xhr.setRequestHeader(header, token);
    });
    //обновление счетчика заблокированных для удаления строк (дисков)
    blockedRowsCount = $('table.inner_table tr[data-unavailable-to-delete = "true"]').length;
});

function createDiskViaAjax() {
    /**Добавление нового диска через ajax-запрос на сервер с последующим получением множества актуальных дисков пользователя,
     чтобы отобразить изменения БД в таблице*/
    var tableSpaceCtrlScope = angular.element($("#tableSpaceCtrl")).scope();           //получение доступа к angular функциям из разных контроллеров
    var controlPanelCtrlScope = angular.element($("#workspaceDivider")).scope();
    var newDiskDescription = textArea.val();
    var event = window.event;        //Для Firefox

    if ((newDiskDescription == "") || (newDiskDescription.length < 10)) {           //контроль ввода
        controlPanelCtrlScope.showAlert(event, "Описание должно составлять не менее 10 и не более 100 символов");   //вывод модального окна с соотвествующим сообщением
    } else {
        disableButton("#edit-create-commit-button", true);                //на время обработки запроса лучше заблокировать кнопку его отправки, чтобы избежать повторных вызовов
        sendAjaxRequest(newDiskDescription, "create/api/getCreateResult", function (data) {
            console.log("SUCCESS: ", data);
            updateCounterValue(data.result.length);   //подробности назначения предстваленных функций в их описании
            updateTableFromAjaxResponse(data);
            getRowsBlockingStatus();
            updateBlockedRowsCounter();
            cancelButtonPressed();
            tableBodyBlock.mCustomScrollbar("scrollTo", "bottom");    //прокрутка в конец таблицы, чтобы было видно добавленный диск
            disableButton("#edit-create-commit-button", false);       //не забыть разблокировать кнопку отправки запроса после того как получен ответ от сервера и произведены все нужные действия
            tableSpaceCtrlScope.showToast(data.msg);            //Вывод уведомления со статусом выполнения запроса
        })
    }
}

function editDiskViaAjax() {
    /**Редактирование описания диска через ajax-запрос на сервер ...*/
    var tableSpaceCtrlScope = angular.element($("#tableSpaceCtrl")).scope();
    var controlPanelCtrlScope = angular.element($("#workspaceDivider")).scope();
    var newDiskDescription = textArea.val();
    var event = window.event;        //Для Firefox

    if ((newDiskDescription == "") || (newDiskDescription.length < 10)) {         //контроль ввода
        controlPanelCtrlScope.showAlert(event, "Описание должно составлять не менее 10 и не более 100 символов");
    } else {
        var newDiskInfo = {};
        newDiskInfo["id"] = selectedRowsIds[0];                     //в режиме редактирования описания диска выбор строк "одиночный", поэтому выбирается только первый элемент массива
        newDiskInfo["newDiskDescription"] = newDiskDescription;
        disableButton("#edit-create-commit-button", true);
        sendAjaxRequest(newDiskInfo, "edit/api/getEditResult", function (data) {
            console.log("SUCCESS: ", data);
            updateTableFromAjaxResponse(data);
            getRowsBlockingStatus();
            cancelButtonPressed();
            disableButton("#edit-create-commit-button", false);
            tableSpaceCtrlScope.showToast(data.msg);
        });
    }
}

function deleteDiskViaAjax() {
    /**Удаление одного или более дисков через запрос на сервер посредством ajax ....*/
    var tableSpaceCtrlScope = angular.element($("#tableSpaceCtrl")).scope();            //получение доступ к дополнительным функциям из angular

    disableButton("#delete-commit-button", true);
    sendAjaxRequest(selectedRowsIds, "delete/api/getDeleteResult", function (data) {
        console.log("SUCCESS: ", data);
        updateCounterValue(data.result.length);
        updateTableFromAjaxResponse(data);
        getRowsBlockingStatus();
        updateBlockedRowsCounter();
        cancelButtonPressed();
        disableButton("#delete-commit-button", false);
        tableSpaceCtrlScope.showToast(data.msg);
    });
}

function borrowDiskViaAjax() {
    /**Заимствование одного или более дисков через запрос на сервер посредством ajax ....*/
    var tableSpaceCtrlScope = angular.element($("#tableSpaceCtrl")).scope();
    var controlPanelCtrlScope = angular.element($("#workspaceDivider")).scope();
    var event = window.event;        //Для Firefox

    disableButton("#borrow-commit-button", true);
    sendAjaxRequest(selectedRowsIds[0], "borrow/api/getBorrowResult", function (data) {
        console.log("SUCCESS: ", data);
        if (data["code"] != "300") {                    //если запрос обработан успешно (диск был взят)
            tableSpaceCtrlScope.showToast(data["msg"]);
        } else {                                            //Если превышен лимит дисков, то выводится сообщение об этом
            controlPanelCtrlScope.showAlert(event, data["msg"], "Исключение!");
        }
        updateTableFromAjaxResponse(data);     //Данные таблицы обновляются при любом раскладе, т.к. повлиять на них может любой из пользователей в любое время, т.е. записи постоянно меняются
        updateCounterValue(data["additionalValue"]);   //обновление счетчика взятых дисков
        cancelButtonPressed();
        disableButton("#borrow-commit-button", false);
    });
}

function giveBackDiskViaAjax() {
    /**Возврат одного или более дисков через запрос на сервер посредством ajax ....*/
    var tableSpaceCtrlScope = angular.element($("#tableSpaceCtrl")).scope();

    $("#give-back-commit-button").prop("disabled", true);     //блокировка кнопки отправки ajax-запроса пока отвечает сервер
    sendAjaxRequest(selectedRowsIds, "giveBack/api/getGiveBackResult", function (data) {
        console.log("SUCCESS: ", data);
        tableSpaceCtrlScope.showToast(data.msg);                 //вывод статуса запроса
        updateCounterValue(data.result.length);
        updateTableFromAjaxResponse(data);
        cancelButtonPressed();
        $("#give-back-commit-button").prop("disabled", false);    //пришел ответ от сервера, данные таблицы обновились и блокировка снята
    });
}

function sendAjaxRequest(dataToTransfer, url, successHandler) {
    /**Функция отправки запроса на сервер посредством ajax с данными в формате json*/
    $.ajax({
        type: "POST",                        //способ отправки HTTP - запроса (для ajax используют POST)
        contentType: "application/json",     //формат передаваемных данных (задан как json)
        url: url,                              //URL для обработки нужного запроса (в каждом случае свой)
        data: JSON.stringify(dataToTransfer),  //здесь данные для отправки на сервер переводятся в формат JSON
        dataType: 'json',                      //формат возвращаемых от сервера данных
        timeout: 100000,                       //время ожидания ответа от сервера
        success: successHandler,               //действия при успешной обработки запроса сервером (в виде функции)
        error: function (e) {                  //действия при ошибке во время обработки запроса сервером~~
            var tableSpaceCtrlScope = angular.element($("#tableSpaceCtrl")).scope();
            tableSpaceCtrlScope.showToast("Не удалось выполнить операцию! Обновите страницу (f5) и попробуйте снова!");
            console.log("ERROR: ", e);
        },
        complete: function () {             //дейтсвия по завершении запроса (после успешного выполнения или после ошибка по истечении времени ожидания). Выполнится при люом исходе!
            console.log("DONE");
            cancelButtonPressed();       //независимо от статуса обработки запроса нужно вернуть прогармму в изначальный режим (режим просмотра)
        }
    });
}

function updateTableFromAjaxResponse(ajaxRespond) {
    /**Обновление таблицы с данными о дисках на основе ответа от сервера*/
    var numberOfDisks = ajaxRespond.result.length;
    var updatedTableCode = "<table id='scrollTable' class='inner_table' data-select-mode='none'>";     //html-код обновленной таблицы формируется динамически далее по ходу функции и записывается в эту переменную

    ajaxRespond.result.sort(idComparator);  //записи сортируются по id дисков по возрастанию
    //каждая строка таблицы формируется по следующему циклическому алгоритму
    for (var i = 0; i < numberOfDisks; i++) {
        updatedTableCode += "<tr onclick='selectRowsHandler(this, event)' data-selected='false'>";
        updatedTableCode += "<td " + " data-item-id='" + ajaxRespond.result[i]["id"] + "'>" + "<div><md-checkbox role='checkbox' class='md-primary'></md-checkbox><span>" + (i + 1) + "</span></div></td>";
        updatedTableCode += "<td " + ">" + ajaxRespond.result[i]["name"] + "</td>";
        updatedTableCode += getUniqueTablePart(ajaxRespond.result[i]);
        updatedTableCode += "</tr>";
    }

    updatedTableCode += "</table>";
    $("table.inner_table").parent('div').html(updatedTableCode);               //обновление старой таблицы на новую, сформированную по ajax-ответу
    if (pageName != "diskLib") {
        $("table.inner_table").addClass("w2");
    }


    var tableSpaceCtrlScope = angular.element($("#tableSpaceCtrl")).scope();           //доступ к дополнительным angular-функциям
    var globalBodyScope = angular.element($("body")).scope();
    tableSpaceCtrlScope.compileFreshElements();                   //после обновления таблицы нужно заново скомпилировать angular-элементы (в данном случае чекбоксы)
    globalBodyScope.numberOfDisks = numberOfDisks;                //обновить общее количество записей таблицы (дисков)
}

function getUniqueTablePart(currentElement) {
    /**Функция позволяет получить часть таблицы(ввиде html-кода), уникальную для каждой конкретной страницы, для которой эта таблица формируется,
     * т.к. для каждой страницы нужны свои данные по дискам*/
    switch (pageName) {
        case "diskLib":
            var uniqueTablePart = "<td " + ">" + ((currentElement["given"]) ? "Занят" : "Свободен") + "</td>";
            break;
        case "borrowDisk":
        case "takenDisks":
            uniqueTablePart = "<td  data-disk-info=\"" + currentElement.host.lastname + " ";
            uniqueTablePart += currentElement.host.name + " ";
            uniqueTablePart += currentElement.host.patronym + " (ID: " + currentElement.host.id + ")\">";
            uniqueTablePart += currentElement.host.lastname + " ";
            uniqueTablePart += firstLetter(currentElement.host.name) + " ";
            uniqueTablePart += firstLetter(currentElement.host.patronym) + "</td>";
            break;
    }
    return uniqueTablePart;
}

function getRowsBlockingStatus() {
    /*Позволяет сделать "Занятые" диски неудаляемыми, через добавление специального атрибута к ним.
     * Т.к. по задумке пока диск находится в аренде его нельзя удалить из БД (каталога пользователя).*/
    var rows = $("table.inner_table tr");
    rows.each(function () {
        var givenStatus = $(this).find("td:last-child").html();
        if (givenStatus == "Занят") {
            $(this).attr("data-unavailable-to-delete", true);
        }
    })
    //обновление счетчика заблокированных для удаления строк (дисков)
    blockedRowsCount = $('table.inner_table tr[data-unavailable-to-delete = "true"]').length;
}