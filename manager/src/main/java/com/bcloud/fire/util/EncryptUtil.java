package com.bcloud.fire.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil {

    public static String md5(String src, String salt) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(src.getBytes());
            if (salt != null) {
                md5.update(salt.getBytes());
            }
            return ByteUtil.byteArrayToHexString(md5.digest());
        } catch (Exception e) {
            return "";
        }
    }

    public static String md5(String data) {
        try {
            return md5(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return md5(data.getBytes());
        }
    }

    public static String md5(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
        md.update(data);
        return ByteUtil.byteArrayToHexString(md.digest());
    }

    public static String SHA1(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte[] messageDigest = digest.digest();
            return ByteUtil.byteArrayToHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }
}
