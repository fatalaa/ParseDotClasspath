package hu.infostyle.parsedotclasspath;

import java.io.File;

/**
 * Builds a CLASSPATH string from {@link File}s.
 *
 * @author Tibor Molnár (fatalaa@hotmail.com)
 */
public final class ClasspathBuilder {
    /**
     * Path separator.
     * The default value is platform-dependent.
     */
    private String separator = File.pathSeparator;

    private final StringBuffer buf = new StringBuffer();

    /**
     * Overrides the platform-default separator string.
     */
    public void setSeparator(String sep) {
        this.separator = sep;
    }

    public void reset() {
        buf.setLength(0);
    }

    /**
     * Adds a new entry
     */
    public void add(File f) {
        if (buf.length() != 0)
            buf.append(separator);
        buf.append(f.toString());
    }
    public void add(String f) {
        if (buf.length() != 0)
            buf.append(separator);
        buf.append(f);
    }

    /**
     * Returns the string formatted for the CLASSPATH variable.
     */
    public String getResult() {
        return buf.toString();
    }
}
