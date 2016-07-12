package com.dvdexchange.controller;


import com.dvdexchange.model.Disk;
import com.dvdexchange.model.MyDisk;
import com.dvdexchange.model.Takenitem;
import com.dvdexchange.service.impl.DiskServiceImpl;
import com.dvdexchange.service.impl.TakenItemServiceImpl;
import com.dvdexchange.service.impl.UserServiceImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**Главный контроллер, отвечающий за обработку запросов пользователя по доступу
 **на разные адреса веб-сервиса**/

@Controller
public class MainController {
    private UserServiceImpl userService = new UserServiceImpl();
    private DiskServiceImpl diskService = new DiskServiceImpl();
    private TakenItemServiceImpl takenItemService = new TakenItemServiceImpl();

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "login";                                 /*Первая страница, которую должен видеть пользователь - это страница авторизации и она же страница приветствия*/
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error,               /*данные о предыдущей неудачной авторизации из предыдущей попытки*/
                              @RequestParam(value = "logout", required = false) String logout, HttpServletRequest request) {   /*или об успешном выходе из аккаунта с данными о запросе на сервер*/

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));         /*Получение сообщения об ошибке и запись в модель для отображения пользователю*/
        }

        if (logout != null) {
            model.addObject("msg", "Вы успешно вышли из учетной записи.");                                /*Запись данных об успешном выходе из аккаунта в модель для отображения пользователю*/
        }
        model.setViewName("login");

        return model;
    }

    @RequestMapping(value = "/diskLib")
    public ModelAndView diskLib() {
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MyDisk> diskList = new ArrayList<MyDisk>();
        List<MyDisk> testDiskList;                     /*Для предварительной проверки множества на валидность*/
        int givenDisksCount = 0;                       /*Количество взятых у пользователя дисков нужно для того, чтобы корректно блокировать кнопку "удалить диск",
                                                       **если все диски пользователя отданы*/

        testDiskList = userService.getUserDisksWithStatus(loggedUserEmail);


        if (testDiskList != null) {             //Множество может быть пустым и нужно позаботиться о валидности результата при таком исходе,
            diskList = testDiskList;            //поэтому используется testDiskList. Валидное пустое множество это не null, а множество( или список), в котором 0 элементов

            //Сортировка дисков по id по возрастанию
            Collections.sort(diskList, new Comparator<MyDisk>() {
                public int compare(MyDisk o1, MyDisk o2) {
                    return o1.getId() - o2.getId();
                }
            });
        }

        for (MyDisk disk : diskList) {
            if (disk.isGiven()) {
                givenDisksCount++;
            }
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("CurrentUserEmail", loggedUserEmail);
        modelAndView.addObject("AllDisksOfTheUser", diskList);
        modelAndView.addObject("GivenDisksCount", givenDisksCount);
        modelAndView.setViewName("diskLib");             /*Указываем для какой страницы предназначены данные (модель). Аналогично в остальных методах*/
        return modelAndView;
    }

    @RequestMapping(value = "/borrowDisk")
    public ModelAndView borrowDisk() {
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Disk> allFreeDisks = new ArrayList<Disk>();
        List<Disk> testDisks;

        testDisks = diskService.getAllFreeDisksForCurrentUser(loggedUserEmail);

        if (testDisks != null) {             //Множество может быть пустым и нужно позаботиться о валидности результата при таком исходе,
            allFreeDisks = testDisks;        //поэтому используется testDisks. Валидное пустое множество это не null, а множество( или список), в котором 0 элементов

            //Сортировка дисков по id по возрастанию
            Collections.sort(allFreeDisks, new Comparator<Disk>() {
                public int compare(Disk o1, Disk o2) {
                    return o1.getId() - o2.getId();
                }
            });
        }

        int borrowCount = 0;                                                      /*Количество взятых пользователем дисков*/
        testDisks = diskService.getAllDisksTakenByLoggedUser(loggedUserEmail);

        if (testDisks != null) {
            borrowCount = testDisks.size();
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("CurrentUserEmail", loggedUserEmail);
        modelAndView.addObject("AllFreeDisks", allFreeDisks);                            /*Множество всех свободных для аренды дисков для текущего пользователя*/
        modelAndView.addObject("BorrowCount", borrowCount);
        modelAndView.addObject("BorrowLimit", TakenItemServiceImpl.maxBorrowedDiskCount);         /*Существует лимит на количество дисков, которые может взять каждый пользователь в аренду*/
        modelAndView.setViewName("borrowDisk");

        return modelAndView;
    }

    @RequestMapping(value = "/takenDisks")
    public ModelAndView takenDisks() {
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Disk> takenDisks = new ArrayList<Disk>();
        List<Disk> testTakenDisks;

        testTakenDisks = diskService.getAllDisksTakenByLoggedUser(loggedUserEmail);

        if (testTakenDisks != null) {             //Множество может быть пустым и нужно позаботиться о валидности результата при таком исходе,
            takenDisks = testTakenDisks;          //поэтому используется testFreeDisks. Валидное пустое множество это не null, а множество( или список), в котором 0 элементов

            //Сортировка дисков по id по возрастанию
            Collections.sort(takenDisks, new Comparator<Disk>() {
                public int compare(Disk o1, Disk o2) {
                    return o1.getId() - o2.getId();
                }
            });
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("CurrentUserEmail", loggedUserEmail);
        modelAndView.addObject("TakenDisks", takenDisks);                        /*Кол-во дисков, отданных пользователем в аренду*/
        modelAndView.addObject("BorrowCount", takenDisks.size());
        modelAndView.addObject("BorrowLimit", TakenItemServiceImpl.maxBorrowedDiskCount);          /*Существует лимит на количество дисков, которые может взять каждый пользователь в аренду*/

        modelAndView.setViewName("takenDisks");
        return modelAndView;
    }

    @RequestMapping(value = "/givenDisks")
    public ModelAndView givenDisks() {
        String loggedUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Takenitem> givenDisks = new ArrayList<Takenitem>();
        List<Takenitem> testGivenDisks;
        testGivenDisks = takenItemService.getAllDisksGivenByLoggedUser(loggedUserEmail);

        if (testGivenDisks != null) {             //Множество может быть пустым и нужно позаботиться о валидности результата при таком исходе,
            givenDisks = testGivenDisks;          //поэтому используется testFreeDisks. Валидное пустое множество это не null, а множество( или список), в котором 0 элементов

            //Сортировка дисков по id по возрастанию
            Collections.sort(givenDisks, new Comparator<Takenitem>() {
                public int compare(Takenitem o1, Takenitem o2) {
                    return o1.getIddisk() - o2.getIddisk();
                }
            });
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("CurrentUserEmail", loggedUserEmail);
        modelAndView.addObject("GivenDisks", givenDisks);                    /*Множество отданных дисков*/
        modelAndView.addObject("GivenCount", givenDisks.size());             /*Кол-во отданных дисков*/
        modelAndView.addObject("TotalCount", userService.getUserDisksWithStatus(loggedUserEmail).size());         /*Общее кол-во дисков собственных пользователя*/

        modelAndView.setViewName("givenDisks");
        return modelAndView;
    }

    /*Получаем исключение с необходимым текстом*/
    private String getErrorMessage(HttpServletRequest request, String key) {

        Exception exception = (Exception) request.getSession().getAttribute(key);

        String error = "";
        /*Получение понятного для пользователя текста ошибки в зависимостит от причин отказа в доступе*/
        if (exception instanceof BadCredentialsException) {           /*Если неверно введен пароль, а логин верный и аккаунт исправен и не заблокирован*/
            error = "Неверные \"Логин\" и/или \"Пароль\"!";         /*Но для безопасности все-равно нельзя указывать, что именно введено неправильно*/
        } else if (exception instanceof LockedException) {            /*Если аккаунт заблокирован*/
            error = exception.getMessage();
        } else {
            error =  "Неверные \"Логин\" и/или \"Пароль\"!";          /*если неверны логин и пароль или только логин*/
        }

        return error;
    }

}
