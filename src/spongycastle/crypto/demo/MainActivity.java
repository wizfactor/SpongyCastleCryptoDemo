package spongycastle.crypto.demo;

import java.math.BigInteger;
import java.security.MessageDigest;

import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.paddings.PKCS7Padding;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;

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
	BlockCipher AESCipher = new AESEngine(); 					//Create a Cipher whose algorithm is AES
	/*This variable will perform the encryption/decryption operations*/
	PaddedBufferedBlockCipher pbbc = new PaddedBufferedBlockCipher(AESCipher,new PKCS7Padding()); 

	KeyParameter key;
	
	String inputString;
	byte[] inputBytes;
	byte[] keyBytes;
	byte[] storedBytes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
			EditText keyField = (EditText) findViewById(R.id.keyField);
			
			inputString = inputField.getText().toString(); 	//Convert input string to byte array
			keyBytes = keyField.getText().toString().getBytes("UTF-8");	   	//Perform similar operation to key input
			
			/*Create a 256-bit AES key by using a SHA-256 hash on the input key string*/			
			MessageDigest md = MessageDigest.getInstance("SHA-256");
		    md.update(keyBytes); 
		    //BigInteger bigInt = new BigInteger(1, md.digest());
		    byte[] digest = md.digest();
		    
		    System.out.println(new BigInteger(1,digest).toString(16));
		    
		    key = new KeyParameter(digest);
		    
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
		    	System.out.println(new String(outputBytes,"UTF-8"));
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
    	
        pbbc.init(encrypt, key);
 
        byte[] output = new byte[pbbc.getOutputSize(input.length)];
        int bytesWrittenOut = pbbc.processBytes(
            input, 0, input.length, output, 0);
 
        pbbc.doFinal(output, bytesWrittenOut);
 
        return output;
 
    }

}
