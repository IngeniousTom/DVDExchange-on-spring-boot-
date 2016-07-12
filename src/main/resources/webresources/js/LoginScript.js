//------------------------------------------Инициализация Angular Material----------------------------------------------

var app = angular.module('pageApp', ['ngMaterial']);         //внедрение ngMaterial к текущему angular-приложению

app.config(function ($mdThemingProvider) {
    // Расщирение темы индиго новым цветом через создание новой темы
    var newIndigoMap = $mdThemingProvider.extendPalette('indigo', {
        '500': '#5C6BC0'
    });
    // Регистрация новой цифровой палитры c названием newIndigo
    $mdThemingProvider.definePalette('newIndigo', newIndigoMap);
    // Присвоение новой палитры в роли основной
    $mdThemingProvider.theme('default')
        .primaryPalette('newIndigo');
    $mdThemingProvider.theme('default')
        .primaryPalette('newIndigo', {
            'hue-3': '500'
        })
        .accentPalette('orange');    //акцентная палитра - оранжевая
});

//------------------------------------------------Полезные функции---------------------------------------------------------

function formSubmit() {
    //Отправка данных на сервер в запросе на авторизацию
    $("#loginForm").submit();
}

function submitOnEnter(event) {
    /**При нажатии клавиши Enter запускает отправку данный формы на сервер 
     * (т.е. запрос на авторизацию аналогично нажатию на кнопку "Вход")*/
    if (event.keyCode == 13) {
        formSubmit();
    }
}