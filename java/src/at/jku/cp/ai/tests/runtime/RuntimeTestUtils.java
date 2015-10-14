package at.jku.cp.ai.tests.runtime;

import static at.jku.cp.ai.utils.FilesystemUtils.join;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RuntimeTestUtils {
	public static Process exec(Class<?> mainClass, String... arguments)
	{
		try
		{
			String javaHome = System.getProperty("java.home");
			String javaBin = join(javaHome, "bin", "java");
			String classpath = System.getProperty("java.class.path");
			String mainClassName = mainClass.getCanonicalName();

			List<String> all = new ArrayList<>();
			all.addAll(Arrays.asList(
					javaBin,
					"-Xms64M",
					"-Xmx64M",
					"-cp", classpath,
					mainClassName));

			all.addAll(Arrays.asList(arguments));

			ProcessBuilder builder = new ProcessBuilder(all);
			return builder.start();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static String convert(InputStream in)
	{
		try
		{
			InputStreamReader is = new InputStreamReader(in);
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(is);
			String read = br.readLine();
			String nl = System.getProperty("line.separator");
			while (read != null)
			{
				sb.append(read);
				read = br.readLine();
				if (read != null)
					sb.append(nl);
			}

			return sb.toString();
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static String convert(Path filename)
	{
		try
		{
			InputStreamReader is = new InputStreamReader(new FileInputStream(filename.toFile()));
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(is);
			String read = br.readLine();
			String nl = System.getProperty("line.separator");
			while (read != null)
			{
				sb.append(read);
				read = br.readLine();
				if (read != null)
					sb.append(nl);
			}
			br.close();
			return sb.toString();
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
