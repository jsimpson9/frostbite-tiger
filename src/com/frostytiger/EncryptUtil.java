package com.frostytiger;
 
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
 
public class EncryptUtil {
    
    //
    // Debug
    //
    public static void main(String[] args) throws NoSuchAlgorithmException {

        String passwordToHash = args[0];
        byte[] salt = getSalt();
        
        System.out.println("Salt: " + new String(salt));

        String securePassword = getSHA1Password(passwordToHash, salt);
        System.out.println("Pass: " + securePassword);
         
    }

    //
    // Create the digest
    //
    public static String getSHA1Password(String passwordToHash, byte[] salt) {

        String generatedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }
     
    //
    // Get a new salt
    //
    public static byte[] getSalt() {

        byte[] salt = new byte[16];

        try {

            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.nextBytes(salt);

        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Error creating salt: " + ex);
            ex.printStackTrace();
        }
        
        return salt;
    }
}
