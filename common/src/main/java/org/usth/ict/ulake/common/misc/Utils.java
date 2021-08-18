package org.usth.ict.ulake.common.misc;

import org.wildfly.security.password.Password;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.WildFlyElytronPasswordProvider;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

public class Utils {
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    public static boolean isEmpty(String str) {
        if (str != null && !str.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public static boolean verifyPassword(String bCryptPasswordHash, String passwordToVerify) throws Exception {
        WildFlyElytronPasswordProvider provider = new WildFlyElytronPasswordProvider();
        PasswordFactory passwordFactory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT, provider);
        Password userPasswordDecoded = ModularCrypt.decode(bCryptPasswordHash);
        Password userPasswordRestored = passwordFactory.translate(userPasswordDecoded);
        return passwordFactory.verify(userPasswordRestored, passwordToVerify.toCharArray());

    }
}
