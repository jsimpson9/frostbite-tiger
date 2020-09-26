package com.frostytiger.servlet;
 
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import javax.json.*;

import com.frostytiger.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(  HttpServletRequest request, 
                            HttpServletResponse response) 
                                throws ServletException, IOException {
 
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        response.setContentType("text/plain");

        //
        // Write json to this object
        //
        StringWriter    writer      = new StringWriter();
        JsonWriter      jsonWriter  = Json.createWriter(writer);

        if( username == null || "".equals(username) || 
            password == null || "".equals(password)     )   {

            //
            // Error
            //
            JsonObject object = Json.createObjectBuilder()
                .add("retError",    JsonObject.TRUE)
                .add("retMessage",  "Empty username or password specified")
                .add("retObject",   JsonObject.NULL)
                .build();

            jsonWriter.writeObject(object);
            jsonWriter.close();
            writer.close();
            response.getWriter().write(writer.toString());
            return;
        }
        
        //
        // Check database here
        //
        User user = UserManager.getUser(username);

        if(user != null) {

            byte[] salt     = user.getSalt();
            String encPass  = EncryptUtil.getSHA1Password(password, salt);

            if(encPass.equals(user.getPassword())) {

                //
                // Success
                //
                JsonObject object = Json.createObjectBuilder()
                    .add("retError",    JsonObject.FALSE)
                    .add("retMessage",  JsonObject.NULL)
                    .add("retObject",   user.toJSON() )
                    .build();
    
                request.getSession().setAttribute("user", object);
                jsonWriter.writeObject(object);
                jsonWriter.close();
                writer.close();
                response.getWriter().write(writer.toString());
                return;
            } 
        }

        //
        // Error, incorrect user or pass.
        //

        JsonObject object = Json.createObjectBuilder()
                .add("retError",    JsonObject.TRUE)
                .add("retMessage",  "Incorrect username or password")
                .add("retObject",   JsonObject.NULL)
                .build();

        jsonWriter.writeObject(object);

        jsonWriter.close();
        writer.close();
        response.getWriter().write(writer.toString());
    }
 
}
