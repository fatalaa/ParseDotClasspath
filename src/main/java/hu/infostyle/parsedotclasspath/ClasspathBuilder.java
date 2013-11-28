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

    private final StringBuffer stringBuffer = new StringBuffer();

    /**
     * Overrides the platform-default separator string.
     */
    public void setSeparator(String sep) {
        this.separator = sep;
    }

    public void reset() {
        stringBuffer.setLength(0);
    }

    /**
     * Adds a new entry
     */
    public void add(File f) {
        if (stringBuffer.length() != 0)
            stringBuffer.append(separator);
        stringBuffer.append(f.toString());
    }
    public void add(String f) {
        if (stringBuffer.length() != 0)
            stringBuffer.append(separator);
        stringBuffer.append(f);
    }

    /**
     * Returns the string formatted for the CLASSPATH variable.
     */
    public String getResult() {
        return stringBuffer.toString();
    }
}
