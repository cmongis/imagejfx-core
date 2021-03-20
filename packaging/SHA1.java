import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.DigestInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.io.InputStream;
import java.util.List;
import java.util.Comparator;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.io.BufferedInputStream;

public class SHA1 {

    /**
 * A helper class for the Checksummer.
 * 
 * When checksumming .properties files for the Updater, we would like to ignore
 * comments, because java.util.Properties writes the current date into a comment
 * when writing pom.properties and nar.properties files. This date is a
 * non-functional change which we would like to ignore when checking whether a
 * .jar file is up-to-date.
 * 
 * This class takes an InputStream that is expected to represent a .properties
 * file and offers an InputStream which skips the lines starting with a '#'.
 * 
 * @author Johannes Schindelin
 */
    private static class SkipHashedLines extends BufferedInputStream {
	
	protected boolean atLineStart;

	public SkipHashedLines(final InputStream in) {
	    super(in, 1024);
	    atLineStart = true;
	}

	@Override
	public synchronized int read() throws IOException {
	    int ch = super.read();
	    if (atLineStart) {
		if (ch == '#')
		    while ((ch = read()) != '\n' && ch != -1)
			; // do nothing
		else
		    atLineStart = false;
	    }
	    else if (ch == '\n')
		atLineStart = true;
	    return ch;
	}

	@Override
	public int read(final byte[] b) throws IOException {
	    return read(b, 0, b.length);
	}

	@Override
	public synchronized int read(final byte[] b, final int off, final int len) throws IOException {
	    int count = 0;
	    while (count < len) {
		int ch = read();
		if (ch < 0)
		    return count == 0 ? -1 : count;
		b[off + count] = (byte)ch;
		count++;
	    }
	    return count;
	}

	@Override
	public synchronized long skip(final long n) throws IOException {
	    throw new IOException("unsupported skip");
	}
    }

    /**
     * A helper class for the Checksummer.
     * 
     * When checksumming manifests for the Updater, we would like to ignore
     * certain entries such as Implementation-Build because they do not provide
     * substantial changes and are likely to differ between builds even if the
     * real content does not.
     * 
     * This class takes an InputStream that is expected to represent a manifest
     * and offers an InputStream which skips the mentioned entries.
     * 
     * @author Johannes Schindelin
     */
    private static class FilterManifest extends ByteArrayInputStream {
	protected static enum Skip {
	    ARCHIVER_VERSION,
	    BUILT_BY,
	    BUILD_JDK,
	    CLASS_PATH,
	    CREATED_BY,
	    IMPLEMENTATION_BUILD
	}
	protected static Set<String> skip = new HashSet<>();
	static {
	    for (Skip s : Skip.values())
		skip.add(s.toString().toUpperCase().replace('_', '-'));
	}

	public FilterManifest(final InputStream in) throws IOException {
	    this(in, true);
	}

	public FilterManifest(final InputStream in, boolean keepOnlyMainClass) throws IOException {
	    super(filter(in, keepOnlyMainClass));
	}

	protected static byte[] filter(final InputStream in, boolean keepOnlyMainClass) throws IOException {
	    return filter(new BufferedReader(new InputStreamReader(in)), keepOnlyMainClass);
	}

	protected static byte[] filter(final BufferedReader reader, boolean keepOnlyMainClass) throws IOException {
	    StringBuilder builder = new StringBuilder();
	    for (;;) {
		String line = reader.readLine();
		if (line == null) {
		    break;
		}
		if (keepOnlyMainClass) {
		    if (line.toUpperCase().startsWith("Main-Class:")) builder.append(line).append('\n');
		    continue;
		}
		int colon = line.indexOf(':');
		if (colon > 0 && skip.contains(line.substring(0, colon).toUpperCase())) {
		    continue;
		}
		builder.append(line).append('\n');
	    }
	    reader.close();
	    return builder.toString().getBytes();
	}
    }



    private static class JarEntryComparator implements Comparator<JarEntry> {

	@Override
	public int compare(final JarEntry entry1, final JarEntry entry2) {
	    final String name1 = entry1.getName();
	    final String name2 = entry2.getName();
	    return name1.compareTo(name2);
	}

    }
    public static MessageDigest getDigest() throws NoSuchAlgorithmException {
	return MessageDigest.getInstance("SHA-1");
    }


    private static String getJarDigest(String file) throws FileNotFoundException, IOException{
	MessageDigest digest = null;
	try {
	    digest = getDigest();
	}
	catch (final NoSuchAlgorithmException e) {
	    throw new RuntimeException(e);
	}

	if (file != null) {
	    final JarFile jar = new JarFile(file);
	    final List<JarEntry> list = Collections.list(jar.entries());
	    Collections.sort(list, new JarEntryComparator());

	    for (final JarEntry entry : list) {
		digest.update(entry.getName().getBytes("ASCII"));
		InputStream inputStream = jar.getInputStream(entry);
		// .properties files have a date in a comment; let's ignore this for the checksum
		// For backwards-compatibility, activate the .properties mangling only from June 15th, 2012
		if (entry.getName().endsWith(".properties")) {
		    inputStream = new SkipHashedLines(inputStream);
		}
		// same for manifests, but with July 6th, 2012
		if (entry.getName().equals("META-INF/MANIFEST.MF")) {
		    inputStream = new FilterManifest(inputStream, true);
		}

		updateDigest(inputStream, digest);
	    }
	    jar.close();
	}
	return toHex(digest.digest());
    }



    public static void updateDigest(final InputStream input,
				    final MessageDigest digest) throws IOException
    {
	final byte[] buffer = new byte[65536];
	final DigestInputStream digestStream = new DigestInputStream(input, digest);
	while (digestStream.read(buffer) >= 0); /* do nothing */
	digestStream.close();
    }

    public final static char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7',
				       '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String toHex(final byte[] bytes) {
	final char[] buffer = new char[bytes.length * 2];
	for (int i = 0; i < bytes.length; i++) {
	    buffer[i * 2] = hex[(bytes[i] & 0xf0) >> 4];
	    buffer[i * 2 + 1] = hex[bytes[i] & 0xf];
	}
	return new String(buffer);
    }

    public static byte[] createSha1(File file) throws Exception {
	MessageDigest digest = MessageDigest.getInstance("SHA-1");
	InputStream fis = new FileInputStream(file);
	int n = 0;
	byte[] buffer = new byte[8192];
	while (n != -1) {
	    n = fis.read(buffer);
	    if (n > 0) {
		digest.update(buffer, 0, n);
	    }
	}
	return digest.digest();
    }

    public static void main(String args[]){
	try{
	    System.out.println(getJarDigest(args[0]));
	}
	catch(Exception e){System.out.println(e);}
    }
}
