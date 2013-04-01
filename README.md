SpongyCastleCryptoDemo
======================

A simple Android implementation of an AES encryption/decryption using SpongyCastle's API. This app implements the following technologies:

1. AES symmetric encryption with CBC mode of operation
2. 256-bit keys are randomly generated
3. PKCS7 Padding

Please note some current caveats with the application:

1. The app currently cannot accept ciphertext as input. To skirt around this issue, I made the app store the latest ciphertext in a variable, so if you want to decrypt the ciphertext, just press the "Decrypt" button, and the app will decrypt the recently stored ciphertext. The issue stems from the reality that once the ciphertext is converted from a byte array into its hexadecimal string form (what is displayed on-screen), it is nearly impossible to perform the reverse operation without accidentally modifying the value of the ciphertext. Until I can find a way around this problem, you will have to settle for the compromised solution.
	
2. There is currently no means of key validation. I will probably use a hash-check solution for this feature once I come around to implementing it.

3. There is currently no means of validating data that was decrypted with an incorrect key. What happens instead is that a decryption using an incorrect key will return an InvalidCipherTextException that is caught by the application; the app will continue to run normally without crashing. I am pretty sure there is a more elegant solution to handling this problem (most likely related to #2), but that will have to come in a future version.

4. Key is currently not locally stored. Currently, the key and the IV are generated together after every button press. I will make sure to separate these two functions in a future update.

This application was tested on the following devices:

1. An Android Virtual Device running Android 4.2.2
2. A GSM Samsung Galaxy Nexus running Android 4.2.2

*This application requires a physical or virtual devices running at least Android 4.0.3.