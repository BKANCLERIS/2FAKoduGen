package com.example.a2fa;

import android.widget.TextView;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import de.taimos.totp.TOTP;

public class elementas {


    private String totp;
    private String issuer;
    private String onetimepassword;
    public MainActivity mainActivity;
    String code = "";
    String lastCode = "";

    public elementas(String totp, String issuer, MainActivity ma) {
        this.mainActivity = ma;
        this.totp = totp;
        this.issuer = issuer;
        this.onetimepassword = "";
        autoupdateOTP();
    }

    public void autoupdateOTP() {
        Runnable runner = new Runnable() {
            public void run() {
                try {
                    GetTOTS();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        Thread t = new Thread(runner);
        t.start();
    }


    public void GetTOTS() {
        while (true) {
            code = getOTPCode(totp);
            if (!code.equals(lastCode)) {
                onetimepassword = code;
                new Thread() {
                    public void run() {
                        try {
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() { mainActivity.atnaujintiduomenis(); }});
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                }.start();
            }
            lastCode = code;
            try { Thread.sleep(500); }
            catch (InterruptedException e) {
            }
        }
    }
    public String getOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    public String getissuer() {
        return issuer;
    }

    public String getOnetimepassword() {
        return onetimepassword;
    }

    public String getTotp() {
        return totp;
    }


    public void setPavadinimas(String pavadinimas) {
        issuer = pavadinimas;
    }


}

