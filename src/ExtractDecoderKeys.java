import edu.cmu.iec62055meter.Main;
import edu.cmu.iec62055meter.domain.TokenParameters;
import edu.cmu.iec62055meter.domain.keys.dk.DecoderKey;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * This class is used to generate a set of decoder keys given
 * a 20 digit token and a set of parameters. It then compares the
 * generated values to determine if a valid set of
 * token values
 * Created by rmbitiru on 1/21/16.
 */
public class ExtractDecoderKeys {

    public static void main(String[] args) {
        final String token = "" ;
        final TokenParameters tokenParameters = new TokenParameters() ;
        ExtractDecoderKeys extractDecoderKeys = new ExtractDecoderKeys() ;
        ArrayList<DecoderKey> decoderKeys = extractDecoderKeys.determineValidDecoderKeys(token, tokenParameters) ;
    }

    public ArrayList<DecoderKey>  determineValidDecoderKeys(String _20DigitToken, TokenParameters tokenParameters) {

        boolean validDecoderKeyFound = false ;
        ArrayList<DecoderKey> validDecoderKeys = new ArrayList<>() ;
        BigInteger desHex = new BigInteger("0", 16) ;
        SecretKey secretKey = null ;
        DecoderKey decoderKey = new DecoderKey() ;

        while (desHex.compareTo(new BigInteger("FFFFFFFFFFFFFFFF")) == -1) {

            // generate the current decoder key
            String HEX = desHex.toString(16) ;
            byte[] currDecoderKeyBytes = hexStringToByteArray(HEX) ;
            secretKey = new SecretKeySpec(currDecoderKeyBytes, "DES") ;
            decoderKey.setSecretKey(secretKey) ;

            // use decoder key to generate token parameters
            Main m = new Main(decoderKey, _20DigitToken) ;
            m.process(new String[]{});
            TokenParameters generatedTokenParameters = m.getDecodedTokenParameters() ;

            // compare generated token parameters with real token
            // parameters and add if correct
            if(compare(generatedTokenParameters, tokenParameters)) {
                validDecoderKeys.add(decoderKey) ;
            }
        }
        return validDecoderKeys ;
    }

    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private boolean compare (TokenParameters tokenParameter1, TokenParameters tokenParameters2) {
        return tokenParameter1.toString().trim().equals(tokenParameters2.toString().trim()) ;
    }
}
