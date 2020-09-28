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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.frostytiger.*;

@WebServlet("/service")
public class ServiceServlet extends HttpServlet {

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

        System.out.println("DEBUG doPost()");

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

        System.out.println("DEBUG Operation is: " + op);

        try {

            //
            // Use reflection to find the method name to invoke
            // and invoke it.
            //
            // Method signature should be:
            // public JsonValue getXxx(HttpServletRequest request);
            //
            Class<?> c = HttpServletRequest.class;
            Method method = this.getClass().getMethod(op, c);
            JsonValue ret = (JsonValue)method.invoke(this, request);

            //
            // Handle methods which return nothing...
            //
            if(ret == null) {
                ret = JsonObject.NULL;
            }

            JsonObject object = Json.createObjectBuilder()
                .add("retError",    JsonObject.FALSE)
                .add("retMessage",  JsonObject.NULL)
                .add("retObject",   ret)
                .build();

            jsonWriter.writeObject(object);
            jsonWriter.close();
            writer.close();
            response.getWriter().write(writer.toString());

            return;

        } catch (NoSuchMethodException nsme) {
            nsme.printStackTrace();
            System.out.println("Error: " + nsme.getMessage());
        } catch (SecurityException se) {
            se.printStackTrace();
            System.out.println("Error: " + se.getMessage());
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            System.out.println("Error: " + iae.getMessage());
        } catch (InvocationTargetException ite) {

            Throwable cause = ite.getCause();
            System.out.println("ite cause: " + cause);

            if(cause instanceof ServiceException) {
               
                // System.out.println("Found ServiceException: " + cause);

                ServiceException serviceException = (ServiceException)cause;

                JsonObject object = Json.createObjectBuilder()
                    .add("retError",    JsonObject.TRUE)
                    .add("retMessage",  serviceException.getMessage())
                    .add("retObject",   JsonObject.NULL)
                    .build();

                jsonWriter.writeObject(object);
                jsonWriter.close();
                writer.close();
                response.getWriter().write(writer.toString());
    
                return;

            } else {
                ite.printStackTrace();
            }

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


    public JsonValue addStructure(HttpServletRequest request) 
                                        throws ServiceException {

        int structTypeId    = Integer.parseInt(request.getParameter("typeId"));
        int locationId      = Integer.parseInt(request.getParameter("locationId"));

        MapManager mapMan = MapManager.getInstance();

        mapMan.addStructure(structTypeId, locationId);

        return null;
    }


    public JsonValue getStructureTypes(HttpServletRequest request) {

        StructureType[] types = 
                            MapManager.getInstance().getStructureTypes();
 
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(StructureType t: types) {
            JsonObject object = t.toJSON();
            arrayBuilder.add(object);
        }
        JsonArray array = arrayBuilder.build();

        return array;
    }

    public JsonValue getResourceTypes(HttpServletRequest request) {

        ResourceType[] resourceTypes = 
                            MapManager.getInstance().getResourceTypes();
 
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(ResourceType t: resourceTypes) {
            JsonObject object = t.toJSON();
            arrayBuilder.add(object);
        }
        JsonArray array = arrayBuilder.build();

        return array;
    }

    public JsonValue getTileTypes(HttpServletRequest request) {

        TileType[] tileTypes = 
                            MapManager.getInstance().getTileTypes();
 
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(TileType t: tileTypes) {
            JsonObject object = t.toJSON();
            arrayBuilder.add(object);
        }
        JsonArray array = arrayBuilder.build();

        return array;
    }

    public JsonValue getMapSection(HttpServletRequest request) {

        int x       = Integer.parseInt(request.getParameter("x"));
        int y       = Integer.parseInt(request.getParameter("y"));
        int width   = Integer.parseInt(request.getParameter("width"));
        int height  = Integer.parseInt(request.getParameter("height"));

        Tile[][] tiles = 
                MapManager.getInstance().getMapSection(x, y, width, height);
 
        JsonArrayBuilder verticalArrayBuilder = Json.createArrayBuilder();
        for(int i = 0; i < tiles.length; i++) {
            JsonArrayBuilder horizontalArrayBuilder = Json.createArrayBuilder();
            for(int j = 0; j < tiles[i].length; j++) {
                JsonObject object = tiles[i][j].toJSON();
                horizontalArrayBuilder.add(object);
            }
            verticalArrayBuilder.add(horizontalArrayBuilder);
        }

        JsonArray array = verticalArrayBuilder.build();

        return array;

    }


}
