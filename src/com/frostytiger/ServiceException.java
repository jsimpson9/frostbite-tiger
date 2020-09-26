package com.frostytiger;
 
import java.io.*;
import java.util.*;
import java.sql.*;

public class ServiceException extends Exception {

    private String      _message;
    private Exception   _cause;

    public ServiceException(String message, Exception cause) {
        _message    = message;
        _cause      = cause;
    }

    public String       getMessage()    { return _message;  }

    public Exception    getCuase()      { return _cause;    }

}
