package com.frostytiger.servlet;
 
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.*;
import javax.json.*;

import com.frostytiger.*;

@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {

    //
    // Debug
    //
    protected void doGet(   HttpServletRequest request, 
                            HttpServletResponse response) 
                                throws ServletException, IOException {
        doPost(request, response);                                
    }

    protected void doPost(  HttpServletRequest request, 
                            HttpServletResponse response) 
                                throws ServletException, IOException {

        //
        // Write json to this object
        //
        StringWriter    writer      = new StringWriter();
        JsonWriter      jsonWriter  = Json.createWriter(writer);

        response.setContentType("text/plain");

        String op = request.getParameter("operation");

        if(op == null) {

            JsonObject object = Json.createObjectBuilder()
                .add("retError",    JsonObject.TRUE)
                .add("retMessage",  "No operation specified.")
                .add("retObject",   JsonObject.NULL)
                .build();

            jsonWriter.writeObject(object);

            jsonWriter.close();
            writer.close();
            response.getWriter().write(writer.toString());

            return;
        }

        if(op.equals("getuser")) {

            String username = request.getParameter("username");
            User user = UserManager.getUser(username);

            JsonObject object = Json.createObjectBuilder()
                .add("retError",    JsonObject.FALSE)
                .add("retMessage",  JsonObject.NULL)
                .add("retObject",   user.toJSON() )
                .build();

            jsonWriter.writeObject(object);
            jsonWriter.close();
            writer.close();
            response.getWriter().write(writer.toString());
            return;
        }

        if(op.equals("adduser")) {

            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String email    = request.getParameter("email");

            User user = UserManager.getUser(username);

            if(user != null) {

                JsonObject object = Json.createObjectBuilder()
                    .add("retError",    JsonObject.TRUE)
                    .add("retMessage",  "User already exists.")
                    .add("retObject",   JsonObject.NULL)
                    .build();

                jsonWriter.writeObject(object);
                jsonWriter.close();
                writer.close();
                response.getWriter().write(writer.toString());
                return;
            }

            byte[] salt     = EncryptUtil.getSalt();
            String encPass  = EncryptUtil.getSHA1Password(password, salt);

            user = new User(username, encPass, salt, email, new Date());

            UserManager.addUser(user);

            JsonObject object = Json.createObjectBuilder()
                .add("retError",    JsonObject.FALSE)
                .add("retMessage",  "User created")
                .add("retObject",   JsonObject.NULL)
                .build();

            jsonWriter.writeObject(object);
            jsonWriter.close();
            writer.close();
            response.getWriter().write(writer.toString());
            return;
        }


        JsonObject object = Json.createObjectBuilder()
                .add("retError",    JsonObject.TRUE)
                .add("retMessage",  "Invalid operation specified: " + op)
                .add("retObject",   JsonObject.NULL)
                .build();

        jsonWriter.writeObject(object);
        jsonWriter.close();
        writer.close();
        response.getWriter().write(writer.toString());
    }
 
}
