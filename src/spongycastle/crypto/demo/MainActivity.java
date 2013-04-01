package spongycastle.crypto.demo;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PKCS7Padding;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	/*This demo will use AES as its encryption method*/
	
	public static final int BLOCKSIZE = 16; //AES uses a block length of 16 bytes 
	 
	//This variable will perform the encryption/decryption operations
	PaddedBufferedBlockCipher pbbc; 

	ParametersWithIV paramIV = null;
	
	SecureRandom sr;
	
	String inputString;
	byte[] inputBytes;
	byte[] storedBytes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* Create a new Padded Buffered Block Cipher that is initialized with the following technologies:
		 * - AES Symmetric Cipher
		 * - PKCS7 Padding
		 * - CBC (Cipher-block chaining) mode of operation
		 * */
		pbbc = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), new PKCS7Padding());
		
		sr = new SecureRandom();
		
		paramIV = generateParameter();
		
		Button encryptButton = (Button) findViewById(R.id.encryptButton);       
        encryptButton.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		cipher(true);
        	}
        });
        
        Button decryptButton = (Button) findViewById(R.id.decryptButton);       
        decryptButton.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		cipher(false);
        	}
        });
        
        
        Button generateButton = (Button) findViewById(R.id.generateButton);
        generateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				paramIV = generateParameter();				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void cipher(boolean encrypt) {  //True: encrypt; False: decrypt
		try {
			System.out.println("Initializing Cipher");
			
			EditText inputField = (EditText) findViewById(R.id.inputField);			
			inputString = inputField.getText().toString(); 	//Convert input string to byte array
		    
		    byte[] inputBytes = inputString.getBytes("UTF-8");
		    
		    byte[] outputBytes;
		    
		    if(encrypt) {
		    	outputBytes = encrypt(inputBytes);
		    } else {
		    	outputBytes = decrypt(storedBytes);
		    }
		    
		    storedBytes = new byte[outputBytes.length];
		    System.arraycopy(outputBytes, 0, storedBytes, 0, outputBytes.length);
		   
		    TextView outputView = (TextView) findViewById(R.id.ouputLabel);
		    
		    String outputString;
		    
		    if(encrypt) {
		    	outputString = new BigInteger(1,outputBytes).toString(16);
		    } else {
		    	outputString = new String(outputBytes,"UTF-8");
		    }
		    outputView.setText(outputString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public byte[] encrypt(byte[] input)
            throws DataLengthException, InvalidCipherTextException {
        return processing(input, true);
    }
 
    public byte[] decrypt(byte[] input)
            throws DataLengthException, InvalidCipherTextException {
        return processing(input, false);
    }
 
    private byte[] processing(byte[] input, boolean encrypt)
            throws DataLengthException, InvalidCipherTextException {
    	
    	//Initialize the Padded Buffered Block Cipher
        pbbc.init(encrypt, paramIV);
 
        byte[] output = new byte[pbbc.getOutputSize(input.length)];
        int bytesWrittenOut = pbbc.processBytes(
            input, 0, input.length, output, 0);
 
        pbbc.doFinal(output, bytesWrittenOut);
 
        return output;
    }

    public ParametersWithIV generateParameter() {
    	try {
    		ParametersWithIV newParamIV = null;
    		byte[] IV = new byte[BLOCKSIZE];
        	
    		//256-bit key is randomly generated
        	KeyGenerator kg = KeyGenerator.getInstance("AES");
        	kg.init(256);
        	SecretKey sk = kg.generateKey();
        	
        	sr.nextBytes(IV); //Initialization Vector is randomly generated
        	
        	newParamIV = new ParametersWithIV(new KeyParameter(sk.getEncoded()), IV);
        	
        	System.out.println("New Cipher Paramters Generated");
        	
        	return newParamIV;
    	} catch (Exception e) {
    		e.printStackTrace();
    		
    		return paramIV; //If parameter generation fails, just return the unaltered parameter
    	}
    }
}
