package com.frostytiger.servlet;
 
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import javax.json.*;

import com.frostytiger.*;

@WebServlet("/SessionServlet")
public class SessionServlet extends HttpServlet {

    protected void doGet(   HttpServletRequest request, 
                            HttpServletResponse response) 
                                throws ServletException, IOException {
        
        String action = request.getParameter("action");

        if(action.equals("logout")) {
            
            request.getSession().setAttribute("user", null);

            // RequestDispatcher rd = request.getRequestDispatcher("index.html");
            // rd.forward(request, response);
            // return;

            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }
    }

    protected void doPost(  HttpServletRequest request, 
                            HttpServletResponse response) 
                                throws ServletException, IOException {
 
        //
        // Check session
        //
        JsonObject object = (JsonObject)request.getSession().getAttribute("user");

        response.setContentType("text/plain");

        //
        // Write json to this object
        //
        StringWriter    writer      = new StringWriter();
        JsonWriter      jsonWriter  = Json.createWriter(writer);

        //
        // Check session here
        //
        
        if(object != null) {
            jsonWriter.writeObject(object);
            
        } else {

            //
            // Error, no session
            //

            object = Json.createObjectBuilder()
                .add("retError",    JsonObject.TRUE)
                .add("retMessage",  "No active session")
                .add("retObject",   JsonObject.NULL)
                .build();

            jsonWriter.writeObject(object);

        }

        jsonWriter.close();
        writer.close();
        response.getWriter().write(writer.toString());
    }
 
}
