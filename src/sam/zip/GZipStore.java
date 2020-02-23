package sam.zip;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;

public class GZipStore {
    private final int password = 1235;

    public void write(CharSequence data, Path path, Charset charset) throws IOException {
    	final ByteBuffer bb = ByteBuffer.allocate(Integer.BYTES);
    	
        try(OutputStream os = Files.newOutputStream(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                GZIPOutputStream gos = new GZIPOutputStream(os);
                WritableByteChannel wbc = Channels.newChannel(gos);
                ) {

            writeInt(bb, password, wbc); // write password;

            // write charset name
            final ByteBuffer name = ByteBuffer.wrap(charset.name().getBytes(StandardCharsets.UTF_8));
            writeInt(bb, name.remaining(), wbc);
            wbc.write(name);

            CharsetEncoder encoder = charset.newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);

            final ByteBuffer buffer = encoder.encode(data instanceof CharBuffer ? (CharBuffer)data : CharBuffer.wrap(data));
            // write size;
            writeInt(bb, buffer.remaining(), wbc);

            wbc.write(buffer);
        }
    }

    private void writeInt(ByteBuffer bb, int value, WritableByteChannel wbc) throws IOException {
        bb.clear();
        bb.putInt(value);
        bb.flip();
        wbc.write(bb);
    }
    private int readInt(ByteBuffer bb, ReadableByteChannel rbc) throws IOException {
        bb.clear();
        rbc.read(bb);
        bb.flip();
        return bb.getInt();
    }
    public String readAsString(Path path) throws IOException {
        return read(path).toString();
    }
    public CharBuffer read(Path path) throws IOException {
        try(InputStream is = Files.newInputStream(path, StandardOpenOption.READ);) {
            return read(is);
        }
    }
    public CharBuffer read(InputStream is) throws IOException {
        try( GZIPInputStream gis = new GZIPInputStream(is);
                ReadableByteChannel rbc = Channels.newChannel(gis);) {

            final ByteBuffer bb = ByteBuffer.allocate(Integer.BYTES);

            // read password;
            if(password != readInt(bb, rbc))
                throw new IllegalGZipStoreException("password matching failed");

            ByteBuffer nameBuf = ByteBuffer.allocate(readInt(bb, rbc));
            rbc.read(nameBuf);
            nameBuf.flip();

            Charset charset = Charset.forName(StandardCharsets.UTF_8.decode(nameBuf).toString());
            LoggerFactory.getLogger(getClass()).debug(charset.name());
            
            ByteBuffer buffer = ByteBuffer.allocate(readInt(bb, rbc));
            rbc.read(buffer);
            buffer.flip();

            CharsetDecoder decoder = charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);

            return decoder.decode(buffer);
        }
    }
    class IllegalGZipStoreException extends IllegalStateException {

        private static final long serialVersionUID = 1L;

        public IllegalGZipStoreException(String s) {
            super(s);
        }

    }
}
