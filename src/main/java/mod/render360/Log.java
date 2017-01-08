package mod.render360;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log
{
	public static final Logger logger = LogManager.getLogger("Render360");

	public static void info(String s)
	{
		logger.info(s);
	}
	
	public static <T> void info(T s) {
		logger.info(s.toString());
	}
}
