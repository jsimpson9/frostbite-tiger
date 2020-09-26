package com.frostytiger.servlet;
 
import java.io.*;
import java.util.*;
import java.nio.file.Paths;
import java.lang.reflect.*;
import javax.json.*;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.frostytiger.*;

@WebServlet("/upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {


    protected void doPost(  HttpServletRequest request, 
                            HttpServletResponse response) 
                                throws ServletException, IOException {

        try {

            response.setContentType("text/plain");

            System.out.println("UploadServlet doPost()");

            String name = request.getParameter("typeName");

            Part filePart = request.getPart("typeImage"); 
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            InputStream fileContent = filePart.getInputStream();

            System.out.println("addGameObjectType() name: "     + name);
            System.out.println("addGameObjectType() fileName: " + fileName);

            JsonObject object = Json.createObjectBuilder()
                    .add("retError",    JsonObject.FALSE)
                    .add("retMessage",  "File uploaded.")
                    .add("retObject",   JsonObject.NULL)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("addGameObjectType() caught: " + e);
        }


    }

}
