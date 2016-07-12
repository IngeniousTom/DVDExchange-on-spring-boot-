package com.dvdexchange.utils;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import java.io.*;

/**Класс описывает jsp-тег, который используется для создания сокращений имен/названий, т.е. в инициалах*/
public class FirstLetterTag extends SimpleTagSupport{
    private String message;
    StringWriter sw = new StringWriter();

    public void setMessage(String msg) {
        this.message = msg;
    }

    public void doTag()
            throws JspException, IOException
    {
        if (message != null) {
          /* Используется сообщение из атрибута */
            JspWriter out = getJspContext().getOut();
            out.println(message.toUpperCase().charAt(0) + ".");
        }
        else {
          /* Используется сообщение из тела тега */
            getJspBody().invoke(sw);
            getJspContext().getOut().println(sw.toString().toUpperCase().charAt(0) + ".");
        }
    }
}
