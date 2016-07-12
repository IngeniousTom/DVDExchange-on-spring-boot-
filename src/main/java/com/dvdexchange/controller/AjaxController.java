package com.dvdexchange.controller;

import com.dvdexchange.model.*;
import com.dvdexchange.service.TakenItemService;
import com.dvdexchange.service.impl.DiskServiceImpl;
import com.dvdexchange.service.impl.TakenItemServiceImpl;
import com.dvdexchange.service.impl.UserServiceImpl;
import com.dvdexchange.utils.MiscUtility;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class AjaxController {
    UserServiceImpl userService = new UserServiceImpl();
    DiskServiceImpl diskService = new DiskServiceImpl();
    TakenItemService takenItemService = new TakenItemServiceImpl();

    @JsonView(Views.Disk.class)
    @RequestMapping("/create/api/getCreateResult")
    public AjaxResponseBody createNewDisk(@RequestBody String newDiskDescription) {
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();   /*Email текущего клиента нужен для выполнения различных подзапросов*/
        boolean isSuccessful = true;                    /*Статус успешности обработки запроса сервером (По-умолчанию - "успешен")*/
        String message = "Диск успешно добавлен!";      /*И сообщение о статусе обработки запроса изначально соответствует успешному сценарию*/

        if ((newDiskDescription != null) && (newDiskDescription != "")) {           //Дополнительная проверка на наличие корректного описания для нового диска
            int loggedUserId = userService.getUserIdByEmail(loggedUserEmail);       //Для некоторых подзапросов нужен именно id польщователя а не email
            newDiskDescription = MiscUtility.removeJSONExtraSymbols(newDiskDescription);      //Удаление лишних символов, появляющихся при передаче текста в формате json
            isSuccessful = diskService.addDisk(newDiskDescription, loggedUserId);        /*Добавление нового диска в БД и получение статуса этого подзапроса*/

            if (!isSuccessful) {
                message = "Не удалось добавить диск! Попробуйте снова!";           /*Если добавление не удалось то нужно чтоб и сообщение было соответствующее*/
            }
        }
        return getResultForDiskLib(loggedUserEmail, isSuccessful, message);         /*После выполнения запроса пользователя нужно вернуть актуальные данные о его дисках*/
    }

    @JsonView(Views.Disk.class)
    @RequestMapping("/edit/api/getEditResult")
    public AjaxResponseBody changeDiskDescription(@RequestBody HashMap<String, String> newDiskInfo) {
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isSuccessful = true;
        String message = "Название диска успешно изменено!";

        String newDiskDescription = newDiskInfo.get("newDiskDescription");
        int diskId = Integer.parseInt(newDiskInfo.get("id"));

        if ((newDiskDescription != null) && (newDiskDescription != "")) {
            isSuccessful = diskService.editDiskInformationById(diskId, newDiskDescription);
            if (!isSuccessful) {
                message = "Не удалось изменить описание диска. Пожалуйста, попробуйте снова!";
            }
        }
        return getResultForDiskLib(loggedUserEmail, isSuccessful, message);
    }

    @JsonView(Views.Disk.class)
    @RequestMapping("/delete/api/getDeleteResult")
    public AjaxResponseBody deleteDisks(@RequestBody List<Integer> idsOfDisksToDelete) {
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isSuccessful = true;
        String message = "Диски успешно удалены! (" + idsOfDisksToDelete.size() + "шт.)";           /*Важно укзаать сколько именно дисков пользователь удалял (для лучшего понимания)*/

        if ((idsOfDisksToDelete != null) && (idsOfDisksToDelete.size() > 0)) {

            int failCount = diskService.deleteDisksByIdList(idsOfDisksToDelete);
            int totalCount = idsOfDisksToDelete.size();

            if (failCount == 0) {
                isSuccessful = true;
            } else {
                isSuccessful = false;
                if (failCount == totalCount) {
                    message = "Не удалось удалить ни одного диска";
                } else {
                    message = "Часть дисков удалить не удалось ( " + failCount + " из " + totalCount + " )!";
                }
            }
        }

        return getResultForDiskLib(loggedUserEmail, isSuccessful, message);
    }

    @JsonView(Views.Disk.class)
    @RequestMapping("/borrow/api/getBorrowResult")
    public AjaxResponseBody borrowDisk(@RequestBody int idOfDiskToBorrow) {
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();    //по значению аналогично currentUserEmail в других методах
        String message = "Диск успешно взят напрокат!";
        boolean isSuccessful;     //Был ли диск позаимствован (true) или не удалось провести эту операцию (false)
        byte borrowStatus = 0;

        borrowStatus = takenItemService.addBorrowing(loggedUserEmail, idOfDiskToBorrow);

        if (borrowStatus == 0) {
            isSuccessful = true;
        } else {
            isSuccessful = false;
            if (borrowStatus == 1) {
                message = "Не удалось взять диск напрокат! Возможно он уже занят или возник сбой!";
            }
            if (borrowStatus == 2) {
                message = "Превышен лимит дисков! Чтобы взять новый диск - верните один из взятых ранее! (стр. \"Взятые диски\").";
            }
        }

        return getResultForBorrowDisk(loggedUserEmail, isSuccessful, message);
    }

    @JsonView(Views.Disk.class)
    @RequestMapping("/giveBack/api/getGiveBackResult")
    public AjaxResponseBody giveBackDisks(@RequestBody List<Integer> idsOfDisksToGiveBack) {
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();    //по значению аналогично currentUserEmail в других методах
        boolean isSuccessful = true;
        String message = "Диски успешно возвращены (" + idsOfDisksToGiveBack.size() + " шт.)!";

        //дополнительный контроль корректности значений (выбран ли хоть один диск), на тот случай, если контроль на клиентской стороне не реализован или в нем произошел сбой
        if (idsOfDisksToGiveBack.size() > 0) {
            int totalCount = idsOfDisksToGiveBack.size();
            int failCount = takenItemService.deleteBorrowing(idsOfDisksToGiveBack);
            ;

            if (failCount == 0) {
                isSuccessful = true;
            } else {
                isSuccessful = false;
                if (failCount == totalCount) {
                    message = "Не удалось вернуть ни одного диска";
                } else {
                    message = "Часть дисков вернуть не удалось (" + failCount + " из " + totalCount + ")!";
                }
            }
        }

        return getResultForGiveBack(loggedUserEmail, isSuccessful, message);
    }

    /***
     * @JsonView(Views.Disk.class)
     * @RequestMapping("/diskLib/api/getRefreshResult") public AjaxResponseBody giveBackDisks() {
     * String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();    //по значению аналогично currentUserEmail в других методах
     * boolean isSuccessful = true;
     * String message = "";
     * <p>
     * return getResultForGiveBack(loggedUserEmail, isSuccessful, message);
     * }
     */

    public AjaxResponseBody getResultForDiskLib(String loggedUserEmail, boolean isSuccessful, String message) {
        /*** Гарантированно возвращает валидный результат, т.е. множество дисков для отображения
         * в таблице плюс все данные, необходимые для этой конкретной страницы приложения*/
        AjaxResponseBody result = new AjaxResponseBody();
        List<MyDisk> resultSet = new ArrayList<MyDisk>();      //Инициализация делает множество валидным, т.е. не равным null
        List<MyDisk> testSet;

        testSet = userService.getUserDisksWithStatus(loggedUserEmail);

        addRespondStatus(testSet, resultSet, result, isSuccessful);
        result.setMsg(message);
        result.setResult(resultSet);

        return result;
    }

    public AjaxResponseBody getResultForBorrowDisk(String loggedUserEmail, boolean isSuccessful, String message) {
        /**Гарантированно возвращает валидный результат, т.е. множество дисков для отображения
         * в таблице плюс все данные, необходимые для этой конкретной страницы приложения*/
        AjaxResponseBody result = new AjaxResponseBody();
        List<Disk> resultSet = new ArrayList<Disk>();      //Инициализация делает множество валидным, т.е. не равным null
        List<Disk> testSet;
        List<Disk> borrowedDisks;

        testSet = diskService.getAllFreeDisksForCurrentUser(loggedUserEmail);
        borrowedDisks = diskService.getAllDisksTakenByLoggedUser(loggedUserEmail);

        int borrowedCounter = (borrowedDisks != null) ? borrowedDisks.size() : 0;

        addRespondStatus(testSet, resultSet, result, isSuccessful);
        result.setMsg(message);
        result.setResult(resultSet);
        result.setAdditionalValue(borrowedCounter);

        return result;
    }

    public AjaxResponseBody getResultForGiveBack(String loggedUserEmail, boolean isSuccessful, String message) {
        /**Гарантированно возвращает валидный результат, т.е. множество дисков для отображения
         * в таблице плюс все данные, необходимые для этой конкретной страницы приложения*/
        AjaxResponseBody result = new AjaxResponseBody();
        List<Disk> resultSet = new ArrayList<Disk>();      //Инициализация делает множество валидным, т.е. не равным null
        List<Disk> testSet;

        testSet = diskService.getAllDisksTakenByLoggedUser(loggedUserEmail);

        addRespondStatus(testSet, resultSet, result, isSuccessful);
        result.setMsg(message);
        result.setResult(resultSet);

        return result;
    }

    public void addRespondStatus(List testSet, List resultSet, AjaxResponseBody result, boolean isSuccessful) {

        if (testSet != null) {                 //Множество может быть пустым и нужно позаботиться о валидности результата при таком исходе,
            resultSet.addAll(testSet);         //поэтому используется testSet. Валидное пустое множество это не null, а множество(или список), в котором 0 элементов
        }

        if (isSuccessful) {

            if (resultSet.size() > 0) {
                result.setCode("200");
            } else {
                result.setCode("204");
            }
        } else {
            result.setCode("300");
        }
    }

}
