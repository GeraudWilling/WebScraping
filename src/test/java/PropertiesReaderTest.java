import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import com.geraudwilling.webscraping.util.PropertiesReader;

public class PropertiesReaderTest {

	@Test
	public void testEncryption() throws IOException, ConfigurationException {
		PropertiesReader.INSTANCE.setSalt("HOSSOU17@ans");
		String mail = PropertiesReader.INSTANCE.decryptPropertyValue("mail.smtp.user");
		String password = PropertiesReader.INSTANCE.decryptPropertyValue("mail.smtp.password");

		System.out.println(mail);
	}
}
